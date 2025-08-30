package com.interview.petmarket.infrastructure.persistence.jpa.mapper;

import com.interview.petmarket.domain.model.pedido.EstadoPedido;
import com.interview.petmarket.domain.model.pedido.Pedido;
import com.interview.petmarket.domain.model.pedido.PedidoItem;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.EstadoPedidoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.PedidoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.PedidoItemEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper entre entidades de dominio y entidades JPA para Pedido.
 */
@Component
public class PedidoMapper {
    
    public Pedido toDomain(PedidoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        List<PedidoItem> items = entity.getItems().stream()
                .map(this::toDomainItem)
                .collect(Collectors.toList());
        
        return Pedido.builder()
                .withId(entity.getId())
                .withClienteId(entity.getClienteId())
                .withItems(items)
                .withTotal(entity.getTotal())
                .withEstado(toDomainEstado(entity.getEstado()))
                .build();
    }
    
    public PedidoEntity toEntity(Pedido domain) {
        if (domain == null) {
            return null;
        }
        
        PedidoEntity entity = PedidoEntity.builder()
                .id(domain.getId())
                .clienteId(domain.getClienteId())
                .total(domain.getTotal())
                .estado(toEntityEstado(domain.getEstado()))
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
        
        List<PedidoItemEntity> itemEntities = domain.getItems().stream()
                .map(item -> toEntityItem(item, entity))
                .collect(Collectors.toList());
        
        entity.setItems(itemEntities);
        return entity;
    }
    
    private PedidoItem toDomainItem(PedidoItemEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new PedidoItem(
                entity.getProductoId(),
                entity.getCantidad(),
                entity.getPrecioUnitario()
        );
    }
    
    private PedidoItemEntity toEntityItem(PedidoItem domain, PedidoEntity pedidoEntity) {
        if (domain == null) {
            return null;
        }
        
        return PedidoItemEntity.builder()
                .pedido(pedidoEntity)
                .productoId(domain.getProductoId())
                .cantidad(domain.getCantidad())
                .precioUnitario(domain.getPrecioUnitario())
                .subtotal(domain.getSubtotal())
                .build();
    }
    
    private EstadoPedido toDomainEstado(EstadoPedidoEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return EstadoPedido.valueOf(entity.name());
    }
    
    private EstadoPedidoEntity toEntityEstado(EstadoPedido domain) {
        if (domain == null) {
            return null;
        }
        
        return EstadoPedidoEntity.valueOf(domain.name());
    }
}
