package com.interview.petmarket.domain.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.interview.petmarket.domain.model.adopcion.TipoMascota;

/**
 * Evento de dominio que se publica cuando se crea una solicitud de adopción.
 * Implementa el principio de Responsabilidad Única (SRP) de SOLID.
 */
public class AdoptionRequestedEvent extends DomainEvent {
    
    private final Long solicitudId;
    private final Long clienteId;
    private final String nombreSolicitante;
    private final String emailSolicitante;
    private final String telefonoSolicitante;
    private final TipoMascota tipoMascotaDeseada;
    private final String motivoAdopcion;
    private final String experienciaPrevia;
    private final String situacionVivienda;
    
    @JsonCreator
    public AdoptionRequestedEvent(@JsonProperty("solicitudId") Long solicitudId,
                                 @JsonProperty("clienteId") Long clienteId,
                                 @JsonProperty("nombreSolicitante") String nombreSolicitante,
                                 @JsonProperty("emailSolicitante") String emailSolicitante,
                                 @JsonProperty("telefonoSolicitante") String telefonoSolicitante,
                                 @JsonProperty("tipoMascotaDeseada") TipoMascota tipoMascotaDeseada,
                                 @JsonProperty("motivoAdopcion") String motivoAdopcion,
                                 @JsonProperty("experienciaPrevia") String experienciaPrevia,
                                 @JsonProperty("situacionVivienda") String situacionVivienda) {
        super();
        this.solicitudId = solicitudId;
        this.clienteId = clienteId;
        this.nombreSolicitante = nombreSolicitante;
        this.emailSolicitante = emailSolicitante;
        this.telefonoSolicitante = telefonoSolicitante;
        this.tipoMascotaDeseada = tipoMascotaDeseada;
        this.motivoAdopcion = motivoAdopcion;
        this.experienciaPrevia = experienciaPrevia;
        this.situacionVivienda = situacionVivienda;
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
    
    public String getTelefonoSolicitante() {
        return telefonoSolicitante;
    }
    
    public TipoMascota getTipoMascotaDeseada() {
        return tipoMascotaDeseada;
    }
    
    public String getMotivoAdopcion() {
        return motivoAdopcion;
    }
    
    public String getExperienciaPrevia() {
        return experienciaPrevia;
    }
    
    public String getSituacionVivienda() {
        return situacionVivienda;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AdoptionRequestedEvent that = (AdoptionRequestedEvent) o;
        
        if (!solicitudId.equals(that.solicitudId)) return false;
        if (!clienteId.equals(that.clienteId)) return false;
        if (!nombreSolicitante.equals(that.nombreSolicitante)) return false;
        if (!emailSolicitante.equals(that.emailSolicitante)) return false;
        if (!telefonoSolicitante.equals(that.telefonoSolicitante)) return false;
        if (tipoMascotaDeseada != that.tipoMascotaDeseada) return false;
        if (!motivoAdopcion.equals(that.motivoAdopcion)) return false;
        if (!experienciaPrevia.equals(that.experienciaPrevia)) return false;
        return situacionVivienda.equals(that.situacionVivienda);
    }
    
    @Override
    public int hashCode() {
        int result = solicitudId.hashCode();
        result = 31 * result + clienteId.hashCode();
        result = 31 * result + nombreSolicitante.hashCode();
        result = 31 * result + emailSolicitante.hashCode();
        result = 31 * result + telefonoSolicitante.hashCode();
        result = 31 * result + tipoMascotaDeseada.hashCode();
        result = 31 * result + motivoAdopcion.hashCode();
        result = 31 * result + experienciaPrevia.hashCode();
        result = 31 * result + situacionVivienda.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "AdoptionRequestedEvent{" +
                "solicitudId=" + solicitudId +
                ", clienteId=" + clienteId +
                ", nombreSolicitante='" + nombreSolicitante + '\'' +
                ", emailSolicitante='" + emailSolicitante + '\'' +
                ", tipoMascotaDeseada=" + tipoMascotaDeseada +
                ", eventId='" + getEventId() + '\'' +
                ", occurredOn=" + getOccurredOn() +
                '}';
    }
}
