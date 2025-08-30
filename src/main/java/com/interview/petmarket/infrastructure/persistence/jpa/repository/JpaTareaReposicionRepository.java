package com.interview.petmarket.infrastructure.persistence.jpa.repository;

import com.interview.petmarket.infrastructure.persistence.jpa.entity.EstadoTareaEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.PrioridadTareaEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.TareaReposicionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio JPA para TareaReposicionEntity.
 */
@Repository
public interface JpaTareaReposicionRepository extends JpaRepository<TareaReposicionEntity, Long> {
    
    /**
     * Busca tareas por producto ID.
     */
    List<TareaReposicionEntity> findByProductoId(Long productoId);
    
    /**
     * Busca tareas por estado.
     */
    List<TareaReposicionEntity> findByEstado(EstadoTareaEntity estado);
    
    /**
     * Busca tareas por prioridad.
     */
    List<TareaReposicionEntity> findByPrioridad(PrioridadTareaEntity prioridad);
    
    /**
     * Busca tareas vencidas.
     */
    List<TareaReposicionEntity> findByFechaVencimientoBefore(LocalDateTime fecha);
    
    /**
     * Busca tareas activas para un producto.
     */
    @Query("SELECT t FROM TareaReposicionEntity t WHERE t.productoId = :productoId AND t.estado IN ('PENDIENTE', 'EN_PROCESO')")
    List<TareaReposicionEntity> findActivasByProductoId(@Param("productoId") Long productoId);
    
    /**
     * Cuenta tareas por estado.
     */
    long countByEstado(EstadoTareaEntity estado);
    
    /**
     * Cuenta tareas por prioridad.
     */
    long countByPrioridad(PrioridadTareaEntity prioridad);
    
    /**
     * Cuenta tareas vencidas.
     */
    @Query("SELECT COUNT(t) FROM TareaReposicionEntity t WHERE t.fechaVencimiento < :fecha AND t.estado != 'COMPLETADA'")
    long countVencidas(@Param("fecha") LocalDateTime fecha);
    
    /**
     * Verifica si existe una tarea activa para un producto.
     */
    @Query("SELECT COUNT(t) > 0 FROM TareaReposicionEntity t WHERE t.productoId = :productoId AND t.estado IN ('PENDIENTE', 'EN_PROCESO')")
    boolean existsActivaByProductoId(@Param("productoId") Long productoId);
    
    /**
     * Busca tareas por producto y estado.
     */
    List<TareaReposicionEntity> findByProductoIdAndEstado(Long productoId, EstadoTareaEntity estado);
    
    /**
     * Busca tareas ordenadas por prioridad y fecha de vencimiento.
     */
    @Query("SELECT t FROM TareaReposicionEntity t WHERE t.estado = 'PENDIENTE' ORDER BY t.prioridad ASC, t.fechaVencimiento ASC")
    List<TareaReposicionEntity> findPendientesOrderByPrioridadAndFechaVencimiento();
}
