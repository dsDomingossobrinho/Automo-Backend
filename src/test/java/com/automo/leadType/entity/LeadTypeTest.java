package com.automo.leadType.entity;

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
@DisplayName("Tests for LeadType Entity")
class LeadTypeTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid LeadType entity")
    void shouldCreateValidLeadTypeEntity() {
        // Given
        LeadType leadType = TestDataFactory.createCallLeadType();
        
        // When
        Set<ConstraintViolation<LeadType>> violations = validator.validate(leadType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("CALL", leadType.getType());
        assertEquals("Call-based lead type", leadType.getDescription());
    }

    @Test
    @DisplayName("Should fail validation with null type")
    void shouldFailValidationWithNullType() {
        // Given
        LeadType leadType = new LeadType();
        leadType.setType(null);
        
        // When
        Set<ConstraintViolation<LeadType>> violations = validator.validate(leadType);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("type") && 
            v.getMessage().contains("Type is required")));
    }

    @Test
    @DisplayName("Should create different lead types")
    void shouldCreateDifferentLeadTypes() {
        // Given
        String[] validTypes = {
            "CALL",
            "EMAIL", 
            "WEBSITE",
            "REFERRAL",
            "SOCIAL_MEDIA",
            "ADVERTISEMENT"
        };
        
        // When & Then
        for (String type : validTypes) {
            LeadType leadType = TestDataFactory.createValidLeadType(type, type + "-based lead");
            
            Set<ConstraintViolation<LeadType>> violations = validator.validate(leadType);
            
            assertTrue(violations.isEmpty(), "Lead type " + type + " should be valid");
            assertEquals(type, leadType.getType());
        }
    }
}