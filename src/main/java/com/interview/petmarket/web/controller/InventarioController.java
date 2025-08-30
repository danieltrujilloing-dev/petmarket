package com.interview.petmarket.web.controller;

import com.interview.petmarket.domain.model.inventario.Inventario;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.ports.in.GestionarInventarioUseCase;
import com.interview.petmarket.domain.ports.out.ProductoRepositoryPort;
import com.interview.petmarket.web.dto.ActualizarStockRequestDto;
import com.interview.petmarket.web.dto.InventarioResponseDto;
import com.interview.petmarket.web.mapper.InventarioWebMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de inventario.
 * Expone endpoints para consultar y actualizar inventarios.
 */
@RestController
@RequestMapping("/api/v1/inventarios")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InventarioController {
    
    private static final Logger logger = LoggerFactory.getLogger(InventarioController.class);
    
    private final GestionarInventarioUseCase inventarioService;
    private final ProductoRepositoryPort productoRepository;
    private final InventarioWebMapper webMapper;
    
    public InventarioController(GestionarInventarioUseCase inventarioService,
                               ProductoRepositoryPort productoRepository,
                               InventarioWebMapper webMapper) {
        this.inventarioService = inventarioService;
        this.productoRepository = productoRepository;
        this.webMapper = webMapper;
    }
    
    /**
     * Obtiene el inventario de un producto específico.
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioResponseDto> obtenerInventario(@PathVariable Long productoId) {
        logger.info("GET /api/v1/inventarios/producto/{} - Obteniendo inventario", productoId);
        
        try {
            Inventario inventario = inventarioService.obtenerInventario(productoId);
            Producto producto = productoRepository.findById(productoId).orElse(null);
            
            InventarioResponseDto response = webMapper.toResponseDto(inventario, producto);
            
            logger.info("Inventario obtenido exitosamente - Producto: {}, Stock: {}", 
                       productoId, inventario.getStockDisponible());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo inventario para producto: {}", productoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Obtiene todos los inventarios con stock bajo.
     */
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<InventarioResponseDto>> obtenerInventariosConStockBajo() {
        logger.info("GET /api/v1/inventarios/stock-bajo - Obteniendo inventarios con stock bajo");
        
        try {
            List<Inventario> inventarios = inventarioService.obtenerInventariosConStockBajo();
            
            List<InventarioResponseDto> response = inventarios.stream()
                    .map(inventario -> {
                        Producto producto = productoRepository.findById(inventario.getProductoId()).orElse(null);
                        return webMapper.toResponseDto(inventario, producto);
                    })
                    .collect(Collectors.toList());
            
            logger.info("Obtenidos {} inventarios con stock bajo", response.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error obteniendo inventarios con stock bajo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Actualiza el stock de un producto.
     */
    @PutMapping("/producto/{productoId}/stock")
    public ResponseEntity<InventarioResponseDto> actualizarStock(@PathVariable Long productoId,
                                                                @Valid @RequestBody ActualizarStockRequestDto request) {
        logger.info("PUT /api/v1/inventarios/producto/{}/stock - Actualizando stock a {}", 
                   productoId, request.getNuevaCantidad());
        
        try {
            String motivo = request.getMotivo() != null ? request.getMotivo() : "Actualización manual";
            
            Inventario inventarioActualizado = inventarioService.actualizarStock(
                    productoId, 
                    request.getNuevaCantidad(), 
                    motivo
            );
            
            Producto producto = productoRepository.findById(productoId).orElse(null);
            InventarioResponseDto response = webMapper.toResponseDto(inventarioActualizado, producto);
            
            logger.info("Stock actualizado exitosamente - Producto: {}, Nuevo stock: {}", 
                       productoId, request.getNuevaCantidad());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error actualizando stock para producto: {}", productoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Ajusta el umbral de reposición de un producto.
     */
    @PutMapping("/producto/{productoId}/umbral")
    public ResponseEntity<InventarioResponseDto> ajustarUmbralReposicion(@PathVariable Long productoId,
                                                                        @RequestParam Integer nuevoUmbral) {
        logger.info("PUT /api/v1/inventarios/producto/{}/umbral - Ajustando umbral a {}", 
                   productoId, nuevoUmbral);
        
        try {
            Inventario inventarioActualizado = inventarioService.ajustarUmbralReposicion(productoId, nuevoUmbral);
            Producto producto = productoRepository.findById(productoId).orElse(null);
            
            InventarioResponseDto response = webMapper.toResponseDto(inventarioActualizado, producto);
            
            logger.info("Umbral de reposición ajustado exitosamente - Producto: {}, Nuevo umbral: {}", 
                       productoId, nuevoUmbral);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error ajustando umbral de reposición para producto: {}", productoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Confirma el stock por pago de un pedido.
     */
    @PostMapping("/confirmar-stock/pedido/{pedidoId}")
    public ResponseEntity<List<InventarioResponseDto>> confirmarStockPorPago(@PathVariable Long pedidoId) {
        logger.info("POST /api/v1/inventarios/confirmar-stock/pedido/{} - Confirmando stock por pago", pedidoId);
        
        try {
            List<Inventario> inventariosActualizados = inventarioService.confirmarStockPorPago(pedidoId);
            
            List<InventarioResponseDto> response = inventariosActualizados.stream()
                    .map(inventario -> {
                        Producto producto = productoRepository.findById(inventario.getProductoId()).orElse(null);
                        return webMapper.toResponseDto(inventario, producto);
                    })
                    .collect(Collectors.toList());
            
            logger.info("Stock confirmado exitosamente para {} productos del pedido: {}", 
                       response.size(), pedidoId);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error confirmando stock para pedido: {}", pedidoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
