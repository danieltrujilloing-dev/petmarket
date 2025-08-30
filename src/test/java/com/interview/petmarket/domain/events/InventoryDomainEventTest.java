package com.interview.petmarket.domain.events;

import com.interview.petmarket.domain.model.inventario.PrioridadTarea;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para eventos de dominio de Inventario.
 * Incluye LowStockEvent y StockConfirmedEvent.
 */
@DisplayName("Inventory Domain Events Tests")
class InventoryDomainEventTest {

    @Nested
    @DisplayName("LowStockEvent Tests")
    class LowStockEventTests {

        @Test
        @DisplayName("Debe crear evento LowStock con datos válidos")
        void shouldCreateLowStockEventWithValidData() {
            // Given
            Long productoId = 1L;
            String nombreProducto = "Test Product";
            int stockActual = 5;
            int umbralReposicion = 15;
            int cantidadSugerida = 30;
            PrioridadTarea prioridad = PrioridadTarea.CRITICA;
            String razonActivacion = "Stock below threshold";

            // When
            LowStockEvent event = new LowStockEvent(productoId, nombreProducto, stockActual,
                    umbralReposicion, cantidadSugerida, prioridad, razonActivacion);

            // Then
            assertThat(event.getProductoId()).isEqualTo(productoId);
            assertThat(event.getNombreProducto()).isEqualTo(nombreProducto);
            assertThat(event.getStockActual()).isEqualTo(stockActual);
            assertThat(event.getUmbralReposicion()).isEqualTo(umbralReposicion);
            assertThat(event.getCantidadSugerida()).isEqualTo(cantidadSugerida);
            assertThat(event.getPrioridad()).isEqualTo(prioridad);
            assertThat(event.getRazonActivacion()).isEqualTo(razonActivacion);
            assertThat(event.getEventId()).isNotNull();
            assertThat(event.getOccurredOn()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("Debe identificar stock agotado correctamente")
        void shouldIdentifyStockOutCorrectly() {
            // Given
            LowStockEvent eventStockAgotado = new LowStockEvent(1L, "Out of Stock Product", 0,
                    15, 30, PrioridadTarea.CRITICA, "Stock depleted");

            LowStockEvent eventStockBajo = new LowStockEvent(2L, "Low Stock Product", 5,
                    15, 30, PrioridadTarea.ALTA, "Stock below threshold");

            // When & Then
            assertThat(eventStockAgotado.stockAgotado()).isTrue();
            assertThat(eventStockBajo.stockAgotado()).isFalse();
        }

        @ParameterizedTest
        @CsvSource({
                "0, 20, 0.0",
                "5, 20, 25.0",
                "10, 20, 50.0",
                "15, 20, 75.0",
                "20, 20, 100.0",
                "25, 20, 125.0"
        })
        @DisplayName("Debe calcular porcentaje de stock correctamente")
        void shouldCalculateStockPercentageCorrectly(int stockActual, int umbral, double expectedPercentage) {
            // Given
            LowStockEvent event = new LowStockEvent(1L, "Test Product", stockActual,
                    umbral, 30, PrioridadTarea.MEDIA, "Test");

            // When
            double porcentaje = event.calcularPorcentajeStock();

            // Then
            assertThat(porcentaje).isEqualTo(expectedPercentage);
        }

        @Test
        @DisplayName("Debe manejar umbral cero en cálculo de porcentaje")
        void shouldHandleZeroThresholdInPercentageCalculation() {
            // Given
            LowStockEvent event = new LowStockEvent(1L, "Zero Threshold Product", 10,
                    0, 30, PrioridadTarea.BAJA, "Zero threshold");

            // When
            double porcentaje = event.calcularPorcentajeStock();

            // Then
            assertThat(porcentaje).isEqualTo(100.0);
        }

        @ParameterizedTest
        @EnumSource(value = PrioridadTarea.class, names = {"CRITICA"})
        @DisplayName("Debe identificar eventos críticos")
        void shouldIdentifyCriticalEvents(PrioridadTarea prioridad) {
            // Given
            LowStockEvent event = new LowStockEvent(1L, "Critical Product", 0,
                    15, 30, prioridad, "Critical stock level");

            // When & Then
            assertThat(event.esCritico()).isTrue();
        }

        @ParameterizedTest
        @EnumSource(value = PrioridadTarea.class, names = {"ALTA", "MEDIA", "BAJA"})
        @DisplayName("Eventos no críticos no deben identificarse como críticos")
        void nonCriticalEventsShouldNotBeIdentifiedAsCritical(PrioridadTarea prioridad) {
            // Given
            LowStockEvent event = new LowStockEvent(1L, "Non-Critical Product", 10,
                    15, 30, prioridad, "Non-critical stock level");

            // When & Then
            assertThat(event.esCritico()).isFalse();
        }

        @Test
        @DisplayName("Debe tener representación toString informativa")
        void shouldHaveInformativeToStringRepresentation() {
            // Given
            LowStockEvent event = new LowStockEvent(1L, "Test Product", 5,
                    15, 30, PrioridadTarea.ALTA, "Test reason");

            // When
            String toString = event.toString();

            // Then
            assertThat(toString).contains("LowStockEvent{");
            assertThat(toString).contains("productoId=" + event.getProductoId());
            assertThat(toString).contains("nombreProducto='" + event.getNombreProducto() + "'");
            assertThat(toString).contains("stockActual=" + event.getStockActual());
            assertThat(toString).contains("umbralReposicion=" + event.getUmbralReposicion());
            assertThat(toString).contains("prioridad=" + event.getPrioridad());
            assertThat(toString).contains("porcentajeStock=");
        }

        @Test
        @DisplayName("Debe heredar propiedades de DomainEvent")
        void shouldInheritDomainEventProperties() {
            // Given
            LowStockEvent event = new LowStockEvent(1L, "Test Product", 5,
                    15, 30, PrioridadTarea.ALTA, "Test");

            // When & Then
            assertThat(event.getEventId()).isNotNull();
            assertThat(event.getEventType()).isEqualTo("LowStockEvent");
            assertThat(event.getOccurredOn()).isNotNull();
            assertThat(event.getOccurredOn()).isBefore(LocalDateTime.now().plusSeconds(1));
        }
    }

    @Nested
    @DisplayName("StockConfirmedEvent Tests")
    class StockConfirmedEventTests {

        @Test
        @DisplayName("Debe crear evento StockConfirmed con datos válidos")
        void shouldCreateStockConfirmedEventWithValidData() {
            // Given
            Long pedidoId = 1L;
            Long productoId = 2L;
            String nombreProducto = "Test Product";
            int cantidadConfirmada = 3;
            int stockAnterior = 20;
            int stockActual = 17;
            String motivoConfirmacion = "Payment confirmed";

            // When
            StockConfirmedEvent event = new StockConfirmedEvent(pedidoId, productoId, nombreProducto,
                    cantidadConfirmada, stockAnterior, stockActual, motivoConfirmacion);

            // Then
            assertThat(event.getPedidoId()).isEqualTo(pedidoId);
            assertThat(event.getProductoId()).isEqualTo(productoId);
            assertThat(event.getNombreProducto()).isEqualTo(nombreProducto);
            assertThat(event.getCantidadConfirmada()).isEqualTo(cantidadConfirmada);
            assertThat(event.getStockAnterior()).isEqualTo(stockAnterior);
            assertThat(event.getStockActual()).isEqualTo(stockActual);
            assertThat(event.getMotivoConfirmacion()).isEqualTo(motivoConfirmacion);
            assertThat(event.getEventId()).isNotNull();
            assertThat(event.getOccurredOn()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("Debe calcular diferencia de stock correctamente")
        void shouldCalculateStockDifferenceCorrectly() {
            // Given
            StockConfirmedEvent event = new StockConfirmedEvent(1L, 2L, "Test Product",
                    5, 20, 15, "Test confirmation");

            // When
            int diferencia = event.calcularDiferenciaStock();

            // Then
            assertThat(diferencia).isEqualTo(5); // 20 - 15 = 5
        }

        @ParameterizedTest
        @ValueSource(ints = {0})
        @DisplayName("Debe identificar stock agotado correctamente")
        void shouldIdentifyStockOutCorrectly(int stockActual) {
            // Given
            StockConfirmedEvent eventStockAgotado = new StockConfirmedEvent(1L, 2L, "Out of Stock Product",
                    10, 10, stockActual, "Stock depleted");

            StockConfirmedEvent eventStockDisponible = new StockConfirmedEvent(1L, 2L, "Available Stock Product",
                    5, 15, 10, "Stock available");

            // When & Then
            assertThat(eventStockAgotado.stockAgotado()).isTrue();
            assertThat(eventStockDisponible.stockAgotado()).isFalse();
        }

        @ParameterizedTest
        @CsvSource({
                "5, 20, 25.0",
                "10, 20, 50.0",
                "20, 20, 100.0",
                "0, 10, 0.0"
        })
        @DisplayName("Debe calcular porcentaje de reducción correctamente")
        void shouldCalculateReductionPercentageCorrectly(int cantidadConfirmada, int stockAnterior, double expectedPercentage) {
            // Given
            StockConfirmedEvent event = new StockConfirmedEvent(1L, 2L, "Test Product",
                    cantidadConfirmada, stockAnterior, stockAnterior - cantidadConfirmada, "Test");

            // When
            double porcentaje = event.calcularPorcentajeReduccion();

            // Then
            assertThat(porcentaje).isEqualTo(expectedPercentage);
        }

        @Test
        @DisplayName("Debe manejar stock anterior cero en cálculo de porcentaje")
        void shouldHandleZeroPreviousStockInPercentageCalculation() {
            // Given
            StockConfirmedEvent event = new StockConfirmedEvent(1L, 2L, "Zero Stock Product",
                    5, 0, 0, "Zero previous stock");

            // When
            double porcentaje = event.calcularPorcentajeReduccion();

            // Then
            assertThat(porcentaje).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Debe tener representación toString informativa")
        void shouldHaveInformativeToStringRepresentation() {
            // Given
            StockConfirmedEvent event = new StockConfirmedEvent(1L, 2L, "Test Product",
                    5, 20, 15, "Payment confirmation");

            // When
            String toString = event.toString();

            // Then
            assertThat(toString).contains("StockConfirmedEvent{");
            assertThat(toString).contains("pedidoId=" + event.getPedidoId());
            assertThat(toString).contains("productoId=" + event.getProductoId());
            assertThat(toString).contains("nombreProducto='" + event.getNombreProducto() + "'");
            assertThat(toString).contains("cantidadConfirmada=" + event.getCantidadConfirmada());
            assertThat(toString).contains("stockAnterior=" + event.getStockAnterior());
            assertThat(toString).contains("stockActual=" + event.getStockActual());
            assertThat(toString).contains("porcentajeReduccion=");
        }

        @Test
        @DisplayName("Debe heredar propiedades de DomainEvent")
        void shouldInheritDomainEventProperties() {
            // Given
            StockConfirmedEvent event = new StockConfirmedEvent(1L, 2L, "Test Product",
                    5, 20, 15, "Test confirmation");

            // When & Then
            assertThat(event.getEventId()).isNotNull();
            assertThat(event.getEventType()).isEqualTo("StockConfirmedEvent");
            assertThat(event.getOccurredOn()).isNotNull();
            assertThat(event.getOccurredOn()).isBefore(LocalDateTime.now().plusSeconds(1));
        }
    }

    @Nested
    @DisplayName("Eventos de Dominio - Propiedades Comunes")
    class EventosDominioComunes {

        @Test
        @DisplayName("Eventos deben tener IDs únicos")
        void eventsShouldHaveUniqueIds() {
            // Given
            LowStockEvent event1 = new LowStockEvent(1L, "Product 1", 5, 15, 30, PrioridadTarea.ALTA, "Test 1");
            LowStockEvent event2 = new LowStockEvent(2L, "Product 2", 3, 10, 20, PrioridadTarea.CRITICA, "Test 2");
            
            StockConfirmedEvent event3 = new StockConfirmedEvent(1L, 3L, "Product 3", 2, 10, 8, "Test 3");
            StockConfirmedEvent event4 = new StockConfirmedEvent(2L, 4L, "Product 4", 1, 5, 4, "Test 4");

            // When & Then
            assertThat(event1.getEventId()).isNotEqualTo(event2.getEventId());
            assertThat(event1.getEventId()).isNotEqualTo(event3.getEventId());
            assertThat(event1.getEventId()).isNotEqualTo(event4.getEventId());
            assertThat(event2.getEventId()).isNotEqualTo(event3.getEventId());
            assertThat(event2.getEventId()).isNotEqualTo(event4.getEventId());
            assertThat(event3.getEventId()).isNotEqualTo(event4.getEventId());
        }

        @Test
        @DisplayName("Eventos deben tener timestamps recientes")
        void eventsShouldHaveRecentTimestamps() {
            // Given
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);
            
            LowStockEvent lowStockEvent = new LowStockEvent(1L, "Product", 5, 15, 30, PrioridadTarea.ALTA, "Test");
            StockConfirmedEvent stockConfirmedEvent = new StockConfirmedEvent(1L, 2L, "Product", 3, 10, 7, "Test");
            
            LocalDateTime after = LocalDateTime.now().plusSeconds(1);

            // When & Then
            assertThat(lowStockEvent.getOccurredOn()).isAfter(before);
            assertThat(lowStockEvent.getOccurredOn()).isBefore(after);
            
            assertThat(stockConfirmedEvent.getOccurredOn()).isAfter(before);
            assertThat(stockConfirmedEvent.getOccurredOn()).isBefore(after);
        }

        @Test
        @DisplayName("Eventos deben tener tipos correctos")
        void eventsShouldHaveCorrectTypes() {
            // Given
            LowStockEvent lowStockEvent = new LowStockEvent(1L, "Product", 5, 15, 30, PrioridadTarea.ALTA, "Test");
            StockConfirmedEvent stockConfirmedEvent = new StockConfirmedEvent(1L, 2L, "Product", 3, 10, 7, "Test");

            // When & Then
            assertThat(lowStockEvent.getEventType()).isEqualTo("LowStockEvent");
            assertThat(stockConfirmedEvent.getEventType()).isEqualTo("StockConfirmedEvent");
        }
    }
}
