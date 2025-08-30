package com.interview.petmarket.infrastructure.persistence.repository;

import com.interview.petmarket.domain.model.pedido.EstadoPedido;
import com.interview.petmarket.domain.model.pedido.Pedido;
import com.interview.petmarket.domain.ports.out.PedidoRepositoryPort;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.EstadoPedidoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.entity.PedidoEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.mapper.PedidoMapper;
import com.interview.petmarket.infrastructure.persistence.jpa.repository.JpaPedidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de repositorio para Pedido.
 * Implementa el puerto de salida usando JPA.
 */
@Repository
@Transactional
public class PedidoRepositoryAdapter implements PedidoRepositoryPort {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidoRepositoryAdapter.class);
    
    private final JpaPedidoRepository jpaRepository;
    private final PedidoMapper mapper;
    
    public PedidoRepositoryAdapter(JpaPedidoRepository jpaRepository, PedidoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> findById(Long id) {
        logger.debug("Buscando pedido por ID: {}", id);
        
        return jpaRepository.findByIdWithItems(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Pedido save(Pedido pedido) {
        logger.debug("Guardando pedido para cliente: {}", pedido.getClienteId());
        
        PedidoEntity entity = mapper.toEntity(pedido);
        PedidoEntity savedEntity = jpaRepository.save(entity);
        
        logger.debug("Pedido guardado con ID: {}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findByClienteId(Long clienteId) {
        logger.debug("Buscando pedidos por cliente ID: {}", clienteId);
        
        return jpaRepository.findByClienteId(clienteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findByEstado(EstadoPedido estado) {
        logger.debug("Buscando pedidos por estado: {}", estado);
        
        EstadoPedidoEntity estadoEntity = EstadoPedidoEntity.valueOf(estado.name());
        return jpaRepository.findByEstado(estadoEntity).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Pedido> findByClienteIdAndEstado(Long clienteId, EstadoPedido estado) {
        logger.debug("Buscando pedidos por cliente {} y estado: {}", clienteId, estado);
        
        EstadoPedidoEntity estadoEntity = EstadoPedidoEntity.valueOf(estado.name());
        return jpaRepository.findByClienteIdAndEstado(clienteId, estadoEntity).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
