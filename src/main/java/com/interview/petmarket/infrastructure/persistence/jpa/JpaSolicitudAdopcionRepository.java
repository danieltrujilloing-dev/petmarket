package com.interview.petmarket.infrastructure.persistence.jpa;

import com.interview.petmarket.domain.model.adopcion.EstadoSolicitudAdopcion;
import com.interview.petmarket.infrastructure.persistence.entity.SolicitudAdopcionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para SolicitudAdopcion
 */
@Repository
public interface JpaSolicitudAdopcionRepository extends JpaRepository<SolicitudAdopcionEntity, Long> {
    
    /**
     * Busca solicitudes por cliente ID
     */
    List<SolicitudAdopcionEntity> findByClienteId(Long clienteId);
    
    /**
     * Busca solicitudes por estado
     */
    List<SolicitudAdopcionEntity> findByEstado(EstadoSolicitudAdopcion estado);
    
    /**
     * Busca solicitudes pendientes (PENDIENTE y EN_VERIFICACION)
     */
    @Query("SELECT s FROM SolicitudAdopcionEntity s WHERE s.estado IN ('PENDIENTE', 'EN_VERIFICACION') ORDER BY s.fechaCreacion ASC")
    List<SolicitudAdopcionEntity> findPendientes();
    
    /**
     * Verifica si existe una solicitud activa para un cliente
     * (estados no terminales: PENDIENTE, EN_VERIFICACION, APROBADA)
     */
    @Query("SELECT COUNT(s) > 0 FROM SolicitudAdopcionEntity s WHERE s.clienteId = :clienteId AND s.estado IN ('PENDIENTE', 'EN_VERIFICACION', 'APROBADA')")
    boolean existeSolicitudActivaPorCliente(@Param("clienteId") Long clienteId);
    
    /**
     * Busca solicitudes por tipo de mascota deseada
     */
    @Query("SELECT s FROM SolicitudAdopcionEntity s WHERE s.tipoMascotaDeseada = :tipoMascota ORDER BY s.fechaCreacion DESC")
    List<SolicitudAdopcionEntity> findByTipoMascotaDeseada(@Param("tipoMascota") String tipoMascota);
    
    /**
     * Busca solicitudes por refugio asignado
     */
    List<SolicitudAdopcionEntity> findByRefugioAsignado(String refugioAsignado);
}
