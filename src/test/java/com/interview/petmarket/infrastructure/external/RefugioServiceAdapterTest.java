package com.interview.petmarket.infrastructure.external;

import com.interview.petmarket.domain.model.adopcion.SolicitudAdopcion;
import com.interview.petmarket.domain.model.adopcion.TipoMascota;
import com.interview.petmarket.domain.ports.out.RefugioServicePort;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RefugioServiceAdapter Tests")
@Tag("unit")
@Tag("infrastructure")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RefugioServiceAdapterTest {

    private RefugioServiceAdapter refugioService;

    private static final Long SOLICITUD_ID_VALIDA = 100L;
    private static final Long CLIENTE_ID_VALIDO = 1L;
    private static final String NOMBRE_VALIDO = "María García";
    private static final String EMAIL_VALIDO = "maria.garcia@email.com";
    private static final String TELEFONO_VALIDO = "+57 300 123 4567";
    private static final String MOTIVO_VALIDO = "Quiero darle amor y cuidado a una mascota";
    private static final String EXPERIENCIA_VALIDA = "He tenido perros durante 10 años";
    private static final String SITUACION_VALIDA = "Casa propia con jardín amplio";

    @BeforeEach
    void setUp() {
        refugioService = new RefugioServiceAdapter();
    }

    @Nested
    @DisplayName("Enviar Solicitud Para Verificación")
    class EnviarSolicitudParaVerificacion {

        @Test
        @Order(1)
        @DisplayName("Should assign refuge successfully for dog adoption")
        @Timeout(value = 2)
        void shouldAssignRefugeSuccessfullyForDogAdoption() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud(TipoMascota.PERRO);

            // When
            String refugioAsignado = assertTimeout(Duration.ofMillis(500), () ->
                    refugioService.enviarSolicitudParaVerificacion(solicitud)
            );

            // Then
            assertAll("Validar asignación de refugio para perro",
                    () -> assertThat(refugioAsignado).isNotNull(),
                    () -> assertThat(refugioAsignado).isNotEmpty(),
                    () -> assertThat(refugioAsignado).containsAnyOf(
                            "Refugio Esperanza Animal", 
                            "Refugio Municipal de Animales"
                    )
            );
        }

        @Test
        @DisplayName("Should assign refuge successfully for cat adoption")
        void shouldAssignRefugeSuccessfullyForCatAdoption() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud(TipoMascota.GATO);

            // When
            String refugioAsignado = refugioService.enviarSolicitudParaVerificacion(solicitud);

            // Then
            assertAll("Validar asignación de refugio para gato",
                    () -> assertThat(refugioAsignado).isNotNull(),
                    () -> assertThat(refugioAsignado).isNotEmpty(),
                    () -> assertThat(refugioAsignado).containsAnyOf(
                            "Fundación Patitas Felices",
                            "Hogar de Mascotas San Francisco"
                    )
            );
        }

        @ParameterizedTest
        @EnumSource(TipoMascota.class)
        @DisplayName("Should assign appropriate refuge for all pet types")
        void shouldAssignAppropriateRefugeForAllPetTypes(TipoMascota tipoMascota) {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud(tipoMascota);

            // When
            String refugioAsignado = refugioService.enviarSolicitudParaVerificacion(solicitud);

            // Then
            assertAll("Validar asignación por tipo de mascota",
                    () -> assertThat(refugioAsignado).isNotNull(),
                    () -> assertThat(refugioAsignado).isNotEmpty(),
                    () -> assertThat(refugioAsignado.length()).isGreaterThan(5)
            );
        }

        @Test
        @DisplayName("Should handle null solicitud gracefully")
        void shouldHandleNullSolicitudGracefully() {
            // When & Then
            assertThatThrownBy(() -> refugioService.enviarSolicitudParaVerificacion(null))
                    .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle multiple requests consistently")
        void shouldHandleMultipleRequestsConsistently() {
            // Given
            SolicitudAdopcion solicitud = createValidSolicitud(TipoMascota.PERRO);

            // When
            String refugio1 = refugioService.enviarSolicitudParaVerificacion(solicitud);
            String refugio2 = refugioService.enviarSolicitudParaVerificacion(solicitud);
            String refugio3 = refugioService.enviarSolicitudParaVerificacion(solicitud);

            // Then
            assertAll("Validar consistencia en múltiples solicitudes",
                    () -> assertThat(refugio1).isNotNull(),
                    () -> assertThat(refugio2).isNotNull(),
                    () -> assertThat(refugio3).isNotNull(),
                    // Los refugios pueden ser diferentes debido a la lógica aleatoria
                    () -> assertThat(refugio1).isNotEmpty(),
                    () -> assertThat(refugio2).isNotEmpty(),
                    () -> assertThat(refugio3).isNotEmpty()
            );
        }
    }

    @Nested
    @DisplayName("Verificar Estado Solicitud")
    class VerificarEstadoSolicitud {

        @Test
        @DisplayName("Should return verification result with random approval")
        void shouldReturnVerificationResultWithRandomApproval() {
            // Given
            Long solicitudId = SOLICITUD_ID_VALIDA;

            // When
            RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud(solicitudId);

            // Then
            assertAll("Validar resultado de verificación",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado.isAprobada()).isIn(true, false),
                    () -> assertThat(resultado.getObservaciones()).isNotNull(),
                    () -> assertThat(resultado.getObservaciones()).isNotEmpty(),
                    () -> assertThat(resultado.getRefugioAsignado()).isNotNull(),
                    () -> assertThat(resultado.getRefugioAsignado()).isNotEmpty()
            );
        }

        @Test
        @DisplayName("Should return appropriate observations for approved requests")
        void shouldReturnAppropriateObservationsForApprovedRequests() {
            // Given & When
            // Ejecutar múltiples veces para obtener al menos una aprobación
            String observacionesAprobacion = null;
            
            for (int i = 0; i < 20; i++) {
                RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud(SOLICITUD_ID_VALIDA + i);
                if (resultado.isAprobada()) {
                    observacionesAprobacion = resultado.getObservaciones();
                    break;
                }
            }

            // Then
            final String finalObservaciones = observacionesAprobacion;
            if (finalObservaciones != null) {
                assertAll("Validar observaciones de aprobación",
                        () -> assertThat(finalObservaciones).isNotNull(),
                        () -> assertThat(finalObservaciones).containsAnyOf(
                                "Excelente perfil para adopción",
                                "Situación de vivienda adecuada",
                                "Motivación genuina",
                                "Condiciones ideales",
                                "Perfil responsable"
                        )
                );
            }
        }

        @Test
        @DisplayName("Should return appropriate reasons for rejected requests")
        void shouldReturnAppropriateReasonsForRejectedRequests() {
            // Given & When
            // Ejecutar múltiples veces para obtener al menos un rechazo
            String motivoRechazo = null;
            
            for (int i = 0; i < 20; i++) {
                RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud(SOLICITUD_ID_VALIDA + i);
                if (!resultado.isAprobada()) {
                    motivoRechazo = resultado.getObservaciones();
                    break;
                }
            }

            // Then
            final String finalMotivoRechazo = motivoRechazo;
            if (finalMotivoRechazo != null) {
                assertAll("Validar motivos de rechazo",
                        () -> assertThat(finalMotivoRechazo).isNotNull(),
                        () -> assertThat(finalMotivoRechazo).containsAnyOf(
                                "Falta de experiencia previa",
                                "Situación de vivienda no adecuada",
                                "Motivación insuficiente",
                                "No cumple con los requisitos",
                                "Referencias no satisfactorias"
                        )
                );
            }
        }

        @Test
        @DisplayName("Should simulate processing time")
        void shouldSimulateProcessingTime() {
            // Given
            Long solicitudId = SOLICITUD_ID_VALIDA;
            long startTime = System.currentTimeMillis();

            // When
            RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud(solicitudId);
            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;

            // Then
            assertAll("Validar tiempo de procesamiento simulado",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(processingTime).isGreaterThanOrEqualTo(200L), // Mínimo 200ms
                    () -> assertThat(processingTime).isLessThan(2000L) // Máximo 2 segundos para el test
            );
        }

        @Test
        @DisplayName("Should handle null solicitudId gracefully")
        void shouldHandleNullSolicitudIdGracefully() {
            // When & Then
            RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud(null);
            assertThat(resultado).isNotNull();
        }

        @ParameterizedTest
        @ValueSource(longs = {-1L, 0L, Long.MIN_VALUE})
        @DisplayName("Should handle invalid solicitud IDs gracefully")
        void shouldHandleInvalidSolicitudIdsGracefully(Long invalidId) {
            // When & Then
            assertDoesNotThrow(() -> {
                RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud(invalidId);
                assertThat(resultado).isNotNull();
            });
        }
    }

    @Nested
    @DisplayName("Randomization and Statistics")
    class RandomizationAndStatistics {

        @Test
        @DisplayName("Should maintain approximately 75% approval rate")
        void shouldMaintainApproximately75PercentApprovalRate() {
            // Given
            int totalTests = 100;
            int approvedCount = 0;

            // When
            for (int i = 0; i < totalTests; i++) {
                RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud((long) i);
                if (resultado.isAprobada()) {
                    approvedCount++;
                }
            }

            // Then
            final double approvalRate = (double) approvedCount / totalTests;
            final int finalApprovedCount = approvedCount;
            assertAll("Validar tasa de aprobación",
                    () -> assertThat(approvalRate).isGreaterThan(0.6), // Al menos 60%
                    () -> assertThat(approvalRate).isLessThan(0.9),    // Máximo 90%
                    () -> assertThat(finalApprovedCount).isGreaterThan(50),  // Al menos 50 aprobaciones
                    () -> assertThat(finalApprovedCount).isLessThan(95)      // Máximo 95 aprobaciones
            );
        }

        @Test
        @DisplayName("Should distribute refuges randomly")
        void shouldDistributeRefugesRandomly() {
            // Given
            int totalTests = 50;
            java.util.Set<String> refugiosAsignados = new java.util.HashSet<>();

            // When
            for (int i = 0; i < totalTests; i++) {
                SolicitudAdopcion solicitud = createValidSolicitud(TipoMascota.PERRO);
                String refugio = refugioService.enviarSolicitudParaVerificacion(solicitud);
                refugiosAsignados.add(refugio);
            }

            // Then
            assertAll("Validar distribución de refugios",
                    () -> assertThat(refugiosAsignados).isNotEmpty(),
                    () -> assertThat(refugiosAsignados.size()).isGreaterThan(1), // Al menos 2 refugios diferentes
                    () -> assertThat(refugiosAsignados).allMatch(refugio -> refugio != null && !refugio.isEmpty())
            );
        }

        @RepeatedTest(value = 5, name = "Repetición {currentRepetition} de {totalRepetitions}")
        @DisplayName("Should handle repeated verifications consistently")
        void shouldHandleRepeatedVerificationsConsistently(RepetitionInfo repetitionInfo) {
            // Given
            Long solicitudId = SOLICITUD_ID_VALIDA + repetitionInfo.getCurrentRepetition();

            // When
            RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud(solicitudId);

            // Then
            assertAll("Validar consistencia en repeticiones",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado.getObservaciones()).isNotEmpty(),
                    () -> assertThat(resultado.getRefugioAsignado()).isNotEmpty()
            );
        }
    }

    @Nested
    @DisplayName("Performance and Concurrency")
    class PerformanceAndConcurrency {

        @Test
        @DisplayName("Should handle concurrent requests safely")
        @Timeout(value = 10)
        void shouldHandleConcurrentRequestsSafely() {
            // Given
            int numberOfThreads = 10;
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(numberOfThreads);
            java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);

            // When
            for (int i = 0; i < numberOfThreads; i++) {
                final int threadId = i;
                new Thread(() -> {
                    try {
                        SolicitudAdopcion solicitud = createValidSolicitud(TipoMascota.PERRO);
                        String refugio = refugioService.enviarSolicitudParaVerificacion(solicitud);
                        
                        RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud((long) threadId);
                        
                        if (refugio != null && !refugio.isEmpty() && resultado != null) {
                            successCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        // Log error but don't fail the test
                        System.err.println("Error in thread " + threadId + ": " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                }).start();
            }

            // Then
            assertTimeout(Duration.ofSeconds(5), () -> {
                latch.await();
                assertThat(successCount.get()).isEqualTo(numberOfThreads);
            });
        }

        @Test
        @DisplayName("Should complete verification within reasonable time")
        void shouldCompleteVerificationWithinReasonableTime() {
            // Given
            Long solicitudId = SOLICITUD_ID_VALIDA;

            // When & Then
            assertTimeout(Duration.ofSeconds(2), () -> {
                RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud(solicitudId);
                assertThat(resultado).isNotNull();
            });
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandling {

        @Test
        @DisplayName("Should handle very large solicitud IDs")
        void shouldHandleVeryLargeSolicitudIds() {
            // Given
            Long largeSolicitudId = Long.MAX_VALUE;

            // When & Then
            assertDoesNotThrow(() -> {
                RefugioServicePort.VerificationResult resultado = refugioService.verificarEstadoSolicitud(largeSolicitudId);
                assertThat(resultado).isNotNull();
            });
        }

        @Test
        @DisplayName("Should handle solicitud with minimal data")
        void shouldHandleSolicitudWithMinimalData() {
            // Given
            SolicitudAdopcion solicitudMinimal = SolicitudAdopcion.builder()
                    .clienteId(1L)
                    .nombreSolicitante("Test")
                    .emailSolicitante("test@test.com")
                    .telefonoSolicitante("123456789")
                    .tipoMascotaDeseada(TipoMascota.PERRO)
                    .motivoAdopcion("Test")
                    .experienciaPrevia("Test")
                    .situacionVivienda("Test")
                    .build();

            // When & Then
            assertDoesNotThrow(() -> {
                String refugio = refugioService.enviarSolicitudParaVerificacion(solicitudMinimal);
                assertThat(refugio).isNotNull();
            });
        }
    }

    // Helper methods
    private SolicitudAdopcion createValidSolicitud(TipoMascota tipoMascota) {
        return SolicitudAdopcion.builder()
                .id(SOLICITUD_ID_VALIDA)
                .clienteId(CLIENTE_ID_VALIDO)
                .nombreSolicitante(NOMBRE_VALIDO)
                .emailSolicitante(EMAIL_VALIDO)
                .telefonoSolicitante(TELEFONO_VALIDO)
                .tipoMascotaDeseada(tipoMascota)
                .motivoAdopcion(MOTIVO_VALIDO)
                .experienciaPrevia(EXPERIENCIA_VALIDA)
                .situacionVivienda(SITUACION_VALIDA)
                .build();
    }
}
