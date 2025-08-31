package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.events.AdoptionApprovedEvent;
import com.interview.petmarket.domain.events.AdoptionRejectedEvent;
import com.interview.petmarket.domain.events.AdoptionRequestedEvent;
import com.interview.petmarket.domain.exceptions.InvalidAdoptionDataException;
import com.interview.petmarket.domain.model.adopcion.EstadoSolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.TipoMascota;
import com.interview.petmarket.domain.ports.out.EventPublisherPort;
import com.interview.petmarket.domain.ports.out.SolicitudAdopcionRepositoryPort;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("AdopcionApplicationService Tests")
@Tag("unit")
@Tag("service")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdopcionApplicationServiceTest {

    @Mock
    private SolicitudAdopcionRepositoryPort solicitudRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private AdopcionApplicationService adopcionService;

    private static final Long CLIENTE_ID_VALIDO = 1L;
    private static final Long SOLICITUD_ID_VALIDA = 100L;
    private static final String NOMBRE_VALIDO = "María García";
    private static final String EMAIL_VALIDO = "maria.garcia@email.com";
    private static final String TELEFONO_VALIDO = "+57 300 123 4567";
    private static final TipoMascota TIPO_MASCOTA_VALIDO = TipoMascota.PERRO;
    private static final String MOTIVO_VALIDO = "Quiero darle amor y cuidado a una mascota";
    private static final String EXPERIENCIA_VALIDA = "He tenido perros durante 10 años";
    private static final String SITUACION_VALIDA = "Casa propia con jardín amplio";

    @BeforeAll
    static void setupClass() {
        // Configuración global para todos los tests
    }

    @AfterAll
    static void tearDownClass() {
        // Limpieza global después de todos los tests
    }

    @Nested
    @DisplayName("Crear Solicitud Adopción")
    class CrearSolicitudAdopcion {

        @Test
        @Order(1)
        @DisplayName("Should create adoption request successfully")
        @Timeout(value = 2)
        void shouldCreateAdoptionRequestSuccessfully() {
            // Given
            SolicitudAdopcion solicitudEsperada = createValidSolicitudAdopcion();
            
            when(solicitudRepository.existeSolicitudActivaPorCliente(CLIENTE_ID_VALIDO))
                    .thenReturn(false);
            when(solicitudRepository.save(any(SolicitudAdopcion.class)))
                    .thenReturn(solicitudEsperada);

            // When
            SolicitudAdopcion resultado = assertTimeout(Duration.ofMillis(100), () ->
                    adopcionService.crearSolicitudAdopcion(
                            CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO, TELEFONO_VALIDO,
                            TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO, EXPERIENCIA_VALIDA, SITUACION_VALIDA
                    )
            );

            // Then
            assertAll("Validar solicitud creada",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(resultado.getNombreSolicitante()).isEqualTo(NOMBRE_VALIDO),
                    () -> assertThat(resultado.getEmailSolicitante()).isEqualTo(EMAIL_VALIDO),
                    () -> assertThat(resultado.getTipoMascotaDeseada()).isEqualTo(TIPO_MASCOTA_VALIDO),
                    () -> assertThat(resultado.getEstado()).isEqualTo(EstadoSolicitudAdopcion.PENDIENTE),
                    () -> verify(solicitudRepository).existeSolicitudActivaPorCliente(CLIENTE_ID_VALIDO),
                    () -> verify(solicitudRepository).save(any(SolicitudAdopcion.class)),
                    () -> verify(eventPublisher).publishEvent(any(AdoptionRequestedEvent.class))
            );
        }

        @Test
        @DisplayName("Should throw exception when client has active request")
        void shouldThrowExceptionWhenClientHasActiveRequest() {
            // Given
            when(solicitudRepository.existeSolicitudActivaPorCliente(CLIENTE_ID_VALIDO))
                    .thenReturn(true);

            // When & Then
            assertThatThrownBy(() -> 
                    adopcionService.crearSolicitudAdopcion(
                            CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO, TELEFONO_VALIDO,
                            TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO, EXPERIENCIA_VALIDA, SITUACION_VALIDA
                    ))
                    .isInstanceOf(InvalidAdoptionDataException.class)
                    .hasMessageContaining("El cliente ya tiene una solicitud de adopción activa");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("Should throw exception when nombre is blank")
        void shouldThrowExceptionWhenNombreIsBlank(String nombreInvalido) {
            // When & Then
            assertThatThrownBy(() -> 
                    adopcionService.crearSolicitudAdopcion(
                            CLIENTE_ID_VALIDO, nombreInvalido, EMAIL_VALIDO, TELEFONO_VALIDO,
                            TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO, EXPERIENCIA_VALIDA, SITUACION_VALIDA
                    ))
                    .isInstanceOf(InvalidAdoptionDataException.class);
        }



        @Test
        @DisplayName("Should publish AdoptionRequestedEvent with correct data")
        void shouldPublishAdoptionRequestedEventWithCorrectData() {
            // Given
            SolicitudAdopcion solicitudEsperada = createValidSolicitudAdopcion();
            
            when(solicitudRepository.existeSolicitudActivaPorCliente(CLIENTE_ID_VALIDO))
                    .thenReturn(false);
            when(solicitudRepository.save(any(SolicitudAdopcion.class)))
                    .thenReturn(solicitudEsperada);

            ArgumentCaptor<AdoptionRequestedEvent> eventCaptor = ArgumentCaptor.forClass(AdoptionRequestedEvent.class);

            // When
            adopcionService.crearSolicitudAdopcion(
                    CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO, TELEFONO_VALIDO,
                    TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO, EXPERIENCIA_VALIDA, SITUACION_VALIDA
            );

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            AdoptionRequestedEvent evento = eventCaptor.getValue();
            
            assertAll("Validar evento publicado",
                    () -> assertThat(evento.getSolicitudId()).isEqualTo(SOLICITUD_ID_VALIDA),
                    () -> assertThat(evento.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(evento.getNombreSolicitante()).isEqualTo(NOMBRE_VALIDO),
                    () -> assertThat(evento.getEmailSolicitante()).isEqualTo(EMAIL_VALIDO),
                    () -> assertThat(evento.getTipoMascotaDeseada()).isEqualTo(TIPO_MASCOTA_VALIDO)
            );
        }
    }

    @Nested
    @DisplayName("Obtener Solicitud Por ID")
    class ObtenerSolicitudPorId {

        @Test
        @DisplayName("Should return solicitud when found")
        void shouldReturnSolicitudWhenFound() {
            // Given
            SolicitudAdopcion solicitudExistente = createValidSolicitudAdopcion();
            when(solicitudRepository.findById(SOLICITUD_ID_VALIDA))
                    .thenReturn(Optional.of(solicitudExistente));

            // When
            SolicitudAdopcion resultado = adopcionService.obtenerSolicitudPorId(SOLICITUD_ID_VALIDA);

            // Then
            assertAll("Validar solicitud obtenida",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado.getId()).isEqualTo(SOLICITUD_ID_VALIDA),
                    () -> verify(solicitudRepository).findById(SOLICITUD_ID_VALIDA)
            );
        }

        @Test
        @DisplayName("Should throw exception when solicitud not found")
        void shouldThrowExceptionWhenSolicitudNotFound() {
            // Given
            when(solicitudRepository.findById(SOLICITUD_ID_VALIDA))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> adopcionService.obtenerSolicitudPorId(SOLICITUD_ID_VALIDA))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Solicitud de adopción no encontrada");
        }


    }

    @Nested
    @DisplayName("Procesar Respuesta Refugio")
    class ProcesarRespuestaRefugio {

        @Test
        @DisplayName("Should approve solicitud and publish event")
        void shouldApproveSolicitudAndPublishEvent() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitudAdopcion();
            solicitud.iniciarVerificacion("Refugio Esperanza");
            String observaciones = "Excelente perfil para adopción";
            
            when(solicitudRepository.findById(SOLICITUD_ID_VALIDA))
                    .thenReturn(Optional.of(solicitud));

            ArgumentCaptor<AdoptionApprovedEvent> eventCaptor = ArgumentCaptor.forClass(AdoptionApprovedEvent.class);

            // When
            adopcionService.procesarRespuestaRefugio(SOLICITUD_ID_VALIDA, true, observaciones);

            // Then
            assertAll("Validar aprobación",
                    () -> verify(solicitudRepository).save(solicitud),
                    () -> verify(eventPublisher).publishEvent(eventCaptor.capture())
            );

            AdoptionApprovedEvent evento = eventCaptor.getValue();
            assertAll("Validar evento de aprobación",
                    () -> assertThat(evento.getSolicitudId()).isEqualTo(SOLICITUD_ID_VALIDA),
                    () -> assertThat(evento.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(evento.getObservacionesRefugio()).isEqualTo(observaciones)
            );
        }

        @Test
        @DisplayName("Should reject solicitud and publish event")
        void shouldRejectSolicitudAndPublishEvent() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitudAdopcion();
            solicitud.iniciarVerificacion("Refugio Esperanza");
            String motivoRechazo = "Experiencia insuficiente";
            
            when(solicitudRepository.findById(SOLICITUD_ID_VALIDA))
                    .thenReturn(Optional.of(solicitud));

            ArgumentCaptor<AdoptionRejectedEvent> eventCaptor = ArgumentCaptor.forClass(AdoptionRejectedEvent.class);

            // When
            adopcionService.procesarRespuestaRefugio(SOLICITUD_ID_VALIDA, false, motivoRechazo);

            // Then
            assertAll("Validar rechazo",
                    () -> verify(solicitudRepository).save(solicitud),
                    () -> verify(eventPublisher).publishEvent(eventCaptor.capture())
            );

            AdoptionRejectedEvent evento = eventCaptor.getValue();
            assertAll("Validar evento de rechazo",
                    () -> assertThat(evento.getSolicitudId()).isEqualTo(SOLICITUD_ID_VALIDA),
                    () -> assertThat(evento.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(evento.getMotivoRechazo()).isEqualTo(motivoRechazo)
            );
        }
    }

    @Nested
    @DisplayName("Listar Solicitudes")
    class ListarSolicitudes {

        @Test
        @DisplayName("Should return solicitudes by client")
        void shouldReturnSolicitudesByClient() {
            // Given
            List<SolicitudAdopcion> solicitudesEsperadas = Arrays.asList(
                    createValidSolicitudAdopcion(),
                    createValidSolicitudAdopcion()
            );
            
            when(solicitudRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(solicitudesEsperadas);

            // When
            List<SolicitudAdopcion> resultado = adopcionService.listarSolicitudesPorCliente(CLIENTE_ID_VALIDO);

            // Then
            assertAll("Validar solicitudes por cliente",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado).hasSize(2),
                    () -> verify(solicitudRepository).findByClienteId(CLIENTE_ID_VALIDO)
            );
        }

        @Test
        @DisplayName("Should return pending solicitudes")
        void shouldReturnPendingSolicitudes() {
            // Given
            List<SolicitudAdopcion> solicitudesPendientes = Arrays.asList(
                    createValidSolicitudAdopcion()
            );
            
            when(solicitudRepository.findPendientes())
                    .thenReturn(solicitudesPendientes);

            // When
            List<SolicitudAdopcion> resultado = adopcionService.listarSolicitudesPendientes();

            // Then
            assertAll("Validar solicitudes pendientes",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado).hasSize(1),
                    () -> verify(solicitudRepository).findPendientes()
            );
        }
    }

    @Nested
    @DisplayName("Cancelar y Completar Solicitud")
    class CancelarYCompletarSolicitud {

        @Test
        @DisplayName("Should cancel solicitud successfully")
        void shouldCancelSolicitudSuccessfully() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitudAdopcion();
            when(solicitudRepository.findById(SOLICITUD_ID_VALIDA))
                    .thenReturn(Optional.of(solicitud));

            // When
            adopcionService.cancelarSolicitud(SOLICITUD_ID_VALIDA);

            // Then
            assertAll("Validar cancelación",
                    () -> assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitudAdopcion.CANCELADA),
                    () -> verify(solicitudRepository).save(solicitud)
            );
        }


    }

    @Nested
    @DisplayName("Validaciones de Parámetros")
    class ValidacionesParametros {

        @ParameterizedTest
        @EnumSource(TipoMascota.class)
        @DisplayName("Should accept all valid TipoMascota values")
        void shouldAcceptAllValidTipoMascotaValues(TipoMascota tipoMascota) {
            // Given
            when(solicitudRepository.existeSolicitudActivaPorCliente(CLIENTE_ID_VALIDO))
                    .thenReturn(false);
            when(solicitudRepository.save(any(SolicitudAdopcion.class)))
                    .thenReturn(createValidSolicitudAdopcion());

            // When & Then
            assertDoesNotThrow(() -> 
                    adopcionService.crearSolicitudAdopcion(
                            CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO, TELEFONO_VALIDO,
                            tipoMascota, MOTIVO_VALIDO, EXPERIENCIA_VALIDA, SITUACION_VALIDA
                    )
            );
        }


    }

    @Nested
    @DisplayName("Advanced Features")
    class AdvancedFeatures {

        @RepeatedTest(value = 3, name = "Repetición {currentRepetition} de {totalRepetitions}")
        @DisplayName("Should handle multiple creation operations consistently")
        void shouldHandleMultipleCreationOperationsConsistently(RepetitionInfo repetitionInfo) {
            // Given
            when(solicitudRepository.existeSolicitudActivaPorCliente(any()))
                    .thenReturn(false);
            when(solicitudRepository.save(any(SolicitudAdopcion.class)))
                    .thenReturn(createValidSolicitudAdopcion());

            // When
            SolicitudAdopcion resultado = adopcionService.crearSolicitudAdopcion(
                    CLIENTE_ID_VALIDO + repetitionInfo.getCurrentRepetition(),
                    NOMBRE_VALIDO, EMAIL_VALIDO, TELEFONO_VALIDO,
                    TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO, EXPERIENCIA_VALIDA, SITUACION_VALIDA
            );

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getEstado()).isEqualTo(EstadoSolicitudAdopcion.PENDIENTE);
        }

        @Test
        @DisplayName("Should handle concurrent operations safely")
        @Timeout(value = 5)
        void shouldHandleConcurrentOperationsSafely() {
            // Given
            when(solicitudRepository.existeSolicitudActivaPorCliente(any()))
                    .thenReturn(false);
            when(solicitudRepository.save(any(SolicitudAdopcion.class)))
                    .thenReturn(createValidSolicitudAdopcion());

            // When & Then
            assertTimeout(Duration.ofSeconds(3), () -> {
                for (int i = 0; i < 10; i++) {
                    adopcionService.crearSolicitudAdopcion(
                            CLIENTE_ID_VALIDO + i, NOMBRE_VALIDO, EMAIL_VALIDO, TELEFONO_VALIDO,
                            TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO, EXPERIENCIA_VALIDA, SITUACION_VALIDA
                    );
                }
            });
        }
    }

    // Helper methods
    private SolicitudAdopcion createValidSolicitudAdopcion() {
        return SolicitudAdopcion.builder()
                .id(SOLICITUD_ID_VALIDA)
                .clienteId(CLIENTE_ID_VALIDO)
                .nombreSolicitante(NOMBRE_VALIDO)
                .emailSolicitante(EMAIL_VALIDO)
                .telefonoSolicitante(TELEFONO_VALIDO)
                .tipoMascotaDeseada(TIPO_MASCOTA_VALIDO)
                .motivoAdopcion(MOTIVO_VALIDO)
                .experienciaPrevia(EXPERIENCIA_VALIDA)
                .situacionVivienda(SITUACION_VALIDA)
                .build();
    }
}
