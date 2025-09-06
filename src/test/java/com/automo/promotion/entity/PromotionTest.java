package com.automo.promotion.entity;

import com.automo.promotion.entity.Promotion;
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
@DisplayName("Tests for Promotion Entity")
class PromotionTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Promotion entity")
    void shouldCreateValidPromotionEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        Promotion promotion = TestDataFactory.createValidPromotion(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Summer Sale", promotion.getName());
        assertEquals(new BigDecimal("20.00"), promotion.getDiscountValue());
        assertEquals("SUMMER20", promotion.getCode());
        assertEquals(state, promotion.getState());
    }

    @Test
    @DisplayName("Should fail validation with null name")
    void shouldFailValidationWithNullName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName(null);
        promotion.setDiscountValue(new BigDecimal("10.00"));
        promotion.setCode("TEST10");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Nome é obrigatório")));
    }

    @Test
    @DisplayName("Should fail validation with blank name")
    void shouldFailValidationWithBlankName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("");
        promotion.setDiscountValue(new BigDecimal("10.00"));
        promotion.setCode("TEST10");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Nome é obrigatório")));
    }

    @Test
    @DisplayName("Should fail validation with whitespace only name")
    void shouldFailValidationWithWhitespaceOnlyName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("   ");
        promotion.setDiscountValue(new BigDecimal("10.00"));
        promotion.setCode("TEST10");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with null discount value")
    void shouldFailValidationWithNullDiscountValue() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Test Promotion");
        promotion.setDiscountValue(null);
        promotion.setCode("TEST10");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("discountValue")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Valor do desconto é obrigatório")));
    }

    @Test
    @DisplayName("Should fail validation with negative discount value")
    void shouldFailValidationWithNegativeDiscountValue() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Test Promotion");
        promotion.setDiscountValue(new BigDecimal("-10.00"));
        promotion.setCode("TEST10");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("discountValue")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Valor do desconto deve ser positivo")));
    }

    @Test
    @DisplayName("Should fail validation with zero discount value")
    void shouldFailValidationWithZeroDiscountValue() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Test Promotion");
        promotion.setDiscountValue(BigDecimal.ZERO);
        promotion.setCode("TEST10");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("discountValue")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Valor do desconto deve ser positivo")));
    }

    @Test
    @DisplayName("Should fail validation with null code")
    void shouldFailValidationWithNullCode() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Test Promotion");
        promotion.setDiscountValue(new BigDecimal("10.00"));
        promotion.setCode(null);
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Código é obrigatório")));
    }

    @Test
    @DisplayName("Should fail validation with blank code")
    void shouldFailValidationWithBlankCode() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Test Promotion");
        promotion.setDiscountValue(new BigDecimal("10.00"));
        promotion.setCode("");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("code")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("Código é obrigatório")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Promotion promotion = new Promotion();
        promotion.setName("Test Promotion");
        promotion.setDiscountValue(new BigDecimal("10.00"));
        promotion.setCode("TEST10");
        promotion.setState(null);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().equals("State is required")));
    }

    @Test
    @DisplayName("Should create promotion with minimal valid discount value")
    void shouldCreatePromotionWithMinimalValidDiscountValue() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Minimal Discount");
        promotion.setDiscountValue(new BigDecimal("0.01"));
        promotion.setCode("MIN01");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("0.01"), promotion.getDiscountValue());
    }

    @Test
    @DisplayName("Should create promotion with very large discount value")
    void shouldCreatePromotionWithVeryLargeDiscountValue() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Huge Discount");
        promotion.setDiscountValue(new BigDecimal("999999999.99"));
        promotion.setCode("HUGE99");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("999999999.99"), promotion.getDiscountValue());
    }

    @Test
    @DisplayName("Should handle different states")
    void shouldHandleDifferentStates() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        State eliminatedState = TestDataFactory.createEliminatedState();
        
        Promotion activePromotion = TestDataFactory.createValidPromotion(activeState);
        Promotion inactivePromotion = TestDataFactory.createValidPromotion(inactiveState);
        inactivePromotion.setName("Inactive Promotion");
        inactivePromotion.setCode("INACTIVE");
        Promotion eliminatedPromotion = TestDataFactory.createValidPromotion(eliminatedState);
        eliminatedPromotion.setName("Eliminated Promotion");
        eliminatedPromotion.setCode("ELIMINATED");
        
        // When
        Set<ConstraintViolation<Promotion>> activeViolations = validator.validate(activePromotion);
        Set<ConstraintViolation<Promotion>> inactiveViolations = validator.validate(inactivePromotion);
        Set<ConstraintViolation<Promotion>> eliminatedViolations = validator.validate(eliminatedPromotion);
        
        // Then
        assertTrue(activeViolations.isEmpty());
        assertTrue(inactiveViolations.isEmpty());
        assertTrue(eliminatedViolations.isEmpty());
        assertEquals("ACTIVE", activePromotion.getState().getState());
        assertEquals("INACTIVE", inactivePromotion.getState().getState());
        assertEquals("ELIMINATED", eliminatedPromotion.getState().getState());
    }

    @Test
    @DisplayName("Should handle long promotion names and codes")
    void shouldHandleLongPromotionNamesAndCodes() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        String longName = "A".repeat(255);
        String longCode = "B".repeat(50);
        
        Promotion promotion = new Promotion();
        promotion.setName(longName);
        promotion.setDiscountValue(new BigDecimal("15.00"));
        promotion.setCode(longCode);
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longName, promotion.getName());
        assertEquals(longCode, promotion.getCode());
    }

    @Test
    @DisplayName("Should handle special characters in name and code")
    void shouldHandleSpecialCharactersInNameAndCode() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Promotion & Co. - Special €100");
        promotion.setDiscountValue(new BigDecimal("25.50"));
        promotion.setCode("SPECIAL@2024#");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Promotion & Co. - Special €100", promotion.getName());
        assertEquals("SPECIAL@2024#", promotion.getCode());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion1 = TestDataFactory.createValidPromotion(state);
        Promotion promotion2 = TestDataFactory.createValidPromotion(state);
        promotion1.setId(1L);
        promotion2.setId(1L);
        
        // Then
        assertEquals(promotion1, promotion2);
        assertEquals(promotion1.hashCode(), promotion2.hashCode());
        
        // When different IDs
        promotion2.setId(2L);
        
        // Then
        assertNotEquals(promotion1, promotion2);
    }

    @Test
    @DisplayName("Should create promotion with all valid variations")
    void shouldCreatePromotionWithAllValidVariations() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion1 = new Promotion("Christmas Sale", new BigDecimal("30.00"), "XMAS30", state);
        Promotion promotion2 = new Promotion("Black Friday", new BigDecimal("50.00"), "BF50", state);
        Promotion promotion3 = new Promotion("New Year", new BigDecimal("15.00"), "NY15", state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations1 = validator.validate(promotion1);
        Set<ConstraintViolation<Promotion>> violations2 = validator.validate(promotion2);
        Set<ConstraintViolation<Promotion>> violations3 = validator.validate(promotion3);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertTrue(violations3.isEmpty());
        
        assertEquals("Christmas Sale", promotion1.getName());
        assertEquals(new BigDecimal("30.00"), promotion1.getDiscountValue());
        assertEquals("XMAS30", promotion1.getCode());
        
        assertEquals("Black Friday", promotion2.getName());
        assertEquals(new BigDecimal("50.00"), promotion2.getDiscountValue());
        assertEquals("BF50", promotion2.getCode());
    }

    @Test
    @DisplayName("Should handle numeric and mixed characters in name and code")
    void shouldHandleNumericAndMixedCharactersInNameAndCode() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Promotion 123 - Version 2.0");
        promotion.setDiscountValue(new BigDecimal("12.34"));
        promotion.setCode("PROMO123V2");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Promotion 123 - Version 2.0", promotion.getName());
        assertEquals("PROMO123V2", promotion.getCode());
    }

    @Test
    @DisplayName("Should maintain AbstractModel inheritance properties")
    void shouldMaintainAbstractModelInheritanceProperties() {
        // Given
        State state = TestDataFactory.createActiveState();
        Promotion promotion = TestDataFactory.createValidPromotion(state);
        promotion.setId(99L);
        
        // When & Then
        assertEquals(99L, promotion.getId());
        assertNull(promotion.getCreatedAt()); // Will be set by JPA
        assertNull(promotion.getUpdatedAt()); // Will be set by JPA
        assertEquals(state, promotion.getState());
    }

    @Test
    @DisplayName("Should handle decimal precision in discount value")
    void shouldHandleDecimalPrecisionInDiscountValue() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Precision Test");
        promotion.setDiscountValue(new BigDecimal("12.345"));
        promotion.setCode("PRECISION");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("12.345"), promotion.getDiscountValue());
    }

    @Test
    @DisplayName("Should validate unique constraint on code field")
    void shouldValidateUniqueConstraintOnCodeField() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Promotion promotion = new Promotion();
        promotion.setName("Unique Code Test");
        promotion.setDiscountValue(new BigDecimal("10.00"));
        promotion.setCode("UNIQUE123");
        promotion.setState(state);
        
        // When
        Set<ConstraintViolation<Promotion>> violations = validator.validate(promotion);
        
        // Then
        assertTrue(violations.isEmpty());
        // Note: Unique constraint validation happens at database level, not bean validation
    }
}