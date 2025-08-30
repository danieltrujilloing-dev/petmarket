package com.interview.petmarket.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO de request para agregar items al carrito.
 */
@Data
public class AgregarItemRequestDto {
    
    @NotNull(message = "Product ID is required")
    private Long productoId;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer cantidad;
}
