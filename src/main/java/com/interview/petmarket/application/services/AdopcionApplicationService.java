package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.events.AdoptionApprovedEvent;
import com.interview.petmarket.domain.events.AdoptionRejectedEvent;
import com.interview.petmarket.domain.events.AdoptionRequestedEvent;
import com.interview.petmarket.domain.exceptions.InvalidAdoptionDataException;
import com.interview.petmarket.domain.model.adopcion.EstadoSolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.TipoMascota;
import com.interview.petmarket.domain.ports.in.GestionarAdopcionUseCase;
import com.interview.petmarket.domain.ports.out.EventPublisherPort;
import com.interview.petmarket.domain.ports.out.SolicitudAdopcionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de aplicación para gestionar adopciones.
 * Implementa los principios SOLID:
 * - SRP: Solo se encarga de la lógica de adopciones
 * - OCP: Extensible para nuevas funcionalidades
 * - LSP: Implementa correctamente la interfaz
 * - ISP: Usa interfaces específicas
 * - DIP: Depende de abstracciones, no de implementaciones
 */
@Service
@Transactional
public class AdopcionApplicationService implements GestionarAdopcionUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(AdopcionApplicationService.class);
    
    private final SolicitudAdopcionRepositoryPort solicitudRepository;
    private final EventPublisherPort eventPublisher;
    
    public AdopcionApplicationService(SolicitudAdopcionRepositoryPort solicitudRepository,
                                    EventPublisherPort eventPublisher) {
        this.solicitudRepository = solicitudRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public SolicitudAdopcion crearSolicitudAdopcion(Long clienteId, String nombreSolicitante,
                                                   String emailSolicitante, String telefonoSolicitante,
                                                   TipoMascota tipoMascotaDeseada, String motivoAdopcion,
                                                   String experienciaPrevia, String situacionVivienda) {
        
        logger.info("🐾 Creando solicitud de adopción para cliente: {}", clienteId);
        
        // Validar reglas de negocio
        validarSolicitudActiva(clienteId);
        
        // Crear la solicitud usando el builder pattern
        SolicitudAdopcion solicitud = construirSolicitudAdopcion(
                clienteId, nombreSolicitante, emailSolicitante, telefonoSolicitante,
                tipoMascotaDeseada, motivoAdopcion, experienciaPrevia, situacionVivienda
        );
        
        // Persistir la solicitud
        SolicitudAdopcion solicitudGuardada = solicitudRepository.save(solicitud);
        
        // Publicar evento de dominio
        publicarEventoSolicitudCreada(solicitudGuardada);
        
        logger.info("✅ Solicitud de adopción creada con ID: {} para cliente: {}", 
                   solicitudGuardada.getId(), clienteId);
        
        return solicitudGuardada;
    }
    
    private void validarSolicitudActiva(Long clienteId) {
        if (solicitudRepository.existeSolicitudActivaPorCliente(clienteId)) {
            throw new InvalidAdoptionDataException("El cliente ya tiene una solicitud de adopción activa");
        }
    }
    
    private SolicitudAdopcion construirSolicitudAdopcion(Long clienteId, String nombreSolicitante,
                                                        String emailSolicitante, String telefonoSolicitante,
                                                        TipoMascota tipoMascotaDeseada, String motivoAdopcion,
                                                        String experienciaPrevia, String situacionVivienda) {
        return SolicitudAdopcion.builder()
                .clienteId(clienteId)
                .nombreSolicitante(nombreSolicitante)
                .emailSolicitante(emailSolicitante)
                .telefonoSolicitante(telefonoSolicitante)
                .tipoMascotaDeseada(tipoMascotaDeseada)
                .motivoAdopcion(motivoAdopcion)
                .experienciaPrevia(experienciaPrevia)
                .situacionVivienda(situacionVivienda)
                .build();
    }
    
    private void publicarEventoSolicitudCreada(SolicitudAdopcion solicitud) {
        AdoptionRequestedEvent event = new AdoptionRequestedEvent(
                solicitud.getId(),
                solicitud.getClienteId(),
                solicitud.getNombreSolicitante(),
                solicitud.getEmailSolicitante(),
                solicitud.getTelefonoSolicitante(),
                solicitud.getTipoMascotaDeseada(),
                solicitud.getMotivoAdopcion(),
                solicitud.getExperienciaPrevia(),
                solicitud.getSituacionVivienda()
        );
        
        eventPublisher.publishEvent(event);
        logger.debug("📢 Evento AdoptionRequested publicado para solicitud: {}", solicitud.getId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public SolicitudAdopcion obtenerSolicitudPorId(Long solicitudId) {
        logger.debug("Obteniendo solicitud de adopción por ID: {}", solicitudId);
        
        return solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new InvalidAdoptionDataException("Solicitud de adopción no encontrada: " + solicitudId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudAdopcion> listarSolicitudesPorCliente(Long clienteId) {
        logger.debug("Listando solicitudes de adopción para cliente: {}", clienteId);
        
        return solicitudRepository.findByClienteId(clienteId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SolicitudAdopcion> listarSolicitudesPendientes() {
        logger.debug("Listando solicitudes de adopción pendientes");
        
        return solicitudRepository.findPendientes();
    }
    
    @Override
    public void procesarRespuestaRefugio(Long solicitudId, boolean aprobada, String observaciones) {
        logger.info("🏥 Procesando respuesta del refugio para solicitud: {} - Resultado: {}", 
                   solicitudId, aprobada ? "APROBADA" : "RECHAZADA");
        
        SolicitudAdopcion solicitud = obtenerSolicitudPorId(solicitudId);
        
        // Procesar según el resultado
        if (aprobada) {
            procesarAprobacion(solicitud, observaciones);
        } else {
            procesarRechazo(solicitud, observaciones);
        }
        
        // Persistir cambios
        solicitudRepository.save(solicitud);
        
        logger.info("✅ Respuesta del refugio procesada para solicitud: {}", solicitudId);
    }
    
    private void procesarAprobacion(SolicitudAdopcion solicitud, String observaciones) {
        solicitud.aprobar(observaciones);
        
        AdoptionApprovedEvent event = new AdoptionApprovedEvent(
                solicitud.getId(),
                solicitud.getClienteId(),
                solicitud.getNombreSolicitante(),
                solicitud.getEmailSolicitante(),
                solicitud.getRefugioAsignado(),
                observaciones
        );
        
        eventPublisher.publishEvent(event);
        logger.debug("📢 Evento AdoptionApproved publicado para solicitud: {}", solicitud.getId());
    }
    
    private void procesarRechazo(SolicitudAdopcion solicitud, String observaciones) {
        solicitud.rechazar(observaciones);
        
        AdoptionRejectedEvent event = new AdoptionRejectedEvent(
                solicitud.getId(),
                solicitud.getClienteId(),
                solicitud.getNombreSolicitante(),
                solicitud.getEmailSolicitante(),
                solicitud.getRefugioAsignado(),
                observaciones
        );
        
        eventPublisher.publishEvent(event);
        logger.debug("📢 Evento AdoptionRejected publicado para solicitud: {}", solicitud.getId());
    }
    
    @Override
    public void cancelarSolicitud(Long solicitudId) {
        logger.info("Cancelando solicitud de adopción: {}", solicitudId);
        
        SolicitudAdopcion solicitud = obtenerSolicitudPorId(solicitudId);
        solicitud.cancelar();
        
        solicitudRepository.save(solicitud);
        
        logger.info("Solicitud de adopción cancelada: {}", solicitudId);
    }
    
    @Override
    public void completarAdopcion(Long solicitudId) {
        logger.info("Completando adopción: {}", solicitudId);
        
        SolicitudAdopcion solicitud = obtenerSolicitudPorId(solicitudId);
        solicitud.completar();
        
        solicitudRepository.save(solicitud);
        
        logger.info("Adopción completada: {}", solicitudId);
    }
}
