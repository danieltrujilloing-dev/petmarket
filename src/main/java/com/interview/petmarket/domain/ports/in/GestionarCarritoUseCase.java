package com.interview.petmarket.domain.ports.in;

import com.interview.petmarket.domain.model.carrito.Carrito;

/**
 * Puerto de entrada para gestionar el carrito de compras.
 * Implementa el principio de Segregación de Interfaces (ISP) de SOLID.
 */
public interface GestionarCarritoUseCase {
    
    /**
     * Obtiene el carrito de un cliente.
     * Si no existe, crea uno nuevo.
     */
    Carrito obtenerCarrito(Long clienteId);
    
    /**
     * Agrega un item al carrito.
     * Reglas:
     * - No permitir cantidades negativas
     * - Validar existencia del producto
     * - Si el producto ya existe, suma las cantidades
     */
    Carrito agregarItem(Long clienteId, Long productoId, int cantidad);
    
    /**
     * Actualiza la cantidad de un item en el carrito.
     * Reglas:
     * - No permitir cantidades negativas
     * - Si cantidad es 0, elimina el item
     */
    Carrito actualizarCantidadItem(Long clienteId, Long productoId, int nuevaCantidad);
    
    /**
     * Elimina un item del carrito.
     */
    Carrito eliminarItem(Long clienteId, Long productoId);
    
    /**
     * Limpia completamente el carrito.
     */
    void limpiarCarrito(Long clienteId);
}
