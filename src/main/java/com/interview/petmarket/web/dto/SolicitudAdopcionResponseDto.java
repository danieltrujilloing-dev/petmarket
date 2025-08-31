package com.interview.petmarket.web.dto;

import com.interview.petmarket.domain.model.adopcion.EstadoSolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.TipoMascota;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de solicitud de adopción
 */
public class SolicitudAdopcionResponseDto {
    
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
    private String estadoDescripcion;
    private String observacionesRefugio;
    private String motivoRechazo;
    private LocalDateTime fechaVerificacion;
    private String refugioAsignado;
    private LocalDateTime fechaCreacion;
    
    // Constructor por defecto
    public SolicitudAdopcionResponseDto() {}
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getNombreSolicitante() {
        return nombreSolicitante;
    }
    
    public void setNombreSolicitante(String nombreSolicitante) {
        this.nombreSolicitante = nombreSolicitante;
    }
    
    public String getEmailSolicitante() {
        return emailSolicitante;
    }
    
    public void setEmailSolicitante(String emailSolicitante) {
        this.emailSolicitante = emailSolicitante;
    }
    
    public String getTelefonoSolicitante() {
        return telefonoSolicitante;
    }
    
    public void setTelefonoSolicitante(String telefonoSolicitante) {
        this.telefonoSolicitante = telefonoSolicitante;
    }
    
    public TipoMascota getTipoMascotaDeseada() {
        return tipoMascotaDeseada;
    }
    
    public void setTipoMascotaDeseada(TipoMascota tipoMascotaDeseada) {
        this.tipoMascotaDeseada = tipoMascotaDeseada;
    }
    
    public String getMotivoAdopcion() {
        return motivoAdopcion;
    }
    
    public void setMotivoAdopcion(String motivoAdopcion) {
        this.motivoAdopcion = motivoAdopcion;
    }
    
    public String getExperienciaPrevia() {
        return experienciaPrevia;
    }
    
    public void setExperienciaPrevia(String experienciaPrevia) {
        this.experienciaPrevia = experienciaPrevia;
    }
    
    public String getSituacionVivienda() {
        return situacionVivienda;
    }
    
    public void setSituacionVivienda(String situacionVivienda) {
        this.situacionVivienda = situacionVivienda;
    }
    
    public EstadoSolicitudAdopcion getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoSolicitudAdopcion estado) {
        this.estado = estado;
        this.estadoDescripcion = estado != null ? estado.getDescripcion() : null;
    }
    
    public String getEstadoDescripcion() {
        return estadoDescripcion;
    }
    
    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = estadoDescripcion;
    }
    
    public String getObservacionesRefugio() {
        return observacionesRefugio;
    }
    
    public void setObservacionesRefugio(String observacionesRefugio) {
        this.observacionesRefugio = observacionesRefugio;
    }
    
    public String getMotivoRechazo() {
        return motivoRechazo;
    }
    
    public void setMotivoRechazo(String motivoRechazo) {
        this.motivoRechazo = motivoRechazo;
    }
    
    public LocalDateTime getFechaVerificacion() {
        return fechaVerificacion;
    }
    
    public void setFechaVerificacion(LocalDateTime fechaVerificacion) {
        this.fechaVerificacion = fechaVerificacion;
    }
    
    public String getRefugioAsignado() {
        return refugioAsignado;
    }
    
    public void setRefugioAsignado(String refugioAsignado) {
        this.refugioAsignado = refugioAsignado;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
