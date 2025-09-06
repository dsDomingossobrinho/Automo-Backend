package com.automo.subscriptionPlan.service;

import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.subscriptionPlan.dto.SubscriptionPlanDto;
import com.automo.subscriptionPlan.entity.SubscriptionPlan;
import com.automo.subscriptionPlan.repository.SubscriptionPlanRepository;
import com.automo.subscriptionPlan.response.SubscriptionPlanResponse;
import com.automo.test.utils.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for SubscriptionPlanServiceImpl")
class SubscriptionPlanServiceImplTest {

    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Mock
    private StateService stateService;

    @InjectMocks
    private SubscriptionPlanServiceImpl subscriptionPlanService;

    private SubscriptionPlan testPlan;
    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private SubscriptionPlanDto testPlanDto;

    @BeforeEach
    void setUp() {
        // Setup test states
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        activeState.setState("ACTIVE");

        inactiveState = TestDataFactory.createInactiveState();
        inactiveState.setId(2L);
        inactiveState.setState("INACTIVE");

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(3L);
        eliminatedState.setState("ELIMINATED");

        // Setup test plan
        testPlan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        testPlan.setId(1L);

        // Setup test DTO
        testPlanDto = TestDataFactory.createValidSubscriptionPlanDto(1L);
    }

    @Test
    @DisplayName("Should create subscription plan successfully")
    void shouldCreateSubscriptionPlanSuccessfully() {
        // Given
        when(stateService.findById(1L)).thenReturn(activeState);
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(testPlan);

        // When
        SubscriptionPlanResponse result = subscriptionPlanService.createSubscriptionPlan(testPlanDto);

        // Then
        assertNotNull(result);
        assertEquals(testPlan.getId(), result.id());
        assertEquals(testPlan.getName(), result.name());
        assertEquals(testPlan.getPrice(), result.price());
        assertEquals(testPlan.getDescription(), result.description());
        assertEquals(activeState.getId(), result.stateId());
        assertEquals(activeState.getState(), result.stateName());

        verify(stateService).findById(1L);
        verify(subscriptionPlanRepository).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Should throw exception when state not found during creation")
    void shouldThrowExceptionWhenStateNotFoundDuringCreation() {
        // Given
        when(stateService.findById(1L)).thenThrow(new EntityNotFoundException("State not found"));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionPlanService.createSubscriptionPlan(testPlanDto);
        });

        verify(stateService).findById(1L);
        verify(subscriptionPlanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update subscription plan successfully")
    void shouldUpdateSubscriptionPlanSuccessfully() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(testPlan);

        // When
        SubscriptionPlanResponse result = subscriptionPlanService.updateSubscriptionPlan(1L, testPlanDto);

        // Then
        assertNotNull(result);
        assertEquals(testPlan.getId(), result.id());
        verify(subscriptionPlanRepository).findById(1L);
        verify(stateService).findById(1L);
        verify(subscriptionPlanRepository).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent subscription plan")
    void shouldThrowExceptionWhenUpdatingNonExistentSubscriptionPlan() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionPlanService.updateSubscriptionPlan(1L, testPlanDto);
        });

        verify(subscriptionPlanRepository).findById(1L);
        verify(stateService, never()).findById(any());
        verify(subscriptionPlanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should get all subscription plans excluding eliminated")
    void shouldGetAllSubscriptionPlansExcludingEliminated() {
        // Given
        SubscriptionPlan activePlan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        SubscriptionPlan inactivePlan = TestDataFactory.createBasicSubscriptionPlan(inactiveState);
        SubscriptionPlan eliminatedPlan = TestDataFactory.createBasicSubscriptionPlan(eliminatedState);

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(subscriptionPlanRepository.findAll()).thenReturn(Arrays.asList(activePlan, inactivePlan, eliminatedPlan));

        // When
        List<SubscriptionPlanResponse> result = subscriptionPlanService.getAllSubscriptionPlans();

        // Then
        assertEquals(2, result.size());
        verify(stateService).getEliminatedState();
        verify(subscriptionPlanRepository).findAll();
    }

    @Test
    @DisplayName("Should get subscription plan by ID")
    void shouldGetSubscriptionPlanById() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));

        // When
        SubscriptionPlan result = subscriptionPlanService.getSubscriptionPlanById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testPlan.getId(), result.getId());
        verify(subscriptionPlanRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when subscription plan not found by ID")
    void shouldThrowExceptionWhenSubscriptionPlanNotFoundById() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionPlanService.getSubscriptionPlanById(1L);
        });
    }

    @Test
    @DisplayName("Should get subscription plan by ID as response")
    void shouldGetSubscriptionPlanByIdAsResponse() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));

        // When
        SubscriptionPlanResponse result = subscriptionPlanService.getSubscriptionPlanByIdResponse(1L);

        // Then
        assertNotNull(result);
        assertEquals(testPlan.getId(), result.id());
        assertEquals(testPlan.getName(), result.name());
        assertEquals(testPlan.getPrice(), result.price());
    }

    @Test
    @DisplayName("Should get subscription plans by state")
    void shouldGetSubscriptionPlansByState() {
        // Given
        List<SubscriptionPlan> plans = Arrays.asList(testPlan);
        when(subscriptionPlanRepository.findByStateId(1L)).thenReturn(plans);

        // When
        List<SubscriptionPlanResponse> result = subscriptionPlanService.getSubscriptionPlansByState(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testPlan.getId(), result.get(0).id());
        verify(subscriptionPlanRepository).findByStateId(1L);
    }

    @Test
    @DisplayName("Should delete subscription plan using soft delete")
    void shouldDeleteSubscriptionPlanUsingSoftDelete() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(testPlan);

        // When
        subscriptionPlanService.deleteSubscriptionPlan(1L);

        // Then
        verify(subscriptionPlanRepository).findById(1L);
        verify(stateService).getEliminatedState();
        verify(subscriptionPlanRepository).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Should findById return subscription plan")
    void shouldFindByIdReturnSubscriptionPlan() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));

        // When
        SubscriptionPlan result = subscriptionPlanService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testPlan.getId(), result.getId());
        verify(subscriptionPlanRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when findById subscription plan not found")
    void shouldThrowExceptionWhenFindByIdSubscriptionPlanNotFound() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionPlanService.findById(1L);
        });
    }

    @Test
    @DisplayName("Should findByIdAndStateId return subscription plan with matching state")
    void shouldFindByIdAndStateIdReturnSubscriptionPlanWithMatchingState() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));

        // When
        SubscriptionPlan result = subscriptionPlanService.findByIdAndStateId(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(testPlan.getId(), result.getId());
        verify(subscriptionPlanRepository).findById(1L);
    }

    @Test
    @DisplayName("Should use default state ID when null provided to findByIdAndStateId")
    void shouldUseDefaultStateIdWhenNullProvidedToFindByIdAndStateId() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));

        // When
        SubscriptionPlan result = subscriptionPlanService.findByIdAndStateId(1L, null);

        // Then
        assertNotNull(result);
        assertEquals(testPlan.getId(), result.getId());
        verify(subscriptionPlanRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when findByIdAndStateId subscription plan not found")
    void shouldThrowExceptionWhenFindByIdAndStateIdSubscriptionPlanNotFound() {
        // Given
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionPlanService.findByIdAndStateId(1L, 1L);
        });
    }

    @Test
    @DisplayName("Should throw exception when subscription plan state doesn't match required state")
    void shouldThrowExceptionWhenSubscriptionPlanStateDoesntMatchRequiredState() {
        // Given
        SubscriptionPlan planWithDifferentState = TestDataFactory.createBasicSubscriptionPlan(inactiveState);
        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(planWithDifferentState));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionPlanService.findByIdAndStateId(1L, 1L); // Looking for active state (ID: 1)
        });
    }

    @Test
    @DisplayName("Should handle empty repository results")
    void shouldHandleEmptyRepositoryResults() {
        // Given
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(subscriptionPlanRepository.findAll()).thenReturn(Arrays.asList());
        when(subscriptionPlanRepository.findByStateId(1L)).thenReturn(Arrays.asList());

        // When
        List<SubscriptionPlanResponse> allPlans = subscriptionPlanService.getAllSubscriptionPlans();
        List<SubscriptionPlanResponse> plansByState = subscriptionPlanService.getSubscriptionPlansByState(1L);

        // Then
        assertTrue(allPlans.isEmpty());
        assertTrue(plansByState.isEmpty());
    }

    @Test
    @DisplayName("Should create subscription plan with null description")
    void shouldCreateSubscriptionPlanWithNullDescription() {
        // Given
        SubscriptionPlanDto dtoWithNullDescription = new SubscriptionPlanDto(
                "Test Plan", testPlanDto.price(), null, testPlanDto.stateId()
        );
        
        SubscriptionPlan planWithNullDescription = TestDataFactory.createBasicSubscriptionPlan(activeState);
        planWithNullDescription.setDescription(null);

        when(stateService.findById(1L)).thenReturn(activeState);
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(planWithNullDescription);

        // When
        SubscriptionPlanResponse result = subscriptionPlanService.createSubscriptionPlan(dtoWithNullDescription);

        // Then
        assertNotNull(result);
        assertNull(result.description());
        verify(stateService).findById(1L);
        verify(subscriptionPlanRepository).save(any(SubscriptionPlan.class));
    }

    @Test
    @DisplayName("Should update subscription plan with different values")
    void shouldUpdateSubscriptionPlanWithDifferentValues() {
        // Given
        SubscriptionPlanDto updatedDto = new SubscriptionPlanDto(
                "Updated Plan Name",
                testPlanDto.price().add(testPlanDto.price()), // Double the price
                "Updated description",
                2L // Different state
        );

        SubscriptionPlan updatedPlan = TestDataFactory.createBasicSubscriptionPlan(inactiveState);
        updatedPlan.setId(1L);
        updatedPlan.setName("Updated Plan Name");
        updatedPlan.setPrice(updatedDto.price());
        updatedPlan.setDescription("Updated description");

        when(subscriptionPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(stateService.findById(2L)).thenReturn(inactiveState);
        when(subscriptionPlanRepository.save(any(SubscriptionPlan.class))).thenReturn(updatedPlan);

        // When
        SubscriptionPlanResponse result = subscriptionPlanService.updateSubscriptionPlan(1L, updatedDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Plan Name", result.name());
        assertEquals("Updated description", result.description());
        assertEquals(inactiveState.getId(), result.stateId());
        assertEquals(inactiveState.getState(), result.stateName());
    }

    @Test
    @DisplayName("Should filter out plans with null states in getAllSubscriptionPlans")
    void shouldFilterOutPlansWithNullStatesInGetAllSubscriptionPlans() {
        // Given
        SubscriptionPlan validPlan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        SubscriptionPlan planWithNullState = TestDataFactory.createBasicSubscriptionPlan(activeState);
        planWithNullState.setState(null);
        SubscriptionPlan eliminatedPlan = TestDataFactory.createBasicSubscriptionPlan(eliminatedState);

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(subscriptionPlanRepository.findAll()).thenReturn(Arrays.asList(validPlan, planWithNullState, eliminatedPlan));

        // When
        List<SubscriptionPlanResponse> result = subscriptionPlanService.getAllSubscriptionPlans();

        // Then
        assertEquals(1, result.size()); // Only the valid plan should be returned
        assertEquals(validPlan.getName(), result.get(0).name());
    }

    @Test
    @DisplayName("Should handle multiple subscription plans with different states")
    void shouldHandleMultipleSubscriptionPlansWithDifferentStates() {
        // Given
        SubscriptionPlan basicPlan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        basicPlan.setName("Basic Plan");
        
        SubscriptionPlan premiumPlan = TestDataFactory.createPremiumSubscriptionPlan(activeState);
        premiumPlan.setName("Premium Plan");
        
        SubscriptionPlan inactivePlan = TestDataFactory.createBasicSubscriptionPlan(inactiveState);
        inactivePlan.setName("Inactive Plan");

        when(subscriptionPlanRepository.findByStateId(1L)).thenReturn(Arrays.asList(basicPlan, premiumPlan));
        when(subscriptionPlanRepository.findByStateId(2L)).thenReturn(Arrays.asList(inactivePlan));

        // When
        List<SubscriptionPlanResponse> activePlans = subscriptionPlanService.getSubscriptionPlansByState(1L);
        List<SubscriptionPlanResponse> inactivePlans = subscriptionPlanService.getSubscriptionPlansByState(2L);

        // Then
        assertEquals(2, activePlans.size());
        assertEquals(1, inactivePlans.size());
        
        assertTrue(activePlans.stream().anyMatch(p -> "Basic Plan".equals(p.name())));
        assertTrue(activePlans.stream().anyMatch(p -> "Premium Plan".equals(p.name())));
        assertTrue(inactivePlans.stream().anyMatch(p -> "Inactive Plan".equals(p.name())));
    }
}