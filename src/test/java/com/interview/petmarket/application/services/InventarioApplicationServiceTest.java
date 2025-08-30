package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.events.LowStockEvent;
import com.interview.petmarket.domain.events.StockConfirmedEvent;
import com.interview.petmarket.domain.exceptions.InvalidInventoryDataException;
import com.interview.petmarket.domain.exceptions.InvalidOrderDataException;
import com.interview.petmarket.domain.model.inventario.Inventario;
import com.interview.petmarket.domain.model.pedido.EstadoPedido;
import com.interview.petmarket.domain.model.pedido.Pedido;
import com.interview.petmarket.domain.model.pedido.PedidoItem;
import com.interview.petmarket.domain.model.producto.EspecieAnimal;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.model.producto.TipoProducto;
import com.interview.petmarket.domain.ports.out.EventPublisherPort;
import com.interview.petmarket.domain.ports.out.InventarioRepositoryPort;
import com.interview.petmarket.domain.ports.out.PedidoRepositoryPort;
import com.interview.petmarket.domain.ports.out.ProductoRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para InventarioApplicationService.
 * Sigue el patrón establecido con JUnit 5, Mockito y AssertJ.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InventarioApplicationService Tests")
class InventarioApplicationServiceTest {

    @Mock
    private InventarioRepositoryPort inventarioRepository;
    
    @Mock
    private PedidoRepositoryPort pedidoRepository;
    
    @Mock
    private ProductoRepositoryPort productoRepository;
    
    @Mock
    private EventPublisherPort eventPublisher;

    private InventarioApplicationService inventarioService;

    @BeforeEach
    void setUp() {
        inventarioService = new InventarioApplicationService(
                inventarioRepository,
                pedidoRepository,
                productoRepository,
                eventPublisher
        );
    }

    @Nested
    @DisplayName("Confirmar Stock por Pago")
    class ConfirmarStockPorPago {

        @Test
        @DisplayName("Debe confirmar stock exitosamente para pedido válido")
        void shouldConfirmStockSuccessfully() {
            // Given
            Long pedidoId = 1L;
            Long productoId = 10L;
            
            Producto producto = createProducto(productoId, "Test Product", BigDecimal.valueOf(50.0));
            Inventario inventario = createInventario(1L, productoId, 20, 10);
            
            PedidoItem item = new PedidoItem(productoId, 2, BigDecimal.valueOf(50.0));
            
            Pedido pedido = createPedido(pedidoId, 1L, List.of(item));

            when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
            when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventario));
            when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

            // When
            List<Inventario> result = inventarioService.confirmarStockPorPago(pedidoId);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(inventario);
            
            // Verificar que se publicó el evento StockConfirmedEvent
            ArgumentCaptor<StockConfirmedEvent> eventCaptor = ArgumentCaptor.forClass(StockConfirmedEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            
            StockConfirmedEvent event = eventCaptor.getValue();
            assertThat(event.getPedidoId()).isEqualTo(pedidoId);
            assertThat(event.getProductoId()).isEqualTo(productoId);
            assertThat(event.getCantidadConfirmada()).isEqualTo(2);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando pedido no existe")
        void shouldThrowExceptionWhenOrderNotFound() {
            // Given
            Long pedidoId = 999L;
            when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> inventarioService.confirmarStockPorPago(pedidoId))
                    .isInstanceOf(InvalidOrderDataException.class)
                    .hasMessageContaining("Order not found: " + pedidoId);
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L})
        @DisplayName("Debe lanzar excepción para IDs de pedido inválidos")
        void shouldThrowExceptionForInvalidOrderIds(Long invalidId) {
            // When & Then
            assertThatThrownBy(() -> inventarioService.confirmarStockPorPago(invalidId))
                    .isInstanceOf(InvalidOrderDataException.class);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando pedido ID es null")
        void shouldThrowExceptionWhenOrderIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> inventarioService.confirmarStockPorPago(null))
                    .isInstanceOf(InvalidOrderDataException.class)
                    .hasMessageContaining("Order ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Obtener Inventario")
    class ObtenerInventario {

        @Test
        @DisplayName("Debe obtener inventario exitosamente")
        void shouldGetInventorySuccessfully() {
            // Given
            Long productoId = 1L;
            Inventario expectedInventario = createInventario(1L, productoId, 50, 10);
            when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(expectedInventario));

            // When
            Inventario result = inventarioService.obtenerInventario(productoId);

            // Then
            assertThat(result).isEqualTo(expectedInventario);
            verify(inventarioRepository).findByProductoId(productoId);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando inventario no existe")
        void shouldThrowExceptionWhenInventoryNotFound() {
            // Given
            Long productoId = 999L;
            when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> inventarioService.obtenerInventario(productoId))
                    .isInstanceOf(InvalidInventoryDataException.class)
                    .hasMessageContaining("Inventory not found for product: " + productoId);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando producto ID es null")
        void shouldThrowExceptionWhenProductIdIsNull() {
            // When & Then
            assertThatThrownBy(() -> inventarioService.obtenerInventario(null))
                    .isInstanceOf(InvalidInventoryDataException.class)
                    .hasMessageContaining("Product ID cannot be null");
        }
    }

    @Nested
    @DisplayName("Actualizar Stock")
    class ActualizarStock {

        @Test
        @DisplayName("Debe actualizar stock exitosamente")
        void shouldUpdateStockSuccessfully() {
            // Given
            Long productoId = 1L;
            int nuevaCantidad = 25;
            String motivo = "Reposición manual";
            
            Inventario inventarioOriginal = createInventario(1L, productoId, 10, 15);
            Inventario inventarioActualizado = createInventario(1L, productoId, nuevaCantidad, 15);
            
            when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventarioOriginal));
            when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioActualizado);

            // When
            Inventario result = inventarioService.actualizarStock(productoId, nuevaCantidad, motivo);

            // Then
            assertThat(result.getStockDisponible()).isEqualTo(nuevaCantidad);
            verify(inventarioRepository).save(any(Inventario.class));
        }

        @Test
        @DisplayName("Debe emitir evento LowStock cuando stock queda bajo")
        void shouldEmitLowStockEventWhenStockBecomesLow() {
            // Given
            Long productoId = 1L;
            int nuevaCantidad = 5; // Menor que umbral de 15
            String motivo = "Venta";
            
            Inventario inventarioOriginal = createInventario(1L, productoId, 20, 15);
            Inventario inventarioActualizado = createInventario(1L, productoId, nuevaCantidad, 15);
            Producto producto = createProducto(productoId, "Test Product", BigDecimal.valueOf(50.0));
            
            when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventarioOriginal));
            when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioActualizado);
            when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

            // When
            inventarioService.actualizarStock(productoId, nuevaCantidad, motivo);

            // Then
            ArgumentCaptor<LowStockEvent> eventCaptor = ArgumentCaptor.forClass(LowStockEvent.class);
            verify(eventPublisher).publishEvent(eventCaptor.capture());
            
            LowStockEvent event = eventCaptor.getValue();
            assertThat(event.getProductoId()).isEqualTo(productoId);
            assertThat(event.getStockActual()).isEqualTo(nuevaCantidad);
            assertThat(event.getUmbralReposicion()).isEqualTo(15);
        }

        @ParameterizedTest
        @CsvSource({
                "-1, Stock quantity cannot be negative",
                "-10, Stock quantity cannot be negative"
        })
        @DisplayName("Debe lanzar excepción para cantidades negativas")
        void shouldThrowExceptionForNegativeQuantities(int invalidQuantity, String expectedMessage) {
            // Given
            Long productoId = 1L;
            String motivo = "Test";

            // When & Then
            assertThatThrownBy(() -> inventarioService.actualizarStock(productoId, invalidQuantity, motivo))
                    .isInstanceOf(InvalidInventoryDataException.class)
                    .hasMessageContaining(expectedMessage);
        }
    }

    @Nested
    @DisplayName("Ajustar Umbral de Reposición")
    class AjustarUmbralReposicion {

        @Test
        @DisplayName("Debe ajustar umbral exitosamente")
        void shouldAdjustThresholdSuccessfully() {
            // Given
            Long productoId = 1L;
            int nuevoUmbral = 20;
            
            Inventario inventarioOriginal = createInventario(1L, productoId, 25, 10);
            Inventario inventarioActualizado = createInventario(1L, productoId, 25, nuevoUmbral);
            
            when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventarioOriginal));
            when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioActualizado);

            // When
            Inventario result = inventarioService.ajustarUmbralReposicion(productoId, nuevoUmbral);

            // Then
            assertThat(result.getUmbralReposicion()).isEqualTo(nuevoUmbral);
            verify(inventarioRepository).save(any(Inventario.class));
        }

        @Test
        @DisplayName("Debe emitir evento LowStock cuando nuevo umbral hace que stock sea bajo")
        void shouldEmitLowStockEventWhenNewThresholdMakesStockLow() {
            // Given
            Long productoId = 1L;
            int nuevoUmbral = 30; // Mayor que stock actual de 25
            
            Inventario inventarioOriginal = createInventario(1L, productoId, 25, 10);
            Inventario inventarioActualizado = createInventario(1L, productoId, 25, nuevoUmbral);
            Producto producto = createProducto(productoId, "Test Product", BigDecimal.valueOf(50.0));
            
            when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(inventarioOriginal));
            when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioActualizado);
            when(productoRepository.findById(productoId)).thenReturn(Optional.of(producto));

            // When
            inventarioService.ajustarUmbralReposicion(productoId, nuevoUmbral);

            // Then
            verify(eventPublisher).publishEvent(any(LowStockEvent.class));
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, -5, -10})
        @DisplayName("Debe lanzar excepción para umbrales negativos")
        void shouldThrowExceptionForNegativeThresholds(int invalidThreshold) {
            // Given
            Long productoId = 1L;

            // When & Then
            assertThatThrownBy(() -> inventarioService.ajustarUmbralReposicion(productoId, invalidThreshold))
                    .isInstanceOf(InvalidInventoryDataException.class)
                    .hasMessageContaining("Replenishment threshold cannot be negative");
        }
    }

    @Nested
    @DisplayName("Obtener Inventarios con Stock Bajo")
    class ObtenerInventariosConStockBajo {

        @Test
        @DisplayName("Debe obtener inventarios con stock bajo")
        void shouldGetLowStockInventories() {
            // Given
            List<Inventario> expectedInventarios = List.of(
                    createInventario(1L, 1L, 5, 10),
                    createInventario(2L, 2L, 8, 15)
            );
            when(inventarioRepository.findByStockBelowThreshold()).thenReturn(expectedInventarios);

            // When
            List<Inventario> result = inventarioService.obtenerInventariosConStockBajo();

            // Then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactlyElementsOf(expectedInventarios);
            verify(inventarioRepository).findByStockBelowThreshold();
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay inventarios con stock bajo")
        void shouldReturnEmptyListWhenNoLowStockInventories() {
            // Given
            when(inventarioRepository.findByStockBelowThreshold()).thenReturn(List.of());

            // When
            List<Inventario> result = inventarioService.obtenerInventariosConStockBajo();

            // Then
            assertThat(result).isEmpty();
        }
    }

    // Métodos helper para crear objetos de prueba
    private Inventario createInventario(Long id, Long productoId, int stock, int umbral) {
        return Inventario.builder()
                .withId(id)
                .withProductoId(productoId)
                .withStockDisponible(stock)
                .withUmbralReposicion(umbral)
                .build();
    }

    private Producto createProducto(Long id, String nombre, BigDecimal precio) {
        return Producto.builder()
                .withId(id)
                .withNombre(nombre)
                .withDescripcion("Test description")
                .withPrecio(precio)
                .withTipo(TipoProducto.ALIMENTO)
                .withEspecie(EspecieAnimal.PERRO)
                .withStock(100)
                .withActivo(true)
                .build();
    }

    private Pedido createPedido(Long id, Long clienteId, List<PedidoItem> items) {
        return Pedido.builder()
                .withId(id)
                .withClienteId(clienteId)
                .withItems(items)
                .withEstado(EstadoPedido.CREADO)
                .withTotal(BigDecimal.valueOf(100.0))
                .build();
    }
}
