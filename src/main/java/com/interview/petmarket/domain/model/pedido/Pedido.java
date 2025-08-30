package com.interview.petmarket.domain.model.pedido;

import com.interview.petmarket.domain.model.common.BaseEntity;
import com.interview.petmarket.domain.exceptions.InvalidOrderDataException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Entidad Pedido del dominio
 * Representa un pedido (id, clienteId, ítems, total, estado)
 */
public class Pedido extends BaseEntity {
    
    private final Long clienteId;
    private final Map<Long, PedidoItem> items;
    private BigDecimal total;
    private EstadoPedido estado;
    
    // Constructor privado para usar el builder
    private Pedido(Builder builder) {
        super(builder.id);
        this.clienteId = builder.clienteId;
        this.items = new HashMap<>(builder.items);
        this.total = builder.total != null ? builder.total : calcularTotal();
        this.estado = builder.estado != null ? builder.estado : EstadoPedido.CREADO;
        
        validatePedido();
    }
    
    private void validatePedido() {
        if (clienteId == null) {
            throw new InvalidOrderDataException("Client ID cannot be null");
        }
        if (items.isEmpty()) {
            throw new InvalidOrderDataException("Order must have at least one item");
        }
        if (total == null || total.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderDataException("Total must be non-negative");
        }
    }
    
    private BigDecimal calcularTotal() {
        return items.values().stream()
                .map(PedidoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Métodos de negocio
    public void marcarComoPagado() {
        cambiarEstado(EstadoPedido.PAGADO);
    }
    
    public void marcarEnPreparacion() {
        cambiarEstado(EstadoPedido.EN_PREPARACION);
    }
    
    public void marcarComoEnviado() {
        cambiarEstado(EstadoPedido.ENVIADO);
    }
    
    public void marcarComoEntregado() {
        cambiarEstado(EstadoPedido.ENTREGADO);
    }
    
    public void cancelar() {
        cambiarEstado(EstadoPedido.CANCELADO);
    }
    
    private void cambiarEstado(EstadoPedido nuevoEstado) {
        if (!this.estado.puedeTransicionarA(nuevoEstado)) {
            throw new InvalidOrderDataException(
                String.format("Cannot transition from %s to %s", this.estado, nuevoEstado)
            );
        }
        this.estado = nuevoEstado;
        updateTimestamp();
    }
    
    public void actualizarTotal(BigDecimal nuevoTotal) {
        if (nuevoTotal == null || nuevoTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderDataException("Total must be non-negative");
        }
        this.total = nuevoTotal;
        updateTimestamp();
    }
    
    public boolean puedeSerCancelado() {
        return estado == EstadoPedido.CREADO || estado == EstadoPedido.PAGADO;
    }
    
    public boolean estaTerminado() {
        return estado.esTerminal();
    }
    
    public boolean estaPendiente() {
        return estado.esPendiente();
    }
    
    public int contarItems() {
        return items.values().stream()
                .mapToInt(PedidoItem::getCantidad)
                .sum();
    }
    
    public boolean contieneProducto(Long productoId) {
        return items.containsKey(productoId);
    }
    
    public Optional<PedidoItem> obtenerItem(Long productoId) {
        return Optional.ofNullable(items.get(productoId));
    }
    
    // Getters
    public Long getClienteId() {
        return clienteId;
    }
    
    public List<PedidoItem> getItems() {
        return new ArrayList<>(items.values());
    }
    
    public Map<Long, PedidoItem> getItemsMap() {
        return Collections.unmodifiableMap(items);
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public EstadoPedido getEstado() {
        return estado;
    }
    
    // Builder Pattern
    public static class Builder {
        private Long id;
        private Long clienteId;
        private Map<Long, PedidoItem> items = new HashMap<>();
        private BigDecimal total;
        private EstadoPedido estado;
        
        public Builder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder withClienteId(Long clienteId) {
            this.clienteId = clienteId;
            return this;
        }
        
        public Builder withItems(List<PedidoItem> items) {
            if (items != null) {
                this.items = items.stream()
                        .collect(Collectors.toMap(
                                PedidoItem::getProductoId,
                                item -> item,
                                (existing, replacement) -> replacement
                        ));
            }
            return this;
        }
        
        public Builder withItem(PedidoItem item) {
            if (item != null) {
                this.items.put(item.getProductoId(), item);
            }
            return this;
        }
        
        public Builder withTotal(BigDecimal total) {
            this.total = total;
            return this;
        }
        
        public Builder withEstado(EstadoPedido estado) {
            this.estado = estado;
            return this;
        }
        
        public Pedido build() {
            return new Pedido(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
