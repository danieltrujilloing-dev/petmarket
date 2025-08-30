package com.interview.petmarket.web.mapper;

import com.interview.petmarket.domain.model.producto.EspecieAnimal;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.model.producto.TipoProducto;
import com.interview.petmarket.web.dto.CreateProductoRequestDto;
import com.interview.petmarket.web.dto.ProductoResponseDto;
import com.interview.petmarket.web.dto.UpdateProductoRequestDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper entre DTOs web y objetos de dominio para productos.
 */
@Component
public class ProductoWebMapper {

    /**
     * Convierte de objeto de dominio a DTO de respuesta.
     */
    public ProductoResponseDto toResponseDto(Producto producto) {
        if (producto == null) {
            return null;
        }

        return new ProductoResponseDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getTipo() != null ? producto.getTipo().getDescripcion() : null,
                producto.getEspecie() != null ? producto.getEspecie().getDescripcion() : null,
                producto.getStock(),
                producto.isActivo(),
                producto.estaDisponible(),
                producto.getImagenUrl(),
                producto.getFechaCreacion(),
                producto.getFechaActualizacion()
        );
    }

    /**
     * Convierte de DTO de creación a objeto de dominio.
     */
    public Producto fromCreateDto(CreateProductoRequestDto dto) {
        if (dto == null) {
            return null;
        }

        TipoProducto tipo = TipoProducto.fromDescripcion(dto.getTipo());
        EspecieAnimal especie = EspecieAnimal.fromDescripcion(dto.getEspecie());

        return Producto.builder()
                .withNombre(dto.getNombre())
                .withDescripcion(dto.getDescripcion())
                .withPrecio(dto.getPrecio())
                .withTipo(tipo)
                .withEspecie(especie)
                .withStock(dto.getStock())
                .withActivo(dto.isActivo())
                .withImagenUrl(dto.getImagenUrl())
                .build();
    }

    /**
     * Aplica actualizaciones de DTO a objeto de dominio existente.
     * Solo actualiza los campos que no son null en el DTO.
     */
    public Producto applyUpdateDto(Producto producto, UpdateProductoRequestDto dto) {
        if (producto == null || dto == null) {
            return producto;
        }

        // Crear builder a partir del producto existente
        Producto.Builder builder = Producto.builder()
                .withId(producto.getId())
                .withNombre(dto.getNombre() != null ? dto.getNombre() : producto.getNombre())
                .withDescripcion(dto.getDescripcion() != null ? dto.getDescripcion() : producto.getDescripcion())
                .withPrecio(dto.getPrecio() != null ? dto.getPrecio() : producto.getPrecio())
                .withTipo(producto.getTipo()) // Tipo no se actualiza
                .withEspecie(producto.getEspecie()) // Especie no se actualiza
                .withStock(dto.getStock() != null ? dto.getStock() : producto.getStock())
                .withActivo(producto.isActivo()) // Activo se actualiza por endpoints específicos
                .withImagenUrl(dto.getImagenUrl() != null ? dto.getImagenUrl() : producto.getImagenUrl())
                .withFechaCreacion(producto.getFechaCreacion())
                .withFechaActualizacion(producto.getFechaActualizacion());

        return builder.build();
    }

    /**
     * Convierte lista de productos de dominio a lista de DTOs de respuesta.
     */
    public List<ProductoResponseDto> toResponseDtoList(List<Producto> productos) {
        if (productos == null) {
            return List.of();
        }

        return productos.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Convierte lista de DTOs de creación a lista de productos de dominio.
     */
    public List<Producto> fromCreateDtoList(List<CreateProductoRequestDto> dtos) {
        if (dtos == null) {
            return List.of();
        }

        return dtos.stream()
                .map(this::fromCreateDto)
                .collect(Collectors.toList());
    }

    // ================ MÉTODOS AUXILIARES ================

    /**
     * Valida que el tipo de producto sea válido.
     */
    public boolean esTipoValido(String tipo) {
        return TipoProducto.fromDescripcion(tipo) != null;
    }

    /**
     * Valida que la especie animal sea válida.
     */
    public boolean esEspecieValida(String especie) {
        return EspecieAnimal.fromDescripcion(especie) != null;
    }

    /**
     * Obtiene lista de tipos de producto disponibles.
     */
    public List<String> obtenerTiposDisponibles() {
        return List.of(
                TipoProducto.ALIMENTO.getDescripcion(),
                TipoProducto.ACCESORIO.getDescripcion(),
                TipoProducto.JUGUETE.getDescripcion(),
                TipoProducto.MEDICINA.getDescripcion(),
                TipoProducto.HIGIENE.getDescripcion(),
                TipoProducto.CAMA.getDescripcion(),
                TipoProducto.COLLAR.getDescripcion(),
                TipoProducto.CORREA.getDescripcion(),
                TipoProducto.TRANSPORTADORA.getDescripcion(),
                TipoProducto.ENTRENAMIENTO.getDescripcion()
        );
    }

    /**
     * Obtiene lista de especies disponibles.
     */
    public List<String> obtenerEspeciesDisponibles() {
        return List.of(
                EspecieAnimal.PERRO.getDescripcion(),
                EspecieAnimal.GATO.getDescripcion(),
                EspecieAnimal.AVE.getDescripcion(),
                EspecieAnimal.PEZ.getDescripcion(),
                EspecieAnimal.REPTIL.getDescripcion(),
                EspecieAnimal.ROEDOR.getDescripcion(),
                EspecieAnimal.CONEJO.getDescripcion(),
                EspecieAnimal.HAMSTER.getDescripcion(),
                EspecieAnimal.UNIVERSAL.getDescripcion()
        );
    }

    /**
     * Crea un DTO de respuesta simplificado (solo información básica).
     */
    public ProductoResponseDto toSimpleResponseDto(Producto producto) {
        if (producto == null) {
            return null;
        }

        ProductoResponseDto dto = new ProductoResponseDto();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setTipo(producto.getTipo() != null ? producto.getTipo().getDescripcion() : null);
        dto.setEspecie(producto.getEspecie() != null ? producto.getEspecie().getDescripcion() : null);
        dto.setDisponible(producto.estaDisponible());
        dto.setImagenUrl(producto.getImagenUrl());

        return dto;
    }

    /**
     * Convierte lista de productos a DTOs simplificados.
     */
    public List<ProductoResponseDto> toSimpleResponseDtoList(List<Producto> productos) {
        if (productos == null) {
            return List.of();
        }

        return productos.stream()
                .map(this::toSimpleResponseDto)
                .collect(Collectors.toList());
    }
}
