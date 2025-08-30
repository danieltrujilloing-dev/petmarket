package com.interview.petmarket.domain.model.producto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.interview.petmarket.domain.exceptions.InvalidProductDataException;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad de dominio que representa un producto en el marketplace PetMarket.
 * 
 * Contiene la lógica de negocio para:
 * - Validación de datos del producto
 * - Gestión de disponibilidad
 * - Cálculos de precios
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Producto {
    
    @Setter(AccessLevel.PACKAGE)
    private Long id;
    
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private TipoProducto tipo;
    
    @Column(name = "especie_destino")
    private EspecieAnimal especie;
    private Integer stock;
    private boolean activo;
    private String imagenUrl;
    
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "created_at")
    private LocalDateTime fechaCreacion;
    
    @Setter(AccessLevel.PACKAGE)
    @Column(name = "updated_at")
    private LocalDateTime fechaActualizacion;

    /**
     * Verifica si el producto está disponible para la venta.
     */
    public boolean estaDisponible() {
        return activo && stock != null && stock > 0;
    }

    /**
     * Reduce el stock del producto.
     */
    public void reducirStock(int cantidad) {
        if (cantidad <= 0) {
            throw new InvalidProductDataException("Quantity to reduce must be positive");
        }
        if (stock == null || stock < cantidad) {
            throw new InvalidProductDataException("Insufficient stock");
        }
        this.stock -= cantidad;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Aumenta el stock del producto.
     */
    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new InvalidProductDataException("Quantity to add must be positive");
        }
        if (this.stock == null) {
            this.stock = cantidad;
        } else {
            this.stock += cantidad;
        }
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Aplica un descuento al precio del producto.
     */
    public BigDecimal calcularPrecioConDescuento(BigDecimal porcentajeDescuento) {
        if (porcentajeDescuento == null || porcentajeDescuento.compareTo(BigDecimal.ZERO) < 0 
            || porcentajeDescuento.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidProductDataException("Discount percentage must be between 0 and 100");
        }
        
        BigDecimal descuento = precio.multiply(porcentajeDescuento).divide(BigDecimal.valueOf(100));
        return precio.subtract(descuento);
    }

    /**
     * Actualiza el producto con nueva información.
     */
    public void actualizar(String nuevoNombre, String nuevaDescripcion, BigDecimal nuevoPrecio) {
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            this.nombre = nuevoNombre.trim();
        }
        if (nuevaDescripcion != null) {
            this.descripcion = nuevaDescripcion.trim();
        }
        if (nuevoPrecio != null && nuevoPrecio.compareTo(BigDecimal.ZERO) > 0) {
            this.precio = nuevoPrecio;
        }
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Activa el producto.
     */
    public void activar() {
        this.activo = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Desactiva el producto.
     */
    public void desactivar() {
        this.activo = false;
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Builder para crear instancias de Producto con validaciones.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Producto producto = new Producto();

        public Builder withId(Long id) {
            producto.id = id;
            return this;
        }

        public Builder withNombre(String nombre) {
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new InvalidProductDataException("Product name cannot be null or empty");
            }
            if (nombre.length() > 255) {
                throw new InvalidProductDataException("Product name cannot exceed 255 characters");
            }
            producto.nombre = nombre.trim();
            return this;
        }

        public Builder withDescripcion(String descripcion) {
            if (descripcion != null && descripcion.length() > 1000) {
                throw new InvalidProductDataException("Product description cannot exceed 1000 characters");
            }
            producto.descripcion = descripcion != null ? descripcion.trim() : null;
            return this;
        }

        public Builder withPrecio(BigDecimal precio) {
            if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidProductDataException("Product price must be greater than zero");
            }
            if (precio.scale() > 2) {
                throw new InvalidProductDataException("Product price cannot have more than 2 decimal places");
            }
            producto.precio = precio;
            return this;
        }

        public Builder withTipo(TipoProducto tipo) {
            if (tipo == null) {
                throw new InvalidProductDataException("Product type cannot be null");
            }
            producto.tipo = tipo;
            return this;
        }

        public Builder withEspecie(EspecieAnimal especie) {
            if (especie == null) {
                throw new InvalidProductDataException("Animal species cannot be null");
            }
            producto.especie = especie;
            return this;
        }

        public Builder withStock(Integer stock) {
            if (stock != null && stock < 0) {
                throw new InvalidProductDataException("Stock cannot be negative");
            }
            producto.stock = stock;
            return this;
        }

        public Builder withActivo(boolean activo) {
            producto.activo = activo;
            return this;
        }

        public Builder withImagenUrl(String imagenUrl) {
            if (imagenUrl != null && imagenUrl.length() > 500) {
                throw new InvalidProductDataException("Image URL cannot exceed 500 characters");
            }
            producto.imagenUrl = imagenUrl;
            return this;
        }

        public Builder withFechaCreacion(LocalDateTime fechaCreacion) {
            producto.fechaCreacion = fechaCreacion;
            return this;
        }

        public Builder withFechaActualizacion(LocalDateTime fechaActualizacion) {
            producto.fechaActualizacion = fechaActualizacion;
            return this;
        }

        public Producto build() {
            // Validaciones obligatorias
            if (producto.nombre == null) {
                throw new InvalidProductDataException("Product name is required");
            }
            if (producto.precio == null) {
                throw new InvalidProductDataException("Product price is required");
            }
            if (producto.tipo == null) {
                throw new InvalidProductDataException("Product type is required");
            }
            if (producto.especie == null) {
                throw new InvalidProductDataException("Animal species is required");
            }

            // Establecer fechas por defecto
            LocalDateTime now = LocalDateTime.now();
            if (producto.fechaCreacion == null) {
                producto.fechaCreacion = now;
            }
            if (producto.fechaActualizacion == null) {
                producto.fechaActualizacion = now;
            }

            return producto;
        }
    }

    @Override
    public String toString() {
        return String.format("Producto{id=%d, nombre='%s', precio=%s, tipo=%s, especie=%s, activo=%s}",
                id, nombre, precio, tipo, especie, activo);
    }
}