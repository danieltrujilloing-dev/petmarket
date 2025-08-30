package com.interview.petmarket.domain.model.carrito;

import com.interview.petmarket.domain.exceptions.InvalidCartDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Carrito Domain Model")
@Tag("unit")
@Tag("domain")
class CarritoTest {

    @Nested
    @DisplayName("Carrito Creation")
    class CarritoCreation {

        @Test
        @DisplayName("Should create empty carrito with valid client ID")
        void shouldCreateEmptyCarrito() {
            // Given
            Long clienteId = 1L;

            // When
            Carrito carrito = Carrito.builder()
                    .withClienteId(clienteId)
                    .build();

            // Then
            assertThat(carrito.getClienteId()).isEqualTo(clienteId);
            assertThat(carrito.estaVacio()).isTrue();
            assertThat(carrito.contarItems()).isEqualTo(0);
        }

        @Test
        @DisplayName("Should throw exception when client ID is null")
        void shouldThrowExceptionWhenClienteIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> 
                Carrito.builder()
                    .withClienteId(null)
                    .build()
            ).isInstanceOf(InvalidCartDataException.class)
             .hasMessageContaining("Client ID");
        }
    }

    @Nested
    @DisplayName("Item Management")
    class ItemManagement {

        private Carrito carrito;
        private final Long clienteId = 1L;

        @BeforeEach
        void setUp() {
            carrito = Carrito.builder()
                    .withClienteId(clienteId)
                    .build();
        }

        @Test
        @DisplayName("Should add item to carrito")
        void shouldAddItemToCarrito() {
            // Given
            Long productoId = 101L;
            int cantidad = 2;

            // When
            carrito.agregarItem(productoId, cantidad);

            // Then
            assertThat(carrito.estaVacio()).isFalse();
            assertThat(carrito.contarItems()).isEqualTo(cantidad);
            assertThat(carrito.contieneProducto(productoId)).isTrue();
        }

        @Test
        @DisplayName("Should update item quantity when adding same product")
        void shouldUpdateItemQuantityWhenAddingSameProduct() {
            // Given
            Long productoId = 101L;

            // When
            carrito.agregarItem(productoId, 2);
            carrito.agregarItem(productoId, 3); // Debe actualizar, no duplicar

            // Then
            assertThat(carrito.contarItems()).isEqualTo(5); // 2 + 3 = 5
            assertThat(carrito.contieneProducto(productoId)).isTrue();
        }

        @Test
        @DisplayName("Should add multiple different products")
        void shouldAddMultipleProducts() {
            // When
            carrito.agregarItem(101L, 2);
            carrito.agregarItem(102L, 1);
            carrito.agregarItem(103L, 3);

            // Then
            assertThat(carrito.contarItems()).isEqualTo(6); // 2 + 1 + 3 = 6
            assertThat(carrito.contieneProducto(101L)).isTrue();
            assertThat(carrito.contieneProducto(102L)).isTrue();
            assertThat(carrito.contieneProducto(103L)).isTrue();
        }

        @Test
        @DisplayName("Should update item quantity")
        void shouldUpdateItemQuantity() {
            // Given
            Long productoId = 101L;
            carrito.agregarItem(productoId, 2);

            // When
            carrito.actualizarCantidadItem(productoId, 5);

            // Then
            assertThat(carrito.contarItems()).isEqualTo(5);
            assertThat(carrito.contieneProducto(productoId)).isTrue();
        }

        @Test
        @DisplayName("Should remove item from carrito")
        void shouldRemoveItemFromCarrito() {
            // Given
            Long productoId1 = 101L;
            Long productoId2 = 102L;
            carrito.agregarItem(productoId1, 2);
            carrito.agregarItem(productoId2, 3);

            // When
            carrito.eliminarItem(productoId1);

            // Then
            assertThat(carrito.contarItems()).isEqualTo(3); // Solo queda producto2
            assertThat(carrito.contieneProducto(productoId1)).isFalse();
            assertThat(carrito.contieneProducto(productoId2)).isTrue();
        }

        @Test
        @DisplayName("Should clear carrito completely")
        void shouldClearCarrito() {
            // Given
            carrito.agregarItem(101L, 2);
            carrito.agregarItem(102L, 3);

            // When
            carrito.limpiarCarrito();

            // Then
            assertThat(carrito.estaVacio()).isTrue();
            assertThat(carrito.contarItems()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -5})
        @DisplayName("Should throw exception when adding item with invalid quantity")
        void shouldThrowExceptionWhenAddingItemWithInvalidQuantity(int invalidQuantity) {
            // Given
            Long clienteId = 1L;
            Long productoId = 101L;
            Carrito carrito = Carrito.builder()
                    .withClienteId(clienteId)
                    .build();

            // When & Then - La validación está en CarritoItem
            assertThatThrownBy(() -> carrito.agregarItem(productoId, invalidQuantity))
                    .isInstanceOf(InvalidCartDataException.class)
                    .hasMessageContaining("positive");
        }

        @Test
        @DisplayName("Should throw exception when adding item with null product ID")
        void shouldThrowExceptionWhenAddingItemWithNullProductId() {
            // Given
            Long clienteId = 1L;
            Carrito carrito = Carrito.builder()
                    .withClienteId(clienteId)
                    .build();

            // When & Then - La validación está en CarritoItem
            assertThatThrownBy(() -> carrito.agregarItem(null, 1))
                    .isInstanceOf(InvalidCartDataException.class)
                    .hasMessageContaining("Product ID");
        }
    }
}
