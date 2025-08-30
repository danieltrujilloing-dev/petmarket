package com.interview.petmarket.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.interview.petmarket.domain.model.inventario.PrioridadTarea;

/**
 * Evento de dominio que se publica cuando el stock de un producto cae por debajo del umbral.
 * Útil para automatizar procesos de reposición.
 */
public class LowStockEvent extends DomainEvent {
    
    private final Long productoId;
    private final String nombreProducto;
    private final int stockActual;
    private final int umbralReposicion;
    private final int cantidadSugerida;
    private final PrioridadTarea prioridad;
    private final String razonActivacion;
    
    @JsonCreator
    public LowStockEvent(@JsonProperty("productoId") Long productoId, 
                        @JsonProperty("nombreProducto") String nombreProducto, 
                        @JsonProperty("stockActual") int stockActual, 
                        @JsonProperty("umbralReposicion") int umbralReposicion, 
                        @JsonProperty("cantidadSugerida") int cantidadSugerida, 
                        @JsonProperty("prioridad") PrioridadTarea prioridad,
                        @JsonProperty("razonActivacion") String razonActivacion) {
        super();
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.stockActual = stockActual;
        this.umbralReposicion = umbralReposicion;
        this.cantidadSugerida = cantidadSugerida;
        this.prioridad = prioridad;
        this.razonActivacion = razonActivacion;
    }
    
    public Long getProductoId() {
        return productoId;
    }
    
    public String getNombreProducto() {
        return nombreProducto;
    }
    
    public int getStockActual() {
        return stockActual;
    }
    
    public int getUmbralReposicion() {
        return umbralReposicion;
    }
    
    public int getCantidadSugerida() {
        return cantidadSugerida;
    }
    
    public PrioridadTarea getPrioridad() {
        return prioridad;
    }
    
    public String getRazonActivacion() {
        return razonActivacion;
    }
    
    /**
     * Verifica si el stock está completamente agotado.
     */
    public boolean stockAgotado() {
        return stockActual == 0;
    }
    
    /**
     * Calcula el porcentaje de stock restante respecto al umbral.
     */
    public double calcularPorcentajeStock() {
        if (umbralReposicion == 0) return 100.0;
        return (double) stockActual / umbralReposicion * 100.0;
    }
    
    /**
     * Verifica si es un evento crítico.
     */
    public boolean esCritico() {
        return prioridad == PrioridadTarea.CRITICA;
    }
    
    @Override
    public String toString() {
        return "LowStockEvent{" +
               "eventId='" + getEventId() + '\'' +
               ", eventType='" + getEventType() + '\'' +
               ", productoId=" + productoId +
               ", nombreProducto='" + nombreProducto + '\'' +
               ", stockActual=" + stockActual +
               ", umbralReposicion=" + umbralReposicion +
               ", cantidadSugerida=" + cantidadSugerida +
               ", prioridad=" + prioridad +
               ", razonActivacion='" + razonActivacion + '\'' +
               ", porcentajeStock=" + String.format("%.1f%%", calcularPorcentajeStock()) +
               ", timestamp=" + getOccurredOn() +
               '}';
    }
}
