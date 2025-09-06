package com.automo.accountType.entity;

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
@DisplayName("Tests for AccountType Entity")
class AccountTypeTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid AccountType entity")
    void shouldCreateValidAccountTypeEntity() {
        // Given
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        
        // When
        Set<ConstraintViolation<AccountType>> violations = validator.validate(accountType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("INDIVIDUAL", accountType.getType());
        assertEquals("Individual account type", accountType.getDescription());
    }

    @Test
    @DisplayName("Should fail validation with null type")
    void shouldFailValidationWithNullType() {
        // Given
        AccountType accountType = new AccountType();
        accountType.setType(null);
        accountType.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<AccountType>> violations = validator.validate(accountType);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("type") && 
            v.getMessage().contains("Type is required")));
    }

    @Test
    @DisplayName("Should create different account types")
    void shouldCreateDifferentAccountTypes() {
        // Given
        String[] validTypes = {
            "INDIVIDUAL",
            "CORPORATE", 
            "BUSINESS",
            "PREMIUM",
            "STANDARD",
            "TRIAL"
        };
        
        // When & Then
        for (String type : validTypes) {
            AccountType accountType = TestDataFactory.createValidAccountType(type, type + " account type");
            
            Set<ConstraintViolation<AccountType>> violations = validator.validate(accountType);
            
            assertTrue(violations.isEmpty(), "Account type " + type + " should be valid");
            assertEquals(type, accountType.getType());
        }
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        AccountType accountType1 = TestDataFactory.createIndividualAccountType();
        AccountType accountType2 = TestDataFactory.createIndividualAccountType();
        accountType1.setId(1L);
        accountType2.setId(1L);
        
        // Then
        assertEquals(accountType1, accountType2);
        assertEquals(accountType1.hashCode(), accountType2.hashCode());
        
        // When different IDs
        accountType2.setId(2L);
        
        // Then
        assertNotEquals(accountType1, accountType2);
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties")
    void shouldInheritAbstractModelProperties() {
        // Given
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        
        // When
        accountType.setId(1L);
        
        // Then
        assertNotNull(accountType.getId());
        assertEquals(1L, accountType.getId());
    }
}