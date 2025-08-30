package com.interview.petmarket.infrastructure.persistence.jpa.entity;

/**
 * Enum JPA para estados de pedido.
 */
public enum EstadoPedidoEntity {
    CREADO,
    PAGADO,
    EN_PREPARACION,
    ENVIADO,
    ENTREGADO,
    CANCELADO
}
