package com.interview.petmarket.domain.ports.out;

import com.interview.petmarket.domain.model.carrito.Carrito;

import java.util.Optional;

/**
 * Puerto de salida para persistencia de carritos.
 * Implementa el principio de Inversión de Dependencias (DIP) de SOLID.
 */
public interface CarritoRepositoryPort {
    
    /**
     * Busca un carrito por ID del cliente.
     */
    Optional<Carrito> findByClienteId(Long clienteId);
    
    /**
     * Guarda un carrito.
     */
    Carrito save(Carrito carrito);
    
    /**
     * Elimina un carrito por ID del cliente.
     */
    void deleteByClienteId(Long clienteId);
    
    /**
     * Verifica si existe un carrito para el cliente.
     */
    boolean existsByClienteId(Long clienteId);
}
