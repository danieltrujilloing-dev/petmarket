package com.interview.petmarket.web.controller;

import com.interview.petmarket.domain.ports.in.ProcesarPedidoUseCase;
import com.interview.petmarket.domain.model.pedido.Pedido;
import com.interview.petmarket.domain.ports.in.GestionarInventarioUseCase;
import com.interview.petmarket.web.dto.PedidoResponseDto;
import com.interview.petmarket.web.mapper.PedidoWebMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador REST para gestión de pedidos.
 * Implementa el caso de uso: Checkout y gestión de pedidos.
 */
@RestController
@RequestMapping("/api/v1/pedidos")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PedidoController {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidoController.class);
    
    private final ProcesarPedidoUseCase pedidoService;
    private final PedidoWebMapper webMapper;
    private final GestionarInventarioUseCase inventarioService;
    
    public PedidoController(ProcesarPedidoUseCase pedidoService, 
                           PedidoWebMapper webMapper,
                           GestionarInventarioUseCase inventarioService) {
        this.pedidoService = pedidoService;
        this.webMapper = webMapper;
        this.inventarioService = inventarioService;
    }
    
    /**
     * Realiza el checkout del carrito creando un pedido (con query parameter).
     * POST /api/v1/pedidos/checkout?clienteId=123
     */
    @PostMapping("/checkout")
    public ResponseEntity<PedidoResponseDto> realizarCheckoutQuery(@RequestParam Long clienteId) {
        logger.info("POST /api/v1/pedidos/checkout?clienteId={} - Iniciando checkout", clienteId);
        
        try {
            Pedido pedido = pedidoService.realizarCheckout(clienteId);
            PedidoResponseDto response = webMapper.toResponseDto(pedido);
            
            logger.info("Checkout completado exitosamente - Pedido: {}, Cliente: {}, Total: {}", 
                       pedido.getId(), clienteId, pedido.getTotal());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error en checkout - Cliente: {}, Error: {}", clienteId, e.getMessage());
            throw e;
        }
    }

    /**
     * Realiza el checkout del carrito creando un pedido (con path parameter - LEGACY).
     * POST /api/v1/pedidos/checkout/cliente/{clienteId}
     * 
     * Proceso completo:
     * 1. Valida carrito no vacío
     * 2. Valida stock de productos
     * 3. Reserva stock
     * 4. Calcula total con estrategia de pricing
     * 5. Crea pedido
     * 6. Limpia carrito
     * 7. Publica evento OrderCreated
     */
    @PostMapping("/checkout/cliente/{clienteId}")
    public ResponseEntity<PedidoResponseDto> realizarCheckout(@PathVariable Long clienteId) {
        logger.info("POST /api/v1/pedidos/checkout/cliente/{} - Iniciando checkout", clienteId);
        
        try {
            Pedido pedido = pedidoService.realizarCheckout(clienteId);
            PedidoResponseDto response = webMapper.toResponseDto(pedido);
            
            logger.info("Checkout completado exitosamente - Pedido: {}, Cliente: {}, Total: {}", 
                       pedido.getId(), clienteId, pedido.getTotal());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Error en checkout - Cliente: {}, Error: {}", clienteId, e.getMessage());
            throw e; // Re-lanzar para que sea manejado por el @ExceptionHandler
        }
    }
    
    /**
     * Obtiene un pedido por su ID.
     * GET /api/v1/pedidos/{pedidoId}
     */
    @GetMapping("/{pedidoId}")
    public ResponseEntity<PedidoResponseDto> obtenerPedido(@PathVariable Long pedidoId) {
        logger.info("GET /api/v1/pedidos/{} - Obteniendo pedido", pedidoId);
        
        Pedido pedido = pedidoService.obtenerPedido(pedidoId);
        PedidoResponseDto response = webMapper.toResponseDto(pedido);
        
        logger.info("Pedido obtenido - ID: {}, Cliente: {}, Estado: {}", 
                   pedidoId, pedido.getClienteId(), pedido.getEstado());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Cancela un pedido.
     * PATCH /api/v1/pedidos/{pedidoId}/cancelar
     */
    @PatchMapping("/{pedidoId}/cancelar")
    public ResponseEntity<PedidoResponseDto> cancelarPedido(@PathVariable Long pedidoId) {
        logger.info("PATCH /api/v1/pedidos/{}/cancelar - Cancelando pedido", pedidoId);
        
        try {
            Pedido pedido = pedidoService.cancelarPedido(pedidoId);
            PedidoResponseDto response = webMapper.toResponseDto(pedido);
            
            logger.info("Pedido cancelado exitosamente - ID: {}, Estado: {}", 
                       pedidoId, pedido.getEstado());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error cancelando pedido {}: {}", pedidoId, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Cambia el estado de un pedido.
     * PATCH /api/v1/pedidos/{pedidoId}/estado
     */
    @PatchMapping("/{pedidoId}/estado")
    public ResponseEntity<PedidoResponseDto> cambiarEstadoPedido(
            @PathVariable Long pedidoId,
            @RequestBody Map<String, String> request) {
        
        String nuevoEstado = request.get("estado");
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            logger.warn("Estado no proporcionado para pedido: {}", pedidoId);
            return ResponseEntity.badRequest().build();
        }
        
        logger.info("PATCH /api/v1/pedidos/{}/estado - Cambiando a: {}", pedidoId, nuevoEstado);
        
        try {
            Pedido pedido = pedidoService.cambiarEstadoPedido(pedidoId, nuevoEstado);
            PedidoResponseDto response = webMapper.toResponseDto(pedido);
            
            logger.info("Estado cambiado exitosamente - Pedido: {}, Nuevo estado: {}", 
                       pedidoId, pedido.getEstado());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error cambiando estado del pedido {}: {}", pedidoId, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Confirma el pago de un pedido.
     * POST /api/v1/pedidos/{id}/pago
     * 
     * Proceso:
     * 1. Cambia estado del pedido a PAGADO
     * 2. Confirma stock definitivo (dispara lógica de inventario)
     * 3. Emite evento LowStock si aplica
     */
    @PostMapping("/{pedidoId}/pago")
    public ResponseEntity<Map<String, String>> confirmarPago(@PathVariable Long pedidoId) {
        logger.info("POST /api/v1/pedidos/{}/pago - Confirmando pago", pedidoId);
        
        try {
            // 1. Cambiar estado del pedido a PAGADO
            Pedido pedido = pedidoService.cambiarEstadoPedido(pedidoId, "PAGADO");
            
            // 2. Confirmar stock definitivo (dispara lógica de stock y LowStock)
            inventarioService.confirmarStockPorPago(pedidoId);
            
            logger.info("Pago confirmado exitosamente - Pedido: {}, Estado: {}", 
                       pedidoId, pedido.getEstado());
            
            Map<String, String> response = Map.of(
                    "message", "Payment confirmed successfully",
                    "pedidoId", pedidoId.toString(),
                    "estado", pedido.getEstado().toString(),
                    "timestamp", java.time.LocalDateTime.now().toString()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error confirmando pago del pedido {}: {}", pedidoId, e.getMessage());
            
            Map<String, String> errorResponse = Map.of(
                    "error", "Payment confirmation failed",
                    "message", e.getMessage(),
                    "pedidoId", pedidoId.toString(),
                    "timestamp", java.time.LocalDateTime.now().toString()
            );
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    // Manejo de errores
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Error en PedidoController: {}", e.getMessage(), e);
        
        Map<String, String> response = Map.of(
                "error", "Order operation failed",
                "message", e.getMessage(),
                "timestamp", java.time.LocalDateTime.now().toString()
        );
        
        // Determinar código de estado según el tipo de excepción
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (e.getMessage().contains("not found")) {
            status = HttpStatus.NOT_FOUND;
        } else if (e.getMessage().contains("stock") || e.getMessage().contains("empty")) {
            status = HttpStatus.CONFLICT;
        }
        
        return ResponseEntity.status(status).body(response);
    }
}
