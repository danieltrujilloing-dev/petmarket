package com.interview.petmarket.infrastructure.persistence.jpa.mapper;

import com.interview.petmarket.domain.model.producto.EspecieAnimal;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.model.producto.TipoProducto;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.ProductoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.TipoProductoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.EspecieAnimalEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper entre entidades JPA y objetos de dominio para productos.
 */
@Component
public class ProductoMapper {

    /**
     * Convierte de entidad JPA a objeto de dominio.
     */
    public Producto toDomain(ProductoEntity entity) {
        if (entity == null) {
            return null;
        }

        return Producto.builder()
                .withId(entity.getId())
                .withNombre(entity.getNombre())
                .withDescripcion(entity.getDescripcion())
                .withPrecio(entity.getPrecio())
                .withTipo(mapTipoToDomain(entity.getTipo()))
                .withEspecie(mapEspecieToDomain(entity.getEspecie()))
                .withStock(entity.getStock())
                .withActivo(entity.getActivo() != null ? entity.getActivo() : false)
                .withImagenUrl(entity.getImagenUrl())
                .withFechaCreacion(entity.getFechaCreacion())
                .withFechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    /**
     * Convierte de objeto de dominio a entidad JPA.
     */
    public ProductoEntity toEntity(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoEntity entity = new ProductoEntity();
        entity.setId(producto.getId());
        entity.setNombre(producto.getNombre());
        entity.setDescripcion(producto.getDescripcion());
        entity.setPrecio(producto.getPrecio());
        entity.setTipo(mapTipoToEntity(producto.getTipo()));
        entity.setEspecie(mapEspecieToEntity(producto.getEspecie()));
        entity.setStock(producto.getStock());
        entity.setActivo(producto.isActivo());
        entity.setImagenUrl(producto.getImagenUrl());
        entity.setFechaCreacion(producto.getFechaCreacion());
        entity.setFechaActualizacion(producto.getFechaActualizacion());
        
        return entity;
    }

    /**
     * Actualiza una entidad existente con datos del dominio.
     */
    public void updateEntity(ProductoEntity entity, Producto producto) {
        if (entity == null || producto == null) {
            return;
        }

        entity.setNombre(producto.getNombre());
        entity.setDescripcion(producto.getDescripcion());
        entity.setPrecio(producto.getPrecio());
        entity.setTipo(mapTipoToEntity(producto.getTipo()));
        entity.setEspecie(mapEspecieToEntity(producto.getEspecie()));
        entity.setStock(producto.getStock());
        entity.setActivo(producto.isActivo());
        entity.setImagenUrl(producto.getImagenUrl());
        entity.setFechaActualizacion(producto.getFechaActualizacion());
    }

    /**
     * Convierte lista de entidades a lista de objetos de dominio.
     */
    public List<Producto> toDomainList(List<ProductoEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convierte lista de objetos de dominio a lista de entidades.
     */
    public List<ProductoEntity> toEntityList(List<Producto> productos) {
        if (productos == null) {
            return List.of();
        }

        return productos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    // ================ MAPPERS PARA ENUMS ================

    private TipoProducto mapTipoToDomain(TipoProductoEntity entity) {
        if (entity == null) {
            return null;
        }

        return switch (entity) {
            case ALIMENTO -> TipoProducto.ALIMENTO;
            case ACCESORIO -> TipoProducto.ACCESORIO;
            case JUGUETE -> TipoProducto.JUGUETE;
            case MEDICINA -> TipoProducto.MEDICINA;
            case HIGIENE -> TipoProducto.HIGIENE;
            case CAMA -> TipoProducto.CAMA;
            case COLLAR -> TipoProducto.COLLAR;
            case CORREA -> TipoProducto.CORREA;
            case TRANSPORTADORA -> TipoProducto.TRANSPORTADORA;
            case ENTRENAMIENTO -> TipoProducto.ENTRENAMIENTO;
        };
    }

    private TipoProductoEntity mapTipoToEntity(TipoProducto domain) {
        if (domain == null) {
            return null;
        }

        return switch (domain) {
            case ALIMENTO -> TipoProductoEntity.ALIMENTO;
            case ACCESORIO -> TipoProductoEntity.ACCESORIO;
            case JUGUETE -> TipoProductoEntity.JUGUETE;
            case MEDICINA -> TipoProductoEntity.MEDICINA;
            case HIGIENE -> TipoProductoEntity.HIGIENE;
            case CAMA -> TipoProductoEntity.CAMA;
            case COLLAR -> TipoProductoEntity.COLLAR;
            case CORREA -> TipoProductoEntity.CORREA;
            case TRANSPORTADORA -> TipoProductoEntity.TRANSPORTADORA;
            case ENTRENAMIENTO -> TipoProductoEntity.ENTRENAMIENTO;
        };
    }

    private EspecieAnimal mapEspecieToDomain(EspecieAnimalEntity entity) {
        if (entity == null) {
            return null;
        }

        return switch (entity) {
            case PERRO -> EspecieAnimal.PERRO;
            case GATO -> EspecieAnimal.GATO;
            case AVE -> EspecieAnimal.AVE;
            case PEZ -> EspecieAnimal.PEZ;
            case REPTIL -> EspecieAnimal.REPTIL;
            case ROEDOR -> EspecieAnimal.ROEDOR;
            case CONEJO -> EspecieAnimal.CONEJO;
            case HAMSTER -> EspecieAnimal.HAMSTER;
            case UNIVERSAL -> EspecieAnimal.UNIVERSAL;
        };
    }

    private EspecieAnimalEntity mapEspecieToEntity(EspecieAnimal domain) {
        if (domain == null) {
            return null;
        }

        return switch (domain) {
            case PERRO -> EspecieAnimalEntity.PERRO;
            case GATO -> EspecieAnimalEntity.GATO;
            case AVE -> EspecieAnimalEntity.AVE;
            case PEZ -> EspecieAnimalEntity.PEZ;
            case REPTIL -> EspecieAnimalEntity.REPTIL;
            case ROEDOR -> EspecieAnimalEntity.ROEDOR;
            case CONEJO -> EspecieAnimalEntity.CONEJO;
            case HAMSTER -> EspecieAnimalEntity.HAMSTER;
            case UNIVERSAL -> EspecieAnimalEntity.UNIVERSAL;
        };
    }
}
