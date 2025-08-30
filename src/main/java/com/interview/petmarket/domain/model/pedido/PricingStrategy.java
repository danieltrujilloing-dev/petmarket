package com.interview.petmarket.domain.model.pedido;

import com.interview.petmarket.domain.model.producto.Producto;

import java.math.BigDecimal;

/**
 * Strategy Pattern para cálculo de precios.
 * Implementa el principio Abierto/Cerrado (OCP) de SOLID.
 * 
 * Permite agregar nuevas estrategias de pricing sin modificar código existente.
 */
public interface PricingStrategy {
    
    /**
     * Calcula el precio final para un producto y cantidad.
     * 
     * @param producto Producto a calcular
     * @param cantidad Cantidad del producto
     * @return Precio total calculado
     */
    BigDecimal calcularPrecio(Producto producto, int cantidad);
    
    /**
     * Nombre de la estrategia para logging/debugging.
     */
    String getNombre();
    
    /**
     * Descripción de la estrategia.
     */
    String getDescripcion();
}
