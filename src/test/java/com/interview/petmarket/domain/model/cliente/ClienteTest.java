package com.interview.petmarket.domain.model.cliente;

import com.interview.petmarket.domain.exceptions.InvalidClientDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Cliente Domain Model")
@Tag("unit")
@Tag("domain")
class ClienteTest {

    @Nested
    @DisplayName("Cliente Creation")
    class ClienteCreation {

        @Test
        @DisplayName("Should create cliente with valid data")
        void shouldCreateClienteWithValidData() {
            // Given
            String nombre = "Juan Pérez";
            String email = "juan.perez@gmail.com";

            // When
            Cliente cliente = Cliente.builder()
                    .withNombre(nombre)
                    .withEmail(email)
                    .build();

            // Then
            assertThat(cliente.getNombre()).isEqualTo(nombre);
            assertThat(cliente.getEmail()).isEqualTo(email);
            assertThat(cliente.getId()).isNull(); // No asignado hasta que se persista
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("Should throw exception when nombre is null or empty")
        void shouldThrowExceptionWhenNombreIsInvalid(String invalidNombre) {
            // Given
            String email = "juan.perez@gmail.com";

            // When & Then
            assertThatThrownBy(() -> 
                Cliente.builder()
                    .withNombre(invalidNombre)
                    .withEmail(email)
                    .build()
            ).isInstanceOf(InvalidClientDataException.class)
             .hasMessageContaining("name");
        }
    }

    @Nested
    @DisplayName("Email Validation")
    class EmailValidation {

        @Test
        @DisplayName("Should throw exception when email is invalid format")
        void shouldThrowExceptionWhenEmailIsInvalid() {
            // Given
            String nombre = "Juan Pérez";

            // When & Then
            assertThatThrownBy(() -> 
                Cliente.builder()
                    .withNombre(nombre)
                    .withEmail("email-invalido")
                    .build()
            ).isInstanceOf(InvalidClientDataException.class)
             .hasMessageContaining("email");
        }

        @Test
        @DisplayName("Should throw exception when email is null")
        void shouldThrowExceptionWhenEmailIsNull() {
            // Given
            String nombre = "Juan Pérez";

            // When & Then
            assertThatThrownBy(() -> 
                Cliente.builder()
                    .withNombre(nombre)
                    .withEmail(null)
                    .build()
            ).isInstanceOf(InvalidClientDataException.class)
             .hasMessageContaining("email");
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "maria@gmail.com",
            "maria.garcia@empresa.co",
            "maria_garcia@test.org",
            "maria123@test-domain.net"
        })
        @DisplayName("Should accept valid email formats")
        void shouldAcceptValidEmailFormats(String validEmail) {
            // Given
            String nombre = "María García";

            // When & Then
            assertThatCode(() -> 
                Cliente.builder()
                    .withNombre(nombre)
                    .withEmail(validEmail)
                    .build()
            ).doesNotThrowAnyException();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "maria@",           // Sin dominio
            "@gmail.com",       // Sin nombre de usuario
            "maria.gmail.com",  // Sin @
            "maria@gmail"       // Sin TLD
        })
        @DisplayName("Should reject invalid email formats")
        void shouldRejectInvalidEmailFormats(String invalidEmail) {
            // Given
            String nombre = "María García";

            // When & Then
            assertThatThrownBy(() -> 
                Cliente.builder()
                    .withNombre(nombre)
                    .withEmail(invalidEmail)
                    .build()
            ).isInstanceOf(InvalidClientDataException.class)
             .hasMessageContaining("email");
        }
    }
}
