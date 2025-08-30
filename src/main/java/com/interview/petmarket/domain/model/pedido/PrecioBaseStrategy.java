package com.interview.petmarket.domain.model.pedido;

import com.interview.petmarket.domain.model.producto.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Estrategia de precio base - sin descuentos.
 * Implementa el principio de Responsabilidad Única (SRP) de SOLID.
 */
public class PrecioBaseStrategy implements PricingStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(PrecioBaseStrategy.class);
    
    @Override
    public BigDecimal calcularPrecio(Producto producto, int cantidad) {
        if (producto == null || cantidad <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal precioUnitario = producto.getPrecio();
        BigDecimal precioTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        
        logger.debug("Precio base calculado: {} x {} = {}", 
                    precioUnitario, cantidad, precioTotal);
        
        return precioTotal;
    }
    
    @Override
    public String getNombre() {
        return "PRECIO_BASE";
    }
    
    @Override
    public String getDescripcion() {
        return "Precio base sin descuentos ni promociones";
    }
}
