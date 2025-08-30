package com.interview.petmarket.domain.ports.in;

import com.interview.petmarket.domain.model.producto.Producto;

/**
 * Puerto de entrada para el caso de uso de gestión de productos.
 * Incluye operaciones que invalidan el cache.
 */
public interface GestionarProductoUseCase {

    /**
     * Crea un nuevo producto en el sistema.
     * Invalida el cache de productos después de la creación.
     * 
     * @param producto Producto a crear
     * @return Producto creado con ID asignado
     */
    Producto crearProducto(Producto producto);

    /**
     * Actualiza un producto existente.
     * Invalida el cache de productos después de la actualización.
     * 
     * @param id ID del producto a actualizar
     * @param producto Datos actualizados del producto
     * @return Producto actualizado
     */
    Producto actualizarProducto(Long id, Producto producto);

    /**
     * Busca un producto por su ID.
     * 
     * @param id ID del producto
     * @return Producto encontrado o null si no existe
     */
    Producto buscarProductoPorId(Long id);

    /**
     * Activa un producto (lo hace visible en el catálogo).
     * Invalida el cache de productos.
     * 
     * @param id ID del producto a activar
     * @return Producto activado
     */
    Producto activarProducto(Long id);

    /**
     * Desactiva un producto (lo oculta del catálogo).
     * Invalida el cache de productos.
     * 
     * @param id ID del producto a desactivar
     * @return Producto desactivado
     */
    Producto desactivarProducto(Long id);

    /**
     * Actualiza el stock de un producto.
     * Invalida el cache de productos si afecta la disponibilidad.
     * 
     * @param id ID del producto
     * @param nuevoStock Nuevo valor de stock
     * @return Producto con stock actualizado
     */
    Producto actualizarStock(Long id, Integer nuevoStock);
}
