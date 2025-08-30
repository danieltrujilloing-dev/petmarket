package com.interview.petmarket.web.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DTO de respuesta para items del pedido.
 */
@Data
@Builder
public class PedidoItemResponseDto {
    
    private Long productoId;
    private String nombreProducto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
