package com.automo.model.exception;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
    
    public InvalidStateTransitionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidStateTransitionException(String entityType, Long id, String fromState, String toState) {
        super(String.format("Invalid state transition for %s with ID %d from %s to %s", 
                entityType, id, fromState, toState));
    }
}