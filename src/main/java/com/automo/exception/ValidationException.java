package com.automo.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

public class ValidationException extends AutomoException {
    
    private final Map<String, String> fieldErrors;
    
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.fieldErrors = fieldErrors;
    }
    
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", HttpStatus.BAD_REQUEST);
        this.fieldErrors = Map.of();
    }
    
    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
    
    public static ValidationException singleField(String field, String error) {
        return new ValidationException("Validation failed", Map.of(field, error));
    }
    
    public static ValidationException multipleFields(Map<String, String> errors) {
        return new ValidationException("Multiple validation errors", errors);
    }
}