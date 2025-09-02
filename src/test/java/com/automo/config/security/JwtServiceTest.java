package com.automo.config.security;

import com.automo.auth.entity.Auth;
import com.automo.authRoles.entity.AuthRoles;
import com.automo.role.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private Auth auth;

    @Mock
    private AuthRoles authRoles1;

    @Mock
    private AuthRoles authRoles2;

    @Mock
    private Role role1;

    @Mock
    private Role role2;

    private String secretKey = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private long jwtExpiration = 86400000; // 24 hours
    private long refreshTokenExpiration = 604800000; // 7 days

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", jwtExpiration);
        ReflectionTestUtils.setField(jwtService, "refreshExpiration", refreshTokenExpiration);
        
        // Initialize after setting fields
        jwtService.init();
    }

    @Test
    void generateToken_WithValidAuth_ShouldReturnJwtToken() {
        // Arrange
        when(auth.getId()).thenReturn(1L);
        when(auth.getUsername()).thenReturn("testuser");
        when(auth.getEmail()).thenReturn("test@example.com");
        
        when(role1.getId()).thenReturn(1L);
        when(role2.getId()).thenReturn(2L);
        when(authRoles1.getRole()).thenReturn(role1);
        when(authRoles2.getRole()).thenReturn(role2);
        
        List<AuthRoles> authRolesList = Arrays.asList(authRoles1, authRoles2);
        when(auth.getAuthRoles()).thenReturn(authRolesList);

        // Act
        String token = jwtService.generateTokenForAuth(auth);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.startsWith("eyJ")); // JWT tokens start with "eyJ"
    }

    @Test
    void generateRefreshToken_WithValidAuth_ShouldReturnRefreshToken() {
        // Arrange
        when(auth.getId()).thenReturn(1L);
        when(auth.getUsername()).thenReturn("testuser");
        when(auth.getEmail()).thenReturn("test@example.com");
        when(auth.getAuthRoles()).thenReturn(Arrays.asList());

        // Act
        String refreshToken = jwtService.generateRefreshTokenForAuth(auth);

        // Assert
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(refreshToken.startsWith("eyJ"));
    }

    @Test
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        // Arrange
        when(auth.getId()).thenReturn(1L);
        when(auth.getUsername()).thenReturn("testuser");
        when(auth.getEmail()).thenReturn("test@example.com");
        when(auth.getAuthRoles()).thenReturn(Arrays.asList());
        
        String token = jwtService.generateTokenForAuth(auth);

        // Act
        String username = jwtService.extractUsername(token);

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void extractUserId_WithValidToken_ShouldReturnUserId() {
        // Arrange
        when(auth.getId()).thenReturn(1L);
        when(auth.getUsername()).thenReturn("testuser");
        when(auth.getEmail()).thenReturn("test@example.com");
        when(auth.getAuthRoles()).thenReturn(Arrays.asList());
        
        String token = jwtService.generateTokenForAuth(auth);

        // Act
        Long userId = jwtService.extractUserId(token);

        // Assert
        assertEquals(1L, userId);
    }

    @Test
    void extractEmail_WithValidToken_ShouldReturnEmail() {
        // Arrange
        when(auth.getId()).thenReturn(1L);
        when(auth.getUsername()).thenReturn("testuser");
        when(auth.getEmail()).thenReturn("test@example.com");
        when(auth.getAuthRoles()).thenReturn(Arrays.asList());
        
        String token = jwtService.generateTokenForAuth(auth);

        // Act
        String email = jwtService.extractEmail(token);

        // Assert
        assertEquals("test@example.com", email);
    }

    @Test
    void isTokenValid_WithValidTokenAndMatchingUsername_ShouldReturnTrue() {
        // Arrange
        when(auth.getId()).thenReturn(1L);
        when(auth.getUsername()).thenReturn("testuser");
        when(auth.getEmail()).thenReturn("test@example.com");
        when(auth.getAuthRoles()).thenReturn(Arrays.asList());
        
        String token = jwtService.generateTokenForAuth(auth);

        // Act
        boolean isValid = jwtService.isTokenValid(token, "testuser");

        // Assert
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithValidTokenButDifferentUsername_ShouldReturnFalse() {
        // Arrange
        when(auth.getId()).thenReturn(1L);
        when(auth.getUsername()).thenReturn("testuser");
        when(auth.getEmail()).thenReturn("test@example.com");
        when(auth.getAuthRoles()).thenReturn(Arrays.asList());
        
        String token = jwtService.generateTokenForAuth(auth);

        // Act
        boolean isValid = jwtService.isTokenValid(token, "differentuser");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void isTokenExpired_WithFreshToken_ShouldReturnFalse() {
        // Arrange
        when(auth.getId()).thenReturn(1L);
        when(auth.getUsername()).thenReturn("testuser");
        when(auth.getEmail()).thenReturn("test@example.com");
        when(auth.getAuthRoles()).thenReturn(Arrays.asList());
        
        String token = jwtService.generateTokenForAuth(auth);

        // Act
        boolean isExpired = jwtService.isTokenExpired(token);

        // Assert
        assertFalse(isExpired);
    }
}