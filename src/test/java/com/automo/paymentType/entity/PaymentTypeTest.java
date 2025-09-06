package com.automo.paymentType.entity;

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
@DisplayName("Tests for PaymentType Entity")
class PaymentTypeTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid PaymentType entity")
    void shouldCreateValidPaymentTypeEntity() {
        // Given
        PaymentType paymentType = TestDataFactory.createBankTransferPaymentType();
        
        // When
        Set<ConstraintViolation<PaymentType>> violations = validator.validate(paymentType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("BANK_TRANSFER", paymentType.getType());
        assertEquals("Bank transfer payment method", paymentType.getDescription());
    }

    @Test
    @DisplayName("Should fail validation with null type")
    void shouldFailValidationWithNullType() {
        // Given
        PaymentType paymentType = new PaymentType();
        paymentType.setType(null);
        
        // When
        Set<ConstraintViolation<PaymentType>> violations = validator.validate(paymentType);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("type") && 
            v.getMessage().contains("Type is required")));
    }

    @Test
    @DisplayName("Should create different payment types")
    void shouldCreateDifferentPaymentTypes() {
        // Given
        String[] validTypes = {
            "BANK_TRANSFER",
            "CREDIT_CARD", 
            "DEBIT_CARD",
            "PAYPAL",
            "CRYPTO",
            "CASH"
        };
        
        // When & Then
        for (String type : validTypes) {
            PaymentType paymentType = TestDataFactory.createValidPaymentType(type, type + " payment method");
            
            Set<ConstraintViolation<PaymentType>> violations = validator.validate(paymentType);
            
            assertTrue(violations.isEmpty(), "Payment type " + type + " should be valid");
            assertEquals(type, paymentType.getType());
        }
    }
}