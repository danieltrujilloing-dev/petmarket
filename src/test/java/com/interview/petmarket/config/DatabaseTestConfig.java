package com.interview.petmarket.config;

import com.interview.petmarket.domain.ports.out.ProductoCachePort;
import com.interview.petmarket.infrastructure.cache.RedisCacheService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.mock;

/**
 * Configuración para tests que solo requieren base de datos (PostgreSQL).
 * Proporciona mocks para componentes que requieren Redis.
 */
@TestConfiguration
@Profile("database-test")
public class DatabaseTestConfig {

    /**
     * Mock de RedisTemplate para tests que no requieren Redis real.
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        return mock(RedisTemplate.class);
    }

    /**
     * Mock de RedisCacheService para tests que no requieren Redis real.
     */
    @Bean
    @Primary
    public RedisCacheService redisCacheService() {
        return mock(RedisCacheService.class);
    }

    /**
     * Mock de ProductoCachePort para tests que no requieren cache real.
     */
    @Bean
    @Primary
    public ProductoCachePort productoCachePort() {
        return mock(ProductoCachePort.class);
    }
}
