package com.interview.petmarket.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de petición para actualizar productos usando Lombok.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductoRequestDto {

    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String nombre;

    @Size(max = 1000, message = "Product description cannot exceed 1000 characters")
    private String descripcion;

    @DecimalMin(value = "0.01", message = "Product price must be greater than zero")
    @Digits(integer = 8, fraction = 2, message = "Product price format is invalid")
    private BigDecimal precio;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imagenUrl;

    /**
     * Verifica si al menos un campo está presente para actualización.
     */
    public boolean tieneAlgunCampo() {
        return nombre != null || descripcion != null || precio != null || 
               stock != null || imagenUrl != null;
    }
}
