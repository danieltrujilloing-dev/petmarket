package com.interview.petmarket.web.mapper;

import com.interview.petmarket.domain.model.inventario.Inventario;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.web.dto.InventarioResponseDto;
import org.springframework.stereotype.Component;

/**
 * Mapper entre Inventario (dominio) y DTOs web.
 * Implementa el patrón Adapter para separar el dominio de la capa web.
 */
@Component
public class InventarioWebMapper {
    
    /**
     * Convierte de modelo de dominio a DTO de respuesta.
     */
    public InventarioResponseDto toResponseDto(Inventario inventario, Producto producto) {
        if (inventario == null) {
            return null;
        }
        
        String nombreProducto = producto != null ? producto.getNombre() : "Producto " + inventario.getProductoId();
        double porcentajeStock = calcularPorcentajeStock(inventario);
        
        return InventarioResponseDto.builder()
                .id(inventario.getId())
                .productoId(inventario.getProductoId())
                .nombreProducto(nombreProducto)
                .stockDisponible(inventario.getStockDisponible())
                .umbralReposicion(inventario.getUmbralReposicion())
                .necesitaReposicion(inventario.necesitaReposicion())
                .porcentajeStock(porcentajeStock)
                .fechaCreacion(inventario.getCreatedAt())
                .fechaActualizacion(inventario.getUpdatedAt())
                .build();
    }
    
    /**
     * Convierte de modelo de dominio a DTO de respuesta (sin producto).
     */
    public InventarioResponseDto toResponseDto(Inventario inventario) {
        return toResponseDto(inventario, null);
    }
    
    /**
     * Calcula el porcentaje de stock respecto al umbral.
     */
    private double calcularPorcentajeStock(Inventario inventario) {
        if (inventario.getUmbralReposicion() == 0) {
            return 100.0;
        }
        return (double) inventario.getStockDisponible() / inventario.getUmbralReposicion() * 100.0;
    }
}
