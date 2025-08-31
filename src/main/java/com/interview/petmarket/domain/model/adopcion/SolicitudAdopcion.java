package com.interview.petmarket.domain.model.adopcion;

import com.interview.petmarket.domain.model.common.BaseEntity;
import com.interview.petmarket.domain.exceptions.InvalidAdoptionDataException;

import java.time.LocalDateTime;

/**
 * Entidad SolicitudAdopcion del dominio
 * Representa una solicitud de adopción de mascota
 */
public class SolicitudAdopcion extends BaseEntity {
    
    private final Long clienteId;
    private final String nombreSolicitante;
    private final String emailSolicitante;
    private final String telefonoSolicitante;
    private final TipoMascota tipoMascotaDeseada;
    private final String motivoAdopcion;
    private final String experienciaPrevia;
    private final String situacionVivienda;
    private EstadoSolicitudAdopcion estado;
    private String observacionesRefugio;
    private String motivoRechazo;
    private LocalDateTime fechaVerificacion;
    private String refugioAsignado;
    
    // Constructor privado para usar el builder
    private SolicitudAdopcion(Builder builder) {
        super(builder.id);
        this.clienteId = builder.clienteId;
        this.nombreSolicitante = builder.nombreSolicitante;
        this.emailSolicitante = builder.emailSolicitante;
        this.telefonoSolicitante = builder.telefonoSolicitante;
        this.tipoMascotaDeseada = builder.tipoMascotaDeseada;
        this.motivoAdopcion = builder.motivoAdopcion;
        this.experienciaPrevia = builder.experienciaPrevia;
        this.situacionVivienda = builder.situacionVivienda;
        this.estado = builder.estado != null ? builder.estado : EstadoSolicitudAdopcion.PENDIENTE;
        this.observacionesRefugio = builder.observacionesRefugio;
        this.motivoRechazo = builder.motivoRechazo;
        this.fechaVerificacion = builder.fechaVerificacion;
        this.refugioAsignado = builder.refugioAsignado;
        
        validateSolicitud();
    }
    
    private void validateSolicitud() {
        if (clienteId == null) {
            throw new InvalidAdoptionDataException("Client ID cannot be null");
        }
        if (nombreSolicitante == null || nombreSolicitante.trim().isEmpty()) {
            throw new InvalidAdoptionDataException("Applicant name cannot be null or empty");
        }
        if (emailSolicitante == null || emailSolicitante.trim().isEmpty()) {
            throw new InvalidAdoptionDataException("Applicant email cannot be null or empty");
        }
        if (tipoMascotaDeseada == null) {
            throw new InvalidAdoptionDataException("Desired pet type cannot be null");
        }
        if (motivoAdopcion == null || motivoAdopcion.trim().isEmpty()) {
            throw new InvalidAdoptionDataException("Adoption reason cannot be null or empty");
        }
    }
    
    // Métodos de negocio
    public void iniciarVerificacion(String refugioAsignado) {
        if (!estado.puedeTransicionarA(EstadoSolicitudAdopcion.EN_VERIFICACION)) {
            throw new InvalidAdoptionDataException("Cannot start verification from current state: " + estado);
        }
        this.estado = EstadoSolicitudAdopcion.EN_VERIFICACION;
        this.refugioAsignado = refugioAsignado;
        this.fechaVerificacion = LocalDateTime.now();
    }
    
    public void aprobar(String observaciones) {
        if (!estado.puedeTransicionarA(EstadoSolicitudAdopcion.APROBADA)) {
            throw new InvalidAdoptionDataException("Cannot approve from current state: " + estado);
        }
        this.estado = EstadoSolicitudAdopcion.APROBADA;
        this.observacionesRefugio = observaciones;
    }
    
    public void rechazar(String motivoRechazo) {
        if (!estado.puedeTransicionarA(EstadoSolicitudAdopcion.RECHAZADA)) {
            throw new InvalidAdoptionDataException("Cannot reject from current state: " + estado);
        }
        this.estado = EstadoSolicitudAdopcion.RECHAZADA;
        this.motivoRechazo = motivoRechazo;
    }
    
    public void completar() {
        if (!estado.puedeTransicionarA(EstadoSolicitudAdopcion.COMPLETADA)) {
            throw new InvalidAdoptionDataException("Cannot complete from current state: " + estado);
        }
        this.estado = EstadoSolicitudAdopcion.COMPLETADA;
    }
    
    public void cancelar() {
        if (estado.esTerminal()) {
            throw new InvalidAdoptionDataException("Cannot cancel from terminal state: " + estado);
        }
        this.estado = EstadoSolicitudAdopcion.CANCELADA;
    }
    
    // Getters
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
    
    public EstadoSolicitudAdopcion getEstado() {
        return estado;
    }
    
    public String getObservacionesRefugio() {
        return observacionesRefugio;
    }
    
    public String getMotivoRechazo() {
        return motivoRechazo;
    }
    
    public LocalDateTime getFechaVerificacion() {
        return fechaVerificacion;
    }
    
    public String getRefugioAsignado() {
        return refugioAsignado;
    }
    
    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private Long id;
        private Long clienteId;
        private String nombreSolicitante;
        private String emailSolicitante;
        private String telefonoSolicitante;
        private TipoMascota tipoMascotaDeseada;
        private String motivoAdopcion;
        private String experienciaPrevia;
        private String situacionVivienda;
        private EstadoSolicitudAdopcion estado;
        private String observacionesRefugio;
        private String motivoRechazo;
        private LocalDateTime fechaVerificacion;
        private String refugioAsignado;
        
        public Builder id(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder clienteId(Long clienteId) {
            this.clienteId = clienteId;
            return this;
        }
        
        public Builder nombreSolicitante(String nombreSolicitante) {
            this.nombreSolicitante = nombreSolicitante;
            return this;
        }
        
        public Builder emailSolicitante(String emailSolicitante) {
            this.emailSolicitante = emailSolicitante;
            return this;
        }
        
        public Builder telefonoSolicitante(String telefonoSolicitante) {
            this.telefonoSolicitante = telefonoSolicitante;
            return this;
        }
        
        public Builder tipoMascotaDeseada(TipoMascota tipoMascotaDeseada) {
            this.tipoMascotaDeseada = tipoMascotaDeseada;
            return this;
        }
        
        public Builder motivoAdopcion(String motivoAdopcion) {
            this.motivoAdopcion = motivoAdopcion;
            return this;
        }
        
        public Builder experienciaPrevia(String experienciaPrevia) {
            this.experienciaPrevia = experienciaPrevia;
            return this;
        }
        
        public Builder situacionVivienda(String situacionVivienda) {
            this.situacionVivienda = situacionVivienda;
            return this;
        }
        
        public Builder estado(EstadoSolicitudAdopcion estado) {
            this.estado = estado;
            return this;
        }
        
        public Builder observacionesRefugio(String observacionesRefugio) {
            this.observacionesRefugio = observacionesRefugio;
            return this;
        }
        
        public Builder motivoRechazo(String motivoRechazo) {
            this.motivoRechazo = motivoRechazo;
            return this;
        }
        
        public Builder fechaVerificacion(LocalDateTime fechaVerificacion) {
            this.fechaVerificacion = fechaVerificacion;
            return this;
        }
        
        public Builder refugioAsignado(String refugioAsignado) {
            this.refugioAsignado = refugioAsignado;
            return this;
        }
        
        public SolicitudAdopcion build() {
            return new SolicitudAdopcion(this);
        }
    }
}
