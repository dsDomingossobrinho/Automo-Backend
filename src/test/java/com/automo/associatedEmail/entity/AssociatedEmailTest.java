package com.automo.associatedEmail.entity;

import com.automo.identifier.entity.Identifier;
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
@DisplayName("Tests for AssociatedEmail Entity")
class AssociatedEmailTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid AssociatedEmail entity")
    void shouldCreateValidAssociatedEmailEntity() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        AssociatedEmail associatedEmail = TestDataFactory.createValidAssociatedEmail(identifier, state);
        
        // When
        Set<ConstraintViolation<AssociatedEmail>> violations = validator.validate(associatedEmail);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(identifier, associatedEmail.getIdentifier());
        assertEquals("test@automo.com", associatedEmail.getEmail());
        assertEquals(state, associatedEmail.getState());
    }

    @Test
    @DisplayName("Should fail validation with null identifier")
    void shouldFailValidationWithNullIdentifier() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        AssociatedEmail associatedEmail = new AssociatedEmail();
        associatedEmail.setIdentifier(null);
        associatedEmail.setEmail("test@automo.com");
        associatedEmail.setState(state);
        
        // When
        Set<ConstraintViolation<AssociatedEmail>> violations = validator.validate(associatedEmail);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("identifier")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Identifier is required")));
    }

    @Test
    @DisplayName("Should fail validation with null email")
    void shouldFailValidationWithNullEmail() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        AssociatedEmail associatedEmail = new AssociatedEmail();
        associatedEmail.setIdentifier(identifier);
        associatedEmail.setEmail(null);
        associatedEmail.setState(state);
        
        // When
        Set<ConstraintViolation<AssociatedEmail>> violations = validator.validate(associatedEmail);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email is required")));
    }

    @Test
    @DisplayName("Should fail validation with blank email")
    void shouldFailValidationWithBlankEmail() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        AssociatedEmail associatedEmail = new AssociatedEmail();
        associatedEmail.setIdentifier(identifier);
        associatedEmail.setEmail("");
        associatedEmail.setState(state);
        
        // When
        Set<ConstraintViolation<AssociatedEmail>> violations = validator.validate(associatedEmail);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Email is required")));
    }

    @Test
    @DisplayName("Should fail validation with whitespace only email")
    void shouldFailValidationWithWhitespaceOnlyEmail() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        AssociatedEmail associatedEmail = new AssociatedEmail();
        associatedEmail.setIdentifier(identifier);
        associatedEmail.setEmail("   ");
        associatedEmail.setState(state);
        
        // When
        Set<ConstraintViolation<AssociatedEmail>> violations = validator.validate(associatedEmail);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Should fail validation with invalid email format")
    void shouldFailValidationWithInvalidEmailFormat() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        String[] invalidEmails = {
            "invalid-email",           // Missing @ and domain
            "@domain.com",            // Missing local part
            "user@",                  // Missing domain
            "user@domain",            // Missing TLD
            "user..name@domain.com",  // Double dots
            "user@.domain.com",       // Domain starts with dot
            "user@domain..com",       // Double dots in domain
            ".user@domain.com",       // Local part starts with dot
            "user.@domain.com"        // Local part ends with dot
        };
        
        for (String invalidEmail : invalidEmails) {
            // When
            AssociatedEmail associatedEmail = new AssociatedEmail();
            associatedEmail.setIdentifier(identifier);
            associatedEmail.setEmail(invalidEmail);
            associatedEmail.setState(state);
            
            Set<ConstraintViolation<AssociatedEmail>> violations = validator.validate(associatedEmail);
            
            // Then
            assertFalse(violations.isEmpty(), "Email should be invalid: " + invalidEmail);
            assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")), "Email validation should fail for: " + invalidEmail);
            assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Invalid email format")), "Should have proper error message for: " + invalidEmail);
        }
    }

    @Test
    @DisplayName("Should pass validation with valid email formats")
    void shouldPassValidationWithValidEmailFormats() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        String[] validEmails = {
            "user@domain.com",
            "test@automo.com",
            "user.name@domain.co.uk",
            "user+tag@domain.com",
            "user_name@domain-name.com",
            "123@domain.com",
            "user@domain123.com",
            "user@sub.domain.com",
            "a@b.co",
            "very.long.email.address@very.long.domain.name.com"
        };
        
        for (String validEmail : validEmails) {
            // When
            AssociatedEmail associatedEmail = TestDataFactory.createValidAssociatedEmail(identifier, validEmail, state);
            Set<ConstraintViolation<AssociatedEmail>> violations = validator.validate(associatedEmail);
            
            // Then
            assertTrue(violations.isEmpty(), "Email should be valid: " + validEmail);
            assertEquals(validEmail, associatedEmail.getEmail());
        }
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        
        AssociatedEmail associatedEmail = new AssociatedEmail();
        associatedEmail.setIdentifier(identifier);
        associatedEmail.setEmail("test@automo.com");
        associatedEmail.setState(null);
        
        // When
        Set<ConstraintViolation<AssociatedEmail>> violations = validator.validate(associatedEmail);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("State is required")));
    }

    @Test
    @DisplayName("Should create AssociatedEmail with different states")
    void shouldCreateAssociatedEmailWithDifferentStates() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        
        AssociatedEmail email1 = TestDataFactory.createValidAssociatedEmail(identifier, activeState);
        AssociatedEmail email2 = TestDataFactory.createValidAssociatedEmail(identifier, inactiveState);
        
        // When
        Set<ConstraintViolation<AssociatedEmail>> violations1 = validator.validate(email1);
        Set<ConstraintViolation<AssociatedEmail>> violations2 = validator.validate(email2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals("ACTIVE", email1.getState().getState());
        assertEquals("INACTIVE", email2.getState().getState());
    }

    @Test
    @DisplayName("Should create AssociatedEmail with different identifier types")
    void shouldCreateAssociatedEmailWithDifferentIdentifierTypes() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Identifier nifIdentifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), state);
        Identifier ccIdentifier = TestDataFactory.createValidIdentifier(2L, TestDataFactory.createValidIdentifierType("CC", "Cartão de Cidadão"), state);
        
        AssociatedEmail email1 = TestDataFactory.createValidAssociatedEmail(nifIdentifier, state);
        AssociatedEmail email2 = TestDataFactory.createValidAssociatedEmail(ccIdentifier, state);
        
        // When
        Set<ConstraintViolation<AssociatedEmail>> violations1 = validator.validate(email1);
        Set<ConstraintViolation<AssociatedEmail>> violations2 = validator.validate(email2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals("NIF", email1.getIdentifier().getIdentifierType().getType());
        assertEquals("CC", email2.getIdentifier().getIdentifierType().getType());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        AssociatedEmail email1 = TestDataFactory.createValidAssociatedEmail(identifier, state);
        AssociatedEmail email2 = TestDataFactory.createValidAssociatedEmail(identifier, state);
        email1.setId(1L);
        email2.setId(1L);
        
        // Then
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
        
        // When different IDs
        email2.setId(2L);
        
        // Then
        assertNotEquals(email1, email2);
    }

    @Test
    @DisplayName("Should inherit from AbstractModel")
    void shouldInheritFromAbstractModel() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        AssociatedEmail associatedEmail = TestDataFactory.createValidAssociatedEmail(identifier, state);
        
        // When
        associatedEmail.setId(1L);
        
        // Then
        assertNotNull(associatedEmail.getId());
        assertEquals(1L, associatedEmail.getId());
        assertNotNull(associatedEmail.getCreatedAt());
        assertNotNull(associatedEmail.getUpdatedAt());
    }

    @Test
    @DisplayName("Should test lazy loading relationships")
    void shouldTestLazyLoadingRelationships() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        AssociatedEmail associatedEmail = TestDataFactory.createValidAssociatedEmail(identifier, state);
        
        // Then - Test that relationships are properly configured
        assertNotNull(associatedEmail.getIdentifier());
        assertNotNull(associatedEmail.getState());
        assertEquals(identifier, associatedEmail.getIdentifier());
        assertEquals(state, associatedEmail.getState());
    }

    @Test
    @DisplayName("Should handle unique email addresses correctly")
    void shouldHandleUniqueEmailAddressesCorrectly() {
        // Given
        Identifier identifier1 = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        Identifier identifier2 = TestDataFactory.createValidIdentifier(2L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        String email1 = TestDataFactory.createUniqueEmail();
        String email2 = TestDataFactory.createUniqueEmail();
        
        AssociatedEmail associatedEmail1 = TestDataFactory.createValidAssociatedEmail(identifier1, email1, state);
        AssociatedEmail associatedEmail2 = TestDataFactory.createValidAssociatedEmail(identifier2, email2, state);
        
        // When
        Set<ConstraintViolation<AssociatedEmail>> violations1 = validator.validate(associatedEmail1);
        Set<ConstraintViolation<AssociatedEmail>> violations2 = validator.validate(associatedEmail2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertNotEquals(associatedEmail1.getEmail(), associatedEmail2.getEmail());
    }

    @Test
    @DisplayName("Should validate email case sensitivity")
    void shouldValidateEmailCaseSensitivity() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        String[] emailVariations = {
            "test@domain.com",
            "TEST@domain.com",
            "Test@Domain.com",
            "test@DOMAIN.COM"
        };
        
        for (String email : emailVariations) {
            // When
            AssociatedEmail associatedEmail = TestDataFactory.createValidAssociatedEmail(identifier, email, state);
            Set<ConstraintViolation<AssociatedEmail>> violations = validator.validate(associatedEmail);
            
            // Then
            assertTrue(violations.isEmpty(), "Email should be valid: " + email);
            assertEquals(email, associatedEmail.getEmail());
        }
    }

    @Test
    @DisplayName("Should validate international email formats")
    void shouldValidateInternationalEmailFormats() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        String[] internationalEmails = {
            "user@domain.pt",          // Portugal
            "user@domain.es",          // Spain
            "user@domain.br",          // Brazil
            "user@domain.fr",          // France
            "user@domain.de",          // Germany
            "user@domain.co.uk",       // UK
            "user@domain.com.br",      // Brazil
            "user@domain.org",         // Organization
            "user@domain.edu",         // Education
            "user@domain.gov"          // Government
        };
        
        for (String email : internationalEmails) {
            // When
            AssociatedEmail associatedEmail = TestDataFactory.createValidAssociatedEmail(identifier, email, state);
            Set<ConstraintViolation<AssociatedEmail>> violations = validator.validate(associatedEmail);
            
            // Then
            assertTrue(violations.isEmpty(), "International email should be valid: " + email);
            assertEquals(email, associatedEmail.getEmail());
        }
    }
}