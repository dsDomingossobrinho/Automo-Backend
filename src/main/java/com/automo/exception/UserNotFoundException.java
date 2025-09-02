package com.automo.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AutomoException {
    
    public UserNotFoundException(String message) {
        super(message, "USER_NOT_FOUND", HttpStatus.NOT_FOUND);
    }
    
    public UserNotFoundException(String message, Throwable cause) {
        super(message, "USER_NOT_FOUND", HttpStatus.NOT_FOUND, cause);
    }
    
    public static UserNotFoundException byId(Long id) {
        return new UserNotFoundException("User not found with ID: " + id);
    }
    
    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException("User not found with email: " + email);
    }
    
    public static UserNotFoundException byEmailOrContact(String emailOrContact) {
        return new UserNotFoundException("User not found with email or contact: " + emailOrContact);
    }
}