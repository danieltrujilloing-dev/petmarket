package com.interview.petmarket.infrastructure.external;

import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.TipoMascota;
import com.interview.petmarket.domain.ports.out.RefugioServicePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Adaptador simulado para comunicación con servicios externos de refugios.
 * Simula las respuestas de un servicio real de refugio.
 */
@Component
public class RefugioServiceAdapter implements RefugioServicePort {
    
    private static final Logger logger = LoggerFactory.getLogger(RefugioServiceAdapter.class);
    
    private static final List<String> REFUGIOS_DISPONIBLES = Arrays.asList(
            "Refugio Esperanza Animal",
            "Fundación Patitas Felices",
            "Hogar de Mascotas San Francisco",
            "Refugio Municipal de Animales",
            "Asociación Protectora de Animales"
    );
    
    private static final List<String> OBSERVACIONES_APROBACION = Arrays.asList(
            "Excelente perfil para adopción. Experiencia previa comprobada.",
            "Situación de vivienda adecuada. Referencias verificadas positivamente.",
            "Motivación genuina para el cuidado de mascotas. Aprobado.",
            "Condiciones ideales para el bienestar del animal.",
            "Perfil responsable con experiencia demostrada en cuidado animal."
    );
    
    private static final List<String> MOTIVOS_RECHAZO = Arrays.asList(
            "Falta de experiencia previa en cuidado de mascotas.",
            "Situación de vivienda no adecuada para el tipo de mascota solicitada.",
            "Motivación insuficiente o poco clara para la adopción.",
            "No cumple con los requisitos mínimos del refugio.",
            "Referencias no satisfactorias o incompletas."
    );
    
    private final Random random = new Random();
    
    @Override
    public String enviarSolicitudParaVerificacion(SolicitudAdopcion solicitud) {
        logger.info("Enviando solicitud {} al refugio para verificación", solicitud.getId());
        
        // Simular asignación de refugio basada en el tipo de mascota
        String refugioAsignado = asignarRefugio(solicitud.getTipoMascotaDeseada());
        
        // Simular tiempo de procesamiento
        try {
            Thread.sleep(100); // Simular latencia de red
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        logger.info("Solicitud {} asignada al refugio: {}", solicitud.getId(), refugioAsignado);
        return refugioAsignado;
    }
    
    @Override
    public VerificationResult verificarEstadoSolicitud(Long solicitudId) {
        logger.info("🏥 Iniciando verificación de solicitud {} en el refugio", solicitudId);
        
        // Simular tiempo de procesamiento del refugio
        simularTiempoProcesamiento();
        
        // Generar decisión aleatoria del refugio (75% aprobación, 25% rechazo)
        boolean aprobada = ThreadLocalRandom.current().nextDouble() < 0.75;
        String refugioAsignado = seleccionarRefugioAleatorio();
        String observaciones = obtenerObservacionesAleatorias(aprobada);
        
        logResultadoVerificacion(solicitudId, aprobada, refugioAsignado);
        
        return new VerificationResult(aprobada, observaciones, refugioAsignado);
    }
    
    private void simularTiempoProcesamiento() {
        try {
            int tiempoEspera = ThreadLocalRandom.current().nextInt(200, 1000);
            Thread.sleep(tiempoEspera);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Procesamiento del refugio interrumpido");
        }
    }
    
    private String seleccionarRefugioAleatorio() {
        int indiceAleatorio = ThreadLocalRandom.current().nextInt(REFUGIOS_DISPONIBLES.size());
        return REFUGIOS_DISPONIBLES.get(indiceAleatorio);
    }
    
    private String obtenerObservacionesAleatorias(boolean aprobada) {
        if (aprobada) {
            int indice = ThreadLocalRandom.current().nextInt(OBSERVACIONES_APROBACION.size());
            return OBSERVACIONES_APROBACION.get(indice);
        } else {
            int indice = ThreadLocalRandom.current().nextInt(MOTIVOS_RECHAZO.size());
            return MOTIVOS_RECHAZO.get(indice);
        }
    }
    
    private void logResultadoVerificacion(Long solicitudId, boolean aprobada, String refugioAsignado) {
        if (aprobada) {
            logger.info("✅ Solicitud {} APROBADA por el refugio: {}", solicitudId, refugioAsignado);
        } else {
            logger.info("❌ Solicitud {} RECHAZADA por el refugio: {}", solicitudId, refugioAsignado);
        }
    }
    
    private String asignarRefugio(TipoMascota tipoMascota) {
        // Simular lógica de asignación basada en especialización del refugio
        switch (tipoMascota) {
            case PERRO:
                return random.nextBoolean() ? "Refugio Esperanza Animal" : "Refugio Municipal de Animales";
            case GATO:
                return random.nextBoolean() ? "Fundación Patitas Felices" : "Hogar de Mascotas San Francisco";
            case AVE:
                return "Asociación Protectora de Animales";
            case CONEJO:
            case HAMSTER:
                return "Hogar de Mascotas San Francisco";
            default:
                return REFUGIOS_DISPONIBLES.get(random.nextInt(REFUGIOS_DISPONIBLES.size()));
        }
    }
}
