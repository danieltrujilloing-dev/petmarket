package com.interview.petmarket.web.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta para items del carrito.
 */
@Data
@Builder
public class CarritoItemResponseDto {
    
    private Long productoId;
    private String nombreProducto;
    private int cantidad;
}
