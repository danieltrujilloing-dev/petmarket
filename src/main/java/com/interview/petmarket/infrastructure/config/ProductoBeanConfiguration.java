package com.interview.petmarket.infrastructure.config;

import com.interview.petmarket.application.services.ProductoApplicationService;
import com.interview.petmarket.domain.ports.in.GestionarProductoUseCase;
import com.interview.petmarket.domain.ports.in.ListarProductosUseCase;
import com.interview.petmarket.domain.ports.out.ProductoCachePort;
import com.interview.petmarket.domain.ports.out.ProductoRepositoryPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuración de beans para el módulo de productos.
 */
@Configuration
public class ProductoBeanConfiguration {

    /**
     * Bean para el caso de uso de listar productos.
     */
    @Bean
    public ListarProductosUseCase listarProductosUseCase(
            ProductoRepositoryPort productoRepository,
            ProductoCachePort productoCache,
            @Value("${petmarket.cache.productos.ttl:PT15M}") Duration cacheTTL) {
        
        return new ProductoApplicationService(productoRepository, productoCache, cacheTTL);
    }

    /**
     * Bean para el caso de uso de gestionar productos.
     */
    @Bean
    public GestionarProductoUseCase gestionarProductoUseCase(
            ProductoRepositoryPort productoRepository,
            ProductoCachePort productoCache,
            @Value("${petmarket.cache.productos.ttl:PT15M}") Duration cacheTTL) {
        
        return new ProductoApplicationService(productoRepository, productoCache, cacheTTL);
    }
}
