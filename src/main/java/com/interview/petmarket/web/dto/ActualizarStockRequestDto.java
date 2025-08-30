package com.interview.petmarket.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de request para actualizar stock.
 */
@Data
public class ActualizarStockRequestDto {
    
    @NotNull(message = "La nueva cantidad es requerida")
    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer nuevaCantidad;
    
    private String motivo;
}
