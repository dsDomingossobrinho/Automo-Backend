package com.automo.deal.service;

import com.automo.deal.dto.DealDto;
import com.automo.deal.entity.Deal;
import com.automo.deal.repository.DealRepository;
import com.automo.deal.response.DealResponse;
import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.lead.entity.Lead;
import com.automo.lead.service.LeadService;
import com.automo.leadType.entity.LeadType;
import com.automo.promotion.entity.Promotion;
import com.automo.promotion.service.PromotionService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.utils.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for DealServiceImpl")
class DealServiceImplTest {

    @Mock
    private DealRepository dealRepository;

    @Mock
    private LeadService leadService;

    @Mock
    private PromotionService promotionService;

    @Mock
    private StateService stateService;

    @InjectMocks
    private DealServiceImpl dealService;

    // Test data objects
    private Deal testDeal;
    private State activeState;
    private State eliminatedState;
    private IdentifierType identifierType;
    private Identifier testIdentifier;
    private LeadType leadType;
    private Lead testLead;
    private Promotion testPromotion;

    @BeforeEach
    void setUp() {
        // Setup basic entities
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);

        identifierType = TestDataFactory.createNifIdentifierType();
        identifierType.setId(1L);

        testIdentifier = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        testIdentifier.setId(1L);

        leadType = TestDataFactory.createCallLeadType();
        leadType.setId(1L);

        testLead = TestDataFactory.createValidLead(testIdentifier, leadType, activeState);
        testLead.setId(1L);
        testLead.setName("Test Lead");

        testPromotion = new Promotion();
        testPromotion.setId(1L);
        testPromotion.setName("Test Promotion");
        testPromotion.setDiscount(new BigDecimal("10.00"));

        testDeal = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        testDeal.setId(1L);
        testDeal.setTotal(new BigDecimal("45000.00"));
        testDeal.setMessageCount(3);
        testDeal.setDeliveryDate(LocalDate.now().plusDays(30));
    }

    @Test
    @DisplayName("Should create deal successfully")
    void shouldCreateDealSuccessfully() {
        // Given
        DealDto dealDto = new DealDto(
            1L, // identifierId
            1L, // leadId
            1L, // promotionId
            new BigDecimal("50000.00"),
            LocalDate.now().plusDays(30),
            5,
            1L  // stateId
        );

        when(stateService.findById(1L)).thenReturn(activeState);
        when(leadService.findById(1L)).thenReturn(testLead);
        when(promotionService.findById(1L)).thenReturn(testPromotion);
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        // When
        DealResponse result = dealService.createDeal(dealDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(new BigDecimal("45000.00"), result.total());
        assertEquals(3, result.messageCount());
        assertEquals("Test Lead", result.leadName());

        verify(stateService).findById(1L);
        verify(leadService).findById(1L);
        verify(promotionService).findById(1L);
        verify(dealRepository).save(any(Deal.class));
    }

    @Test
    @DisplayName("Should create deal without optional fields")
    void shouldCreateDealWithoutOptionalFields() {
        // Given
        DealDto dealDto = new DealDto(
            1L, // identifierId
            null, // leadId - optional
            null, // promotionId - optional
            new BigDecimal("30000.00"),
            null, // deliveryDate - optional
            2,
            1L  // stateId
        );

        when(stateService.findById(1L)).thenReturn(activeState);
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        // When
        DealResponse result = dealService.createDeal(dealDto);

        // Then
        assertNotNull(result);
        verify(stateService).findById(1L);
        verify(leadService, never()).findById(anyLong());
        verify(promotionService, never()).findById(anyLong());
        verify(dealRepository).save(any(Deal.class));
    }

    @Test
    @DisplayName("Should update deal successfully")
    void shouldUpdateDealSuccessfully() {
        // Given
        Long dealId = 1L;
        DealDto dealDto = new DealDto(
            1L,
            1L,
            1L,
            new BigDecimal("55000.00"),
            LocalDate.now().plusDays(45),
            7,
            1L
        );

        when(dealRepository.findById(dealId)).thenReturn(Optional.of(testDeal));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(leadService.findById(1L)).thenReturn(testLead);
        when(promotionService.findById(1L)).thenReturn(testPromotion);
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        // When
        DealResponse result = dealService.updateDeal(dealId, dealDto);

        // Then
        assertNotNull(result);
        verify(dealRepository).findById(dealId);
        verify(stateService).findById(1L);
        verify(leadService).findById(1L);
        verify(promotionService).findById(1L);
        verify(dealRepository).save(testDeal);
    }

    @Test
    @DisplayName("Should update deal removing optional associations")
    void shouldUpdateDealRemovingOptionalAssociations() {
        // Given
        Long dealId = 1L;
        DealDto dealDto = new DealDto(
            1L,
            null, // Remove lead
            null, // Remove promotion
            new BigDecimal("40000.00"),
            LocalDate.now().plusDays(15),
            4,
            1L
        );

        when(dealRepository.findById(dealId)).thenReturn(Optional.of(testDeal));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        // When
        DealResponse result = dealService.updateDeal(dealId, dealDto);

        // Then
        assertNotNull(result);
        verify(dealRepository).findById(dealId);
        verify(stateService).findById(1L);
        verify(leadService, never()).findById(anyLong());
        verify(promotionService, never()).findById(anyLong());
        verify(dealRepository).save(testDeal);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing deal")
    void shouldThrowExceptionWhenUpdatingNonExistingDeal() {
        // Given
        Long dealId = 999L;
        DealDto dealDto = new DealDto(1L, 1L, 1L, new BigDecimal("50000.00"), null, 5, 1L);

        when(dealRepository.findById(dealId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> dealService.updateDeal(dealId, dealDto));

        verify(dealRepository).findById(dealId);
        verify(dealRepository, never()).save(any(Deal.class));
    }

    @Test
    @DisplayName("Should get all deals excluding eliminated")
    void shouldGetAllDealsExcludingEliminated() {
        // Given
        Deal deal1 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        deal1.setId(1L);
        Deal deal2 = TestDataFactory.createValidDeal(testIdentifier, testLead, activeState);
        deal2.setId(2L);
        Deal eliminatedDeal = TestDataFactory.createValidDeal(testIdentifier, testLead, eliminatedState);
        eliminatedDeal.setId(3L);

        List<Deal> allDeals = Arrays.asList(deal1, deal2, eliminatedDeal);

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(dealRepository.findAllWithRelations()).thenReturn(allDeals);

        // When
        List<DealResponse> result = dealService.getAllDeals();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().noneMatch(d -> d.stateId().equals(eliminatedState.getId())));

        verify(stateService).getEliminatedState();
        verify(dealRepository).findAllWithRelations();
    }

    @Test
    @DisplayName("Should get deal by id successfully")
    void shouldGetDealByIdSuccessfully() {
        // Given
        Long dealId = 1L;
        when(dealRepository.findById(dealId)).thenReturn(Optional.of(testDeal));

        // When
        Deal result = dealService.getDealById(dealId);

        // Then
        assertNotNull(result);
        assertEquals(testDeal.getId(), result.getId());
        assertEquals(testDeal.getTotal(), result.getTotal());

        verify(dealRepository).findById(dealId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existing deal")
    void shouldThrowExceptionWhenGettingNonExistingDeal() {
        // Given
        Long dealId = 999L;
        when(dealRepository.findById(dealId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> dealService.getDealById(dealId));

        verify(dealRepository).findById(dealId);
    }

    @Test
    @DisplayName("Should get deal by id response successfully")
    void shouldGetDealByIdResponseSuccessfully() {
        // Given
        Long dealId = 1L;
        when(dealRepository.findByIdWithRelations(dealId)).thenReturn(Optional.of(testDeal));

        // When
        DealResponse result = dealService.getDealByIdResponse(dealId);

        // Then
        assertNotNull(result);
        assertEquals(testDeal.getId(), result.id());
        assertEquals(testDeal.getTotal(), result.total());
        assertEquals(testDeal.getMessageCount(), result.messageCount());

        verify(dealRepository).findByIdWithRelations(dealId);
    }

    @Test
    @DisplayName("Should get deals by state successfully")
    void shouldGetDealsByStateSuccessfully() {
        // Given
        Long stateId = 1L;
        List<Deal> deals = Arrays.asList(testDeal);

        when(dealRepository.findByStateIdWithRelations(stateId)).thenReturn(deals);

        // When
        List<DealResponse> result = dealService.getDealsByState(stateId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDeal.getId(), result.get(0).id());

        verify(dealRepository).findByStateIdWithRelations(stateId);
    }

    @Test
    @DisplayName("Should get deals by identifier successfully")
    void shouldGetDealsByIdentifierSuccessfully() {
        // Given
        Long identifierId = 1L;
        List<Deal> deals = Arrays.asList(testDeal);

        when(dealRepository.findByIdentifierIdWithRelations(identifierId)).thenReturn(deals);

        // When
        List<DealResponse> result = dealService.getDealsByIdentifier(identifierId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDeal.getId(), result.get(0).id());

        verify(dealRepository).findByIdentifierIdWithRelations(identifierId);
    }

    @Test
    @DisplayName("Should get deals by lead successfully")
    void shouldGetDealsByLeadSuccessfully() {
        // Given
        Long leadId = 1L;
        List<Deal> deals = Arrays.asList(testDeal);

        when(dealRepository.findByLeadIdWithRelations(leadId)).thenReturn(deals);

        // When
        List<DealResponse> result = dealService.getDealsByLead(leadId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDeal.getId(), result.get(0).id());

        verify(dealRepository).findByLeadIdWithRelations(leadId);
    }

    @Test
    @DisplayName("Should get deals by promotion successfully")
    void shouldGetDealsByPromotionSuccessfully() {
        // Given
        Long promotionId = 1L;
        List<Deal> deals = Arrays.asList(testDeal);

        when(dealRepository.findByPromotionIdWithRelations(promotionId)).thenReturn(deals);

        // When
        List<DealResponse> result = dealService.getDealsByPromotion(promotionId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDeal.getId(), result.get(0).id());

        verify(dealRepository).findByPromotionIdWithRelations(promotionId);
    }

    @Test
    @DisplayName("Should soft delete deal successfully")
    void shouldSoftDeleteDealSuccessfully() {
        // Given
        Long dealId = 1L;

        when(dealRepository.findById(dealId)).thenReturn(Optional.of(testDeal));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        // When
        dealService.deleteDeal(dealId);

        // Then
        verify(dealRepository).findById(dealId);
        verify(stateService).getEliminatedState();
        verify(dealRepository).save(testDeal);
        assertEquals(eliminatedState, testDeal.getState());
    }

    @Test
    @DisplayName("Should implement findById method correctly")
    void shouldImplementFindByIdMethodCorrectly() {
        // Given
        Long dealId = 1L;
        when(dealRepository.findById(dealId)).thenReturn(Optional.of(testDeal));

        // When
        Deal result = dealService.findById(dealId);

        // Then
        assertNotNull(result);
        assertEquals(testDeal, result);
        verify(dealRepository).findById(dealId);
    }

    @Test
    @DisplayName("Should throw exception in findById when deal not found")
    void shouldThrowExceptionInFindByIdWhenDealNotFound() {
        // Given
        Long dealId = 999L;
        when(dealRepository.findById(dealId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> dealService.findById(dealId));

        verify(dealRepository).findById(dealId);
    }

    @Test
    @DisplayName("Should implement findByIdAndStateId method correctly")
    void shouldImplementFindByIdAndStateIdMethodCorrectly() {
        // Given
        Long dealId = 1L;
        Long stateId = 1L;

        when(dealRepository.findById(dealId)).thenReturn(Optional.of(testDeal));

        // When
        Deal result = dealService.findByIdAndStateId(dealId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testDeal, result);
        verify(dealRepository).findById(dealId);
    }

    @Test
    @DisplayName("Should use default state in findByIdAndStateId when stateId is null")
    void shouldUseDefaultStateInFindByIdAndStateIdWhenStateIdIsNull() {
        // Given
        Long dealId = 1L;
        Long stateId = null;

        when(dealRepository.findById(dealId)).thenReturn(Optional.of(testDeal));

        // When
        Deal result = dealService.findByIdAndStateId(dealId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testDeal, result);
        verify(dealRepository).findById(dealId);
    }

    @Test
    @DisplayName("Should throw exception in findByIdAndStateId when states don't match")
    void shouldThrowExceptionInFindByIdAndStateIdWhenStatesDontMatch() {
        // Given
        Long dealId = 1L;
        Long stateId = 2L; // Different from deal's state (1L)

        when(dealRepository.findById(dealId)).thenReturn(Optional.of(testDeal));

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> dealService.findByIdAndStateId(dealId, stateId));

        verify(dealRepository).findById(dealId);
    }

    @Test
    @DisplayName("Should return empty list when no deals exist")
    void shouldReturnEmptyListWhenNoDealsExist() {
        // Given
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(dealRepository.findAllWithRelations()).thenReturn(Collections.emptyList());

        // When
        List<DealResponse> result = dealService.getAllDeals();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(stateService).getEliminatedState();
        verify(dealRepository).findAllWithRelations();
    }

    @Test
    @DisplayName("Should return empty list when no deals exist for specific state")
    void shouldReturnEmptyListWhenNoDealsExistForSpecificState() {
        // Given
        Long stateId = 999L;
        when(dealRepository.findByStateIdWithRelations(stateId)).thenReturn(Collections.emptyList());

        // When
        List<DealResponse> result = dealService.getDealsByState(stateId);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(dealRepository).findByStateIdWithRelations(stateId);
    }

    @Test
    @DisplayName("Should handle mapping deal with null optional fields")
    void shouldHandleMappingDealWithNullOptionalFields() {
        // Given
        Deal dealWithNulls = TestDataFactory.createValidDeal(testIdentifier, null, activeState);
        dealWithNulls.setId(1L);
        dealWithNulls.setLead(null);
        dealWithNulls.setPromotion(null);
        dealWithNulls.setDeliveryDate(null);

        when(dealRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(dealWithNulls));

        // When
        DealResponse result = dealService.getDealByIdResponse(1L);

        // Then
        assertNotNull(result);
        assertNull(result.leadId());
        assertNull(result.leadName());
        assertNull(result.promotionId());
        assertNull(result.promotionName());
        assertNull(result.deliveryDate());

        verify(dealRepository).findByIdWithRelations(1L);
    }

    @Test
    @DisplayName("Should handle large total amounts")
    void shouldHandleLargeTotalAmounts() {
        // Given
        DealDto dealDto = new DealDto(
            1L,
            1L,
            null,
            new BigDecimal("999999999.99"),
            LocalDate.now().plusDays(30),
            1,
            1L
        );

        testDeal.setTotal(new BigDecimal("999999999.99"));

        when(stateService.findById(1L)).thenReturn(activeState);
        when(leadService.findById(1L)).thenReturn(testLead);
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        // When
        DealResponse result = dealService.createDeal(dealDto);

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("999999999.99"), result.total());

        verify(stateService).findById(1L);
        verify(leadService).findById(1L);
        verify(dealRepository).save(any(Deal.class));
    }

    @Test
    @DisplayName("Should handle high message counts")
    void shouldHandleHighMessageCounts() {
        // Given
        DealDto dealDto = new DealDto(
            1L,
            1L,
            null,
            new BigDecimal("50000.00"),
            LocalDate.now().plusDays(30),
            999999,
            1L
        );

        testDeal.setMessageCount(999999);

        when(stateService.findById(1L)).thenReturn(activeState);
        when(leadService.findById(1L)).thenReturn(testLead);
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        // When
        DealResponse result = dealService.createDeal(dealDto);

        // Then
        assertNotNull(result);
        assertEquals(999999, result.messageCount());

        verify(stateService).findById(1L);
        verify(leadService).findById(1L);
        verify(dealRepository).save(any(Deal.class));
    }

    @Test
    @DisplayName("Should handle future delivery dates")
    void shouldHandleFutureDeliveryDates() {
        // Given
        LocalDate futureDate = LocalDate.now().plusYears(5);
        DealDto dealDto = new DealDto(
            1L,
            1L,
            null,
            new BigDecimal("50000.00"),
            futureDate,
            5,
            1L
        );

        testDeal.setDeliveryDate(futureDate);

        when(stateService.findById(1L)).thenReturn(activeState);
        when(leadService.findById(1L)).thenReturn(testLead);
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        // When
        DealResponse result = dealService.createDeal(dealDto);

        // Then
        assertNotNull(result);
        assertEquals(futureDate, result.deliveryDate());

        verify(stateService).findById(1L);
        verify(leadService).findById(1L);
        verify(dealRepository).save(any(Deal.class));
    }

    @Test
    @DisplayName("Should handle past delivery dates")
    void shouldHandlePastDeliveryDates() {
        // Given
        LocalDate pastDate = LocalDate.now().minusYears(1);
        DealDto dealDto = new DealDto(
            1L,
            1L,
            null,
            new BigDecimal("50000.00"),
            pastDate,
            3,
            1L
        );

        testDeal.setDeliveryDate(pastDate);

        when(stateService.findById(1L)).thenReturn(activeState);
        when(leadService.findById(1L)).thenReturn(testLead);
        when(dealRepository.save(any(Deal.class))).thenReturn(testDeal);

        // When
        DealResponse result = dealService.createDeal(dealDto);

        // Then
        assertNotNull(result);
        assertEquals(pastDate, result.deliveryDate());

        verify(stateService).findById(1L);
        verify(leadService).findById(1L);
        verify(dealRepository).save(any(Deal.class));
    }
}