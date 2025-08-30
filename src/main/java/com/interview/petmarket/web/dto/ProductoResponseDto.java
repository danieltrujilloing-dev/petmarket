package com.interview.petmarket.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de respuesta para productos en la API REST usando Lombok.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDto {
    
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String tipo;
    private String especie;
    private Integer stock;
    private boolean activo;
    private boolean disponible;
    private String imagenUrl;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaCreacion;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaActualizacion;
}
