package com.interview.petmarket.domain.exceptions;

/**
 * Excepción para datos inválidos de productos
 */
public class InvalidProductDataException extends DomainException {
    
    public InvalidProductDataException(String message) {
        super(message);
    }
    
    public InvalidProductDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
