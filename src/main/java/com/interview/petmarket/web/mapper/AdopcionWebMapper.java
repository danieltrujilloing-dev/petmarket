package com.interview.petmarket.web.mapper;

import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;
import com.interview.petmarket.web.dto.CreateSolicitudAdopcionRequestDto;
import com.interview.petmarket.web.dto.SolicitudAdopcionResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre DTOs web y entidades de dominio
 */
@Component
public class AdopcionWebMapper {
    
    /**
     * Convierte de DTO de request a parámetros para el servicio
     */
    public SolicitudAdopcion fromCreateRequest(CreateSolicitudAdopcionRequestDto request) {
        if (request == null) {
            return null;
        }
        
        return SolicitudAdopcion.builder()
                .clienteId(request.getClienteId())
                .nombreSolicitante(request.getNombreSolicitante())
                .emailSolicitante(request.getEmailSolicitante())
                .telefonoSolicitante(request.getTelefonoSolicitante())
                .tipoMascotaDeseada(request.getTipoMascotaDeseada())
                .motivoAdopcion(request.getMotivoAdopcion())
                .experienciaPrevia(request.getExperienciaPrevia())
                .situacionVivienda(request.getSituacionVivienda())
                .build();
    }
    
    /**
     * Convierte de entidad de dominio a DTO de respuesta
     */
    public SolicitudAdopcionResponseDto toResponseDto(SolicitudAdopcion solicitud) {
        if (solicitud == null) {
            return null;
        }
        
        SolicitudAdopcionResponseDto dto = new SolicitudAdopcionResponseDto();
        dto.setId(solicitud.getId());
        dto.setClienteId(solicitud.getClienteId());
        dto.setNombreSolicitante(solicitud.getNombreSolicitante());
        dto.setEmailSolicitante(solicitud.getEmailSolicitante());
        dto.setTelefonoSolicitante(solicitud.getTelefonoSolicitante());
        dto.setTipoMascotaDeseada(solicitud.getTipoMascotaDeseada());
        dto.setMotivoAdopcion(solicitud.getMotivoAdopcion());
        dto.setExperienciaPrevia(solicitud.getExperienciaPrevia());
        dto.setSituacionVivienda(solicitud.getSituacionVivienda());
        dto.setEstado(solicitud.getEstado());
        dto.setObservacionesRefugio(solicitud.getObservacionesRefugio());
        dto.setMotivoRechazo(solicitud.getMotivoRechazo());
        dto.setFechaVerificacion(solicitud.getFechaVerificacion());
        dto.setRefugioAsignado(solicitud.getRefugioAsignado());
        dto.setFechaCreacion(solicitud.getCreatedAt());
        
        return dto;
    }
    
    /**
     * Convierte una lista de entidades de dominio a DTOs de respuesta
     */
    public List<SolicitudAdopcionResponseDto> toResponseDtoList(List<SolicitudAdopcion> solicitudes) {
        if (solicitudes == null) {
            return null;
        }
        
        return solicitudes.stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }
}
