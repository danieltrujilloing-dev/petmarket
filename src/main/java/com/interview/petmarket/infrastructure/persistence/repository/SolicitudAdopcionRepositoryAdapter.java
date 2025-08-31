package com.interview.petmarket.infrastructure.persistence.repository;

import com.interview.petmarket.domain.model.adopcion.EstadoSolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;
import com.interview.petmarket.domain.ports.out.SolicitudAdopcionRepositoryPort;
import com.interview.petmarket.infrastructure.persistence.entity.SolicitudAdopcionEntity;
import com.interview.petmarket.infrastructure.persistence.jpa.JpaSolicitudAdopcionRepository;
import com.interview.petmarket.infrastructure.persistence.mapper.SolicitudAdopcionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de repositorio para SolicitudAdopcion.
 * Implementa el puerto de salida usando JPA.
 */
@Repository
@Transactional
public class SolicitudAdopcionRepositoryAdapter implements SolicitudAdopcionRepositoryPort {
    
    private static final Logger logger = LoggerFactory.getLogger(SolicitudAdopcionRepositoryAdapter.class);
    
    private final JpaSolicitudAdopcionRepository jpaRepository;
    private final SolicitudAdopcionMapper mapper;
    
    public SolicitudAdopcionRepositoryAdapter(JpaSolicitudAdopcionRepository jpaRepository,
                                            SolicitudAdopcionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public SolicitudAdopcion save(SolicitudAdopcion solicitud) {
        logger.debug("Guardando solicitud de adopción para cliente: {}", solicitud.getClienteId());
        
        SolicitudAdopcionEntity entity = mapper.toEntity(solicitud);
        SolicitudAdopcionEntity savedEntity = jpaRepository.save(entity);
        
        logger.debug("Solicitud de adopción guardada con ID: {}", savedEntity.getId());
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SolicitudAdopcion> findById(Long id) {
        logger.debug("Buscando solicitud de adopción por ID: {}", id);
        
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudAdopcion> findByClienteId(Long clienteId) {
        logger.debug("Buscando solicitudes de adopción por cliente ID: {}", clienteId);
        
        return jpaRepository.findByClienteId(clienteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudAdopcion> findByEstado(EstadoSolicitudAdopcion estado) {
        logger.debug("Buscando solicitudes de adopción por estado: {}", estado);
        
        return jpaRepository.findByEstado(estado).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudAdopcion> findPendientes() {
        logger.debug("Buscando solicitudes de adopción pendientes");
        
        return jpaRepository.findPendientes().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existeSolicitudActivaPorCliente(Long clienteId) {
        logger.debug("Verificando si existe solicitud activa para cliente: {}", clienteId);
        
        return jpaRepository.existeSolicitudActivaPorCliente(clienteId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudAdopcion> findAll() {
        logger.debug("Buscando todas las solicitudes de adopción");
        
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
