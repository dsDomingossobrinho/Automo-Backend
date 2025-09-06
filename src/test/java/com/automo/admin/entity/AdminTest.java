package com.automo.admin.entity;

import com.automo.auth.entity.Auth;
import com.automo.state.entity.State;
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
@DisplayName("Tests for Admin Entity")
class AdminTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Admin entity")
    void shouldCreateValidAdminEntity() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        auth.setId(1L);
        State state = TestDataFactory.createActiveState();
        state.setId(1L);
        Admin admin = TestDataFactory.createValidAdmin(auth, state);
        admin.setEmail("admin@automo.com");
        
        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Test Admin", admin.getName());
        assertEquals("admin@automo.com", admin.getEmail());
        assertEquals(auth, admin.getAuth());
        assertEquals(state, admin.getState());
    }

    @Test
    @DisplayName("Should fail validation with null email")
    void shouldFailValidationWithNullEmail() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        Admin admin = new Admin();
        admin.setEmail(null);
        admin.setName("Test Admin");
        admin.setAuth(auth);
        admin.setState(state);
        
        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail validation with blank email")
    void shouldFailValidationWithBlankEmail() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        Admin admin = new Admin();
        admin.setEmail("");
        admin.setName("Test Admin");
        admin.setAuth(auth);
        admin.setState(state);
        
        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail validation with invalid email format")
    void shouldFailValidationWithInvalidEmailFormat() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        Admin admin = new Admin();
        admin.setEmail("invalid-email");
        admin.setName("Test Admin");
        admin.setAuth(auth);
        admin.setState(state);
        
        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail validation with null name")
    void shouldFailValidationWithNullName() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        Admin admin = new Admin();
        admin.setEmail("admin@automo.com");
        admin.setName(null);
        admin.setAuth(auth);
        admin.setState(state);
        
        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with blank name")
    void shouldFailValidationWithBlankName() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        Admin admin = new Admin();
        admin.setEmail("admin@automo.com");
        admin.setName("");
        admin.setAuth(auth);
        admin.setState(state);
        
        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with null auth")
    void shouldFailValidationWithNullAuth() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Admin admin = new Admin();
        admin.setEmail("admin@automo.com");
        admin.setName("Test Admin");
        admin.setAuth(null);
        admin.setState(state);
        
        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("auth")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        
        Admin admin = new Admin();
        admin.setEmail("admin@automo.com");
        admin.setName("Test Admin");
        admin.setAuth(auth);
        admin.setState(null);
        
        // When
        Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should create Admin with optional img field")
    void shouldCreateAdminWithOptionalImgField() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        Admin admin1 = TestDataFactory.createValidAdmin(auth, state);
        admin1.setEmail("admin1@automo.com");
        admin1.setImg(null); // Null img should be allowed
        
        Admin admin2 = TestDataFactory.createValidAdmin(auth, state);
        admin2.setEmail("admin2@automo.com");
        admin2.setImg("profile.jpg"); // Set img should be allowed
        
        // When
        Set<ConstraintViolation<Admin>> violations1 = validator.validate(admin1);
        Set<ConstraintViolation<Admin>> violations2 = validator.validate(admin2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertNull(admin1.getImg());
        assertEquals("profile.jpg", admin2.getImg());
    }

    @Test
    @DisplayName("Should create Admin with unique emails")
    void shouldCreateAdminWithUniqueEmails() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        String email1 = TestDataFactory.createUniqueEmail();
        String email2 = TestDataFactory.createUniqueEmail();
        
        Admin admin1 = TestDataFactory.createValidAdmin(auth, state);
        Admin admin2 = TestDataFactory.createValidAdmin(auth, state);
        admin1.setEmail(email1);
        admin2.setEmail(email2);
        
        // When
        Set<ConstraintViolation<Admin>> violations1 = validator.validate(admin1);
        Set<ConstraintViolation<Admin>> violations2 = validator.validate(admin2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertNotEquals(admin1.getEmail(), admin2.getEmail());
    }

    @Test
    @DisplayName("Should accept valid email formats")
    void shouldAcceptValidEmailFormats() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        String[] validEmails = {
            "admin@automo.com",
            "admin.test@automo.com",
            "admin_test@automo.com",
            "admin123@automo.com",
            "admin@sub.automo.com"
        };
        
        // When & Then
        for (String email : validEmails) {
            Admin admin = TestDataFactory.createValidAdmin(auth, state);
            admin.setEmail(email);
            
            Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
            assertTrue(violations.isEmpty(), "Email " + email + " should be valid");
        }
    }

    @Test
    @DisplayName("Should reject invalid email formats")
    void shouldRejectInvalidEmailFormats() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        String[] invalidEmails = {
            "admin",
            "@automo.com",
            "admin@",
            "admin.automo.com",
            "admin@@automo.com",
            "admin@automo",
            " admin@automo.com ",
            ""
        };
        
        // When & Then
        for (String email : invalidEmails) {
            Admin admin = TestDataFactory.createValidAdmin(auth, state);
            admin.setEmail(email);
            
            Set<ConstraintViolation<Admin>> violations = validator.validate(admin);
            assertFalse(violations.isEmpty(), "Email " + email + " should be invalid");
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        }
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        Admin admin1 = TestDataFactory.createValidAdmin(auth, state);
        Admin admin2 = TestDataFactory.createValidAdmin(auth, state);
        admin1.setId(1L);
        admin2.setId(1L);
        admin1.setEmail("admin@automo.com");
        admin2.setEmail("admin@automo.com");
        
        // Then
        assertEquals(admin1, admin2);
        assertEquals(admin1.hashCode(), admin2.hashCode());
        
        // When different IDs
        admin2.setId(2L);
        
        // Then
        assertNotEquals(admin1, admin2);
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties")
    void shouldInheritAbstractModelProperties() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        Admin admin = TestDataFactory.createValidAdmin(auth, state);
        admin.setEmail("admin@automo.com");
        
        // When
        admin.setId(1L);
        
        // Then
        assertNotNull(admin.getId());
        assertEquals(1L, admin.getId());
        // Note: createdAt and updatedAt are set by JPA auditing in real scenarios
    }
}