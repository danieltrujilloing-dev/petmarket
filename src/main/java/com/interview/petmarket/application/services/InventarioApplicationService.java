package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.events.LowStockEvent;
import com.interview.petmarket.domain.events.StockConfirmedEvent;
import com.interview.petmarket.domain.exceptions.InvalidInventoryDataException;
import com.interview.petmarket.domain.exceptions.InvalidOrderDataException;
import com.interview.petmarket.domain.model.inventario.Inventario;
import com.interview.petmarket.domain.model.inventario.PrioridadTarea;
import com.interview.petmarket.domain.model.pedido.Pedido;
import com.interview.petmarket.domain.model.pedido.PedidoItem;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.ports.in.GestionarInventarioUseCase;
import com.interview.petmarket.domain.ports.out.EventPublisherPort;
import com.interview.petmarket.domain.ports.out.InventarioRepositoryPort;
import com.interview.petmarket.domain.ports.out.PedidoRepositoryPort;
import com.interview.petmarket.domain.ports.out.ProductoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de aplicación para gestión de inventario.
 * Implementa los principios SOLID:
 * - SRP: Solo se encarga de la lógica de inventario
 * - OCP: Extensible para nuevas funcionalidades
 * - LSP: Implementa correctamente la interfaz
 * - ISP: Usa interfaces específicas
 * - DIP: Depende de abstracciones, no de implementaciones
 */
@Transactional
public class InventarioApplicationService implements GestionarInventarioUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(InventarioApplicationService.class);
    
    private final InventarioRepositoryPort inventarioRepository;
    private final PedidoRepositoryPort pedidoRepository;
    private final ProductoRepositoryPort productoRepository;
    private final EventPublisherPort eventPublisher;
    
    public InventarioApplicationService(InventarioRepositoryPort inventarioRepository,
                                       PedidoRepositoryPort pedidoRepository,
                                       ProductoRepositoryPort productoRepository,
                                       EventPublisherPort eventPublisher) {
        this.inventarioRepository = inventarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    @Transactional
    public List<Inventario> confirmarStockPorPago(Long pedidoId) {
        logger.info("Confirmando stock por pago del pedido: {}", pedidoId);
        
        if (pedidoId == null) {
            throw new InvalidOrderDataException("Order ID cannot be null");
        }
        
        // Obtener el pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new InvalidOrderDataException("Order not found: " + pedidoId));
        
        List<Inventario> inventariosActualizados = new ArrayList<>();
        
        // Procesar cada item del pedido
        for (PedidoItem item : pedido.getItems()) {
            Inventario inventarioActualizado = confirmarStockItem(pedido, item);
            inventariosActualizados.add(inventarioActualizado);
        }
        
        logger.info("Stock confirmado exitosamente para {} productos del pedido: {}", 
                   inventariosActualizados.size(), pedidoId);
        
        return inventariosActualizados;
    }
    
    @Override
    public Inventario obtenerInventario(Long productoId) {
        logger.debug("Obteniendo inventario para producto: {}", productoId);
        
        if (productoId == null) {
            throw new InvalidInventoryDataException("Product ID cannot be null");
        }
        
        return inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new InvalidInventoryDataException("Inventory not found for product: " + productoId));
    }
    
    @Override
    public List<Inventario> obtenerInventariosConStockBajo() {
        logger.debug("Obteniendo inventarios con stock bajo");
        return inventarioRepository.findByStockBelowThreshold();
    }
    
    @Override
    @Transactional
    public Inventario actualizarStock(Long productoId, int nuevaCantidad, String motivo) {
        logger.info("Actualizando stock del producto {} a {} unidades. Motivo: {}", 
                   productoId, nuevaCantidad, motivo);
        
        if (productoId == null) {
            throw new InvalidInventoryDataException("Product ID cannot be null");
        }
        if (nuevaCantidad < 0) {
            throw new InvalidInventoryDataException("Stock quantity cannot be negative");
        }
        
        Inventario inventario = obtenerInventario(productoId);
        int stockAnterior = inventario.getStockDisponible();
        
        // Crear nuevo inventario con stock actualizado
        Inventario inventarioActualizado = Inventario.builder()
                .withId(inventario.getId())
                .withProductoId(inventario.getProductoId())
                .withStockDisponible(nuevaCantidad)
                .withUmbralReposicion(inventario.getUmbralReposicion())
                .build();
        inventarioActualizado = inventarioRepository.save(inventarioActualizado);
        
        // Verificar si necesita emitir evento de stock bajo
        verificarYEmitirEventoStockBajo(inventarioActualizado, "Manual stock update: " + motivo);
        
        logger.info("Stock actualizado exitosamente - Producto: {}, Stock anterior: {}, Stock actual: {}", 
                   productoId, stockAnterior, nuevaCantidad);
        
        return inventarioActualizado;
    }
    
    @Override
    @Transactional
    public Inventario ajustarUmbralReposicion(Long productoId, int nuevoUmbral) {
        logger.info("Ajustando umbral de reposición del producto {} a {}", productoId, nuevoUmbral);
        
        if (productoId == null) {
            throw new InvalidInventoryDataException("Product ID cannot be null");
        }
        if (nuevoUmbral < 0) {
            throw new InvalidInventoryDataException("Replenishment threshold cannot be negative");
        }
        
        Inventario inventario = obtenerInventario(productoId);
        
        // Crear nuevo inventario con umbral actualizado
        Inventario inventarioActualizado = Inventario.builder()
                .withId(inventario.getId())
                .withProductoId(inventario.getProductoId())
                .withStockDisponible(inventario.getStockDisponible())
                .withUmbralReposicion(nuevoUmbral)
                .build();
        inventarioActualizado = inventarioRepository.save(inventarioActualizado);
        
        // Verificar si el nuevo umbral requiere emitir evento de stock bajo
        verificarYEmitirEventoStockBajo(inventarioActualizado, "Threshold adjustment");
        
        logger.info("Umbral de reposición ajustado exitosamente - Producto: {}, Nuevo umbral: {}", 
                   productoId, nuevoUmbral);
        
        return inventarioActualizado;
    }
    
    private Inventario confirmarStockItem(Pedido pedido, PedidoItem item) {
        Long productoId = item.getProductoId();
        int cantidadConfirmada = item.getCantidad();
        
        logger.debug("Confirmando stock para producto {} - Cantidad: {}", productoId, cantidadConfirmada);
        
        // Obtener inventario actual
        Inventario inventario = obtenerInventario(productoId);
        int stockAnterior = inventario.getStockDisponible();
        
        // Obtener información del producto para el evento
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new InvalidInventoryDataException("Product not found: " + productoId));
        
        // La confirmación no reduce más stock, solo confirma la reducción ya hecha en la reserva
        // Pero emitimos el evento de confirmación
        StockConfirmedEvent evento = new StockConfirmedEvent(
                pedido.getId(),
                productoId,
                producto.getNombre(),
                cantidadConfirmada,
                stockAnterior + cantidadConfirmada, // Stock antes de la reserva
                stockAnterior, // Stock actual (ya reducido por la reserva)
                "Payment confirmation for order " + pedido.getId()
        );
        
        eventPublisher.publishEvent(evento);
        
        // Verificar si necesita emitir evento de stock bajo
        verificarYEmitirEventoStockBajo(inventario, "Stock confirmed after payment");
        
        logger.debug("Stock confirmado para producto {} - Stock actual: {}", productoId, stockAnterior);
        
        return inventario;
    }
    
    private void verificarYEmitirEventoStockBajo(Inventario inventario, String razonActivacion) {
        if (inventario.necesitaReposicion()) {
            logger.warn("Stock bajo detectado para producto {} - Stock: {}, Umbral: {}", 
                       inventario.getProductoId(), inventario.getStockDisponible(), 
                       inventario.getUmbralReposicion());
            
            // Obtener información del producto
            Producto producto = productoRepository.findById(inventario.getProductoId())
                    .orElse(null);
            
            String nombreProducto = producto != null ? producto.getNombre() : "Producto " + inventario.getProductoId();
            
            // Calcular cantidad sugerida (2x el umbral o mínimo 10)
            int cantidadSugerida = Math.max(inventario.getUmbralReposicion() * 2, 10);
            
            // Determinar prioridad
            PrioridadTarea prioridad = determinarPrioridad(inventario);
            
            LowStockEvent evento = new LowStockEvent(
                    inventario.getProductoId(),
                    nombreProducto,
                    inventario.getStockDisponible(),
                    inventario.getUmbralReposicion(),
                    cantidadSugerida,
                    prioridad,
                    razonActivacion
            );
            
            eventPublisher.publishEvent(evento);
            
            logger.info("Evento LowStock emitido para producto {} - Prioridad: {}", 
                       inventario.getProductoId(), prioridad);
        }
    }
    
    private PrioridadTarea determinarPrioridad(Inventario inventario) {
        int stock = inventario.getStockDisponible();
        int umbral = inventario.getUmbralReposicion();
        
        if (stock == 0) {
            return PrioridadTarea.CRITICA;
        }
        
        if (umbral == 0) {
            return PrioridadTarea.BAJA;
        }
        
        double porcentaje = (double) stock / umbral * 100.0;
        
        if (porcentaje <= 25) {
            return PrioridadTarea.CRITICA;
        } else if (porcentaje <= 50) {
            return PrioridadTarea.ALTA;
        } else if (porcentaje <= 75) {
            return PrioridadTarea.MEDIA;
        } else {
            return PrioridadTarea.BAJA;
        }
    }
}
