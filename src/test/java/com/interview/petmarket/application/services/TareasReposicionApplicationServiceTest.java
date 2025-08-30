package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.exceptions.InvalidTaskDataException;
import com.interview.petmarket.domain.model.inventario.EstadoTarea;
import com.interview.petmarket.domain.model.inventario.PrioridadTarea;
import com.interview.petmarket.domain.model.inventario.TareaReposicion;
import com.interview.petmarket.domain.ports.in.GestionarTareasReposicionUseCase.EstadisticasTareas;
import com.interview.petmarket.domain.ports.out.TareaReposicionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para TareasReposicionApplicationService.
 * Sigue el patrón establecido con JUnit 5, Mockito y AssertJ.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TareasReposicionApplicationService Tests")
class TareasReposicionApplicationServiceTest {

    @Mock
    private TareaReposicionRepositoryPort tareaRepository;

    private TareasReposicionApplicationService tareasService;

    @BeforeEach
    void setUp() {
        tareasService = new TareasReposicionApplicationService(tareaRepository);
    }

    @Nested
    @DisplayName("Crear Tarea de Reposición")
    class CrearTareaReposicion {

        @Test
        @DisplayName("Debe crear tarea exitosamente")
        void shouldCreateTaskSuccessfully() {
            // Given
            Long productoId = 1L;
            String nombreProducto = "Test Product";
            int stockActual = 5;
            int umbralReposicion = 15;
            int cantidadSugerida = 30;

            TareaReposicion expectedTarea = createTareaReposicion(1L, productoId, nombreProducto, 
                    stockActual, umbralReposicion, cantidadSugerida, EstadoTarea.PENDIENTE, PrioridadTarea.CRITICA);

            when(tareaRepository.existsActivaByProductoId(productoId)).thenReturn(false);
            when(tareaRepository.save(any(TareaReposicion.class))).thenReturn(expectedTarea);

            // When
            TareaReposicion result = tareasService.crearTareaReposicion(productoId, nombreProducto, 
                    stockActual, umbralReposicion, cantidadSugerida);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getProductoId()).isEqualTo(productoId);
            assertThat(result.getNombreProducto()).isEqualTo(nombreProducto);
            assertThat(result.getStockActual()).isEqualTo(stockActual);
            assertThat(result.getEstado()).isEqualTo(EstadoTarea.PENDIENTE);
            
            verify(tareaRepository).existsActivaByProductoId(productoId);
            verify(tareaRepository).save(any(TareaReposicion.class));
        }

        @Test
        @DisplayName("Debe retornar tarea existente cuando ya hay una activa")
        void shouldReturnExistingTaskWhenActiveTaskExists() {
            // Given
            Long productoId = 1L;
            TareaReposicion tareaExistente = createTareaReposicion(1L, productoId, "Existing Task", 
                    5, 15, 30, EstadoTarea.PENDIENTE, PrioridadTarea.CRITICA);

            when(tareaRepository.existsActivaByProductoId(productoId)).thenReturn(true);
            when(tareaRepository.findActivasByProductoId(productoId)).thenReturn(List.of(tareaExistente));

            // When
            TareaReposicion result = tareasService.crearTareaReposicion(productoId, "New Task", 
                    3, 15, 30);

            // Then
            assertThat(result).isEqualTo(tareaExistente);
            verify(tareaRepository, never()).save(any(TareaReposicion.class));
        }

        @ParameterizedTest
        @CsvSource({
                ", Test Product, 5, 15, 30, Product ID cannot be null",
                "1, , 5, 15, 30, Product name cannot be null or empty",
                "1, '   ', 5, 15, 30, Product name cannot be null or empty",
                "1, Test Product, -1, 15, 30, Current stock cannot be negative",
                "1, Test Product, 5, -1, 30, Replenishment threshold cannot be negative",
                "1, Test Product, 5, 15, 0, Suggested quantity must be positive",
                "1, Test Product, 5, 15, -5, Suggested quantity must be positive"
        })
        @DisplayName("Debe lanzar excepción para datos inválidos")
        void shouldThrowExceptionForInvalidData(String productoIdStr, String nombreProducto, 
                int stockActual, int umbralReposicion, int cantidadSugerida, String expectedMessage) {
            // Given
            Long productoId = productoIdStr != null && !productoIdStr.isEmpty() ? Long.valueOf(productoIdStr) : null;

            // When & Then
            assertThatThrownBy(() -> tareasService.crearTareaReposicion(productoId, nombreProducto, 
                    stockActual, umbralReposicion, cantidadSugerida))
                    .isInstanceOf(InvalidTaskDataException.class)
                    .hasMessageContaining(expectedMessage);
        }
    }

    @Nested
    @DisplayName("Obtener Tarea")
    class ObtenerTarea {

        @Test
        @DisplayName("Debe obtener tarea exitosamente")
        void shouldGetTaskSuccessfully() {
            // Given
            Long tareaId = 1L;
            TareaReposicion expectedTarea = createTareaReposicion(tareaId, 1L, "Test Product", 
                    5, 15, 30, EstadoTarea.PENDIENTE, PrioridadTarea.ALTA);

            when(tareaRepository.findById(tareaId)).thenReturn(Optional.of(expectedTarea));

            // When
            TareaReposicion result = tareasService.obtenerTarea(tareaId);

            // Then
            assertThat(result).isEqualTo(expectedTarea);
            verify(tareaRepository).findById(tareaId);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando tarea no existe")
        void shouldThrowExceptionWhenTaskNotFound() {
            // Given
            Long tareaId = 999L;
            when(tareaRepository.findById(tareaId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> tareasService.obtenerTarea(tareaId))
                    .isInstanceOf(InvalidTaskDataException.class)
                    .hasMessageContaining("Task not found: " + tareaId);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando ID es null")
        void shouldThrowExceptionWhenIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> tareasService.obtenerTarea(null))
                    .isInstanceOf(InvalidTaskDataException.class)
                    .hasMessageContaining("Task ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Obtener Tareas por Estado")
    class ObtenerTareasPorEstado {

        @ParameterizedTest
        @EnumSource(EstadoTarea.class)
        @DisplayName("Debe obtener tareas por estado")
        void shouldGetTasksByState(EstadoTarea estado) {
            // Given
            List<TareaReposicion> expectedTareas = List.of(
                    createTareaReposicion(1L, 1L, "Task 1", 5, 15, 30, estado, PrioridadTarea.ALTA),
                    createTareaReposicion(2L, 2L, "Task 2", 8, 20, 40, estado, PrioridadTarea.MEDIA)
            );

            when(tareaRepository.findByEstado(estado)).thenReturn(expectedTareas);

            // When
            List<TareaReposicion> result = tareasService.obtenerTareasPorEstado(estado);

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(expectedTareas);
            verify(tareaRepository).findByEstado(estado);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando estado es null")
        void shouldThrowExceptionWhenStateIsNull() {
            // When & Then
            assertThatThrownBy(() -> tareasService.obtenerTareasPorEstado(null))
                    .isInstanceOf(InvalidTaskDataException.class)
                    .hasMessageContaining("Task state cannot be null");
        }
    }

    @Nested
    @DisplayName("Obtener Tareas por Prioridad")
    class ObtenerTareasPorPrioridad {

        @ParameterizedTest
        @EnumSource(PrioridadTarea.class)
        @DisplayName("Debe obtener tareas por prioridad")
        void shouldGetTasksByPriority(PrioridadTarea prioridad) {
            // Given
            List<TareaReposicion> expectedTareas = List.of(
                    createTareaReposicion(1L, 1L, "Task 1", 5, 15, 30, EstadoTarea.PENDIENTE, prioridad)
            );

            when(tareaRepository.findByPrioridad(prioridad)).thenReturn(expectedTareas);

            // When
            List<TareaReposicion> result = tareasService.obtenerTareasPorPrioridad(prioridad);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getPrioridad()).isEqualTo(prioridad);
            verify(tareaRepository).findByPrioridad(prioridad);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando prioridad es null")
        void shouldThrowExceptionWhenPriorityIsNull() {
            // When & Then
            assertThatThrownBy(() -> tareasService.obtenerTareasPorPrioridad(null))
                    .isInstanceOf(InvalidTaskDataException.class)
                    .hasMessageContaining("Task priority cannot be null");
        }
    }

    @Nested
    @DisplayName("Marcar Tarea en Proceso")
    class MarcarTareaEnProceso {

        @Test
        @DisplayName("Debe marcar tarea en proceso exitosamente")
        void shouldMarkTaskInProgressSuccessfully() {
            // Given
            Long tareaId = 1L;
            TareaReposicion tareaOriginal = createTareaReposicion(tareaId, 1L, "Test Task", 
                    5, 15, 30, EstadoTarea.PENDIENTE, PrioridadTarea.ALTA);
            TareaReposicion tareaActualizada = createTareaReposicion(tareaId, 1L, "Test Task", 
                    5, 15, 30, EstadoTarea.EN_PROCESO, PrioridadTarea.ALTA);

            when(tareaRepository.findById(tareaId)).thenReturn(Optional.of(tareaOriginal));
            when(tareaRepository.save(any(TareaReposicion.class))).thenReturn(tareaActualizada);

            // When
            TareaReposicion result = tareasService.marcarTareaEnProceso(tareaId);

            // Then
            assertThat(result.getEstado()).isEqualTo(EstadoTarea.EN_PROCESO);
            verify(tareaRepository).save(any(TareaReposicion.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando tarea está completada")
        void shouldThrowExceptionWhenTaskIsCompleted() {
            // Given
            Long tareaId = 1L;
            TareaReposicion tareaCompletada = createTareaReposicion(tareaId, 1L, "Completed Task", 
                    5, 15, 30, EstadoTarea.COMPLETADA, PrioridadTarea.ALTA);

            when(tareaRepository.findById(tareaId)).thenReturn(Optional.of(tareaCompletada));

            // When & Then
            assertThatThrownBy(() -> tareasService.marcarTareaEnProceso(tareaId))
                    .isInstanceOf(InvalidTaskDataException.class)
                    .hasMessageContaining("Cannot change state of completed task");
        }
    }

    @Nested
    @DisplayName("Completar Tarea")
    class CompletarTarea {

        @Test
        @DisplayName("Debe completar tarea exitosamente")
        void shouldCompleteTaskSuccessfully() {
            // Given
            Long tareaId = 1L;
            String observacionesFinales = "Reposición completada exitosamente";
            
            TareaReposicion tareaOriginal = createTareaReposicion(tareaId, 1L, "Test Task", 
                    5, 15, 30, EstadoTarea.EN_PROCESO, PrioridadTarea.ALTA);
            TareaReposicion tareaCompletada = createTareaReposicion(tareaId, 1L, "Test Task", 
                    5, 15, 30, EstadoTarea.COMPLETADA, PrioridadTarea.ALTA);

            when(tareaRepository.findById(tareaId)).thenReturn(Optional.of(tareaOriginal));
            when(tareaRepository.save(any(TareaReposicion.class))).thenReturn(tareaCompletada);

            // When
            TareaReposicion result = tareasService.completarTarea(tareaId, observacionesFinales);

            // Then
            assertThat(result.getEstado()).isEqualTo(EstadoTarea.COMPLETADA);
            verify(tareaRepository).save(any(TareaReposicion.class));
        }

        @Test
        @DisplayName("Debe completar tarea con observaciones null")
        void shouldCompleteTaskWithNullObservations() {
            // Given
            Long tareaId = 1L;
            TareaReposicion tareaOriginal = createTareaReposicion(tareaId, 1L, "Test Task", 
                    5, 15, 30, EstadoTarea.PENDIENTE, PrioridadTarea.ALTA);

            when(tareaRepository.findById(tareaId)).thenReturn(Optional.of(tareaOriginal));
            when(tareaRepository.save(any(TareaReposicion.class))).thenReturn(tareaOriginal);

            // When & Then
            assertThatCode(() -> tareasService.completarTarea(tareaId, null))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("Obtener Estadísticas")
    class ObtenerEstadisticas {

        @Test
        @DisplayName("Debe obtener estadísticas correctamente")
        void shouldGetStatisticsCorrectly() {
            // Given
            List<TareaReposicion> allTasks = List.of(
                    createTareaReposicion(1L, 1L, "Task 1", 5, 15, 30, EstadoTarea.PENDIENTE, PrioridadTarea.CRITICA),
                    createTareaReposicion(2L, 2L, "Task 2", 8, 20, 40, EstadoTarea.EN_PROCESO, PrioridadTarea.ALTA),
                    createTareaReposicion(3L, 3L, "Task 3", 12, 25, 50, EstadoTarea.COMPLETADA, PrioridadTarea.MEDIA)
            );

            when(tareaRepository.findAll()).thenReturn(allTasks);
            when(tareaRepository.countByEstado(EstadoTarea.PENDIENTE)).thenReturn(1L);
            when(tareaRepository.countByEstado(EstadoTarea.EN_PROCESO)).thenReturn(1L);
            when(tareaRepository.countByEstado(EstadoTarea.COMPLETADA)).thenReturn(1L);
            when(tareaRepository.countByPrioridad(PrioridadTarea.CRITICA)).thenReturn(1L);
            when(tareaRepository.countVencidas()).thenReturn(0L);

            // When
            EstadisticasTareas result = tareasService.obtenerEstadisticas();

            // Then
            assertThat(result.totalTareas()).isEqualTo(3);
            assertThat(result.tareasPendientes()).isEqualTo(1);
            assertThat(result.tareasEnProceso()).isEqualTo(1);
            assertThat(result.tareasCompletadas()).isEqualTo(1);
            assertThat(result.tareasCriticas()).isEqualTo(1);
            assertThat(result.tareasVencidas()).isEqualTo(0);
        }

        @Test
        @DisplayName("Debe manejar estadísticas vacías")
        void shouldHandleEmptyStatistics() {
            // Given
            when(tareaRepository.findAll()).thenReturn(List.of());
            when(tareaRepository.countByEstado(any())).thenReturn(0L);
            when(tareaRepository.countByPrioridad(any())).thenReturn(0L);
            when(tareaRepository.countVencidas()).thenReturn(0L);

            // When
            EstadisticasTareas result = tareasService.obtenerEstadisticas();

            // Then
            assertThat(result.totalTareas()).isZero();
            assertThat(result.tareasPendientes()).isZero();
            assertThat(result.tareasEnProceso()).isZero();
            assertThat(result.tareasCompletadas()).isZero();
            assertThat(result.tareasCriticas()).isZero();
            assertThat(result.tareasVencidas()).isZero();
        }
    }

    @Nested
    @DisplayName("Obtener Tareas Vencidas")
    class ObtenerTareasVencidas {

        @Test
        @DisplayName("Debe obtener tareas vencidas")
        void shouldGetOverdueTasks() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            List<TareaReposicion> tareasVencidas = List.of(
                    createTareaReposicion(1L, 1L, "Overdue Task", 5, 15, 30, EstadoTarea.PENDIENTE, PrioridadTarea.CRITICA)
            );

            when(tareaRepository.findByFechaVencimientoBefore(any(LocalDateTime.class))).thenReturn(tareasVencidas);

            // When
            List<TareaReposicion> result = tareasService.obtenerTareasVencidas();

            // Then
            assertThat(result).hasSize(1);
            verify(tareaRepository).findByFechaVencimientoBefore(any(LocalDateTime.class));
        }
    }

    // Método helper para crear objetos de prueba
    private TareaReposicion createTareaReposicion(Long id, Long productoId, String nombreProducto,
                                                 int stockActual, int umbralReposicion, int cantidadSugerida,
                                                 EstadoTarea estado, PrioridadTarea prioridad) {
        return TareaReposicion.builder()
                .withId(id)
                .withProductoId(productoId)
                .withNombreProducto(nombreProducto)
                .withStockActual(stockActual)
                .withUmbralReposicion(umbralReposicion)
                .withCantidadSugerida(cantidadSugerida)
                .withEstado(estado)
                .withPrioridad(prioridad)
                .withObservaciones("Test observations")
                .withFechaVencimiento(LocalDateTime.now().plusDays(1))
                .build();
    }
}
