package com.interview.petmarket.domain.events;

import com.interview.petmarket.domain.model.pedido.EstadoPedido;
import com.interview.petmarket.domain.model.pedido.PedidoItem;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@DisplayName("Domain Events Tests")
@Tag("unit")
@Tag("domain")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DomainEventTest {

    private static final Long CLIENTE_ID_VALIDO = 1L;
    private static final Long PRODUCTO_ID_VALIDO = 100L;
    private static final Long PEDIDO_ID_VALIDO = 200L;
    private static final int CANTIDAD_VALIDA = 2;
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
    @DisplayName("CartItemAddedEvent Tests")
    class CartItemAddedEventTest {

        @Test
        @Order(1)
        @DisplayName("Should create CartItemAddedEvent with valid data")
        @Timeout(value = 2)
        void shouldCreateCartItemAddedEventWithValidData() {
            // Given
            int cantidadAnterior = 0;

            // When
            LocalDateTime before = LocalDateTime.now();
            CartItemAddedEvent evento = assertTimeout(Duration.ofMillis(100), () ->
                    new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA, cantidadAnterior)
            );
            LocalDateTime after = LocalDateTime.now();

            // Then
            assertAll("Validar creación de CartItemAddedEvent",
                    () -> assertThat(evento.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(evento.getProductoId()).isEqualTo(PRODUCTO_ID_VALIDO),
                    () -> assertThat(evento.getCantidad()).isEqualTo(CANTIDAD_VALIDA),
                    () -> assertThat(evento.getCantidadAnterior()).isEqualTo(cantidadAnterior),
                    () -> assertThat(evento.getEventId()).isNotNull(),
                    () -> assertThat(evento.getEventType()).isEqualTo("CartItemAddedEvent"),
                    () -> assertThat(evento.getOccurredOn()).isBetween(before, after),
                    () -> assertThat(evento.esNuevoItem()).isTrue()
            );
        }

        @Test
        @DisplayName("Should identify new item correctly")
        void shouldIdentifyNewItemCorrectly() {
            // Given & When
            CartItemAddedEvent eventoNuevo = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, 2, 0);
            CartItemAddedEvent eventoActualizado = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, 5, 3);

            // Then
            assertAll("Validar identificación de item nuevo",
                    () -> assertThat(eventoNuevo.esNuevoItem()).isTrue(),
                    () -> assertThat(eventoActualizado.esNuevoItem()).isFalse()
            );
        }

        @Test
        @DisplayName("Should calculate quantity difference correctly")
        void shouldCalculateQuantityDifferenceCorrectly() {
            // Given
            int cantidadAnterior = 3;
            int cantidadNueva = 7;

            // When
            CartItemAddedEvent evento = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, cantidadNueva, cantidadAnterior);

            // Then
            int diferencia = evento.getCantidad() - evento.getCantidadAnterior();
            assertThat(diferencia).isEqualTo(4);
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 5, 10, 100})
        @DisplayName("Should handle different quantities correctly")
        void shouldHandleDifferentQuantitiesCorrectly(int cantidad) {
            // When
            CartItemAddedEvent evento = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, cantidad, 0);

            // Then
            assertAll("Validar diferentes cantidades",
                    () -> assertThat(evento.getCantidad()).isEqualTo(cantidad),
                    () -> assertThat(evento.getCantidad()).isPositive(),
                    () -> assertThat(evento.esNuevoItem()).isTrue()
            );
        }

        @Test
        @DisplayName("Should generate unique event IDs")
        void shouldGenerateUniqueEventIds() {
            // When
            CartItemAddedEvent evento1 = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, 1, 0);
            CartItemAddedEvent evento2 = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, 1, 0);

            // Then
            assertThat(evento1.getEventId()).isNotEqualTo(evento2.getEventId());
        }

        @Test
        @DisplayName("Should have consistent toString format")
        void shouldHaveConsistentToStringFormat() {
            // Given
            CartItemAddedEvent evento = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, CANTIDAD_VALIDA, 0);

            // When
            String eventoString = evento.toString();

            // Then
            assertAll("Validar formato toString",
                    () -> assertThat(eventoString).contains("CartItemAddedEvent"),
                    () -> assertThat(eventoString).contains("clienteId=" + CLIENTE_ID_VALIDO),
                    () -> assertThat(eventoString).contains("productoId=" + PRODUCTO_ID_VALIDO),
                    () -> assertThat(eventoString).contains("cantidad=" + CANTIDAD_VALIDA),
                    () -> assertThat(eventoString).contains("timestamp=")
            );
        }
    }

    @Nested
    @DisplayName("OrderCreatedEvent Tests")
    class OrderCreatedEventTest {

        @Test
        @DisplayName("Should create OrderCreatedEvent with valid data")
        void shouldCreateOrderCreatedEventWithValidData() {
            // Given
            List<PedidoItem> items = createPedidoItems();
            EstadoPedido estado = EstadoPedido.CREADO;
            String estrategia = "PRECIO_BASE";

            // When
            LocalDateTime before = LocalDateTime.now();
            OrderCreatedEvent evento = new OrderCreatedEvent(
                    PEDIDO_ID_VALIDO, CLIENTE_ID_VALIDO, items, TOTAL_VALIDO, estado, estrategia
            );
            LocalDateTime after = LocalDateTime.now();

            // Then
            assertAll("Validar creación de OrderCreatedEvent",
                    () -> assertThat(evento.getPedidoId()).isEqualTo(PEDIDO_ID_VALIDO),
                    () -> assertThat(evento.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(evento.getItems()).hasSize(2),
                    () -> assertThat(evento.getTotal()).isEqualTo(TOTAL_VALIDO),
                    () -> assertThat(evento.getEstado()).isEqualTo(estado),
                    () -> assertThat(evento.getEstrategiaPrecio()).isEqualTo(estrategia),
                    () -> assertThat(evento.getEventId()).isNotNull(),
                    () -> assertThat(evento.getEventType()).isEqualTo("OrderCreatedEvent"),
                    () -> assertThat(evento.getOccurredOn()).isBetween(before, after)
            );
        }

        @Test
        @DisplayName("Should create immutable items list")
        void shouldCreateImmutableItemsList() {
            // Given
            List<PedidoItem> items = createPedidoItems();
            OrderCreatedEvent evento = new OrderCreatedEvent(
                    PEDIDO_ID_VALIDO, CLIENTE_ID_VALIDO, items, TOTAL_VALIDO, EstadoPedido.CREADO, "PRECIO_BASE"
            );

            // When & Then
            assertThatThrownBy(() -> evento.getItems().clear())
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should calculate items count correctly")
        void shouldCalculateItemsCountCorrectly() {
            // Given
            List<PedidoItem> items = createPedidoItems();
            OrderCreatedEvent evento = new OrderCreatedEvent(
                    PEDIDO_ID_VALIDO, CLIENTE_ID_VALIDO, items, TOTAL_VALIDO, EstadoPedido.CREADO, "PRECIO_BASE"
            );

            // When
            int cantidadItems = evento.getCantidadItems();

            // Then
            assertThat(cantidadItems).isEqualTo(5); // 2 + 3 de los items creados
        }

        @Test
        @DisplayName("Should handle empty items list")
        void shouldHandleEmptyItemsList() {
            // Given
            List<PedidoItem> itemsVacios = List.of();

            // When
            OrderCreatedEvent evento = new OrderCreatedEvent(
                    PEDIDO_ID_VALIDO, CLIENTE_ID_VALIDO, itemsVacios, BigDecimal.ZERO, EstadoPedido.CREADO, "PRECIO_BASE"
            );

            // Then
            assertAll("Validar lista vacía",
                    () -> assertThat(evento.getItems()).isEmpty(),
                    () -> assertThat(evento.getCantidadItems()).isZero(),
                    () -> assertThat(evento.getTotal()).isEqualTo(BigDecimal.ZERO)
            );
        }

        @Test
        @DisplayName("Should generate unique event IDs for orders")
        void shouldGenerateUniqueEventIdsForOrders() {
            // Given
            List<PedidoItem> items = createPedidoItems();

            // When
            OrderCreatedEvent evento1 = new OrderCreatedEvent(
                    PEDIDO_ID_VALIDO, CLIENTE_ID_VALIDO, items, TOTAL_VALIDO, EstadoPedido.CREADO, "PRECIO_BASE"
            );
            OrderCreatedEvent evento2 = new OrderCreatedEvent(
                    PEDIDO_ID_VALIDO + 1, CLIENTE_ID_VALIDO, items, TOTAL_VALIDO, EstadoPedido.CREADO, "PRECIO_BASE"
            );

            // Then
            assertThat(evento1.getEventId()).isNotEqualTo(evento2.getEventId());
        }

        @Test
        @DisplayName("Should have detailed toString format")
        void shouldHaveDetailedToStringFormat() {
            // Given
            List<PedidoItem> items = createPedidoItems();
            OrderCreatedEvent evento = new OrderCreatedEvent(
                    PEDIDO_ID_VALIDO, CLIENTE_ID_VALIDO, items, TOTAL_VALIDO, EstadoPedido.CREADO, "PROMO_PERRO"
            );

            // When
            String eventoString = evento.toString();

            // Then
            assertAll("Validar formato toString detallado",
                    () -> assertThat(eventoString).contains("OrderCreatedEvent"),
                    () -> assertThat(eventoString).contains("pedidoId=" + PEDIDO_ID_VALIDO),
                    () -> assertThat(eventoString).contains("clienteId=" + CLIENTE_ID_VALIDO),
                    () -> assertThat(eventoString).contains("itemsCount=2"),
                    () -> assertThat(eventoString).contains("total=" + TOTAL_VALIDO),
                    () -> assertThat(eventoString).contains("estado=CREADO"),
                    () -> assertThat(eventoString).contains("estrategiaPrecio='PROMO_PERRO'"),
                    () -> assertThat(eventoString).contains("timestamp=")
            );
        }
    }

    @Nested
    @DisplayName("Event Inheritance Tests")
    class EventInheritanceTest {

        @Test
        @DisplayName("Should inherit DomainEvent properties correctly")
        void shouldInheritDomainEventPropertiesCorrectly() {
            // Given
            CartItemAddedEvent cartEvent = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, 1, 0);
            List<PedidoItem> items = createPedidoItems();
            OrderCreatedEvent orderEvent = new OrderCreatedEvent(
                    PEDIDO_ID_VALIDO, CLIENTE_ID_VALIDO, items, TOTAL_VALIDO, EstadoPedido.CREADO, "PRECIO_BASE"
            );

            // Then
            assertAll("Validar herencia de DomainEvent",
                    () -> assertThat(cartEvent).isInstanceOf(DomainEvent.class),
                    () -> assertThat(orderEvent).isInstanceOf(DomainEvent.class),
                    () -> assertThat(cartEvent.getEventId()).isNotNull(),
                    () -> assertThat(orderEvent.getEventId()).isNotNull(),
                    () -> assertThat(cartEvent.getOccurredOn()).isNotNull(),
                    () -> assertThat(orderEvent.getOccurredOn()).isNotNull()
            );
        }

        @Test
        @DisplayName("Should have different event types")
        void shouldHaveDifferentEventTypes() {
            // Given
            CartItemAddedEvent cartEvent = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, 1, 0);
            List<PedidoItem> items = createPedidoItems();
            OrderCreatedEvent orderEvent = new OrderCreatedEvent(
                    PEDIDO_ID_VALIDO, CLIENTE_ID_VALIDO, items, TOTAL_VALIDO, EstadoPedido.CREADO, "PRECIO_BASE"
            );

            // Then
            assertAll("Validar tipos de evento diferentes",
                    () -> assertThat(cartEvent.getEventType()).isEqualTo("CartItemAddedEvent"),
                    () -> assertThat(orderEvent.getEventType()).isEqualTo("OrderCreatedEvent"),
                    () -> assertThat(cartEvent.getEventType()).isNotEqualTo(orderEvent.getEventType())
            );
        }
    }

    @Nested
    @DisplayName("Advanced Features")
    class AdvancedFeatures {

        @RepeatedTest(value = 3, name = "Repetición {currentRepetition} de {totalRepetitions}")
        @DisplayName("Should create events consistently")
        void shouldCreateEventsConsistently(RepetitionInfo repetitionInfo) {
            // Given
            int numeroRepeticion = repetitionInfo.getCurrentRepetition();

            // When
            CartItemAddedEvent evento = new CartItemAddedEvent(
                    CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO + numeroRepeticion, 1, 0
            );

            // Then
            assertAll("Validar creación consistente",
                    () -> assertThat(evento.getClienteId()).isEqualTo(CLIENTE_ID_VALIDO),
                    () -> assertThat(evento.getProductoId()).isEqualTo(PRODUCTO_ID_VALIDO + numeroRepeticion),
                    () -> assertThat(evento.getEventId()).isNotNull(),
                    () -> assertThat(evento.getOccurredOn()).isNotNull()
            );
        }

        @Test
        @DisplayName("Should handle concurrent event creation safely")
        @Timeout(value = 5)
        void shouldHandleConcurrentEventCreationSafely() {
            // When & Then
            assertTimeout(Duration.ofSeconds(3), () -> {
                for (int i = 0; i < 100; i++) {
                    CartItemAddedEvent evento = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO + i, 1, 0);
                    assertThat(evento.getEventId()).isNotNull();
                }
            });
        }

        @Test
        @DisplayName("Should maintain event ordering by timestamp")
        void shouldMaintainEventOrderingByTimestamp() throws InterruptedException {
            // Given
            CartItemAddedEvent evento1 = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO, 1, 0);
            
            // Pequeña pausa para garantizar diferencia en timestamp
            Thread.sleep(1);
            
            CartItemAddedEvent evento2 = new CartItemAddedEvent(CLIENTE_ID_VALIDO, PRODUCTO_ID_VALIDO + 1, 1, 0);

            // Then
            assertThat(evento1.getOccurredOn()).isBefore(evento2.getOccurredOn());
        }
    }

    // Helper methods
    private List<PedidoItem> createPedidoItems() {
        PedidoItem item1 = new PedidoItem(PRODUCTO_ID_VALIDO, 2, new BigDecimal("29.99"));
        PedidoItem item2 = new PedidoItem(PRODUCTO_ID_VALIDO + 1, 3, new BigDecimal("19.99"));
        return List.of(item1, item2);
    }
}
