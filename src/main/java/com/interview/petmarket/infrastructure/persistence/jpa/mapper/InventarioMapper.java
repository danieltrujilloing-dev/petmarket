package com.interview.petmarket.infrastructure.persistence.jpa.mapper;

import com.interview.petmarket.domain.model.inventario.Inventario;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.InventarioEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper entre entidades de dominio y entidades JPA para Inventario.
 */
@Component
public class InventarioMapper {
    
    public Inventario toDomain(InventarioEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Inventario.builder()
                .withId(entity.getId())
                .withProductoId(entity.getProductoId())
                .withStockDisponible(entity.getStockDisponible())
                .withUmbralReposicion(entity.getUmbralReposicion())
                .build();
    }
    
    public InventarioEntity toEntity(Inventario domain) {
        if (domain == null) {
            return null;
        }
        
        return InventarioEntity.builder()
                .id(domain.getId())
                .productoId(domain.getProductoId())
                .stockDisponible(domain.getStockDisponible())
                .umbralReposicion(domain.getUmbralReposicion())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
