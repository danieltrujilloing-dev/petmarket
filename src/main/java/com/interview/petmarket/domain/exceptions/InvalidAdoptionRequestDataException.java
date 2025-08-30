package com.interview.petmarket.domain.exceptions;

/**
 * Excepción para datos inválidos de solicitudes de adopción
 */
public class InvalidAdoptionRequestDataException extends DomainException {
    
    public InvalidAdoptionRequestDataException(String message) {
        super(message);
    }
    
    public InvalidAdoptionRequestDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
