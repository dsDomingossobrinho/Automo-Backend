package com.automo.exception;

import com.automo.exception.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AutomoException.class)
    public ResponseEntity<ApiErrorResponse> handleAutomoException(
            AutomoException ex, 
            HttpServletRequest request) {
        
        log.error("Automo exception occurred: {}", ex.getMessage(), ex);
        
        ApiErrorResponse error = new ApiErrorResponse(
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getHttpStatus().value(),
                request.getRequestURI()
        );
        
        if (ex instanceof ValidationException validationEx) {
            error.setFieldErrors(validationEx.getFieldErrors());
        }
        
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        log.warn("Validation failed for request {}: {}", request.getRequestURI(), fieldErrors);
        
        ApiErrorResponse error = new ApiErrorResponse(
                "VALIDATION_ERROR",
                "Input validation failed",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                fieldErrors
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationException(
            AuthenticationException ex,
            HttpServletRequest request) {
        
        log.warn("Authentication failed for request {}: {}", request.getRequestURI(), ex.getMessage());
        
        // Mensagem mais clara baseada no contexto da requisição
        String message = "Email ou senha incorretos. Verifique suas credenciais e tente novamente.";
        
        if (request.getRequestURI().contains("request-otp")) {
            message = "Email ou senha incorretos. Não foi possível gerar o código OTP.";
        }
        
        ApiErrorResponse error = new ApiErrorResponse(
                "INVALID_CREDENTIALS",
                message,
                HttpStatus.UNAUTHORIZED.value(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {
        
        log.warn("Access denied for request {}: {}", request.getRequestURI(), ex.getMessage());
        
        ApiErrorResponse error = new ApiErrorResponse(
                "ACCESS_DENIED",
                "Access denied",
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {
        
        log.error("Data integrity violation for request {}: {}", request.getRequestURI(), ex.getMessage());
        
        String message = "Data integrity constraint violation";
        if (ex.getMessage() != null && ex.getMessage().contains("duplicate key")) {
            message = "Duplicate entry found";
        }
        
        ApiErrorResponse error = new ApiErrorResponse(
                "DATA_INTEGRITY_ERROR",
                message,
                HttpStatus.CONFLICT.value(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request) {
        
        log.warn("Illegal argument for request {}: {}", request.getRequestURI(), ex.getMessage());
        
        ApiErrorResponse error = new ApiErrorResponse(
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        log.error("Unexpected error occurred for request {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        
        ApiErrorResponse error = new ApiErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}