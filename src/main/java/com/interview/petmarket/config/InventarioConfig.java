package com.interview.petmarket.config;

import com.interview.petmarket.application.services.InventarioApplicationService;
import com.interview.petmarket.application.services.TareasReposicionApplicationService;
import com.interview.petmarket.domain.ports.in.GestionarInventarioUseCase;
import com.interview.petmarket.domain.ports.in.GestionarTareasReposicionUseCase;
import com.interview.petmarket.domain.ports.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración Spring para el caso de uso de Inventario.
 * Define los beans de los servicios de aplicación y sus dependencias.
 * 
 * Implementa el principio de Inversión de Dependencias (DIP) de SOLID.
 */
@Configuration
public class InventarioConfig {
    
    /**
     * Bean para el servicio de gestión de inventario.
     */
    @Bean
    public GestionarInventarioUseCase gestionarInventarioUseCase(
            InventarioRepositoryPort inventarioRepository,
            PedidoRepositoryPort pedidoRepository,
            ProductoRepositoryPort productoRepository,
            EventPublisherPort eventPublisher) {
        
        return new InventarioApplicationService(
                inventarioRepository,
                pedidoRepository,
                productoRepository,
                eventPublisher
        );
    }
    
    /**
     * Bean para el servicio de gestión de tareas de reposición.
     */
    @Bean
    public GestionarTareasReposicionUseCase gestionarTareasReposicionUseCase(
            TareaReposicionRepositoryPort tareaRepository) {
        
        return new TareasReposicionApplicationService(tareaRepository);
    }
}
