package com.interview.petmarket.domain.ports.out;

import com.interview.petmarket.domain.model.inventario.Inventario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para gestión de inventario.
 * Implementa el principio de Inversión de Dependencias (DIP) de SOLID.
 */
public interface InventarioRepositoryPort {
    
    /**
     * Busca inventario por ID del producto.
     */
    Optional<Inventario> findByProductoId(Long productoId);
    
    /**
     * Guarda inventario.
     */
    Inventario save(Inventario inventario);
    
    /**
     * Busca inventarios con stock bajo el umbral.
     */
    List<Inventario> findByStockBelowThreshold();
    
    /**
     * Verifica si hay stock disponible para un producto.
     */
    boolean hasStock(Long productoId, int cantidadRequerida);
    
    /**
     * Reserva stock para un producto (disminuye stock disponible).
     */
    boolean reservarStock(Long productoId, int cantidad);
    
    /**
     * Libera stock reservado (aumenta stock disponible).
     */
    void liberarStock(Long productoId, int cantidad);
}
