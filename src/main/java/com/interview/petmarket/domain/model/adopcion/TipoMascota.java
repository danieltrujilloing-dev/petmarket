package com.interview.petmarket.domain.model.adopcion;

/**
 * Enum para los tipos de mascotas disponibles para adopción
 */
public enum TipoMascota {
    PERRO("Perro"),
    GATO("Gato"),
    AVE("Ave"),
    CONEJO("Conejo"),
    HAMSTER("Hámster"),
    OTRO("Otro");
    
    private final String descripcion;
    
    TipoMascota(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
}
