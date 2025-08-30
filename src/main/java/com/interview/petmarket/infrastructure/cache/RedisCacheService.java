package com.interview.petmarket.infrastructure.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servicio para operaciones de cache con Redis.
 * 
 * Proporciona métodos básicos para:
 * - Almacenar y recuperar datos con TTL
 * - Verificar existencia de claves
 * - Eliminar datos del cache
 * - Obtener métricas básicas
 */
@Service
public class RedisCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Almacena un valor en Redis con TTL especificado.
     */
    public void set(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    /**
     * Almacena un valor en Redis sin TTL (persistente hasta eliminación manual).
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * Recupera un valor de Redis.
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Recupera un valor de Redis con tipo específico.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }

    /**
     * Verifica si una clave existe en Redis.
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Elimina una clave de Redis.
     */
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    /**
     * Elimina múltiples claves de Redis.
     */
    public long delete(String... keys) {
        Long deleted = redisTemplate.delete(Set.of(keys));
        return deleted != null ? deleted : 0;
    }

    /**
     * Establece un TTL para una clave existente.
     */
    public boolean expire(String key, Duration ttl) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, ttl));
    }

    /**
     * Obtiene el TTL restante de una clave.
     */
    public Duration getTimeToLive(String key) {
        Long ttl = redisTemplate.getExpire(key);
        return ttl != null && ttl > 0 ? Duration.ofSeconds(ttl) : Duration.ZERO;
    }

    /**
     * Incrementa un valor numérico en Redis.
     */
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * Incrementa un valor numérico en Redis por un delta específico.
     */
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * Obtiene todas las claves que coinciden con un patrón.
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * Limpia todas las claves de la base de datos actual.
     * ⚠️ USAR CON PRECAUCIÓN - solo para tests.
     */
    public void flushDb() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    /**
     * Verifica la conectividad con Redis.
     */
    public boolean isConnected() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ================== Métodos adicionales para tests ==================

    /**
     * Almacena un valor con TTL - alias para compatibilidad con tests.
     */
    public void setWithTTL(String key, Object value, Duration ttl) {
        set(key, value, ttl);
    }

    /**
     * Incrementa por un valor específico - alias para compatibilidad con tests.
     */
    public Long incrementBy(String key, long delta) {
        return increment(key, delta);
    }

    /**
     * Operaciones con Hash - almacena un mapa como hash en Redis.
     */
    public void setHash(String key, Map<String, Object> hashMap) {
        redisTemplate.opsForHash().putAll(key, hashMap);
    }

    /**
     * Operaciones con Hash - recupera un hash completo de Redis.
     */
    public Map<String, Object> getHash(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        return entries.entrySet().stream()
                .collect(java.util.stream.Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        Map.Entry::getValue
                ));
    }

    /**
     * Operaciones con List - almacena una lista en Redis.
     */
    public void setList(String key, List<String> list) {
        redisTemplate.delete(key); // Limpiar lista existente
        if (!list.isEmpty()) {
            redisTemplate.opsForList().rightPushAll(key, list.toArray());
        }
    }

    /**
     * Operaciones con List - recupera una lista completa de Redis.
     */
    public List<String> getList(String key) {
        Long size = redisTemplate.opsForList().size(key);
        if (size == null || size == 0) {
            return List.of();
        }
        List<Object> objects = redisTemplate.opsForList().range(key, 0, -1);
        return objects != null ? objects.stream()
                .map(Object::toString)
                .toList() : List.of();
    }

    /**
     * Operaciones con Set - almacena un conjunto en Redis.
     */
    public void setSet(String key, Set<String> set) {
        redisTemplate.delete(key); // Limpiar set existente
        if (!set.isEmpty()) {
            redisTemplate.opsForSet().add(key, set.toArray());
        }
    }

    /**
     * Operaciones con Set - recupera un conjunto completo de Redis.
     */
    public Set<String> getSet(String key) {
        Set<Object> objects = redisTemplate.opsForSet().members(key);
        return objects != null ? objects.stream()
                .map(Object::toString)
                .collect(java.util.stream.Collectors.toSet()) : Set.of();
    }
}
