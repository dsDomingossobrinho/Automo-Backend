package com.automo.associatedContact.entity;

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
@DisplayName("Tests for AssociatedContact Entity")
class AssociatedContactTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid AssociatedContact entity")
    void shouldCreateValidAssociatedContactEntity() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        AssociatedContact associatedContact = TestDataFactory.createValidAssociatedContact(identifier, state);
        
        // When
        Set<ConstraintViolation<AssociatedContact>> violations = validator.validate(associatedContact);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(identifier, associatedContact.getIdentifier());
        assertEquals("912345678", associatedContact.getContact());
        assertEquals(state, associatedContact.getState());
    }

    @Test
    @DisplayName("Should fail validation with null identifier")
    void shouldFailValidationWithNullIdentifier() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        AssociatedContact associatedContact = new AssociatedContact();
        associatedContact.setIdentifier(null);
        associatedContact.setContact("912345678");
        associatedContact.setState(state);
        
        // When
        Set<ConstraintViolation<AssociatedContact>> violations = validator.validate(associatedContact);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("identifier")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Identifier is required")));
    }

    @Test
    @DisplayName("Should fail validation with null contact")
    void shouldFailValidationWithNullContact() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        AssociatedContact associatedContact = new AssociatedContact();
        associatedContact.setIdentifier(identifier);
        associatedContact.setContact(null);
        associatedContact.setState(state);
        
        // When
        Set<ConstraintViolation<AssociatedContact>> violations = validator.validate(associatedContact);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contact")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Contact is required")));
    }

    @Test
    @DisplayName("Should fail validation with blank contact")
    void shouldFailValidationWithBlankContact() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        AssociatedContact associatedContact = new AssociatedContact();
        associatedContact.setIdentifier(identifier);
        associatedContact.setContact("");
        associatedContact.setState(state);
        
        // When
        Set<ConstraintViolation<AssociatedContact>> violations = validator.validate(associatedContact);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contact")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Contact is required")));
    }

    @Test
    @DisplayName("Should fail validation with whitespace only contact")
    void shouldFailValidationWithWhitespaceOnlyContact() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        AssociatedContact associatedContact = new AssociatedContact();
        associatedContact.setIdentifier(identifier);
        associatedContact.setContact("   ");
        associatedContact.setState(state);
        
        // When
        Set<ConstraintViolation<AssociatedContact>> violations = validator.validate(associatedContact);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contact")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        
        AssociatedContact associatedContact = new AssociatedContact();
        associatedContact.setIdentifier(identifier);
        associatedContact.setContact("912345678");
        associatedContact.setState(null);
        
        // When
        Set<ConstraintViolation<AssociatedContact>> violations = validator.validate(associatedContact);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("State is required")));
    }

    @Test
    @DisplayName("Should create AssociatedContact with different contact formats")
    void shouldCreateAssociatedContactWithDifferentContactFormats() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        String[] contactFormats = {
            "912345678",           // Portuguese mobile
            "+351912345678",       // Portuguese mobile with country code
            "213456789",           // Portuguese landline
            "+351213456789",       // Portuguese landline with country code
            "912-345-678",         // With dashes
            "(21) 3456-7890",      // With parentheses and dashes
            "912 345 678"          // With spaces
        };
        
        for (String contactFormat : contactFormats) {
            // When
            AssociatedContact associatedContact = TestDataFactory.createValidAssociatedContact(identifier, contactFormat, state);
            Set<ConstraintViolation<AssociatedContact>> violations = validator.validate(associatedContact);
            
            // Then
            assertTrue(violations.isEmpty(), "Contact format should be valid: " + contactFormat);
            assertEquals(contactFormat, associatedContact.getContact());
        }
    }

    @Test
    @DisplayName("Should create AssociatedContact with different states")
    void shouldCreateAssociatedContactWithDifferentStates() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        
        AssociatedContact contact1 = TestDataFactory.createValidAssociatedContact(identifier, activeState);
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(identifier, inactiveState);
        
        // When
        Set<ConstraintViolation<AssociatedContact>> violations1 = validator.validate(contact1);
        Set<ConstraintViolation<AssociatedContact>> violations2 = validator.validate(contact2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals("ACTIVE", contact1.getState().getState());
        assertEquals("INACTIVE", contact2.getState().getState());
    }

    @Test
    @DisplayName("Should create AssociatedContact with different identifier types")
    void shouldCreateAssociatedContactWithDifferentIdentifierTypes() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Identifier nifIdentifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), state);
        Identifier ccIdentifier = TestDataFactory.createValidIdentifier(2L, TestDataFactory.createValidIdentifierType("CC", "Cartão de Cidadão"), state);
        
        AssociatedContact contact1 = TestDataFactory.createValidAssociatedContact(nifIdentifier, state);
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(ccIdentifier, state);
        
        // When
        Set<ConstraintViolation<AssociatedContact>> violations1 = validator.validate(contact1);
        Set<ConstraintViolation<AssociatedContact>> violations2 = validator.validate(contact2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals("NIF", contact1.getIdentifier().getIdentifierType().getType());
        assertEquals("CC", contact2.getIdentifier().getIdentifierType().getType());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        AssociatedContact contact1 = TestDataFactory.createValidAssociatedContact(identifier, state);
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(identifier, state);
        contact1.setId(1L);
        contact2.setId(1L);
        
        // Then
        assertEquals(contact1, contact2);
        assertEquals(contact1.hashCode(), contact2.hashCode());
        
        // When different IDs
        contact2.setId(2L);
        
        // Then
        assertNotEquals(contact1, contact2);
    }

    @Test
    @DisplayName("Should inherit from AbstractModel")
    void shouldInheritFromAbstractModel() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        AssociatedContact associatedContact = TestDataFactory.createValidAssociatedContact(identifier, state);
        
        // When
        associatedContact.setId(1L);
        
        // Then
        assertNotNull(associatedContact.getId());
        assertEquals(1L, associatedContact.getId());
        assertNotNull(associatedContact.getCreatedAt());
        assertNotNull(associatedContact.getUpdatedAt());
    }

    @Test
    @DisplayName("Should test lazy loading relationships")
    void shouldTestLazyLoadingRelationships() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        AssociatedContact associatedContact = TestDataFactory.createValidAssociatedContact(identifier, state);
        
        // Then - Test that relationships are properly configured
        assertNotNull(associatedContact.getIdentifier());
        assertNotNull(associatedContact.getState());
        assertEquals(identifier, associatedContact.getIdentifier());
        assertEquals(state, associatedContact.getState());
    }

    @Test
    @DisplayName("Should validate contact with various characters")
    void shouldValidateContactWithVariousCharacters() {
        // Given
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        String[] validContacts = {
            "912345678",
            "+351912345678",
            "912-345-678",
            "912 345 678",
            "(912) 345-678",
            "912.345.678"
        };
        
        for (String contact : validContacts) {
            // When
            AssociatedContact associatedContact = TestDataFactory.createValidAssociatedContact(identifier, contact, state);
            Set<ConstraintViolation<AssociatedContact>> violations = validator.validate(associatedContact);
            
            // Then
            assertTrue(violations.isEmpty(), "Contact should be valid: " + contact);
            assertEquals(contact, associatedContact.getContact());
        }
    }

    @Test
    @DisplayName("Should handle unique phone numbers correctly")
    void shouldHandleUniquePhoneNumbersCorrectly() {
        // Given
        Identifier identifier1 = TestDataFactory.createValidIdentifier(1L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        Identifier identifier2 = TestDataFactory.createValidIdentifier(2L, TestDataFactory.createNifIdentifierType(), TestDataFactory.createActiveState());
        State state = TestDataFactory.createActiveState();
        
        String phone1 = TestDataFactory.createUniquePhone();
        String phone2 = TestDataFactory.createUniquePhone();
        
        AssociatedContact contact1 = TestDataFactory.createValidAssociatedContact(identifier1, phone1, state);
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(identifier2, phone2, state);
        
        // When
        Set<ConstraintViolation<AssociatedContact>> violations1 = validator.validate(contact1);
        Set<ConstraintViolation<AssociatedContact>> violations2 = validator.validate(contact2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertNotEquals(contact1.getContact(), contact2.getContact());
    }
}