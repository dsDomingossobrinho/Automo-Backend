package com.automo.subscription.service;

import com.automo.promotion.entity.Promotion;
import com.automo.promotion.service.PromotionService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.subscription.dto.SubscriptionDto;
import com.automo.subscription.entity.Subscription;
import com.automo.subscription.repository.SubscriptionRepository;
import com.automo.subscription.response.SubscriptionResponse;
import com.automo.subscriptionPlan.entity.SubscriptionPlan;
import com.automo.subscriptionPlan.service.SubscriptionPlanService;
import com.automo.test.utils.TestDataFactory;
import com.automo.user.entity.User;
import com.automo.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for SubscriptionServiceImpl")
class SubscriptionServiceImplTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserService userService;

    @Mock
    private SubscriptionPlanService planService;

    @Mock
    private PromotionService promotionService;

    @Mock
    private StateService stateService;

    @InjectMocks
    private SubscriptionServiceImpl subscriptionService;

    private User testUser;
    private SubscriptionPlan testPlan;
    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private Subscription testSubscription;
    private SubscriptionDto testSubscriptionDto;

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

        // Setup test user
        testUser = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                activeState
        );
        testUser.setId(1L);
        testUser.setName("Test User");

        // Setup test plan
        testPlan = TestDataFactory.createBasicSubscriptionPlan(activeState);
        testPlan.setId(1L);

        // Setup test subscription
        testSubscription = TestDataFactory.createValidSubscription(testUser, testPlan, activeState);
        testSubscription.setId(1L);

        // Setup test DTO
        testSubscriptionDto = TestDataFactory.createValidSubscriptionDto(1L, 1L, 1L);
    }

    @Test
    @DisplayName("Should create subscription successfully")
    void shouldCreateSubscriptionSuccessfully() {
        // Given
        when(userService.findById(1L)).thenReturn(testUser);
        when(planService.findById(1L)).thenReturn(testPlan);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(subscriptionRepository.findByUserIdAndStateId(1L, 1L)).thenReturn(List.of());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        SubscriptionResponse result = subscriptionService.createSubscription(testSubscriptionDto);

        // Then
        assertNotNull(result);
        assertEquals(testSubscription.getId(), result.id());
        assertEquals(testUser.getId(), result.userId());
        assertEquals(testUser.getName(), result.userName());
        assertEquals(testPlan.getId(), result.planId());
        assertEquals(testPlan.getName(), result.planName());
        assertEquals(activeState.getId(), result.stateId());
        assertEquals(activeState.getState(), result.stateName());

        verify(userService).findById(1L);
        verify(planService).findById(1L);
        verify(stateService).findById(1L);
        verify(subscriptionRepository).findByUserIdAndStateId(1L, 1L);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("Should create subscription with promotion")
    void shouldCreateSubscriptionWithPromotion() {
        // Given
        Promotion testPromotion = new Promotion();
        testPromotion.setId(1L);
        testPromotion.setName("Test Promotion");

        SubscriptionDto dtoWithPromotion = new SubscriptionDto(
                1L, 1L, 1L, new BigDecimal("29.99"),
                LocalDate.now(), LocalDate.now().plusMonths(1), 1000, 1L
        );

        when(userService.findById(1L)).thenReturn(testUser);
        when(planService.findById(1L)).thenReturn(testPlan);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(promotionService.findById(1L)).thenReturn(testPromotion);
        when(subscriptionRepository.findByUserIdAndStateId(1L, 1L)).thenReturn(List.of());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        SubscriptionResponse result = subscriptionService.createSubscription(dtoWithPromotion);

        // Then
        assertNotNull(result);
        verify(promotionService).findById(1L);
    }

    @Test
    @DisplayName("Should handle existing active subscription when creating new one")
    void shouldHandleExistingActiveSubscriptionWhenCreatingNewOne() {
        // Given
        Subscription existingSubscription = TestDataFactory.createValidSubscription(testUser, testPlan, activeState);
        existingSubscription.setId(2L);
        existingSubscription.setEndDate(LocalDate.now().plusDays(15)); // 15 days remaining

        when(userService.findById(1L)).thenReturn(testUser);
        when(planService.findById(1L)).thenReturn(testPlan);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(stateService.getStateByState("ACTIVE")).thenReturn(activeState);
        when(stateService.getStateByState("INACTIVE")).thenReturn(inactiveState);
        when(subscriptionRepository.findByUserIdAndStateId(1L, 1L)).thenReturn(List.of(existingSubscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        SubscriptionResponse result = subscriptionService.createSubscription(testSubscriptionDto);

        // Then
        assertNotNull(result);
        verify(subscriptionRepository, times(2)).save(any(Subscription.class)); // Once for deactivating old, once for new
    }

    @Test
    @DisplayName("Should update subscription successfully")
    void shouldUpdateSubscriptionSuccessfully() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(userService.findById(1L)).thenReturn(testUser);
        when(planService.findById(1L)).thenReturn(testPlan);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        SubscriptionResponse result = subscriptionService.updateSubscription(1L, testSubscriptionDto);

        // Then
        assertNotNull(result);
        assertEquals(testSubscription.getId(), result.id());
        verify(subscriptionRepository).findById(1L);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent subscription")
    void shouldThrowExceptionWhenUpdatingNonExistentSubscription() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.updateSubscription(1L, testSubscriptionDto);
        });
    }

    @Test
    @DisplayName("Should get all subscriptions excluding eliminated")
    void shouldGetAllSubscriptionsExcludingEliminated() {
        // Given
        Subscription subscription1 = TestDataFactory.createValidSubscription(testUser, testPlan, activeState);
        Subscription subscription2 = TestDataFactory.createValidSubscription(testUser, testPlan, inactiveState);
        Subscription eliminatedSubscription = TestDataFactory.createValidSubscription(testUser, testPlan, eliminatedState);

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(subscriptionRepository.findAll()).thenReturn(Arrays.asList(subscription1, subscription2, eliminatedSubscription));
        when(userService.findById(any())).thenReturn(testUser);

        // When
        List<SubscriptionResponse> result = subscriptionService.getAllSubscriptions();

        // Then
        assertEquals(2, result.size());
        verify(stateService).getEliminatedState();
        verify(subscriptionRepository).findAll();
    }

    @Test
    @DisplayName("Should get subscription by ID")
    void shouldGetSubscriptionById() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));

        // When
        Subscription result = subscriptionService.getSubscriptionById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testSubscription.getId(), result.getId());
        verify(subscriptionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when subscription not found by ID")
    void shouldThrowExceptionWhenSubscriptionNotFoundById() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.getSubscriptionById(1L);
        });
    }

    @Test
    @DisplayName("Should get subscription by ID as response")
    void shouldGetSubscriptionByIdAsResponse() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(userService.findById(any())).thenReturn(testUser);

        // When
        SubscriptionResponse result = subscriptionService.getSubscriptionByIdResponse(1L);

        // Then
        assertNotNull(result);
        assertEquals(testSubscription.getId(), result.id());
    }

    @Test
    @DisplayName("Should get subscriptions by state")
    void shouldGetSubscriptionsByState() {
        // Given
        List<Subscription> subscriptions = Arrays.asList(testSubscription);
        when(subscriptionRepository.findByStateId(1L)).thenReturn(subscriptions);
        when(userService.findById(any())).thenReturn(testUser);

        // When
        List<SubscriptionResponse> result = subscriptionService.getSubscriptionsByState(1L);

        // Then
        assertEquals(1, result.size());
        verify(subscriptionRepository).findByStateId(1L);
    }

    @Test
    @DisplayName("Should get subscriptions by user")
    void shouldGetSubscriptionsByUser() {
        // Given
        List<Subscription> subscriptions = Arrays.asList(testSubscription);
        when(subscriptionRepository.findByUserId(1L)).thenReturn(subscriptions);
        when(userService.findById(any())).thenReturn(testUser);

        // When
        List<SubscriptionResponse> result = subscriptionService.getSubscriptionsByUser(1L);

        // Then
        assertEquals(1, result.size());
        verify(subscriptionRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should get subscriptions by plan")
    void shouldGetSubscriptionsByPlan() {
        // Given
        List<Subscription> subscriptions = Arrays.asList(testSubscription);
        when(subscriptionRepository.findByPlanId(1L)).thenReturn(subscriptions);
        when(userService.findById(any())).thenReturn(testUser);

        // When
        List<SubscriptionResponse> result = subscriptionService.getSubscriptionsByPlan(1L);

        // Then
        assertEquals(1, result.size());
        verify(subscriptionRepository).findByPlanId(1L);
    }

    @Test
    @DisplayName("Should get subscriptions by promotion")
    void shouldGetSubscriptionsByPromotion() {
        // Given
        List<Subscription> subscriptions = Arrays.asList(testSubscription);
        when(subscriptionRepository.findByPromotionId(1L)).thenReturn(subscriptions);
        when(userService.findById(any())).thenReturn(testUser);

        // When
        List<SubscriptionResponse> result = subscriptionService.getSubscriptionsByPromotion(1L);

        // Then
        assertEquals(1, result.size());
        verify(subscriptionRepository).findByPromotionId(1L);
    }

    @Test
    @DisplayName("Should get subscriptions by date range")
    void shouldGetSubscriptionsByDateRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();
        List<Subscription> subscriptions = Arrays.asList(testSubscription);
        when(subscriptionRepository.findByStartDateBetween(startDate, endDate)).thenReturn(subscriptions);
        when(userService.findById(any())).thenReturn(testUser);

        // When
        List<SubscriptionResponse> result = subscriptionService.getSubscriptionsByDateRange(startDate, endDate);

        // Then
        assertEquals(1, result.size());
        verify(subscriptionRepository).findByStartDateBetween(startDate, endDate);
    }

    @Test
    @DisplayName("Should get expired subscriptions")
    void shouldGetExpiredSubscriptions() {
        // Given
        LocalDate date = LocalDate.now();
        List<Subscription> subscriptions = Arrays.asList(testSubscription);
        when(subscriptionRepository.findByEndDateBefore(date)).thenReturn(subscriptions);
        when(userService.findById(any())).thenReturn(testUser);

        // When
        List<SubscriptionResponse> result = subscriptionService.getExpiredSubscriptions(date);

        // Then
        assertEquals(1, result.size());
        verify(subscriptionRepository).findByEndDateBefore(date);
    }

    @Test
    @DisplayName("Should delete subscription using soft delete")
    void shouldDeleteSubscriptionUsingSoftDelete() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        subscriptionService.deleteSubscription(1L);

        // Then
        verify(subscriptionRepository).findById(1L);
        verify(stateService).getEliminatedState();
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    @DisplayName("Should findById return subscription")
    void shouldFindByIdReturnSubscription() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));

        // When
        Subscription result = subscriptionService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testSubscription.getId(), result.getId());
        verify(subscriptionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when findById subscription not found")
    void shouldThrowExceptionWhenFindByIdSubscriptionNotFound() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.findById(1L);
        });
    }

    @Test
    @DisplayName("Should findByIdAndStateId return subscription with matching state")
    void shouldFindByIdAndStateIdReturnSubscriptionWithMatchingState() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));

        // When
        Subscription result = subscriptionService.findByIdAndStateId(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(testSubscription.getId(), result.getId());
        verify(subscriptionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should use default state ID when null provided to findByIdAndStateId")
    void shouldUseDefaultStateIdWhenNullProvidedToFindByIdAndStateId() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));

        // When
        Subscription result = subscriptionService.findByIdAndStateId(1L, null);

        // Then
        assertNotNull(result);
        assertEquals(testSubscription.getId(), result.getId());
        verify(subscriptionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when findByIdAndStateId subscription not found")
    void shouldThrowExceptionWhenFindByIdAndStateIdSubscriptionNotFound() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.findByIdAndStateId(1L, 1L);
        });
    }

    @Test
    @DisplayName("Should throw exception when subscription state doesn't match required state")
    void shouldThrowExceptionWhenSubscriptionStateDoesntMatchRequiredState() {
        // Given
        Subscription subscriptionWithDifferentState = TestDataFactory.createValidSubscription(testUser, testPlan, inactiveState);
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(subscriptionWithDifferentState));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            subscriptionService.findByIdAndStateId(1L, 1L); // Looking for active state (ID: 1)
        });
    }

    @Test
    @DisplayName("Should handle user not found in mapToResponse gracefully")
    void shouldHandleUserNotFoundInMapToResponseGracefully() {
        // Given
        when(subscriptionRepository.findById(1L)).thenReturn(Optional.of(testSubscription));
        when(userService.findById(any())).thenThrow(new RuntimeException("User not found"));

        // When
        SubscriptionResponse result = subscriptionService.getSubscriptionByIdResponse(1L);

        // Then
        assertNotNull(result);
        assertEquals(testSubscription.getId(), result.id());
        assertNull(result.userName()); // userName should be null when user service fails
    }

    @Test
    @DisplayName("Should handle fallback to default state when getStateByState fails")
    void shouldHandleFallbackToDefaultStateWhenGetStateByStateFails() {
        // Given
        when(userService.findById(1L)).thenReturn(testUser);
        when(planService.findById(1L)).thenReturn(testPlan);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(stateService.getStateByState("ACTIVE")).thenThrow(new EntityNotFoundException("State not found"));
        when(subscriptionRepository.findByUserIdAndStateId(1L, 1L)).thenReturn(List.of());
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        // When
        SubscriptionResponse result = subscriptionService.createSubscription(testSubscriptionDto);

        // Then
        assertNotNull(result);
        verify(stateService).getStateByState("ACTIVE");
        verify(subscriptionRepository).findByUserIdAndStateId(1L, 1L); // Should use default state ID 1L
    }
}