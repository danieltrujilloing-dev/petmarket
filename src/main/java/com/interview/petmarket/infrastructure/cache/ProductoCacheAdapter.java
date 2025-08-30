package com.interview.petmarket.infrastructure.cache;

import com.interview.petmarket.domain.model.producto.FiltroProducto;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.ports.out.ProductoCachePort;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Adaptador que implementa ProductoCachePort usando Redis con Lombok y TTL configurable.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductoCacheAdapter implements ProductoCachePort {

    private static final String CACHE_PREFIX = "petmarket:productos:";
    private static final String CACHE_INDIVIDUAL_PREFIX = "petmarket:producto:";

    private final RedisCacheService redisCacheService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    @Value("${petmarket.cache.productos.ttl:PT15M}")
    private Duration defaultTTL;
    
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public void guardarProductos(String clave, List<Producto> productos, Duration ttl) {
        Duration efectiveTTL = ttl != null ? ttl : defaultTTL;
        log.debug("Caching products with key: {}, count: {}, TTL: {}", clave, productos.size(), efectiveTTL);
        
        try {
            String fullKey = CACHE_PREFIX + clave;
            String jsonProductos = objectMapper.writeValueAsString(productos);
            redisCacheService.setWithTTL(fullKey, jsonProductos, efectiveTTL);
            
            log.debug("Products cached successfully with key: {}", fullKey);
        } catch (JsonProcessingException e) {
            log.error("Error serializing products for cache: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<Producto> obtenerProductos(String clave) {
        log.debug("Retrieving products from cache with key: {}", clave);
        
        try {
            String fullKey = CACHE_PREFIX + clave;
            Object cached = redisCacheService.get(fullKey);
            
            if (cached == null) {
                log.debug("No cached products found for key: {}", fullKey);
                return null;
            }

            String jsonProductos = cached.toString();
            List<Producto> productos = objectMapper.readValue(jsonProductos, new TypeReference<List<Producto>>() {});
            
            log.debug("Retrieved {} products from cache for key: {}", productos.size(), fullKey);
            return productos;
            
        } catch (JsonProcessingException e) {
            log.error("Error deserializing products from cache: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void guardarProducto(Long id, Producto producto, Duration ttl) {
        log.debug("Caching individual product: {} with TTL: {}", id, ttl);
        
        try {
            String fullKey = CACHE_INDIVIDUAL_PREFIX + id;
            String jsonProducto = objectMapper.writeValueAsString(producto);
            redisCacheService.setWithTTL(fullKey, jsonProducto, ttl);
            
            log.debug("Product cached successfully: {}", id);
        } catch (JsonProcessingException e) {
            log.error("Error serializing product {} for cache: {}", id, e.getMessage(), e);
        }
    }

    @Override
    public Producto obtenerProducto(Long id) {
        log.debug("Retrieving individual product from cache: {}", id);
        
        try {
            String fullKey = CACHE_INDIVIDUAL_PREFIX + id;
            Object cached = redisCacheService.get(fullKey);
            
            if (cached == null) {
                log.debug("No cached product found for ID: {}", id);
                return null;
            }

            String jsonProducto = cached.toString();
            Producto producto = objectMapper.readValue(jsonProducto, Producto.class);
            
            log.debug("Retrieved product from cache: {}", id);
            return producto;
            
        } catch (JsonProcessingException e) {
            log.error("Error deserializing product {} from cache: {}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void invalidarCacheProductos() {
        log.info("Invalidating all product caches");
        
        try {
            // Invalidar cache de listas de productos
            Set<String> clavesProductos = redisCacheService.keys(CACHE_PREFIX + "*");
            if (clavesProductos != null && !clavesProductos.isEmpty()) {
                for (String clave : clavesProductos) {
                    redisCacheService.delete(clave);
                }
                log.info("Invalidated {} product list caches", clavesProductos.size());
            }
            
            // También invalidar productos individuales para forzar refresh
            Set<String> clavesIndividuales = redisCacheService.keys(CACHE_INDIVIDUAL_PREFIX + "*");
            if (clavesIndividuales != null && !clavesIndividuales.isEmpty()) {
                for (String clave : clavesIndividuales) {
                    redisCacheService.delete(clave);
                }
                log.info("Invalidated {} individual product caches", clavesIndividuales.size());
            }
            
        } catch (Exception e) {
            log.error("Error invalidating product caches: {}", e.getMessage(), e);
        }
    }

    @Override
    public void invalidarCacheFiltro(FiltroProducto filtro) {
        if (filtro == null) {
            return;
        }
        
        log.debug("Invalidating cache for specific filter: {}", filtro);
        
        String claveCache = filtro.generarClaveCache();
        String fullKey = CACHE_PREFIX + claveCache;
        
        boolean eliminado = redisCacheService.delete(fullKey);
        log.debug("Filter cache invalidated: {} (deleted: {})", fullKey, eliminado);
    }

    @Override
    public void invalidarCacheProducto(Long id) {
        log.debug("Invalidating cache for product: {}", id);
        
        String fullKey = CACHE_INDIVIDUAL_PREFIX + id;
        boolean eliminado = redisCacheService.delete(fullKey);
        
        log.debug("Individual product cache invalidated: {} (deleted: {})", id, eliminado);
    }

    @Override
    public void invalidarCacheTipo(String tipo) {
        log.debug("Invalidating cache for product type: {}", tipo);
        
        try {
            String patron = CACHE_PREFIX + "*tipo*" + tipo + "*";
            Set<String> claves = redisCacheService.keys(patron);
            
            if (claves != null && !claves.isEmpty()) {
                for (String clave : claves) {
                    redisCacheService.delete(clave);
                }
                log.debug("Invalidated {} caches for type: {}", claves.size(), tipo);
            }
        } catch (Exception e) {
            log.error("Error invalidating type cache for {}: {}", tipo, e.getMessage(), e);
        }
    }

    @Override
    public void invalidarCacheEspecie(String especie) {
        log.debug("Invalidating cache for species: {}", especie);
        
        try {
            String patron = CACHE_PREFIX + "*especie*" + especie + "*";
            Set<String> claves = redisCacheService.keys(patron);
            
            if (claves != null && !claves.isEmpty()) {
                for (String clave : claves) {
                    redisCacheService.delete(clave);
                }
                log.debug("Invalidated {} caches for species: {}", claves.size(), especie);
            }
        } catch (Exception e) {
            log.error("Error invalidating species cache for {}: {}", especie, e.getMessage(), e);
        }
    }

    @Override
    public boolean existeCache(String clave) {
        String fullKey = CACHE_PREFIX + clave;
        boolean existe = redisCacheService.exists(fullKey);
        
        log.debug("Cache exists for key {}: {}", fullKey, existe);
        return existe;
    }

    @Override
    public Duration obtenerTTLRestante(String clave) {
        String fullKey = CACHE_PREFIX + clave;
        Duration ttl = redisCacheService.getTimeToLive(fullKey);
        
        log.debug("TTL for key {}: {}", fullKey, ttl);
        return ttl;
    }

    // ================ MÉTODOS ADICIONALES ÚTILES ================

    /**
     * Invalidar cache por múltiples patrones.
     */
    public void invalidarCachePatrones(String... patrones) {
        log.debug("Invalidating cache for multiple patterns: {}", (Object) patrones);
        
        for (String patron : patrones) {
            try {
                String fullPattern = CACHE_PREFIX + patron;
                Set<String> claves = redisCacheService.keys(fullPattern);
                
                if (claves != null && !claves.isEmpty()) {
                    for (String clave : claves) {
                        redisCacheService.delete(clave);
                    }
                    log.debug("Invalidated {} caches for pattern: {}", claves.size(), patron);
                }
            } catch (Exception e) {
                log.error("Error invalidating cache for pattern {}: {}", patron, e.getMessage(), e);
            }
        }
    }

    /**
     * Obtiene estadísticas del cache de productos.
     */
    public CacheStats obtenerEstadisticasCache() {
        try {
            Set<String> clavesProductos = redisCacheService.keys(CACHE_PREFIX + "*");
            Set<String> clavesIndividuales = redisCacheService.keys(CACHE_INDIVIDUAL_PREFIX + "*");
            
            int totalListas = clavesProductos != null ? clavesProductos.size() : 0;
            int totalIndividuales = clavesIndividuales != null ? clavesIndividuales.size() : 0;
            
            return new CacheStats(totalListas, totalIndividuales);
            
        } catch (Exception e) {
            log.error("Error getting cache statistics: {}", e.getMessage(), e);
            return new CacheStats(0, 0);
        }
    }

    /**
     * Clase para estadísticas del cache.
     */
    public static class CacheStats {
        private final int cachesListas;
        private final int cachesIndividuales;

        public CacheStats(int cachesListas, int cachesIndividuales) {
            this.cachesListas = cachesListas;
            this.cachesIndividuales = cachesIndividuales;
        }

        public int getCachesListas() { return cachesListas; }
        public int getCachesIndividuales() { return cachesIndividuales; }
        public int getTotal() { return cachesListas + cachesIndividuales; }

        @Override
        public String toString() {
            return String.format("CacheStats{listas=%d, individuales=%d, total=%d}", 
                               cachesListas, cachesIndividuales, getTotal());
        }
    }
}
