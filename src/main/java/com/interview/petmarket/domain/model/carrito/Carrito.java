package com.interview.petmarket.domain.model.carrito;

import com.interview.petmarket.domain.model.common.BaseEntity;
import com.interview.petmarket.domain.exceptions.InvalidCartDataException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Entidad Carrito del dominio
 * Representa el carrito de compras de un cliente
 */
public class Carrito extends BaseEntity {
    
    private final Long clienteId;
    private final Map<Long, CarritoItem> items;
    
    // Constructor privado para usar el builder
    private Carrito(Builder builder) {
        super(builder.id);
        this.clienteId = builder.clienteId;
        this.items = new HashMap<>(builder.items);
        
        validateCarrito();
    }
    
    private void validateCarrito() {
        if (clienteId == null) {
            throw new InvalidCartDataException("Client ID cannot be null");
        }
    }
    
    // Métodos de negocio
    public void agregarItem(Long productoId, int cantidad) {
        if (items.containsKey(productoId)) {
            // Si el producto ya existe, actualizar cantidad
            CarritoItem itemExistente = items.get(productoId);
            int nuevaCantidad = itemExistente.getCantidad() + cantidad;
            items.put(productoId, itemExistente.actualizarCantidad(nuevaCantidad));
        } else {
            // Nuevo producto
            items.put(productoId, new CarritoItem(productoId, cantidad));
        }
        updateTimestamp();
    }
    
    public void actualizarCantidadItem(Long productoId, int nuevaCantidad) {
        if (!items.containsKey(productoId)) {
            throw new InvalidCartDataException("Product not found in cart");
        }
        
        if (nuevaCantidad <= 0) {
            eliminarItem(productoId);
        } else {
            CarritoItem item = items.get(productoId);
            items.put(productoId, item.actualizarCantidad(nuevaCantidad));
        }
        updateTimestamp();
    }
    
    public void eliminarItem(Long productoId) {
        if (!items.containsKey(productoId)) {
            throw new InvalidCartDataException("Product not found in cart");
        }
        items.remove(productoId);
        updateTimestamp();
    }
    
    public void limpiarCarrito() {
        items.clear();
        updateTimestamp();
    }
    
    public int contarItems() {
        return items.values().stream()
                .mapToInt(CarritoItem::getCantidad)
                .sum();
    }
    
    public boolean estaVacio() {
        return items.isEmpty();
    }
    
    public boolean contieneProducto(Long productoId) {
        return items.containsKey(productoId);
    }
    
    public Optional<CarritoItem> obtenerItem(Long productoId) {
        return Optional.ofNullable(items.get(productoId));
    }
    
    // Getters
    public Long getClienteId() {
        return clienteId;
    }
    
    public List<CarritoItem> getItems() {
        return new ArrayList<>(items.values());
    }
    
    public Map<Long, CarritoItem> getItemsMap() {
        return Collections.unmodifiableMap(items);
    }
    
    // Builder Pattern
    public static class Builder {
        private Long id;
        private Long clienteId;
        private Map<Long, CarritoItem> items = new HashMap<>();
        
        public Builder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder withClienteId(Long clienteId) {
            this.clienteId = clienteId;
            return this;
        }
        
        public Builder withItems(List<CarritoItem> items) {
            if (items != null) {
                this.items = items.stream()
                        .collect(Collectors.toMap(
                                CarritoItem::getProductoId,
                                item -> item,
                                (existing, replacement) -> replacement
                        ));
            }
            return this;
        }
        
        public Builder withItem(CarritoItem item) {
            if (item != null) {
                this.items.put(item.getProductoId(), item);
            }
            return this;
        }
        
        public Carrito build() {
            return new Carrito(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
