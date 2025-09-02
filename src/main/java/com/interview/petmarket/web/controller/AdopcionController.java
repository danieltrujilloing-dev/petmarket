package com.interview.petmarket.web.controller;

import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;
import com.interview.petmarket.domain.ports.in.GestionarAdopcionUseCase;
import com.interview.petmarket.web.dto.CreateSolicitudAdopcionRequestDto;
import com.interview.petmarket.web.dto.SolicitudAdopcionResponseDto;
import com.interview.petmarket.web.mapper.AdopcionWebMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de adopciones.
 * Implementa el caso de uso: Solicitudes de adopción orientadas a eventos.
 */
@RestController
@RequestMapping("/api/v1/adopciones")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdopcionController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdopcionController.class);
    
    private final GestionarAdopcionUseCase adopcionService;
    private final AdopcionWebMapper webMapper;
    
    public AdopcionController(GestionarAdopcionUseCase adopcionService, AdopcionWebMapper webMapper) {
        this.adopcionService = adopcionService;
        this.webMapper = webMapper;
    }
    
    /**
     * Crea una nueva solicitud de adopción.
     * POST /api/v1/adopciones/solicitudes
     * 
     * Proceso:
     * 1. Valida datos de entrada (síncrono)
     * 2. Crea solicitud en base de datos (síncrono)
     * 3. Publica evento AdoptionRequested (asíncrono)
     * 4. Worker consume evento y verifica con refugio (asíncrono)
     * 5. Publica AdoptionApproved/Rejected (asíncrono)
     * 6. Notifica al cliente (asíncrono)
     */
    @PostMapping("/solicitudes")
    public ResponseEntity<SolicitudAdopcionResponseDto> crearSolicitudAdopcion(
            @Valid @RequestBody CreateSolicitudAdopcionRequestDto request) {
        
        logger.info("🐾 POST /api/v1/adopciones/solicitudes - Creando solicitud observacionespara cliente: {}",
                   request.getClienteId());
        
        try {
            SolicitudAdopcion solicitud = crearSolicitud(request);
            SolicitudAdopcionResponseDto response = webMapper.toResponseDto(solicitud);
            
            logger.info("✅ Solicitud de adopción creada exitosamente con ID: {} para cliente: {}", 
                       solicitud.getId(), request.getClienteId());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("❌ Error creando solicitud de adopción para cliente: {}", request.getClienteId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private SolicitudAdopcion crearSolicitud(CreateSolicitudAdopcionRequestDto request) {
        return adopcionService.crearSolicitudAdopcion(
                request.getClienteId(),
                request.getNombreSolicitante(),
                request.getEmailSolicitante(),
                request.getTelefonoSolicitante(),
                request.getTipoMascotaDeseada(),
                request.getMotivoAdopcion(),
                request.getExperienciaPrevia(),
                request.getSituacionVivienda()
        );
    }
    
    /**
     * Obtiene una solicitud de adopción por ID.
     * GET /api/v1/adopciones/solicitudes/{id}
     */
    @GetMapping("/solicitudes/{id}")
    public ResponseEntity<SolicitudAdopcionResponseDto> obtenerSolicitudPorId(@PathVariable Long id) {
        
        logger.info("GET /api/v1/adopciones/solicitudes/{} - Getting adoption request", id);
        
        try {
            SolicitudAdopcion solicitud = adopcionService.obtenerSolicitudPorId(id);
            SolicitudAdopcionResponseDto response = webMapper.toResponseDto(solicitud);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting adoption request with ID: {}", id, e);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Lista todas las solicitudes de adopción de un cliente.
     * GET /api/v1/adopciones/solicitudes/cliente/{clienteId}
     */
    @GetMapping("/solicitudes/cliente/{clienteId}")
    public ResponseEntity<List<SolicitudAdopcionResponseDto>> listarSolicitudesPorCliente(
            @PathVariable Long clienteId) {
        
        logger.info("GET /api/v1/adopciones/solicitudes/cliente/{} - Listing adoption requests", clienteId);
        
        try {
            List<SolicitudAdopcion> solicitudes = adopcionService.listarSolicitudesPorCliente(clienteId);
            List<SolicitudAdopcionResponseDto> response = webMapper.toResponseDtoList(solicitudes);
            
            logger.info("Found {} adoption requests for client: {}", response.size(), clienteId);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error listing adoption requests for client: {}", clienteId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Lista todas las solicitudes pendientes de verificación.
     * GET /api/v1/adopciones/solicitudes/pendientes
     */
    @GetMapping("/solicitudes/pendientes")
    public ResponseEntity<List<SolicitudAdopcionResponseDto>> listarSolicitudesPendientes() {
        
        logger.info("GET /api/v1/adopciones/solicitudes/pendientes - Listing pending adoption requests");
        
        try {
            List<SolicitudAdopcion> solicitudes = adopcionService.listarSolicitudesPendientes();
            List<SolicitudAdopcionResponseDto> response = webMapper.toResponseDtoList(solicitudes);
            
            logger.info("Found {} pending adoption requests", response.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error listing pending adoption requests", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Cancela una solicitud de adopción.
     * PUT /api/v1/adopciones/solicitudes/{id}/cancelar
     */
    @PutMapping("/solicitudes/{id}/cancelar")
    public ResponseEntity<Map<String, String>> cancelarSolicitud(@PathVariable Long id) {
        
        logger.info("PUT /api/v1/adopciones/solicitudes/{}/cancelar - Cancelling adoption request", id);
        
        try {
            adopcionService.cancelarSolicitud(id);
            
            logger.info("Adoption request {} cancelled successfully", id);
            return ResponseEntity.ok(Map.of(
                    "message", "Solicitud de adopción cancelada exitosamente",
                    "solicitudId", id.toString()
            ));
            
        } catch (Exception e) {
            logger.error("Error cancelling adoption request with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cancelar la solicitud de adopción"));
        }
    }
    
    /**
     * Completa una adopción (marca como completada).
     * PUT /api/v1/adopciones/solicitudes/{id}/completar
     */
    @PutMapping("/solicitudes/{id}/completar")
    public ResponseEntity<Map<String, String>> completarAdopcion(@PathVariable Long id) {
        
        logger.info("PUT /api/v1/adopciones/solicitudes/{}/completar - Completing adoption", id);
        
        try {
            adopcionService.completarAdopcion(id);
            
            logger.info("Adoption {} completed successfully", id);
            return ResponseEntity.ok(Map.of(
                    "message", "Adopción completada exitosamente",
                    "solicitudId", id.toString()
            ));
            
        } catch (Exception e) {
            logger.error("Error completing adoption with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al completar la adopción"));
        }
    }
    
    /**
     * Endpoint para simular respuesta manual del refugio (solo para testing).
     * PUT /api/v1/adopciones/solicitudes/{id}/procesar-respuesta
     */
    @PutMapping("/solicitudes/{id}/procesar-respuesta")
    public ResponseEntity<Map<String, String>> procesarRespuestaRefugio(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request) {
        
        logger.info("PUT /api/v1/adopciones/solicitudes/{}/procesar-respuesta - Processing shelter response", id);
        
        try {
            boolean aprobada = (Boolean) request.get("aprobada");
            String observaciones = (String) request.get("observaciones");
            
            adopcionService.procesarRespuestaRefugio(id, aprobada, observaciones);
            
            String mensaje = aprobada ? "Solicitud aprobada por el refugio" : "Solicitud rechazada por el refugio";
            logger.info("Shelter response processed for adoption request {}: {}", id, mensaje);
            
            return ResponseEntity.ok(Map.of(
                    "message", mensaje,
                    "solicitudId", id.toString(),
                    "aprobada", String.valueOf(aprobada)
            ));
            
        } catch (Exception e) {
            logger.error("Error processing shelter response for adoption request with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al procesar la respuesta del refugio"));
        }
    }
}
