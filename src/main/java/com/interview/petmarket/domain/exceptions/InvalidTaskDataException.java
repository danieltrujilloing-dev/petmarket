package com.interview.petmarket.domain.exceptions;

/**
 * Excepción lanzada cuando los datos de tarea de reposición son inválidos.
 */
public class InvalidTaskDataException extends RuntimeException {
    
    public InvalidTaskDataException(String message) {
        super(message);
    }
    
    public InvalidTaskDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
