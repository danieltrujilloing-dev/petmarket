package com.interview.petmarket.domain.ports.in;

import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.TipoMascota;

import java.util.List;

/**
 * Puerto de entrada para casos de uso de adopción.
 * Define las operaciones disponibles para gestionar solicitudes de adopción.
 */
public interface GestionarAdopcionUseCase {
    
    /**
     * Crea una nueva solicitud de adopción
     */
    SolicitudAdopcion crearSolicitudAdopcion(Long clienteId, String nombreSolicitante, 
                                           String emailSolicitante, String telefonoSolicitante,
                                           TipoMascota tipoMascotaDeseada, String motivoAdopcion,
                                           String experienciaPrevia, String situacionVivienda);
    
    /**
     * Obtiene una solicitud de adopción por ID
     */
    SolicitudAdopcion obtenerSolicitudPorId(Long solicitudId);
    
    /**
     * Lista todas las solicitudes de adopción de un cliente
     */
    List<SolicitudAdopcion> listarSolicitudesPorCliente(Long clienteId);
    
    /**
     * Lista todas las solicitudes pendientes de verificación
     */
    List<SolicitudAdopcion> listarSolicitudesPendientes();
    
    /**
     * Procesa la respuesta del refugio (aprobación o rechazo)
     */
    void procesarRespuestaRefugio(Long solicitudId, boolean aprobada, String observaciones);
    
    /**
     * Cancela una solicitud de adopción
     */
    void cancelarSolicitud(Long solicitudId);
    
    /**
     * Completa una adopción (marca como completada)
     */
    void completarAdopcion(Long solicitudId);
}
