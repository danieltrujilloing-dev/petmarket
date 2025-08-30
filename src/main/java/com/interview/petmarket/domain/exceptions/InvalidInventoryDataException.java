package com.interview.petmarket.domain.exceptions;

/**
 * Excepción para datos inválidos de inventario
 */
public class InvalidInventoryDataException extends DomainException {
    
    public InvalidInventoryDataException(String message) {
        super(message);
    }
    
    public InvalidInventoryDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
