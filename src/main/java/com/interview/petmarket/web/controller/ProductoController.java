package com.interview.petmarket.web.controller;

import com.interview.petmarket.application.services.ProductoApplicationService;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.infrastructure.cache.ProductoCacheAdapter;
import com.interview.petmarket.web.dto.CreateProductoRequestDto;
import com.interview.petmarket.web.dto.ProductoResponseDto;
import com.interview.petmarket.web.dto.UpdateProductoRequestDto;
import com.interview.petmarket.web.mapper.ProductoWebMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controlador REST para el catálogo de productos.
 * 
 * Expone endpoints para:
 * - Listar productos con filtros (usa cache de lectura)
 * - Gestionar productos (invalida cache automáticamente)
 */
@RestController
@RequestMapping("/api/v1/productos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    private final ProductoApplicationService productoService;
    private final ProductoWebMapper webMapper;
    private final ProductoCacheAdapter cacheAdapter;

    public ProductoController(ProductoApplicationService productoService,
                             ProductoWebMapper webMapper,
                             ProductoCacheAdapter cacheAdapter) {
        this.productoService = productoService;
        this.webMapper = webMapper;
        this.cacheAdapter = cacheAdapter;
    }

    // ==================== ENDPOINTS DE CATÁLOGO (CON CACHE) ====================

    /**
     * Lista todos los productos activos.
     * GET /api/v1/productos
     */
    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> listarProductos() {
        logger.info("GET /api/v1/productos - Listing all active products");
        
        List<Producto> productos = productoService.listarProductosActivos();
        List<ProductoResponseDto> response = webMapper.toResponseDtoList(productos);
        
        logger.info("Returned {} active products", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Lista productos con filtros complejos.
     * GET /api/v1/productos/buscar?tipos=Alimento,Accesorio&especies=Perro&precioMin=10.00&precioMax=100.00&q=collar
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponseDto>> buscarProductos(
            @RequestParam(required = false) Set<String> tipos,
            @RequestParam(required = false) Set<String> especies,
            @RequestParam(required = false) String precioMin,
            @RequestParam(required = false) String precioMax,
            @RequestParam(required = false) String q) {
        
        logger.info("GET /api/v1/productos/buscar - tipos={}, especies={}, precioMin={}, precioMax={}, q={}", 
                   tipos, especies, precioMin, precioMax, q);
        
        List<Producto> productos = productoService.buscarProductosConFiltroComplejo(
                tipos, especies, precioMin, precioMax, q);
        List<ProductoResponseDto> response = webMapper.toResponseDtoList(productos);
        
        logger.info("Search returned {} products", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Lista productos por tipo específico.
     * GET /api/v1/productos/tipo/Alimento
     */
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<ProductoResponseDto>> listarProductosPorTipo(@PathVariable String tipo) {
        logger.info("GET /api/v1/productos/tipo/{} - Listing products by type", tipo);
        
        if (!webMapper.esTipoValido(tipo)) {
            logger.warn("Invalid product type: {}", tipo);
            return ResponseEntity.badRequest().build();
        }
        
        List<Producto> productos = productoService.listarProductosPorTipo(tipo);
        List<ProductoResponseDto> response = webMapper.toResponseDtoList(productos);
        
        logger.info("Found {} products for type: {}", response.size(), tipo);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista productos por especie específica.
     * GET /api/v1/productos/especie/Perro
     */
    @GetMapping("/especie/{especie}")
    public ResponseEntity<List<ProductoResponseDto>> listarProductosPorEspecie(@PathVariable String especie) {
        logger.info("GET /api/v1/productos/especie/{} - Listing products by species", especie);
        
        if (!webMapper.esEspecieValida(especie)) {
            logger.warn("Invalid animal species: {}", especie);
            return ResponseEntity.badRequest().build();
        }
        
        List<Producto> productos = productoService.listarProductosPorEspecie(especie);
        List<ProductoResponseDto> response = webMapper.toResponseDtoList(productos);
        
        logger.info("Found {} products for species: {}", response.size(), especie);
        return ResponseEntity.ok(response);
    }

    // ==================== ENDPOINTS DE GESTIÓN (INVALIDAN CACHE) ====================

    /**
     * Obtiene un producto por ID.
     * GET /api/v1/productos/123
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> obtenerProducto(@PathVariable Long id) {
        logger.info("GET /api/v1/productos/{} - Getting product by ID", id);
        
        Producto producto = productoService.buscarProductoPorId(id);
        if (producto == null) {
            logger.warn("Product not found: {}", id);
            return ResponseEntity.notFound().build();
        }
        
        ProductoResponseDto response = webMapper.toResponseDto(producto);
        logger.info("Product found: {}", producto.getNombre());
        return ResponseEntity.ok(response);
    }

    /**
     * Crea un nuevo producto.
     * POST /api/v1/productos
     */
    @PostMapping
    public ResponseEntity<ProductoResponseDto> crearProducto(@Valid @RequestBody CreateProductoRequestDto request) {
        logger.info("POST /api/v1/productos - Creating new product: {}", request.getNombre());
        
        // Validar tipo y especie
        if (!webMapper.esTipoValido(request.getTipo())) {
            logger.warn("Invalid product type in request: {}", request.getTipo());
            return ResponseEntity.badRequest().build();
        }
        if (!webMapper.esEspecieValida(request.getEspecie())) {
            logger.warn("Invalid animal species in request: {}", request.getEspecie());
            return ResponseEntity.badRequest().build();
        }
        
        Producto producto = webMapper.fromCreateDto(request);
        Producto productoCreado = productoService.crearProducto(producto);
        ProductoResponseDto response = webMapper.toResponseDto(productoCreado);
        
        logger.info("Product created successfully with ID: {}", productoCreado.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Actualiza un producto existente.
     * PUT /api/v1/productos/123
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductoRequestDto request) {
        
        logger.info("PUT /api/v1/productos/{} - Updating product", id);
        
        if (!request.tieneAlgunCampo()) {
            logger.warn("No fields provided for update");
            return ResponseEntity.badRequest().build();
        }
        
        Producto productoExistente = productoService.buscarProductoPorId(id);
        if (productoExistente == null) {
            logger.warn("Product not found for update: {}", id);
            return ResponseEntity.notFound().build();
        }
        
        Producto productoActualizado = webMapper.applyUpdateDto(productoExistente, request);
        Producto resultado = productoService.actualizarProducto(id, productoActualizado);
        ProductoResponseDto response = webMapper.toResponseDto(resultado);
        
        logger.info("Product updated successfully: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Activa un producto.
     * PATCH /api/v1/productos/123/activar
     */
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ProductoResponseDto> activarProducto(@PathVariable Long id) {
        logger.info("PATCH /api/v1/productos/{}/activar - Activating product", id);
        
        try {
            Producto producto = productoService.activarProducto(id);
            ProductoResponseDto response = webMapper.toResponseDto(producto);
            
            logger.info("Product activated successfully: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error activating product {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Desactiva un producto.
     * PATCH /api/v1/productos/123/desactivar
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ProductoResponseDto> desactivarProducto(@PathVariable Long id) {
        logger.info("PATCH /api/v1/productos/{}/desactivar - Deactivating product", id);
        
        try {
            Producto producto = productoService.desactivarProducto(id);
            ProductoResponseDto response = webMapper.toResponseDto(producto);
            
            logger.info("Product deactivated successfully: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error deactivating product {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Actualiza el stock de un producto.
     * PATCH /api/v1/productos/123/stock
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductoResponseDto> actualizarStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        
        logger.info("PATCH /api/v1/productos/{}/stock - Updating stock", id);
        
        Integer nuevoStock = request.get("stock");
        if (nuevoStock == null || nuevoStock < 0) {
            logger.warn("Invalid stock value: {}", nuevoStock);
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Producto producto = productoService.actualizarStock(id, nuevoStock);
            ProductoResponseDto response = webMapper.toResponseDto(producto);
            
            logger.info("Stock updated successfully for product {}: {}", id, nuevoStock);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating stock for product {}: {}", id, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== ENDPOINTS DE UTILIDAD ====================

    /**
     * Obtiene los tipos de producto disponibles.
     * GET /api/v1/productos/tipos
     */
    @GetMapping("/tipos")
    public ResponseEntity<List<String>> obtenerTipos() {
        logger.debug("GET /api/v1/productos/tipos - Getting available product types");
        
        List<String> tipos = webMapper.obtenerTiposDisponibles();
        return ResponseEntity.ok(tipos);
    }

    /**
     * Obtiene las especies disponibles.
     * GET /api/v1/productos/especies
     */
    @GetMapping("/especies")
    public ResponseEntity<List<String>> obtenerEspecies() {
        logger.debug("GET /api/v1/productos/especies - Getting available species");
        
        List<String> especies = webMapper.obtenerEspeciesDisponibles();
        return ResponseEntity.ok(especies);
    }

    /**
     * Obtiene estadísticas del cache de productos.
     * GET /api/v1/productos/cache/stats
     */
    @GetMapping("/cache/stats")
    public ResponseEntity<ProductoCacheAdapter.CacheStats> obtenerEstadisticasCache() {
        logger.debug("GET /api/v1/productos/cache/stats - Getting cache statistics");
        
        ProductoCacheAdapter.CacheStats stats = cacheAdapter.obtenerEstadisticasCache();
        return ResponseEntity.ok(stats);
    }

    /**
     * Invalida manualmente todo el cache de productos.
     * DELETE /api/v1/productos/cache
     */
    @DeleteMapping("/cache")
    public ResponseEntity<Map<String, String>> invalidarCache() {
        logger.info("DELETE /api/v1/productos/cache - Manually invalidating all product cache");
        
        cacheAdapter.invalidarCacheProductos();
        
        Map<String, String> response = Map.of(
                "message", "Product cache invalidated successfully",
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        logger.info("All product cache invalidated manually");
        return ResponseEntity.ok(response);
    }

    // ==================== MANEJO DE ERRORES ====================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Unexpected error in ProductoController: {}", e.getMessage(), e);
        
        Map<String, String> response = Map.of(
                "error", "Internal server error",
                "message", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
