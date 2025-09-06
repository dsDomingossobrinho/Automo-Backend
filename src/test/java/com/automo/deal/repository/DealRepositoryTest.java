package com.automo.deal.repository;

import com.automo.deal.entity.Deal;
import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.lead.entity.Lead;
import com.automo.leadType.entity.LeadType;
import com.automo.promotion.entity.Promotion;
import com.automo.state.entity.State;
import com.automo.test.utils.TestDataFactory;
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
@DisplayName("Tests for DealRepository")
class DealRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DealRepository dealRepository;

    // Test data entities
    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private IdentifierType identifierType;
    private Identifier testIdentifier;
    private LeadType leadType;
    private Lead testLead;
    private Promotion testPromotion;
    private Deal testDeal;

    @BeforeEach
    void setUp() {
        // Setup states
        activeState = TestDataFactory.createActiveState();
        activeState = entityManager.persistAndFlush(activeState);

        inactiveState = TestDataFactory.createInactiveState();
        inactiveState = entityManager.persistAndFlush(inactiveState);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = entityManager.persistAndFlush(eliminatedState);

        // Setup identifier type
        identifierType = TestDataFactory.createNifIdentifierType();
        identifierType = entityManager.persistAndFlush(identifierType);

        // Setup identifier
        testIdentifier = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        testIdentifier = entityManager.persistAndFlush(testIdentifier);

        // Setup lead type
        leadType = TestDataFactory.createCallLeadType();
        leadType = entityManager.persistAndFlush(leadType);

        // Setup lead
        testLead = TestDataFactory.createValidLead(testIdentifier, leadType, activeState);
        testLead.setName("Test Lead");
        testLead = entityManager.persistAndFlush(testLead);

        // Setup promotion
        testPromotion = new Promotion();
        testPromotion.setName("Test Promotion");
        testPromotion.setDiscount(new BigDecimal("10.00"));
        testPromotion.setState(activeState);
        testPromotion = entityManager.persistAndFlush(testPromotion);

        // Setup test deal
        testDeal = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        testDeal.setPromotion(testPromotion);
        testDeal.setDeliveryDate(LocalDate.now().plusDays(30));
        testDeal = entityManager.persistAndFlush(testDeal);

        entityManager.clear();
    }

    @Test
    @DisplayName("Should find deal by id successfully")
    void shouldFindDealByIdSuccessfully() {
        Optional<Deal> found = dealRepository.findById(testDeal.getId());

        assertTrue(found.isPresent());
        Deal deal = found.get();
        assertEquals(testDeal.getTotal(), deal.getTotal());
        assertEquals(testDeal.getMessageCount(), deal.getMessageCount());
        assertEquals(testDeal.getDeliveryDate(), deal.getDeliveryDate());
    }

    @Test
    @DisplayName("Should save deal successfully")
    void shouldSaveDealSuccessfully() {
        // Given
        Deal newDeal = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        newDeal.setTotal(new BigDecimal("75000.00"));
        newDeal.setMessageCount(7);
        newDeal.setDeliveryDate(LocalDate.now().plusDays(45));

        // When
        Deal savedDeal = dealRepository.save(newDeal);

        // Then
        assertNotNull(savedDeal.getId());
        assertEquals(new BigDecimal("75000.00"), savedDeal.getTotal());
        assertEquals(7, savedDeal.getMessageCount());
        assertEquals(LocalDate.now().plusDays(45), savedDeal.getDeliveryDate());
        assertEquals(testIdentifier.getId(), savedDeal.getIdentifier().getId());
        assertEquals(testLead.getId(), savedDeal.getLead().getId());
        assertEquals(activeState.getId(), savedDeal.getState().getId());
    }

    @Test
    @DisplayName("Should update deal successfully")
    void shouldUpdateDealSuccessfully() {
        // Given
        testDeal.setTotal(new BigDecimal("55000.00"));
        testDeal.setMessageCount(10);
        testDeal.setDeliveryDate(LocalDate.now().plusDays(60));

        // When
        Deal updatedDeal = dealRepository.save(testDeal);

        // Then
        assertEquals(new BigDecimal("55000.00"), updatedDeal.getTotal());
        assertEquals(10, updatedDeal.getMessageCount());
        assertEquals(LocalDate.now().plusDays(60), updatedDeal.getDeliveryDate());
        assertEquals(testDeal.getId(), updatedDeal.getId());
    }

    @Test
    @DisplayName("Should delete deal successfully")
    void shouldDeleteDealSuccessfully() {
        Long dealId = testDeal.getId();

        dealRepository.delete(testDeal);
        entityManager.flush();

        Optional<Deal> deletedDeal = dealRepository.findById(dealId);
        assertFalse(deletedDeal.isPresent());
    }

    @Test
    @DisplayName("Should find all deals")
    void shouldFindAllDeals() {
        // Given - create additional deals
        Deal deal2 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        deal2.setTotal(new BigDecimal("35000.00"));
        deal2.setMessageCount(2);
        entityManager.persistAndFlush(deal2);

        Deal deal3 = TestDataFactory.createValidDeal(testIdentifier, testLead, inactiveState);
        deal3.setTotal(new BigDecimal("25000.00"));
        deal3.setMessageCount(1);
        entityManager.persistAndFlush(deal3);

        entityManager.clear();

        // When
        List<Deal> allDeals = dealRepository.findAll();

        // Then
        assertEquals(3, allDeals.size());
        assertTrue(allDeals.stream().anyMatch(d -> d.getTotal().equals(new BigDecimal("45000.00"))));
        assertTrue(allDeals.stream().anyMatch(d -> d.getTotal().equals(new BigDecimal("35000.00"))));
        assertTrue(allDeals.stream().anyMatch(d -> d.getTotal().equals(new BigDecimal("25000.00"))));
    }

    @Test
    @DisplayName("Should find all deals with relations")
    void shouldFindAllDealsWithRelations() {
        // Given - create additional deals
        Deal deal2 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        deal2.setTotal(new BigDecimal("65000.00"));
        deal2.setMessageCount(4);
        entityManager.persistAndFlush(deal2);

        entityManager.clear();

        // When
        List<Deal> deals = dealRepository.findAllWithRelations();

        // Then
        assertEquals(2, deals.size());
        
        for (Deal deal : deals) {
            assertNotNull(deal.getIdentifier());
            assertNotNull(deal.getLead());
            assertNotNull(deal.getState());
            // Promotion might be null for some deals, but should be loaded if present
            if (deal.getPromotion() != null) {
                assertNotNull(deal.getPromotion().getName());
            }
        }
    }

    @Test
    @DisplayName("Should find deal by id with relations")
    void shouldFindDealByIdWithRelations() {
        Optional<Deal> found = dealRepository.findByIdWithRelations(testDeal.getId());

        assertTrue(found.isPresent());
        Deal deal = found.get();
        
        // Verify all relations are loaded
        assertNotNull(deal.getIdentifier());
        assertNotNull(deal.getLead());
        assertNotNull(deal.getPromotion());
        assertNotNull(deal.getState());
        
        assertEquals("Test Lead", deal.getLead().getName());
        assertEquals("Test Promotion", deal.getPromotion().getName());
        assertEquals("ACTIVE", deal.getState().getState());
    }

    @Test
    @DisplayName("Should find deals by state id with relations")
    void shouldFindDealsByStateIdWithRelations() {
        // Given - create deals with different states
        Deal activeDeal2 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        activeDeal2.setTotal(new BigDecimal("30000.00"));
        activeDeal2.setMessageCount(2);
        entityManager.persistAndFlush(activeDeal2);

        Deal inactiveDeal = TestDataFactory.createValidDeal(testIdentifier, testLead, inactiveState);
        inactiveDeal.setTotal(new BigDecimal("20000.00"));
        inactiveDeal.setMessageCount(1);
        entityManager.persistAndFlush(inactiveDeal);

        entityManager.clear();

        // When
        List<Deal> activeDeals = dealRepository.findByStateIdWithRelations(activeState.getId());
        List<Deal> inactiveDeals = dealRepository.findByStateIdWithRelations(inactiveState.getId());

        // Then
        assertEquals(2, activeDeals.size());
        assertEquals(1, inactiveDeals.size());

        // Verify relations are loaded
        for (Deal deal : activeDeals) {
            assertNotNull(deal.getState());
            assertEquals(activeState.getId(), deal.getState().getId());
            assertNotNull(deal.getIdentifier());
            assertNotNull(deal.getLead());
        }

        Deal inactiveDealFound = inactiveDeals.get(0);
        assertEquals(inactiveState.getId(), inactiveDealFound.getState().getId());
        assertEquals(new BigDecimal("20000.00"), inactiveDealFound.getTotal());
    }

    @Test
    @DisplayName("Should find deals by identifier id with relations")
    void shouldFindDealsByIdentifierIdWithRelations() {
        // Given - create another identifier and deals
        Identifier anotherIdentifier = TestDataFactory.createValidIdentifier(2L, identifierType, activeState);
        anotherIdentifier = entityManager.persistAndFlush(anotherIdentifier);

        Deal dealWithAnotherIdentifier = TestDataFactory.createValidDeal(anotherIdentifier, testLead, activeState);
        dealWithAnotherIdentifier.setTotal(new BigDecimal("40000.00"));
        dealWithAnotherIdentifier.setMessageCount(3);
        entityManager.persistAndFlush(dealWithAnotherIdentifier);

        entityManager.clear();

        // When
        List<Deal> dealsForFirstIdentifier = dealRepository.findByIdentifierIdWithRelations(testIdentifier.getId());
        List<Deal> dealsForSecondIdentifier = dealRepository.findByIdentifierIdWithRelations(anotherIdentifier.getId());

        // Then
        assertEquals(1, dealsForFirstIdentifier.size());
        assertEquals(1, dealsForSecondIdentifier.size());

        Deal firstDeal = dealsForFirstIdentifier.get(0);
        assertEquals(testIdentifier.getId(), firstDeal.getIdentifier().getId());
        assertEquals(new BigDecimal("45000.00"), firstDeal.getTotal());

        Deal secondDeal = dealsForSecondIdentifier.get(0);
        assertEquals(anotherIdentifier.getId(), secondDeal.getIdentifier().getId());
        assertEquals(new BigDecimal("40000.00"), secondDeal.getTotal());
    }

    @Test
    @DisplayName("Should find deals by lead id with relations")
    void shouldFindDealsByLeadIdWithRelations() {
        // Given - create another lead and deals
        Lead anotherLead = TestDataFactory.createValidLead(testIdentifier, leadType, activeState);
        anotherLead.setName("Another Lead");
        anotherLead.setContactValue("987654321");
        anotherLead = entityManager.persistAndFlush(anotherLead);

        Deal dealWithAnotherLead = TestDataFactory.createValidDeal(testIdentifier, anotherLead, activeState);
        dealWithAnotherLead.setTotal(new BigDecimal("60000.00"));
        dealWithAnotherLead.setMessageCount(6);
        entityManager.persistAndFlush(dealWithAnotherLead);

        entityManager.clear();

        // When
        List<Deal> dealsForFirstLead = dealRepository.findByLeadIdWithRelations(testLead.getId());
        List<Deal> dealsForSecondLead = dealRepository.findByLeadIdWithRelations(anotherLead.getId());

        // Then
        assertEquals(1, dealsForFirstLead.size());
        assertEquals(1, dealsForSecondLead.size());

        Deal firstDeal = dealsForFirstLead.get(0);
        assertEquals(testLead.getId(), firstDeal.getLead().getId());
        assertEquals("Test Lead", firstDeal.getLead().getName());

        Deal secondDeal = dealsForSecondLead.get(0);
        assertEquals(anotherLead.getId(), secondDeal.getLead().getId());
        assertEquals("Another Lead", secondDeal.getLead().getName());
    }

    @Test
    @DisplayName("Should find deals by promotion id with relations")
    void shouldFindDealsByPromotionIdWithRelations() {
        // Given - create another promotion and deals
        Promotion anotherPromotion = new Promotion();
        anotherPromotion.setName("Another Promotion");
        anotherPromotion.setDiscount(new BigDecimal("15.00"));
        anotherPromotion.setState(activeState);
        anotherPromotion = entityManager.persistAndFlush(anotherPromotion);

        Deal dealWithAnotherPromotion = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        dealWithAnotherPromotion.setPromotion(anotherPromotion);
        dealWithAnotherPromotion.setTotal(new BigDecimal("70000.00"));
        dealWithAnotherPromotion.setMessageCount(8);
        entityManager.persistAndFlush(dealWithAnotherPromotion);

        entityManager.clear();

        // When
        List<Deal> dealsForFirstPromotion = dealRepository.findByPromotionIdWithRelations(testPromotion.getId());
        List<Deal> dealsForSecondPromotion = dealRepository.findByPromotionIdWithRelations(anotherPromotion.getId());

        // Then
        assertEquals(1, dealsForFirstPromotion.size());
        assertEquals(1, dealsForSecondPromotion.size());

        Deal firstDeal = dealsForFirstPromotion.get(0);
        assertEquals(testPromotion.getId(), firstDeal.getPromotion().getId());
        assertEquals("Test Promotion", firstDeal.getPromotion().getName());

        Deal secondDeal = dealsForSecondPromotion.get(0);
        assertEquals(anotherPromotion.getId(), secondDeal.getPromotion().getId());
        assertEquals("Another Promotion", secondDeal.getPromotion().getName());
    }

    @Test
    @DisplayName("Should find deals by state id using simple query")
    void shouldFindDealsByStateIdUsingSimpleQuery() {
        // Given - create deals with different states
        Deal inactiveDeal = TestDataFactory.createValidDeal(testIdentifier, testLead, inactiveState);
        inactiveDeal.setTotal(new BigDecimal("35000.00"));
        entityManager.persistAndFlush(inactiveDeal);

        entityManager.clear();

        // When
        List<Deal> activeDeals = dealRepository.findByStateId(activeState.getId());
        List<Deal> inactiveDeals = dealRepository.findByStateId(inactiveState.getId());

        // Then
        assertEquals(1, activeDeals.size());
        assertEquals(1, inactiveDeals.size());

        assertEquals(testDeal.getId(), activeDeals.get(0).getId());
        assertEquals(inactiveDeal.getId(), inactiveDeals.get(0).getId());
    }

    @Test
    @DisplayName("Should find deals by identifier id using simple query")
    void shouldFindDealsByIdentifierIdUsingSimpleQuery() {
        List<Deal> deals = dealRepository.findByIdentifierId(testIdentifier.getId());

        assertEquals(1, deals.size());
        assertEquals(testDeal.getId(), deals.get(0).getId());
        assertEquals(testIdentifier.getId(), deals.get(0).getIdentifier().getId());
    }

    @Test
    @DisplayName("Should find deals by lead id using simple query")
    void shouldFindDealsByLeadIdUsingSimpleQuery() {
        List<Deal> deals = dealRepository.findByLeadId(testLead.getId());

        assertEquals(1, deals.size());
        assertEquals(testDeal.getId(), deals.get(0).getId());
        assertEquals(testLead.getId(), deals.get(0).getLead().getId());
    }

    @Test
    @DisplayName("Should find deals by promotion id using simple query")
    void shouldFindDealsByPromotionIdUsingSimpleQuery() {
        List<Deal> deals = dealRepository.findByPromotionId(testPromotion.getId());

        assertEquals(1, deals.size());
        assertEquals(testDeal.getId(), deals.get(0).getId());
        assertEquals(testPromotion.getId(), deals.get(0).getPromotion().getId());
    }

    @Test
    @DisplayName("Should calculate average messages per deal by agent")
    void shouldCalculateAverageMessagesPerDealByAgent() {
        // Given - create deals with different message counts for the same agent
        Deal deal2 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        deal2.setMessageCount(5);
        entityManager.persistAndFlush(deal2);

        Deal deal3 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        deal3.setMessageCount(7);
        entityManager.persistAndFlush(deal3);

        entityManager.clear();

        // When
        Double averageMessages = dealRepository.calculateAverageMessagesPerDealByAgent(1L);

        // Then
        assertNotNull(averageMessages);
        // Average of 3, 5, and 7 should be 5.0
        assertEquals(5.0, averageMessages, 0.01);
    }

    @Test
    @DisplayName("Should count deals closed by agent")
    void shouldCountDealsClosedByAgent() {
        // Given - create additional deals for the same agent
        Deal deal2 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        deal2.setMessageCount(4);
        entityManager.persistAndFlush(deal2);

        Deal deal3 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        deal3.setMessageCount(2);
        entityManager.persistAndFlush(deal3);

        // Create eliminated deal (should not be counted)
        Deal eliminatedDeal = TestDataFactory.createValidDeal(testIdentifier, testLead, eliminatedState);
        eliminatedDeal.setMessageCount(1);
        entityManager.persistAndFlush(eliminatedDeal);

        entityManager.clear();

        // When
        long dealCount = dealRepository.countDealsClosedByAgent(1L);

        // Then
        assertEquals(3, dealCount); // Only non-eliminated deals
    }

    @Test
    @DisplayName("Should calculate global average messages per deal")
    void shouldCalculateGlobalAverageMessagesPerDeal() {
        // Given - create deals with different message counts
        Deal deal2 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        deal2.setMessageCount(6);
        entityManager.persistAndFlush(deal2);

        Deal deal3 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        deal3.setMessageCount(9);
        entityManager.persistAndFlush(deal3);

        // Create eliminated deal (should not be counted)
        Deal eliminatedDeal = TestDataFactory.createValidDeal(testIdentifier, testLead, eliminatedState);
        eliminatedDeal.setMessageCount(100);
        entityManager.persistAndFlush(eliminatedDeal);

        entityManager.clear();

        // When
        Double globalAverage = dealRepository.calculateGlobalAverageMessagesPerDeal();

        // Then
        assertNotNull(globalAverage);
        // Average of 3, 6, and 9 (excluding eliminated) should be 6.0
        assertEquals(6.0, globalAverage, 0.01);
    }

    @Test
    @DisplayName("Should return null when calculating average for non-existent agent")
    void shouldReturnNullWhenCalculatingAverageForNonExistentAgent() {
        Double averageMessages = dealRepository.calculateAverageMessagesPerDealByAgent(999L);
        assertNull(averageMessages);
    }

    @Test
    @DisplayName("Should return zero when counting deals for non-existent agent")
    void shouldReturnZeroWhenCountingDealsForNonExistentAgent() {
        long dealCount = dealRepository.countDealsClosedByAgent(999L);
        assertEquals(0, dealCount);
    }

    @Test
    @DisplayName("Should return empty lists when no deals exist for filters")
    void shouldReturnEmptyListsWhenNoDealsExistForFilters() {
        // Test with non-existent IDs
        List<Deal> dealsByState = dealRepository.findByStateIdWithRelations(999L);
        List<Deal> dealsByIdentifier = dealRepository.findByIdentifierIdWithRelations(999L);
        List<Deal> dealsByLead = dealRepository.findByLeadIdWithRelations(999L);
        List<Deal> dealsByPromotion = dealRepository.findByPromotionIdWithRelations(999L);

        assertTrue(dealsByState.isEmpty());
        assertTrue(dealsByIdentifier.isEmpty());
        assertTrue(dealsByLead.isEmpty());
        assertTrue(dealsByPromotion.isEmpty());
    }

    @Test
    @DisplayName("Should handle deals without promotion")
    void shouldHandleDealsWithoutPromotion() {
        // Given - create deal without promotion
        Deal dealWithoutPromotion = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        dealWithoutPromotion.setPromotion(null);
        dealWithoutPromotion.setTotal(new BigDecimal("25000.00"));
        dealWithoutPromotion.setMessageCount(2);
        dealWithoutPromotion = entityManager.persistAndFlush(dealWithoutPromotion);

        entityManager.clear();

        // When
        Optional<Deal> found = dealRepository.findByIdWithRelations(dealWithoutPromotion.getId());

        // Then
        assertTrue(found.isPresent());
        Deal deal = found.get();
        assertNull(deal.getPromotion());
        assertNotNull(deal.getIdentifier());
        assertNotNull(deal.getLead());
        assertNotNull(deal.getState());
    }

    @Test
    @DisplayName("Should handle deals without delivery date")
    void shouldHandleDealsWithoutDeliveryDate() {
        // Given
        Deal dealWithoutDeliveryDate = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        dealWithoutDeliveryDate.setDeliveryDate(null);
        dealWithoutDeliveryDate.setTotal(new BigDecimal("30000.00"));
        dealWithoutDeliveryDate.setMessageCount(4);
        dealWithoutDeliveryDate = entityManager.persistAndFlush(dealWithoutDeliveryDate);

        entityManager.clear();

        // When
        Optional<Deal> found = dealRepository.findById(dealWithoutDeliveryDate.getId());

        // Then
        assertTrue(found.isPresent());
        Deal deal = found.get();
        assertNull(deal.getDeliveryDate());
        assertEquals(new BigDecimal("30000.00"), deal.getTotal());
        assertEquals(4, deal.getMessageCount());
    }

    @Test
    @DisplayName("Should persist and retrieve timestamps correctly")
    void shouldPersistAndRetrieveTimestampsCorrectly() {
        Optional<Deal> foundDeal = dealRepository.findById(testDeal.getId());

        assertTrue(foundDeal.isPresent());
        Deal deal = foundDeal.get();

        assertNotNull(deal.getCreatedAt());
        assertNotNull(deal.getUpdatedAt());

        // Update the deal to test updatedAt
        deal.setTotal(new BigDecimal("55000.00"));
        Deal savedDeal = dealRepository.save(deal);

        assertNotNull(savedDeal.getUpdatedAt());
        assertTrue(savedDeal.getUpdatedAt().isAfter(savedDeal.getCreatedAt()) ||
                   savedDeal.getUpdatedAt().isEqual(savedDeal.getCreatedAt()));
    }

    @Test
    @DisplayName("Should handle large total amounts")
    void shouldHandleLargeTotalAmounts() {
        // Given
        Deal dealWithLargeAmount = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        dealWithLargeAmount.setTotal(new BigDecimal("999999999.99"));
        dealWithLargeAmount.setMessageCount(1);

        // When
        Deal savedDeal = dealRepository.save(dealWithLargeAmount);

        // Then
        assertNotNull(savedDeal.getId());
        assertEquals(new BigDecimal("999999999.99"), savedDeal.getTotal());
    }

    @Test
    @DisplayName("Should handle high message counts")
    void shouldHandleHighMessageCounts() {
        // Given
        Deal dealWithHighMessageCount = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        dealWithHighMessageCount.setTotal(new BigDecimal("50000.00"));
        dealWithHighMessageCount.setMessageCount(999999);

        // When
        Deal savedDeal = dealRepository.save(dealWithHighMessageCount);

        // Then
        assertNotNull(savedDeal.getId());
        assertEquals(999999, savedDeal.getMessageCount());
    }

    @Test
    @DisplayName("Should handle fractional total amounts")
    void shouldHandleFractionalTotalAmounts() {
        // Given
        Deal dealWithFractionalAmount = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        dealWithFractionalAmount.setTotal(new BigDecimal("12345.678"));
        dealWithFractionalAmount.setMessageCount(3);

        // When
        Deal savedDeal = dealRepository.save(dealWithFractionalAmount);

        // Then
        assertNotNull(savedDeal.getId());
        assertEquals(new BigDecimal("12345.678"), savedDeal.getTotal());
    }

    @Test
    @DisplayName("Should handle past and future delivery dates")
    void shouldHandlePastAndFutureDeliveryDates() {
        // Given
        Deal dealWithPastDate = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        dealWithPastDate.setDeliveryDate(LocalDate.now().minusYears(1));
        dealWithPastDate.setTotal(new BigDecimal("40000.00"));
        dealWithPastDate.setMessageCount(2);
        
        Deal dealWithFutureDate = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        dealWithFutureDate.setDeliveryDate(LocalDate.now().plusYears(5));
        dealWithFutureDate.setTotal(new BigDecimal("60000.00"));
        dealWithFutureDate.setMessageCount(4);

        // When
        Deal savedPastDeal = dealRepository.save(dealWithPastDate);
        Deal savedFutureDeal = dealRepository.save(dealWithFutureDate);

        // Then
        assertEquals(LocalDate.now().minusYears(1), savedPastDeal.getDeliveryDate());
        assertEquals(LocalDate.now().plusYears(5), savedFutureDeal.getDeliveryDate());
    }
}