package com.automo.deal.entity;

import com.automo.deal.entity.Deal;
import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.lead.entity.Lead;
import com.automo.leadType.entity.LeadType;
import com.automo.promotion.entity.Promotion;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for Deal Entity")
class DealTest {

    @Autowired
    private Validator validator;

    private State activeState;
    private IdentifierType identifierType;
    private Identifier identifier;
    private LeadType leadType;
    private Lead lead;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        identifierType = TestDataFactory.createNifIdentifierType();
        identifier = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        leadType = TestDataFactory.createCallLeadType();
        lead = TestDataFactory.createValidLead(identifier, leadType, activeState);
    }

    @Test
    @DisplayName("Should create valid Deal entity")
    void shouldCreateValidDealEntity() {
        // Given
        Deal deal = TestDataFactory.createValidDeal(identifier, lead, activeState);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(identifier, deal.getIdentifier());
        assertEquals(lead, deal.getLead());
        assertEquals(new BigDecimal("45000.00"), deal.getTotal());
        assertEquals(3, deal.getMessageCount());
        assertEquals(activeState, deal.getState());
    }

    @Test
    @DisplayName("Should fail validation with null identifier")
    void shouldFailValidationWithNullIdentifier() {
        // Given
        Deal deal = new Deal();
        deal.setIdentifier(null);
        deal.setLead(lead);
        deal.setTotal(new BigDecimal("1000.00"));
        deal.setMessageCount(1);
        deal.setState(activeState);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("identifier")));
    }

    @Test
    @DisplayName("Should fail validation with null lead")
    void shouldFailValidationWithNullLead() {
        // Given
        Deal deal = new Deal();
        deal.setIdentifier(identifier);
        deal.setLead(null);
        deal.setTotal(new BigDecimal("1000.00"));
        deal.setMessageCount(1);
        deal.setState(activeState);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("lead")));
    }

    @Test
    @DisplayName("Should fail validation with null total")
    void shouldFailValidationWithNullTotal() {
        // Given
        Deal deal = new Deal();
        deal.setIdentifier(identifier);
        deal.setLead(lead);
        deal.setTotal(null);
        deal.setMessageCount(1);
        deal.setState(activeState);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("total")));
    }

    @Test
    @DisplayName("Should fail validation with negative total")
    void shouldFailValidationWithNegativeTotal() {
        // Given
        Deal deal = new Deal();
        deal.setIdentifier(identifier);
        deal.setLead(lead);
        deal.setTotal(new BigDecimal("-1000.00"));
        deal.setMessageCount(1);
        deal.setState(activeState);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("total")));
    }

    @Test
    @DisplayName("Should fail validation with zero total")
    void shouldFailValidationWithZeroTotal() {
        // Given
        Deal deal = new Deal();
        deal.setIdentifier(identifier);
        deal.setLead(lead);
        deal.setTotal(BigDecimal.ZERO);
        deal.setMessageCount(1);
        deal.setState(activeState);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("total")));
    }

    @Test
    @DisplayName("Should fail validation with null message count")
    void shouldFailValidationWithNullMessageCount() {
        // Given
        Deal deal = new Deal();
        deal.setIdentifier(identifier);
        deal.setLead(lead);
        deal.setTotal(new BigDecimal("1000.00"));
        deal.setMessageCount(null);
        deal.setState(activeState);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("messageCount")));
    }

    @Test
    @DisplayName("Should fail validation with negative message count")
    void shouldFailValidationWithNegativeMessageCount() {
        // Given
        Deal deal = new Deal();
        deal.setIdentifier(identifier);
        deal.setLead(lead);
        deal.setTotal(new BigDecimal("1000.00"));
        deal.setMessageCount(-1);
        deal.setState(activeState);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("messageCount")));
    }

    @Test
    @DisplayName("Should fail validation with zero message count")
    void shouldFailValidationWithZeroMessageCount() {
        // Given
        Deal deal = new Deal();
        deal.setIdentifier(identifier);
        deal.setLead(lead);
        deal.setTotal(new BigDecimal("1000.00"));
        deal.setMessageCount(0);
        deal.setState(activeState);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("messageCount")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Deal deal = new Deal();
        deal.setIdentifier(identifier);
        deal.setLead(lead);
        deal.setTotal(new BigDecimal("1000.00"));
        deal.setMessageCount(1);
        deal.setState(null);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should create deal with optional promotion")
    void shouldCreateDealWithOptionalPromotion() {
        // Given
        Promotion promotion = new Promotion();
        promotion.setName("Test Promotion");
        promotion.setDiscount(new BigDecimal("10.00"));
        
        Deal deal = TestDataFactory.createValidDeal(identifier, lead, activeState);
        deal.setPromotion(promotion);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(promotion, deal.getPromotion());
    }

    @Test
    @DisplayName("Should create deal with optional delivery date")
    void shouldCreateDealWithOptionalDeliveryDate() {
        // Given
        LocalDate deliveryDate = LocalDate.now().plusDays(30);
        
        Deal deal = TestDataFactory.createValidDeal(identifier, lead, activeState);
        deal.setDeliveryDate(deliveryDate);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(deliveryDate, deal.getDeliveryDate());
    }

    @Test
    @DisplayName("Should handle very large total amounts")
    void shouldHandleVeryLargeTotalAmounts() {
        // Given
        Deal deal = TestDataFactory.createValidDeal(identifier, lead, activeState);
        deal.setTotal(new BigDecimal("999999999.99"));
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("999999999.99"), deal.getTotal());
    }

    @Test
    @DisplayName("Should handle very small positive total amounts")
    void shouldHandleVerySmallPositiveTotalAmounts() {
        // Given
        Deal deal = TestDataFactory.createValidDeal(identifier, lead, activeState);
        deal.setTotal(new BigDecimal("0.01"));
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("0.01"), deal.getTotal());
    }

    @Test
    @DisplayName("Should handle high message counts")
    void shouldHandleHighMessageCounts() {
        // Given
        Deal deal = TestDataFactory.createValidDeal(identifier, lead, activeState);
        deal.setMessageCount(999999);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(999999, deal.getMessageCount());
    }

    @Test
    @DisplayName("Should handle past delivery dates")
    void shouldHandlePastDeliveryDates() {
        // Given
        LocalDate pastDate = LocalDate.now().minusDays(30);
        
        Deal deal = TestDataFactory.createValidDeal(identifier, lead, activeState);
        deal.setDeliveryDate(pastDate);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(pastDate, deal.getDeliveryDate());
    }

    @Test
    @DisplayName("Should handle future delivery dates")
    void shouldHandleFutureDeliveryDates() {
        // Given
        LocalDate futureDate = LocalDate.now().plusYears(1);
        
        Deal deal = TestDataFactory.createValidDeal(identifier, lead, activeState);
        deal.setDeliveryDate(futureDate);
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(futureDate, deal.getDeliveryDate());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        Deal deal1 = TestDataFactory.createValidDeal(identifier, lead, activeState);
        Deal deal2 = TestDataFactory.createValidDeal(identifier, lead, activeState);
        deal1.setId(1L);
        deal2.setId(1L);
        
        // Then
        assertEquals(deal1, deal2);
        assertEquals(deal1.hashCode(), deal2.hashCode());
        
        // When different IDs
        deal2.setId(2L);
        
        // Then
        assertNotEquals(deal1, deal2);
    }

    @Test
    @DisplayName("Should handle fractional total amounts")
    void shouldHandleFractionalTotalAmounts() {
        // Given
        Deal deal = TestDataFactory.createValidDeal(identifier, lead, activeState);
        deal.setTotal(new BigDecimal("1234.567"));
        
        // When
        Set<ConstraintViolation<Deal>> violations = validator.validate(deal);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(new BigDecimal("1234.567"), deal.getTotal());
    }
}