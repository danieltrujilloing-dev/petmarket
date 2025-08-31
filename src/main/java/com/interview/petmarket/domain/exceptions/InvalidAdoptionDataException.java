package com.interview.petmarket.domain.exceptions;

/**
 * Excepción específica para errores en datos de adopción
 */
public class InvalidAdoptionDataException extends DomainException {
    
    public InvalidAdoptionDataException(String message) {
        super(message);
    }
    
    public InvalidAdoptionDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
