package com.interview.petmarket.web.mapper;

import com.interview.petmarket.domain.model.inventario.TareaReposicion;
import com.interview.petmarket.domain.ports.in.GestionarTareasReposicionUseCase.EstadisticasTareas;
import com.interview.petmarket.web.dto.EstadisticasTareasResponseDto;
import com.interview.petmarket.web.dto.TareaReposicionResponseDto;
import org.springframework.stereotype.Component;

/**
 * Mapper entre TareaReposicion (dominio) y DTOs web.
 * Implementa el patrón Adapter para separar el dominio de la capa web.
 */
@Component
public class TareaReposicionWebMapper {
    
    /**
     * Convierte de modelo de dominio a DTO de respuesta.
     */
    public TareaReposicionResponseDto toResponseDto(TareaReposicion tarea) {
        if (tarea == null) {
            return null;
        }
        
        return TareaReposicionResponseDto.builder()
                .id(tarea.getId())
                .productoId(tarea.getProductoId())
                .nombreProducto(tarea.getNombreProducto())
                .stockActual(tarea.getStockActual())
                .umbralReposicion(tarea.getUmbralReposicion())
                .cantidadSugerida(tarea.getCantidadSugerida())
                .estado(tarea.getEstado().name())
                .prioridad(tarea.getPrioridad().name())
                .observaciones(tarea.getObservaciones())
                .fechaVencimiento(tarea.getFechaVencimiento())
                .estaVencida(tarea.estaVencida())
                .esCritica(tarea.esCritica())
                .porcentajeStock(tarea.calcularPorcentajeStock())
                .fechaCreacion(tarea.getCreatedAt())
                .fechaActualizacion(tarea.getUpdatedAt())
                .build();
    }
    
    /**
     * Convierte estadísticas de dominio a DTO de respuesta.
     */
    public EstadisticasTareasResponseDto toEstadisticasDto(EstadisticasTareas estadisticas) {
        if (estadisticas == null) {
            return null;
        }
        
        double porcentajeCompletadas = estadisticas.totalTareas() > 0 
                ? (double) estadisticas.tareasCompletadas() / estadisticas.totalTareas() * 100.0 
                : 0.0;
        
        double porcentajeCriticas = estadisticas.totalTareas() > 0 
                ? (double) estadisticas.tareasCriticas() / estadisticas.totalTareas() * 100.0 
                : 0.0;
        
        return EstadisticasTareasResponseDto.builder()
                .totalTareas(estadisticas.totalTareas())
                .tareasPendientes(estadisticas.tareasPendientes())
                .tareasEnProceso(estadisticas.tareasEnProceso())
                .tareasCompletadas(estadisticas.tareasCompletadas())
                .tareasCriticas(estadisticas.tareasCriticas())
                .tareasVencidas(estadisticas.tareasVencidas())
                .porcentajeCompletadas(Math.round(porcentajeCompletadas * 100.0) / 100.0)
                .porcentajeCriticas(Math.round(porcentajeCriticas * 100.0) / 100.0)
                .build();
    }
}
