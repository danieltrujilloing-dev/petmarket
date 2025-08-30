package com.interview.petmarket.domain.exceptions;

/**
 * Excepción lanzada cuando los datos de un producto no son válidos.
 */
public class InvalidProductDataException extends RuntimeException {

    public InvalidProductDataException(String message) {
        super(message);
    }

    public InvalidProductDataException(String message, Throwable cause) {
        super(message, cause);
    }
}