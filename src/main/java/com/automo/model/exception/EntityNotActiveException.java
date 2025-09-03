package com.automo.model.exception;

public class EntityNotActiveException extends RuntimeException {
    public EntityNotActiveException(String message) {
        super(message);
    }
    
    public EntityNotActiveException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EntityNotActiveException(String entityType, Long id) {
        super(String.format("%s with ID %d is not active", entityType, id));
    }
}