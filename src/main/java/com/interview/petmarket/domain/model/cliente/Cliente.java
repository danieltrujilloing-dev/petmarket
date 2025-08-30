package com.interview.petmarket.domain.model.cliente;

import com.interview.petmarket.domain.model.common.BaseEntity;
import com.interview.petmarket.domain.exceptions.InvalidClientDataException;

import java.util.regex.Pattern;

/**
 * Entidad Cliente del dominio
 * Representa un cliente del marketplace (id, nombre, email)
 */
public class Cliente extends BaseEntity {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]+$");
    
    private String nombre;
    private String email;
    
    // Constructor privado para usar el builder
    private Cliente(Builder builder) {
        super(builder.id);
        this.nombre = builder.nombre;
        this.email = builder.email;
        
        validateCliente();
    }
    
    private void validateCliente() {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new InvalidClientDataException("Client name cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidClientDataException("Client email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidClientDataException("Invalid email format");
        }
    }
    
    // Métodos de negocio
    public void actualizarNombre(String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new InvalidClientDataException("Name cannot be null or empty");
        }
        this.nombre = nuevoNombre;
        updateTimestamp();
    }
    
    public void actualizarEmail(String nuevoEmail) {
        if (nuevoEmail == null || nuevoEmail.trim().isEmpty()) {
            throw new InvalidClientDataException("Email cannot be null or empty");
        }
        if (!EMAIL_PATTERN.matcher(nuevoEmail).matches()) {
            throw new InvalidClientDataException("Invalid email format");
        }
        this.email = nuevoEmail;
        updateTimestamp();
    }
    
    public boolean tieneInformacionCompleta() {
        return nombre != null && !nombre.trim().isEmpty() &&
               email != null && !email.trim().isEmpty();
    }
    
    // Getters
    public String getNombre() {
        return nombre;
    }
    
    public String getEmail() {
        return email;
    }
    
    // Builder Pattern
    public static class Builder {
        private Long id;
        private String nombre;
        private String email;
        
        public Builder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder withNombre(String nombre) {
            this.nombre = nombre;
            return this;
        }
        
        public Builder withEmail(String email) {
            this.email = email;
            return this;
        }
        
        public Cliente build() {
            return new Cliente(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
