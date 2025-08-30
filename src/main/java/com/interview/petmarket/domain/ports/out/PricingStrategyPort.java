package com.interview.petmarket.domain.ports.out;

import com.interview.petmarket.domain.model.pedido.PricingStrategy;

/**
 * Puerto de salida para obtener estrategias de pricing.
 * Implementa el principio de Inversión de Dependencias (DIP) de SOLID.
 */
public interface PricingStrategyPort {
    
    /**
     * Obtiene la estrategia de pricing por defecto.
     */
    PricingStrategy getDefaultStrategy();
    
    /**
     * Obtiene una estrategia específica por nombre.
     */
    PricingStrategy getStrategy(String strategyName);
    
    /**
     * Obtiene la estrategia más apropiada para un cliente.
     * Puede basarse en historial, membresía, etc.
     */
    PricingStrategy getStrategyForClient(Long clienteId);
}
