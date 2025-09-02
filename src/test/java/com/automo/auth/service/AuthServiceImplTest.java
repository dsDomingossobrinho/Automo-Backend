package com.automo.auth.service;

import com.automo.auth.dto.LoginRequest;
import com.automo.auth.dto.LoginResponse;
import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.config.security.JwtService;
import com.automo.exception.InvalidCredentialsException;
import com.automo.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Auth testAuth;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testAuth = new Auth();
        testAuth.setId(1L);
        testAuth.setEmail("test@example.com");
        testAuth.setUsername("testuser");
        testAuth.setPassword("encodedPassword");

        loginRequest = new LoginRequest("test@example.com", "rawPassword");
    }

    @Test
    void authenticate_WithValidCredentials_ShouldReturnLoginResponse() {
        // Arrange
        when(authRepository.findByEmailOrUsernameOrContact(any())).thenReturn(Optional.of(testAuth));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtService.generateTokenForAuth(any())).thenReturn("jwt-token");

        // Act
        LoginResponse response = authService.authenticate(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        verify(authRepository).findByEmailOrUsernameOrContact("test@example.com");
        verify(passwordEncoder).matches("rawPassword", "encodedPassword");
        verify(jwtService).generateTokenForAuth(testAuth);
        verify(jwtService, never()).generateRefreshTokenForAuth(any());
    }

    @Test
    void authenticate_WithInvalidUser_ShouldThrowUserNotFoundException() {
        // Arrange
        when(authRepository.findByEmailOrUsernameOrContact(any())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> authService.authenticate(loginRequest));
        verify(authRepository).findByEmailOrUsernameOrContact("test@example.com");
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void authenticate_WithInvalidPassword_ShouldThrowInvalidCredentialsException() {
        // Arrange
        when(authRepository.findByEmailOrUsernameOrContact(any())).thenReturn(Optional.of(testAuth));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> authService.authenticate(loginRequest));
        verify(authRepository).findByEmailOrUsernameOrContact("test@example.com");
        verify(passwordEncoder).matches("rawPassword", "encodedPassword");
        verify(jwtService, never()).generateTokenForAuth(any());
    }

    @Test
    void authenticate_WithNullRequest_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.authenticate(null));
        verify(authRepository, never()).findByEmailOrUsernameOrContact(any());
    }

    @Test
    void findByEmailOrUsernameOrContact_WithValidIdentifier_ShouldReturnAuth() {
        // Arrange
        when(authRepository.findByEmailOrUsernameOrContact(any())).thenReturn(Optional.of(testAuth));

        // Act
        Optional<Auth> result = authService.findByEmailOrUsernameOrContact("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testAuth, result.get());
        verify(authRepository).findByEmailOrUsernameOrContact("test@example.com");
    }

    @Test
    void findByEmailOrUsernameOrContact_WithInvalidIdentifier_ShouldReturnEmpty() {
        // Arrange
        when(authRepository.findByEmailOrUsernameOrContact(any())).thenReturn(Optional.empty());

        // Act
        Optional<Auth> result = authService.findByEmailOrUsernameOrContact("nonexistent@example.com");

        // Assert
        assertTrue(result.isEmpty());
        verify(authRepository).findByEmailOrUsernameOrContact("nonexistent@example.com");
    }
}