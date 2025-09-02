package com.interview.petmarket.infrastructure.persistence.repository;

import com.interview.petmarket.domain.model.carrito.Carrito;
import com.interview.petmarket.domain.ports.out.CarritoRepositoryPort;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.CarritoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.mapper.CarritoMapper;
import com.interview.petmarket.infrastructure.persistence.jpa.repository.JpaCarritoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.Optional;

/**
 * Adaptador de repositorio para Carrito.
 * Implementa el puerto de salida usando JPA.
 */
@Repository
@Transactional
public class CarritoRepositoryAdapter implements CarritoRepositoryPort {
    
    private static final Logger logger = LoggerFactory.getLogger(CarritoRepositoryAdapter.class);
    
    private final JpaCarritoRepository jpaRepository;
    private final CarritoMapper mapper;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public CarritoRepositoryAdapter(JpaCarritoRepository jpaRepository, CarritoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Carrito> findByClienteId(Long clienteId) {
        logger.debug("Buscando carrito por cliente ID: {}", clienteId);
        
        return jpaRepository.findByClienteIdWithItems(clienteId)
                .map(mapper::toDomain);
    }
    
    @Override
    public Carrito save(Carrito carrito) {
        logger.debug("Guardando carrito para cliente: {}", carrito.getClienteId());
        
        if (carrito.getId() != null) {
            // Actualización de carrito existente
            logger.debug("Actualizando carrito existente con ID: {}", carrito.getId());
            return updateExistingCarrito(carrito);
        } else {
            // Nuevo carrito
            logger.debug("Creando nuevo carrito para cliente: {}", carrito.getClienteId());
            CarritoEntity entity = mapper.toEntity(carrito);
            CarritoEntity savedEntity = jpaRepository.save(entity);
            logger.debug("Nuevo carrito creado con ID: {}", savedEntity.getId());
            return mapper.toDomain(savedEntity);
        }
    }
    
    private Carrito updateExistingCarrito(Carrito carrito) {
        // Buscar el carrito existente en la BD
        CarritoEntity existingEntity = jpaRepository.findById(carrito.getId())
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado: " + carrito.getId()));
        
        // Limpiar items existentes completamente
        existingEntity.getItems().clear();
        entityManager.flush(); // Forzar sincronización con BD
        
        // Agregar los items actualizados
        for (com.interview.petmarket.domain.model.carrito.CarritoItem item : carrito.getItems()) {
            com.interview.petmarket.infrastructure.persistence.jpa.entity.CarritoItemEntity itemEntity = 
                com.interview.petmarket.infrastructure.persistence.jpa.entity.CarritoItemEntity.builder()
                    .carrito(existingEntity)
                    .productoId(item.getProductoId())
                    .cantidad(item.getCantidad())
                    .precioUnitario(java.math.BigDecimal.ZERO)
                    .build();
            existingEntity.getItems().add(itemEntity);
        }
        
        // Actualizar timestamps
        existingEntity.setUpdatedAt(carrito.getUpdatedAt());
        
        // Guardar
        CarritoEntity savedEntity = jpaRepository.save(existingEntity);
        logger.debug("Carrito actualizado con ID: {}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteByClienteId(Long clienteId) {
        logger.debug("Eliminando carrito del cliente: {}", clienteId);
        
        jpaRepository.deleteByClienteId(clienteId);
        
        logger.debug("Carrito eliminado para cliente: {}", clienteId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByClienteId(Long clienteId) {
        logger.debug("Verificando existencia de carrito para cliente: {}", clienteId);
        
        return jpaRepository.existsByClienteId(clienteId);
    }
}
