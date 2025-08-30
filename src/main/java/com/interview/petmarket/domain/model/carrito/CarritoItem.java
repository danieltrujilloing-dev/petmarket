package com.interview.petmarket.domain.model.carrito;

import com.interview.petmarket.domain.model.common.ValueObject;
import com.interview.petmarket.domain.exceptions.InvalidCartDataException;

import java.util.Objects;

/**
 * Value Object que representa un item del carrito (productoId, cantidad)
 */
public class CarritoItem implements ValueObject {
    
    private final Long productoId;
    private final int cantidad;
    
    public CarritoItem(Long productoId, int cantidad) {
        if (productoId == null) {
            throw new InvalidCartDataException("Product ID cannot be null");
        }
        if (cantidad <= 0) {
            throw new InvalidCartDataException("Quantity must be positive");
        }
        
        this.productoId = productoId;
        this.cantidad = cantidad;
    }
    
    public CarritoItem actualizarCantidad(int nuevaCantidad) {
        return new CarritoItem(this.productoId, nuevaCantidad);
    }
    
    // Getters
    public Long getProductoId() {
        return productoId;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        CarritoItem that = (CarritoItem) obj;
        return cantidad == that.cantidad &&
               Objects.equals(productoId, that.productoId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productoId, cantidad);
    }
    
    @Override
    public String toString() {
        return "CarritoItem{" +
               "productoId=" + productoId +
               ", cantidad=" + cantidad +
               '}';
    }
}
