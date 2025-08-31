package com.interview.petmarket.web.dto;

import com.interview.petmarket.domain.model.adopcion.TipoMascota;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear una nueva solicitud de adopción
 */
public class CreateSolicitudAdopcionRequestDto {
    
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
    
    @NotBlank(message = "El nombre del solicitante es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombreSolicitante;
    
    @NotBlank(message = "El email del solicitante es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String emailSolicitante;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefonoSolicitante;
    
    @NotNull(message = "El tipo de mascota deseada es obligatorio")
    private TipoMascota tipoMascotaDeseada;
    
    @NotBlank(message = "El motivo de adopción es obligatorio")
    @Size(max = 1000, message = "El motivo de adopción no puede exceder 1000 caracteres")
    private String motivoAdopcion;
    
    @Size(max = 1000, message = "La experiencia previa no puede exceder 1000 caracteres")
    private String experienciaPrevia;
    
    @Size(max = 1000, message = "La situación de vivienda no puede exceder 1000 caracteres")
    private String situacionVivienda;
    
    // Constructor por defecto
    public CreateSolicitudAdopcionRequestDto() {}
    
    // Getters y Setters
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
}
