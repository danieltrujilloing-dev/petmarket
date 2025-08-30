package com.interview.petmarket.domain.ports.out;

import com.interview.petmarket.domain.model.producto.FiltroProducto;
import com.interview.petmarket.domain.model.producto.Producto;

import java.time.Duration;
import java.util.List;

/**
 * Puerto de salida para operaciones de cache de productos.
 * Define las operaciones de cache necesarias para el catálogo.
 */
public interface ProductoCachePort {

    /**
     * Almacena una lista de productos en cache con TTL.
     * 
     * @param clave Clave del cache
     * @param productos Lista de productos a cachear
     * @param ttl Tiempo de vida del cache
     */
    void guardarProductos(String clave, List<Producto> productos, Duration ttl);

    /**
     * Recupera una lista de productos del cache.
     * 
     * @param clave Clave del cache
     * @return Lista de productos cacheados o null si no existe/expiró
     */
    List<Producto> obtenerProductos(String clave);

    /**
     * Almacena un producto individual en cache.
     * 
     * @param id ID del producto
     * @param producto Producto a cachear
     * @param ttl Tiempo de vida del cache
     */
    void guardarProducto(Long id, Producto producto, Duration ttl);

    /**
     * Recupera un producto individual del cache.
     * 
     * @param id ID del producto
     * @return Producto cacheado o null si no existe/expiró
     */
    Producto obtenerProducto(Long id);

    /**
     * Invalida todo el cache de productos.
     * Se ejecuta cuando se crea/actualiza cualquier producto.
     */
    void invalidarCacheProductos();

    /**
     * Invalida cache específico por filtro.
     * 
     * @param filtro Filtro cuyo cache se debe invalidar
     */
    void invalidarCacheFiltro(FiltroProducto filtro);

    /**
     * Invalida cache de un producto específico.
     * 
     * @param id ID del producto
     */
    void invalidarCacheProducto(Long id);

    /**
     * Invalida cache por tipo de producto.
     * 
     * @param tipo Tipo de producto
     */
    void invalidarCacheTipo(String tipo);

    /**
     * Invalida cache por especie.
     * 
     * @param especie Especie animal
     */
    void invalidarCacheEspecie(String especie);

    /**
     * Verifica si existe cache para una clave específica.
     * 
     * @param clave Clave del cache
     * @return true si existe cache válido
     */
    boolean existeCache(String clave);

    /**
     * Obtiene el TTL restante de un cache.
     * 
     * @param clave Clave del cache
     * @return Tiempo restante o Duration.ZERO si no existe
     */
    Duration obtenerTTLRestante(String clave);
}
