package com.interview.petmarket.domain.exceptions;

/**
 * Excepción para datos inválidos de clientes
 */
public class InvalidClientDataException extends DomainException {
    
    public InvalidClientDataException(String message) {
        super(message);
    }
    
    public InvalidClientDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
