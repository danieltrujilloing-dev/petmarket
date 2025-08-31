package com.interview.petmarket.domain.ports.out;

import com.interview.petmarket.domain.model.adopcion.EstadoSolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para el repositorio de solicitudes de adopción.
 * Define las operaciones de persistencia necesarias.
 */
public interface SolicitudAdopcionRepositoryPort {
    
    /**
     * Guarda una solicitud de adopción
     */
    SolicitudAdopcion save(SolicitudAdopcion solicitud);
    
    /**
     * Busca una solicitud por ID
     */
    Optional<SolicitudAdopcion> findById(Long id);
    
    /**
     * Lista todas las solicitudes de un cliente
     */
    List<SolicitudAdopcion> findByClienteId(Long clienteId);
    
    /**
     * Lista solicitudes por estado
     */
    List<SolicitudAdopcion> findByEstado(EstadoSolicitudAdopcion estado);
    
    /**
     * Lista todas las solicitudes pendientes (PENDIENTE y EN_VERIFICACION)
     */
    List<SolicitudAdopcion> findPendientes();
    
    /**
     * Verifica si existe una solicitud activa para un cliente
     */
    boolean existeSolicitudActivaPorCliente(Long clienteId);
    
    /**
     * Lista todas las solicitudes
     */
    List<SolicitudAdopcion> findAll();
}
