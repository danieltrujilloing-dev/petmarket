package com.interview.petmarket.domain.model.common;

import java.time.LocalDateTime;

/**
 * Clase base para todas las entidades del dominio
 */
public abstract class BaseEntity {
    
    protected Long id;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    
    public BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public BaseEntity(Long id) {
        this();
        this.id = id;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Método para actualizar el timestamp
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Métodos de comparación por ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BaseEntity that = (BaseEntity) obj;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
