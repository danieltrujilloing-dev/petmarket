package com.interview.petmarket.web.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO de respuesta para estadísticas de tareas.
 */
@Data
@Builder
public class EstadisticasTareasResponseDto {
    private Integer totalTareas;
    private Integer tareasPendientes;
    private Integer tareasEnProceso;
    private Integer tareasCompletadas;
    private Integer tareasCriticas;
    private Integer tareasVencidas;
    private Double porcentajeCompletadas;
    private Double porcentajeCriticas;
}
