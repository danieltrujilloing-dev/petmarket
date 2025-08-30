package com.interview.petmarket.domain.model.producto;

/**
 * Enum para los tipos de productos
 */
public enum TipoProducto {
    ALIMENTO("Alimento para mascotas"),
    ACCESORIO("Accesorio para mascotas");
    
    private final String descripcion;
    
    TipoProducto(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public boolean esAlimento() {
        return this == ALIMENTO;
    }
    
    public boolean esAccesorio() {
        return this == ACCESORIO;
    }
}
