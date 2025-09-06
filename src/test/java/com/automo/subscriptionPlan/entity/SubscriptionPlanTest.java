package com.automo.subscriptionPlan.entity;

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
@DisplayName("Tests for SubscriptionPlan Entity")
class SubscriptionPlanTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid SubscriptionPlan entity")
    void shouldCreateValidSubscriptionPlanEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Basic Plan", plan.getName());
        assertEquals(new BigDecimal("29.99"), plan.getPrice());
        assertEquals("Basic subscription plan", plan.getDescription());
        assertEquals(state, plan.getState());
    }

    @Test
    @DisplayName("Should fail validation with null name")
    void shouldFailValidationWithNullName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(null);
        plan.setPrice(new BigDecimal("29.99"));
        plan.setDescription("Test description");
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with blank name")
    void shouldFailValidationWithBlankName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setDescription("Test description");
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with whitespace-only name")
    void shouldFailValidationWithWhitespaceOnlyName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("   ");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setDescription("Test description");
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with null price")
    void shouldFailValidationWithNullPrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Test Plan");
        plan.setPrice(null);
        plan.setDescription("Test description");
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation with negative price")
    void shouldFailValidationWithNegativePrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Test Plan");
        plan.setPrice(new BigDecimal("-10.00"));
        plan.setDescription("Test description");
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation with zero price")
    void shouldFailValidationWithZeroPrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Test Plan");
        plan.setPrice(BigDecimal.ZERO);
        plan.setDescription("Test description");
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Test Plan");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setDescription("Test description");
        plan.setState(null);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should create valid SubscriptionPlan with null description")
    void shouldCreateValidSubscriptionPlanWithNullDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Test Plan");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setDescription(null);
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNull(plan.getDescription());
    }

    @Test
    @DisplayName("Should create valid SubscriptionPlan with empty description")
    void shouldCreateValidSubscriptionPlanWithEmptyDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Test Plan");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setDescription("");
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("", plan.getDescription());
    }

    @Test
    @DisplayName("Should create SubscriptionPlan with different price values")
    void shouldCreateSubscriptionPlanWithDifferentPriceValues() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan1 = new SubscriptionPlan();
        plan1.setName("Basic Plan");
        plan1.setPrice(new BigDecimal("0.01")); // Minimum positive value
        plan1.setDescription("Minimum price plan");
        plan1.setState(state);
        
        SubscriptionPlan plan2 = new SubscriptionPlan();
        plan2.setName("Premium Plan");
        plan2.setPrice(new BigDecimal("999.99"));
        plan2.setDescription("High price plan");
        plan2.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations1 = validator.validate(plan1);
        Set<ConstraintViolation<SubscriptionPlan>> violations2 = validator.validate(plan2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals(new BigDecimal("0.01"), plan1.getPrice());
        assertEquals(new BigDecimal("999.99"), plan2.getPrice());
    }

    @Test
    @DisplayName("Should create SubscriptionPlan with different states")
    void shouldCreateSubscriptionPlanWithDifferentStates() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        
        SubscriptionPlan activePlan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        SubscriptionPlan inactivePlan = TestDataFactory.createBasicSubscriptionPlan(inactiveState);
        inactivePlan.setName("Inactive Plan");
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violationsActive = validator.validate(activePlan);
        Set<ConstraintViolation<SubscriptionPlan>> violationsInactive = validator.validate(inactivePlan);
        
        // Then
        assertTrue(violationsActive.isEmpty());
        assertTrue(violationsInactive.isEmpty());
        assertEquals(activeState, activePlan.getState());
        assertEquals(inactiveState, inactivePlan.getState());
    }

    @Test
    @DisplayName("Should create SubscriptionPlan with long description")
    void shouldCreateSubscriptionPlanWithLongDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        String longDescription = "This is a very long description that contains multiple sentences. " +
                "It describes the subscription plan in great detail, including all the features, " +
                "benefits, limitations, and terms and conditions that apply to this specific plan. " +
                "The description can be quite extensive and should be stored properly in the database.";
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Detailed Plan");
        plan.setPrice(new BigDecimal("49.99"));
        plan.setDescription(longDescription);
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longDescription, plan.getDescription());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan1 = TestDataFactory.createBasicSubscriptionPlan(state);
        SubscriptionPlan plan2 = TestDataFactory.createBasicSubscriptionPlan(state);
        plan1.setId(1L);
        plan2.setId(1L);
        
        // Then
        assertEquals(plan1, plan2);
        assertEquals(plan1.hashCode(), plan2.hashCode());
        
        // When different IDs
        plan2.setId(2L);
        
        // Then
        assertNotEquals(plan1, plan2);
    }

    @Test
    @DisplayName("Should create multiple distinct SubscriptionPlans")
    void shouldCreateMultipleDistinctSubscriptionPlans() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan basicPlan = TestDataFactory.createBasicSubscriptionPlan(state);
        SubscriptionPlan premiumPlan = TestDataFactory.createPremiumSubscriptionPlan(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violationsBasic = validator.validate(basicPlan);
        Set<ConstraintViolation<SubscriptionPlan>> violationsPremium = validator.validate(premiumPlan);
        
        // Then
        assertTrue(violationsBasic.isEmpty());
        assertTrue(violationsPremium.isEmpty());
        
        assertNotEquals(basicPlan.getName(), premiumPlan.getName());
        assertNotEquals(basicPlan.getPrice(), premiumPlan.getPrice());
        assertEquals(basicPlan.getState(), premiumPlan.getState());
    }

    @Test
    @DisplayName("Should validate price precision and scale")
    void shouldValidatePricePrecisionAndScale() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Precision Test Plan");
        plan.setPrice(new BigDecimal("123.456789"));
        plan.setDescription("Testing decimal precision");
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("123.456789"), plan.getPrice());
    }

    @Test
    @DisplayName("Should handle special characters in name and description")
    void shouldHandleSpecialCharactersInNameAndDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Plan with Special Chars: @#$%^&*()_+-={}[]|\\:;\"'<>?,./");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setDescription("Description with special chars: àáâãäåæçèéêëìíîïñòóôõöø");
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertTrue(violations.isEmpty());
        assertTrue(plan.getName().contains("@#$%^&*"));
        assertTrue(plan.getDescription().contains("àáâãäåæçèéêë"));
    }

    @Test
    @DisplayName("Should handle Unicode characters in name and description")
    void shouldHandleUnicodeCharactersInNameAndDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Plano Básico 中文 العربية русский");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setDescription("Descrição em português 中文描述 وصف عربي русское описание");
        plan.setState(state);
        
        // When
        Set<ConstraintViolation<SubscriptionPlan>> violations = validator.validate(plan);
        
        // Then
        assertTrue(violations.isEmpty());
        assertTrue(plan.getName().contains("中文"));
        assertTrue(plan.getDescription().contains("العربية"));
    }
}