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
        return new InvalidCredentialsException("Email ou senha incorretos. Verifique suas credenciais e tente novamente.");
    }
    
    public static InvalidCredentialsException invalidPassword() {
        return new InvalidCredentialsException("Invalid password");
    }
    
    public static InvalidCredentialsException expiredToken() {
        return new InvalidCredentialsException("Código OTP inválido ou expirado. Solicite um novo código.");
    }
    
    public static InvalidCredentialsException invalidOtp() {
        return new InvalidCredentialsException("Código OTP incorreto. Verifique o código e tente novamente.");
    }
}