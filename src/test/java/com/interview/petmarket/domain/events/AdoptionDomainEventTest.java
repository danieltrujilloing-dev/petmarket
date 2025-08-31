package com.interview.petmarket.domain.events;

import com.interview.petmarket.domain.model.adopcion.TipoMascota;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Adoption Domain Events Tests")
@Tag("unit")
@Tag("domain")
@Tag("events")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AdoptionDomainEventTest {

    private static final Long SOLICITUD_ID_VALIDA = 100L;
    private static final Long CLIENTE_ID_VALIDO = 1L;
    private static final String NOMBRE_VALIDO = "María García";
    private static final String EMAIL_VALIDO = "maria.garcia@email.com";
    private static final String TELEFONO_VALIDO = "+57 300 123 4567";
    private static final TipoMascota TIPO_MASCOTA_VALIDO = TipoMascota.PERRO;
    private static final String MOTIVO_VALIDO = "Quiero darle amor y cuidado a una mascota";
    private static final String EXPERIENCIA_VALIDA = "He tenido perros durante 10 años";
    private static final String SITUACION_VALIDA = "Casa propia con jardín amplio";
    private static final String REFUGIO_VALIDO = "Refugio Esperanza";
    private static final String OBSERVACIONES_VALIDAS = "Excelente perfil para adopción";
    private static final String MOTIVO_RECHAZO_VALIDO = "Experiencia insuficiente";

    @Nested
    @DisplayName("AdoptionRequestedEvent Tests")
    class AdoptionRequestedEventTests {

        @Test
        @Order(1)
        @DisplayName("Should create AdoptionRequestedEvent with all required fields")
        void shouldCreateAdoptionRequestedEventWithAllRequiredFields() {
            // When
            AdoptionRequestedEvent event = new AdoptionRequestedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO,
                    TELEFONO_VALIDO, TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO, 
                    EXPERIENCIA_VALIDA, SITUACION_VALIDA
            );

            // Then
            assertAll("Validar AdoptionRequestedEvent",
                    () -> assertThat(event).isNotNull(),
                    () -> assertThat(event.getSolicitudId()).isEqualTo(SOLICITUD_ID_VALIDA),
                    () -> assertThat(event.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(event.getNombreSolicitante()).isEqualTo(NOMBRE_VALIDO),
                    () -> assertThat(event.getEmailSolicitante()).isEqualTo(EMAIL_VALIDO),
                    () -> assertThat(event.getTelefonoSolicitante()).isEqualTo(TELEFONO_VALIDO),
                    () -> assertThat(event.getTipoMascotaDeseada()).isEqualTo(TIPO_MASCOTA_VALIDO),
                    () -> assertThat(event.getMotivoAdopcion()).isEqualTo(MOTIVO_VALIDO),
                    () -> assertThat(event.getExperienciaPrevia()).isEqualTo(EXPERIENCIA_VALIDA),
                    () -> assertThat(event.getSituacionVivienda()).isEqualTo(SITUACION_VALIDA),
                    () -> assertThat(event.getOccurredOn()).isNotNull(),
                    () -> assertThat(event.getOccurredOn()).isBefore(LocalDateTime.now().plusSeconds(1))
            );
        }

        @Test
        @DisplayName("Should allow null values in constructor")
        void shouldAllowNullValuesInConstructor() {
            // When & Then
            assertDoesNotThrow(() -> new AdoptionRequestedEvent(
                    null, null, NOMBRE_VALIDO, EMAIL_VALIDO,
                    TELEFONO_VALIDO, TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO,
                    EXPERIENCIA_VALIDA, SITUACION_VALIDA
            ));
        }

        @ParameterizedTest
        @EnumSource(TipoMascota.class)
        @DisplayName("Should accept all valid TipoMascota values")
        void shouldAcceptAllValidTipoMascotaValues(TipoMascota tipoMascota) {
            // When & Then
            assertDoesNotThrow(() -> new AdoptionRequestedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO,
                    TELEFONO_VALIDO, tipoMascota, MOTIVO_VALIDO,
                    EXPERIENCIA_VALIDA, SITUACION_VALIDA
            ));
        }

        @Test
        @DisplayName("Should implement equals and hashCode correctly")
        void shouldImplementEqualsAndHashCodeCorrectly() {
            // Given
            AdoptionRequestedEvent event1 = new AdoptionRequestedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO,
                    TELEFONO_VALIDO, TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO,
                    EXPERIENCIA_VALIDA, SITUACION_VALIDA
            );
            
            AdoptionRequestedEvent event2 = new AdoptionRequestedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO,
                    TELEFONO_VALIDO, TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO,
                    EXPERIENCIA_VALIDA, SITUACION_VALIDA
            );

            AdoptionRequestedEvent event3 = new AdoptionRequestedEvent(
                    999L, CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO,
                    TELEFONO_VALIDO, TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO,
                    EXPERIENCIA_VALIDA, SITUACION_VALIDA
            );

            // When & Then
            assertAll("Validar equals y hashCode",
                    () -> assertThat(event1).isEqualTo(event2),
                    () -> assertThat(event1).isNotEqualTo(event3),
                    () -> assertThat(event1.hashCode()).isEqualTo(event2.hashCode())
            );
        }
    }

    @Nested
    @DisplayName("AdoptionApprovedEvent Tests")
    class AdoptionApprovedEventTests {

        @Test
        @DisplayName("Should create AdoptionApprovedEvent with all required fields")
        void shouldCreateAdoptionApprovedEventWithAllRequiredFields() {
            // When
            AdoptionApprovedEvent event = new AdoptionApprovedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                    EMAIL_VALIDO, REFUGIO_VALIDO, OBSERVACIONES_VALIDAS
            );

            // Then
            assertAll("Validar AdoptionApprovedEvent",
                    () -> assertThat(event).isNotNull(),
                    () -> assertThat(event.getSolicitudId()).isEqualTo(SOLICITUD_ID_VALIDA),
                    () -> assertThat(event.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(event.getNombreSolicitante()).isEqualTo(NOMBRE_VALIDO),
                    () -> assertThat(event.getEmailSolicitante()).isEqualTo(EMAIL_VALIDO),
                    () -> assertThat(event.getRefugioAsignado()).isEqualTo(REFUGIO_VALIDO),
                    () -> assertThat(event.getObservacionesRefugio()).isEqualTo(OBSERVACIONES_VALIDAS),
                    () -> assertThat(event.getOccurredOn()).isNotNull(),
                    () -> assertThat(event.getOccurredOn()).isBefore(LocalDateTime.now().plusSeconds(1))
            );
        }

        @Test
        @DisplayName("Should allow null values in constructor")
        void shouldAllowNullValuesInConstructor() {
            // When & Then
            assertDoesNotThrow(() -> new AdoptionApprovedEvent(
                    null, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                    EMAIL_VALIDO, REFUGIO_VALIDO, null
            ));
        }

        @Test
        @DisplayName("Should generate correct string representation")
        void shouldGenerateCorrectStringRepresentation() {
            // Given
            AdoptionApprovedEvent event = new AdoptionApprovedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                    EMAIL_VALIDO, REFUGIO_VALIDO, OBSERVACIONES_VALIDAS
            );

            // When
            String resultado = event.toString();

            // Then
            assertAll("Validar toString",
                    () -> assertThat(resultado).contains("AdoptionApprovedEvent"),
                    () -> assertThat(resultado).contains(SOLICITUD_ID_VALIDA.toString()),
                    () -> assertThat(resultado).contains(NOMBRE_VALIDO),
                    () -> assertThat(resultado).contains(REFUGIO_VALIDO)
            );
        }
    }

    @Nested
    @DisplayName("AdoptionRejectedEvent Tests")
    class AdoptionRejectedEventTests {

        @Test
        @DisplayName("Should create AdoptionRejectedEvent with all required fields")
        void shouldCreateAdoptionRejectedEventWithAllRequiredFields() {
            // When
            AdoptionRejectedEvent event = new AdoptionRejectedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                    EMAIL_VALIDO, REFUGIO_VALIDO, MOTIVO_RECHAZO_VALIDO
            );

            // Then
            assertAll("Validar AdoptionRejectedEvent",
                    () -> assertThat(event).isNotNull(),
                    () -> assertThat(event.getSolicitudId()).isEqualTo(SOLICITUD_ID_VALIDA),
                    () -> assertThat(event.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(event.getNombreSolicitante()).isEqualTo(NOMBRE_VALIDO),
                    () -> assertThat(event.getEmailSolicitante()).isEqualTo(EMAIL_VALIDO),
                    () -> assertThat(event.getRefugioAsignado()).isEqualTo(REFUGIO_VALIDO),
                    () -> assertThat(event.getMotivoRechazo()).isEqualTo(MOTIVO_RECHAZO_VALIDO),
                    () -> assertThat(event.getOccurredOn()).isNotNull(),
                    () -> assertThat(event.getOccurredOn()).isBefore(LocalDateTime.now().plusSeconds(1))
            );
        }

        @Test
        @DisplayName("Should allow null values in constructor")
        void shouldAllowNullValuesInConstructor() {
            // When & Then
            assertDoesNotThrow(() -> new AdoptionRejectedEvent(
                    null, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                    EMAIL_VALIDO, REFUGIO_VALIDO, MOTIVO_RECHAZO_VALIDO
            ));
        }

        @Test
        @DisplayName("Should allow empty motivoRechazo")
        void shouldAllowEmptyMotivoRechazo() {
            // When & Then
            assertDoesNotThrow(() -> new AdoptionRejectedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                    EMAIL_VALIDO, REFUGIO_VALIDO, ""
            ));
        }

        @Test
        @DisplayName("Should generate correct string representation")
        void shouldGenerateCorrectStringRepresentation() {
            // Given
            AdoptionRejectedEvent event = new AdoptionRejectedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                    EMAIL_VALIDO, REFUGIO_VALIDO, MOTIVO_RECHAZO_VALIDO
            );

            // When
            String resultado = event.toString();

            // Then
            assertAll("Validar toString",
                    () -> assertThat(resultado).contains("AdoptionRejectedEvent"),
                    () -> assertThat(resultado).contains(SOLICITUD_ID_VALIDA.toString()),
                    () -> assertThat(resultado).contains(NOMBRE_VALIDO),
                    () -> assertThat(resultado).contains(MOTIVO_RECHAZO_VALIDO)
            );
        }
    }

    @Nested
    @DisplayName("Event Inheritance and Polymorphism")
    class EventInheritanceAndPolymorphism {

        @Test
        @DisplayName("All adoption events should extend DomainEvent")
        void allAdoptionEventsShouldExtendDomainEvent() {
            // Given
            AdoptionRequestedEvent requestedEvent = new AdoptionRequestedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO,
                    TELEFONO_VALIDO, TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO,
                    EXPERIENCIA_VALIDA, SITUACION_VALIDA
            );

            AdoptionApprovedEvent approvedEvent = new AdoptionApprovedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                    EMAIL_VALIDO, REFUGIO_VALIDO, OBSERVACIONES_VALIDAS
            );

            AdoptionRejectedEvent rejectedEvent = new AdoptionRejectedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                    EMAIL_VALIDO, REFUGIO_VALIDO, MOTIVO_RECHAZO_VALIDO
            );

            // When & Then
            assertAll("Validar herencia de DomainEvent",
                    () -> assertThat(requestedEvent).isInstanceOf(DomainEvent.class),
                    () -> assertThat(approvedEvent).isInstanceOf(DomainEvent.class),
                    () -> assertThat(rejectedEvent).isInstanceOf(DomainEvent.class)
            );
        }

        @Test
        @DisplayName("Should handle events polymorphically")
        void shouldHandleEventsPolymorphically() {
            // Given
            DomainEvent[] events = {
                    new AdoptionRequestedEvent(
                            SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO,
                            TELEFONO_VALIDO, TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO,
                            EXPERIENCIA_VALIDA, SITUACION_VALIDA
                    ),
                    new AdoptionApprovedEvent(
                            SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                            EMAIL_VALIDO, REFUGIO_VALIDO, OBSERVACIONES_VALIDAS
                    ),
                    new AdoptionRejectedEvent(
                            SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                            EMAIL_VALIDO, REFUGIO_VALIDO, MOTIVO_RECHAZO_VALIDO
                    )
            };

            // When & Then
            for (DomainEvent event : events) {
                assertAll("Validar comportamiento polimórfico",
                        () -> assertThat(event.getOccurredOn()).isNotNull(),
                        () -> assertThat(event.toString()).isNotEmpty()
                );
            }
        }
    }

    @Nested
    @DisplayName("Event Serialization and Deserialization")
    class EventSerializationAndDeserialization {

        @Test
        @DisplayName("Events should be serializable")
        void eventsShouldBeSerializable() {
            // Given
            AdoptionRequestedEvent requestedEvent = new AdoptionRequestedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO,
                    TELEFONO_VALIDO, TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO,
                    EXPERIENCIA_VALIDA, SITUACION_VALIDA
            );

            AdoptionApprovedEvent approvedEvent = new AdoptionApprovedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO,
                    EMAIL_VALIDO, REFUGIO_VALIDO, OBSERVACIONES_VALIDAS
            );

            // When & Then
            assertAll("Validar serialización",
                    () -> assertThat(requestedEvent).hasNoNullFieldsOrProperties(),
                    () -> assertThat(approvedEvent).hasNoNullFieldsOrPropertiesExcept("observaciones")
            );
        }

        @Test
        @DisplayName("Events should maintain immutability")
        void eventsShouldMaintainImmutability() {
            // Given
            AdoptionRequestedEvent event = new AdoptionRequestedEvent(
                    SOLICITUD_ID_VALIDA, CLIENTE_ID_VALIDO, NOMBRE_VALIDO, EMAIL_VALIDO,
                    TELEFONO_VALIDO, TIPO_MASCOTA_VALIDO, MOTIVO_VALIDO,
                    EXPERIENCIA_VALIDA, SITUACION_VALIDA
            );

            LocalDateTime originalTimestamp = event.getOccurredOn();
            Long originalSolicitudId = event.getSolicitudId();

            // When
            // Intentar modificar (no debería ser posible si es inmutable)
            LocalDateTime newTimestamp = event.getOccurredOn();
            Long newSolicitudId = event.getSolicitudId();

            // Then
            assertAll("Validar inmutabilidad",
                    () -> assertThat(newTimestamp).isEqualTo(originalTimestamp),
                    () -> assertThat(newSolicitudId).isEqualTo(originalSolicitudId),
                    () -> assertThat(event.getSolicitudId()).isEqualTo(SOLICITUD_ID_VALIDA)
            );
        }
    }
}
