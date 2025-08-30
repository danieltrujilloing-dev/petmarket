package com.interview.petmarket.domain.model.producto;

/**
 * Enumeración que define las especies de animales para las cuales están destinados los productos.
 */
public enum EspecieAnimal {
    PERRO("Perro"),
    GATO("Gato"),
    AVE("Ave"),
    PEZ("Pez"),
    REPTIL("Reptil"),
    ROEDOR("Roedor"),
    CONEJO("Conejo"),
    HAMSTER("Hámster"),
    UNIVERSAL("Universal"); // Para productos que sirven para múltiples especies

    private final String descripcion;

    EspecieAnimal(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Busca una especie por su descripción.
     */
    public static EspecieAnimal fromDescripcion(String descripcion) {
        if (descripcion == null) {
            return null;
        }
        
        for (EspecieAnimal especie : values()) {
            if (especie.descripcion.equalsIgnoreCase(descripcion.trim())) {
                return especie;
            }
        }
        return null;
    }

    /**
     * Verifica si la especie es compatible con otra especie.
     * UNIVERSAL es compatible con todas las especies.
     */
    public boolean esCompatibleCon(EspecieAnimal otraEspecie) {
        if (otraEspecie == null) {
            return false;
        }
        return this == UNIVERSAL || otraEspecie == UNIVERSAL || this == otraEspecie;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
