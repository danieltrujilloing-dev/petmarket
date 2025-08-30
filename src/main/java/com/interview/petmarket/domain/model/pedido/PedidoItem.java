package com.interview.petmarket.domain.model.pedido;

import com.interview.petmarket.domain.model.common.ValueObject;
import com.interview.petmarket.domain.exceptions.InvalidOrderDataException;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object que representa un item del pedido
 */
public class PedidoItem implements ValueObject {
    
    private final Long productoId;
    private final int cantidad;
    private final BigDecimal precioUnitario;
    private final BigDecimal subtotal;
    
    public PedidoItem(Long productoId, int cantidad, BigDecimal precioUnitario) {
        if (productoId == null) {
            throw new InvalidOrderDataException("Product ID cannot be null");
        }
        if (cantidad <= 0) {
            throw new InvalidOrderDataException("Quantity must be positive");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderDataException("Unit price must be non-negative");
        }
        
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
    
    // Constructor con subtotal específico (para casos donde se aplican descuentos)
    public PedidoItem(Long productoId, int cantidad, BigDecimal precioUnitario, BigDecimal subtotal) {
        if (productoId == null) {
            throw new InvalidOrderDataException("Product ID cannot be null");
        }
        if (cantidad <= 0) {
            throw new InvalidOrderDataException("Quantity must be positive");
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderDataException("Unit price must be non-negative");
        }
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidOrderDataException("Subtotal must be non-negative");
        }
        
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }
    
    public boolean tieneDescuento() {
        BigDecimal subtotalCalculado = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return subtotal.compareTo(subtotalCalculado) < 0;
    }
    
    public BigDecimal calcularDescuento() {
        BigDecimal subtotalCalculado = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        return subtotalCalculado.subtract(subtotal);
    }
    
    // Getters
    public Long getProductoId() {
        return productoId;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        PedidoItem that = (PedidoItem) obj;
        return cantidad == that.cantidad &&
               Objects.equals(productoId, that.productoId) &&
               Objects.equals(precioUnitario, that.precioUnitario) &&
               Objects.equals(subtotal, that.subtotal);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productoId, cantidad, precioUnitario, subtotal);
    }
    
    @Override
    public String toString() {
        return "PedidoItem{" +
               "productoId=" + productoId +
               ", cantidad=" + cantidad +
               ", precioUnitario=" + precioUnitario +
               ", subtotal=" + subtotal +
               '}';
    }
}
