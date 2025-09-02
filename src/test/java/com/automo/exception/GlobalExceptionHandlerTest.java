package com.automo.exception;

import com.automo.exception.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    void handleUserNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        UserNotFoundException exception = UserNotFoundException.byEmail("test@example.com");

        // Act
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleAutomoException(exception, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("USER_NOT_FOUND", response.getBody().getErrorCode());
        assertEquals("User not found with email: test@example.com", response.getBody().getMessage());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("/api/test", response.getBody().getPath());
    }

    @Test
    void handleUserAlreadyExistsException_ShouldReturnConflictResponse() {
        // Arrange
        UserAlreadyExistsException exception = UserAlreadyExistsException.withEmail("test@example.com");

        // Act
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleAutomoException(exception, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("USER_ALREADY_EXISTS", response.getBody().getErrorCode());
        assertEquals("User already exists with email: test@example.com", response.getBody().getMessage());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    void handleInvalidCredentialsException_ShouldReturnUnauthorizedResponse() {
        // Arrange
        InvalidCredentialsException exception = InvalidCredentialsException.create();

        // Act
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleAutomoException(exception, request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_CREDENTIALS", response.getBody().getErrorCode());
        assertEquals("Invalid email/contact or password", response.getBody().getMessage());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void handleValidationException_WithFieldErrors_ShouldReturnBadRequestWithFieldErrors() {
        // Arrange
        Map<String, String> fieldErrors = Map.of("email", "Invalid format", "password", "Too short");
        ValidationException exception = ValidationException.multipleFields(fieldErrors);

        // Act
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleAutomoException(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VALIDATION_ERROR", response.getBody().getErrorCode());
        assertEquals("Multiple validation errors", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
        assertEquals(fieldErrors, response.getBody().getFieldErrors());
    }

    @Test
    void handleAuthenticationException_ShouldReturnUnauthorizedResponse() {
        // Arrange
        AuthenticationException exception = new AuthenticationException("Authentication failed") {};

        // Act
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleAuthenticationException(exception, request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("AUTHENTICATION_FAILED", response.getBody().getErrorCode());
        assertEquals("Authentication failed", response.getBody().getMessage());
        assertEquals(401, response.getBody().getStatus());
    }

    @Test
    void handleAccessDeniedException_ShouldReturnForbiddenResponse() {
        // Arrange
        AccessDeniedException exception = new AccessDeniedException("Access denied");

        // Act
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleAccessDeniedException(exception, request);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ACCESS_DENIED", response.getBody().getErrorCode());
        assertEquals("Access denied", response.getBody().getMessage());
        assertEquals(403, response.getBody().getStatus());
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequestResponse() {
        // Arrange
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");

        // Act
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleIllegalArgumentException(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ILLEGAL_ARGUMENT", response.getBody().getErrorCode());
        assertEquals("Invalid argument", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        RuntimeException exception = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<ApiErrorResponse> response = globalExceptionHandler.handleGenericException(exception, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_SERVER_ERROR", response.getBody().getErrorCode());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
    }
}