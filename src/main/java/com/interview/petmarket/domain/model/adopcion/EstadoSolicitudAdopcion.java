package com.interview.petmarket.domain.model.adopcion;

/**
 * Enum para los estados posibles de una solicitud de adopción
 */
public enum EstadoSolicitudAdopcion {
    PENDIENTE("Solicitud pendiente de verificación"),
    EN_VERIFICACION("Solicitud en proceso de verificación por el refugio"),
    APROBADA("Solicitud aprobada por el refugio"),
    RECHAZADA("Solicitud rechazada por el refugio"),
    COMPLETADA("Adopción completada exitosamente"),
    CANCELADA("Solicitud cancelada por el solicitante");
    
    private final String descripcion;
    
    EstadoSolicitudAdopcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public boolean esPendiente() {
        return this == PENDIENTE || this == EN_VERIFICACION;
    }
    
    public boolean esTerminal() {
        return this == COMPLETADA || this == CANCELADA || this == RECHAZADA;
    }
    
    public boolean puedeTransicionarA(EstadoSolicitudAdopcion nuevoEstado) {
        switch (this) {
            case PENDIENTE:
                return nuevoEstado == EN_VERIFICACION || nuevoEstado == CANCELADA;
            case EN_VERIFICACION:
                return nuevoEstado == APROBADA || nuevoEstado == RECHAZADA || nuevoEstado == CANCELADA;
            case APROBADA:
                return nuevoEstado == COMPLETADA || nuevoEstado == CANCELADA;
            case RECHAZADA:
            case COMPLETADA:
            case CANCELADA:
                return false; // Estados terminales
            default:
                return false;
        }
    }
}
