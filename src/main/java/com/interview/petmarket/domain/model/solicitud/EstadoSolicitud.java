package com.interview.petmarket.domain.model.solicitud;

/**
 * Enum para los estados posibles de una solicitud de adopción
 */
public enum EstadoSolicitud {
    RECIBIDA("Solicitud recibida, pendiente de revisión"),
    EN_REVISION("Solicitud en proceso de revisión"),
    APROBADA("Solicitud aprobada, adopción autorizada"),
    RECHAZADA("Solicitud rechazada");
    
    private final String descripcion;
    
    EstadoSolicitud(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public boolean esPendiente() {
        return this == RECIBIDA || this == EN_REVISION;
    }
    
    public boolean esTerminal() {
        return this == APROBADA || this == RECHAZADA;
    }
    
    public boolean puedeTransicionarA(EstadoSolicitud nuevoEstado) {
        switch (this) {
            case RECIBIDA:
                return nuevoEstado == EN_REVISION || nuevoEstado == RECHAZADA;
            case EN_REVISION:
                return nuevoEstado == APROBADA || nuevoEstado == RECHAZADA;
            case APROBADA:
            case RECHAZADA:
                return false; // Estados terminales
            default:
                return false;
        }
    }
}
