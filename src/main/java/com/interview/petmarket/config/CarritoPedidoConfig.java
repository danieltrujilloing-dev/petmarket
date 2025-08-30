package com.interview.petmarket.config;

import com.interview.petmarket.application.services.CarritoApplicationService;
import com.interview.petmarket.application.services.PedidoApplicationService;
import com.interview.petmarket.domain.ports.in.GestionarCarritoUseCase;
import com.interview.petmarket.domain.ports.in.ProcesarPedidoUseCase;
import com.interview.petmarket.domain.ports.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de beans para el caso de uso de Carrito y Pedido.
 * Implementa la inyección de dependencias siguiendo el principio DIP de SOLID.
 */
@Configuration
public class CarritoPedidoConfig {
    
    /**
     * Bean para el servicio de gestión de carrito.
     */
    @Bean("carritoApplicationService")
    public GestionarCarritoUseCase gestionarCarritoUseCase(
            CarritoRepositoryPort carritoRepository,
            ProductoRepositoryPort productoRepository,
            EventPublisherPort eventPublisher) {
        
        return new CarritoApplicationService(
                carritoRepository,
                productoRepository,
                eventPublisher
        );
    }
    
    /**
     * Bean para el servicio de procesamiento de pedidos.
     */
    @Bean("pedidoApplicationService")
    public ProcesarPedidoUseCase procesarPedidoUseCase(
            PedidoRepositoryPort pedidoRepository,
            CarritoRepositoryPort carritoRepository,
            ProductoRepositoryPort productoRepository,
            InventarioRepositoryPort inventarioRepository,
            PricingStrategyPort pricingStrategyPort,
            EventPublisherPort eventPublisher) {
        
        return new PedidoApplicationService(
                pedidoRepository,
                carritoRepository,
                productoRepository,
                inventarioRepository,
                pricingStrategyPort,
                eventPublisher
        );
    }
}
