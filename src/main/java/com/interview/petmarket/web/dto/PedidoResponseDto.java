package com.interview.petmarket.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para pedidos.
 */
@Data
@Builder
public class PedidoResponseDto {
    
    private Long id;
    private Long clienteId;
    private List<PedidoItemResponseDto> items;
    private BigDecimal total;
    private String estado;
    private int totalItems;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaActualizacion;
}
