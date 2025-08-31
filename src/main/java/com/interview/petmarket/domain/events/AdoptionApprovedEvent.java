package com.interview.petmarket.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Evento de dominio que se publica cuando una solicitud de adopción es aprobada por el refugio.
 */
public class AdoptionApprovedEvent extends DomainEvent {
    
    private final Long solicitudId;
    private final Long clienteId;
    private final String nombreSolicitante;
    private final String emailSolicitante;
    private final String refugioAsignado;
    private final String observacionesRefugio;
    
    @JsonCreator
    public AdoptionApprovedEvent(@JsonProperty("solicitudId") Long solicitudId,
                                @JsonProperty("clienteId") Long clienteId,
                                @JsonProperty("nombreSolicitante") String nombreSolicitante,
                                @JsonProperty("emailSolicitante") String emailSolicitante,
                                @JsonProperty("refugioAsignado") String refugioAsignado,
                                @JsonProperty("observacionesRefugio") String observacionesRefugio) {
        super();
        this.solicitudId = solicitudId;
        this.clienteId = clienteId;
        this.nombreSolicitante = nombreSolicitante;
        this.emailSolicitante = emailSolicitante;
        this.refugioAsignado = refugioAsignado;
        this.observacionesRefugio = observacionesRefugio;
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
    
    public String getObservacionesRefugio() {
        return observacionesRefugio;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AdoptionApprovedEvent that = (AdoptionApprovedEvent) o;
        
        if (!solicitudId.equals(that.solicitudId)) return false;
        if (!clienteId.equals(that.clienteId)) return false;
        if (!nombreSolicitante.equals(that.nombreSolicitante)) return false;
        if (!emailSolicitante.equals(that.emailSolicitante)) return false;
        if (!refugioAsignado.equals(that.refugioAsignado)) return false;
        return observacionesRefugio.equals(that.observacionesRefugio);
    }
    
    @Override
    public int hashCode() {
        int result = solicitudId.hashCode();
        result = 31 * result + clienteId.hashCode();
        result = 31 * result + nombreSolicitante.hashCode();
        result = 31 * result + emailSolicitante.hashCode();
        result = 31 * result + refugioAsignado.hashCode();
        result = 31 * result + observacionesRefugio.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "AdoptionApprovedEvent{" +
                "solicitudId=" + solicitudId +
                ", clienteId=" + clienteId +
                ", nombreSolicitante='" + nombreSolicitante + '\'' +
                ", emailSolicitante='" + emailSolicitante + '\'' +
                ", refugioAsignado='" + refugioAsignado + '\'' +
                ", eventId='" + getEventId() + '\'' +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
