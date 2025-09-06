package com.automo.auth.entity;

import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for Auth Entity")
class AuthTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Auth entity")
    void shouldCreateValidAuthEntity() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        
        // When
        Set<ConstraintViolation<Auth>> violations = validator.validate(auth);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("test@automo.com", auth.getEmail());
        assertEquals("$2a$10$encrypted.password.hash", auth.getPassword());
        assertNotNull(auth.getCreatedAt());
        assertNotNull(auth.getUpdatedAt());
    }

    @Test
    @DisplayName("Should fail validation with null email")
    void shouldFailValidationWithNullEmail() {
        // Given
        Auth auth = new Auth();
        auth.setEmail(null);
        auth.setPassword("password123");
        
        // When
        Set<ConstraintViolation<Auth>> violations = validator.validate(auth);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail validation with invalid email format")
    void shouldFailValidationWithInvalidEmailFormat() {
        // Given
        Auth auth = new Auth();
        auth.setEmail("invalid-email");
        auth.setPassword("password123");
        
        // When
        Set<ConstraintViolation<Auth>> violations = validator.validate(auth);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail validation with null password")
    void shouldFailValidationWithNullPassword() {
        // Given
        Auth auth = new Auth();
        auth.setEmail("test@automo.com");
        auth.setPassword(null);
        
        // When
        Set<ConstraintViolation<Auth>> violations = validator.validate(auth);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should fail validation with blank password")
    void shouldFailValidationWithBlankPassword() {
        // Given
        Auth auth = new Auth();
        auth.setEmail("test@automo.com");
        auth.setPassword("");
        
        // When
        Set<ConstraintViolation<Auth>> violations = validator.validate(auth);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Should create Auth with different emails")
    void shouldCreateAuthWithDifferentEmails() {
        // Given
        String email1 = TestDataFactory.createUniqueEmail();
        String email2 = TestDataFactory.createUniqueEmail();
        
        Auth auth1 = TestDataFactory.createValidAuth(email1);
        Auth auth2 = TestDataFactory.createValidAuth(email2);
        
        // When
        Set<ConstraintViolation<Auth>> violations1 = validator.validate(auth1);
        Set<ConstraintViolation<Auth>> violations2 = validator.validate(auth2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertNotEquals(auth1.getEmail(), auth2.getEmail());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        Auth auth1 = TestDataFactory.createValidAuth();
        Auth auth2 = TestDataFactory.createValidAuth();
        auth1.setId(1L);
        auth2.setId(1L);
        
        // Then
        assertEquals(auth1, auth2);
        assertEquals(auth1.hashCode(), auth2.hashCode());
        
        // When different IDs
        auth2.setId(2L);
        
        // Then
        assertNotEquals(auth1, auth2);
    }

    @Test
    @DisplayName("Should test toString method")
    void shouldTestToStringMethod() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        auth.setId(1L);
        
        // When
        String toString = auth.toString();
        
        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Auth"));
        assertTrue(toString.contains("test@automo.com"));
    }
}