package com.automo.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends AutomoException {
    
    public UserAlreadyExistsException(String message) {
        super(message, "USER_ALREADY_EXISTS", HttpStatus.CONFLICT);
    }
    
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, "USER_ALREADY_EXISTS", HttpStatus.CONFLICT, cause);
    }
    
    public static UserAlreadyExistsException withEmail(String email) {
        return new UserAlreadyExistsException("User already exists with email: " + email);
    }
    
    public static UserAlreadyExistsException withContact(String contact) {
        return new UserAlreadyExistsException("User already exists with contact: " + contact);
    }
    
    public static UserAlreadyExistsException withEmailOrContact(String emailOrContact) {
        return new UserAlreadyExistsException("User already exists with email or contact: " + emailOrContact);
    }
}