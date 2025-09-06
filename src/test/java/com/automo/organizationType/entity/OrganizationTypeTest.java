package com.automo.organizationType.entity;

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
@DisplayName("Tests for OrganizationType Entity")
class OrganizationTypeTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid OrganizationType entity")
    void shouldCreateValidOrganizationTypeEntity() {
        // Given
        OrganizationType organizationType = TestDataFactory.createPrivateOrganizationType();
        
        // When
        Set<ConstraintViolation<OrganizationType>> violations = validator.validate(organizationType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("PRIVATE", organizationType.getType());
        assertEquals("Private organization type", organizationType.getDescription());
    }

    @Test
    @DisplayName("Should fail validation with null type")
    void shouldFailValidationWithNullType() {
        // Given
        OrganizationType organizationType = new OrganizationType();
        organizationType.setType(null);
        
        // When
        Set<ConstraintViolation<OrganizationType>> violations = validator.validate(organizationType);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("type") && 
            v.getMessage().contains("Type is required")));
    }

    @Test
    @DisplayName("Should create different organization types")
    void shouldCreateDifferentOrganizationTypes() {
        // Given
        String[] validTypes = {
            "PRIVATE",
            "PUBLIC", 
            "NONPROFIT",
            "GOVERNMENT",
            "PARTNERSHIP",
            "CORPORATION"
        };
        
        // When & Then
        for (String type : validTypes) {
            OrganizationType organizationType = TestDataFactory.createValidOrganizationType(type, type + " organization");
            
            Set<ConstraintViolation<OrganizationType>> violations = validator.validate(organizationType);
            
            assertTrue(violations.isEmpty(), "Organization type " + type + " should be valid");
            assertEquals(type, organizationType.getType());
        }
    }
}