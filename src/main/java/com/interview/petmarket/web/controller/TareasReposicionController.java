package com.interview.petmarket.web.controller;

import com.interview.petmarket.domain.model.inventario.EstadoTarea;
import com.interview.petmarket.domain.model.inventario.PrioridadTarea;
import com.interview.petmarket.domain.model.inventario.TareaReposicion;
import com.interview.petmarket.domain.ports.in.GestionarTareasReposicionUseCase;
import com.interview.petmarket.web.dto.CompletarTareaRequestDto;
import com.interview.petmarket.web.dto.EstadisticasTareasResponseDto;
import com.interview.petmarket.web.dto.TareaReposicionResponseDto;
import com.interview.petmarket.web.mapper.TareaReposicionWebMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de tareas de reposición.
 * Expone endpoints para consultar y gestionar tareas automáticas de reposición.
 */
@RestController
@RequestMapping("/api/v1/tareas-reposicion")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TareasReposicionController {
    
    private static final Logger logger = LoggerFactory.getLogger(TareasReposicionController.class);
    
    private final GestionarTareasReposicionUseCase tareasService;
    private final TareaReposicionWebMapper webMapper;
    
    public TareasReposicionController(GestionarTareasReposicionUseCase tareasService,
                                     TareaReposicionWebMapper webMapper) {
        this.tareasService = tareasService;
        this.webMapper = webMapper;
    }
    
    /**
     * Obtiene una tarea específica por ID.
     */
    @GetMapping("/{tareaId}")
    public ResponseEntity<TareaReposicionResponseDto> obtenerTarea(@PathVariable Long tareaId) {
        logger.info("GET /api/v1/tareas-reposicion/{} - Obteniendo tarea", tareaId);
        
        try {
            TareaReposicion tarea = tareasService.obtenerTarea(tareaId);
            TareaReposicionResponseDto response = webMapper.toResponseDto(tarea);
            
            logger.info("Tarea obtenida exitosamente - ID: {}, Estado: {}", tareaId, tarea.getEstado());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo tarea: {}", tareaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene todas las tareas pendientes.
     */
    @GetMapping("/pendientes")
    public ResponseEntity<List<TareaReposicionResponseDto>> obtenerTareasPendientes() {
        logger.info("GET /api/v1/tareas-reposicion/pendientes - Obteniendo tareas pendientes");
        
        try {
            List<TareaReposicion> tareas = tareasService.obtenerTareasPendientes();
            List<TareaReposicionResponseDto> response = tareas.stream()
                    .map(webMapper::toResponseDto)
                    .collect(Collectors.toList());
            
            logger.info("Obtenidas {} tareas pendientes", response.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo tareas pendientes", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene tareas por estado.
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TareaReposicionResponseDto>> obtenerTareasPorEstado(@PathVariable String estado) {
        logger.info("GET /api/v1/tareas-reposicion/estado/{} - Obteniendo tareas por estado", estado);
        
        try {
            EstadoTarea estadoTarea = EstadoTarea.valueOf(estado.toUpperCase());
            List<TareaReposicion> tareas = tareasService.obtenerTareasPorEstado(estadoTarea);
            
            List<TareaReposicionResponseDto> response = tareas.stream()
                    .map(webMapper::toResponseDto)
                    .collect(Collectors.toList());
            
            logger.info("Obtenidas {} tareas con estado {}", response.size(), estado);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("Estado inválido: {}", estado, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error obteniendo tareas por estado: {}", estado, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene tareas por prioridad.
     */
    @GetMapping("/prioridad/{prioridad}")
    public ResponseEntity<List<TareaReposicionResponseDto>> obtenerTareasPorPrioridad(@PathVariable String prioridad) {
        logger.info("GET /api/v1/tareas-reposicion/prioridad/{} - Obteniendo tareas por prioridad", prioridad);
        
        try {
            PrioridadTarea prioridadTarea = PrioridadTarea.valueOf(prioridad.toUpperCase());
            List<TareaReposicion> tareas = tareasService.obtenerTareasPorPrioridad(prioridadTarea);
            
            List<TareaReposicionResponseDto> response = tareas.stream()
                    .map(webMapper::toResponseDto)
                    .collect(Collectors.toList());
            
            logger.info("Obtenidas {} tareas con prioridad {}", response.size(), prioridad);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("Prioridad inválida: {}", prioridad, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error obteniendo tareas por prioridad: {}", prioridad, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene tareas vencidas.
     */
    @GetMapping("/vencidas")
    public ResponseEntity<List<TareaReposicionResponseDto>> obtenerTareasVencidas() {
        logger.info("GET /api/v1/tareas-reposicion/vencidas - Obteniendo tareas vencidas");
        
        try {
            List<TareaReposicion> tareas = tareasService.obtenerTareasVencidas();
            List<TareaReposicionResponseDto> response = tareas.stream()
                    .map(webMapper::toResponseDto)
                    .collect(Collectors.toList());
            
            logger.info("Obtenidas {} tareas vencidas", response.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo tareas vencidas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Marca una tarea como en proceso.
     */
    @PutMapping("/{tareaId}/en-proceso")
    public ResponseEntity<TareaReposicionResponseDto> marcarTareaEnProceso(@PathVariable Long tareaId) {
        logger.info("PUT /api/v1/tareas-reposicion/{}/en-proceso - Marcando tarea en proceso", tareaId);
        
        try {
            TareaReposicion tareaActualizada = tareasService.marcarTareaEnProceso(tareaId);
            TareaReposicionResponseDto response = webMapper.toResponseDto(tareaActualizada);
            
            logger.info("Tarea {} marcada como en proceso exitosamente", tareaId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error marcando tarea {} en proceso", tareaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Completa una tarea de reposición.
     */
    @PutMapping("/{tareaId}/completar")
    public ResponseEntity<TareaReposicionResponseDto> completarTarea(@PathVariable Long tareaId,
                                                                    @Valid @RequestBody CompletarTareaRequestDto request) {
        logger.info("PUT /api/v1/tareas-reposicion/{}/completar - Completando tarea", tareaId);
        
        try {
            TareaReposicion tareaCompletada = tareasService.completarTarea(tareaId, request.getObservacionesFinales());
            TareaReposicionResponseDto response = webMapper.toResponseDto(tareaCompletada);
            
            logger.info("Tarea {} completada exitosamente", tareaId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error completando tarea: {}", tareaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene estadísticas de tareas.
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasTareasResponseDto> obtenerEstadisticas() {
        logger.info("GET /api/v1/tareas-reposicion/estadisticas - Obteniendo estadísticas");
        
        try {
            var estadisticas = tareasService.obtenerEstadisticas();
            EstadisticasTareasResponseDto response = webMapper.toEstadisticasDto(estadisticas);
            
            logger.info("Estadísticas obtenidas - Total: {}, Pendientes: {}, Críticas: {}", 
                       estadisticas.totalTareas(), estadisticas.tareasPendientes(), estadisticas.tareasCriticas());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo estadísticas de tareas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
