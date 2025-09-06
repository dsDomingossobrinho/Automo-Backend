package com.automo.promotion.service;

import com.automo.promotion.dto.PromotionDto;
import com.automo.promotion.entity.Promotion;
import com.automo.promotion.repository.PromotionRepository;
import com.automo.promotion.response.PromotionResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.utils.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for PromotionServiceImpl")
class PromotionServiceImplTest {

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private StateService stateService;

    @InjectMocks
    private PromotionServiceImpl promotionService;

    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private Promotion promotion;
    private PromotionDto promotionDto;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        inactiveState = TestDataFactory.createInactiveState();
        inactiveState.setId(2L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(3L);

        promotion = TestDataFactory.createValidPromotion(activeState);
        promotion.setId(1L);
        promotion.setCreatedAt(LocalDateTime.now());
        promotion.setUpdatedAt(LocalDateTime.now());

        promotionDto = TestDataFactory.createValidPromotionDto(activeState.getId());
    }

    @Test
    @DisplayName("Should create Promotion successfully")
    void shouldCreatePromotionSuccessfully() {
        // Given
        when(stateService.findById(activeState.getId())).thenReturn(activeState);
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);

        // When
        PromotionResponse response = promotionService.createPromotion(promotionDto);

        // Then
        assertNotNull(response);
        assertEquals(promotion.getId(), response.id());
        assertEquals(promotion.getName(), response.name());
        assertEquals(promotion.getDiscountValue(), response.discountValue());
        assertEquals(promotion.getCode(), response.code());
        assertEquals(activeState.getId(), response.stateId());
        assertEquals(activeState.getState(), response.stateName());

        verify(stateService).findById(activeState.getId());
        verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Should throw exception when state not found during creation")
    void shouldThrowExceptionWhenStateNotFoundDuringCreation() {
        // Given
        when(stateService.findById(activeState.getId())).thenThrow(new EntityNotFoundException("State not found"));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            promotionService.createPromotion(promotionDto);
        });

        verify(stateService).findById(activeState.getId());
        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Should update Promotion successfully")
    void shouldUpdatePromotionSuccessfully() {
        // Given
        PromotionDto updateDto = new PromotionDto("Updated Promotion", new BigDecimal("30.00"), "UPDATED30", activeState.getId());
        
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
        when(stateService.findById(activeState.getId())).thenReturn(activeState);
        
        Promotion updatedPromotion = TestDataFactory.createValidPromotion(activeState);
        updatedPromotion.setId(1L);
        updatedPromotion.setName("Updated Promotion");
        updatedPromotion.setDiscountValue(new BigDecimal("30.00"));
        updatedPromotion.setCode("UPDATED30");
        
        when(promotionRepository.save(any(Promotion.class))).thenReturn(updatedPromotion);

        // When
        PromotionResponse response = promotionService.updatePromotion(1L, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Updated Promotion", response.name());
        assertEquals(new BigDecimal("30.00"), response.discountValue());
        assertEquals("UPDATED30", response.code());

        verify(promotionRepository).findById(1L);
        verify(stateService).findById(activeState.getId());
        verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Should throw exception when Promotion not found during update")
    void shouldThrowExceptionWhenPromotionNotFoundDuringUpdate() {
        // Given
        when(promotionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            promotionService.updatePromotion(1L, promotionDto);
        });

        verify(promotionRepository).findById(1L);
        verify(stateService, never()).findById(any());
        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Should get all Promotions excluding eliminated ones")
    void shouldGetAllPromotionsExcludingEliminatedOnes() {
        // Given
        Promotion promotion1 = TestDataFactory.createValidPromotion(activeState);
        promotion1.setId(1L);
        promotion1.setName("Promotion 1");
        promotion1.setCode("PROMO1");
        
        Promotion promotion2 = TestDataFactory.createValidPromotion(inactiveState);
        promotion2.setId(2L);
        promotion2.setName("Promotion 2");
        promotion2.setCode("PROMO2");
        
        Promotion eliminatedPromotion = TestDataFactory.createValidPromotion(eliminatedState);
        eliminatedPromotion.setId(3L);
        eliminatedPromotion.setName("Eliminated Promotion");
        eliminatedPromotion.setCode("ELIMINATED");

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(promotionRepository.findAll()).thenReturn(Arrays.asList(promotion1, promotion2, eliminatedPromotion));

        // When
        List<PromotionResponse> responses = promotionService.getAllPromotions();

        // Then
        assertEquals(2, responses.size());
        assertEquals("Promotion 1", responses.get(0).name());
        assertEquals("Promotion 2", responses.get(1).name());
        
        // Verify eliminated promotion is not in the results
        assertTrue(responses.stream().noneMatch(r -> r.name().equals("Eliminated Promotion")));

        verify(stateService).getEliminatedState();
        verify(promotionRepository).findAll();
    }

    @Test
    @DisplayName("Should get Promotion by ID")
    void shouldGetPromotionById() {
        // Given
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));

        // When
        Promotion result = promotionService.getPromotionById(1L);

        // Then
        assertNotNull(result);
        assertEquals(promotion.getId(), result.getId());
        assertEquals(promotion.getName(), result.getName());

        verify(promotionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when Promotion not found by ID")
    void shouldThrowExceptionWhenPromotionNotFoundById() {
        // Given
        when(promotionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            promotionService.getPromotionById(1L);
        });

        verify(promotionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get Promotion by ID as response")
    void shouldGetPromotionByIdAsResponse() {
        // Given
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));

        // When
        PromotionResponse response = promotionService.getPromotionByIdResponse(1L);

        // Then
        assertNotNull(response);
        assertEquals(promotion.getId(), response.id());
        assertEquals(promotion.getName(), response.name());
        assertEquals(promotion.getDiscountValue(), response.discountValue());
        assertEquals(promotion.getCode(), response.code());

        verify(promotionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get Promotions by state")
    void shouldGetPromotionsByState() {
        // Given
        List<Promotion> promotions = Arrays.asList(promotion);
        when(promotionRepository.findByStateId(activeState.getId())).thenReturn(promotions);

        // When
        List<PromotionResponse> responses = promotionService.getPromotionsByState(activeState.getId());

        // Then
        assertEquals(1, responses.size());
        assertEquals(promotion.getName(), responses.get(0).name());

        verify(promotionRepository).findByStateId(activeState.getId());
    }

    @Test
    @DisplayName("Should get Promotion by code")
    void shouldGetPromotionByCode() {
        // Given
        String code = "SUMMER20";
        when(promotionRepository.findByCode(code)).thenReturn(Optional.of(promotion));

        // When
        PromotionResponse response = promotionService.getPromotionByCode(code);

        // Then
        assertNotNull(response);
        assertEquals(promotion.getId(), response.id());
        assertEquals(promotion.getName(), response.name());
        assertEquals(promotion.getCode(), response.code());

        verify(promotionRepository).findByCode(code);
    }

    @Test
    @DisplayName("Should throw exception when Promotion not found by code")
    void shouldThrowExceptionWhenPromotionNotFoundByCode() {
        // Given
        String code = "NONEXISTENT";
        when(promotionRepository.findByCode(code)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            promotionService.getPromotionByCode(code);
        });

        verify(promotionRepository).findByCode(code);
    }

    @Test
    @DisplayName("Should delete Promotion with soft delete")
    void shouldDeletePromotionWithSoftDelete() {
        // Given
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);

        // When
        promotionService.deletePromotion(1L);

        // Then
        verify(promotionRepository).findById(1L);
        verify(stateService).getEliminatedState();
        verify(promotionRepository).save(argThat(p -> 
            p.getState().equals(eliminatedState)));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent Promotion")
    void shouldThrowExceptionWhenDeletingNonExistentPromotion() {
        // Given
        when(promotionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            promotionService.deletePromotion(1L);
        });

        verify(promotionRepository).findById(1L);
        verify(stateService, never()).getEliminatedState();
        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Should find Promotion by ID")
    void shouldFindPromotionById() {
        // Given
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));

        // When
        Promotion result = promotionService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(promotion, result);

        verify(promotionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find Promotion by ID and state ID")
    void shouldFindPromotionByIdAndStateId() {
        // Given
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));

        // When
        Promotion result = promotionService.findByIdAndStateId(1L, activeState.getId());

        // Then
        assertNotNull(result);
        assertEquals(promotion, result);

        verify(promotionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should use default state when state ID is null")
    void shouldUseDefaultStateWhenStateIdIsNull() {
        // Given
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotion));

        // When
        Promotion result = promotionService.findByIdAndStateId(1L, null);

        // Then
        assertNotNull(result);
        assertEquals(promotion, result);

        verify(promotionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when state doesn't match")
    void shouldThrowExceptionWhenStateDoesntMatch() {
        // Given
        Promotion promotionWithDifferentState = TestDataFactory.createValidPromotion(inactiveState);
        promotionWithDifferentState.setId(1L);
        
        when(promotionRepository.findById(1L)).thenReturn(Optional.of(promotionWithDifferentState));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            promotionService.findByIdAndStateId(1L, activeState.getId());
        });

        verify(promotionRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle empty repository when getting all promotions")
    void shouldHandleEmptyRepositoryWhenGettingAllPromotions() {
        // Given
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(promotionRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<PromotionResponse> responses = promotionService.getAllPromotions();

        // Then
        assertTrue(responses.isEmpty());

        verify(stateService).getEliminatedState();
        verify(promotionRepository).findAll();
    }

    @Test
    @DisplayName("Should handle repository with only eliminated promotions")
    void shouldHandleRepositoryWithOnlyEliminatedPromotions() {
        // Given
        Promotion eliminatedPromotion1 = TestDataFactory.createValidPromotion(eliminatedState);
        eliminatedPromotion1.setId(1L);
        eliminatedPromotion1.setName("Eliminated 1");
        eliminatedPromotion1.setCode("ELIM1");
        
        Promotion eliminatedPromotion2 = TestDataFactory.createValidPromotion(eliminatedState);
        eliminatedPromotion2.setId(2L);
        eliminatedPromotion2.setName("Eliminated 2");
        eliminatedPromotion2.setCode("ELIM2");

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(promotionRepository.findAll()).thenReturn(Arrays.asList(eliminatedPromotion1, eliminatedPromotion2));

        // When
        List<PromotionResponse> responses = promotionService.getAllPromotions();

        // Then
        assertTrue(responses.isEmpty());

        verify(stateService).getEliminatedState();
        verify(promotionRepository).findAll();
    }

    @Test
    @DisplayName("Should handle promotions with null states gracefully")
    void shouldHandlePromotionsWithNullStatesGracefully() {
        // Given
        Promotion promotionWithNullState = new Promotion();
        promotionWithNullState.setId(1L);
        promotionWithNullState.setName("Promotion with null state");
        promotionWithNullState.setDiscountValue(new BigDecimal("10.00"));
        promotionWithNullState.setCode("NULL10");
        promotionWithNullState.setState(null);

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(promotionRepository.findAll()).thenReturn(Arrays.asList(promotionWithNullState, promotion));

        // When
        List<PromotionResponse> responses = promotionService.getAllPromotions();

        // Then
        assertEquals(1, responses.size()); // Only the promotion with valid state
        assertEquals(promotion.getName(), responses.get(0).name());

        verify(stateService).getEliminatedState();
        verify(promotionRepository).findAll();
    }

    @Test
    @DisplayName("Should handle unique code constraint scenarios")
    void shouldHandleUniqueCodeConstraintScenarios() {
        // Given
        String duplicateCode = "DUPLICATE";
        Promotion existingPromotion = TestDataFactory.createValidPromotion(activeState);
        existingPromotion.setCode(duplicateCode);
        
        when(promotionRepository.findByCode(duplicateCode)).thenReturn(Optional.of(existingPromotion));

        // When
        PromotionResponse response = promotionService.getPromotionByCode(duplicateCode);

        // Then
        assertNotNull(response);
        assertEquals(duplicateCode, response.code());

        verify(promotionRepository).findByCode(duplicateCode);
    }

    @Test
    @DisplayName("Should handle case sensitive code search")
    void shouldHandleCaseSensitiveCodeSearch() {
        // Given
        String code = "SUMMER20";
        String differentCaseCode = "summer20";
        
        when(promotionRepository.findByCode(code)).thenReturn(Optional.of(promotion));
        when(promotionRepository.findByCode(differentCaseCode)).thenReturn(Optional.empty());

        // When
        PromotionResponse response = promotionService.getPromotionByCode(code);

        // Then
        assertNotNull(response);
        assertEquals(promotion.getCode(), response.code());

        // And when searching with different case
        assertThrows(EntityNotFoundException.class, () -> {
            promotionService.getPromotionByCode(differentCaseCode);
        });

        verify(promotionRepository).findByCode(code);
        verify(promotionRepository).findByCode(differentCaseCode);
    }
}