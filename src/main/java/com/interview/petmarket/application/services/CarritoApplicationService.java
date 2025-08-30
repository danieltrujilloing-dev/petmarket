package com.interview.petmarket.application.services;

import com.interview.petmarket.domain.events.CartItemAddedEvent;
import com.interview.petmarket.domain.exceptions.InvalidCartDataException;
import com.interview.petmarket.domain.exceptions.InvalidProductDataException;
import com.interview.petmarket.domain.model.carrito.Carrito;
import com.interview.petmarket.domain.model.carrito.CarritoItem;
import com.interview.petmarket.domain.model.producto.Producto;
import com.interview.petmarket.domain.ports.in.GestionarCarritoUseCase;
import com.interview.petmarket.domain.ports.out.CarritoRepositoryPort;
import com.interview.petmarket.domain.ports.out.EventPublisherPort;
import com.interview.petmarket.domain.ports.out.ProductoRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de aplicación para gestionar carritos.
 * Implementa los principios SOLID:
 * - SRP: Solo se encarga de la lógica de carrito
 * - OCP: Extensible para nuevas funcionalidades
 * - LSP: Implementa correctamente la interfaz
 * - ISP: Usa interfaces específicas
 * - DIP: Depende de abstracciones, no de implementaciones
 */
@Transactional
public class CarritoApplicationService implements GestionarCarritoUseCase {
    
    private static final Logger logger = LoggerFactory.getLogger(CarritoApplicationService.class);
    
    private final CarritoRepositoryPort carritoRepository;
    private final ProductoRepositoryPort productoRepository;
    private final EventPublisherPort eventPublisher;
    
    public CarritoApplicationService(CarritoRepositoryPort carritoRepository,
                                   ProductoRepositoryPort productoRepository,
                                   EventPublisherPort eventPublisher) {
        this.carritoRepository = carritoRepository;
        this.productoRepository = productoRepository;
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public Carrito obtenerCarrito(Long clienteId) {
        logger.debug("Obteniendo carrito para cliente: {}", clienteId);
        
        if (clienteId == null) {
            throw new InvalidCartDataException("Client ID cannot be null");
        }
        
        return carritoRepository.findByClienteId(clienteId)
                .orElseGet(() -> {
                    logger.debug("Carrito no encontrado, creando nuevo para cliente: {}", clienteId);
                    Carrito nuevoCarrito = Carrito.builder()
                            .withClienteId(clienteId)
                            .build();
                    return carritoRepository.save(nuevoCarrito);
                });
    }
    
    @Override
    public Carrito agregarItem(Long clienteId, Long productoId, int cantidad) {
        logger.info("Agregando item al carrito - Cliente: {}, Producto: {}, Cantidad: {}", 
                   clienteId, productoId, cantidad);
        
        // Validaciones
        validarParametrosItem(clienteId, productoId, cantidad);
        
        // Verificar que el producto existe y está activo
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new InvalidProductDataException("Product not found: " + productoId));
        
        if (!producto.isActivo()) {
            throw new InvalidProductDataException("Product is not active: " + productoId);
        }
        
        // Obtener carrito
        Carrito carrito = obtenerCarrito(clienteId);
        
        // Guardar cantidad anterior para el evento
        int cantidadAnterior = carrito.obtenerItem(productoId)
                .map(CarritoItem::getCantidad)
                .orElse(0);
        
        // Agregar item al carrito
        carrito.agregarItem(productoId, cantidad);
        
        // Guardar carrito
        Carrito carritoActualizado = carritoRepository.save(carrito);
        
        // Publicar evento
        CartItemAddedEvent evento = new CartItemAddedEvent(clienteId, productoId, 
                                                          cantidadAnterior + cantidad, cantidadAnterior);
        eventPublisher.publishEvent(evento);
        
        logger.info("Item agregado exitosamente al carrito del cliente: {}", clienteId);
        return carritoActualizado;
    }
    
    @Override
    public Carrito actualizarCantidadItem(Long clienteId, Long productoId, int nuevaCantidad) {
        logger.info("Actualizando cantidad de item - Cliente: {}, Producto: {}, Nueva cantidad: {}", 
                   clienteId, productoId, nuevaCantidad);
        
        // Validaciones
        if (clienteId == null) {
            throw new InvalidCartDataException("Client ID cannot be null");
        }
        if (productoId == null) {
            throw new InvalidCartDataException("Product ID cannot be null");
        }
        if (nuevaCantidad < 0) {
            throw new InvalidCartDataException("Quantity cannot be negative");
        }
        
        // Obtener carrito
        Carrito carrito = obtenerCarrito(clienteId);
        
        // Actualizar cantidad
        carrito.actualizarCantidadItem(productoId, nuevaCantidad);
        
        // Guardar carrito
        Carrito carritoActualizado = carritoRepository.save(carrito);
        
        logger.info("Cantidad actualizada exitosamente para cliente: {}", clienteId);
        return carritoActualizado;
    }
    
    @Override
    public Carrito eliminarItem(Long clienteId, Long productoId) {
        logger.info("Eliminando item del carrito - Cliente: {}, Producto: {}", clienteId, productoId);
        
        // Validaciones
        if (clienteId == null) {
            throw new InvalidCartDataException("Client ID cannot be null");
        }
        if (productoId == null) {
            throw new InvalidCartDataException("Product ID cannot be null");
        }
        
        // Obtener carrito
        Carrito carrito = obtenerCarrito(clienteId);
        
        // Eliminar item
        carrito.eliminarItem(productoId);
        
        // Guardar carrito
        Carrito carritoActualizado = carritoRepository.save(carrito);
        
        logger.info("Item eliminado exitosamente del carrito del cliente: {}", clienteId);
        return carritoActualizado;
    }
    
    @Override
    public void limpiarCarrito(Long clienteId) {
        logger.info("Limpiando carrito del cliente: {}", clienteId);
        
        if (clienteId == null) {
            throw new InvalidCartDataException("Client ID cannot be null");
        }
        
        // Eliminar carrito completamente
        carritoRepository.deleteByClienteId(clienteId);
        
        logger.info("Carrito limpiado exitosamente para cliente: {}", clienteId);
    }
    
    // Métodos privados de validación
    private void validarParametrosItem(Long clienteId, Long productoId, int cantidad) {
        if (clienteId == null) {
            throw new InvalidCartDataException("Client ID cannot be null");
        }
        if (productoId == null) {
            throw new InvalidCartDataException("Product ID cannot be null");
        }
        if (cantidad <= 0) {
            throw new InvalidCartDataException("Quantity must be positive");
        }
    }
}
