package com.interview.petmarket.domain.model.pedido;

import com.interview.petmarket.domain.model.producto.EspecieAnimal;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.model.producto.TipoProducto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@DisplayName("Pricing Strategy Tests")
@Tag("unit")
@Tag("domain")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PricingStrategyTest {

    private static final BigDecimal PRECIO_BASE = new BigDecimal("100.00");
    private static final BigDecimal DESCUENTO_PERRO = new BigDecimal("0.15"); // 15%
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
    @DisplayName("Precio Base Strategy")
    class PrecioBaseStrategyTest {

        private PrecioBaseStrategy strategy;

        @BeforeEach
        void setUp() {
            strategy = new PrecioBaseStrategy();
        }

        @Test
        @Order(1)
        @DisplayName("Should return correct strategy name")
        @Timeout(value = 2)
        void shouldReturnCorrectStrategyName() {
            // When
            String nombre = assertTimeout(Duration.ofMillis(10), () ->
                    strategy.getNombre()
            );

            // Then
            assertThat(nombre).isEqualTo("PRECIO_BASE");
        }

        @Test
        @DisplayName("Should calculate item price correctly")
        void shouldCalculateItemPriceCorrectly() {
            // Given
            Producto producto = createProductoPerro(PRECIO_BASE);

            // When
            BigDecimal precioCalculado = strategy.calcularPrecio(producto, CANTIDAD_VALIDA);

            // Then
            BigDecimal precioEsperado = PRECIO_BASE.multiply(BigDecimal.valueOf(CANTIDAD_VALIDA));
            assertThat(precioCalculado).isEqualTo(precioEsperado);
        }

        @ParameterizedTest
        @CsvSource({
                "50.00, 1, 50.00",
                "25.99, 3, 77.97",
                "100.00, 5, 500.00",
                "10.50, 2, 21.00"
        })
        @DisplayName("Should calculate item price with different values")
        void shouldCalculateItemPriceWithDifferentValues(String precio, int cantidad, String esperado) {
            // Given
            Producto producto = createProductoPerro(new BigDecimal(precio));

            // When
            BigDecimal resultado = strategy.calcularPrecio(producto, cantidad);

            // Then
            assertThat(resultado).isEqualTo(new BigDecimal(esperado));
        }

        @Test
        @DisplayName("Should handle zero quantity")
        void shouldHandleZeroQuantity() {
            // Given
            Producto producto = createProductoPerro(PRECIO_BASE);

            // When
            BigDecimal resultado = strategy.calcularPrecio(producto, 0);

            // Then
            assertThat(resultado).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should handle null product")
        void shouldHandleNullProduct() {
            // When
            BigDecimal resultado = strategy.calcularPrecio(null, CANTIDAD_VALIDA);

            // Then
            assertThat(resultado).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should not apply discount for any species")
        void shouldNotApplyDiscountForAnySpecies() {
            // Given
            Producto productoPerro = createProductoPerro(PRECIO_BASE);
            Producto productoGato = createProductoGato(PRECIO_BASE);
            Producto productoUniversal = createProductoUniversal(PRECIO_BASE);

            // When
            BigDecimal precioPerro = strategy.calcularPrecio(productoPerro, 1);
            BigDecimal precioGato = strategy.calcularPrecio(productoGato, 1);
            BigDecimal precioUniversal = strategy.calcularPrecio(productoUniversal, 1);

            // Then
            assertAll("Validar que no se aplica descuento",
                    () -> assertThat(precioPerro).isEqualTo(PRECIO_BASE),
                    () -> assertThat(precioGato).isEqualTo(PRECIO_BASE),
                    () -> assertThat(precioUniversal).isEqualTo(PRECIO_BASE)
            );
        }
    }

    @Nested
    @DisplayName("Promo Perro Strategy")
    class PromoPerroStrategyTest {

        private PromoPerroStrategy strategy;

        @BeforeEach
        void setUp() {
            strategy = new PromoPerroStrategy();
        }

        @Test
        @DisplayName("Should return correct strategy name")
        void shouldReturnCorrectStrategyName() {
            // When
            String nombre = strategy.getNombre();

            // Then
            assertThat(nombre).isEqualTo("PROMO_PERRO");
        }

        @Test
        @DisplayName("Should apply discount to dog products")
        void shouldApplyDiscountToDogProducts() {
            // Given
            Producto productoPerro = createProductoPerro(PRECIO_BASE);

            // When
            BigDecimal precioConDescuento = strategy.calcularPrecio(productoPerro, 1);

            // Then
            BigDecimal descuentoEsperado = PRECIO_BASE.multiply(DESCUENTO_PERRO);
            BigDecimal precioEsperado = PRECIO_BASE.subtract(descuentoEsperado)
                    .setScale(2, RoundingMode.HALF_UP);
            
            assertThat(precioConDescuento).isEqualTo(precioEsperado);
        }

        @Test
        @DisplayName("Should not apply discount to non-dog products")
        void shouldNotApplyDiscountToNonDogProducts() {
            // Given
            Producto productoGato = createProductoGato(PRECIO_BASE);
            Producto productoUniversal = createProductoUniversal(PRECIO_BASE);

            // When
            BigDecimal precioGato = strategy.calcularPrecio(productoGato, 1);
            BigDecimal precioUniversal = strategy.calcularPrecio(productoUniversal, 1);

            // Then
            assertAll("Validar que no se aplica descuento a no-perros",
                    () -> assertThat(precioGato).isEqualTo(PRECIO_BASE),
                    () -> assertThat(precioUniversal).isEqualTo(PRECIO_BASE)
            );
        }

        @ParameterizedTest
        @MethodSource("preciosConDescuentoPerro")
        @DisplayName("Should calculate correct discount for dog products")
        void shouldCalculateCorrectDiscountForDogProducts(BigDecimal precioOriginal, int cantidad, BigDecimal esperado) {
            // Given
            Producto productoPerro = createProductoPerro(precioOriginal);

            // When
            BigDecimal resultado = strategy.calcularPrecio(productoPerro, cantidad);

            // Then
            assertThat(resultado).isEqualTo(esperado);
        }

        @Test
        @DisplayName("Should handle negative quantity gracefully")
        void shouldHandleNegativeQuantityGracefully() {
            // Given
            Producto productoPerro = createProductoPerro(PRECIO_BASE);

            // When
            BigDecimal resultado = strategy.calcularPrecio(productoPerro, -1);

            // Then
            assertThat(resultado).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should handle multiple dog items with discount")
        void shouldHandleMultipleDogItemsWithDiscount() {
            // Given
            Producto productoPerro = createProductoPerro(new BigDecimal("50.00"));

            // When
            BigDecimal precioTotal = strategy.calcularPrecio(productoPerro, 3);

            // Then
            // 50.00 - (50.00 * 0.15) = 42.50 por unidad
            // 42.50 * 3 = 127.50
            BigDecimal precioEsperado = new BigDecimal("127.50");
            assertThat(precioTotal).isEqualTo(precioEsperado);
        }

        @Test
        @DisplayName("Should round prices correctly")
        void shouldRoundPricesCorrectly() {
            // Given - Precio que genera decimales al aplicar descuento
            Producto productoPerro = createProductoPerro(new BigDecimal("33.33"));

            // When
            BigDecimal precioConDescuento = strategy.calcularPrecio(productoPerro, 1);

            // Then
            // 33.33 - (33.33 * 0.15) = 33.33 - 4.9995 = 28.3305 -> 28.33
            BigDecimal precioEsperado = new BigDecimal("28.33");
            assertThat(precioConDescuento).isEqualTo(precioEsperado);
        }

        private static Stream<Arguments> preciosConDescuentoPerro() {
            return Stream.of(
                    Arguments.of(new BigDecimal("100.00"), 1, new BigDecimal("85.00")),
                    Arguments.of(new BigDecimal("50.00"), 2, new BigDecimal("85.00")),
                    Arguments.of(new BigDecimal("200.00"), 1, new BigDecimal("170.00")),
                    Arguments.of(new BigDecimal("10.00"), 5, new BigDecimal("42.50"))
            );
        }
    }

    @Nested
    @DisplayName("Strategy Comparison")
    class StrategyComparison {

        @Test
        @DisplayName("Should show price difference between strategies for dog products")
        void shouldShowPriceDifferenceBetweenStrategiesForDogProducts() {
            // Given
            PrecioBaseStrategy baseStrategy = new PrecioBaseStrategy();
            PromoPerroStrategy promoStrategy = new PromoPerroStrategy();
            Producto productoPerro = createProductoPerro(PRECIO_BASE);

            // When
            BigDecimal precioBase = baseStrategy.calcularPrecio(productoPerro, CANTIDAD_VALIDA);
            BigDecimal precioPromo = promoStrategy.calcularPrecio(productoPerro, CANTIDAD_VALIDA);

            // Then
            BigDecimal diferencia = precioBase.subtract(precioPromo);
            BigDecimal diferenciaEsperada = PRECIO_BASE.multiply(DESCUENTO_PERRO)
                    .multiply(BigDecimal.valueOf(CANTIDAD_VALIDA))
                    .setScale(2, RoundingMode.HALF_UP);
            
            assertAll("Validar diferencia de precios",
                    () -> assertThat(precioPromo).isLessThan(precioBase),
                    () -> assertThat(diferencia).isEqualTo(diferenciaEsperada),
                    () -> assertThat(precioBase).isEqualTo(new BigDecimal("200.00")),
                    () -> assertThat(precioPromo).isEqualTo(new BigDecimal("170.00"))
            );
        }

        @Test
        @DisplayName("Should have same price for non-dog products")
        void shouldHaveSamePriceForNonDogProducts() {
            // Given
            PrecioBaseStrategy baseStrategy = new PrecioBaseStrategy();
            PromoPerroStrategy promoStrategy = new PromoPerroStrategy();
            Producto productoGato = createProductoGato(PRECIO_BASE);

            // When
            BigDecimal precioBase = baseStrategy.calcularPrecio(productoGato, CANTIDAD_VALIDA);
            BigDecimal precioPromo = promoStrategy.calcularPrecio(productoGato, CANTIDAD_VALIDA);

            // Then
            assertThat(precioBase).isEqualTo(precioPromo);
        }
    }

    @Nested
    @DisplayName("Advanced Features")
    class AdvancedFeatures {

        @RepeatedTest(value = 5, name = "Repetición {currentRepetition} de {totalRepetitions}")
        @DisplayName("Should calculate prices consistently")
        void shouldCalculatePricesConsistently(RepetitionInfo repetitionInfo) {
            // Given
            PromoPerroStrategy strategy = new PromoPerroStrategy();
            Producto producto = createProductoPerro(PRECIO_BASE);

            // When
            BigDecimal precio1 = strategy.calcularPrecio(producto, 1);
            BigDecimal precio2 = strategy.calcularPrecio(producto, 1);

            // Then
            assertThat(precio1).isEqualTo(precio2);
        }

        @Test
        @DisplayName("Should handle concurrent calculations safely")
        @Timeout(value = 5)
        void shouldHandleConcurrentCalculationsSafely() {
            // Given
            PromoPerroStrategy strategy = new PromoPerroStrategy();
            Producto producto = createProductoPerro(PRECIO_BASE);

            // When & Then
            assertTimeout(Duration.ofSeconds(3), () -> {
                for (int i = 0; i < 100; i++) {
                    BigDecimal precio = strategy.calcularPrecio(producto, 1);
                    assertThat(precio).isEqualTo(new BigDecimal("85.00"));
                }
            });
        }
    }

    // Helper methods
    private Producto createProductoPerro(BigDecimal precio) {
        return Producto.builder()
                .withId(1L)
                .withNombre("Producto para Perro")
                .withDescripcion("Descripción test")
                .withPrecio(precio)
                .withTipo(TipoProducto.ACCESORIO)
                .withEspecie(EspecieAnimal.PERRO)
                .withStock(10)
                .withActivo(true)
                .build();
    }

    private Producto createProductoGato(BigDecimal precio) {
        return Producto.builder()
                .withId(2L)
                .withNombre("Producto para Gato")
                .withDescripcion("Descripción test")
                .withPrecio(precio)
                .withTipo(TipoProducto.ACCESORIO)
                .withEspecie(EspecieAnimal.GATO)
                .withStock(10)
                .withActivo(true)
                .build();
    }

    private Producto createProductoUniversal(BigDecimal precio) {
        return Producto.builder()
                .withId(3L)
                .withNombre("Producto Universal")
                .withDescripcion("Descripción test")
                .withPrecio(precio)
                .withTipo(TipoProducto.ACCESORIO)
                .withEspecie(EspecieAnimal.UNIVERSAL)
                .withStock(10)
                .withActivo(true)
                .build();
    }


}
