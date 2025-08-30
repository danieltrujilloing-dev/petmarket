package com.interview.petmarket.web.controller;

import com.interview.petmarket.domain.ports.in.GestionarCarritoUseCase;
import com.interview.petmarket.domain.model.carrito.Carrito;
import com.interview.petmarket.web.dto.AgregarItemRequestDto;
import com.interview.petmarket.web.dto.CarritoResponseDto;
import com.interview.petmarket.web.mapper.CarritoWebMapper;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para gestión de carritos.
 * Implementa el caso de uso: Agregar/quitar ítems al carrito.
 */
@RestController
@RequestMapping("/api/v1/carritos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CarritoController {
    
    private static final Logger logger = LoggerFactory.getLogger(CarritoController.class);
    
    private final GestionarCarritoUseCase carritoService;
    private final CarritoWebMapper webMapper;
    
    public CarritoController(GestionarCarritoUseCase carritoService, CarritoWebMapper webMapper) {
        this.carritoService = carritoService;
        this.webMapper = webMapper;
    }
    
    /**
     * Obtiene el carrito de un cliente.
     * GET /api/v1/carritos/cliente/{clienteId}
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<CarritoResponseDto> obtenerCarrito(@PathVariable Long clienteId) {
        logger.info("GET /api/v1/carritos/cliente/{} - Obteniendo carrito", clienteId);
        
        Carrito carrito = carritoService.obtenerCarrito(clienteId);
        CarritoResponseDto response = webMapper.toResponseDto(carrito);
        
        logger.info("Carrito obtenido - Cliente: {}, Items: {}", clienteId, carrito.contarItems());
        return ResponseEntity.ok(response);
    }
    
    /**
     * Agrega un item al carrito.
     * POST /api/v1/carritos/cliente/{clienteId}/items
     */
    @PostMapping("/cliente/{clienteId}/items")
    public ResponseEntity<CarritoResponseDto> agregarItem(
            @PathVariable Long clienteId,
            @Valid @RequestBody AgregarItemRequestDto request) {
        
        logger.info("POST /api/v1/carritos/cliente/{}/items - Agregando item: {}", 
                   clienteId, request);
        
        Carrito carrito = carritoService.agregarItem(clienteId, request.getProductoId(), request.getCantidad());
        CarritoResponseDto response = webMapper.toResponseDto(carrito);
        
        logger.info("Item agregado exitosamente - Cliente: {}, Producto: {}, Cantidad: {}", 
                   clienteId, request.getProductoId(), request.getCantidad());
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Actualiza la cantidad de un item en el carrito.
     * PUT /api/v1/carritos/cliente/{clienteId}/items/{productoId}
     */
    @PutMapping("/cliente/{clienteId}/items/{productoId}")
    public ResponseEntity<CarritoResponseDto> actualizarCantidadItem(
            @PathVariable Long clienteId,
            @PathVariable Long productoId,
            @RequestBody Map<String, Integer> request) {
        
        Integer nuevaCantidad = request.get("cantidad");
        if (nuevaCantidad == null || nuevaCantidad < 0) {
            logger.warn("Cantidad inválida: {}", nuevaCantidad);
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("PUT /api/v1/carritos/cliente/{}/items/{} - Nueva cantidad: {}", 
                   clienteId, productoId, nuevaCantidad);
        
        Carrito carrito = carritoService.actualizarCantidadItem(clienteId, productoId, nuevaCantidad);
        CarritoResponseDto response = webMapper.toResponseDto(carrito);
        
        logger.info("Cantidad actualizada exitosamente - Cliente: {}, Producto: {}, Cantidad: {}", 
                   clienteId, productoId, nuevaCantidad);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Elimina un item del carrito.
     * DELETE /api/v1/carritos/cliente/{clienteId}/items/{productoId}
     */
    @DeleteMapping("/cliente/{clienteId}/items/{productoId}")
    public ResponseEntity<CarritoResponseDto> eliminarItem(
            @PathVariable Long clienteId,
            @PathVariable Long productoId) {
        
        logger.info("DELETE /api/v1/carritos/cliente/{}/items/{} - Eliminando item", 
                   clienteId, productoId);
        
        Carrito carrito = carritoService.eliminarItem(clienteId, productoId);
        CarritoResponseDto response = webMapper.toResponseDto(carrito);
        
        logger.info("Item eliminado exitosamente - Cliente: {}, Producto: {}", 
                   clienteId, productoId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Limpia completamente el carrito.
     * DELETE /api/v1/carritos/cliente/{clienteId}
     */
    @DeleteMapping("/cliente/{clienteId}")
    public ResponseEntity<Map<String, String>> limpiarCarrito(@PathVariable Long clienteId) {
        logger.info("DELETE /api/v1/carritos/cliente/{} - Limpiando carrito", clienteId);
        
        carritoService.limpiarCarrito(clienteId);
        
        Map<String, String> response = Map.of(
                "message", "Cart cleared successfully",
                "clienteId", clienteId.toString(),
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        logger.info("Carrito limpiado exitosamente - Cliente: {}", clienteId);
        return ResponseEntity.ok(response);
    }
    
    // Manejo de errores
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Error en CarritoController: {}", e.getMessage(), e);
        
        Map<String, String> response = Map.of(
                "error", "Cart operation failed",
                "message", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
