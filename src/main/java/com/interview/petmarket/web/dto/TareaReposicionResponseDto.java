package com.interview.petmarket.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para Tarea de Reposición.
 */
@Data
@Builder
public class TareaReposicionResponseDto {
    private Long id;
    private Long productoId;
    private String nombreProducto;
    private Integer stockActual;
    private Integer umbralReposicion;
    private Integer cantidadSugerida;
    private String estado;
    private String prioridad;
    private String observaciones;
    private LocalDateTime fechaVencimiento;
    private Boolean estaVencida;
    private Boolean esCritica;
    private Double porcentajeStock;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
