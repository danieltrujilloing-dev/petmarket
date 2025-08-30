package com.interview.petmarket.domain.model.inventario;

/**
 * Prioridades de las tareas de reposición.
 */
public enum PrioridadTarea {
    CRITICA(1, "Crítica - Stock agotado o muy bajo"),
    ALTA(2, "Alta - Stock por debajo del 50% del umbral"),
    MEDIA(3, "Media - Stock por debajo del 75% del umbral"),
    BAJA(4, "Baja - Stock cerca del umbral");
    
    private final int nivel;
    private final String descripcion;
    
    PrioridadTarea(int nivel, String descripcion) {
        this.nivel = nivel;
        this.descripcion = descripcion;
    }
    
    public int getNivel() {
        return nivel;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public boolean esMayorQue(PrioridadTarea otra) {
        return this.nivel < otra.nivel; // Menor número = mayor prioridad
    }
    
    public boolean esCritica() {
        return this == CRITICA;
    }
}
