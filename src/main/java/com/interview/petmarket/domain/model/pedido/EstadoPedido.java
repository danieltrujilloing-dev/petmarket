package com.interview.petmarket.domain.model.pedido;

/**
 * Enum para los estados posibles de un pedido
 */
public enum EstadoPedido {
    CREADO("Pedido creado, esperando pago"),
    PAGADO("Pedido pagado, preparando envío"),
    EN_PREPARACION("Pedido en preparación"),
    ENVIADO("Pedido enviado al cliente"),
    ENTREGADO("Pedido entregado exitosamente"),
    CANCELADO("Pedido cancelado");
    
    private final String descripcion;
    
    EstadoPedido(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public boolean esPendiente() {
        return this == CREADO || this == PAGADO || this == EN_PREPARACION || this == ENVIADO;
    }
    
    public boolean esTerminal() {
        return this == ENTREGADO || this == CANCELADO;
    }
    
    public boolean puedeTransicionarA(EstadoPedido nuevoEstado) {
        switch (this) {
            case CREADO:
                return nuevoEstado == PAGADO || nuevoEstado == CANCELADO;
            case PAGADO:
                return nuevoEstado == EN_PREPARACION || nuevoEstado == CANCELADO;
            case EN_PREPARACION:
                return nuevoEstado == ENVIADO || nuevoEstado == CANCELADO;
            case ENVIADO:
                return nuevoEstado == ENTREGADO;
            case ENTREGADO:
            case CANCELADO:
                return false; // Estados terminales
            default:
                return false;
        }
    }
}
