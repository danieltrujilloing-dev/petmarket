package com.interview.petmarket.web.mapper;

import com.interview.petmarket.domain.model.carrito.Carrito;
import com.interview.petmarket.domain.model.carrito.CarritoItem;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.ports.out.ProductoRepositoryPort;
import com.interview.petmarket.web.dto.CarritoItemResponseDto;
import com.interview.petmarket.web.dto.CarritoResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades de dominio y DTOs web del carrito.
 */
@Component
public class CarritoWebMapper {
    
    private final ProductoRepositoryPort productoRepository;
    
    public CarritoWebMapper(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }
    
    public CarritoResponseDto toResponseDto(Carrito carrito) {
        if (carrito == null) {
            return null;
        }
        
        List<CarritoItemResponseDto> itemsDto = carrito.getItems().stream()
                .map(this::toItemResponseDto)
                .collect(Collectors.toList());
        
        return CarritoResponseDto.builder()
                .id(carrito.getId())
                .clienteId(carrito.getClienteId())
                .items(itemsDto)
                .totalItems(carrito.contarItems())
                .vacio(carrito.estaVacio())
                .fechaCreacion(carrito.getCreatedAt())
                .fechaActualizacion(carrito.getUpdatedAt())
                .build();
    }
    
    private CarritoItemResponseDto toItemResponseDto(CarritoItem item) {
        if (item == null) {
            return null;
        }
        
        // Obtener nombre del producto
        String nombreProducto = productoRepository.findById(item.getProductoId())
                .map(Producto::getNombre)
                .orElse("Producto no encontrado");
        
        return CarritoItemResponseDto.builder()
                .productoId(item.getProductoId())
                .nombreProducto(nombreProducto)
                .cantidad(item.getCantidad())
                .build();
    }
}
