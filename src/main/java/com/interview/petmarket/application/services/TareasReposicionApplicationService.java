package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.exceptions.InvalidTaskDataException;
import com.interview.petmarket.domain.model.inventario.EstadoTarea;
import com.interview.petmarket.domain.model.inventario.PrioridadTarea;
import com.interview.petmarket.domain.model.inventario.TareaReposicion;
import com.interview.petmarket.domain.ports.in.GestionarTareasReposicionUseCase;
import com.interview.petmarket.domain.ports.out.TareaReposicionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de aplicación para gestión de tareas de reposición.
 * Implementa los principios SOLID:
 * - SRP: Solo se encarga de la lógica de tareas de reposición
 * - OCP: Extensible para nuevas funcionalidades
 * - LSP: Implementa correctamente la interfaz
 * - ISP: Usa interfaces específicas
 * - DIP: Depende de abstracciones, no de implementaciones
 */
@Transactional
public class TareasReposicionApplicationService implements GestionarTareasReposicionUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(TareasReposicionApplicationService.class);
    
    private final TareaReposicionRepositoryPort tareaRepository;
    
    public TareasReposicionApplicationService(TareaReposicionRepositoryPort tareaRepository) {
        this.tareaRepository = tareaRepository;
    }
    
    @Override
    @Transactional
    public TareaReposicion crearTareaReposicion(Long productoId, String nombreProducto, 
                                               int stockActual, int umbralReposicion, 
                                               int cantidadSugerida) {
        logger.info("Creando tarea de reposición para producto {} - Stock: {}, Umbral: {}", 
                   productoId, stockActual, umbralReposicion);
        
        validarDatosCreacion(productoId, nombreProducto, stockActual, umbralReposicion, cantidadSugerida);
        
        // Verificar si ya existe una tarea activa para este producto
        if (tareaRepository.existsActivaByProductoId(productoId)) {
            logger.warn("Ya existe una tarea activa para el producto {}, no se crea nueva tarea", productoId);
            // Retornar la tarea existente
            return tareaRepository.findActivasByProductoId(productoId).get(0);
        }
        
        // Crear nueva tarea
        TareaReposicion nuevaTarea = TareaReposicion.builder()
                .withProductoId(productoId)
                .withNombreProducto(nombreProducto)
                .withStockActual(stockActual)
                .withUmbralReposicion(umbralReposicion)
                .withCantidadSugerida(cantidadSugerida)
                .withEstado(EstadoTarea.PENDIENTE)
                .withObservaciones("Tarea creada automáticamente por stock bajo")
                .build();
        
        TareaReposicion tareaGuardada = tareaRepository.save(nuevaTarea);
        
        logger.info("Tarea de reposición creada exitosamente - ID: {}, Producto: {}, Prioridad: {}", 
                   tareaGuardada.getId(), productoId, tareaGuardada.getPrioridad());
        
        return tareaGuardada;
    }
    
    @Override
    public TareaReposicion obtenerTarea(Long tareaId) {
        logger.debug("Obteniendo tarea: {}", tareaId);
        
        if (tareaId == null) {
            throw new InvalidTaskDataException("Task ID cannot be null");
        }
        
        return tareaRepository.findById(tareaId)
                .orElseThrow(() -> new InvalidTaskDataException("Task not found: " + tareaId));
    }
    
    @Override
    public List<TareaReposicion> obtenerTareasPendientes() {
        logger.debug("Obteniendo tareas pendientes");
        return tareaRepository.findByEstado(EstadoTarea.PENDIENTE);
    }
    
    @Override
    public List<TareaReposicion> obtenerTareasPorEstado(EstadoTarea estado) {
        logger.debug("Obteniendo tareas por estado: {}", estado);
        
        if (estado == null) {
            throw new InvalidTaskDataException("Task state cannot be null");
        }
        
        return tareaRepository.findByEstado(estado);
    }
    
    @Override
    public List<TareaReposicion> obtenerTareasPorPrioridad(PrioridadTarea prioridad) {
        logger.debug("Obteniendo tareas por prioridad: {}", prioridad);
        
        if (prioridad == null) {
            throw new InvalidTaskDataException("Task priority cannot be null");
        }
        
        return tareaRepository.findByPrioridad(prioridad);
    }
    
    @Override
    public List<TareaReposicion> obtenerTareasVencidas() {
        logger.debug("Obteniendo tareas vencidas");
        return tareaRepository.findByFechaVencimientoBefore(LocalDateTime.now());
    }
    
    @Override
    @Transactional
    public TareaReposicion marcarTareaEnProceso(Long tareaId) {
        logger.info("Marcando tarea {} como en proceso", tareaId);
        
        TareaReposicion tarea = obtenerTarea(tareaId);
        
        if (tarea.getEstado() == EstadoTarea.COMPLETADA) {
            throw new InvalidTaskDataException("Cannot change state of completed task");
        }
        
        TareaReposicion tareaActualizada = tarea.marcarEnProceso();
        tareaActualizada = tareaRepository.save(tareaActualizada);
        
        logger.info("Tarea {} marcada como en proceso exitosamente", tareaId);
        
        return tareaActualizada;
    }
    
    @Override
    @Transactional
    public TareaReposicion completarTarea(Long tareaId, String observacionesFinales) {
        logger.info("Completando tarea {} con observaciones: {}", tareaId, observacionesFinales);
        
        TareaReposicion tarea = obtenerTarea(tareaId);
        
        TareaReposicion tareaCompletada = tarea.marcarCompletada(observacionesFinales);
        tareaCompletada = tareaRepository.save(tareaCompletada);
        
        logger.info("Tarea {} completada exitosamente", tareaId);
        
        return tareaCompletada;
    }
    
    @Override
    public EstadisticasTareas obtenerEstadisticas() {
        logger.debug("Obteniendo estadísticas de tareas");
        
        long totalTareas = tareaRepository.findAll().size();
        long tareasPendientes = tareaRepository.countByEstado(EstadoTarea.PENDIENTE);
        long tareasEnProceso = tareaRepository.countByEstado(EstadoTarea.EN_PROCESO);
        long tareasCompletadas = tareaRepository.countByEstado(EstadoTarea.COMPLETADA);
        long tareasCriticas = tareaRepository.countByPrioridad(PrioridadTarea.CRITICA);
        long tareasVencidas = tareaRepository.countVencidas();
        
        EstadisticasTareas estadisticas = new EstadisticasTareas(
                (int) totalTareas,
                (int) tareasPendientes,
                (int) tareasEnProceso,
                (int) tareasCompletadas,
                (int) tareasCriticas,
                (int) tareasVencidas
        );
        
        logger.debug("Estadísticas obtenidas: {}", estadisticas);
        
        return estadisticas;
    }
    
    private void validarDatosCreacion(Long productoId, String nombreProducto, int stockActual, 
                                     int umbralReposicion, int cantidadSugerida) {
        if (productoId == null) {
            throw new InvalidTaskDataException("Product ID cannot be null");
        }
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) {
            throw new InvalidTaskDataException("Product name cannot be null or empty");
        }
        if (stockActual < 0) {
            throw new InvalidTaskDataException("Current stock cannot be negative");
        }
        if (umbralReposicion < 0) {
            throw new InvalidTaskDataException("Replenishment threshold cannot be negative");
        }
        if (cantidadSugerida <= 0) {
            throw new InvalidTaskDataException("Suggested quantity must be positive");
        }
    }
}
