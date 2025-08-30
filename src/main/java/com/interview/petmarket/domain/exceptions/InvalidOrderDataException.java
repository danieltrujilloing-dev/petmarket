package com.interview.petmarket.domain.exceptions;

/**
 * Excepción para datos inválidos de pedidos
 */
public class InvalidOrderDataException extends DomainException {
    
    public InvalidOrderDataException(String message) {
        super(message);
    }
    
    public InvalidOrderDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
