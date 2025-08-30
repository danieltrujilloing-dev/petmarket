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
        
        CarritoEntity entity = mapper.toEntity(carrito);
        CarritoEntity savedEntity = jpaRepository.save(entity);
        
        logger.debug("Carrito guardado con ID: {}", savedEntity.getId());
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
