package com.interview.petmarket.infrastructure.persistence.entity;

import com.interview.petmarket.domain.model.adopcion.EstadoSolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.TipoMascota;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA para SolicitudAdopcion
 */
@Entity
@Table(name = "solicitudes_adopcion")
public class SolicitudAdopcionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;
    
    @Column(name = "nombre_solicitante", nullable = false, length = 100)
    private String nombreSolicitante;
    
    @Column(name = "email_solicitante", nullable = false, length = 100)
    private String emailSolicitante;
    
    @Column(name = "telefono_solicitante", length = 20)
    private String telefonoSolicitante;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_mascota_deseada", nullable = false)
    private TipoMascota tipoMascotaDeseada;
    
    @Column(name = "motivo_adopcion", nullable = false, columnDefinition = "TEXT")
    private String motivoAdopcion;
    
    @Column(name = "experiencia_previa", columnDefinition = "TEXT")
    private String experienciaPrevia;
    
    @Column(name = "situacion_vivienda", columnDefinition = "TEXT")
    private String situacionVivienda;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoSolicitudAdopcion estado;
    
    @Column(name = "observaciones_refugio", columnDefinition = "TEXT")
    private String observacionesRefugio;
    
    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;
    
    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;
    
    @Column(name = "refugio_asignado", length = 100)
    private String refugioAsignado;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Constructor por defecto
    public SolicitudAdopcionEntity() {
        this.fechaCreacion = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
    
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
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
