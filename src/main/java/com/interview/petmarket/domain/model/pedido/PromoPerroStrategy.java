package com.interview.petmarket.domain.model.pedido;

import com.interview.petmarket.domain.model.producto.EspecieAnimal;
import com.interview.petmarket.domain.model.producto.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Estrategia de promoción para productos de perros - 15% de descuento.
 * Implementa el principio de Responsabilidad Única (SRP) de SOLID.
 */
public class PromoPerroStrategy implements PricingStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(PromoPerroStrategy.class);
    private static final BigDecimal DESCUENTO_PERRO = new BigDecimal("0.15"); // 15%
    
    @Override
    public BigDecimal calcularPrecio(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal precioUnitario = producto.getPrecio();
        BigDecimal precioBase = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        
        // Aplicar descuento solo si es para perros
        if (EspecieAnimal.PERRO.equals(producto.getEspecie())) {
            BigDecimal descuento = precioBase.multiply(DESCUENTO_PERRO);
            BigDecimal precioConDescuento = precioBase.subtract(descuento);
            
            logger.debug("Promo perro aplicada: {} - {} (15%) = {}", 
                        precioBase, descuento, precioConDescuento);
            
            return precioConDescuento.setScale(2, RoundingMode.HALF_UP);
        }
        
        // Si no es para perros, precio base
        logger.debug("Producto no es para perros, precio base: {}", precioBase);
        return precioBase;
    }
    
    @Override
    public String getNombre() {
        return "PROMO_PERRO";
    }
    
    @Override
    public String getDescripcion() {
        return "Promoción especial para productos de perros - 15% de descuento";
    }
}
