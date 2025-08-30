package com.interview.petmarket.domain.ports.in;

import com.interview.petmarket.domain.model.inventario.Inventario;

import java.util.List;

/**
 * Puerto de entrada para gestión de inventario.
 * Define los casos de uso relacionados con inventario y stock.
 * 
 * Implementa el principio de Segregación de Interfaces (ISP) de SOLID.
 */
public interface GestionarInventarioUseCase {
    
    /**
     * Confirma la disminución definitiva de stock al pagar un pedido.
     * Esto hace permanente la reserva temporal de stock.
     * 
     * @param pedidoId ID del pedido pagado
     * @return Lista de inventarios actualizados
     */
    List<Inventario> confirmarStockPorPago(Long pedidoId);
    
    /**
     * Obtiene el inventario de un producto específico.
     * 
     * @param productoId ID del producto
     * @return Inventario del producto
     */
    Inventario obtenerInventario(Long productoId);
    
    /**
     * Obtiene todos los inventarios con stock bajo el umbral.
     * 
     * @return Lista de inventarios con stock bajo
     */
    List<Inventario> obtenerInventariosConStockBajo();
    
    /**
     * Actualiza el stock de un producto (para reposiciones manuales).
     * 
     * @param productoId ID del producto
     * @param nuevaCantidad Nueva cantidad de stock
     * @param motivo Motivo del ajuste
     * @return Inventario actualizado
     */
    Inventario actualizarStock(Long productoId, int nuevaCantidad, String motivo);
    
    /**
     * Ajusta el umbral de reposición de un producto.
     * 
     * @param productoId ID del producto
     * @param nuevoUmbral Nuevo umbral de reposición
     * @return Inventario actualizado
     */
    Inventario ajustarUmbralReposicion(Long productoId, int nuevoUmbral);
}
