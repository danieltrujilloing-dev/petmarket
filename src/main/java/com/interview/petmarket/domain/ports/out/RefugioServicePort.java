package com.interview.petmarket.domain.ports.out;

import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;

/**
 * Puerto de salida para comunicación con servicios externos de refugios.
 * Define las operaciones para verificación de solicitudes de adopción.
 */
public interface RefugioServicePort {
    
    /**
     * Envía una solicitud de adopción al refugio para verificación
     * @param solicitud La solicitud a verificar
     * @return El refugio asignado para la verificación
     */
    String enviarSolicitudParaVerificacion(SolicitudAdopcion solicitud);
    
    /**
     * Verifica el estado de una solicitud en el refugio
     * @param solicitudId ID de la solicitud
     * @return Resultado de la verificación
     */
    VerificationResult verificarEstadoSolicitud(Long solicitudId);
    
    /**
     * Clase para encapsular el resultado de la verificación
     */
    class VerificationResult {
        private final boolean aprobada;
        private final String observaciones;
        private final String refugioAsignado;
        
        public VerificationResult(boolean aprobada, String observaciones, String refugioAsignado) {
            this.aprobada = aprobada;
            this.observaciones = observaciones;
            this.refugioAsignado = refugioAsignado;
        }
        
        public boolean isAprobada() {
            return aprobada;
        }
        
        public String getObservaciones() {
            return observaciones;
        }
        
        public String getRefugioAsignado() {
            return refugioAsignado;
        }
    }
}
