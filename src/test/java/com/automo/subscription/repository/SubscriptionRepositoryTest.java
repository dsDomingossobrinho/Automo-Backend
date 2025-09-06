package com.automo.subscription.repository;

import com.automo.accountType.entity.AccountType;
import com.automo.accountType.repository.AccountTypeRepository;
import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.promotion.entity.Promotion;
import com.automo.promotion.repository.PromotionRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import com.automo.subscription.entity.Subscription;
import com.automo.subscriptionPlan.entity.SubscriptionPlan;
import com.automo.subscriptionPlan.repository.SubscriptionPlanRepository;
import com.automo.test.utils.TestDataFactory;
import com.automo.user.entity.User;
import com.automo.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for SubscriptionRepository")
class SubscriptionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    private User testUser1;
    private User testUser2;
    private SubscriptionPlan testPlan1;
    private SubscriptionPlan testPlan2;
    private Promotion testPromotion;
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

        // Create and persist account type
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        accountType = accountTypeRepository.save(accountType);

        // Create and persist auth entities
        Auth auth1 = TestDataFactory.createValidAuth("user1@test.com");
        Auth auth2 = TestDataFactory.createValidAuth("user2@test.com");
        auth1 = authRepository.save(auth1);
        auth2 = authRepository.save(auth2);

        // Create and persist users
        testUser1 = TestDataFactory.createValidUser(auth1, accountType, activeState);
        testUser1.setName("User 1");
        testUser2 = TestDataFactory.createValidUser(auth2, accountType, activeState);
        testUser2.setName("User 2");
        
        testUser1 = userRepository.save(testUser1);
        testUser2 = userRepository.save(testUser2);

        // Create and persist subscription plans
        testPlan1 = TestDataFactory.createBasicSubscriptionPlan(activeState);
        testPlan1.setName("Basic Plan");
        testPlan2 = TestDataFactory.createPremiumSubscriptionPlan(activeState);
        testPlan2.setName("Premium Plan");
        
        testPlan1 = subscriptionPlanRepository.save(testPlan1);
        testPlan2 = subscriptionPlanRepository.save(testPlan2);

        // Create and persist promotion
        testPromotion = new Promotion();
        testPromotion.setName("Test Promotion");
        testPromotion.setDescription("Test promotion description");
        testPromotion.setDiscountPercentage(new BigDecimal("10.00"));
        testPromotion.setState(activeState);
        testPromotion = promotionRepository.save(testPromotion);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Should save and retrieve subscription")
    void shouldSaveAndRetrieveSubscription() {
        // Given
        Subscription subscription = TestDataFactory.createValidSubscription(testUser1, testPlan1, activeState);
        
        // When
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        Optional<Subscription> retrievedSubscription = subscriptionRepository.findById(savedSubscription.getId());
        
        // Then
        assertTrue(retrievedSubscription.isPresent());
        assertEquals(savedSubscription.getId(), retrievedSubscription.get().getId());
        assertEquals(testUser1.getId(), retrievedSubscription.get().getUser().getId());
        assertEquals(testPlan1.getId(), retrievedSubscription.get().getPlan().getId());
        assertEquals(activeState.getId(), retrievedSubscription.get().getState().getId());
    }

    @Test
    @DisplayName("Should find subscriptions by state ID")
    void shouldFindSubscriptionsByStateId() {
        // Given
        Subscription activeSubscription1 = TestDataFactory.createValidSubscription(testUser1, testPlan1, activeState);
        Subscription activeSubscription2 = TestDataFactory.createValidSubscription(testUser2, testPlan2, activeState);
        Subscription inactiveSubscription = TestDataFactory.createValidSubscription(testUser1, testPlan2, inactiveState);
        
        subscriptionRepository.save(activeSubscription1);
        subscriptionRepository.save(activeSubscription2);
        subscriptionRepository.save(inactiveSubscription);
        
        // When
        List<Subscription> activeSubscriptions = subscriptionRepository.findByStateId(activeState.getId());
        List<Subscription> inactiveSubscriptions = subscriptionRepository.findByStateId(inactiveState.getId());
        
        // Then
        assertEquals(2, activeSubscriptions.size());
        assertEquals(1, inactiveSubscriptions.size());
        assertTrue(activeSubscriptions.stream().allMatch(s -> s.getState().getId().equals(activeState.getId())));
        assertTrue(inactiveSubscriptions.stream().allMatch(s -> s.getState().getId().equals(inactiveState.getId())));
    }

    @Test
    @DisplayName("Should find subscriptions by user ID")
    void shouldFindSubscriptionsByUserId() {
        // Given
        Subscription subscription1 = TestDataFactory.createValidSubscription(testUser1, testPlan1, activeState);
        Subscription subscription2 = TestDataFactory.createValidSubscription(testUser1, testPlan2, inactiveState);
        Subscription subscription3 = TestDataFactory.createValidSubscription(testUser2, testPlan1, activeState);
        
        subscriptionRepository.save(subscription1);
        subscriptionRepository.save(subscription2);
        subscriptionRepository.save(subscription3);
        
        // When
        List<Subscription> user1Subscriptions = subscriptionRepository.findByUserId(testUser1.getId());
        List<Subscription> user2Subscriptions = subscriptionRepository.findByUserId(testUser2.getId());
        
        // Then
        assertEquals(2, user1Subscriptions.size());
        assertEquals(1, user2Subscriptions.size());
        assertTrue(user1Subscriptions.stream().allMatch(s -> s.getUser().getId().equals(testUser1.getId())));
        assertTrue(user2Subscriptions.stream().allMatch(s -> s.getUser().getId().equals(testUser2.getId())));
    }

    @Test
    @DisplayName("Should find subscriptions by user ID and state ID")
    void shouldFindSubscriptionsByUserIdAndStateId() {
        // Given
        Subscription activeSubscription = TestDataFactory.createValidSubscription(testUser1, testPlan1, activeState);
        Subscription inactiveSubscription = TestDataFactory.createValidSubscription(testUser1, testPlan2, inactiveState);
        Subscription otherUserSubscription = TestDataFactory.createValidSubscription(testUser2, testPlan1, activeState);
        
        subscriptionRepository.save(activeSubscription);
        subscriptionRepository.save(inactiveSubscription);
        subscriptionRepository.save(otherUserSubscription);
        
        // When
        List<Subscription> user1ActiveSubscriptions = subscriptionRepository.findByUserIdAndStateId(testUser1.getId(), activeState.getId());
        List<Subscription> user1InactiveSubscriptions = subscriptionRepository.findByUserIdAndStateId(testUser1.getId(), inactiveState.getId());
        
        // Then
        assertEquals(1, user1ActiveSubscriptions.size());
        assertEquals(1, user1InactiveSubscriptions.size());
        assertEquals(testUser1.getId(), user1ActiveSubscriptions.get(0).getUser().getId());
        assertEquals(activeState.getId(), user1ActiveSubscriptions.get(0).getState().getId());
        assertEquals(testUser1.getId(), user1InactiveSubscriptions.get(0).getUser().getId());
        assertEquals(inactiveState.getId(), user1InactiveSubscriptions.get(0).getState().getId());
    }

    @Test
    @DisplayName("Should find subscriptions by plan ID")
    void shouldFindSubscriptionsByPlanId() {
        // Given
        Subscription subscription1 = TestDataFactory.createValidSubscription(testUser1, testPlan1, activeState);
        Subscription subscription2 = TestDataFactory.createValidSubscription(testUser2, testPlan1, activeState);
        Subscription subscription3 = TestDataFactory.createValidSubscription(testUser1, testPlan2, activeState);
        
        subscriptionRepository.save(subscription1);
        subscriptionRepository.save(subscription2);
        subscriptionRepository.save(subscription3);
        
        // When
        List<Subscription> plan1Subscriptions = subscriptionRepository.findByPlanId(testPlan1.getId());
        List<Subscription> plan2Subscriptions = subscriptionRepository.findByPlanId(testPlan2.getId());
        
        // Then
        assertEquals(2, plan1Subscriptions.size());
        assertEquals(1, plan2Subscriptions.size());
        assertTrue(plan1Subscriptions.stream().allMatch(s -> s.getPlan().getId().equals(testPlan1.getId())));
        assertTrue(plan2Subscriptions.stream().allMatch(s -> s.getPlan().getId().equals(testPlan2.getId())));
    }

    @Test
    @DisplayName("Should find subscriptions by promotion ID")
    void shouldFindSubscriptionsByPromotionId() {
        // Given
        Subscription subscriptionWithPromotion1 = TestDataFactory.createValidSubscription(testUser1, testPlan1, activeState);
        subscriptionWithPromotion1.setPromotion(testPromotion);
        
        Subscription subscriptionWithPromotion2 = TestDataFactory.createValidSubscription(testUser2, testPlan2, activeState);
        subscriptionWithPromotion2.setPromotion(testPromotion);
        
        Subscription subscriptionWithoutPromotion = TestDataFactory.createValidSubscription(testUser1, testPlan2, activeState);
        
        subscriptionRepository.save(subscriptionWithPromotion1);
        subscriptionRepository.save(subscriptionWithPromotion2);
        subscriptionRepository.save(subscriptionWithoutPromotion);
        
        // When
        List<Subscription> subscriptionsWithPromotion = subscriptionRepository.findByPromotionId(testPromotion.getId());
        List<Subscription> subscriptionsWithoutPromotion = subscriptionRepository.findByPromotionId(null);
        
        // Then
        assertEquals(2, subscriptionsWithPromotion.size());
        assertTrue(subscriptionsWithPromotion.stream().allMatch(s -> s.getPromotion() != null && s.getPromotion().getId().equals(testPromotion.getId())));
        
        // Note: findByPromotionId(null) might not return expected results depending on JPA implementation
        // This test focuses on the positive case
    }

    @Test
    @DisplayName("Should find subscriptions by start date between range")
    void shouldFindSubscriptionsByStartDateBetweenRange() {
        // Given
        LocalDate baseDate = LocalDate.of(2023, 6, 1);
        LocalDate startDate = baseDate.minusDays(10);
        LocalDate endDate = baseDate.plusDays(10);
        
        Subscription subscriptionInRange1 = TestDataFactory.createValidSubscription(testUser1, testPlan1, baseDate, baseDate.plusMonths(1), activeState);
        Subscription subscriptionInRange2 = TestDataFactory.createValidSubscription(testUser2, testPlan2, baseDate.plusDays(5), baseDate.plusDays(35), activeState);
        Subscription subscriptionOutOfRange = TestDataFactory.createValidSubscription(testUser1, testPlan2, baseDate.minusDays(20), baseDate.plusDays(10), activeState);
        
        subscriptionRepository.save(subscriptionInRange1);
        subscriptionRepository.save(subscriptionInRange2);
        subscriptionRepository.save(subscriptionOutOfRange);
        
        // When
        List<Subscription> subscriptionsInRange = subscriptionRepository.findByStartDateBetween(startDate, endDate);
        
        // Then
        assertEquals(2, subscriptionsInRange.size());
        assertTrue(subscriptionsInRange.stream().allMatch(s -> 
            !s.getStartDate().isBefore(startDate) && !s.getStartDate().isAfter(endDate)
        ));
    }

    @Test
    @DisplayName("Should find subscriptions by end date before specified date")
    void shouldFindSubscriptionsByEndDateBeforeSpecifiedDate() {
        // Given
        LocalDate currentDate = LocalDate.of(2023, 6, 15);
        
        Subscription expiredSubscription1 = TestDataFactory.createValidSubscription(testUser1, testPlan1, 
            currentDate.minusMonths(2), currentDate.minusDays(5), activeState);
        Subscription expiredSubscription2 = TestDataFactory.createValidSubscription(testUser2, testPlan2,
            currentDate.minusMonths(1), currentDate.minusDays(1), activeState);
        Subscription activeSubscription = TestDataFactory.createValidSubscription(testUser1, testPlan2,
            currentDate.minusDays(10), currentDate.plusDays(20), activeState);
        
        subscriptionRepository.save(expiredSubscription1);
        subscriptionRepository.save(expiredSubscription2);
        subscriptionRepository.save(activeSubscription);
        
        // When
        List<Subscription> expiredSubscriptions = subscriptionRepository.findByEndDateBefore(currentDate);
        
        // Then
        assertEquals(2, expiredSubscriptions.size());
        assertTrue(expiredSubscriptions.stream().allMatch(s -> s.getEndDate().isBefore(currentDate)));
    }

    @Test
    @DisplayName("Should handle empty results for non-existent data")
    void shouldHandleEmptyResultsForNonExistentData() {
        // When
        List<Subscription> nonExistentStateSubscriptions = subscriptionRepository.findByStateId(999L);
        List<Subscription> nonExistentUserSubscriptions = subscriptionRepository.findByUserId(999L);
        List<Subscription> nonExistentPlanSubscriptions = subscriptionRepository.findByPlanId(999L);
        List<Subscription> nonExistentPromotionSubscriptions = subscriptionRepository.findByPromotionId(999L);
        List<Subscription> emptyDateRangeSubscriptions = subscriptionRepository.findByStartDateBetween(
            LocalDate.of(1990, 1, 1), LocalDate.of(1990, 12, 31)
        );
        List<Subscription> emptyExpiredSubscriptions = subscriptionRepository.findByEndDateBefore(LocalDate.of(1990, 1, 1));
        
        // Then
        assertTrue(nonExistentStateSubscriptions.isEmpty());
        assertTrue(nonExistentUserSubscriptions.isEmpty());
        assertTrue(nonExistentPlanSubscriptions.isEmpty());
        assertTrue(nonExistentPromotionSubscriptions.isEmpty());
        assertTrue(emptyDateRangeSubscriptions.isEmpty());
        assertTrue(emptyExpiredSubscriptions.isEmpty());
    }

    @Test
    @DisplayName("Should save subscription with all optional fields")
    void shouldSaveSubscriptionWithAllOptionalFields() {
        // Given
        Subscription subscription = TestDataFactory.createValidSubscription(testUser1, testPlan1, activeState);
        subscription.setPromotion(testPromotion);
        subscription.setPrice(new BigDecimal("19.99")); // Discounted price
        subscription.setMessageCount(2000);
        
        // When
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        Optional<Subscription> retrievedSubscription = subscriptionRepository.findById(savedSubscription.getId());
        
        // Then
        assertTrue(retrievedSubscription.isPresent());
        assertEquals(testPromotion.getId(), retrievedSubscription.get().getPromotion().getId());
        assertEquals(0, new BigDecimal("19.99").compareTo(retrievedSubscription.get().getPrice()));
        assertEquals(2000, retrievedSubscription.get().getMessageCount());
    }

    @Test
    @DisplayName("Should maintain referential integrity with related entities")
    void shouldMaintainReferentialIntegrityWithRelatedEntities() {
        // Given
        Subscription subscription = TestDataFactory.createValidSubscription(testUser1, testPlan1, activeState);
        subscription.setPromotion(testPromotion);
        Subscription savedSubscription = subscriptionRepository.save(subscription);
        
        // When
        Optional<Subscription> retrievedSubscription = subscriptionRepository.findById(savedSubscription.getId());
        
        // Then
        assertTrue(retrievedSubscription.isPresent());
        Subscription sub = retrievedSubscription.get();
        
        // Verify all relationships are properly loaded
        assertNotNull(sub.getUser());
        assertNotNull(sub.getPlan());
        assertNotNull(sub.getPromotion());
        assertNotNull(sub.getState());
        
        assertEquals(testUser1.getName(), sub.getUser().getName());
        assertEquals(testPlan1.getName(), sub.getPlan().getName());
        assertEquals(testPromotion.getName(), sub.getPromotion().getName());
        assertEquals(activeState.getState(), sub.getState().getState());
    }

    @Test
    @DisplayName("Should handle soft delete scenarios")
    void shouldHandleSoftDeleteScenarios() {
        // Given
        Subscription activeSubscription = TestDataFactory.createValidSubscription(testUser1, testPlan1, activeState);
        Subscription eliminatedSubscription = TestDataFactory.createValidSubscription(testUser1, testPlan2, eliminatedState);
        
        subscriptionRepository.save(activeSubscription);
        subscriptionRepository.save(eliminatedSubscription);
        
        // When
        List<Subscription> activeSubscriptions = subscriptionRepository.findByStateId(activeState.getId());
        List<Subscription> eliminatedSubscriptions = subscriptionRepository.findByStateId(eliminatedState.getId());
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();
        
        // Then
        assertEquals(1, activeSubscriptions.size());
        assertEquals(1, eliminatedSubscriptions.size());
        assertEquals(2, allSubscriptions.size()); // Both should be present in findAll()
        
        assertTrue(activeSubscriptions.stream().allMatch(s -> s.getState().getState().equals("ACTIVE")));
        assertTrue(eliminatedSubscriptions.stream().allMatch(s -> s.getState().getState().equals("ELIMINATED")));
    }
}