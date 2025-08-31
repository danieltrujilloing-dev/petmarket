package com.interview.petmarket.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Evento de dominio que se publica cuando una solicitud de adopción es rechazada por el refugio.
 */
public class AdoptionRejectedEvent extends DomainEvent {
    
    private final Long solicitudId;
    private final Long clienteId;
    private final String nombreSolicitante;
    private final String emailSolicitante;
    private final String refugioAsignado;
    private final String motivoRechazo;
    
    @JsonCreator
    public AdoptionRejectedEvent(@JsonProperty("solicitudId") Long solicitudId,
                                @JsonProperty("clienteId") Long clienteId,
                                @JsonProperty("nombreSolicitante") String nombreSolicitante,
                                @JsonProperty("emailSolicitante") String emailSolicitante,
                                @JsonProperty("refugioAsignado") String refugioAsignado,
                                @JsonProperty("motivoRechazo") String motivoRechazo) {
        super();
        this.solicitudId = solicitudId;
        this.clienteId = clienteId;
        this.nombreSolicitante = nombreSolicitante;
        this.emailSolicitante = emailSolicitante;
        this.refugioAsignado = refugioAsignado;
        this.motivoRechazo = motivoRechazo;
    }
    
    // Getters
    public Long getSolicitudId() {
        return solicitudId;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public String getNombreSolicitante() {
        return nombreSolicitante;
    }
    
    public String getEmailSolicitante() {
        return emailSolicitante;
    }
    
    public String getRefugioAsignado() {
        return refugioAsignado;
    }
    
    public String getMotivoRechazo() {
        return motivoRechazo;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AdoptionRejectedEvent that = (AdoptionRejectedEvent) o;
        
        if (!solicitudId.equals(that.solicitudId)) return false;
        if (!clienteId.equals(that.clienteId)) return false;
        if (!nombreSolicitante.equals(that.nombreSolicitante)) return false;
        if (!emailSolicitante.equals(that.emailSolicitante)) return false;
        if (!refugioAsignado.equals(that.refugioAsignado)) return false;
        return motivoRechazo.equals(that.motivoRechazo);
    }
    
    @Override
    public int hashCode() {
        int result = solicitudId.hashCode();
        result = 31 * result + clienteId.hashCode();
        result = 31 * result + nombreSolicitante.hashCode();
        result = 31 * result + emailSolicitante.hashCode();
        result = 31 * result + refugioAsignado.hashCode();
        result = 31 * result + motivoRechazo.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "AdoptionRejectedEvent{" +
                "solicitudId=" + solicitudId +
                ", clienteId=" + clienteId +
                ", nombreSolicitante='" + nombreSolicitante + '\'' +
                ", emailSolicitante='" + emailSolicitante + '\'' +
                ", refugioAsignado='" + refugioAsignado + '\'' +
                ", motivoRechazo='" + motivoRechazo + '\'' +
                ", eventId='" + getEventId() + '\'' +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
