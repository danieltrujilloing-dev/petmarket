package com.interview.petmarket.domain.exceptions;

/**
 * Excepción para datos inválidos del carrito
 */
public class InvalidCartDataException extends DomainException {
    
    public InvalidCartDataException(String message) {
        super(message);
    }
    
    public InvalidCartDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
