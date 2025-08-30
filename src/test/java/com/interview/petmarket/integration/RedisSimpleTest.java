package com.interview.petmarket.integration;

import com.interview.petmarket.infrastructure.cache.RedisCacheService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test simple para verificar que Redis está funcionando correctamente
 * en el marketplace PetMarket.
 * 
 * Este test se enfoca únicamente en verificar:
 * - Conectividad con Redis
 * - Operaciones básicas de cache
 * - TTL y expiración
 * - Tipos de datos complejos
 */
@SpringBootTest
@ActiveProfiles("redis-test")
@DisplayName("Redis Simple Integration Tests")
@Tag("integration")
@Tag("redis")
class RedisSimpleTest {

    @Autowired
    private RedisCacheService redisCacheService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TEST_KEY_PREFIX = "test:simple:";

    @BeforeEach
    void setUp() {
        cleanupTestKeys();
    }

    @AfterEach
    void tearDown() {
        cleanupTestKeys();
    }

    private void cleanupTestKeys() {
        Set<String> testKeys = redisTemplate.keys(TEST_KEY_PREFIX + "*");
        if (testKeys != null && !testKeys.isEmpty()) {
            redisTemplate.delete(testKeys);
        }
    }

    @Test
    @DisplayName("Should connect to Redis successfully")
    void shouldConnectToRedis() {
        // When & Then
        assertThat(redisCacheService.isConnected())
                .as("Redis should be connected and responding")
                .isTrue();
    }

    @Test
    void shouldStoreAndRetrieveBasicData() {
        // Given
        String key = TEST_KEY_PREFIX + "basic:data";
        String value = "PetMarket Redis Test";

        // When
        redisCacheService.set(key, value);
        Object retrieved = redisCacheService.get(key);

        // Then
        assertThat(retrieved).isEqualTo(value);
        assertThat(redisCacheService.exists(key)).isTrue();
    }

    @Test
    void shouldHandleMarketplaceData() {
        // Given - Datos típicos del marketplace
        String productKey = TEST_KEY_PREFIX + "product:stats";
        Map<String, Object> productStats = new HashMap<>();
        productStats.put("views", 150L);
        productStats.put("likes", 25L);
        productStats.put("inCart", 5L);
        productStats.put("lastViewed", System.currentTimeMillis());

        String clientKey = TEST_KEY_PREFIX + "client:session";
        Map<String, Object> clientSession = new HashMap<>();
        clientSession.put("clientId", "CLIENT_001");
        clientSession.put("cartTotal", 299.99);
        clientSession.put("itemCount", 3);

        // When
        redisCacheService.set(productKey, productStats, Duration.ofHours(1));
        redisCacheService.set(clientKey, clientSession, Duration.ofMinutes(30));

        // Then
        Object retrievedProduct = redisCacheService.get(productKey);
        Object retrievedClient = redisCacheService.get(clientKey);

        assertThat(retrievedProduct).isNotNull();
        assertThat(retrievedClient).isNotNull();
        assertThat(redisCacheService.exists(productKey)).isTrue();
        assertThat(redisCacheService.exists(clientKey)).isTrue();
    }

    @Test
    void shouldHandleTtlCorrectly() {
        // Given
        String shortKey = TEST_KEY_PREFIX + "short:ttl";
        String longKey = TEST_KEY_PREFIX + "long:ttl";

        // When
        redisCacheService.set(shortKey, "Short lived data", Duration.ofSeconds(2));
        redisCacheService.set(longKey, "Long lived data", Duration.ofMinutes(10));

        // Then - Initially both should exist
        assertThat(redisCacheService.exists(shortKey)).isTrue();
        assertThat(redisCacheService.exists(longKey)).isTrue();

        // Check TTL values
        Duration shortTtl = redisCacheService.getTimeToLive(shortKey);
        Duration longTtl = redisCacheService.getTimeToLive(longKey);

        assertThat(shortTtl.getSeconds()).isLessThanOrEqualTo(2L);
        assertThat(longTtl.getSeconds()).isGreaterThan(Duration.ofMinutes(5).getSeconds());

        // Wait for short TTL to expire
        try {
            Thread.sleep(2100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Then - Only long-lived data should exist
        assertThat(redisCacheService.exists(shortKey)).isFalse();
        assertThat(redisCacheService.exists(longKey)).isTrue();
    }

    @Test
    void shouldIncrementCounters() {
        // Given - Contadores típicos del marketplace
        String visitsKey = TEST_KEY_PREFIX + "visits:daily";
        String salesKey = TEST_KEY_PREFIX + "sales:product:001";

        // When - Simular actividad
        redisCacheService.increment(visitsKey); // Primera visita
        redisCacheService.increment(visitsKey, 5L); // 5 visitas más

        redisCacheService.increment(salesKey, 3L); // 3 ventas

        // Then
        Object totalVisits = redisCacheService.get(visitsKey);
        Object totalSales = redisCacheService.get(salesKey);

        assertThat(totalVisits).isEqualTo(6); // 1 + 5
        assertThat(totalSales).isEqualTo(3);
    }

    @Test
    void shouldDeleteData() {
        // Given
        String key1 = TEST_KEY_PREFIX + "delete:test1";
        String key2 = TEST_KEY_PREFIX + "delete:test2";

        redisCacheService.set(key1, "Data 1");
        redisCacheService.set(key2, "Data 2");

        // When
        boolean deleted1 = redisCacheService.delete(key1);
        boolean deleted2 = redisCacheService.delete(key2);

        // Then
        assertThat(deleted1).isTrue();
        assertThat(deleted2).isTrue();
        assertThat(redisCacheService.exists(key1)).isFalse();
        assertThat(redisCacheService.exists(key2)).isFalse();
    }

    @Test
    void shouldFindKeysByPattern() {
        // Given - Crear varias claves con patrones
        redisCacheService.set(TEST_KEY_PREFIX + "product:001", "Product 1");
        redisCacheService.set(TEST_KEY_PREFIX + "product:002", "Product 2");
        redisCacheService.set(TEST_KEY_PREFIX + "client:001", "Client 1");

        // When
        Set<String> productKeys = redisCacheService.keys(TEST_KEY_PREFIX + "product:*");
        Set<String> allTestKeys = redisCacheService.keys(TEST_KEY_PREFIX + "*");

        // Then
        assertThat(productKeys).hasSize(2);
        assertThat(allTestKeys).hasSize(3);
        assertThat(productKeys).allMatch(key -> key.contains("product:"));
    }
}
