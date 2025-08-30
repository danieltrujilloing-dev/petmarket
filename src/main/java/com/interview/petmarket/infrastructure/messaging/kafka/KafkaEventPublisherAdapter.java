package com.interview.petmarket.infrastructure.messaging.kafka;

import com.interview.petmarket.domain.events.DomainEvent;
import com.interview.petmarket.domain.ports.out.EventPublisherPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Adaptador para publicar eventos usando Kafka
 */
@Component
public class KafkaEventPublisherAdapter implements EventPublisherPort {
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisherAdapter.class);
    private static final String TOPIC_PREFIX = "petmarket.";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public KafkaEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @Override
    public void publishEvent(DomainEvent event) {
        String topic = TOPIC_PREFIX + event.getEventType().toLowerCase();
        
        try {
            kafkaTemplate.send(topic, event.getEventId(), event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            logger.info("Event published successfully: {} to topic: {}", 
                                    event.getEventType(), topic);
                        } else {
                            logger.error("Failed to publish event: {} to topic: {}", 
                                    event.getEventType(), topic, ex);
                        }
                    });
        } catch (Exception e) {
            logger.error("Error publishing event: {} to topic: {}", 
                    event.getEventType(), topic, e);
        }
    }
}
