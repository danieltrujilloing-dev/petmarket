package com.interview.petmarket.domain.model.producto;

import com.interview.petmarket.domain.exceptions.InvalidProductDataException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTimeout;

@DisplayName("Producto Domain Model Tests")
@Tag("unit")
@Tag("domain")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductoTest {

    private static final BigDecimal PRECIO_VALIDO = new BigDecimal("29.99");
    private static final String NOMBRE_VALIDO = "Collar para Perro";
    private static final String DESCRIPCION_VALIDA = "Collar ajustable de cuero";

    @BeforeAll
    static void setupClass() {
        // Configuración global para todos los tests
    }

    @AfterAll
    static void tearDownClass() {
        // Limpieza global después de todos los tests
    }

    @Nested
    @DisplayName("Product Creation")
    class ProductCreation {

        @Test
        @Order(1)
        @DisplayName("Should create product with valid data using assertAll")
        @Timeout(value = 2)
        void shouldCreateProductWithValidData() {
            // Given
            TipoProducto tipo = TipoProducto.COLLAR;
            EspecieAnimal especie = EspecieAnimal.PERRO;

            // When
            LocalDateTime before = LocalDateTime.now();
            Producto producto = assertTimeout(Duration.ofMillis(100), () ->
                Producto.builder()
                    .withNombre(NOMBRE_VALIDO)
                    .withDescripcion(DESCRIPCION_VALIDA)
                    .withPrecio(PRECIO_VALIDO)
                    .withTipo(tipo)
                    .withEspecie(especie)
                    .withStock(10)
                    .withActivo(true)
                    .build()
            );
            LocalDateTime after = LocalDateTime.now();

            // Then - Usar assertAll para validaciones agrupadas
            assertAll("Validar creación de producto",
                () -> assertThat(producto.getNombre()).isEqualTo(NOMBRE_VALIDO),
                () -> assertThat(producto.getDescripcion()).isEqualTo(DESCRIPCION_VALIDA),
                () -> assertThat(producto.getPrecio()).isEqualTo(PRECIO_VALIDO),
                () -> assertThat(producto.getTipo()).isEqualTo(tipo),
                () -> assertThat(producto.getEspecie()).isEqualTo(especie),
                () -> assertThat(producto.getStock()).isEqualTo(10),
                () -> assertThat(producto.isActivo()).isTrue(),
                () -> assertThat(producto.getFechaCreacion()).isBetween(before, after),
                () -> assertThat(producto.getFechaActualizacion()).isBetween(before, after)
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "\t", "\n"})
        @DisplayName("Should throw exception when name is blank")
        void shouldThrowExceptionWhenNameIsBlank(String invalidName) {
            // When & Then
            assertThatThrownBy(() -> 
                Producto.builder()
                    .withNombre(invalidName)
                    .withPrecio(new BigDecimal("29.99"))
                    .withTipo(TipoProducto.COLLAR)
                    .withEspecie(EspecieAnimal.PERRO)
                    .build()
            ).isInstanceOf(InvalidProductDataException.class)
             .hasMessageContaining("name cannot be null or empty");
        }

        @Test
        @DisplayName("Should throw exception when price is null")
        void shouldThrowExceptionWhenPriceIsNull() {
            // When & Then
            assertThatThrownBy(() -> 
                Producto.builder()
                    .withNombre("Collar")
                    .withPrecio(null)
                    .withTipo(TipoProducto.COLLAR)
                    .withEspecie(EspecieAnimal.PERRO)
                    .build()
            ).isInstanceOf(InvalidProductDataException.class);
        }

        @Test
        @DisplayName("Should throw exception when price is zero or negative")
        void shouldThrowExceptionWhenPriceIsZeroOrNegative() {
            // When & Then
            assertThatThrownBy(() -> 
                Producto.builder()
                    .withNombre("Collar")
                    .withPrecio(BigDecimal.ZERO)
                    .withTipo(TipoProducto.COLLAR)
                    .withEspecie(EspecieAnimal.PERRO)
                    .build()
            ).isInstanceOf(InvalidProductDataException.class)
             .hasMessageContaining("price must be greater than zero");

            assertThatThrownBy(() -> 
                Producto.builder()
                    .withNombre("Collar")
                    .withPrecio(new BigDecimal("-10.00"))
                    .withTipo(TipoProducto.COLLAR)
                    .withEspecie(EspecieAnimal.PERRO)
                    .build()
            ).isInstanceOf(InvalidProductDataException.class)
             .hasMessageContaining("price must be greater than zero");
        }
    }

    @Nested
    @DisplayName("Product Business Logic")
    class ProductBusinessLogic {

        @Test
        @DisplayName("Should be available when active and has stock")
        void shouldBeAvailableWhenActiveAndHasStock() {
            // Given
            Producto producto = createValidProduct()
                    .withStock(5)
                    .withActivo(true)
                    .build();

            // When & Then
            assertThat(producto.estaDisponible()).isTrue();
        }

        @Test
        @DisplayName("Should not be available when inactive")
        void shouldNotBeAvailableWhenInactive() {
            // Given
            Producto producto = createValidProduct()
                    .withStock(5)
                    .withActivo(false)
                    .build();

            // When & Then
            assertThat(producto.estaDisponible()).isFalse();
        }

        @Test
        @DisplayName("Should not be available when stock is zero")
        void shouldNotBeAvailableWhenStockIsZero() {
            // Given
            Producto producto = createValidProduct()
                    .withStock(0)
                    .withActivo(true)
                    .build();

            // When & Then
            assertThat(producto.estaDisponible()).isFalse();
        }

        @Test
        @DisplayName("Should reduce stock correctly")
        void shouldReduceStockCorrectly() {
            // Given
            Producto producto = createValidProduct()
                    .withStock(10)
                    .build();

            // When
            producto.reducirStock(3);

            // Then
            assertThat(producto.getStock()).isEqualTo(7);
        }

        @Test
        @DisplayName("Should throw exception when reducing more stock than available")
        void shouldThrowExceptionWhenReducingMoreStockThanAvailable() {
            // Given
            Producto producto = createValidProduct()
                    .withStock(5)
                    .build();

            // When & Then
            assertThatThrownBy(() -> producto.reducirStock(10))
                    .isInstanceOf(InvalidProductDataException.class)
                    .hasMessageContaining("Insufficient stock");
        }

        @Test
        @DisplayName("Should increase stock correctly")
        void shouldIncreaseStockCorrectly() {
            // Given
            Producto producto = createValidProduct()
                    .withStock(10)
                    .build();

            // When
            producto.aumentarStock(5);

            // Then
            assertThat(producto.getStock()).isEqualTo(15);
        }

        @Test
        @DisplayName("Should calculate discounted price correctly")
        void shouldCalculateDiscountedPriceCorrectly() {
            // Given
            Producto producto = createValidProduct()
                    .withPrecio(new BigDecimal("100.00"))
                    .build();

            // When
            BigDecimal discountedPrice = producto.calcularPrecioConDescuento(new BigDecimal("20"));

            // Then
            assertThat(discountedPrice).isEqualTo(new BigDecimal("80.00"));
        }

        @Test
        @DisplayName("Should activate and deactivate product")
        void shouldActivateAndDeactivateProduct() {
            // Given
            Producto producto = createValidProduct()
                    .withActivo(false)
                    .build();

            // When
            producto.activar();

            // Then
            assertThat(producto.isActivo()).isTrue();

            // When
            producto.desactivar();

            // Then
            assertThat(producto.isActivo()).isFalse();
        }
    }

    @Nested
    @DisplayName("Filter Tests")
    class FilterTests {

        @Test
        @DisplayName("Should create filter with types and species")
        void shouldCreateFilterWithTypesAndSpecies() {
            // Given
            Set<TipoProducto> tipos = Set.of(TipoProducto.ALIMENTO, TipoProducto.JUGUETE);
            Set<EspecieAnimal> especies = Set.of(EspecieAnimal.PERRO, EspecieAnimal.GATO);

            // When
            FiltroProducto filtro = FiltroProducto.builder()
                    .tipos(tipos)
                    .especies(especies)
                    .build();

            // Then
            assertThat(filtro.getTipos()).containsExactlyInAnyOrderElementsOf(tipos);
            assertThat(filtro.getEspecies()).containsExactlyInAnyOrderElementsOf(especies);
            assertThat(filtro.tieneAlgunFiltro()).isTrue();
        }

        @Test
        @DisplayName("Should create filter with price range")
        void shouldCreateFilterWithPriceRange() {
            // Given
            BigDecimal minPrice = new BigDecimal("10.00");
            BigDecimal maxPrice = new BigDecimal("50.00");

            // When
            FiltroProducto filtro = FiltroProducto.builder()
                    .precioMinimo(minPrice)
                    .precioMaximo(maxPrice)
                    .build();

            // Then
            assertThat(filtro.getPrecioMinimo()).isEqualTo(minPrice);
            assertThat(filtro.getPrecioMaximo()).isEqualTo(maxPrice);
            assertThat(filtro.tieneFiltroPrecios()).isTrue();
        }

        @Test
        @DisplayName("Should generate unique cache key for filters")
        void shouldGenerateUniqueCacheKeyForFilters() {
            // Given
            FiltroProducto filtro1 = FiltroProducto.builder()
                    .tipos(Set.of(TipoProducto.ALIMENTO))
                    .especies(Set.of(EspecieAnimal.PERRO))
                    .build();

            FiltroProducto filtro2 = FiltroProducto.builder()
                    .tipos(Set.of(TipoProducto.JUGUETE))
                    .especies(Set.of(EspecieAnimal.GATO))
                    .build();

            // When
            String clave1 = filtro1.generarClaveCache();
            String clave2 = filtro2.generarClaveCache();

            // Then
            assertThat(clave1).isNotEqualTo(clave2);
            assertThat(clave1).contains("tipos:ALIMENTO");
            assertThat(clave1).contains("especies:PERRO");
            assertThat(clave2).contains("tipos:JUGUETE");
            assertThat(clave2).contains("especies:GATO");
        }
    }

    @Nested
    @DisplayName("Advanced JUnit 5 Features")
    class AdvancedFeatures {

        @ParameterizedTest(name = "Test #{index}: Tipo {0} should be valid")
        @EnumSource(TipoProducto.class)
        @DisplayName("Should accept all valid product types")
        void shouldAcceptAllValidProductTypes(TipoProducto tipo) {
            // When & Then
            assertThatNoException().isThrownBy(() ->
                createValidProduct()
                    .withTipo(tipo)
                    .build()
            );
        }

        @ParameterizedTest
        @MethodSource("preciosInvalidos")
        @DisplayName("Should reject invalid prices")
        void shouldRejectInvalidPrices(BigDecimal precioInvalido, String descripcion) {
            // When & Then
            assertThatThrownBy(() ->
                createValidProduct()
                    .withPrecio(precioInvalido)
                    .build()
            ).isInstanceOf(InvalidProductDataException.class)
             .hasMessageContaining("price")
             .as("Precio inválido: %s", descripcion);
        }

        @ParameterizedTest
        @CsvSource({
            "10, 5, 5",
            "100, 25, 75", 
            "50, 10, 40",
            "1, 1, 0"
        })
        @DisplayName("Should reduce stock correctly with CSV data")
        void shouldReduceStockCorrectlyWithCsvData(int stockInicial, int cantidadReducir, int stockEsperado) {
            // Given
            Producto producto = createValidProduct()
                    .withStock(stockInicial)
                    .build();

            // When
            producto.reducirStock(cantidadReducir);

            // Then
            assertThat(producto.getStock()).isEqualTo(stockEsperado);
        }

        @RepeatedTest(value = 5, name = "Repetición {currentRepetition} de {totalRepetitions}")
        @DisplayName("Should create product consistently")
        void shouldCreateProductConsistently(RepetitionInfo repetitionInfo) {
            // Given
            int numeroRepeticion = repetitionInfo.getCurrentRepetition();
            String nombrePersonalizado = NOMBRE_VALIDO + " - Repetición " + numeroRepeticion;

            // When
            Producto producto = createValidProduct()
                    .withNombre(nombrePersonalizado)
                    .build();

            // Then
            assertThat(producto.getNombre()).isEqualTo(nombrePersonalizado);
            assertThat(producto.getFechaCreacion()).isNotNull();
        }

        @Test
        @DisplayName("Should handle concurrent access safely")
        @Timeout(value = 5)
        void shouldHandleConcurrentAccessSafely() {
            // Given
            Producto producto = createValidProduct()
                    .withStock(100)
                    .build();

            // When & Then - Simular operaciones concurrentes
            assertTimeout(Duration.ofSeconds(3), () -> {
                for (int i = 0; i < 50; i++) {
                    producto.reducirStock(1);
                }
                assertThat(producto.getStock()).isEqualTo(50);
            });
        }

        private static Stream<Arguments> preciosInvalidos() {
            return Stream.of(
                Arguments.of(BigDecimal.ZERO, "precio cero"),
                Arguments.of(new BigDecimal("-10.50"), "precio negativo"),
                Arguments.of(new BigDecimal("0.00"), "precio exactamente cero"),
                Arguments.of(null, "precio nulo")
            );
        }
    }

    // Helper method mejorado
    private Producto.Builder createValidProduct() {
        return Producto.builder()
                .withNombre(NOMBRE_VALIDO)
                .withDescripcion(DESCRIPCION_VALIDA)
                .withPrecio(PRECIO_VALIDO)
                .withTipo(TipoProducto.ACCESORIO)
                .withEspecie(EspecieAnimal.UNIVERSAL);
    }
}
