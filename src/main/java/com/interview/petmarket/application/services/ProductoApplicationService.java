package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.exceptions.InvalidProductDataException;
import com.interview.petmarket.domain.model.producto.EspecieAnimal;
import com.interview.petmarket.domain.model.producto.FiltroProducto;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.model.producto.TipoProducto;
import com.interview.petmarket.domain.ports.in.GestionarProductoUseCase;
import com.interview.petmarket.domain.ports.in.ListarProductosUseCase;
import com.interview.petmarket.domain.ports.out.ProductoCachePort;
import com.interview.petmarket.domain.ports.out.ProductoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Servicio de aplicación que implementa los casos de uso de productos.
 * Coordina entre el dominio, repositorio y cache.
 */
@Service
@Transactional
public class ProductoApplicationService implements ListarProductosUseCase, GestionarProductoUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ProductoApplicationService.class);

    private final ProductoRepositoryPort productoRepository;
    private final ProductoCachePort productoCache;
    private final Duration defaultCacheTTL;

    public ProductoApplicationService(
            ProductoRepositoryPort productoRepository,
            ProductoCachePort productoCache,
            @Value("${petmarket.cache.productos.ttl:PT15M}") Duration defaultCacheTTL) {
        this.productoRepository = productoRepository;
        this.productoCache = productoCache;
        this.defaultCacheTTL = defaultCacheTTL;
        logger.info("ProductoApplicationService initialized with cache TTL: {}", defaultCacheTTL);
    }

    // ==================== IMPLEMENTACIÓN DE ListarProductosUseCase ====================

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarProductos(FiltroProducto filtro) {
        logger.debug("Listing products with filter: {}", filtro);
        
        if (filtro == null) {
            return listarProductosActivos();
        }

        // Generar clave de cache
        String claveCache = filtro.generarClaveCache();
        
        // Intentar obtener del cache
        List<Producto> productosCache = productoCache.obtenerProductos(claveCache);
        if (productosCache != null) {
            logger.debug("Products found in cache for key: {}", claveCache);
            return productosCache;
        }

        // Buscar en repositorio
        List<Producto> productos = productoRepository.findByFiltros(filtro);
        logger.debug("Found {} products in repository", productos.size());

        // Guardar en cache
        productoCache.guardarProductos(claveCache, productos, defaultCacheTTL);
        logger.debug("Products cached with key: {} and TTL: {}", claveCache, defaultCacheTTL);

        return productos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarProductosActivos() {
        logger.debug("Listing all active products");
        
        String claveCache = "productos:activos";
        
        // Intentar obtener del cache
        List<Producto> productosCache = productoCache.obtenerProductos(claveCache);
        if (productosCache != null) {
            logger.debug("Active products found in cache");
            return productosCache;
        }

        // Buscar en repositorio
        List<Producto> productos = productoRepository.findAllActive();
        logger.debug("Found {} active products in repository", productos.size());

        // Guardar en cache
        productoCache.guardarProductos(claveCache, productos, defaultCacheTTL);
        
        return productos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarProductosPorTipo(String tipo) {
        logger.debug("Listing products by type: {}", tipo);
        
        if (tipo == null || tipo.trim().isEmpty()) {
            return List.of();
        }

        // Validar que el tipo existe
        TipoProducto tipoProducto = TipoProducto.fromDescripcion(tipo);
        if (tipoProducto == null) {
            logger.warn("Invalid product type: {}", tipo);
            return List.of();
        }

        String claveCache = "productos:tipo:" + tipoProducto.name();
        
        // Intentar obtener del cache
        List<Producto> productosCache = productoCache.obtenerProductos(claveCache);
        if (productosCache != null) {
            logger.debug("Products found in cache for type: {}", tipo);
            return productosCache;
        }

        // Buscar en repositorio
        List<Producto> productos = productoRepository.findByTipo(tipoProducto.name());
        logger.debug("Found {} products for type {} in repository", productos.size(), tipo);

        // Guardar en cache
        productoCache.guardarProductos(claveCache, productos, defaultCacheTTL);
        
        return productos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarProductosPorEspecie(String especie) {
        logger.debug("Listing products by species: {}", especie);
        
        if (especie == null || especie.trim().isEmpty()) {
            return List.of();
        }

        // Validar que la especie existe
        EspecieAnimal especieAnimal = EspecieAnimal.fromDescripcion(especie);
        if (especieAnimal == null) {
            logger.warn("Invalid animal species: {}", especie);
            return List.of();
        }

        String claveCache = "productos:especie:" + especieAnimal.name();
        
        // Intentar obtener del cache
        List<Producto> productosCache = productoCache.obtenerProductos(claveCache);
        if (productosCache != null) {
            logger.debug("Products found in cache for species: {}", especie);
            return productosCache;
        }

        // Buscar en repositorio
        List<Producto> productos = productoRepository.findByEspecie(especieAnimal.name());
        logger.debug("Found {} products for species {} in repository", productos.size(), especie);

        // Guardar en cache
        productoCache.guardarProductos(claveCache, productos, defaultCacheTTL);
        
        return productos;
    }

    // ==================== IMPLEMENTACIÓN DE GestionarProductoUseCase ====================

    @Override
    public Producto crearProducto(Producto producto) {
        logger.info("Creating new product: {}", producto.getNombre());
        
        if (producto == null) {
            throw new InvalidProductDataException("Product cannot be null");
        }

        // Guardar en repositorio
        Producto productoCreado = productoRepository.save(producto);
        logger.info("Product created with ID: {}", productoCreado.getId());

        // Invalidar cache
        invalidarCacheProductos();
        
        return productoCreado;
    }

    @Override
    public Producto actualizarProducto(Long id, Producto producto) {
        logger.info("Updating product with ID: {}", id);
        
        if (id == null) {
            throw new InvalidProductDataException("Product ID cannot be null");
        }
        if (producto == null) {
            throw new InvalidProductDataException("Product cannot be null");
        }

        // Verificar que el producto existe
        Producto productoExistente = buscarProductoPorId(id);
        if (productoExistente == null) {
            throw new InvalidProductDataException("Product not found with ID: " + id);
        }

        // Actualizar campos del producto existente
        productoExistente.actualizar(
            producto.getNombre(),
            producto.getDescripcion(),
            producto.getPrecio()
        );

        // Guardar cambios
        Producto productoActualizado = productoRepository.save(productoExistente);
        logger.info("Product updated: {}", productoActualizado.getId());

        // Invalidar cache
        invalidarCacheProductos();
        productoCache.invalidarCacheProducto(id);
        
        return productoActualizado;
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarProductoPorId(Long id) {
        logger.debug("Searching product by ID: {}", id);
        
        if (id == null) {
            return null;
        }

        // Intentar obtener del cache
        Producto productoCache = this.productoCache.obtenerProducto(id);
        if (productoCache != null) {
            logger.debug("Product found in cache: {}", id);
            return productoCache;
        }

        // Buscar en repositorio
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            // Guardar en cache individual
            this.productoCache.guardarProducto(id, producto, defaultCacheTTL);
            logger.debug("Product cached: {}", id);
        }
        
        return producto;
    }

    @Override
    public Producto activarProducto(Long id) {
        logger.info("Activating product: {}", id);
        
        Producto producto = buscarProductoPorId(id);
        if (producto == null) {
            throw new InvalidProductDataException("Product not found with ID: " + id);
        }

        producto.activar();
        Producto productoActualizado = productoRepository.save(producto);
        
        // Invalidar cache
        invalidarCacheProductos();
        productoCache.invalidarCacheProducto(id);
        
        logger.info("Product activated: {}", id);
        return productoActualizado;
    }

    @Override
    public Producto desactivarProducto(Long id) {
        logger.info("Deactivating product: {}", id);
        
        Producto producto = buscarProductoPorId(id);
        if (producto == null) {
            throw new InvalidProductDataException("Product not found with ID: " + id);
        }

        producto.desactivar();
        Producto productoActualizado = productoRepository.save(producto);
        
        // Invalidar cache
        invalidarCacheProductos();
        productoCache.invalidarCacheProducto(id);
        
        logger.info("Product deactivated: {}", id);
        return productoActualizado;
    }

    @Override
    public Producto actualizarStock(Long id, Integer nuevoStock) {
        logger.info("Updating stock for product {}: {}", id, nuevoStock);
        
        Producto producto = buscarProductoPorId(id);
        if (producto == null) {
            throw new InvalidProductDataException("Product not found with ID: " + id);
        }

        boolean estabaDisponible = producto.estaDisponible();
        
        // Actualizar stock usando la lógica del dominio
        if (nuevoStock != null && nuevoStock >= 0) {
            if (producto.getStock() == null || nuevoStock > producto.getStock()) {
                int diferencia = nuevoStock - (producto.getStock() != null ? producto.getStock() : 0);
                producto.aumentarStock(diferencia);
            } else if (nuevoStock < producto.getStock()) {
                int diferencia = producto.getStock() - nuevoStock;
                producto.reducirStock(diferencia);
            }
        }

        Producto productoActualizado = productoRepository.save(producto);
        
        // Invalidar cache solo si cambió la disponibilidad
        boolean estaDisponible = productoActualizado.estaDisponible();
        if (estabaDisponible != estaDisponible) {
            invalidarCacheProductos();
        }
        productoCache.invalidarCacheProducto(id);
        
        logger.info("Stock updated for product {}: {} -> {}", id, 
                   producto.getStock(), productoActualizado.getStock());
        return productoActualizado;
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private void invalidarCacheProductos() {
        logger.debug("Invalidating all product caches");
        productoCache.invalidarCacheProductos();
    }

    // ==================== MÉTODO ADICIONAL PARA FILTROS COMPLEJOS ====================

    /**
     * Método helper para crear filtros complejos.
     */
    public List<Producto> buscarProductosConFiltroComplejo(
            Set<String> tipos, 
            Set<String> especies, 
            String precioMin, 
            String precioMax,
            String textoBusqueda) {
        
        FiltroProducto.FiltroProductoBuilder builder = FiltroProducto.builder();
        
        // Convertir tipos
        if (tipos != null && !tipos.isEmpty()) {
            Set<TipoProducto> tiposEnum = tipos.stream()
                    .map(TipoProducto::fromDescripcion)
                    .filter(tipo -> tipo != null)
                    .collect(java.util.stream.Collectors.toSet());
            if (!tiposEnum.isEmpty()) {
                builder.tipos(tiposEnum);
            }
        }
        
        // Convertir especies
        if (especies != null && !especies.isEmpty()) {
            Set<EspecieAnimal> especiesEnum = especies.stream()
                    .map(EspecieAnimal::fromDescripcion)
                    .filter(especie -> especie != null)
                    .collect(java.util.stream.Collectors.toSet());
            if (!especiesEnum.isEmpty()) {
                builder.especies(especiesEnum);
            }
        }
        
        // Precios
        if (precioMin != null && !precioMin.trim().isEmpty()) {
            try {
                builder.precioMinimo(new java.math.BigDecimal(precioMin));
            } catch (NumberFormatException e) {
                logger.warn("Invalid minimum price format: {}", precioMin);
            }
        }
        
        if (precioMax != null && !precioMax.trim().isEmpty()) {
            try {
                builder.precioMaximo(new java.math.BigDecimal(precioMax));
            } catch (NumberFormatException e) {
                logger.warn("Invalid maximum price format: {}", precioMax);
            }
        }
        
        // Texto de búsqueda
        if (textoBusqueda != null && !textoBusqueda.trim().isEmpty()) {
            builder.textoBusqueda(textoBusqueda.trim());
        }
        
        // Solo productos activos y disponibles por defecto
        builder.soloActivos(true).soloDisponibles(true);
        
        FiltroProducto filtro = builder.build();
        return listarProductos(filtro);
    }
}
