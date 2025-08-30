package com.interview.petmarket.infrastructure.persistence.repository;

import com.interview.petmarket.domain.model.inventario.Inventario;
import com.interview.petmarket.domain.ports.out.InventarioRepositoryPort;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.InventarioEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.mapper.InventarioMapper;
import com.interview.petmarket.infrastructure.persistence.jpa.repository.JpaInventarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de repositorio para Inventario.
 * Implementa el puerto de salida usando JPA.
 */
@Repository
@Transactional
public class InventarioRepositoryAdapter implements InventarioRepositoryPort {
    
    private static final Logger logger = LoggerFactory.getLogger(InventarioRepositoryAdapter.class);
    
    private final JpaInventarioRepository jpaRepository;
    private final InventarioMapper mapper;
    
    public InventarioRepositoryAdapter(JpaInventarioRepository jpaRepository, InventarioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Inventario> findByProductoId(Long productoId) {
        logger.debug("Buscando inventario por producto ID: {}", productoId);
        
        return jpaRepository.findByProductoId(productoId)
                .map(mapper::toDomain);
    }
    
    @Override
    public Inventario save(Inventario inventario) {
        logger.debug("Guardando inventario para producto: {}", inventario.getProductoId());
        
        InventarioEntity entity = mapper.toEntity(inventario);
        InventarioEntity savedEntity = jpaRepository.save(entity);
        
        logger.debug("Inventario guardado con ID: {}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Inventario> findByStockBelowThreshold() {
        logger.debug("Buscando inventarios con stock bajo el umbral");
        
        return jpaRepository.findByStockBelowThreshold().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean hasStock(Long productoId, int cantidadRequerida) {
        logger.debug("Verificando stock para producto {}: {} unidades", productoId, cantidadRequerida);
        
        return jpaRepository.hasStock(productoId, cantidadRequerida);
    }
    
    @Override
    public boolean reservarStock(Long productoId, int cantidad) {
        logger.debug("Reservando stock para producto {}: {} unidades", productoId, cantidad);
        
        int filasAfectadas = jpaRepository.reservarStock(productoId, cantidad);
        boolean reservado = filasAfectadas > 0;
        
        if (reservado) {
            logger.debug("Stock reservado exitosamente para producto: {}", productoId);
        } else {
            logger.warn("No se pudo reservar stock para producto: {}", productoId);
        }
        
        return reservado;
    }
    
    @Override
    public void liberarStock(Long productoId, int cantidad) {
        logger.debug("Liberando stock para producto {}: {} unidades", productoId, cantidad);
        
        int filasAfectadas = jpaRepository.liberarStock(productoId, cantidad);
        
        if (filasAfectadas > 0) {
            logger.debug("Stock liberado exitosamente para producto: {}", productoId);
        } else {
            logger.warn("No se encontró inventario para liberar stock del producto: {}", productoId);
        }
    }
}
