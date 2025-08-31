package com.interview.petmarket.infrastructure.messaging.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.petmarket.domain.events.AdoptionRequestedEvent;
import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;
import com.interview.petmarket.domain.ports.in.GestionarAdopcionUseCase;
import com.interview.petmarket.domain.ports.out.RefugioServicePort;
import com.interview.petmarket.domain.ports.out.SolicitudAdopcionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Consumidor asíncrono de eventos AdoptionRequested.
 * Inicia el proceso de verificación con el refugio cuando se crea una solicitud.
 * 
 * Implementa el patrón Observer para reaccionar a eventos de dominio.
 */
@Component
public class AdoptionRequestedEventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(AdoptionRequestedEventConsumer.class);
    
    private final SolicitudAdopcionRepositoryPort solicitudRepository;
    private final RefugioServicePort refugioService;
    private final GestionarAdopcionUseCase adopcionService;
    private final ObjectMapper objectMapper;
    
    public AdoptionRequestedEventConsumer(SolicitudAdopcionRepositoryPort solicitudRepository,
                                        RefugioServicePort refugioService,
                                        GestionarAdopcionUseCase adopcionService,
                                        ObjectMapper objectMapper) {
        this.solicitudRepository = solicitudRepository;
        this.refugioService = refugioService;
        this.adopcionService = adopcionService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Consume eventos AdoptionRequested y inicia el proceso de verificación asíncrono.
     */
    @KafkaListener(
            topics = "petmarket.adoptionrequestedevent",
            groupId = "adoption-verification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAdoptionRequestedEvent(@Payload String eventPayload,
                                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                           @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                           @Header(KafkaHeaders.OFFSET) long offset,
                                           Acknowledgment acknowledgment) {
        
        logger.info("Received AdoptionRequestedEvent from topic: {}, partition: {}, offset: {}", 
                   topic, partition, offset);
        
        try {
            // Deserializar el evento
            AdoptionRequestedEvent event = objectMapper.readValue(eventPayload, AdoptionRequestedEvent.class);
            
            logger.info("Processing adoption request for solicitud ID: {} from client: {}", 
                       event.getSolicitudId(), event.getClienteId());
            
            // Procesar de forma asíncrona para no bloquear el consumidor
            CompletableFuture.runAsync(() -> procesarSolicitudAdopcion(event))
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Error processing adoption request for solicitud ID: {}", 
                                       event.getSolicitudId(), throwable);
                        } else {
                            logger.info("Successfully processed adoption request for solicitud ID: {}", 
                                       event.getSolicitudId());
                        }
                        
                        // Confirmar el mensaje después del procesamiento
                        acknowledgment.acknowledge();
                    });
            
        } catch (Exception e) {
            logger.error("Error deserializing AdoptionRequestedEvent from topic: {}", topic, e);
            // En caso de error de deserialización, confirmar el mensaje para evitar reintento infinito
            acknowledgment.acknowledge();
        }
    }
    
    private void procesarSolicitudAdopcion(AdoptionRequestedEvent event) {
        try {
            logger.info("Iniciando procesamiento de solicitud de adopción: {}", event.getSolicitudId());
            
            // Obtener la solicitud de la base de datos
            SolicitudAdopcion solicitud = solicitudRepository.findById(event.getSolicitudId())
                    .orElseThrow(() -> new RuntimeException("Solicitud no encontrada: " + event.getSolicitudId()));
            
            // Enviar al refugio para verificación
            String refugioAsignado = refugioService.enviarSolicitudParaVerificacion(solicitud);
            
            // Actualizar la solicitud con el refugio asignado e iniciar verificación
            solicitud.iniciarVerificacion(refugioAsignado);
            solicitudRepository.save(solicitud);
            
            logger.info("Solicitud {} enviada al refugio {} para verificación", 
                       event.getSolicitudId(), refugioAsignado);
            
            // Simular proceso asíncrono de verificación del refugio
            procesarVerificacionAsincrona(event.getSolicitudId());
            
        } catch (Exception e) {
            logger.error("Error processing adoption request for solicitud ID: {}", 
                       event.getSolicitudId(), e);
            throw new RuntimeException("Failed to process adoption request", e);
        }
    }
    
    /**
     * Procesa la verificación de forma asíncrona simulando el tiempo real de un refugio
     */
    private void procesarVerificacionAsincrona(Long solicitudId) {
        CompletableFuture.runAsync(() -> {
            try {
                // Simular tiempo de verificación del refugio (5-15 segundos)
                int tiempoEspera = ThreadLocalRandom.current().nextInt(5000, 15000);
                Thread.sleep(tiempoEspera);
                
                logger.info("Completando verificación para solicitud: {} después de {} ms", 
                           solicitudId, tiempoEspera);
                
                // Obtener resultado de la verificación (con random)
                RefugioServicePort.VerificationResult resultado = 
                        refugioService.verificarEstadoSolicitud(solicitudId);
                
                // Procesar la respuesta del refugio
                adopcionService.procesarRespuestaRefugio(
                        solicitudId,
                        resultado.isAprobada(),
                        resultado.getObservaciones()
                );
                
                logger.info("Verificación completada para solicitud: {} - Resultado: {}", 
                           solicitudId, resultado.isAprobada() ? "APROBADA" : "RECHAZADA");
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Verificación interrumpida para solicitud: {}", solicitudId, e);
            } catch (Exception e) {
                logger.error("Error en el proceso de verificación para solicitud: {}", 
                           solicitudId, e);
            }
        });
    }
}
