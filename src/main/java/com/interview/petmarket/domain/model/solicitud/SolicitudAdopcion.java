package com.interview.petmarket.domain.model.solicitud;

import com.interview.petmarket.domain.model.common.BaseEntity;
import com.interview.petmarket.domain.exceptions.InvalidAdoptionRequestDataException;

/**
 * Entidad SolicitudAdopción del dominio
 * Representa una solicitud de adopción (id, clienteId, mascotaIdExterna, estado)
 */
public class SolicitudAdopcion extends BaseEntity {
    
    private final Long clienteId;
    private final String mascotaIdExterna;
    private EstadoSolicitud estado;
    private String comentarios;
    
    // Constructor privado para usar el builder
    private SolicitudAdopcion(Builder builder) {
        super(builder.id);
        this.clienteId = builder.clienteId;
        this.mascotaIdExterna = builder.mascotaIdExterna;
        this.estado = builder.estado != null ? builder.estado : EstadoSolicitud.RECIBIDA;
        this.comentarios = builder.comentarios;
        
        validateSolicitud();
    }
    
    private void validateSolicitud() {
        if (clienteId == null) {
            throw new InvalidAdoptionRequestDataException("Client ID cannot be null");
        }
        if (mascotaIdExterna == null || mascotaIdExterna.trim().isEmpty()) {
            throw new InvalidAdoptionRequestDataException("External pet ID cannot be null or empty");
        }
    }
    
    // Métodos de negocio
    public void marcarEnRevision() {
        cambiarEstado(EstadoSolicitud.EN_REVISION);
    }
    
    public void aprobar() {
        cambiarEstado(EstadoSolicitud.APROBADA);
    }
    
    public void rechazar() {
        cambiarEstado(EstadoSolicitud.RECHAZADA);
    }
    
    private void cambiarEstado(EstadoSolicitud nuevoEstado) {
        if (!this.estado.puedeTransicionarA(nuevoEstado)) {
            throw new InvalidAdoptionRequestDataException(
                String.format("Cannot transition from %s to %s", this.estado, nuevoEstado)
            );
        }
        this.estado = nuevoEstado;
        updateTimestamp();
    }
    
    public void actualizarComentarios(String nuevosComentarios) {
        this.comentarios = nuevosComentarios;
        updateTimestamp();
    }
    
    public boolean estaTerminada() {
        return estado.esTerminal();
    }
    
    public boolean estaPendiente() {
        return estado.esPendiente();
    }
    
    public boolean fueAprobada() {
        return estado == EstadoSolicitud.APROBADA;
    }
    
    public boolean fueRechazada() {
        return estado == EstadoSolicitud.RECHAZADA;
    }
    
    // Getters
    public Long getClienteId() {
        return clienteId;
    }
    
    public String getMascotaIdExterna() {
        return mascotaIdExterna;
    }
    
    public EstadoSolicitud getEstado() {
        return estado;
    }
    
    public String getComentarios() {
        return comentarios;
    }
    
    // Builder Pattern
    public static class Builder {
        private Long id;
        private Long clienteId;
        private String mascotaIdExterna;
        private EstadoSolicitud estado;
        private String comentarios;
        
        public Builder withId(Long id) {
            this.id = id;
            return this;
        }
        
        public Builder withClienteId(Long clienteId) {
            this.clienteId = clienteId;
            return this;
        }
        
        public Builder withMascotaIdExterna(String mascotaIdExterna) {
            this.mascotaIdExterna = mascotaIdExterna;
            return this;
        }
        
        public Builder withEstado(EstadoSolicitud estado) {
            this.estado = estado;
            return this;
        }
        
        public Builder withComentarios(String comentarios) {
            this.comentarios = comentarios;
            return this;
        }
        
        public SolicitudAdopcion build() {
            return new SolicitudAdopcion(this);
        }
    }
    
    public static Builder builder() {
        return new Builder();
    }
}
