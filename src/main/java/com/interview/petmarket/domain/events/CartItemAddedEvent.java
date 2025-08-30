package com.interview.petmarket.domain.events;

import java.time.LocalDateTime;

/**
 * Evento de dominio que se publica cuando se agrega un item al carrito.
 * Útil para analytics y recomendaciones.
 */
public class CartItemAddedEvent extends DomainEvent {
    
    private final Long clienteId;
    private final Long productoId;
    private final int cantidad;
    private final int cantidadAnterior;
    
    public CartItemAddedEvent(Long clienteId, Long productoId, int cantidad, int cantidadAnterior) {
        super();
        this.clienteId = clienteId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.cantidadAnterior = cantidadAnterior;
    }
    
    // Getters
    public Long getClienteId() {
        return clienteId;
    }
    
    public Long getProductoId() {
        return productoId;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public int getCantidadAnterior() {
        return cantidadAnterior;
    }
    
    public boolean esNuevoItem() {
        return cantidadAnterior == 0;
    }
    
    @Override
    public String toString() {
        return "CartItemAddedEvent{" +
               "clienteId=" + clienteId +
               ", productoId=" + productoId +
               ", cantidad=" + cantidad +
               ", cantidadAnterior=" + cantidadAnterior +
               ", timestamp=" + getOccurredOn() +
               '}';
    }
}
