package com.interview.petmarket.infrastructure.messaging.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.petmarket.domain.events.AdoptionApprovedEvent;
import com.interview.petmarket.domain.events.AdoptionRejectedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumidor asíncrono de eventos de cambio de estado de adopción.
 * Simula notificaciones al cliente cuando cambia el estado de su solicitud.
 * 
 * En un sistema real, esto podría enviar emails, SMS, push notifications, etc.
 */
@Component
public class AdoptionStatusEventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(AdoptionStatusEventConsumer.class);
    
    private final ObjectMapper objectMapper;
    
    public AdoptionStatusEventConsumer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Consume eventos AdoptionApproved y simula notificación al cliente.
     */
    @KafkaListener(
            topics = "petmarket.adoptionapprovedevent",
            groupId = "adoption-notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAdoptionApprovedEvent(@Payload String eventPayload,
                                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                          @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                          @Header(KafkaHeaders.OFFSET) long offset,
                                          Acknowledgment acknowledgment) {
        
        logger.info("Received AdoptionApprovedEvent from topic: {}, partition: {}, offset: {}", 
                   topic, partition, offset);
        
        try {
            // Deserializar el evento
            AdoptionApprovedEvent event = objectMapper.readValue(eventPayload, AdoptionApprovedEvent.class);
            
            // Simular notificación al cliente
            enviarNotificacionAprobacion(event);
            
            // Simular webhook a sistema externo
            simularWebhookAprobacion(event);
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing AdoptionApprovedEvent from topic: {}", topic, e);
            acknowledgment.acknowledge();
        }
    }
    
    /**
     * Consume eventos AdoptionRejected y simula notificación al cliente.
     */
    @KafkaListener(
            topics = "petmarket.adoptionrejectedevent",
            groupId = "adoption-notification-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleAdoptionRejectedEvent(@Payload String eventPayload,
                                          @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                          @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                          @Header(KafkaHeaders.OFFSET) long offset,
                                          Acknowledgment acknowledgment) {
        
        logger.info("Received AdoptionRejectedEvent from topic: {}, partition: {}, offset: {}", 
                   topic, partition, offset);
        
        try {
            // Deserializar el evento
            AdoptionRejectedEvent event = objectMapper.readValue(eventPayload, AdoptionRejectedEvent.class);
            
            // Simular notificación al cliente
            enviarNotificacionRechazo(event);
            
            // Simular webhook a sistema externo
            simularWebhookRechazo(event);
            
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing AdoptionRejectedEvent from topic: {}", topic, e);
            acknowledgment.acknowledge();
        }
    }
    
    private void enviarNotificacionAprobacion(AdoptionApprovedEvent event) {
        // Simular envío de email/SMS/push notification
        logger.info("📧 NOTIFICACIÓN ENVIADA - Adopción APROBADA");
        logger.info("   Para: {} ({})", event.getNombreSolicitante(), event.getEmailSolicitante());
        logger.info("   Solicitud ID: {}", event.getSolicitudId());
        logger.info("   Refugio: {}", event.getRefugioAsignado());
        logger.info("   Observaciones: {}", event.getObservacionesRefugio());
        logger.info("   Mensaje: ¡Felicidades! Tu solicitud de adopción ha sido aprobada. " +
                   "El refugio se pondrá en contacto contigo pronto.");
    }
    
    private void enviarNotificacionRechazo(AdoptionRejectedEvent event) {
        // Simular envío de email/SMS/push notification
        logger.info("📧 NOTIFICACIÓN ENVIADA - Adopción RECHAZADA");
        logger.info("   Para: {} ({})", event.getNombreSolicitante(), event.getEmailSolicitante());
        logger.info("   Solicitud ID: {}", event.getSolicitudId());
        logger.info("   Refugio: {}", event.getRefugioAsignado());
        logger.info("   Motivo: {}", event.getMotivoRechazo());
        logger.info("   Mensaje: Lamentamos informarte que tu solicitud de adopción no ha sido aprobada. " +
                   "Puedes intentar nuevamente en el futuro.");
    }
    
    private void simularWebhookAprobacion(AdoptionApprovedEvent event) {
        // Simular llamada a webhook externo
        logger.info("🔗 WEBHOOK SIMULADO - Adopción Aprobada");
        logger.info("   URL: https://external-system.com/webhook/adoption-approved");
        logger.info("   Payload: {\"solicitudId\": {}, \"clienteId\": {}, \"estado\": \"APROBADA\"}", 
                   event.getSolicitudId(), event.getClienteId());
    }
    
    private void simularWebhookRechazo(AdoptionRejectedEvent event) {
        // Simular llamada a webhook externo
        logger.info("🔗 WEBHOOK SIMULADO - Adopción Rechazada");
        logger.info("   URL: https://external-system.com/webhook/adoption-rejected");
        logger.info("   Payload: {\"solicitudId\": {}, \"clienteId\": {}, \"estado\": \"RECHAZADA\"}", 
                   event.getSolicitudId(), event.getClienteId());
    }
}
