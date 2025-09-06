package com.automo.payment.entity;

import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.paymentType.entity.PaymentType;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for Payment Entity")
class PaymentTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Payment entity")
    void shouldCreateValidPaymentEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        PaymentType paymentType = TestDataFactory.createBankTransferPaymentType();
        
        Payment payment = TestDataFactory.createValidPayment(identifier, paymentType, state);
        
        // When
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(identifier, payment.getIdentifier());
        assertEquals(paymentType, payment.getPaymentType());
        assertEquals(state, payment.getState());
        assertEquals(new BigDecimal("100.00"), payment.getAmount());
        assertNotNull(payment.getImageFilename());
        assertEquals("receipt.jpg", payment.getOriginalFilename());
    }

    @Test
    @DisplayName("Should fail validation with null identifier")
    void shouldFailValidationWithNullIdentifier() {
        // Given
        State state = TestDataFactory.createActiveState();
        PaymentType paymentType = TestDataFactory.createBankTransferPaymentType();
        
        Payment payment = new Payment();
        payment.setIdentifier(null);
        payment.setPaymentType(paymentType);
        payment.setState(state);
        payment.setAmount(new BigDecimal("100.00"));
        
        // When
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("identifier")));
    }

    @Test
    @DisplayName("Should fail validation with null payment type")
    void shouldFailValidationWithNullPaymentType() {
        // Given
        State state = TestDataFactory.createActiveState();
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        
        Payment payment = new Payment();
        payment.setIdentifier(identifier);
        payment.setPaymentType(null);
        payment.setState(state);
        payment.setAmount(new BigDecimal("100.00"));
        
        // When
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("paymentType")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        State state = TestDataFactory.createActiveState();
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        PaymentType paymentType = TestDataFactory.createBankTransferPaymentType();
        
        Payment payment = new Payment();
        payment.setIdentifier(identifier);
        payment.setPaymentType(paymentType);
        payment.setState(null);
        payment.setAmount(new BigDecimal("100.00"));
        
        // When
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should accept payment with null amount")
    void shouldAcceptPaymentWithNullAmount() {
        // Given
        State state = TestDataFactory.createActiveState();
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        PaymentType paymentType = TestDataFactory.createBankTransferPaymentType();
        
        Payment payment = TestDataFactory.createValidPayment(identifier, paymentType, state);
        payment.setAmount(null);
        
        // When
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNull(payment.getAmount());
    }

    @Test
    @DisplayName("Should accept payment with zero amount")
    void shouldAcceptPaymentWithZeroAmount() {
        // Given
        State state = TestDataFactory.createActiveState();
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        PaymentType paymentType = TestDataFactory.createBankTransferPaymentType();
        
        Payment payment = TestDataFactory.createValidPayment(identifier, paymentType, state);
        payment.setAmount(BigDecimal.ZERO);
        
        // When
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(BigDecimal.ZERO, payment.getAmount());
    }

    @Test
    @DisplayName("Should accept payment with large amount")
    void shouldAcceptPaymentWithLargeAmount() {
        // Given
        State state = TestDataFactory.createActiveState();
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        PaymentType paymentType = TestDataFactory.createBankTransferPaymentType();
        
        Payment payment = TestDataFactory.createValidPayment(identifier, paymentType, state);
        payment.setAmount(new BigDecimal("999999.99"));
        
        // When
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("999999.99"), payment.getAmount());
    }

    @Test
    @DisplayName("Should create payment with different file names")
    void shouldCreatePaymentWithDifferentFileNames() {
        // Given
        State state = TestDataFactory.createActiveState();
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        PaymentType paymentType = TestDataFactory.createBankTransferPaymentType();
        
        Payment payment1 = TestDataFactory.createValidPayment(identifier, paymentType, state);
        Payment payment2 = TestDataFactory.createValidPayment(identifier, paymentType, state);
        
        // When
        Set<ConstraintViolation<Payment>> violations1 = validator.validate(payment1);
        Set<ConstraintViolation<Payment>> violations2 = validator.validate(payment2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertNotEquals(payment1.getImageFilename(), payment2.getImageFilename());
        assertEquals("receipt.jpg", payment1.getOriginalFilename());
        assertEquals("receipt.jpg", payment2.getOriginalFilename());
    }

    @Test
    @DisplayName("Should create payment with random amounts")
    void shouldCreatePaymentWithRandomAmounts() {
        // Given
        State state = TestDataFactory.createActiveState();
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        PaymentType paymentType = TestDataFactory.createBankTransferPaymentType();
        
        Payment payment1 = TestDataFactory.createValidPayment(identifier, paymentType, state);
        Payment payment2 = TestDataFactory.createValidPayment(identifier, paymentType, state);
        
        payment1.setAmount(TestDataFactory.createRandomPrice());
        payment2.setAmount(TestDataFactory.createRandomPrice());
        
        // When
        Set<ConstraintViolation<Payment>> violations1 = validator.validate(payment1);
        Set<ConstraintViolation<Payment>> violations2 = validator.validate(payment2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertNotNull(payment1.getAmount());
        assertNotNull(payment2.getAmount());
        assertTrue(payment1.getAmount().compareTo(BigDecimal.ZERO) >= 0);
        assertTrue(payment2.getAmount().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state = TestDataFactory.createActiveState();
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        PaymentType paymentType = TestDataFactory.createBankTransferPaymentType();
        
        Payment payment1 = TestDataFactory.createValidPayment(identifier, paymentType, state);
        Payment payment2 = TestDataFactory.createValidPayment(identifier, paymentType, state);
        payment1.setId(1L);
        payment2.setId(1L);
        
        // Then
        assertEquals(payment1, payment2);
        assertEquals(payment1.hashCode(), payment2.hashCode());
        
        // When different IDs
        payment2.setId(2L);
        
        // Then
        assertNotEquals(payment1, payment2);
    }
}