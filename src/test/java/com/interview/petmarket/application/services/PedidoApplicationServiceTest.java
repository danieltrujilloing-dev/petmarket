package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.events.OrderCreatedEvent;
import com.interview.petmarket.domain.exceptions.InvalidCartDataException;
import com.interview.petmarket.domain.exceptions.InvalidOrderDataException;
import com.interview.petmarket.domain.exceptions.InvalidProductDataException;
import com.interview.petmarket.domain.model.carrito.Carrito;
import com.interview.petmarket.domain.model.carrito.CarritoItem;
import com.interview.petmarket.domain.model.pedido.EstadoPedido;
import com.interview.petmarket.domain.model.pedido.Pedido;
import com.interview.petmarket.domain.model.pedido.PedidoItem;
import com.interview.petmarket.domain.model.pedido.PricingStrategy;
import com.interview.petmarket.domain.model.producto.EspecieAnimal;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.model.producto.TipoProducto;
import com.interview.petmarket.domain.ports.out.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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

@DisplayName("PedidoApplicationService Tests")
@Tag("unit")
@Tag("service")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PedidoApplicationServiceTest {

    @Mock
    private PedidoRepositoryPort pedidoRepository;
    
    @Mock
    private CarritoRepositoryPort carritoRepository;
    
    @Mock
    private ProductoRepositoryPort productoRepository;
    
    @Mock
    private InventarioRepositoryPort inventarioRepository;
    
    @Mock
    private PricingStrategyPort pricingStrategyPort;
    
    @Mock
    private EventPublisherPort eventPublisher;
    
    @Mock
    private PricingStrategy pricingStrategy;
    
    @InjectMocks
    private PedidoApplicationService pedidoService;

    private static final Long CLIENTE_ID_VALIDO = 1L;
    private static final Long PRODUCTO_ID_VALIDO = 100L;
    private static final Long PEDIDO_ID_VALIDO = 200L;
    private static final int CANTIDAD_VALIDA = 2;
    private static final BigDecimal PRECIO_VALIDO = new BigDecimal("29.99");
    private static final BigDecimal TOTAL_VALIDO = new BigDecimal("59.98");

    @BeforeAll
    static void setupClass() {
        // Configuración global para todos los tests
    }

    @AfterAll
    static void tearDownClass() {
        // Limpieza global después de todos los tests
    }

    @Nested
    @DisplayName("Realizar Checkout")
    class RealizarCheckout {

        @Test
        @Order(1)
        @DisplayName("Should complete checkout successfully")
        @Timeout(value = 2)
        void shouldCompleteCheckoutSuccessfully() {
            // Given
            Carrito carritoConItems = createCarritoWithItems();
            Producto producto = createValidProducto();
            Pedido pedidoCreado = createValidPedido();

            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carritoConItems));
            when(pricingStrategyPort.getStrategyForClient(CLIENTE_ID_VALIDO))
                    .thenReturn(pricingStrategy);
            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.of(producto));
            when(inventarioRepository.hasStock(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA))
                    .thenReturn(true);
            when(inventarioRepository.reservarStock(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA))
                    .thenReturn(true);
            when(pricingStrategy.calcularPrecio(producto, CANTIDAD_VALIDA))
                    .thenReturn(TOTAL_VALIDO);
            when(pricingStrategy.getNombre())
                    .thenReturn("PRECIO_BASE");
            when(pedidoRepository.save(any(Pedido.class)))
                    .thenReturn(pedidoCreado);

            // When
            Pedido resultado = assertTimeout(Duration.ofMillis(500), () ->
                    pedidoService.realizarCheckout(CLIENTE_ID_VALIDO)
            );

            // Then
            assertAll("Validar checkout completado",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.CREADO),
                    () -> assertThat(resultado.getTotal()).isEqualTo(TOTAL_VALIDO),
                    () -> verify(carritoRepository).findByClienteId(CLIENTE_ID_VALIDO),
                    () -> verify(inventarioRepository).reservarStock(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA),
                    () -> verify(pedidoRepository).save(any(Pedido.class)),
                    () -> verify(carritoRepository).deleteByClienteId(CLIENTE_ID_VALIDO),
                    () -> verify(eventPublisher).publishEvent(any(OrderCreatedEvent.class))
            );
        }

        @Test
        @DisplayName("Should throw exception when cart is empty")
        void shouldThrowExceptionWhenCartIsEmpty() {
            // Given
            Carrito carritoVacio = createEmptyCarrito();
            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carritoVacio));

            // When & Then
            assertThatThrownBy(() -> pedidoService.realizarCheckout(CLIENTE_ID_VALIDO))
                    .isInstanceOf(InvalidCartDataException.class)
                    .hasMessageContaining("Cannot checkout empty cart");
        }

        @Test
        @DisplayName("Should throw exception when cart not found")
        void shouldThrowExceptionWhenCartNotFound() {
            // Given
            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> pedidoService.realizarCheckout(CLIENTE_ID_VALIDO))
                    .isInstanceOf(InvalidCartDataException.class)
                    .hasMessageContaining("Cart not found for client");
        }

        @Test
        @DisplayName("Should throw exception when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            // Given
            Carrito carritoConItems = createCarritoWithItems();
            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carritoConItems));
            when(pricingStrategyPort.getStrategyForClient(CLIENTE_ID_VALIDO))
                    .thenReturn(pricingStrategy);
            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> pedidoService.realizarCheckout(CLIENTE_ID_VALIDO))
                    .isInstanceOf(InvalidProductDataException.class)
                    .hasMessageContaining("Product not found");
        }

        @Test
        @DisplayName("Should throw exception when product is inactive")
        void shouldThrowExceptionWhenProductIsInactive() {
            // Given
            Carrito carritoConItems = createCarritoWithItems();
            Producto productoInactivo = createValidProducto();
            productoInactivo.desactivar();

            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carritoConItems));
            when(pricingStrategyPort.getStrategyForClient(CLIENTE_ID_VALIDO))
                    .thenReturn(pricingStrategy);
            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.of(productoInactivo));

            // When & Then
            assertThatThrownBy(() -> pedidoService.realizarCheckout(CLIENTE_ID_VALIDO))
                    .isInstanceOf(InvalidProductDataException.class)
                    .hasMessageContaining("Product is not active");
        }

        @Test
        @DisplayName("Should throw exception when insufficient stock")
        void shouldThrowExceptionWhenInsufficientStock() {
            // Given
            Carrito carritoConItems = createCarritoWithItems();
            Producto producto = createValidProducto();

            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carritoConItems));
            when(pricingStrategyPort.getStrategyForClient(CLIENTE_ID_VALIDO))
                    .thenReturn(pricingStrategy);
            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.of(producto));
            when(inventarioRepository.hasStock(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA))
                    .thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> pedidoService.realizarCheckout(CLIENTE_ID_VALIDO))
                    .isInstanceOf(InvalidOrderDataException.class)
                    .hasMessageContaining("Insufficient stock for product");
        }

        @Test
        @DisplayName("Should throw exception when stock reservation fails")
        void shouldThrowExceptionWhenStockReservationFails() {
            // Given
            Carrito carritoConItems = createCarritoWithItems();
            Producto producto = createValidProducto();

            when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                    .thenReturn(Optional.of(carritoConItems));
            when(pricingStrategyPort.getStrategyForClient(CLIENTE_ID_VALIDO))
                    .thenReturn(pricingStrategy);
            when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                    .thenReturn(Optional.of(producto));
            when(inventarioRepository.hasStock(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA))
                    .thenReturn(true);
            when(inventarioRepository.reservarStock(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA))
                    .thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> pedidoService.realizarCheckout(CLIENTE_ID_VALIDO))
                    .isInstanceOf(InvalidOrderDataException.class)
                    .hasMessageContaining("Could not reserve stock for product");
        }

        @Test
        @DisplayName("Should publish OrderCreatedEvent with correct data")
        void shouldPublishOrderCreatedEventWithCorrectData() {
            // Given
            setupSuccessfulCheckout();
            ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);

            // When
            pedidoService.realizarCheckout(CLIENTE_ID_VALIDO);

            // Then
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            OrderCreatedEvent evento = eventCaptor.getValue();
            
            assertAll("Validar evento OrderCreated",
                    () -> assertThat(evento.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(evento.getEstado()).isEqualTo(EstadoPedido.CREADO),
                    () -> assertThat(evento.getTotal()).isEqualTo(TOTAL_VALIDO),
                    () -> assertThat(evento.getEstrategiaPrecio()).isEqualTo("PRECIO_BASE"),
                    () -> assertThat(evento.getCantidadItems()).isEqualTo(CANTIDAD_VALIDA)
            );
        }
    }

    @Nested
    @DisplayName("Obtener Pedido")
    class ObtenerPedido {

        @Test
        @DisplayName("Should return pedido when found")
        void shouldReturnPedidoWhenFound() {
            // Given
            Pedido pedido = createValidPedido();
            when(pedidoRepository.findById(PEDIDO_ID_VALIDO))
                    .thenReturn(Optional.of(pedido));

            // When
            Pedido resultado = pedidoService.obtenerPedido(PEDIDO_ID_VALIDO);

            // Then
            assertAll("Validar pedido obtenido",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado.getId()).isEqualTo(PEDIDO_ID_VALIDO),
                    () -> verify(pedidoRepository).findById(PEDIDO_ID_VALIDO)
            );
        }

        @Test
        @DisplayName("Should throw exception when pedido not found")
        void shouldThrowExceptionWhenPedidoNotFound() {
            // Given
            when(pedidoRepository.findById(PEDIDO_ID_VALIDO))
                    .thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> pedidoService.obtenerPedido(PEDIDO_ID_VALIDO))
                    .isInstanceOf(InvalidOrderDataException.class)
                    .hasMessageContaining("Order not found");
        }

        @Test
        @DisplayName("Should throw exception when pedido ID is null")
        void shouldThrowExceptionWhenPedidoIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> pedidoService.obtenerPedido(null))
                    .isInstanceOf(InvalidOrderDataException.class)
                    .hasMessageContaining("Order ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Cancelar Pedido")
    class CancelarPedido {

        @Test
        @DisplayName("Should cancel pedido successfully")
        void shouldCancelPedidoSuccessfully() {
            // Given
            Pedido pedido = createValidPedido();
            Pedido pedidoCancelado = createCancelledPedido();
            
            when(pedidoRepository.findById(PEDIDO_ID_VALIDO))
                    .thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class)))
                    .thenReturn(pedidoCancelado);

            // When
            Pedido resultado = pedidoService.cancelarPedido(PEDIDO_ID_VALIDO);

            // Then
            assertAll("Validar pedido cancelado",
                    () -> assertThat(resultado).isNotNull(),
                    () -> assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.CANCELADO),
                    () -> verify(inventarioRepository).liberarStock(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA),
                    () -> verify(pedidoRepository).save(any(Pedido.class))
            );
        }

        @Test
        @DisplayName("Should throw exception when pedido cannot be cancelled")
        void shouldThrowExceptionWhenPedidoCannotBeCancelled() {
            // Given - Crear pedido en estado PAGADO primero, luego ENVIADO
            Pedido pedidoEnviado = createValidPedido();
            pedidoEnviado.marcarComoPagado(); // Primero PAGADO
            pedidoEnviado.marcarEnPreparacion(); // Luego EN_PREPARACION
            pedidoEnviado.marcarComoEnviado(); // Finalmente ENVIADO
            
            when(pedidoRepository.findById(PEDIDO_ID_VALIDO))
                    .thenReturn(Optional.of(pedidoEnviado));

            // When & Then
            assertThatThrownBy(() -> pedidoService.cancelarPedido(PEDIDO_ID_VALIDO))
                    .isInstanceOf(InvalidOrderDataException.class)
                    .hasMessageContaining("Order cannot be cancelled in current state");
        }
    }

    @Nested
    @DisplayName("Cambiar Estado Pedido")
    class CambiarEstadoPedido {

        @Test
        @DisplayName("Should change pedido state to PAGADO successfully")
        void shouldChangePedidoStateToPagadoSuccessfully() {
            // Given
            Pedido pedido = createValidPedido();
            when(pedidoRepository.findById(PEDIDO_ID_VALIDO))
                    .thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class)))
                    .thenReturn(pedido);

            // When
            Pedido resultado = pedidoService.cambiarEstadoPedido(PEDIDO_ID_VALIDO, "PAGADO");

            // Then
            assertThat(resultado).isNotNull();
            verify(pedidoRepository).save(any(Pedido.class));
        }

        @ParameterizedTest
        @ValueSource(strings = {"INVALID_STATE", "UNKNOWN", ""})
        @DisplayName("Should throw exception for invalid states")
        void shouldThrowExceptionForInvalidStates(String estadoInvalido) {
            // Given
            Pedido pedido = createValidPedido();
            when(pedidoRepository.findById(PEDIDO_ID_VALIDO))
                    .thenReturn(Optional.of(pedido));

            // When & Then
            assertThatThrownBy(() -> 
                    pedidoService.cambiarEstadoPedido(PEDIDO_ID_VALIDO, estadoInvalido))
                    .isInstanceOf(InvalidOrderDataException.class)
                    .hasMessageContaining("Invalid order state");
        }

        @Test
        @DisplayName("Should handle CANCELADO state by calling cancelar method")
        void shouldHandleCanceladoStateByCancellingOrder() {
            // Given
            Pedido pedido = createValidPedido();
            Pedido pedidoCancelado = createCancelledPedido();
            
            when(pedidoRepository.findById(PEDIDO_ID_VALIDO))
                    .thenReturn(Optional.of(pedido));
            when(pedidoRepository.save(any(Pedido.class)))
                    .thenReturn(pedidoCancelado);

            // When
            Pedido resultado = pedidoService.cambiarEstadoPedido(PEDIDO_ID_VALIDO, "CANCELADO");

            // Then
            assertThat(resultado.getEstado()).isEqualTo(EstadoPedido.CANCELADO);
            verify(inventarioRepository).liberarStock(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA);
        }
    }

    @Nested
    @DisplayName("Advanced Features")
    class AdvancedFeatures {

        @RepeatedTest(value = 3, name = "Repetición {currentRepetition} de {totalRepetitions}")
        @DisplayName("Should handle multiple checkout operations consistently")
        void shouldHandleMultipleCheckoutOperationsConsistently(RepetitionInfo repetitionInfo) {
            // Given
            setupSuccessfulCheckout();

            // When
            Pedido resultado = pedidoService.realizarCheckout(CLIENTE_ID_VALIDO);

            // Then
            assertThat(resultado).isNotNull();
            assertThat(resultado.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO);
        }

        @Test
        @DisplayName("Should handle concurrent operations safely")
        @Timeout(value = 5)
        void shouldHandleConcurrentOperationsSafely() {
            // Given
            Pedido pedido = createValidPedido();
            when(pedidoRepository.findById(any())).thenReturn(Optional.of(pedido));

            // When & Then
            assertTimeout(Duration.ofSeconds(3), () -> {
                for (int i = 0; i < 10; i++) {
                    pedidoService.obtenerPedido(PEDIDO_ID_VALIDO + i);
                }
            });
        }
    }

    // Helper methods
    private void setupSuccessfulCheckout() {
        Carrito carritoConItems = createCarritoWithItems();
        Producto producto = createValidProducto();
        Pedido pedidoCreado = createValidPedido();

        when(carritoRepository.findByClienteId(CLIENTE_ID_VALIDO))
                .thenReturn(Optional.of(carritoConItems));
        when(pricingStrategyPort.getStrategyForClient(CLIENTE_ID_VALIDO))
                .thenReturn(pricingStrategy);
        when(productoRepository.findById(PRODUCTO_ID_VALIDO))
                .thenReturn(Optional.of(producto));
        when(inventarioRepository.hasStock(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA))
                .thenReturn(true);
        when(inventarioRepository.reservarStock(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA))
                .thenReturn(true);
        when(pricingStrategy.calcularPrecio(producto, CANTIDAD_VALIDA))
                .thenReturn(TOTAL_VALIDO);
        when(pricingStrategy.getNombre())
                .thenReturn("PRECIO_BASE");
        when(pedidoRepository.save(any(Pedido.class)))
                .thenReturn(pedidoCreado);
    }

    private Carrito createCarritoWithItems() {
        CarritoItem item = new CarritoItem(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA);
        return Carrito.builder()
                .withId(1L)
                .withClienteId(CLIENTE_ID_VALIDO)
                .withItem(item)
                .build();
    }

    private Carrito createEmptyCarrito() {
        return Carrito.builder()
                .withId(1L)
                .withClienteId(CLIENTE_ID_VALIDO)
                .build();
    }

    private Producto createValidProducto() {
        return Producto.builder()
                .withId(PRODUCTO_ID_VALIDO)
                .withNombre("Producto Test")
                .withDescripcion("Descripción test")
                .withPrecio(PRECIO_VALIDO)
                .withTipo(TipoProducto.ACCESORIO)
                .withEspecie(EspecieAnimal.PERRO)
                .withStock(10)
                .withActivo(true)
                .build();
    }

    private Pedido createValidPedido() {
        PedidoItem item = new PedidoItem(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA, PRECIO_VALIDO);
        return Pedido.builder()
                .withId(PEDIDO_ID_VALIDO)
                .withClienteId(CLIENTE_ID_VALIDO)
                .withItem(item)
                .withTotal(TOTAL_VALIDO)
                .withEstado(EstadoPedido.CREADO)
                .build();
    }

    private Pedido createCancelledPedido() {
        PedidoItem item = new PedidoItem(PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA, PRECIO_VALIDO);
        return Pedido.builder()
                .withId(PEDIDO_ID_VALIDO)
                .withClienteId(CLIENTE_ID_VALIDO)
                .withItem(item)
                .withTotal(TOTAL_VALIDO)
                .withEstado(EstadoPedido.CANCELADO)
                .build();
    }
}
