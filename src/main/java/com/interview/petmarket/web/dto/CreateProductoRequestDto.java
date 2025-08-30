package com.interview.petmarket.web.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de petición para crear productos usando Lombok.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductoRequestDto {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name cannot exceed 255 characters")
    private String nombre;

    @Size(max = 1000, message = "Product description cannot exceed 1000 characters")
    private String descripcion;

    @NotNull(message = "Product price is required")
    @DecimalMin(value = "0.01", message = "Product price must be greater than zero")
    @Digits(integer = 8, fraction = 2, message = "Product price format is invalid")
    private BigDecimal precio;

    @NotBlank(message = "Product type is required")
    private String tipo;

    @NotBlank(message = "Animal species is required")
    private String especie;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    @Builder.Default
    private boolean activo = true;

    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imagenUrl;
}
