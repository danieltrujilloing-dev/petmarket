package com.interview.petmarket.domain.events;

import com.interview.petmarket.domain.model.pedido.EstadoPedido;
import com.interview.petmarket.domain.model.pedido.PedidoItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Evento de dominio que se publica cuando se crea un pedido.
 * Implementa el principio de Responsabilidad Única (SRP) de SOLID.
 */
public class OrderCreatedEvent extends DomainEvent {
    
    private final Long pedidoId;
    private final Long clienteId;
    private final List<PedidoItem> items;
    private final BigDecimal total;
    private final EstadoPedido estado;
    private final String estrategiaPrecio;
    
    public OrderCreatedEvent(Long pedidoId, Long clienteId, List<PedidoItem> items, 
                           BigDecimal total, EstadoPedido estado, String estrategiaPrecio) {
        super();
        this.pedidoId = pedidoId;
        this.clienteId = clienteId;
        this.items = List.copyOf(items); // Inmutable
        this.total = total;
        this.estado = estado;
        this.estrategiaPrecio = estrategiaPrecio;
    }
    
    // Getters
    public Long getPedidoId() {
        return pedidoId;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public List<PedidoItem> getItems() {
        return items;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public EstadoPedido getEstado() {
        return estado;
    }
    
    public String getEstrategiaPrecio() {
        return estrategiaPrecio;
    }
    
    public int getCantidadItems() {
        return items.stream()
                .mapToInt(PedidoItem::getCantidad)
                .sum();
    }
    
    @Override
    public String toString() {
        return "OrderCreatedEvent{" +
               "pedidoId=" + pedidoId +
               ", clienteId=" + clienteId +
               ", itemsCount=" + items.size() +
               ", total=" + total +
               ", estado=" + estado +
               ", estrategiaPrecio='" + estrategiaPrecio + '\'' +
               ", timestamp=" + getOccurredOn() +
               '}';
    }
}
