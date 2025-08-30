package com.interview.petmarket.domain.model.producto;

import com.interview.petmarket.domain.model.common.BaseEntity;
import com.interview.petmarket.domain.exceptions.InvalidProductDataException;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Entidad Producto del dominio
 * Representa un producto disponible en el marketplace
 */
public class Producto extends BaseEntity {
    
    private String nombre;
    private TipoProducto tipo;
    private String especieDestino;
    private BigDecimal precio;
    private Map<String, Object> atributos;
    private boolean activo;
    private String descripcion;
    private String imagenUrl;
    
    // Constructor privado para usar el builder
    private Producto(Builder builder) {
        super(builder.id);
        this.nombre = builder.nombre;
        this.tipo = builder.tipo;
        this.especieDestino = builder.especieDestino;
        this.precio = builder.precio;
        this.atributos = builder.atributos;
        this.activo = builder.activo != null ? builder.activo : true;
        this.descripcion = builder.descripcion;
        this.imagenUrl = builder.imagenUrl;
        
        validateProducto();
    }
    
    private void validateProducto() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new InvalidProductDataException("Product name cannot be null or empty");
        }
        if (tipo == null) {
            throw new InvalidProductDataException("Product type is required");
        }
        if (precio == null || precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductDataException("Product price must be non-negative");
        }
    }
    
    // Métodos de negocio
    public void activar() {
        this.activo = true;
        updateTimestamp();
    }
    
    public void desactivar() {
        this.activo = false;
        updateTimestamp();
    }
    
    public void actualizarPrecio(BigDecimal nuevoPrecio) {
        if (nuevoPrecio == null || nuevoPrecio.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductDataException("Price must be non-negative");
        }
        this.precio = nuevoPrecio;
        updateTimestamp();
    }
    
    public void actualizarDescripcion(String nuevaDescripcion) {
        this.descripcion = nuevaDescripcion;
        updateTimestamp();
    }
    
    public void actualizarImagenUrl(String nuevaImagenUrl) {
        this.imagenUrl = nuevaImagenUrl;
        updateTimestamp();
    }
    
    public boolean estaDisponible() {
        return this.activo;
    }
    
    public boolean esParaEspecie(String especie) {
        return this.especieDestino != null && 
               this.especieDestino.toLowerCase().contains(especie.toLowerCase());
    }
    
    public Object getAtributo(String clave) {
        return atributos != null ? atributos.get(clave) : null;
    }
    
    // Getters
    public String getNombre() {
        return nombre;
    }
    
    public TipoProducto getTipo() {
        return tipo;
    }
    
    public String getEspecieDestino() {
        return especieDestino;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public Map<String, Object> getAtributos() {
        return atributos;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public String getImagenUrl() {
        return imagenUrl;
    }
    
    // Builder Pattern
    public static class Builder {
        private Long id;
        private String nombre;
        private TipoProducto tipo;
        private String especieDestino;
        private BigDecimal precio;
        private Map<String, Object> atributos;
        private Boolean activo;
        private String descripcion;
        private String imagenUrl;
        
        public Builder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder withNombre(String nombre) {
            this.nombre = nombre;
            return this;
        }
        
        public Builder withTipo(TipoProducto tipo) {
            this.tipo = tipo;
            return this;
        }
        
        public Builder withEspecieDestino(String especieDestino) {
            this.especieDestino = especieDestino;
            return this;
        }
        
        public Builder withPrecio(BigDecimal precio) {
            this.precio = precio;
            return this;
        }
        
        public Builder withAtributos(Map<String, Object> atributos) {
            this.atributos = atributos;
            return this;
        }
        
        public Builder withActivo(Boolean activo) {
            this.activo = activo;
            return this;
        }
        
        public Builder withDescripcion(String descripcion) {
            this.descripcion = descripcion;
            return this;
        }
        
        public Builder withImagenUrl(String imagenUrl) {
            this.imagenUrl = imagenUrl;
            return this;
        }
        
        public Producto build() {
            return new Producto(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
