package com.interview.petmarket.infrastructure.persistence.jpa.mapper;

import com.interview.petmarket.domain.model.inventario.EstadoTarea;
import com.interview.petmarket.domain.model.inventario.PrioridadTarea;
import com.interview.petmarket.domain.model.inventario.TareaReposicion;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.EstadoTareaEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.PrioridadTareaEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.TareaReposicionEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper entre TareaReposicion (dominio) y TareaReposicionEntity (JPA).
 * Implementa el patrón Adapter para separar el dominio de la infraestructura.
 */
@Component
public class TareaReposicionMapper {
    
    /**
     * Convierte de entidad JPA a modelo de dominio.
     */
    public TareaReposicion toDomain(TareaReposicionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return TareaReposicion.builder()
                .withId(entity.getId())
                .withProductoId(entity.getProductoId())
                .withNombreProducto(entity.getNombreProducto())
                .withStockActual(entity.getStockActual())
                .withUmbralReposicion(entity.getUmbralReposicion())
                .withCantidadSugerida(entity.getCantidadSugerida())
                .withEstado(toDomainEstado(entity.getEstado()))
                .withPrioridad(toDomainPrioridad(entity.getPrioridad()))
                .withObservaciones(entity.getObservaciones())
                .withFechaVencimiento(entity.getFechaVencimiento())
                .build();
    }
    
    /**
     * Convierte de modelo de dominio a entidad JPA.
     */
    public TareaReposicionEntity toEntity(TareaReposicion domain) {
        if (domain == null) {
            return null;
        }
        
        return TareaReposicionEntity.builder()
                .id(domain.getId())
                .productoId(domain.getProductoId())
                .nombreProducto(domain.getNombreProducto())
                .stockActual(domain.getStockActual())
                .umbralReposicion(domain.getUmbralReposicion())
                .cantidadSugerida(domain.getCantidadSugerida())
                .estado(toEntityEstado(domain.getEstado()))
                .prioridad(toEntityPrioridad(domain.getPrioridad()))
                .observaciones(domain.getObservaciones())
                .fechaVencimiento(domain.getFechaVencimiento())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
    
    /**
     * Convierte estado de entidad a dominio.
     */
    public EstadoTarea toDomainEstado(EstadoTareaEntity entityEstado) {
        if (entityEstado == null) {
            return null;
        }
        
        return switch (entityEstado) {
            case PENDIENTE -> EstadoTarea.PENDIENTE;
            case EN_PROCESO -> EstadoTarea.EN_PROCESO;
            case COMPLETADA -> EstadoTarea.COMPLETADA;
            case CANCELADA -> EstadoTarea.CANCELADA;
        };
    }
    
    /**
     * Convierte estado de dominio a entidad.
     */
    public EstadoTareaEntity toEntityEstado(EstadoTarea domainEstado) {
        if (domainEstado == null) {
            return null;
        }
        
        return switch (domainEstado) {
            case PENDIENTE -> EstadoTareaEntity.PENDIENTE;
            case EN_PROCESO -> EstadoTareaEntity.EN_PROCESO;
            case COMPLETADA -> EstadoTareaEntity.COMPLETADA;
            case CANCELADA -> EstadoTareaEntity.CANCELADA;
        };
    }
    
    /**
     * Convierte prioridad de entidad a dominio.
     */
    public PrioridadTarea toDomainPrioridad(PrioridadTareaEntity entityPrioridad) {
        if (entityPrioridad == null) {
            return null;
        }
        
        return switch (entityPrioridad) {
            case CRITICA -> PrioridadTarea.CRITICA;
            case ALTA -> PrioridadTarea.ALTA;
            case MEDIA -> PrioridadTarea.MEDIA;
            case BAJA -> PrioridadTarea.BAJA;
        };
    }
    
    /**
     * Convierte prioridad de dominio a entidad.
     */
    public PrioridadTareaEntity toEntityPrioridad(PrioridadTarea domainPrioridad) {
        if (domainPrioridad == null) {
            return null;
        }
        
        return switch (domainPrioridad) {
            case CRITICA -> PrioridadTareaEntity.CRITICA;
            case ALTA -> PrioridadTareaEntity.ALTA;
            case MEDIA -> PrioridadTareaEntity.MEDIA;
            case BAJA -> PrioridadTareaEntity.BAJA;
        };
    }
}
