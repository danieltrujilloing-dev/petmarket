package com.interview.petmarket.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Inventario.
 */
@Data
@Builder
public class InventarioResponseDto {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer stockDisponible;
    private Integer umbralReposicion;
    private Boolean necesitaReposicion;
    private Double porcentajeStock;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
