package com.interview.petmarket.web.mapper;

import com.interview.petmarket.domain.model.pedido.Pedido;
import com.interview.petmarket.domain.model.pedido.PedidoItem;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.ports.out.ProductoRepositoryPort;
import com.interview.petmarket.web.dto.PedidoItemResponseDto;
import com.interview.petmarket.web.dto.PedidoResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades de dominio y DTOs web del pedido.
 */
@Component
public class PedidoWebMapper {
    
    private final ProductoRepositoryPort productoRepository;
    
    public PedidoWebMapper(ProductoRepositoryPort productoRepository) {
        this.productoRepository = productoRepository;
    }
    
    public PedidoResponseDto toResponseDto(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        
        List<PedidoItemResponseDto> itemsDto = pedido.getItems().stream()
                .map(this::toItemResponseDto)
                .collect(Collectors.toList());
        
        return PedidoResponseDto.builder()
                .id(pedido.getId())
                .clienteId(pedido.getClienteId())
                .items(itemsDto)
                .total(pedido.getTotal())
                .estado(pedido.getEstado().name())
                .totalItems(pedido.contarItems())
                .fechaCreacion(pedido.getCreatedAt())
                .fechaActualizacion(pedido.getUpdatedAt())
                .build();
    }
    
    private PedidoItemResponseDto toItemResponseDto(PedidoItem item) {
        if (item == null) {
            return null;
        }
        
        // Obtener nombre del producto
        String nombreProducto = productoRepository.findById(item.getProductoId())
                .map(Producto::getNombre)
                .orElse("Producto no encontrado");
        
        return PedidoItemResponseDto.builder()
                .productoId(item.getProductoId())
                .nombreProducto(nombreProducto)
                .cantidad(item.getCantidad())
                .precioUnitario(item.getPrecioUnitario())
                .subtotal(item.getSubtotal())
                .build();
    }
}
