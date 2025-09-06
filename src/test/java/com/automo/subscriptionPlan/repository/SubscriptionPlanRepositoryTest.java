package com.automo.subscriptionPlan.repository;

import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import com.automo.subscriptionPlan.entity.SubscriptionPlan;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for SubscriptionPlanRepository")
class SubscriptionPlanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private StateRepository stateRepository;

    private State activeState;
    private State inactiveState;
    private State eliminatedState;

    @BeforeEach
    void setUp() {
        // Create and persist states
        activeState = TestDataFactory.createActiveState();
        inactiveState = TestDataFactory.createInactiveState();
        eliminatedState = TestDataFactory.createEliminatedState();
        
        activeState = stateRepository.save(activeState);
        inactiveState = stateRepository.save(inactiveState);
        eliminatedState = stateRepository.save(eliminatedState);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should save and retrieve subscription plan")
    void shouldSaveAndRetrieveSubscriptionPlan() {
        // Given
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        plan.setName("Test Plan");
        plan.setPrice(new BigDecimal("39.99"));
        plan.setDescription("Test subscription plan");
        
        // When
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        Optional<SubscriptionPlan> retrievedPlan = subscriptionPlanRepository.findById(savedPlan.getId());
        
        // Then
        assertTrue(retrievedPlan.isPresent());
        assertEquals(savedPlan.getId(), retrievedPlan.get().getId());
        assertEquals("Test Plan", retrievedPlan.get().getName());
        assertEquals(0, new BigDecimal("39.99").compareTo(retrievedPlan.get().getPrice()));
        assertEquals("Test subscription plan", retrievedPlan.get().getDescription());
        assertEquals(activeState.getId(), retrievedPlan.get().getState().getId());
    }

    @Test
    @DisplayName("Should find subscription plans by state ID")
    void shouldFindSubscriptionPlansByStateId() {
        // Given
        SubscriptionPlan activePlan1 = TestDataFactory.createBasicSubscriptionPlan(activeState);
        activePlan1.setName("Active Plan 1");
        
        SubscriptionPlan activePlan2 = TestDataFactory.createBasicSubscriptionPlan(activeState);
        activePlan2.setName("Active Plan 2");
        
        SubscriptionPlan inactivePlan = TestDataFactory.createBasicSubscriptionPlan(inactiveState);
        inactivePlan.setName("Inactive Plan");
        
        subscriptionPlanRepository.save(activePlan1);
        subscriptionPlanRepository.save(activePlan2);
        subscriptionPlanRepository.save(inactivePlan);
        
        // When
        List<SubscriptionPlan> activePlans = subscriptionPlanRepository.findByStateId(activeState.getId());
        List<SubscriptionPlan> inactivePlans = subscriptionPlanRepository.findByStateId(inactiveState.getId());
        
        // Then
        assertEquals(2, activePlans.size());
        assertEquals(1, inactivePlans.size());
        
        assertTrue(activePlans.stream().allMatch(p -> p.getState().getId().equals(activeState.getId())));
        assertTrue(inactivePlans.stream().allMatch(p -> p.getState().getId().equals(inactiveState.getId())));
        
        assertTrue(activePlans.stream().anyMatch(p -> "Active Plan 1".equals(p.getName())));
        assertTrue(activePlans.stream().anyMatch(p -> "Active Plan 2".equals(p.getName())));
        assertTrue(inactivePlans.stream().anyMatch(p -> "Inactive Plan".equals(p.getName())));
    }

    @Test
    @DisplayName("Should return empty list for non-existent state ID")
    void shouldReturnEmptyListForNonExistentStateId() {
        // Given
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        subscriptionPlanRepository.save(plan);
        
        // When
        List<SubscriptionPlan> plansForNonExistentState = subscriptionPlanRepository.findByStateId(999L);
        
        // Then
        assertTrue(plansForNonExistentState.isEmpty());
    }

    @Test
    @DisplayName("Should save subscription plan with minimal required fields")
    void shouldSaveSubscriptionPlanWithMinimalRequiredFields() {
        // Given
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Minimal Plan");
        plan.setPrice(new BigDecimal("0.01")); // Minimum positive value
        plan.setDescription(null); // Optional field
        plan.setState(activeState);
        
        // When
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        Optional<SubscriptionPlan> retrievedPlan = subscriptionPlanRepository.findById(savedPlan.getId());
        
        // Then
        assertTrue(retrievedPlan.isPresent());
        assertEquals("Minimal Plan", retrievedPlan.get().getName());
        assertEquals(0, new BigDecimal("0.01").compareTo(retrievedPlan.get().getPrice()));
        assertNull(retrievedPlan.get().getDescription());
        assertEquals(activeState.getId(), retrievedPlan.get().getState().getId());
    }

    @Test
    @DisplayName("Should save subscription plan with empty description")
    void shouldSaveSubscriptionPlanWithEmptyDescription() {
        // Given
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Plan with Empty Description");
        plan.setPrice(new BigDecimal("29.99"));
        plan.setDescription(""); // Empty description
        plan.setState(activeState);
        
        // When
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        Optional<SubscriptionPlan> retrievedPlan = subscriptionPlanRepository.findById(savedPlan.getId());
        
        // Then
        assertTrue(retrievedPlan.isPresent());
        assertEquals("", retrievedPlan.get().getDescription());
    }

    @Test
    @DisplayName("Should save subscription plan with long description")
    void shouldSaveSubscriptionPlanWithLongDescription() {
        // Given
        String longDescription = "This is a very long description that contains multiple sentences and detailed information about the subscription plan. ".repeat(10);
        
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Plan with Long Description");
        plan.setPrice(new BigDecimal("59.99"));
        plan.setDescription(longDescription);
        plan.setState(activeState);
        
        // When
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        Optional<SubscriptionPlan> retrievedPlan = subscriptionPlanRepository.findById(savedPlan.getId());
        
        // Then
        assertTrue(retrievedPlan.isPresent());
        assertEquals(longDescription, retrievedPlan.get().getDescription());
        assertTrue(retrievedPlan.get().getDescription().length() > 500);
    }

    @Test
    @DisplayName("Should handle multiple plans with same name but different states")
    void shouldHandleMultiplePlansWithSameNameButDifferentStates() {
        // Given
        SubscriptionPlan activePlan = new SubscriptionPlan();
        activePlan.setName("Basic Plan");
        activePlan.setPrice(new BigDecimal("29.99"));
        activePlan.setDescription("Active basic plan");
        activePlan.setState(activeState);
        
        SubscriptionPlan inactivePlan = new SubscriptionPlan();
        inactivePlan.setName("Basic Plan"); // Same name
        inactivePlan.setPrice(new BigDecimal("19.99")); // Different price
        inactivePlan.setDescription("Inactive basic plan");
        inactivePlan.setState(inactiveState);
        
        // When
        SubscriptionPlan savedActivePlan = subscriptionPlanRepository.save(activePlan);
        SubscriptionPlan savedInactivePlan = subscriptionPlanRepository.save(inactivePlan);
        
        List<SubscriptionPlan> activePlans = subscriptionPlanRepository.findByStateId(activeState.getId());
        List<SubscriptionPlan> inactivePlans = subscriptionPlanRepository.findByStateId(inactiveState.getId());
        
        // Then
        assertNotNull(savedActivePlan.getId());
        assertNotNull(savedInactivePlan.getId());
        assertNotEquals(savedActivePlan.getId(), savedInactivePlan.getId());
        
        assertEquals(1, activePlans.size());
        assertEquals(1, inactivePlans.size());
        
        assertEquals("Basic Plan", activePlans.get(0).getName());
        assertEquals("Basic Plan", inactivePlans.get(0).getName());
        assertEquals(0, new BigDecimal("29.99").compareTo(activePlans.get(0).getPrice()));
        assertEquals(0, new BigDecimal("19.99").compareTo(inactivePlans.get(0).getPrice()));
    }

    @Test
    @DisplayName("Should maintain referential integrity with state entity")
    void shouldMaintainReferentialIntegrityWithStateEntity() {
        // Given
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        
        // When
        Optional<SubscriptionPlan> retrievedPlan = subscriptionPlanRepository.findById(savedPlan.getId());
        
        // Then
        assertTrue(retrievedPlan.isPresent());
        assertNotNull(retrievedPlan.get().getState());
        assertEquals(activeState.getId(), retrievedPlan.get().getState().getId());
        assertEquals(activeState.getState(), retrievedPlan.get().getState().getState());
    }

    @Test
    @DisplayName("Should handle soft delete scenarios")
    void shouldHandleSoftDeleteScenarios() {
        // Given
        SubscriptionPlan activePlan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        activePlan.setName("Active Plan");
        
        SubscriptionPlan eliminatedPlan = TestDataFactory.createBasicSubscriptionPlan(eliminatedState);
        eliminatedPlan.setName("Eliminated Plan");
        
        subscriptionPlanRepository.save(activePlan);
        subscriptionPlanRepository.save(eliminatedPlan);
        
        // When
        List<SubscriptionPlan> activePlans = subscriptionPlanRepository.findByStateId(activeState.getId());
        List<SubscriptionPlan> eliminatedPlans = subscriptionPlanRepository.findByStateId(eliminatedState.getId());
        List<SubscriptionPlan> allPlans = subscriptionPlanRepository.findAll();
        
        // Then
        assertEquals(1, activePlans.size());
        assertEquals(1, eliminatedPlans.size());
        assertEquals(2, allPlans.size()); // Both should be present in findAll()
        
        assertTrue(activePlans.stream().allMatch(p -> p.getState().getState().equals("ACTIVE")));
        assertTrue(eliminatedPlans.stream().allMatch(p -> p.getState().getState().equals("ELIMINATED")));
    }

    @Test
    @DisplayName("Should handle large price values")
    void shouldHandleLargePriceValues() {
        // Given
        SubscriptionPlan expensivePlan = new SubscriptionPlan();
        expensivePlan.setName("Enterprise Plan");
        expensivePlan.setPrice(new BigDecimal("99999.99"));
        expensivePlan.setDescription("Very expensive enterprise plan");
        expensivePlan.setState(activeState);
        
        // When
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(expensivePlan);
        Optional<SubscriptionPlan> retrievedPlan = subscriptionPlanRepository.findById(savedPlan.getId());
        
        // Then
        assertTrue(retrievedPlan.isPresent());
        assertEquals(0, new BigDecimal("99999.99").compareTo(retrievedPlan.get().getPrice()));
    }

    @Test
    @DisplayName("Should handle price values with many decimal places")
    void shouldHandlePriceValuesWithManyDecimalPlaces() {
        // Given
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Precision Plan");
        plan.setPrice(new BigDecimal("29.999999"));
        plan.setDescription("Plan with high precision price");
        plan.setState(activeState);
        
        // When
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        Optional<SubscriptionPlan> retrievedPlan = subscriptionPlanRepository.findById(savedPlan.getId());
        
        // Then
        assertTrue(retrievedPlan.isPresent());
        assertEquals(0, new BigDecimal("29.999999").compareTo(retrievedPlan.get().getPrice()));
    }

    @Test
    @DisplayName("Should handle special characters in name and description")
    void shouldHandleSpecialCharactersInNameAndDescription() {
        // Given
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Plan with Special Chars: @#$%^&*()_+-={}[]|\\:;\"'<>?,./");
        plan.setPrice(new BigDecimal("39.99"));
        plan.setDescription("Description with special chars: àáâãäåæçèéêëìíîïñòóôõöø");
        plan.setState(activeState);
        
        // When
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        Optional<SubscriptionPlan> retrievedPlan = subscriptionPlanRepository.findById(savedPlan.getId());
        
        // Then
        assertTrue(retrievedPlan.isPresent());
        assertTrue(retrievedPlan.get().getName().contains("@#$%^&*"));
        assertTrue(retrievedPlan.get().getDescription().contains("àáâãäåæçèéêë"));
    }

    @Test
    @DisplayName("Should handle Unicode characters in name and description")
    void shouldHandleUnicodeCharactersInNameAndDescription() {
        // Given
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName("Plano Básico 中文 العربية русский");
        plan.setPrice(new BigDecimal("49.99"));
        plan.setDescription("Descrição em português 中文描述 وصف عربي русское описание");
        plan.setState(activeState);
        
        // When
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        Optional<SubscriptionPlan> retrievedPlan = subscriptionPlanRepository.findById(savedPlan.getId());
        
        // Then
        assertTrue(retrievedPlan.isPresent());
        assertTrue(retrievedPlan.get().getName().contains("中文"));
        assertTrue(retrievedPlan.get().getDescription().contains("العربية"));
    }

    @Test
    @DisplayName("Should update existing subscription plan")
    void shouldUpdateExistingSubscriptionPlan() {
        // Given
        SubscriptionPlan originalPlan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        originalPlan.setName("Original Plan");
        originalPlan.setPrice(new BigDecimal("29.99"));
        originalPlan.setDescription("Original description");
        
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(originalPlan);
        
        // When
        savedPlan.setName("Updated Plan");
        savedPlan.setPrice(new BigDecimal("39.99"));
        savedPlan.setDescription("Updated description");
        savedPlan.setState(inactiveState);
        
        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(savedPlan);
        Optional<SubscriptionPlan> retrievedPlan = subscriptionPlanRepository.findById(updatedPlan.getId());
        
        // Then
        assertTrue(retrievedPlan.isPresent());
        assertEquals("Updated Plan", retrievedPlan.get().getName());
        assertEquals(0, new BigDecimal("39.99").compareTo(retrievedPlan.get().getPrice()));
        assertEquals("Updated description", retrievedPlan.get().getDescription());
        assertEquals(inactiveState.getId(), retrievedPlan.get().getState().getId());
    }

    @Test
    @DisplayName("Should delete subscription plan")
    void shouldDeleteSubscriptionPlan() {
        // Given
        SubscriptionPlan plan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
        Long planId = savedPlan.getId();
        
        // When
        subscriptionPlanRepository.delete(savedPlan);
        Optional<SubscriptionPlan> deletedPlan = subscriptionPlanRepository.findById(planId);
        
        // Then
        assertFalse(deletedPlan.isPresent());
    }

    @Test
    @DisplayName("Should count subscription plans correctly")
    void shouldCountSubscriptionPlansCorrectly() {
        // Given
        SubscriptionPlan plan1 = TestDataFactory.createBasicSubscriptionPlan(activeState);
        plan1.setName("Plan 1");
        
        SubscriptionPlan plan2 = TestDataFactory.createBasicSubscriptionPlan(activeState);
        plan2.setName("Plan 2");
        
        SubscriptionPlan plan3 = TestDataFactory.createBasicSubscriptionPlan(inactiveState);
        plan3.setName("Plan 3");
        
        // When
        long initialCount = subscriptionPlanRepository.count();
        
        subscriptionPlanRepository.save(plan1);
        subscriptionPlanRepository.save(plan2);
        subscriptionPlanRepository.save(plan3);
        
        long finalCount = subscriptionPlanRepository.count();
        
        // Then
        assertEquals(initialCount + 3, finalCount);
    }
}