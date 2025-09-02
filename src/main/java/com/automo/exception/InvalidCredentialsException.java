package com.automo.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AutomoException {
    
    public InvalidCredentialsException(String message) {
        super(message, "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED);
    }
    
    public InvalidCredentialsException(String message, Throwable cause) {
        super(message, "INVALID_CREDENTIALS", HttpStatus.UNAUTHORIZED, cause);
    }
    
    public static InvalidCredentialsException create() {
        return new InvalidCredentialsException("Invalid email/contact or password");
    }
    
    public static InvalidCredentialsException invalidPassword() {
        return new InvalidCredentialsException("Invalid password");
    }
    
    public static InvalidCredentialsException expiredToken() {
        return new InvalidCredentialsException("Token has expired");
    }
}