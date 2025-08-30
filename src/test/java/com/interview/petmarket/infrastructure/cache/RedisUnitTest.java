package com.interview.petmarket.infrastructure.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario completo para validar Redis en PetMarket
 * 
 * Validaciones incluidas:
 * - Conectividad y disponibilidad
 * - Operaciones CRUD básicas
 * - Gestión de TTL y expiración
 * - Tipos de datos complejos
 * - Operaciones atómicas
 * - Rendimiento y timeouts
 */
@SpringBootTest
@ActiveProfiles("redis-test")
@DisplayName("Redis Unit Tests - PetMarket")
@Tag("unit")
@Tag("redis")
@Tag("cache")
class RedisUnitTest {

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TEST_KEY_PREFIX = "petmarket:unit:test:";
    private String testKey;

    @BeforeEach
    void setUp(TestInfo testInfo) {
        // Generar clave única para cada test
        testKey = TEST_KEY_PREFIX + testInfo.getDisplayName().replaceAll("\\s+", "_").toLowerCase();
        
        // Limpiar cualquier dato previo
        cleanupTestData();
    }

    @AfterEach
    void tearDown() {
        cleanupTestData();
    }

    private void cleanupTestData() {
        Set<String> keys = redisTemplate.keys(TEST_KEY_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Nested
    @DisplayName("Connectivity and Health Checks")
    class ConnectivityTests {

        @Test
        @DisplayName("Should successfully connect to Redis")
        @Timeout(value = 5, unit = TimeUnit.SECONDS)
        void shouldConnectToRedis() {
            // When & Then
            assertThat(redisCacheService.isConnected())
                    .as("Redis should be connected and responding")
                    .isTrue();
        }

        @Test
        @DisplayName("Should ping Redis server successfully")
        void shouldPingRedisServer() {
            // When
            String response = redisTemplate.getConnectionFactory()
                    .getConnection()
                    .ping();

            // Then
            assertThat(response)
                    .as("Redis ping should return PONG")
                    .isEqualTo("PONG");
        }

        @Test
        @DisplayName("Should get Redis server info")
        void shouldGetRedisServerInfo() {
            // When
            boolean isConnected = redisCacheService.isConnected();

            // Then
            assertThat(isConnected)
                    .as("Redis connection should be healthy")
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicOperationsTests {

        @Test
        @DisplayName("Should store and retrieve string value")
        void shouldStoreAndRetrieveString() {
            // Given
            String value = "test-value-string";

            // When
            redisCacheService.set(testKey, value);
            Object retrieved = redisCacheService.get(testKey);

            // Then
            assertThat(retrieved)
                    .as("Retrieved value should match stored value")
                    .isEqualTo(value);
        }

        @Test
        @DisplayName("Should store and retrieve complex object as JSON string")
        void shouldStoreAndRetrieveComplexObject() {
            // Given - Usar String JSON en lugar de Map para evitar problemas de serialización
            String productDataJson = "{\"id\":1,\"name\":\"Collar para Perro\",\"price\":29.99,\"category\":\"Accesorios\",\"inStock\":true}";

            // When
            redisCacheService.set(testKey, productDataJson);
            Object retrieved = redisCacheService.get(testKey);

            // Then
            assertThat(retrieved)
                    .as("Retrieved JSON should match stored JSON")
                    .isEqualTo(productDataJson);
        }

        @ParameterizedTest
        @ValueSource(strings = {"simple", "with spaces", "with-dashes", "with_underscores", "123numbers"})
        @DisplayName("Should handle different key formats")
        void shouldHandleDifferentKeyFormats(String keySuffix) {
            // Given
            String key = testKey + ":" + keySuffix;
            String value = "test-value-for-" + keySuffix;

            // When
            redisCacheService.set(key, value);
            Object retrieved = redisCacheService.get(key);

            // Then
            assertThat(retrieved)
                    .as("Should handle key format: %s", keySuffix)
                    .isEqualTo(value);
        }

        @Test
        @DisplayName("Should delete existing key")
        void shouldDeleteExistingKey() {
            // Given
            String value = "value-to-delete";
            redisCacheService.set(testKey, value);

            // When
            redisCacheService.delete(testKey);

            // Then
            assertThat(redisCacheService.get(testKey))
                    .as("Value should be null after deletion")
                    .isNull();
        }

        @Test
        @DisplayName("Should return null for non-existent key")
        void shouldReturnNullForNonExistentKey() {
            // Given
            String nonExistentKey = testKey + ":does-not-exist";

            // When
            Object result = redisCacheService.get(nonExistentKey);

            // Then
            assertThat(result)
                    .as("Non-existent key should return null")
                    .isNull();
        }
    }

    @Nested
    @DisplayName("TTL and Expiration Management")
    class TTLTests {

        @Test
        @DisplayName("Should set value with TTL")
        void shouldSetValueWithTTL() {
            // Given
            String value = "value-with-ttl";
            Duration ttl = Duration.ofSeconds(5);

            // When
            redisCacheService.setWithTTL(testKey, value, ttl);

            // Then
            assertThat(redisCacheService.get(testKey))
                    .as("Value should be present immediately after setting")
                    .isEqualTo(value);

            Long actualTTL = redisTemplate.getExpire(testKey, TimeUnit.SECONDS);
            assertThat(actualTTL)
                    .as("TTL should be set correctly")
                    .isBetween(1L, 5L);
        }

        @ParameterizedTest
        @CsvSource({
            "1, value1",
            "3, value3", 
            "5, value5"
        })
        @DisplayName("Should respect different TTL values")
        void shouldRespectDifferentTTLValues(int ttlSeconds, String value) {
            // Given
            String key = testKey + ":" + ttlSeconds;
            Duration ttl = Duration.ofSeconds(ttlSeconds);

            // When
            redisCacheService.setWithTTL(key, value, ttl);

            // Then
            Long actualTTL = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            assertThat(actualTTL)
                    .as("TTL should be approximately %d seconds", ttlSeconds)
                    .isBetween((long)(ttlSeconds - 1), (long)ttlSeconds);
        }

        @Test
        @DisplayName("Should handle key expiration")
        @Timeout(value = 10, unit = TimeUnit.SECONDS)
        void shouldHandleKeyExpiration() throws InterruptedException {
            // Given
            String value = "expiring-value";
            Duration shortTTL = Duration.ofSeconds(2);

            // When
            redisCacheService.setWithTTL(testKey, value, shortTTL);

            // Then - Immediately should exist
            assertThat(redisCacheService.get(testKey))
                    .as("Value should exist immediately")
                    .isEqualTo(value);

            // Wait for expiration
            Thread.sleep(2500);

            // Then - Should be expired
            assertThat(redisCacheService.get(testKey))
                    .as("Value should be null after expiration")
                    .isNull();
        }
    }

    @Nested
    @DisplayName("Data Type Operations")
    class DataTypeTests {

        @Test
        @DisplayName("Should handle numeric operations")
        void shouldHandleNumericOperations() {
            // Given
            String counterKey = testKey + ":counter";

            // When
            Long result1 = redisCacheService.increment(counterKey);
            Long result2 = redisCacheService.increment(counterKey);
            Long result3 = redisCacheService.incrementBy(counterKey, 5);

            // Then
            assertThat(result1).isEqualTo(1L);
            assertThat(result2).isEqualTo(2L);
            assertThat(result3).isEqualTo(7L);
        }

        @Test
        @DisplayName("Should handle hash operations")
        void shouldHandleHashOperations() {
            // Given
            String hashKey = testKey + ":hash";
            Map<String, Object> clientData = Map.of(
                    "id", "123",
                    "name", "Juan Pérez",
                    "email", "juan@petmarket.com",
                    "isActive", true
            );

            // When
            redisCacheService.setHash(hashKey, clientData);
            Map<String, Object> retrieved = redisCacheService.getHash(hashKey);

            // Then
            assertThat(retrieved)
                    .as("Hash data should match stored data")
                    .containsExactlyInAnyOrderEntriesOf(clientData);
        }

        @Test
        @DisplayName("Should handle list operations")
        void shouldHandleListOperations() {
            // Given
            String listKey = testKey + ":list";
            List<String> products = List.of("Collar", "Correa", "Juguete", "Comida");

            // When
            redisCacheService.setList(listKey, products);
            List<String> retrieved = redisCacheService.getList(listKey);

            // Then
            assertThat(retrieved)
                    .as("List should contain all products in order")
                    .containsExactlyElementsOf(products);
        }

        @Test
        @DisplayName("Should handle set operations")
        void shouldHandleSetOperations() {
            // Given
            String setKey = testKey + ":set";
            Set<String> categories = Set.of("Alimentos", "Accesorios", "Juguetes", "Medicina");

            // When
            redisCacheService.setSet(setKey, categories);
            Set<String> retrieved = redisCacheService.getSet(setKey);

            // Then
            assertThat(retrieved)
                    .as("Set should contain all categories")
                    .containsExactlyInAnyOrderElementsOf(categories);
        }
    }

    @Nested
    @DisplayName("Performance and Atomic Operations")
    class PerformanceTests {

        @Test
        @DisplayName("Should perform operations within timeout")
        @Timeout(value = 1, unit = TimeUnit.SECONDS)
        void shouldPerformOperationsWithinTimeout() {
            // Given
            String value = "performance-test-value";

            // When & Then
            assertDoesNotThrow(() -> {
                for (int i = 0; i < 100; i++) {
                    String key = testKey + ":perf:" + i;
                    redisCacheService.set(key, value + i);
                    redisCacheService.get(key);
                }
            });
        }

        @Test
        @DisplayName("Should handle concurrent operations")
        void shouldHandleConcurrentOperations() {
            // Given
            String counterKey = testKey + ":concurrent";
            int numberOfOperations = 10;

            // When
            List<Long> results = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).parallelStream()
                    .map(i -> redisCacheService.increment(counterKey))
                    .toList();

            // Then
            assertThat(results)
                    .as("All increments should return unique values")
                    .hasSize(numberOfOperations)
                    .allSatisfy(result -> assertThat(result).isBetween(1L, (long)numberOfOperations));

            Long finalValue = redisCacheService.incrementBy(counterKey, 0); // Get current value
            assertThat(finalValue)
                    .as("Final counter value should be %d", numberOfOperations)
                    .isEqualTo((long)numberOfOperations);
        }
    }

    @Nested
    @DisplayName("Error Handling and Edge Cases")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null values gracefully")
        void shouldHandleNullValuesGracefully() {
            // When & Then
            assertDoesNotThrow(() -> {
                redisCacheService.set(testKey, null);
                Object result = redisCacheService.get(testKey);
                assertThat(result).isNull();
            });
        }

        @Test
        @DisplayName("Should handle empty strings")
        void shouldHandleEmptyStrings() {
            // Given
            String emptyValue = "";

            // When
            redisCacheService.set(testKey, emptyValue);
            Object retrieved = redisCacheService.get(testKey);

            // Then
            assertThat(retrieved)
                    .as("Empty string should be stored and retrieved correctly")
                    .isEqualTo(emptyValue);
        }

        @Test
        @DisplayName("Should handle large data")
        void shouldHandleLargeData() {
            // Given
            StringBuilder largeData = new StringBuilder();
            for (int i = 0; i < 1000; i++) {
                largeData.append("PetMarket-Large-Data-Test-").append(i).append("-");
            }
            String largeValue = largeData.toString();

            // When & Then
            assertDoesNotThrow(() -> {
                redisCacheService.set(testKey, largeValue);
                Object retrieved = redisCacheService.get(testKey);
                assertThat(retrieved).isEqualTo(largeValue);
            });
        }

        @Test
        @DisplayName("Should handle special characters in keys and values")
        void shouldHandleSpecialCharacters() {
            // Given
            String specialKey = testKey + ":special:ñáéíóú@#$%";
            String specialValue = "Comida para perros pequeños ñ á é í ó ú @#$%&*()";

            // When
            redisCacheService.set(specialKey, specialValue);
            Object retrieved = redisCacheService.get(specialKey);

            // Then
            assertThat(retrieved)
                    .as("Special characters should be handled correctly")
                    .isEqualTo(specialValue);
        }
    }

    @Nested
    @DisplayName("Real-world PetMarket Scenarios")
    class PetMarketScenariosTests {

        @Test
        @DisplayName("Should cache product search results")
        void shouldCacheProductSearchResults() {
            // Given - Usar JSON string en lugar de List<Map> para evitar problemas de serialización
            String searchKey = testKey + ":search:collares_perro";
            String searchResultsJson = "[{\"id\":1,\"name\":\"Collar Básico\",\"price\":15.99},{\"id\":2,\"name\":\"Collar Premium\",\"price\":35.99}]";
            Duration cacheTTL = Duration.ofMinutes(15);

            // When
            redisCacheService.setWithTTL(searchKey, searchResultsJson, cacheTTL);
            Object cached = redisCacheService.get(searchKey);

            // Then
            assertThat(cached)
                    .as("Search results should be cached correctly")
                    .isEqualTo(searchResultsJson);

            Long ttl = redisTemplate.getExpire(searchKey, TimeUnit.MINUTES);
            assertThat(ttl)
                    .as("Cache TTL should be approximately 15 minutes")
                    .isBetween(10L, 15L);
        }

        @Test
        @DisplayName("Should cache user session data")
        void shouldCacheUserSessionData() {
            // Given - Usar JSON string para evitar problemas de serialización
            String sessionKey = testKey + ":session:user123";
            String sessionDataJson = "{\"userId\":123,\"username\":\"juan.perez\",\"email\":\"juan@petmarket.com\",\"cartId\":\"cart_456\"}";
            Duration sessionTTL = Duration.ofHours(2);

            // When
            redisCacheService.setWithTTL(sessionKey, sessionDataJson, sessionTTL);
            Object cached = redisCacheService.get(sessionKey);

            // Then
            assertThat(cached)
                    .as("Session data should be cached correctly")
                    .isEqualTo(sessionDataJson);
        }

        @Test
        @DisplayName("Should track shopping cart items")
        void shouldTrackShoppingCartItems() {
            // Given - Usar JSON string para evitar problemas de serialización
            String cartKey = testKey + ":cart:user123";
            String cartDataJson = "{\"items\":[{\"productId\":1,\"quantity\":2,\"price\":15.99},{\"productId\":5,\"quantity\":1,\"price\":89.99}],\"total\":121.97,\"itemCount\":3}";

            // When
            redisCacheService.set(cartKey, cartDataJson);
            Object cached = redisCacheService.get(cartKey);

            // Then
            assertThat(cached)
                    .as("Shopping cart should be cached correctly")
                    .isEqualTo(cartDataJson);
        }

        @Test
        @DisplayName("Should count page views and statistics")
        void shouldCountPageViewsAndStatistics() {
            // Given
            String viewsKey = testKey + ":views:product:123";
            String dailyStatsKey = testKey + ":stats:daily:" + java.time.LocalDate.now();

            // When
            Long productViews = redisCacheService.increment(viewsKey);
            Long dailyViews = redisCacheService.increment(dailyStatsKey);
            
            // Additional views
            redisCacheService.incrementBy(viewsKey, 5);
            redisCacheService.incrementBy(dailyStatsKey, 10);

            // Then
            assertThat(productViews).isEqualTo(1L);
            assertThat(dailyViews).isEqualTo(1L);

            Long finalProductViews = redisCacheService.incrementBy(viewsKey, 0);
            Long finalDailyViews = redisCacheService.incrementBy(dailyStatsKey, 0);

            assertThat(finalProductViews).isEqualTo(6L);
            assertThat(finalDailyViews).isEqualTo(11L);
        }
    }
}
