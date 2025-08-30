package com.interview.petmarket.domain.ports.out;

import com.interview.petmarket.domain.model.pedido.EstadoPedido;
import com.interview.petmarket.domain.model.pedido.Pedido;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para persistencia de pedidos.
 * Implementa el principio de Inversión de Dependencias (DIP) de SOLID.
 */
public interface PedidoRepositoryPort {
    
    /**
     * Busca un pedido por ID.
     */
    Optional<Pedido> findById(Long id);
    
    /**
     * Guarda un pedido.
     */
    Pedido save(Pedido pedido);
    
    /**
     * Busca pedidos por cliente.
     */
    List<Pedido> findByClienteId(Long clienteId);
    
    /**
     * Busca pedidos por estado.
     */
    List<Pedido> findByEstado(EstadoPedido estado);
    
    /**
     * Busca pedidos por cliente y estado.
     */
    List<Pedido> findByClienteIdAndEstado(Long clienteId, EstadoPedido estado);
}
