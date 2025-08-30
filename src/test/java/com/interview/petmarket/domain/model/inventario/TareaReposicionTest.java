package com.interview.petmarket.domain.model.inventario;

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
 * Tests unitarios para TareaReposicion.
 * Sigue el patrón establecido con JUnit 5 y AssertJ.
 */
@DisplayName("TareaReposicion Tests")
class TareaReposicionTest {

    @Nested
    @DisplayName("Construcción de TareaReposicion")
    class ConstruccionTareaReposicion {

        @Test
        @DisplayName("Debe crear tarea con datos válidos")
        void shouldCreateTaskWithValidData() {
            // Given
            Long productoId = 1L;
            String nombreProducto = "Test Product";
            int stockActual = 5;
            int umbralReposicion = 15;
            int cantidadSugerida = 30;

            // When
            TareaReposicion tarea = TareaReposicion.builder()
                    .withProductoId(productoId)
                    .withNombreProducto(nombreProducto)
                    .withStockActual(stockActual)
                    .withUmbralReposicion(umbralReposicion)
                    .withCantidadSugerida(cantidadSugerida)
                    .build();

            // Then
            assertThat(tarea.getProductoId()).isEqualTo(productoId);
            assertThat(tarea.getNombreProducto()).isEqualTo(nombreProducto);
            assertThat(tarea.getStockActual()).isEqualTo(stockActual);
            assertThat(tarea.getUmbralReposicion()).isEqualTo(umbralReposicion);
            assertThat(tarea.getCantidadSugerida()).isEqualTo(cantidadSugerida);
            assertThat(tarea.getEstado()).isEqualTo(EstadoTarea.PENDIENTE);
            assertThat(tarea.getFechaVencimiento()).isAfter(LocalDateTime.now());
        }

        @Test
        @DisplayName("Debe asignar estado PENDIENTE por defecto")
        void shouldAssignPendingStateByDefault() {
            // When
            TareaReposicion tarea = createValidTarea();

            // Then
            assertThat(tarea.getEstado()).isEqualTo(EstadoTarea.PENDIENTE);
        }

        @Test
        @DisplayName("Debe calcular prioridad automáticamente basada en stock")
        void shouldCalculatePriorityAutomaticallyBasedOnStock() {
            // Given & When
            TareaReposicion tareaCritica = TareaReposicion.builder()
                    .withProductoId(1L)
                    .withNombreProducto("Critical Product")
                    .withStockActual(0) // Stock agotado
                    .withUmbralReposicion(20)
                    .withCantidadSugerida(40)
                    .build();

            TareaReposicion tareaAlta = TareaReposicion.builder()
                    .withProductoId(2L)
                    .withNombreProducto("High Priority Product")
                    .withStockActual(8) // 40% del umbral
                    .withUmbralReposicion(20)
                    .withCantidadSugerida(40)
                    .build();

            TareaReposicion tareaMedia = TareaReposicion.builder()
                    .withProductoId(3L)
                    .withNombreProducto("Medium Priority Product")
                    .withStockActual(12) // 60% del umbral
                    .withUmbralReposicion(20)
                    .withCantidadSugerida(40)
                    .build();

            TareaReposicion tareaBaja = TareaReposicion.builder()
                    .withProductoId(4L)
                    .withNombreProducto("Low Priority Product")
                    .withStockActual(18) // 90% del umbral
                    .withUmbralReposicion(20)
                    .withCantidadSugerida(40)
                    .build();

            // Then
            assertThat(tareaCritica.getPrioridad()).isEqualTo(PrioridadTarea.CRITICA);
            assertThat(tareaAlta.getPrioridad()).isEqualTo(PrioridadTarea.ALTA);
            assertThat(tareaMedia.getPrioridad()).isEqualTo(PrioridadTarea.MEDIA);
            assertThat(tareaBaja.getPrioridad()).isEqualTo(PrioridadTarea.BAJA);
        }

        @ParameterizedTest
        @CsvSource({
                ", Product Name, 5, 15, 30, Product ID cannot be null",
                "1, , 5, 15, 30, Product name cannot be null or empty",
                "1, '   ', 5, 15, 30, Product name cannot be null or empty",
                "1, Product Name, -1, 15, 30, Current stock cannot be negative",
                "1, Product Name, 5, -1, 30, Replenishment threshold cannot be negative",
                "1, Product Name, 5, 15, 0, Suggested quantity must be positive",
                "1, Product Name, 5, 15, -5, Suggested quantity must be positive"
        })
        @DisplayName("Debe lanzar excepción para datos inválidos")
        void shouldThrowExceptionForInvalidData(String productoIdStr, String nombreProducto, 
                int stockActual, int umbralReposicion, int cantidadSugerida, String expectedMessage) {
            // Given
            Long productoId = productoIdStr != null && !productoIdStr.isEmpty() ? Long.valueOf(productoIdStr) : null;

            // When & Then
            assertThatThrownBy(() -> TareaReposicion.builder()
                    .withProductoId(productoId)
                    .withNombreProducto(nombreProducto)
                    .withStockActual(stockActual)
                    .withUmbralReposicion(umbralReposicion)
                    .withCantidadSugerida(cantidadSugerida)
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(expectedMessage);
        }
    }

    @Nested
    @DisplayName("Cambios de Estado")
    class CambiosEstado {

        @Test
        @DisplayName("Debe marcar tarea como en proceso")
        void shouldMarkTaskAsInProgress() {
            // Given
            TareaReposicion tarea = createValidTarea();

            // When
            TareaReposicion tareaActualizada = tarea.marcarEnProceso();

            // Then
            assertThat(tareaActualizada.getEstado()).isEqualTo(EstadoTarea.EN_PROCESO);
            assertThat(tareaActualizada.getProductoId()).isEqualTo(tarea.getProductoId());
            assertThat(tareaActualizada.getNombreProducto()).isEqualTo(tarea.getNombreProducto());
        }

        @Test
        @DisplayName("Debe marcar tarea como completada")
        void shouldMarkTaskAsCompleted() {
            // Given
            TareaReposicion tarea = createValidTarea();
            String observacionesFinales = "Reposición completada exitosamente";

            // When
            TareaReposicion tareaCompletada = tarea.marcarCompletada(observacionesFinales);

            // Then
            assertThat(tareaCompletada.getEstado()).isEqualTo(EstadoTarea.COMPLETADA);
            assertThat(tareaCompletada.getObservaciones()).isEqualTo(observacionesFinales);
        }

        @Test
        @DisplayName("Debe marcar como completada con observaciones null")
        void shouldMarkAsCompletedWithNullObservations() {
            // Given
            TareaReposicion tarea = createValidTarea();

            // When
            TareaReposicion tareaCompletada = tarea.marcarCompletada(null);

            // Then
            assertThat(tareaCompletada.getEstado()).isEqualTo(EstadoTarea.COMPLETADA);
            assertThat(tareaCompletada.getObservaciones()).isEqualTo(tarea.getObservaciones());
        }

        @Test
        @DisplayName("No debe permitir cambiar estado de tarea completada")
        void shouldNotAllowStateChangeOfCompletedTask() {
            // Given
            TareaReposicion tareaCompletada = TareaReposicion.builder()
                    .withProductoId(1L)
                    .withNombreProducto("Completed Task")
                    .withStockActual(5)
                    .withUmbralReposicion(15)
                    .withCantidadSugerida(30)
                    .withEstado(EstadoTarea.COMPLETADA)
                    .build();

            // When & Then
            assertThatThrownBy(() -> tareaCompletada.marcarEnProceso())
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Cannot change state of completed task");
        }
    }

    @Nested
    @DisplayName("Métodos de Consulta")
    class MetodosConsulta {

        @Test
        @DisplayName("Debe identificar si tarea está vencida")
        void shouldIdentifyIfTaskIsOverdue() {
            // Given
            TareaReposicion tareaVencida = TareaReposicion.builder()
                    .withProductoId(1L)
                    .withNombreProducto("Overdue Task")
                    .withStockActual(5)
                    .withUmbralReposicion(15)
                    .withCantidadSugerida(30)
                    .withFechaVencimiento(LocalDateTime.now().minusHours(1))
                    .build();

            TareaReposicion tareaNoVencida = TareaReposicion.builder()
                    .withProductoId(2L)
                    .withNombreProducto("Not Overdue Task")
                    .withStockActual(5)
                    .withUmbralReposicion(15)
                    .withCantidadSugerida(30)
                    .withFechaVencimiento(LocalDateTime.now().plusHours(1))
                    .build();

            // When & Then
            assertThat(tareaVencida.estaVencida()).isTrue();
            assertThat(tareaNoVencida.estaVencida()).isFalse();
        }

        @Test
        @DisplayName("Tarea completada no debe considerarse vencida")
        void completedTaskShouldNotBeConsideredOverdue() {
            // Given
            TareaReposicion tareaCompletada = TareaReposicion.builder()
                    .withProductoId(1L)
                    .withNombreProducto("Completed Task")
                    .withStockActual(5)
                    .withUmbralReposicion(15)
                    .withCantidadSugerida(30)
                    .withEstado(EstadoTarea.COMPLETADA)
                    .withFechaVencimiento(LocalDateTime.now().minusHours(1))
                    .build();

            // When & Then
            assertThat(tareaCompletada.estaVencida()).isFalse();
        }

        @ParameterizedTest
        @EnumSource(value = PrioridadTarea.class, names = {"CRITICA"})
        @DisplayName("Debe identificar tareas críticas")
        void shouldIdentifyCriticalTasks(PrioridadTarea prioridad) {
            // Given
            TareaReposicion tarea = TareaReposicion.builder()
                    .withProductoId(1L)
                    .withNombreProducto("Critical Task")
                    .withStockActual(0) // Stock agotado = crítica
                    .withUmbralReposicion(15)
                    .withCantidadSugerida(30)
                    .build();

            // When & Then
            assertThat(tarea.esCritica()).isTrue();
            assertThat(tarea.getPrioridad()).isEqualTo(PrioridadTarea.CRITICA);
        }

        @ParameterizedTest
        @EnumSource(value = PrioridadTarea.class, names = {"ALTA", "MEDIA", "BAJA"})
        @DisplayName("Tareas no críticas no deben identificarse como críticas")
        void nonCriticalTasksShouldNotBeIdentifiedAsCritical(PrioridadTarea prioridad) {
            // Given
            TareaReposicion tarea = TareaReposicion.builder()
                    .withProductoId(1L)
                    .withNombreProducto("Non-Critical Task")
                    .withStockActual(10)
                    .withUmbralReposicion(15)
                    .withCantidadSugerida(30)
                    .withPrioridad(prioridad)
                    .build();

            // When & Then
            assertThat(tarea.esCritica()).isFalse();
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
            TareaReposicion tarea = TareaReposicion.builder()
                    .withProductoId(1L)
                    .withNombreProducto("Test Task")
                    .withStockActual(stockActual)
                    .withUmbralReposicion(umbral)
                    .withCantidadSugerida(30)
                    .build();

            // When
            double porcentaje = tarea.calcularPorcentajeStock();

            // Then
            assertThat(porcentaje).isEqualTo(expectedPercentage);
        }

        @Test
        @DisplayName("Debe manejar umbral cero en cálculo de porcentaje")
        void shouldHandleZeroThresholdInPercentageCalculation() {
            // Given
            TareaReposicion tarea = TareaReposicion.builder()
                    .withProductoId(1L)
                    .withNombreProducto("Zero Threshold Task")
                    .withStockActual(10)
                    .withUmbralReposicion(0)
                    .withCantidadSugerida(30)
                    .build();

            // When
            double porcentaje = tarea.calcularPorcentajeStock();

            // Then
            assertThat(porcentaje).isEqualTo(100.0);
        }
    }

    @Nested
    @DisplayName("Fechas de Vencimiento")
    class FechasVencimiento {

        @Test
        @DisplayName("Debe asignar fecha de vencimiento basada en prioridad crítica")
        void shouldAssignDueDateBasedOnCriticalPriority() {
            // Given
            LocalDateTime before = LocalDateTime.now();
            
            TareaReposicion tareaCritica = TareaReposicion.builder()
                    .withProductoId(1L)
                    .withNombreProducto("Critical Task")
                    .withStockActual(0) // Crítica
                    .withUmbralReposicion(20)
                    .withCantidadSugerida(40)
                    .build();
            
            LocalDateTime after = LocalDateTime.now().plusHours(5);

            // Then
            assertThat(tareaCritica.getFechaVencimiento()).isAfter(before);
            assertThat(tareaCritica.getFechaVencimiento()).isBefore(after); // Crítica = 4 horas
        }

        @Test
        @DisplayName("Debe permitir fecha de vencimiento personalizada")
        void shouldAllowCustomDueDate() {
            // Given
            LocalDateTime fechaPersonalizada = LocalDateTime.now().plusDays(7);
            
            TareaReposicion tarea = TareaReposicion.builder()
                    .withProductoId(1L)
                    .withNombreProducto("Custom Due Date Task")
                    .withStockActual(10)
                    .withUmbralReposicion(20)
                    .withCantidadSugerida(40)
                    .withFechaVencimiento(fechaPersonalizada)
                    .build();

            // Then
            assertThat(tarea.getFechaVencimiento()).isEqualTo(fechaPersonalizada);
        }
    }

    @Nested
    @DisplayName("ToString y Representación")
    class ToStringYRepresentacion {

        @Test
        @DisplayName("Debe tener representación toString informativa")
        void shouldHaveInformativeToStringRepresentation() {
            // Given
            TareaReposicion tarea = createValidTarea();

            // When
            String toString = tarea.toString();

            // Then
            assertThat(toString).contains("TareaReposicion{");
            assertThat(toString).contains("productoId=" + tarea.getProductoId());
            assertThat(toString).contains("nombreProducto='" + tarea.getNombreProducto() + "'");
            assertThat(toString).contains("stockActual=" + tarea.getStockActual());
            assertThat(toString).contains("estado=" + tarea.getEstado());
            assertThat(toString).contains("prioridad=" + tarea.getPrioridad());
        }
    }

    // Método helper para crear tarea válida
    private TareaReposicion createValidTarea() {
        return TareaReposicion.builder()
                .withProductoId(1L)
                .withNombreProducto("Test Product")
                .withStockActual(5)
                .withUmbralReposicion(15)
                .withCantidadSugerida(30)
                .withObservaciones("Test observations")
                .build();
    }
}
