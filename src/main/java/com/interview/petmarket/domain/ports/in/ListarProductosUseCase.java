package com.interview.petmarket.domain.ports.in;

import com.interview.petmarket.domain.model.producto.FiltroProducto;
import com.interview.petmarket.domain.model.producto.Producto;

import java.util.List;

/**
 * Puerto de entrada para el caso de uso de listar productos con filtros.
 * Implementa la lógica para búsqueda de productos con cache.
 */
public interface ListarProductosUseCase {

    /**
     * Lista productos aplicando los filtros especificados.
     * Utiliza cache de lectura con TTL configurable.
     * 
     * @param filtro Filtros a aplicar en la búsqueda
     * @return Lista de productos que cumplen con los filtros
     */
    List<Producto> listarProductos(FiltroProducto filtro);

    /**
     * Lista todos los productos activos (sin filtros específicos).
     * Versión simplificada para casos comunes.
     * 
     * @return Lista de todos los productos activos
     */
    List<Producto> listarProductosActivos();

    /**
     * Lista productos por tipo específico.
     * Caso de uso común optimizado.
     * 
     * @param tipo Tipo de producto a buscar
     * @return Lista de productos del tipo especificado
     */
    List<Producto> listarProductosPorTipo(String tipo);

    /**
     * Lista productos por especie específica.
     * Caso de uso común optimizado.
     * 
     * @param especie Especie animal para la cual buscar productos
     * @return Lista de productos para la especie especificada
     */
    List<Producto> listarProductosPorEspecie(String especie);
}
