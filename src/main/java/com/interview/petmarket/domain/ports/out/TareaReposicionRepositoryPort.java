package com.interview.petmarket.domain.ports.out;

import com.interview.petmarket.domain.model.inventario.EstadoTarea;
import com.interview.petmarket.domain.model.inventario.PrioridadTarea;
import com.interview.petmarket.domain.model.inventario.TareaReposicion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de tareas de reposición.
 * Implementa el principio de Inversión de Dependencias (DIP) de SOLID.
 */
public interface TareaReposicionRepositoryPort {
    
    /**
     * Busca una tarea por ID.
     */
    Optional<TareaReposicion> findById(Long id);
    
    /**
     * Guarda una tarea de reposición.
     */
    TareaReposicion save(TareaReposicion tarea);
    
    /**
     * Busca tareas por producto ID.
     */
    List<TareaReposicion> findByProductoId(Long productoId);
    
    /**
     * Busca tareas por estado.
     */
    List<TareaReposicion> findByEstado(EstadoTarea estado);
    
    /**
     * Busca tareas por prioridad.
     */
    List<TareaReposicion> findByPrioridad(PrioridadTarea prioridad);
    
    /**
     * Busca tareas vencidas.
     */
    List<TareaReposicion> findByFechaVencimientoBefore(LocalDateTime fecha);
    
    /**
     * Busca tareas activas para un producto (evitar duplicados).
     */
    List<TareaReposicion> findActivasByProductoId(Long productoId);
    
    /**
     * Cuenta tareas por estado.
     */
    long countByEstado(EstadoTarea estado);
    
    /**
     * Cuenta tareas por prioridad.
     */
    long countByPrioridad(PrioridadTarea prioridad);
    
    /**
     * Cuenta tareas vencidas.
     */
    long countVencidas();
    
    /**
     * Busca todas las tareas.
     */
    List<TareaReposicion> findAll();
    
    /**
     * Verifica si existe una tarea activa para un producto.
     */
    boolean existsActivaByProductoId(Long productoId);
}
