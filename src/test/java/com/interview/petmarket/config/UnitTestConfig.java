package com.interview.petmarket.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.mock;

/**
 * Configuración para tests unitarios que no requieren servicios externos.
 * Proporciona mocks para componentes que normalmente requieren Redis, Kafka, etc.
 */
@TestConfiguration
@Profile("unit-test")
public class UnitTestConfig {

    /**
     * Mock de RedisTemplate para tests unitarios que no requieren Redis real.
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        return mock(RedisTemplate.class);
    }
}
