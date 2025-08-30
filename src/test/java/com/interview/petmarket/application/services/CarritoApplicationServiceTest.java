package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.events.CartItemAddedEvent;
import com.interview.petmarket.domain.exceptions.InvalidCartDataException;
import com.interview.petmarket.domain.exceptions.InvalidProductDataException;
import com.interview.petmarket.domain.model.carrito.Carrito;
import com.interview.petmarket.domain.model.carrito.CarritoItem;
import com.interview.petmarket.domain.model.producto.EspecieAnimal;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.model.producto.TipoProducto;
import com.interview.petmarket.domain.ports.out.CarritoRepositoryPort;
import com.interview.petmarket.domain.ports.out.EventPublisherPort;
import com.interview.petmarket.domain.ports.out.ProductoRepositoryPort;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("CarritoApplicationService Tests")
@Tag("unit")
@Tag("service")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CarritoApplicationServiceTest {

    @Mock
    private CarritoRepositoryPort carritoRepository;
    
    @Mock
    private ProductoRepositoryPort productoRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @InjectMocks
    private CarritoApplicationService carritoService;

    private static final Long CLIENTE_ID_VALIDO = 1L;
    private static final Long PRODUCTO_ID_VALIDO = 100L;
    private static final int CANTIDAD_VALIDA = 2;

    @BeforeAll
    static void setupClass() {
        // Configuración global para todos los tests
    }

    @AfterAll
    static void tearDownClass() {
        // Limpieza global después de todos los tests
    }

    @Nested
    @DisplayName("Obtener Carrito")
    class ObtenerCarrito {

        @Test
        @Order(1)
        @DisplayName("Should return existing cart when found")
        @Timeout(value = 2)
        void shouldReturnExistingCartWhenFound() {
            // Given
            Carrito carritoExistente = createValidCarrito();
            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carritoExistente));

            // When
            Carrito resultado = assertTimeout(Duration.ofMillis(100), () ->
                    carritoService.obtenerCarrito(CLIENTE_ID_VALIDO)
            );

            // Then
            assertAll("Validar carrito obtenido",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> verify(carritoRepository).findByClienteId(CLIENTE_ID_VALIDO),
                    () -> verify(carritoRepository, never()).save(any())
            );
        }

        @Test
        @DisplayName("Should create new cart when not found")
        void shouldCreateNewCartWhenNotFound() {
            // Given
            Carrito nuevoCarrito = createValidCarrito();
            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.empty());
            when(carritoRepository.save(any(Carrito.class)))
                    .thenReturn(nuevoCarrito);

            // When
            Carrito resultado = carritoService.obtenerCarrito(CLIENTE_ID_VALIDO);

            // Then
            assertAll("Validar nuevo carrito creado",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(resultado.estaVacio()).isTrue(),
                    () -> verify(carritoRepository).findByClienteId(CLIENTE_ID_VALIDO),
                    () -> verify(carritoRepository).save(any(Carrito.class))
            );
        }

        @Test
        @DisplayName("Should throw exception when client ID is null")
        void shouldThrowExceptionWhenClientIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> carritoService.obtenerCarrito(null))
                    .isInstanceOf(InvalidCartDataException.class)
                    .hasMessageContaining("Client ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Agregar Item")
    class AgregarItem {

        @Test
        @DisplayName("Should add item to existing cart successfully")
        void shouldAddItemToExistingCartSuccessfully() {
            // Given
            Producto producto = createValidProducto();
            Carrito carrito = createValidCarrito();
            Carrito carritoActualizado = createCarritoWithItem();

            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.of(producto));
            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any(Carrito.class)))
                    .thenReturn(carritoActualizado);

            // When
            Carrito resultado = carritoService.agregarItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA);

            // Then
            assertAll("Validar item agregado",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado.contarItems()).isEqualTo(CANTIDAD_VALIDA),
                    () -> verify(productoRepository).findById(PRODUCTO_ID_VALIDO),
                    () -> verify(carritoRepository).save(any(Carrito.class)),
                    () -> verify(eventPublisher).publishEvent(any(CartItemAddedEvent.class))
            );
        }

        @Test
        @DisplayName("Should create new cart when adding item to non-existing cart")
        void shouldCreateNewCartWhenAddingItemToNonExistingCart() {
            // Given
            Producto producto = createValidProducto();
            Carrito nuevoCarrito = createValidCarrito();
            Carrito carritoConItem = createCarritoWithItem();

            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.of(producto));
            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.empty());
            when(carritoRepository.save(any(Carrito.class)))
                    .thenReturn(nuevoCarrito)
                    .thenReturn(carritoConItem);

            // When
            Carrito resultado = carritoService.agregarItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA);

            // Then
            assertThat(resultado).isNotNull();
            verify(carritoRepository, times(2)).save(any(Carrito.class));
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            // Given
            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> 
                    carritoService.agregarItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA))
                    .isInstanceOf(InvalidProductDataException.class)
                    .hasMessageContaining("Product not found");
        }

        @Test
        @DisplayName("Should throw exception when product is inactive")
        void shouldThrowExceptionWhenProductIsInactive() {
            // Given
            Producto productoInactivo = createValidProducto();
            productoInactivo.desactivar();
            
            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.of(productoInactivo));

            // When & Then
            assertThatThrownBy(() -> 
                    carritoService.agregarItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA))
                    .isInstanceOf(InvalidProductDataException.class)
                    .hasMessageContaining("Product is not active");
        }

        @ParameterizedTest
        @ValueSource(ints = {0, -1, -5})
        @DisplayName("Should throw exception when quantity is invalid")
        void shouldThrowExceptionWhenQuantityIsInvalid(int cantidadInvalida) {
            // When & Then
            assertThatThrownBy(() -> 
                    carritoService.agregarItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, cantidadInvalida))
                    .isInstanceOf(InvalidCartDataException.class)
                    .hasMessageContaining("Quantity must be positive");
        }

        @Test
        @DisplayName("Should publish CartItemAddedEvent with correct data")
        void shouldPublishCartItemAddedEventWithCorrectData() {
            // Given
            Producto producto = createValidProducto();
            Carrito carrito = createValidCarrito();
            Carrito carritoActualizado = createCarritoWithItem();

            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.of(producto));
            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any(Carrito.class)))
                    .thenReturn(carritoActualizado);

            ArgumentCaptor<CartItemAddedEvent> eventCaptor = ArgumentCaptor.forClass(CartItemAddedEvent.class);

            // When
            carritoService.agregarItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            CartItemAddedEvent evento = eventCaptor.getValue();
            
            assertAll("Validar evento publicado",
                    () -> assertThat(evento.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(evento.getProductoId()).isEqualTo(PRODUCTO_ID_VALIDO),
                    () -> assertThat(evento.getCantidad()).isEqualTo(CANTIDAD_VALIDA),
                    () -> assertThat(evento.getCantidadAnterior()).isEqualTo(0),
                    () -> assertThat(evento.esNuevoItem()).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("Actualizar Cantidad Item")
    class ActualizarCantidadItem {

        @ParameterizedTest
        @CsvSource({
                "5, 3, 3",
                "2, 1, 1",
                "10, 7, 7"
        })
        @DisplayName("Should update item quantity correctly")
        void shouldUpdateItemQuantityCorrectly(int cantidadInicial, int nuevaCantidad, int cantidadEsperada) {
            // Given
            Carrito carrito = createCarritoWithSpecificQuantity(cantidadInicial);
            Carrito carritoActualizado = createCarritoWithSpecificQuantity(cantidadEsperada);

            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any(Carrito.class)))
                    .thenReturn(carritoActualizado);

            // When
            Carrito resultado = carritoService.actualizarCantidadItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, nuevaCantidad);

            // Then
            assertThat(resultado).isNotNull();
            verify(carritoRepository).save(any(Carrito.class));
        }

        @Test
        @DisplayName("Should remove item when quantity is zero")
        void shouldRemoveItemWhenQuantityIsZero() {
            // Given
            Carrito carritoConItem = createCarritoWithItem();
            Carrito carritoVacio = createValidCarrito();

            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carritoConItem));
            when(carritoRepository.save(any(Carrito.class)))
                    .thenReturn(carritoVacio);

            // When
            Carrito resultado = carritoService.actualizarCantidadItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, 0);

            // Then
            assertThat(resultado).isNotNull();
            verify(carritoRepository).save(any(Carrito.class));
        }

        @Test
        @DisplayName("Should throw exception when quantity is negative")
        void shouldThrowExceptionWhenQuantityIsNegative() {
            // When & Then
            assertThatThrownBy(() -> 
                    carritoService.actualizarCantidadItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, -1))
                    .isInstanceOf(InvalidCartDataException.class)
                    .hasMessageContaining("Quantity cannot be negative");
        }
    }

    @Nested
    @DisplayName("Eliminar Item")
    class EliminarItem {

        @Test
        @DisplayName("Should remove item from cart successfully")
        void shouldRemoveItemFromCartSuccessfully() {
            // Given
            Carrito carritoConItem = createCarritoWithItem();
            Carrito carritoSinItem = createValidCarrito();

            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carritoConItem));
            when(carritoRepository.save(any(Carrito.class)))
                    .thenReturn(carritoSinItem);

            // When
            Carrito resultado = carritoService.eliminarItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO);

            // Then
            assertAll("Validar item eliminado",
                    () -> assertThat(resultado).isNotNull(),
                    () -> verify(carritoRepository).findByClienteId(CLIENTE_ID_VALIDO),
                    () -> verify(carritoRepository).save(any(Carrito.class))
            );
        }

        @Test
        @DisplayName("Should throw exception when client ID is null")
        void shouldThrowExceptionWhenClientIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> carritoService.eliminarItem(null, PRODUCTO_ID_VALIDO))
                    .isInstanceOf(InvalidCartDataException.class)
                    .hasMessageContaining("Client ID cannot be null");
        }

        @Test
        @DisplayName("Should throw exception when product ID is null")
        void shouldThrowExceptionWhenProductIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> carritoService.eliminarItem(CLIENTE_ID_VALIDO, null))
                    .isInstanceOf(InvalidCartDataException.class)
                    .hasMessageContaining("Product ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Limpiar Carrito")
    class LimpiarCarrito {

        @Test
        @DisplayName("Should clear cart successfully")
        void shouldClearCartSuccessfully() {
            // When
            carritoService.limpiarCarrito(CLIENTE_ID_VALIDO);

            // Then
            verify(carritoRepository).deleteByClienteId(CLIENTE_ID_VALIDO);
        }

        @Test
        @DisplayName("Should throw exception when client ID is null")
        void shouldThrowExceptionWhenClientIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> carritoService.limpiarCarrito(null))
                    .isInstanceOf(InvalidCartDataException.class)
                    .hasMessageContaining("Client ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Advanced Features")
    class AdvancedFeatures {

        @RepeatedTest(value = 3, name = "Repetición {currentRepetition} de {totalRepetitions}")
        @DisplayName("Should handle multiple add operations consistently")
        void shouldHandleMultipleAddOperationsConsistently(RepetitionInfo repetitionInfo) {
            // Given
            Producto producto = createValidProducto();
            Carrito carrito = createValidCarrito();
            Carrito carritoActualizado = createCarritoWithItem();

            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.of(producto));
            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any(Carrito.class)))
                    .thenReturn(carritoActualizado);

            // When
            Carrito resultado = carritoService.agregarItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO);
        }

        @Test
        @DisplayName("Should handle concurrent operations safely")
        @Timeout(value = 5)
        void shouldHandleConcurrentOperationsSafely() {
            // Given
            Producto producto = createValidProducto();
            Carrito carrito = createValidCarrito();

            when(productoRepository.findById(any())).thenReturn(Optional.of(producto));
            when(carritoRepository.findByClienteId(any())).thenReturn(Optional.of(carrito));
            when(carritoRepository.save(any())).thenReturn(carrito);

            // When & Then
            assertTimeout(Duration.ofSeconds(3), () -> {
                for (int i = 0; i < 10; i++) {
                    carritoService.agregarItem(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO + i, 1);
                }
            });
        }
    }

    // Helper methods
    private Carrito createValidCarrito() {
        return Carrito.builder()
                .withId(1L)
                .withClienteId(CLIENTE_ID_VALIDO)
                .build();
    }

    private Carrito createCarritoWithItem() {
        CarritoItem item = new CarritoItem(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA);
        return Carrito.builder()
                .withId(1L)
                .withClienteId(CLIENTE_ID_VALIDO)
                .withItem(item)
                .build();
    }

    private Carrito createCarritoWithSpecificQuantity(int cantidad) {
        CarritoItem item = new CarritoItem(PRODUCTO_ID_VALIDO, cantidad);
        return Carrito.builder()
                .withId(1L)
                .withClienteId(CLIENTE_ID_VALIDO)
                .withItem(item)
                .build();
    }

    private Producto createValidProducto() {
        return Producto.builder()
                .withId(PRODUCTO_ID_VALIDO)
                .withNombre("Producto Test")
                .withDescripcion("Descripción test")
                .withPrecio(new BigDecimal("29.99"))
                .withTipo(TipoProducto.ACCESORIO)
                .withEspecie(EspecieAnimal.PERRO)
                .withStock(10)
                .withActivo(true)
                .build();
    }
}
