package com.interview.petmarket.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de respuesta para el carrito.
 */
@Data
@Builder
public class CarritoResponseDto {
    
    private Long id;
    private Long clienteId;
    private List<CarritoItemResponseDto> items;
    private int totalItems;
    private boolean vacio;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaActualizacion;
}
