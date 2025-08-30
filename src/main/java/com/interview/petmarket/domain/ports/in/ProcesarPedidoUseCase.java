package com.interview.petmarket.domain.ports.in;

import com.interview.petmarket.domain.model.pedido.Pedido;

/**
 * Puerto de entrada para procesar pedidos.
 * Implementa el principio de Segregación de Interfaces (ISP) de SOLID.
 */
public interface ProcesarPedidoUseCase {
    
    /**
     * Realiza el checkout del carrito creando un pedido.
     * 
     * Proceso:
     * 1. Valida que el carrito no esté vacío
     * 2. Valida existencia y stock de productos
     * 3. Reserva stock (disminuye stock disponible)
     * 4. Calcula total aplicando estrategia de pricing
     * 5. Crea el pedido
     * 6. Limpia el carrito
     * 7. Publica evento OrderCreated
     * 
     * @param clienteId ID del cliente
     * @return Pedido creado
     */
    Pedido realizarCheckout(Long clienteId);
    
    /**
     * Obtiene un pedido por su ID.
     */
    Pedido obtenerPedido(Long pedidoId);
    
    /**
     * Cancela un pedido.
     * Solo se puede cancelar si está en estado CREADO o PAGADO.
     * Restaura el stock de los productos.
     */
    Pedido cancelarPedido(Long pedidoId);
    
    /**
     * Cambia el estado de un pedido.
     */
    Pedido cambiarEstadoPedido(Long pedidoId, String nuevoEstado);
}
