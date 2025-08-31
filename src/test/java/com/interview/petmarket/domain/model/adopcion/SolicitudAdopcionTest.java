package com.interview.petmarket.domain.model.adopcion;

import com.interview.petmarket.domain.exceptions.InvalidAdoptionDataException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SolicitudAdopcion Domain Tests")
@Tag("unit")
@Tag("domain")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SolicitudAdopcionTest {

    private static final Long CLIENTE_ID_VALIDO = 1L;
    private static final String NOMBRE_VALIDO = "María García";
    private static final String EMAIL_VALIDO = "maria.garcia@email.com";
    private static final String TELEFONO_VALIDO = "+57 300 123 4567";
    private static final TipoMascota TIPO_MASCOTA_VALIDO = TipoMascota.PERRO;
    private static final String MOTIVO_VALIDO = "Quiero darle amor y cuidado a una mascota";
    private static final String EXPERIENCIA_VALIDA = "He tenido perros durante 10 años";
    private static final String SITUACION_VALIDA = "Casa propia con jardín amplio";

    @Nested
    @DisplayName("Construcción de Solicitud")
    class ConstruccionSolicitud {

        @Test
        @Order(1)
        @DisplayName("Should create valid solicitud with builder")
        void shouldCreateValidSolicitudWithBuilder() {
            // When
            SolicitudAdopcion solicitud = SolicitudAdopcion.builder()
                    .clienteId(CLIENTE_ID_VALIDO)
                    .nombreSolicitante(NOMBRE_VALIDO)
                    .emailSolicitante(EMAIL_VALIDO)
                    .telefonoSolicitante(TELEFONO_VALIDO)
                    .tipoMascotaDeseada(TIPO_MASCOTA_VALIDO)
                    .motivoAdopcion(MOTIVO_VALIDO)
                    .experienciaPrevia(EXPERIENCIA_VALIDA)
                    .situacionVivienda(SITUACION_VALIDA)
                    .build();

            // Then
            assertAll("Validar solicitud creada",
                    () -> assertThat(solicitud).isNotNull(),
                    () -> assertThat(solicitud.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(solicitud.getNombreSolicitante()).isEqualTo(NOMBRE_VALIDO),
                    () -> assertThat(solicitud.getEmailSolicitante()).isEqualTo(EMAIL_VALIDO),
                    () -> assertThat(solicitud.getTelefonoSolicitante()).isEqualTo(TELEFONO_VALIDO),
                    () -> assertThat(solicitud.getTipoMascotaDeseada()).isEqualTo(TIPO_MASCOTA_VALIDO),
                    () -> assertThat(solicitud.getMotivoAdopcion()).isEqualTo(MOTIVO_VALIDO),
                    () -> assertThat(solicitud.getExperienciaPrevia()).isEqualTo(EXPERIENCIA_VALIDA),
                    () -> assertThat(solicitud.getSituacionVivienda()).isEqualTo(SITUACION_VALIDA),
                    () -> assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitudAdopcion.PENDIENTE),
                    () -> assertThat(solicitud.getCreatedAt()).isNotNull(),
                    () -> assertThat(solicitud.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1))
            );
        }

        @Test
        @DisplayName("Should throw exception when clienteId is null")
        void shouldThrowExceptionWhenClienteIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> 
                    SolicitudAdopcion.builder()
                            .clienteId(null)
                            .nombreSolicitante(NOMBRE_VALIDO)
                            .emailSolicitante(EMAIL_VALIDO)
                            .telefonoSolicitante(TELEFONO_VALIDO)
                            .tipoMascotaDeseada(TIPO_MASCOTA_VALIDO)
                            .motivoAdopcion(MOTIVO_VALIDO)
                            .experienciaPrevia(EXPERIENCIA_VALIDA)
                            .situacionVivienda(SITUACION_VALIDA)
                            .build())
                    .isInstanceOf(InvalidAdoptionDataException.class)
                    .hasMessageContaining("Client ID cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("Should throw exception when nombre is blank")
        void shouldThrowExceptionWhenNombreIsBlank(String nombreInvalido) {
            // When & Then
            assertThatThrownBy(() -> 
                    SolicitudAdopcion.builder()
                            .clienteId(CLIENTE_ID_VALIDO)
                            .nombreSolicitante(nombreInvalido)
                            .emailSolicitante(EMAIL_VALIDO)
                            .telefonoSolicitante(TELEFONO_VALIDO)
                            .tipoMascotaDeseada(TIPO_MASCOTA_VALIDO)
                            .motivoAdopcion(MOTIVO_VALIDO)
                            .experienciaPrevia(EXPERIENCIA_VALIDA)
                            .situacionVivienda(SITUACION_VALIDA)
                            .build())
                    .isInstanceOf(InvalidAdoptionDataException.class)
                    .hasMessageContaining("Applicant name cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when email is null or empty")
        void shouldThrowExceptionWhenEmailIsNullOrEmpty() {
            // When & Then
            assertThatThrownBy(() -> 
                    SolicitudAdopcion.builder()
                            .clienteId(CLIENTE_ID_VALIDO)
                            .nombreSolicitante(NOMBRE_VALIDO)
                            .emailSolicitante("")
                            .telefonoSolicitante(TELEFONO_VALIDO)
                            .tipoMascotaDeseada(TIPO_MASCOTA_VALIDO)
                            .motivoAdopcion(MOTIVO_VALIDO)
                            .experienciaPrevia(EXPERIENCIA_VALIDA)
                            .situacionVivienda(SITUACION_VALIDA)
                            .build())
                    .isInstanceOf(InvalidAdoptionDataException.class)
                    .hasMessageContaining("Applicant email cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when tipoMascotaDeseada is null")
        void shouldThrowExceptionWhenTipoMascotaDeseadaIsNull() {
            // When & Then
            assertThatThrownBy(() -> 
                    SolicitudAdopcion.builder()
                            .clienteId(CLIENTE_ID_VALIDO)
                            .nombreSolicitante(NOMBRE_VALIDO)
                            .emailSolicitante(EMAIL_VALIDO)
                            .telefonoSolicitante(TELEFONO_VALIDO)
                            .tipoMascotaDeseada(null)
                            .motivoAdopcion(MOTIVO_VALIDO)
                            .experienciaPrevia(EXPERIENCIA_VALIDA)
                            .situacionVivienda(SITUACION_VALIDA)
                            .build())
                    .isInstanceOf(InvalidAdoptionDataException.class)
                    .hasMessageContaining("Desired pet type cannot be null");
        }
    }

    @Nested
    @DisplayName("Transiciones de Estado")
    class TransicionesEstado {

        @Test
        @DisplayName("Should start verification successfully")
        void shouldStartVerificationSuccessfully() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud();
            String refugioAsignado = "Refugio Esperanza";

            // When
            solicitud.iniciarVerificacion(refugioAsignado);

            // Then
            assertAll("Validar inicio de verificación",
                    () -> assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitudAdopcion.EN_VERIFICACION),
                    () -> assertThat(solicitud.getRefugioAsignado()).isEqualTo(refugioAsignado),
                    () -> assertThat(solicitud.getFechaVerificacion()).isNotNull()
            );
        }

        @Test
        @DisplayName("Should approve solicitud successfully")
        void shouldApproveSolicitudSuccessfully() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud();
            solicitud.iniciarVerificacion("Refugio Esperanza");
            String observaciones = "Excelente perfil para adopción";

            // When
            solicitud.aprobar(observaciones);

            // Then
            assertAll("Validar aprobación",
                    () -> assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitudAdopcion.APROBADA),
                    () -> assertThat(solicitud.getObservacionesRefugio()).isEqualTo(observaciones),
                    () -> assertThat(solicitud.getMotivoRechazo()).isNull()
            );
        }

        @Test
        @DisplayName("Should reject solicitud successfully")
        void shouldRejectSolicitudSuccessfully() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud();
            solicitud.iniciarVerificacion("Refugio Esperanza");
            String motivoRechazo = "Experiencia insuficiente con mascotas";

            // When
            solicitud.rechazar(motivoRechazo);

            // Then
            assertAll("Validar rechazo",
                    () -> assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitudAdopcion.RECHAZADA),
                    () -> assertThat(solicitud.getMotivoRechazo()).isEqualTo(motivoRechazo),
                    () -> assertThat(solicitud.getObservacionesRefugio()).isNull()
            );
        }

        @Test
        @DisplayName("Should cancel solicitud successfully")
        void shouldCancelSolicitudSuccessfully() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud();

            // When
            solicitud.cancelar();

            // Then
            assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitudAdopcion.CANCELADA);
        }

        @Test
        @DisplayName("Should complete solicitud successfully")
        void shouldCompleteSolicitudSuccessfully() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud();
            solicitud.iniciarVerificacion("Refugio Esperanza");
            solicitud.aprobar("Aprobada");

            // When
            solicitud.completar();

            // Then
            assertThat(solicitud.getEstado()).isEqualTo(EstadoSolicitudAdopcion.COMPLETADA);
        }

        @Test
        @DisplayName("Should throw exception when trying to approve from invalid state")
        void shouldThrowExceptionWhenTryingToApproveFromInvalidState() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud();
            // No iniciar verificación

            // When & Then
            assertThatThrownBy(() -> solicitud.aprobar("Observaciones"))
                    .isInstanceOf(InvalidAdoptionDataException.class)
                    .hasMessageContaining("Cannot approve from current state");
        }

        @Test
        @DisplayName("Should throw exception when trying to complete non-approved solicitud")
        void shouldThrowExceptionWhenTryingToCompleteNonApprovedSolicitud() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud();

            // When & Then
            assertThatThrownBy(() -> solicitud.completar())
                    .isInstanceOf(InvalidAdoptionDataException.class)
                    .hasMessageContaining("Cannot complete from current state");
        }
    }

    @Nested
    @DisplayName("Validaciones de Negocio")
    class ValidacionesNegocio {

        @Test
        @DisplayName("Should validate solicitud states correctly")
        void shouldValidateSolicitudStatesCorrectly() {
            // Given
            SolicitudAdopcion solicitudPendiente = createValidSolicitud();
            SolicitudAdopcion solicitudCancelada = createValidSolicitud();
            solicitudCancelada.cancelar();

            // When & Then
            assertAll("Validar estados",
                    () -> assertThat(solicitudPendiente.getEstado()).isEqualTo(EstadoSolicitudAdopcion.PENDIENTE),
                    () -> assertThat(solicitudCancelada.getEstado()).isEqualTo(EstadoSolicitudAdopcion.CANCELADA)
            );
        }

        @ParameterizedTest
        @EnumSource(TipoMascota.class)
        @DisplayName("Should accept all valid TipoMascota values")
        void shouldAcceptAllValidTipoMascotaValues(TipoMascota tipoMascota) {
            // When & Then
            assertDoesNotThrow(() -> 
                    SolicitudAdopcion.builder()
                            .clienteId(CLIENTE_ID_VALIDO)
                            .nombreSolicitante(NOMBRE_VALIDO)
                            .emailSolicitante(EMAIL_VALIDO)
                            .telefonoSolicitante(TELEFONO_VALIDO)
                            .tipoMascotaDeseada(tipoMascota)
                            .motivoAdopcion(MOTIVO_VALIDO)
                            .experienciaPrevia(EXPERIENCIA_VALIDA)
                            .situacionVivienda(SITUACION_VALIDA)
                            .build()
            );
        }
    }

    @Nested
    @DisplayName("Métodos de Utilidad")
    class MetodosUtilidad {

        @Test
        @DisplayName("Should generate correct string representation")
        void shouldGenerateCorrectStringRepresentation() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud();

            // When
            String resultado = solicitud.toString();

            // Then
            assertAll("Validar toString",
                    () -> assertThat(resultado).contains("SolicitudAdopcion"),
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado).isNotEmpty()
            );
        }

        @Test
        @DisplayName("Should implement equals and hashCode correctly")
        void shouldImplementEqualsAndHashCodeCorrectly() {
            // Given
            SolicitudAdopcion solicitud1 = createValidSolicitudWithId(1L);
            SolicitudAdopcion solicitud2 = createValidSolicitudWithId(1L);
            SolicitudAdopcion solicitud3 = createValidSolicitudWithId(2L);

            // When & Then
            assertAll("Validar equals y hashCode",
                    () -> assertThat(solicitud1).isEqualTo(solicitud2),
                    () -> assertThat(solicitud1).isNotEqualTo(solicitud3),
                    () -> assertThat(solicitud1.hashCode()).isEqualTo(solicitud2.hashCode()),
                    () -> assertThat(solicitud1.hashCode()).isNotEqualTo(solicitud3.hashCode())
            );
        }

        @Test
        @DisplayName("Should handle null values in equals")
        void shouldHandleNullValuesInEquals() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud();

            // When & Then
            assertAll("Validar equals con null",
                    () -> assertThat(solicitud).isNotEqualTo(null),
                    () -> assertThat(solicitud).isNotEqualTo("string"),
                    () -> assertThat(solicitud).isEqualTo(solicitud)
            );
        }
    }

    @Nested
    @DisplayName("Casos Edge")
    class CasosEdge {

        @Test
        @DisplayName("Should handle very long text fields")
        void shouldHandleVeryLongTextFields() {
            // Given
            String textoLargo = "A".repeat(1000);

            // When & Then
            assertDoesNotThrow(() -> 
                    SolicitudAdopcion.builder()
                            .clienteId(CLIENTE_ID_VALIDO)
                            .nombreSolicitante(NOMBRE_VALIDO)
                            .emailSolicitante(EMAIL_VALIDO)
                            .telefonoSolicitante(TELEFONO_VALIDO)
                            .tipoMascotaDeseada(TIPO_MASCOTA_VALIDO)
                            .motivoAdopcion(textoLargo)
                            .experienciaPrevia(textoLargo)
                            .situacionVivienda(textoLargo)
                            .build()
            );
        }

        @Test
        @DisplayName("Should handle special characters in text fields")
        void shouldHandleSpecialCharactersInTextFields() {
            // Given
            String textoConCaracteresEspeciales = "Texto con ñ, acentós, símbolos @#$%^&*()";

            // When & Then
            assertDoesNotThrow(() -> 
                    SolicitudAdopcion.builder()
                            .clienteId(CLIENTE_ID_VALIDO)
                            .nombreSolicitante(textoConCaracteresEspeciales)
                            .emailSolicitante(EMAIL_VALIDO)
                            .telefonoSolicitante(TELEFONO_VALIDO)
                            .tipoMascotaDeseada(TIPO_MASCOTA_VALIDO)
                            .motivoAdopcion(textoConCaracteresEspeciales)
                            .experienciaPrevia(textoConCaracteresEspeciales)
                            .situacionVivienda(textoConCaracteresEspeciales)
                            .build()
            );
        }
    }

    // Helper methods
    private SolicitudAdopcion createValidSolicitud() {
        return SolicitudAdopcion.builder()
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

    private SolicitudAdopcion createValidSolicitudWithId(Long id) {
        return SolicitudAdopcion.builder()
                .id(id)
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
