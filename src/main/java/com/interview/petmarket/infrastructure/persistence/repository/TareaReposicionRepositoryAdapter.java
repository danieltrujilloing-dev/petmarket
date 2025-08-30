package com.interview.petmarket.infrastructure.persistence.repository;

import com.interview.petmarket.domain.model.inventario.EstadoTarea;
import com.interview.petmarket.domain.model.inventario.PrioridadTarea;
import com.interview.petmarket.domain.model.inventario.TareaReposicion;
import com.interview.petmarket.domain.ports.out.TareaReposicionRepositoryPort;
import com.interview.petmarket.infrastructure.persistence.jpa.mapper.TareaReposicionMapper;
import com.interview.petmarket.infrastructure.persistence.jpa.repository.JpaTareaReposicionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador del repositorio de tareas de reposición.
 * Implementa el puerto de salida usando JPA.
 * 
 * Implementa el principio de Inversión de Dependencias (DIP) de SOLID.
 */
@Repository
public class TareaReposicionRepositoryAdapter implements TareaReposicionRepositoryPort {
    
    private static final Logger logger = LoggerFactory.getLogger(TareaReposicionRepositoryAdapter.class);
    
    private final JpaTareaReposicionRepository jpaRepository;
    private final TareaReposicionMapper mapper;
    
    public TareaReposicionRepositoryAdapter(JpaTareaReposicionRepository jpaRepository,
                                           TareaReposicionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public Optional<TareaReposicion> findById(Long id) {
        logger.debug("Buscando tarea por ID: {}", id);
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public TareaReposicion save(TareaReposicion tarea) {
        logger.debug("Guardando tarea: {}", tarea.getId());
        
        var entity = mapper.toEntity(tarea);
        var savedEntity = jpaRepository.save(entity);
        var savedDomain = mapper.toDomain(savedEntity);
        
        logger.debug("Tarea guardada con ID: {}", savedDomain.getId());
        return savedDomain;
    }
    
    @Override
    public List<TareaReposicion> findByProductoId(Long productoId) {
        logger.debug("Buscando tareas por producto ID: {}", productoId);
        return jpaRepository.findByProductoId(productoId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TareaReposicion> findByEstado(EstadoTarea estado) {
        logger.debug("Buscando tareas por estado: {}", estado);
        var entityEstado = mapper.toEntityEstado(estado);
        return jpaRepository.findByEstado(entityEstado)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TareaReposicion> findByPrioridad(PrioridadTarea prioridad) {
        logger.debug("Buscando tareas por prioridad: {}", prioridad);
        var entityPrioridad = mapper.toEntityPrioridad(prioridad);
        return jpaRepository.findByPrioridad(entityPrioridad)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TareaReposicion> findByFechaVencimientoBefore(LocalDateTime fecha) {
        logger.debug("Buscando tareas vencidas antes de: {}", fecha);
        return jpaRepository.findByFechaVencimientoBefore(fecha)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<TareaReposicion> findActivasByProductoId(Long productoId) {
        logger.debug("Buscando tareas activas por producto ID: {}", productoId);
        return jpaRepository.findActivasByProductoId(productoId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public long countByEstado(EstadoTarea estado) {
        logger.debug("Contando tareas por estado: {}", estado);
        var entityEstado = mapper.toEntityEstado(estado);
        return jpaRepository.countByEstado(entityEstado);
    }
    
    @Override
    public long countByPrioridad(PrioridadTarea prioridad) {
        logger.debug("Contando tareas por prioridad: {}", prioridad);
        var entityPrioridad = mapper.toEntityPrioridad(prioridad);
        return jpaRepository.countByPrioridad(entityPrioridad);
    }
    
    @Override
    public long countVencidas() {
        logger.debug("Contando tareas vencidas");
        return jpaRepository.countVencidas(LocalDateTime.now());
    }
    
    @Override
    public List<TareaReposicion> findAll() {
        logger.debug("Buscando todas las tareas");
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsActivaByProductoId(Long productoId) {
        logger.debug("Verificando si existe tarea activa para producto: {}", productoId);
        boolean exists = jpaRepository.existsActivaByProductoId(productoId);
        logger.debug("Existe tarea activa para producto {}: {}", productoId, exists);
        return exists;
    }
}
