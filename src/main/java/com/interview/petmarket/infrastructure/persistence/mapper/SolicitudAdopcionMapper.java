package com.interview.petmarket.infrastructure.persistence.mapper;

import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;
import com.interview.petmarket.infrastructure.persistence.entity.SolicitudAdopcionEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre entidades de dominio y entidades JPA
 */
@Component
public class SolicitudAdopcionMapper {
    
    /**
     * Convierte de entidad JPA a entidad de dominio
     */
    public SolicitudAdopcion toDomain(SolicitudAdopcionEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return SolicitudAdopcion.builder()
                .id(entity.getId())
                .clienteId(entity.getClienteId())
                .nombreSolicitante(entity.getNombreSolicitante())
                .emailSolicitante(entity.getEmailSolicitante())
                .telefonoSolicitante(entity.getTelefonoSolicitante())
                .tipoMascotaDeseada(entity.getTipoMascotaDeseada())
                .motivoAdopcion(entity.getMotivoAdopcion())
                .experienciaPrevia(entity.getExperienciaPrevia())
                .situacionVivienda(entity.getSituacionVivienda())
                .estado(entity.getEstado())
                .observacionesRefugio(entity.getObservacionesRefugio())
                .motivoRechazo(entity.getMotivoRechazo())
                .fechaVerificacion(entity.getFechaVerificacion())
                .refugioAsignado(entity.getRefugioAsignado())
                .build();
    }
    
    /**
     * Convierte de entidad de dominio a entidad JPA
     */
    public SolicitudAdopcionEntity toEntity(SolicitudAdopcion domain) {
        if (domain == null) {
            return null;
        }
        
        SolicitudAdopcionEntity entity = new SolicitudAdopcionEntity();
        entity.setId(domain.getId());
        entity.setClienteId(domain.getClienteId());
        entity.setNombreSolicitante(domain.getNombreSolicitante());
        entity.setEmailSolicitante(domain.getEmailSolicitante());
        entity.setTelefonoSolicitante(domain.getTelefonoSolicitante());
        entity.setTipoMascotaDeseada(domain.getTipoMascotaDeseada());
        entity.setMotivoAdopcion(domain.getMotivoAdopcion());
        entity.setExperienciaPrevia(domain.getExperienciaPrevia());
        entity.setSituacionVivienda(domain.getSituacionVivienda());
        entity.setEstado(domain.getEstado());
        entity.setObservacionesRefugio(domain.getObservacionesRefugio());
        entity.setMotivoRechazo(domain.getMotivoRechazo());
        entity.setFechaVerificacion(domain.getFechaVerificacion());
        entity.setRefugioAsignado(domain.getRefugioAsignado());
        
        return entity;
    }
}
