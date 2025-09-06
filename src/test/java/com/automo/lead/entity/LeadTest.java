package com.automo.lead.entity;

import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.lead.entity.Lead;
import com.automo.leadType.entity.LeadType;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for Lead Entity")
class LeadTest {

    @Autowired
    private Validator validator;

    private State activeState;
    private IdentifierType identifierType;
    private Identifier identifier;
    private LeadType leadType;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        identifierType = TestDataFactory.createNifIdentifierType();
        identifier = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        leadType = TestDataFactory.createCallLeadType();
    }

    @Test
    @DisplayName("Should create valid Lead entity")
    void shouldCreateValidLeadEntity() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("João Silva");
        lead.setEmail("joao.silva@example.com");
        lead.setContact("912345678");
        lead.setZone("Lisboa");
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(identifier, lead.getIdentifier());
        assertEquals("João Silva", lead.getName());
        assertEquals("joao.silva@example.com", lead.getEmail());
        assertEquals("912345678", lead.getContact());
        assertEquals("Lisboa", lead.getZone());
        assertEquals(leadType, lead.getLeadType());
        assertEquals(activeState, lead.getState());
    }

    @Test
    @DisplayName("Should fail validation with null identifier")
    void shouldFailValidationWithNullIdentifier() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(null);
        lead.setName("João Silva");
        lead.setEmail("joao.silva@example.com");
        lead.setContact("912345678");
        lead.setZone("Lisboa");
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("identifier") &&
            v.getMessage().equals("Identifier is required")));
    }

    @Test
    @DisplayName("Should fail validation with null name")
    void shouldFailValidationWithNullName() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName(null);
        lead.setEmail("joao.silva@example.com");
        lead.setContact("912345678");
        lead.setZone("Lisboa");
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("name") &&
            v.getMessage().equals("Name is required")));
    }

    @Test
    @DisplayName("Should fail validation with blank name")
    void shouldFailValidationWithBlankName() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("   ");
        lead.setEmail("joao.silva@example.com");
        lead.setContact("912345678");
        lead.setZone("Lisboa");
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("name") &&
            v.getMessage().equals("Name is required")));
    }

    @Test
    @DisplayName("Should fail validation with null email")
    void shouldFailValidationWithNullEmail() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("João Silva");
        lead.setEmail(null);
        lead.setContact("912345678");
        lead.setZone("Lisboa");
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("email") &&
            v.getMessage().equals("Email is required")));
    }

    @Test
    @DisplayName("Should fail validation with blank email")
    void shouldFailValidationWithBlankEmail() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("João Silva");
        lead.setEmail("   ");
        lead.setContact("912345678");
        lead.setZone("Lisboa");
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("email") &&
            v.getMessage().equals("Email is required")));
    }

    @Test
    @DisplayName("Should fail validation with invalid email format")
    void shouldFailValidationWithInvalidEmailFormat() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("João Silva");
        lead.setEmail("invalid-email");
        lead.setContact("912345678");
        lead.setZone("Lisboa");
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("email") &&
            v.getMessage().equals("Invalid email format")));
    }

    @Test
    @DisplayName("Should fail validation with null leadType")
    void shouldFailValidationWithNullLeadType() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("João Silva");
        lead.setEmail("joao.silva@example.com");
        lead.setContact("912345678");
        lead.setZone("Lisboa");
        lead.setLeadType(null);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("leadType") &&
            v.getMessage().equals("Lead type is required")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("João Silva");
        lead.setEmail("joao.silva@example.com");
        lead.setContact("912345678");
        lead.setZone("Lisboa");
        lead.setLeadType(leadType);
        lead.setState(null);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("state") &&
            v.getMessage().equals("State is required")));
    }

    @Test
    @DisplayName("Should create Lead with null contact")
    void shouldCreateLeadWithNullContact() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("João Silva");
        lead.setEmail("joao.silva@example.com");
        lead.setContact(null);
        lead.setZone("Lisboa");
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNull(lead.getContact());
    }

    @Test
    @DisplayName("Should create Lead with null zone")
    void shouldCreateLeadWithNullZone() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("João Silva");
        lead.setEmail("joao.silva@example.com");
        lead.setContact("912345678");
        lead.setZone(null);
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNull(lead.getZone());
    }

    @Test
    @DisplayName("Should accept valid email formats")
    void shouldAcceptValidEmailFormats() {
        String[] validEmails = {
            "user@example.com",
            "test.email@domain.co.uk",
            "firstname.lastname@company.org",
            "user123@test-domain.com",
            "valid_email@sub.domain.com"
        };
        
        for (String email : validEmails) {
            // Given
            Lead lead = new Lead();
            lead.setIdentifier(identifier);
            lead.setName("Test User");
            lead.setEmail(email);
            lead.setContact("912345678");
            lead.setZone("Test Zone");
            lead.setLeadType(leadType);
            lead.setState(activeState);
            
            // When
            Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
            
            // Then
            assertTrue(violations.isEmpty(), "Email " + email + " should be valid");
        }
    }

    @Test
    @DisplayName("Should reject invalid email formats")
    void shouldRejectInvalidEmailFormats() {
        String[] invalidEmails = {
            "plainaddress",
            "@missingdomain.com",
            "missing@.com",
            "missing.domain@.com",
            "spaces in@email.com",
            "double@@domain.com",
            ""
        };
        
        for (String email : invalidEmails) {
            // Given
            Lead lead = new Lead();
            lead.setIdentifier(identifier);
            lead.setName("Test User");
            lead.setEmail(email);
            lead.setContact("912345678");
            lead.setZone("Test Zone");
            lead.setLeadType(leadType);
            lead.setState(activeState);
            
            // When
            Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
            
            // Then
            assertFalse(violations.isEmpty(), "Email " + email + " should be invalid");
        }
    }

    @Test
    @DisplayName("Should handle long text values within limits")
    void shouldHandleLongTextValuesWithinLimits() {
        // Given
        String longName = "A".repeat(255); // Assuming standard VARCHAR limit
        String longEmail = "user@" + "a".repeat(240) + ".com"; // Still valid email under 255 chars
        String longContact = "9".repeat(50);
        String longZone = "Z".repeat(100);
        
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName(longName);
        lead.setEmail(longEmail);
        lead.setContact(longContact);
        lead.setZone(longZone);
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longName, lead.getName());
        assertEquals(longEmail, lead.getEmail());
        assertEquals(longContact, lead.getContact());
        assertEquals(longZone, lead.getZone());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        Lead lead1 = new Lead();
        lead1.setId(1L);
        lead1.setIdentifier(identifier);
        lead1.setName("João Silva");
        lead1.setEmail("joao.silva@example.com");
        lead1.setLeadType(leadType);
        lead1.setState(activeState);
        
        Lead lead2 = new Lead();
        lead2.setId(1L);
        lead2.setIdentifier(identifier);
        lead2.setName("João Silva");
        lead2.setEmail("joao.silva@example.com");
        lead2.setLeadType(leadType);
        lead2.setState(activeState);
        
        // Then
        assertEquals(lead1, lead2);
        assertEquals(lead1.hashCode(), lead2.hashCode());
        
        // When different IDs
        lead2.setId(2L);
        
        // Then
        assertNotEquals(lead1, lead2);
    }

    @Test
    @DisplayName("Should handle special characters in name")
    void shouldHandleSpecialCharactersInName() {
        // Given
        String[] specialNames = {
            "José da Silva",
            "María González",
            "François Müller",
            "João D'Almeida",
            "Anne-Marie Dubois",
            "O'Connor",
            "São Paulo"
        };
        
        for (String name : specialNames) {
            Lead lead = new Lead();
            lead.setIdentifier(identifier);
            lead.setName(name);
            lead.setEmail("test@example.com");
            lead.setContact("912345678");
            lead.setZone("Test Zone");
            lead.setLeadType(leadType);
            lead.setState(activeState);
            
            // When
            Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
            
            // Then
            assertTrue(violations.isEmpty(), "Name " + name + " should be valid");
            assertEquals(name, lead.getName());
        }
    }

    @Test
    @DisplayName("Should handle edge case contact formats")
    void shouldHandleEdgeCaseContactFormats() {
        // Given
        String[] validContacts = {
            "912345678",
            "+351912345678", 
            "(912) 345-678",
            "912 345 678",
            "+44 20 7123 4567",
            null // contact is optional
        };
        
        for (String contact : validContacts) {
            Lead lead = new Lead();
            lead.setIdentifier(identifier);
            lead.setName("Test User");
            lead.setEmail("test@example.com");
            lead.setContact(contact);
            lead.setZone("Test Zone");
            lead.setLeadType(leadType);
            lead.setState(activeState);
            
            // When
            Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
            
            // Then
            assertTrue(violations.isEmpty(), "Contact " + contact + " should be valid");
            assertEquals(contact, lead.getContact());
        }
    }

    @Test
    @DisplayName("Should preserve case sensitivity in text fields")
    void shouldPreserveCaseSensitivityInTextFields() {
        // Given
        Lead lead = new Lead();
        lead.setIdentifier(identifier);
        lead.setName("João SILVA");
        lead.setEmail("Joao.SILVA@Example.COM");
        lead.setContact("912345678");
        lead.setZone("LISBOA norte");
        lead.setLeadType(leadType);
        lead.setState(activeState);
        
        // When
        Set<ConstraintViolation<Lead>> violations = validator.validate(lead);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("João SILVA", lead.getName());
        assertEquals("Joao.SILVA@Example.COM", lead.getEmail());
        assertEquals("LISBOA norte", lead.getZone());
    }
}