package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.events.OrderCreatedEvent;
import com.interview.petmarket.domain.exceptions.InvalidCartDataException;
import com.interview.petmarket.domain.exceptions.InvalidOrderDataException;
import com.interview.petmarket.domain.exceptions.InvalidProductDataException;
import com.interview.petmarket.domain.model.carrito.Carrito;
import com.interview.petmarket.domain.model.carrito.CarritoItem;
import com.interview.petmarket.domain.model.pedido.EstadoPedido;
import com.interview.petmarket.domain.model.pedido.Pedido;
import com.interview.petmarket.domain.model.pedido.PedidoItem;
import com.interview.petmarket.domain.model.pedido.PricingStrategy;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.ports.in.ProcesarPedidoUseCase;
import com.interview.petmarket.domain.ports.out.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de aplicación para procesar pedidos.
 * Implementa los principios SOLID y maneja el checkout completo.
 */
@Transactional
public class PedidoApplicationService implements ProcesarPedidoUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(PedidoApplicationService.class);
    
    private final PedidoRepositoryPort pedidoRepository;
    private final CarritoRepositoryPort carritoRepository;
    private final ProductoRepositoryPort productoRepository;
    private final InventarioRepositoryPort inventarioRepository;
    private final PricingStrategyPort pricingStrategyPort;
    private final EventPublisherPort eventPublisher;
    
    public PedidoApplicationService(PedidoRepositoryPort pedidoRepository,
                                  CarritoRepositoryPort carritoRepository,
                                  ProductoRepositoryPort productoRepository,
                                  InventarioRepositoryPort inventarioRepository,
                                  PricingStrategyPort pricingStrategyPort,
                                  EventPublisherPort eventPublisher) {
        this.pedidoRepository = pedidoRepository;
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
        this.pricingStrategyPort = pricingStrategyPort;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Pedido realizarCheckout(Long clienteId) {
        logger.info("Iniciando checkout para cliente: {}", clienteId);
        
        // 1. Validar que el carrito no esté vacío
        Carrito carrito = carritoRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new InvalidCartDataException("Cart not found for client: " + clienteId));
        
        if (carrito.estaVacio()) {
            throw new InvalidCartDataException("Cannot checkout empty cart");
        }
        
        // 2. Obtener estrategia de pricing
        PricingStrategy strategy = pricingStrategyPort.getStrategyForClient(clienteId);
        logger.debug("Usando estrategia de pricing: {}", strategy.getNombre());
        
        // 3. Validar existencia, stock y calcular items del pedido
        List<PedidoItem> pedidoItems = new ArrayList<>();
        BigDecimal totalPedido = BigDecimal.ZERO;
        
        for (CarritoItem carritoItem : carrito.getItems()) {
            // Validar producto
            Producto producto = productoRepository.findById(carritoItem.getProductoId())
                    .orElseThrow(() -> new InvalidProductDataException(
                            "Product not found: " + carritoItem.getProductoId()));
            
            if (!producto.isActivo()) {
                throw new InvalidProductDataException(
                        "Product is not active: " + carritoItem.getProductoId());
            }
            
            // Validar y reservar stock
            if (!inventarioRepository.hasStock(carritoItem.getProductoId(), carritoItem.getCantidad())) {
                throw new InvalidOrderDataException(
                        "Insufficient stock for product: " + carritoItem.getProductoId());
            }
            
            boolean stockReservado = inventarioRepository.reservarStock(
                    carritoItem.getProductoId(), carritoItem.getCantidad());
            
            if (!stockReservado) {
                throw new InvalidOrderDataException(
                        "Could not reserve stock for product: " + carritoItem.getProductoId());
            }
            
            // Calcular precio con estrategia
            BigDecimal precioCalculado = strategy.calcularPrecio(producto, carritoItem.getCantidad());
            BigDecimal precioUnitario = precioCalculado.divide(
                    BigDecimal.valueOf(carritoItem.getCantidad()), 2, BigDecimal.ROUND_HALF_UP);
            
            // Crear item del pedido
            PedidoItem pedidoItem = new PedidoItem(
                    carritoItem.getProductoId(),
                    carritoItem.getCantidad(),
                    precioUnitario
            );
            
            pedidoItems.add(pedidoItem);
            totalPedido = totalPedido.add(precioCalculado);
            
            logger.debug("Item procesado - Producto: {}, Cantidad: {}, Precio: {}", 
                        carritoItem.getProductoId(), carritoItem.getCantidad(), precioCalculado);
        }
        
        // 4. Crear el pedido
        Pedido pedido = Pedido.builder()
                .withClienteId(clienteId)
                .withItems(pedidoItems)
                .withTotal(totalPedido)
                .withEstado(EstadoPedido.CREADO)
                .build();
        
        Pedido pedidoGuardado = pedidoRepository.save(pedido);
        
        // 5. Limpiar el carrito
        carritoRepository.deleteByClienteId(clienteId);
        
        // 6. Publicar evento OrderCreated
        OrderCreatedEvent evento = new OrderCreatedEvent(
                pedidoGuardado.getId(),
                clienteId,
                pedidoItems,
                totalPedido,
                EstadoPedido.CREADO,
                strategy.getNombre()
        );
        eventPublisher.publishEvent(evento);
        
        logger.info("Checkout completado exitosamente - Pedido: {}, Total: {}", 
                   pedidoGuardado.getId(), totalPedido);
        
        return pedidoGuardado;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Pedido obtenerPedido(Long pedidoId) {
        logger.debug("Obteniendo pedido: {}", pedidoId);
        
        if (pedidoId == null) {
            throw new InvalidOrderDataException("Order ID cannot be null");
        }
        
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new InvalidOrderDataException("Order not found: " + pedidoId));
    }
    
    @Override
    public Pedido cancelarPedido(Long pedidoId) {
        logger.info("Cancelando pedido: {}", pedidoId);
        
        Pedido pedido = obtenerPedido(pedidoId);
        
        if (!pedido.puedeSerCancelado()) {
            throw new InvalidOrderDataException(
                    "Order cannot be cancelled in current state: " + pedido.getEstado());
        }
        
        // Restaurar stock
        for (PedidoItem item : pedido.getItems()) {
            inventarioRepository.liberarStock(item.getProductoId(), item.getCantidad());
            logger.debug("Stock liberado - Producto: {}, Cantidad: {}", 
                        item.getProductoId(), item.getCantidad());
        }
        
        // Cancelar pedido
        pedido.cancelar();
        Pedido pedidoCancelado = pedidoRepository.save(pedido);
        
        logger.info("Pedido cancelado exitosamente: {}", pedidoId);
        return pedidoCancelado;
    }
    
    @Override
    public Pedido cambiarEstadoPedido(Long pedidoId, String nuevoEstado) {
        logger.info("Cambiando estado del pedido {} a: {}", pedidoId, nuevoEstado);
        
        Pedido pedido = obtenerPedido(pedidoId);
        EstadoPedido estado;
        
        try {
            estado = EstadoPedido.valueOf(nuevoEstado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOrderDataException("Invalid order state: " + nuevoEstado);
        }
        
        // Aplicar cambio de estado según el estado objetivo
        switch (estado) {
            case PAGADO:
                pedido.marcarComoPagado();
                break;
            case EN_PREPARACION:
                pedido.marcarEnPreparacion();
                break;
            case ENVIADO:
                pedido.marcarComoEnviado();
                break;
            case ENTREGADO:
                pedido.marcarComoEntregado();
                break;
            case CANCELADO:
                return cancelarPedido(pedidoId); // Usa el método que libera stock
            default:
                throw new InvalidOrderDataException("Cannot transition to state: " + estado);
        }
        
        Pedido pedidoActualizado = pedidoRepository.save(pedido);
        
        logger.info("Estado del pedido {} cambiado exitosamente a: {}", pedidoId, estado);
        return pedidoActualizado;
    }
}
