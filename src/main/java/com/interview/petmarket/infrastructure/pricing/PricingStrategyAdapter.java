package com.interview.petmarket.infrastructure.pricing;

import com.interview.petmarket.domain.model.pedido.PrecioBaseStrategy;
import com.interview.petmarket.domain.model.pedido.PricingStrategy;
import com.interview.petmarket.domain.model.pedido.PromoPerroStrategy;
import com.interview.petmarket.domain.ports.out.PricingStrategyPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Adaptador para estrategias de pricing.
 * Implementa el patrón Strategy y el principio Open/Closed de SOLID.
 */
@Component
public class PricingStrategyAdapter implements PricingStrategyPort {
    
    private static final Logger logger = LoggerFactory.getLogger(PricingStrategyAdapter.class);
    
    private final Map<String, PricingStrategy> strategies;
    private final PricingStrategy defaultStrategy;
    
    public PricingStrategyAdapter() {
        this.strategies = new HashMap<>();
        this.defaultStrategy = new PrecioBaseStrategy();
        
        // Registrar estrategias disponibles
        registerStrategy(new PrecioBaseStrategy());
        registerStrategy(new PromoPerroStrategy());
        
        logger.info("PricingStrategyAdapter inicializado con {} estrategias", strategies.size());
    }
    
    @Override
    public PricingStrategy getDefaultStrategy() {
        logger.debug("Obteniendo estrategia por defecto: {}", defaultStrategy.getNombre());
        return defaultStrategy;
    }
    
    @Override
    public PricingStrategy getStrategy(String strategyName) {
        logger.debug("Obteniendo estrategia: {}", strategyName);
        
        PricingStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            logger.warn("Estrategia no encontrada: {}, usando estrategia por defecto", strategyName);
            return defaultStrategy;
        }
        
        return strategy;
    }
    
    @Override
    public PricingStrategy getStrategyForClient(Long clienteId) {
        logger.debug("Obteniendo estrategia para cliente: {}", clienteId);
        
        // Lógica de negocio para determinar la estrategia según el cliente
        // Por ahora, aplicamos promo de perro para clientes pares, precio base para impares
        if (clienteId != null && clienteId % 2 == 0) {
            logger.debug("Cliente {} elegible para promo perro", clienteId);
            return getStrategy("PROMO_PERRO");
        } else {
            logger.debug("Cliente {} usando precio base", clienteId);
            return getDefaultStrategy();
        }
    }
    
    /**
     * Registra una nueva estrategia.
     * Permite extensibilidad sin modificar código existente (OCP).
     */
    private void registerStrategy(PricingStrategy strategy) {
        strategies.put(strategy.getNombre(), strategy);
        logger.debug("Estrategia registrada: {} - {}", strategy.getNombre(), strategy.getDescripcion());
    }
    
    /**
     * Obtiene todas las estrategias disponibles.
     * Útil para debugging y administración.
     */
    public Map<String, String> getAvailableStrategies() {
        Map<String, String> available = new HashMap<>();
        strategies.forEach((name, strategy) -> available.put(name, strategy.getDescripcion()));
        return available;
    }
}
