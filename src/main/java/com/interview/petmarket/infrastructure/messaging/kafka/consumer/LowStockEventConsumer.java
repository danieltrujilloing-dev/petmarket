package com.interview.petmarket.infrastructure.messaging.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interview.petmarket.domain.events.LowStockEvent;
import com.interview.petmarket.domain.ports.in.GestionarTareasReposicionUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Consumidor asíncrono de eventos LowStock.
 * Crea automáticamente tareas de reposición cuando se detecta stock bajo.
 * 
 * Implementa el patrón Observer para reaccionar a eventos de dominio.
 */
@Component
public class LowStockEventConsumer {
    
    private static final Logger logger = LoggerFactory.getLogger(LowStockEventConsumer.class);
    
    private final GestionarTareasReposicionUseCase tareasReposicionService;
    private final ObjectMapper objectMapper;
    
    public LowStockEventConsumer(GestionarTareasReposicionUseCase tareasReposicionService,
                                ObjectMapper objectMapper) {
        this.tareasReposicionService = tareasReposicionService;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Consume eventos LowStock y crea tareas de reposición automáticamente.
     */
    @KafkaListener(
            topics = "petmarket.lowstockevent",
            groupId = "inventory-management-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleLowStockEvent(@Payload String eventPayload,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                   @Header(KafkaHeaders.OFFSET) long offset,
                                   Acknowledgment acknowledgment) {
        
                logger.info("Recibido evento LowStock - Topic: {}, Partition: {}, Offset: {}", 
                   topic, partition, offset);
        
        try {
            // Deserializar el evento
            LowStockEvent event = deserializeEvent(eventPayload);
            
            logger.info("Procesando evento LowStock para producto {} - Stock: {}, Umbral: {}, Prioridad: {}", 
                       event.getProductoId(), event.getStockActual(), 
                       event.getUmbralReposicion(), event.getPrioridad());
            
            // Crear tarea de reposición automáticamente
            var tareaCreada = tareasReposicionService.crearTareaReposicion(
                    event.getProductoId(),
                    event.getNombreProducto(),
                    event.getStockActual(),
                    event.getUmbralReposicion(),
                    event.getCantidadSugerida()
            );
            
            logger.info("Tarea de reposición creada exitosamente - ID: {}, Producto: {}, Prioridad: {}", 
                       tareaCreada.getId(), event.getProductoId(), tareaCreada.getPrioridad());
            
            // Confirmar procesamiento del mensaje
            acknowledgment.acknowledge();
            
            // Log adicional para eventos críticos
            if (event.esCritico()) {
                logger.warn("EVENTO CRÍTICO: Stock agotado o muy bajo para producto {} - Se requiere acción inmediata", 
                           event.getProductoId());
            }
            
        } catch (Exception e) {
                        logger.error("Error procesando evento LowStock - Topic: {}, Partition: {}, Offset: {}", 
                        topic, partition, offset, e);
            
            // En un entorno productivo, aquí podrías:
            // 1. Enviar a una cola de dead letter
            // 2. Implementar retry con backoff
            // 3. Alertar al equipo de operaciones
            
            // Por ahora, no confirmamos el mensaje para que se reintente
            // acknowledgment.acknowledge(); // Comentado para permitir retry
        }
    }
    
    /**
     * Deserializa el payload JSON a un objeto LowStockEvent.
     */
    private LowStockEvent deserializeEvent(String eventPayload) {
        try {
            return objectMapper.readValue(eventPayload, LowStockEvent.class);
        } catch (Exception e) {
            logger.error("Error deserializando evento LowStock: {}", eventPayload, e);
            throw new RuntimeException("Failed to deserialize LowStockEvent", e);
        }
    }
    
    /**
     * Maneja errores de consumo de Kafka.
     */
    @KafkaListener(
            topics = "petmarket.lowstockevent.DLT", // Dead Letter Topic
            groupId = "inventory-management-group-dlt"
    )
    public void handleDeadLetterEvent(@Payload String eventPayload,
                                     @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                     @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage) {
        
        logger.error("Evento LowStock enviado a Dead Letter Topic - Topic: {}, Error: {}, Payload: {}", 
                    topic, exceptionMessage, eventPayload);
        
        // Aquí podrías implementar:
        // 1. Notificación al equipo de operaciones
        // 2. Almacenamiento en base de datos para análisis posterior
        // 3. Métricas de errores
        
        // Por simplicidad, solo loggeamos el error
    }
}
