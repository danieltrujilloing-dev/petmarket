package com.interview.petmarket.infrastructure.persistence.jpa.mapper;

import com.interview.petmarket.domain.model.carrito.Carrito;
import com.interview.petmarket.domain.model.carrito.CarritoItem;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.CarritoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.CarritoItemEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper entre entidades de dominio y entidades JPA para Carrito.
 */
@Component
public class CarritoMapper {
    
    public Carrito toDomain(CarritoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        List<CarritoItem> items = entity.getItems().stream()
                .map(this::toDomainItem)
                .collect(Collectors.toList());
        
        return Carrito.builder()
                .withId(entity.getId())
                .withClienteId(entity.getClienteId())
                .withItems(items)
                .build();
    }
    
    public CarritoEntity toEntity(Carrito domain) {
        if (domain == null) {
            return null;
        }
        
        CarritoEntity entity = CarritoEntity.builder()
                .id(domain.getId())
                .clienteId(domain.getClienteId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .items(new ArrayList<>())
                .build();
        
        List<CarritoItemEntity> itemEntities = domain.getItems().stream()
                .map(item -> toEntityItem(item, entity))
                .collect(Collectors.toList());
        
        entity.setItems(itemEntities);
        return entity;
    }
    
    private CarritoItem toDomainItem(CarritoItemEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new CarritoItem(entity.getProductoId(), entity.getCantidad());
    }
    
    private CarritoItemEntity toEntityItem(CarritoItem domain, CarritoEntity carritoEntity) {
        if (domain == null) {
            return null;
        }
        
        return CarritoItemEntity.builder()
                .carrito(carritoEntity)
                .productoId(domain.getProductoId())
                .cantidad(domain.getCantidad())
                .precioUnitario(java.math.BigDecimal.ZERO) // Se actualiza en el checkout
                .build();
    }
}
