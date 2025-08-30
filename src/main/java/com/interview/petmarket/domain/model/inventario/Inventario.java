package com.interview.petmarket.domain.model.inventario;

import com.interview.petmarket.domain.model.common.BaseEntity;
import com.interview.petmarket.domain.exceptions.InvalidInventoryDataException;

/**
 * Entidad Inventario del dominio
 * Representa el inventario de un producto (productoId, stockDisponible, umbralReposición)
 */
public class Inventario extends BaseEntity {
    
    private final Long productoId;
    private int stockDisponible;
    private int umbralReposicion;
    
    // Constructor privado para usar el builder
    private Inventario(Builder builder) {
        super(builder.id);
        this.productoId = builder.productoId;
        this.stockDisponible = builder.stockDisponible;
        this.umbralReposicion = builder.umbralReposicion;
        
        validateInventario();
    }
    
    private void validateInventario() {
        if (productoId == null) {
            throw new InvalidInventoryDataException("Product ID cannot be null");
        }
        if (stockDisponible < 0) {
            throw new InvalidInventoryDataException("Stock cannot be negative");
        }
        if (umbralReposicion <= 0) {
            throw new InvalidInventoryDataException("Restock threshold must be positive");
        }
    }
    
    // Métodos de negocio
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new InvalidInventoryDataException("Quantity to add must be positive");
        }
        this.stockDisponible += cantidad;
        updateTimestamp();
    }
    
    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new InvalidInventoryDataException("Quantity to reduce must be positive");
        }
        if (cantidad > this.stockDisponible) {
            throw new InvalidInventoryDataException("Insufficient stock available");
        }
        this.stockDisponible -= cantidad;
        updateTimestamp();
    }
    
    public void actualizarUmbralReposicion(int nuevoUmbral) {
        if (nuevoUmbral <= 0) {
            throw new InvalidInventoryDataException("Restock threshold must be positive");
        }
        this.umbralReposicion = nuevoUmbral;
        updateTimestamp();
    }
    
    public boolean necesitaReposicion() {
        return this.stockDisponible <= this.umbralReposicion;
    }
    
    public boolean tieneStockDisponible() {
        return this.stockDisponible > 0;
    }
    
    public boolean tieneStockSuficiente(int cantidadRequerida) {
        return this.stockDisponible >= cantidadRequerida;
    }
    
    public int cantidadSugeridaReposicion() {
        if (!necesitaReposicion()) {
            return 0;
        }
        // Sugerir reponer hasta el doble del umbral
        return (umbralReposicion * 2) - stockDisponible;
    }
    
    // Getters
    public Long getProductoId() {
        return productoId;
    }
    
    public int getStockDisponible() {
        return stockDisponible;
    }
    
    public int getUmbralReposicion() {
        return umbralReposicion;
    }
    
    // Builder Pattern
    public static class Builder {
        private Long id;
        private Long productoId;
        private int stockDisponible;
        private int umbralReposicion = 10; // Valor por defecto
        
        public Builder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder withProductoId(Long productoId) {
            this.productoId = productoId;
            return this;
        }
        
        public Builder withStockDisponible(int stockDisponible) {
            this.stockDisponible = stockDisponible;
            return this;
        }
        
        public Builder withUmbralReposicion(int umbralReposicion) {
            this.umbralReposicion = umbralReposicion;
            return this;
        }
        
        public Inventario build() {
            return new Inventario(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
