package com.interview.petmarket.domain.model.inventario;

import com.interview.petmarket.domain.model.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Entidad de dominio para Tarea de Reposición.
 * Representa una tarea creada automáticamente cuando el stock está bajo.
 * 
 * Implementa principios SOLID:
 * - SRP: Solo se encarga de la lógica de tareas de reposición
 * - OCP: Extensible para nuevos tipos de tareas
 */
@Getter
public class TareaReposicion extends BaseEntity {
    
    private final Long productoId;
    private final String nombreProducto;
    private final int stockActual;
    private final int umbralReposicion;
    private final int cantidadSugerida;
    private final EstadoTarea estado;
    private final PrioridadTarea prioridad;
    private final String observaciones;
    private final LocalDateTime fechaVencimiento;
    
    @Builder(setterPrefix = "with")
    public TareaReposicion(Long id, Long productoId, String nombreProducto, 
                          int stockActual, int umbralReposicion, int cantidadSugerida,
                          EstadoTarea estado, PrioridadTarea prioridad, 
                          String observaciones, LocalDateTime fechaVencimiento) {
        super(id);
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.stockActual = stockActual;
        this.umbralReposicion = umbralReposicion;
        this.cantidadSugerida = cantidadSugerida;
        this.estado = estado != null ? estado : EstadoTarea.PENDIENTE;
        this.prioridad = calcularPrioridad(stockActual, umbralReposicion, prioridad);
        this.observaciones = observaciones;
        this.fechaVencimiento = fechaVencimiento != null ? fechaVencimiento : calcularFechaVencimiento();
        
        validarDatos();
    }
    
    /**
     * Marca la tarea como en proceso.
     */
    public TareaReposicion marcarEnProceso() {
        if (estado == EstadoTarea.COMPLETADA) {
            throw new IllegalStateException("Cannot change state of completed task");
        }
        
        return TareaReposicion.builder()
                .withId(this.getId())
                .withProductoId(this.productoId)
                .withNombreProducto(this.nombreProducto)
                .withStockActual(this.stockActual)
                .withUmbralReposicion(this.umbralReposicion)
                .withCantidadSugerida(this.cantidadSugerida)
                .withEstado(EstadoTarea.EN_PROCESO)
                .withPrioridad(this.prioridad)
                .withObservaciones(this.observaciones)
                .withFechaVencimiento(this.fechaVencimiento)
                .build();
    }
    
    /**
     * Marca la tarea como completada.
     */
    public TareaReposicion marcarCompletada(String observacionesFinales) {
        return TareaReposicion.builder()
                .withId(this.getId())
                .withProductoId(this.productoId)
                .withNombreProducto(this.nombreProducto)
                .withStockActual(this.stockActual)
                .withUmbralReposicion(this.umbralReposicion)
                .withCantidadSugerida(this.cantidadSugerida)
                .withEstado(EstadoTarea.COMPLETADA)
                .withPrioridad(this.prioridad)
                .withObservaciones(observacionesFinales != null ? observacionesFinales : this.observaciones)
                .withFechaVencimiento(this.fechaVencimiento)
                .build();
    }
    
    /**
     * Verifica si la tarea está vencida.
     */
    public boolean estaVencida() {
        return LocalDateTime.now().isAfter(fechaVencimiento) && estado != EstadoTarea.COMPLETADA;
    }
    
    /**
     * Verifica si la tarea es crítica (stock muy bajo).
     */
    public boolean esCritica() {
        return prioridad == PrioridadTarea.CRITICA;
    }
    
    /**
     * Calcula el porcentaje de stock restante respecto al umbral.
     */
    public double calcularPorcentajeStock() {
        if (umbralReposicion == 0) return 100.0;
        return (double) stockActual / umbralReposicion * 100.0;
    }
    
    private PrioridadTarea calcularPrioridad(int stockActual, int umbralReposicion, PrioridadTarea prioridadManual) {
        if (prioridadManual != null) {
            return prioridadManual;
        }
        
        if (stockActual == 0) {
            return PrioridadTarea.CRITICA;
        }
        
        double porcentaje = calcularPorcentajeStock();
        if (porcentaje <= 25) {
            return PrioridadTarea.CRITICA;
        } else if (porcentaje <= 50) {
            return PrioridadTarea.ALTA;
        } else if (porcentaje <= 75) {
            return PrioridadTarea.MEDIA;
        } else {
            return PrioridadTarea.BAJA;
        }
    }
    
    private LocalDateTime calcularFechaVencimiento() {
        // Fecha de vencimiento basada en prioridad
        return switch (prioridad) {
            case CRITICA -> LocalDateTime.now().plusHours(4);  // 4 horas
            case ALTA -> LocalDateTime.now().plusHours(12);    // 12 horas
            case MEDIA -> LocalDateTime.now().plusDays(1);     // 1 día
            case BAJA -> LocalDateTime.now().plusDays(3);      // 3 días
        };
    }
    
    private void validarDatos() {
        if (productoId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (stockActual < 0) {
            throw new IllegalArgumentException("Current stock cannot be negative");
        }
        if (umbralReposicion < 0) {
            throw new IllegalArgumentException("Replenishment threshold cannot be negative");
        }
        if (cantidadSugerida <= 0) {
            throw new IllegalArgumentException("Suggested quantity must be positive");
        }
    }
    
    @Override
    public String toString() {
        return "TareaReposicion{" +
               "id=" + getId() +
               ", productoId=" + productoId +
               ", nombreProducto='" + nombreProducto + '\'' +
               ", stockActual=" + stockActual +
               ", umbralReposicion=" + umbralReposicion +
               ", cantidadSugerida=" + cantidadSugerida +
               ", estado=" + estado +
               ", prioridad=" + prioridad +
               ", fechaVencimiento=" + fechaVencimiento +
               ", estaVencida=" + estaVencida() +
               '}';
    }
}
