package com.automo.subscription.entity;

import com.automo.promotion.entity.Promotion;
import com.automo.state.entity.State;
import com.automo.subscriptionPlan.entity.SubscriptionPlan;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import com.automo.user.entity.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for Subscription Entity")
class SubscriptionTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Subscription entity")
    void shouldCreateValidSubscriptionEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = TestDataFactory.createValidSubscription(user, plan, state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(user, subscription.getUser());
        assertEquals(plan, subscription.getPlan());
        assertEquals(plan.getPrice(), subscription.getPrice());
        assertNotNull(subscription.getStartDate());
        assertNotNull(subscription.getEndDate());
        assertEquals(1000, subscription.getMessageCount());
        assertEquals(state, subscription.getState());
    }

    @Test
    @DisplayName("Should fail validation with null user")
    void shouldFailValidationWithNullUser() {
        // Given
        State state = TestDataFactory.createActiveState();
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = new Subscription();
        subscription.setUser(null);
        subscription.setPlan(plan);
        subscription.setPrice(new BigDecimal("29.99"));
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(1000);
        subscription.setState(state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("user")));
    }

    @Test
    @DisplayName("Should fail validation with null plan")
    void shouldFailValidationWithNullPlan() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(null);
        subscription.setPrice(new BigDecimal("29.99"));
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(1000);
        subscription.setState(state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("plan")));
    }

    @Test
    @DisplayName("Should fail validation with null price")
    void shouldFailValidationWithNullPrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(null);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(1000);
        subscription.setState(state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation with negative price")
    void shouldFailValidationWithNegativePrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(new BigDecimal("-10.00"));
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(1000);
        subscription.setState(state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation with zero price")
    void shouldFailValidationWithZeroPrice() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(BigDecimal.ZERO);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(1000);
        subscription.setState(state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("price")));
    }

    @Test
    @DisplayName("Should fail validation with null start date")
    void shouldFailValidationWithNullStartDate() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(new BigDecimal("29.99"));
        subscription.setStartDate(null);
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(1000);
        subscription.setState(state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("startDate")));
    }

    @Test
    @DisplayName("Should fail validation with null end date")
    void shouldFailValidationWithNullEndDate() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(new BigDecimal("29.99"));
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(null);
        subscription.setMessageCount(1000);
        subscription.setState(state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("endDate")));
    }

    @Test
    @DisplayName("Should fail validation with null message count")
    void shouldFailValidationWithNullMessageCount() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(new BigDecimal("29.99"));
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(null);
        subscription.setState(state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("messageCount")));
    }

    @Test
    @DisplayName("Should fail validation with negative message count")
    void shouldFailValidationWithNegativeMessageCount() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(new BigDecimal("29.99"));
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(-10);
        subscription.setState(state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("messageCount")));
    }

    @Test
    @DisplayName("Should fail validation with zero message count")
    void shouldFailValidationWithZeroMessageCount() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(new BigDecimal("29.99"));
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(0);
        subscription.setState(state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("messageCount")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setPlan(plan);
        subscription.setPrice(new BigDecimal("29.99"));
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1));
        subscription.setMessageCount(1000);
        subscription.setState(null);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should create subscription with optional promotion")
    void shouldCreateSubscriptionWithOptionalPromotion() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        // Create a mock promotion
        Promotion promotion = new Promotion();
        promotion.setId(1L);
        promotion.setName("Test Promotion");
        
        Subscription subscription = TestDataFactory.createValidSubscription(user, plan, state);
        subscription.setPromotion(promotion);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(promotion, subscription.getPromotion());
    }

    @Test
    @DisplayName("Should create subscription with null promotion")
    void shouldCreateSubscriptionWithNullPromotion() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription = TestDataFactory.createValidSubscription(user, plan, state);
        subscription.setPromotion(null);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNull(subscription.getPromotion());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription1 = TestDataFactory.createValidSubscription(user, plan, state);
        Subscription subscription2 = TestDataFactory.createValidSubscription(user, plan, state);
        subscription1.setId(1L);
        subscription2.setId(1L);
        
        // Then
        assertEquals(subscription1, subscription2);
        assertEquals(subscription1.hashCode(), subscription2.hashCode());
        
        // When different IDs
        subscription2.setId(2L);
        
        // Then
        assertNotEquals(subscription1, subscription2);
    }

    @Test
    @DisplayName("Should create subscription with different date ranges")
    void shouldCreateSubscriptionWithDifferentDateRanges() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        LocalDate startDate = LocalDate.now().minusDays(10);
        LocalDate endDate = LocalDate.now().plusDays(20);
        
        Subscription subscription = TestDataFactory.createValidSubscription(user, plan, startDate, endDate, state);
        
        // When
        Set<ConstraintViolation<Subscription>> violations = validator.validate(subscription);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(startDate, subscription.getStartDate());
        assertEquals(endDate, subscription.getEndDate());
    }

    @Test
    @DisplayName("Should create subscription with different message counts")
    void shouldCreateSubscriptionWithDifferentMessageCounts() {
        // Given
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                state
        );
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(state);
        
        Subscription subscription1 = TestDataFactory.createValidSubscription(user, plan, state);
        subscription1.setMessageCount(500);
        
        Subscription subscription2 = TestDataFactory.createValidSubscription(user, plan, state);
        subscription2.setMessageCount(5000);
        
        // When
        Set<ConstraintViolation<Subscription>> violations1 = validator.validate(subscription1);
        Set<ConstraintViolation<Subscription>> violations2 = validator.validate(subscription2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals(500, subscription1.getMessageCount());
        assertEquals(5000, subscription2.getMessageCount());
    }
}