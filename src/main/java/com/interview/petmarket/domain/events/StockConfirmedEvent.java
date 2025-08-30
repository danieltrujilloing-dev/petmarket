package com.interview.petmarket.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Evento de dominio que se publica cuando se confirma la disminución definitiva de stock
 * al pagar un pedido.
 */
public class StockConfirmedEvent extends DomainEvent {
    
    private final Long pedidoId;
    private final Long productoId;
    private final String nombreProducto;
    private final int cantidadConfirmada;
    private final int stockAnterior;
    private final int stockActual;
    private final String motivoConfirmacion;
    
    @JsonCreator
    public StockConfirmedEvent(@JsonProperty("pedidoId") Long pedidoId, 
                              @JsonProperty("productoId") Long productoId, 
                              @JsonProperty("nombreProducto") String nombreProducto,
                              @JsonProperty("cantidadConfirmada") int cantidadConfirmada, 
                              @JsonProperty("stockAnterior") int stockAnterior, 
                              @JsonProperty("stockActual") int stockActual,
                              @JsonProperty("motivoConfirmacion") String motivoConfirmacion) {
        super();
        this.pedidoId = pedidoId;
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidadConfirmada = cantidadConfirmada;
        this.stockAnterior = stockAnterior;
        this.stockActual = stockActual;
        this.motivoConfirmacion = motivoConfirmacion;
    }
    
    public Long getPedidoId() {
        return pedidoId;
    }
    
    public Long getProductoId() {
        return productoId;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public int getCantidadConfirmada() {
        return cantidadConfirmada;
    }
    
    public int getStockAnterior() {
        return stockAnterior;
    }
    
    public int getStockActual() {
        return stockActual;
    }
    
    public String getMotivoConfirmacion() {
        return motivoConfirmacion;
    }
    
    /**
     * Calcula la diferencia de stock.
     */
    public int calcularDiferenciaStock() {
        return stockAnterior - stockActual;
    }
    
    /**
     * Verifica si el stock se agotó completamente.
     */
    public boolean stockAgotado() {
        return stockActual == 0;
    }
    
    /**
     * Calcula el porcentaje de reducción de stock.
     */
    public double calcularPorcentajeReduccion() {
        if (stockAnterior == 0) return 0.0;
        return (double) cantidadConfirmada / stockAnterior * 100.0;
    }
    
    @Override
    public String toString() {
        return "StockConfirmedEvent{" +
               "eventId='" + getEventId() + '\'' +
               ", eventType='" + getEventType() + '\'' +
               ", pedidoId=" + pedidoId +
               ", productoId=" + productoId +
               ", nombreProducto='" + nombreProducto + '\'' +
               ", cantidadConfirmada=" + cantidadConfirmada +
               ", stockAnterior=" + stockAnterior +
               ", stockActual=" + stockActual +
               ", motivoConfirmacion='" + motivoConfirmacion + '\'' +
               ", porcentajeReduccion=" + String.format("%.1f%%", calcularPorcentajeReduccion()) +
               ", timestamp=" + getOccurredOn() +
               '}';
    }
}
