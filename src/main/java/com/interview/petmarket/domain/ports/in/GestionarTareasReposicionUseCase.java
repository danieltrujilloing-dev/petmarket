package com.interview.petmarket.domain.ports.in;

import com.interview.petmarket.domain.model.inventario.EstadoTarea;
import com.interview.petmarket.domain.model.inventario.PrioridadTarea;
import com.interview.petmarket.domain.model.inventario.TareaReposicion;

import java.util.List;

/**
 * Puerto de entrada para gestión de tareas de reposición.
 * Define los casos de uso relacionados con tareas automáticas de reposición.
 * 
 * Implementa el principio de Segregación de Interfaces (ISP) de SOLID.
 */
public interface GestionarTareasReposicionUseCase {
    
    /**
     * Crea una nueva tarea de reposición automáticamente.
     * Llamado por el consumidor de eventos LowStock.
     * 
     * @param productoId ID del producto
     * @param nombreProducto Nombre del producto
     * @param stockActual Stock actual del producto
     * @param umbralReposicion Umbral de reposición
     * @param cantidadSugerida Cantidad sugerida para reponer
     * @return Tarea de reposición creada
     */
    TareaReposicion crearTareaReposicion(Long productoId, String nombreProducto, 
                                        int stockActual, int umbralReposicion, 
                                        int cantidadSugerida);
    
    /**
     * Obtiene una tarea de reposición por ID.
     * 
     * @param tareaId ID de la tarea
     * @return Tarea de reposición
     */
    TareaReposicion obtenerTarea(Long tareaId);
    
    /**
     * Obtiene todas las tareas pendientes.
     * 
     * @return Lista de tareas pendientes
     */
    List<TareaReposicion> obtenerTareasPendientes();
    
    /**
     * Obtiene tareas por estado.
     * 
     * @param estado Estado de las tareas
     * @return Lista de tareas con el estado especificado
     */
    List<TareaReposicion> obtenerTareasPorEstado(EstadoTarea estado);
    
    /**
     * Obtiene tareas por prioridad.
     * 
     * @param prioridad Prioridad de las tareas
     * @return Lista de tareas con la prioridad especificada
     */
    List<TareaReposicion> obtenerTareasPorPrioridad(PrioridadTarea prioridad);
    
    /**
     * Obtiene tareas vencidas.
     * 
     * @return Lista de tareas vencidas
     */
    List<TareaReposicion> obtenerTareasVencidas();
    
    /**
     * Marca una tarea como en proceso.
     * 
     * @param tareaId ID de la tarea
     * @return Tarea actualizada
     */
    TareaReposicion marcarTareaEnProceso(Long tareaId);
    
    /**
     * Marca una tarea como completada.
     * 
     * @param tareaId ID de la tarea
     * @param observacionesFinales Observaciones finales
     * @return Tarea actualizada
     */
    TareaReposicion completarTarea(Long tareaId, String observacionesFinales);
    
    /**
     * Obtiene estadísticas de tareas.
     * 
     * @return Estadísticas de tareas de reposición
     */
    EstadisticasTareas obtenerEstadisticas();
    
    /**
     * Clase para estadísticas de tareas.
     */
    record EstadisticasTareas(
            int totalTareas,
            int tareasPendientes,
            int tareasEnProceso,
            int tareasCompletadas,
            int tareasCriticas,
            int tareasVencidas
    ) {}
}
