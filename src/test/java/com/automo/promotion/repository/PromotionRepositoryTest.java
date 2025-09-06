package com.automo.promotion.repository;

import com.automo.promotion.entity.Promotion;
import com.automo.state.entity.State;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for PromotionRepository")
class PromotionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PromotionRepository promotionRepository;

    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private Promotion promotion1;
    private Promotion promotion2;
    private Promotion eliminatedPromotion;

    @BeforeEach
    void setUp() {
        // Create and persist states
        activeState = TestDataFactory.createActiveState();
        inactiveState = TestDataFactory.createInactiveState();
        eliminatedState = TestDataFactory.createEliminatedState();
        
        activeState = entityManager.persistAndFlush(activeState);
        inactiveState = entityManager.persistAndFlush(inactiveState);
        eliminatedState = entityManager.persistAndFlush(eliminatedState);

        // Create and persist promotions
        promotion1 = TestDataFactory.createValidPromotion(activeState);
        promotion1.setName("Summer Sale");
        promotion1.setDiscountValue(new BigDecimal("20.00"));
        promotion1.setCode("SUMMER20");
        promotion1 = entityManager.persistAndFlush(promotion1);

        promotion2 = TestDataFactory.createValidPromotion(inactiveState);
        promotion2.setName("Black Friday");
        promotion2.setDiscountValue(new BigDecimal("50.00"));
        promotion2.setCode("BF50");
        promotion2 = entityManager.persistAndFlush(promotion2);

        eliminatedPromotion = TestDataFactory.createValidPromotion(eliminatedState);
        eliminatedPromotion.setName("Eliminated Promotion");
        eliminatedPromotion.setDiscountValue(new BigDecimal("10.00"));
        eliminatedPromotion.setCode("ELIMINATED10");
        eliminatedPromotion = entityManager.persistAndFlush(eliminatedPromotion);

        entityManager.clear();
    }

    @Test
    @DisplayName("Should find promotions by state ID")
    void shouldFindPromotionsByStateId() {
        // When
        List<Promotion> activePromotions = promotionRepository.findByStateId(activeState.getId());
        List<Promotion> inactivePromotions = promotionRepository.findByStateId(inactiveState.getId());
        List<Promotion> eliminatedPromotions = promotionRepository.findByStateId(eliminatedState.getId());

        // Then
        assertEquals(1, activePromotions.size());
        assertEquals("Summer Sale", activePromotions.get(0).getName());
        assertEquals("SUMMER20", activePromotions.get(0).getCode());

        assertEquals(1, inactivePromotions.size());
        assertEquals("Black Friday", inactivePromotions.get(0).getName());
        assertEquals("BF50", inactivePromotions.get(0).getCode());

        assertEquals(1, eliminatedPromotions.size());
        assertEquals("Eliminated Promotion", eliminatedPromotions.get(0).getName());
        assertEquals("ELIMINATED10", eliminatedPromotions.get(0).getCode());
    }

    @Test
    @DisplayName("Should return empty list when no promotions found for state")
    void shouldReturnEmptyListWhenNoPromotionsFoundForState() {
        // Given
        Long nonExistentStateId = 999L;

        // When
        List<Promotion> promotions = promotionRepository.findByStateId(nonExistentStateId);

        // Then
        assertTrue(promotions.isEmpty());
    }

    @Test
    @DisplayName("Should find promotion by code")
    void shouldFindPromotionByCode() {
        // When
        Optional<Promotion> result = promotionRepository.findByCode("SUMMER20");

        // Then
        assertTrue(result.isPresent());
        Promotion found = result.get();
        assertEquals("Summer Sale", found.getName());
        assertEquals(new BigDecimal("20.00"), found.getDiscountValue());
        assertEquals("SUMMER20", found.getCode());
        assertEquals(activeState.getId(), found.getState().getId());
    }

    @Test
    @DisplayName("Should return empty when promotion code not found")
    void shouldReturnEmptyWhenPromotionCodeNotFound() {
        // When
        Optional<Promotion> result = promotionRepository.findByCode("NONEXISTENT");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should handle case sensitive code search")
    void shouldHandleCaseSensitiveCodeSearch() {
        // When & Then
        Optional<Promotion> exact = promotionRepository.findByCode("SUMMER20");
        Optional<Promotion> lowercase = promotionRepository.findByCode("summer20");
        Optional<Promotion> mixed = promotionRepository.findByCode("Summer20");

        assertTrue(exact.isPresent());
        assertFalse(lowercase.isPresent());
        assertFalse(mixed.isPresent());
    }

    @Test
    @DisplayName("Should save new promotion")
    void shouldSaveNewPromotion() {
        // Given
        Promotion newPromotion = new Promotion();
        newPromotion.setName("Christmas Sale");
        newPromotion.setDiscountValue(new BigDecimal("30.00"));
        newPromotion.setCode("XMAS30");
        newPromotion.setState(activeState);

        // When
        Promotion saved = promotionRepository.save(newPromotion);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Christmas Sale", saved.getName());
        assertEquals(new BigDecimal("30.00"), saved.getDiscountValue());
        assertEquals("XMAS30", saved.getCode());
        assertEquals(activeState.getId(), saved.getState().getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());

        // Verify it can be found
        Optional<Promotion> found = promotionRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Christmas Sale", found.get().getName());
    }

    @Test
    @DisplayName("Should update existing promotion")
    void shouldUpdateExistingPromotion() {
        // Given
        Promotion toUpdate = promotionRepository.findById(promotion1.getId()).orElseThrow();

        // When
        toUpdate.setName("Updated Summer Sale");
        toUpdate.setDiscountValue(new BigDecimal("25.00"));
        toUpdate.setCode("SUMMER25");
        Promotion updated = promotionRepository.save(toUpdate);

        // Then
        assertEquals("Updated Summer Sale", updated.getName());
        assertEquals(new BigDecimal("25.00"), updated.getDiscountValue());
        assertEquals("SUMMER25", updated.getCode());
        
        // Verify the update was persisted
        Promotion reloaded = promotionRepository.findById(promotion1.getId()).orElseThrow();
        assertEquals("Updated Summer Sale", reloaded.getName());
        assertEquals(new BigDecimal("25.00"), reloaded.getDiscountValue());
        assertEquals("SUMMER25", reloaded.getCode());
    }

    @Test
    @DisplayName("Should delete promotion")
    void shouldDeletePromotion() {
        // Given
        Long promotionId = promotion1.getId();
        assertTrue(promotionRepository.existsById(promotionId));

        // When
        promotionRepository.deleteById(promotionId);

        // Then
        assertFalse(promotionRepository.existsById(promotionId));
        Optional<Promotion> deleted = promotionRepository.findById(promotionId);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should enforce unique code constraint")
    void shouldEnforceUniqueCodeConstraint() {
        // Given
        Promotion duplicateCodePromotion = new Promotion();
        duplicateCodePromotion.setName("Duplicate Code Promotion");
        duplicateCodePromotion.setDiscountValue(new BigDecimal("15.00"));
        duplicateCodePromotion.setCode("SUMMER20"); // Same code as promotion1
        duplicateCodePromotion.setState(activeState);

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            promotionRepository.saveAndFlush(duplicateCodePromotion);
        });
    }

    @Test
    @DisplayName("Should find all promotions including different states")
    void shouldFindAllPromotionsIncludingDifferentStates() {
        // When
        List<Promotion> allPromotions = promotionRepository.findAll();

        // Then
        assertEquals(3, allPromotions.size());
        
        // Verify all promotions are present
        assertTrue(allPromotions.stream().anyMatch(p -> p.getName().equals("Summer Sale")));
        assertTrue(allPromotions.stream().anyMatch(p -> p.getName().equals("Black Friday")));
        assertTrue(allPromotions.stream().anyMatch(p -> p.getName().equals("Eliminated Promotion")));
    }

    @Test
    @DisplayName("Should handle null and empty code searches gracefully")
    void shouldHandleNullAndEmptyCodeSearchesGracefully() {
        // When & Then
        Optional<Promotion> nullResult = promotionRepository.findByCode(null);
        Optional<Promotion> emptyResult = promotionRepository.findByCode("");
        Optional<Promotion> spacesResult = promotionRepository.findByCode("   ");
        
        assertFalse(nullResult.isPresent());
        assertFalse(emptyResult.isPresent());
        assertFalse(spacesResult.isPresent());
    }

    @Test
    @DisplayName("Should save promotion with decimal precision")
    void shouldSavePromotionWithDecimalPrecision() {
        // Given
        Promotion precisionPromotion = new Promotion();
        precisionPromotion.setName("Precision Test");
        precisionPromotion.setDiscountValue(new BigDecimal("12.345"));
        precisionPromotion.setCode("PRECISION123");
        precisionPromotion.setState(activeState);

        // When
        Promotion saved = promotionRepository.save(precisionPromotion);

        // Then
        assertEquals(new BigDecimal("12.345"), saved.getDiscountValue());
        
        // Verify persistence
        Promotion reloaded = promotionRepository.findById(saved.getId()).orElseThrow();
        assertEquals(new BigDecimal("12.345"), reloaded.getDiscountValue());
    }

    @Test
    @DisplayName("Should handle very large discount values")
    void shouldHandleVeryLargeDiscountValues() {
        // Given
        Promotion largeValuePromotion = new Promotion();
        largeValuePromotion.setName("Huge Discount");
        largeValuePromotion.setDiscountValue(new BigDecimal("999999999.99"));
        largeValuePromotion.setCode("HUGE999");
        largeValuePromotion.setState(activeState);

        // When
        Promotion saved = promotionRepository.save(largeValuePromotion);

        // Then
        assertEquals(new BigDecimal("999999999.99"), saved.getDiscountValue());
    }

    @Test
    @DisplayName("Should handle very small discount values")
    void shouldHandleVerySmallDiscountValues() {
        // Given
        Promotion smallValuePromotion = new Promotion();
        smallValuePromotion.setName("Tiny Discount");
        smallValuePromotion.setDiscountValue(new BigDecimal("0.01"));
        smallValuePromotion.setCode("TINY01");
        smallValuePromotion.setState(activeState);

        // When
        Promotion saved = promotionRepository.save(smallValuePromotion);

        // Then
        assertEquals(new BigDecimal("0.01"), saved.getDiscountValue());
    }

    @Test
    @DisplayName("Should handle special characters in codes and names")
    void shouldHandleSpecialCharactersInCodesAndNames() {
        // Given
        Promotion specialPromotion = new Promotion();
        specialPromotion.setName("Special Promo & Co. - €100");
        specialPromotion.setDiscountValue(new BigDecimal("15.00"));
        specialPromotion.setCode("SPECIAL@2024#");
        specialPromotion.setState(activeState);

        // When
        Promotion saved = promotionRepository.save(specialPromotion);

        // Then
        assertEquals("Special Promo & Co. - €100", saved.getName());
        assertEquals("SPECIAL@2024#", saved.getCode());
        
        // Verify it can be found by code
        Optional<Promotion> foundByCode = promotionRepository.findByCode("SPECIAL@2024#");
        assertTrue(foundByCode.isPresent());
        assertEquals("Special Promo & Co. - €100", foundByCode.get().getName());
    }

    @Test
    @DisplayName("Should handle long names and codes")
    void shouldHandleLongNamesAndCodes() {
        // Given
        String longName = "A".repeat(255);
        String longCode = "B".repeat(50);
        
        Promotion longPromotion = new Promotion();
        longPromotion.setName(longName);
        longPromotion.setDiscountValue(new BigDecimal("20.00"));
        longPromotion.setCode(longCode);
        longPromotion.setState(activeState);

        // When
        Promotion saved = promotionRepository.save(longPromotion);

        // Then
        assertEquals(longName, saved.getName());
        assertEquals(longCode, saved.getCode());
    }

    @Test
    @DisplayName("Should count promotions correctly")
    void shouldCountPromotionsCorrectly() {
        // When
        long totalCount = promotionRepository.count();

        // Then
        assertEquals(3, totalCount);
    }

    @Test
    @DisplayName("Should handle concurrent modifications")
    void shouldHandleConcurrentModifications() {
        // Given
        Promotion promotion = promotionRepository.findById(promotion1.getId()).orElseThrow();
        
        // When - Simulate concurrent modification by updating the same entity
        promotion.setName("Modified by First Thread");
        Promotion firstUpdate = promotionRepository.save(promotion);
        
        // Reload and modify again
        Promotion reloaded = promotionRepository.findById(promotion1.getId()).orElseThrow();
        reloaded.setName("Modified by Second Thread");
        Promotion secondUpdate = promotionRepository.save(reloaded);

        // Then
        assertEquals("Modified by Second Thread", secondUpdate.getName());
        
        // Verify final state
        Promotion finalState = promotionRepository.findById(promotion1.getId()).orElseThrow();
        assertEquals("Modified by Second Thread", finalState.getName());
    }

    @Test
    @DisplayName("Should allow same code in different states after unique constraint violation")
    void shouldAllowSameCodeInDifferentStatesAfterUniqueConstraintViolation() {
        // Note: This test is included for completeness but the unique constraint on code
        // is global, not per-state. If business logic requires per-state uniqueness,
        // the constraint would need to be changed to a compound unique constraint.
        
        // Given - Try to create promotion with existing code but different state
        Promotion sameCodeDifferentState = new Promotion();
        sameCodeDifferentState.setName("Same Code Different State");
        sameCodeDifferentState.setDiscountValue(new BigDecimal("15.00"));
        sameCodeDifferentState.setCode("SUMMER20"); // Same as promotion1
        sameCodeDifferentState.setState(inactiveState); // Different state

        // When & Then - Should still throw exception because code is globally unique
        assertThrows(DataIntegrityViolationException.class, () -> {
            promotionRepository.saveAndFlush(sameCodeDifferentState);
        });
    }
}