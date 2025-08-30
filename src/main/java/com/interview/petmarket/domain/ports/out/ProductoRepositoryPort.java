package com.interview.petmarket.domain.ports.out;

import com.interview.petmarket.domain.model.producto.FiltroProducto;
import com.interview.petmarket.domain.model.producto.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para el repositorio de productos.
 * Define las operaciones de persistencia necesarias.
 */
public interface ProductoRepositoryPort {

    /**
     * Guarda un producto (crear o actualizar).
     * 
     * @param producto Producto a guardar
     * @return Producto guardado con ID asignado
     */
    Producto save(Producto producto);

    /**
     * Busca un producto por su ID.
     * 
     * @param id ID del producto
     * @return Optional con el producto si existe
     */
    Optional<Producto> findById(Long id);

    /**
     * Lista productos aplicando filtros.
     * 
     * @param filtro Filtros a aplicar
     * @return Lista de productos que cumplen los filtros
     */
    List<Producto> findByFiltros(FiltroProducto filtro);

    /**
     * Lista todos los productos activos.
     * 
     * @return Lista de productos activos
     */
    List<Producto> findAllActive();

    /**
     * Lista productos por tipo.
     * 
     * @param tipo Tipo de producto
     * @return Lista de productos del tipo especificado
     */
    List<Producto> findByTipo(String tipo);

    /**
     * Lista productos por especie.
     * 
     * @param especie Especie animal
     * @return Lista de productos para la especie especificada
     */
    List<Producto> findByEspecie(String especie);

    /**
     * Verifica si existe un producto con el ID especificado.
     * 
     * @param id ID del producto
     * @return true si existe, false en caso contrario
     */
    boolean existsById(Long id);

    /**
     * Elimina un producto por su ID.
     * 
     * @param id ID del producto a eliminar
     */
    void deleteById(Long id);

    /**
     * Cuenta el total de productos activos.
     * 
     * @return Número de productos activos
     */
    long countActive();

    /**
     * Lista productos con stock bajo (menos del mínimo especificado).
     * 
     * @param stockMinimo Stock mínimo
     * @return Lista de productos con stock bajo
     */
    List<Producto> findWithLowStock(Integer stockMinimo);
}
