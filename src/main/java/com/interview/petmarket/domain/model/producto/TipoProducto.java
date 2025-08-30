package com.interview.petmarket.domain.model.producto;

/**
 * Enumeración que define los tipos de productos disponibles en el marketplace.
 */
public enum TipoProducto {
    ALIMENTO("Alimento"),
    ACCESORIO("Accesorio"),
    JUGUETE("Juguete"),
    MEDICINA("Medicina"),
    HIGIENE("Higiene"),
    CAMA("Cama"),
    COLLAR("Collar"),
    CORREA("Correa"),
    TRANSPORTADORA("Transportadora"),
    ENTRENAMIENTO("Entrenamiento");

    private final String descripcion;

    TipoProducto(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Busca un tipo de producto por su descripción.
     */
    public static TipoProducto fromDescripcion(String descripcion) {
        if (descripcion == null) {
            return null;
        }
        
        for (TipoProducto tipo : values()) {
            if (tipo.descripcion.equalsIgnoreCase(descripcion.trim())) {
                return tipo;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}