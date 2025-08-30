package com.interview.petmarket.domain.ports.out;

import com.interview.petmarket.domain.events.DomainEvent;

/**
 * Puerto de salida para publicar eventos de dominio
 */
public interface EventPublisherPort {
    
    /**
     * Publica un evento de dominio
     */
    void publishEvent(DomainEvent event);
}
