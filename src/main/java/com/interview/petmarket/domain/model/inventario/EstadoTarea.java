package com.interview.petmarket.domain.model.inventario;

/**
 * Estados posibles de una tarea de reposición.
 */
public enum EstadoTarea {
    PENDIENTE("Pendiente de procesamiento"),
    EN_PROCESO("En proceso de reposición"),
    COMPLETADA("Reposición completada"),
    CANCELADA("Tarea cancelada");
    
    private final String descripcion;
    
    EstadoTarea(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public boolean esActiva() {
        return this == PENDIENTE || this == EN_PROCESO;
    }
    
    public boolean esFinal() {
        return this == COMPLETADA || this == CANCELADA;
    }
}
