package com.interview.petmarket.domain.exceptions;

/**
 * Excepción lanzada cuando los datos de inventario son inválidos.
 */
public class InvalidInventoryDataException extends RuntimeException {
    
    public InvalidInventoryDataException(String message) {
        super(message);
    }
    
    public InvalidInventoryDataException(String message, Throwable cause) {
        super(message, cause);
    }
}