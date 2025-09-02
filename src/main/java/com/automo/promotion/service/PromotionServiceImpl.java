package com.automo.promotion.service;

import com.automo.promotion.dto.PromotionDto;
import com.automo.promotion.entity.Promotion;
import com.automo.promotion.repository.PromotionRepository;
import com.automo.promotion.response.PromotionResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final StateService stateService;

    @Override
    public PromotionResponse createPromotion(PromotionDto promotionDto) {
        State state = stateService.findById(promotionDto.stateId());

        Promotion promotion = new Promotion();
        promotion.setName(promotionDto.name());
        promotion.setDiscountValue(promotionDto.discountValue());
        promotion.setCode(promotionDto.code());
        promotion.setState(state);
        
        Promotion savedPromotion = promotionRepository.save(promotion);
        return mapToResponse(savedPromotion);
    }

    @Override
    public PromotionResponse updatePromotion(Long id, PromotionDto promotionDto) {
        Promotion promotion = this.getPromotionById(id);
        
        State state = stateService.findById(promotionDto.stateId());

        promotion.setName(promotionDto.name());
        promotion.setDiscountValue(promotionDto.discountValue());
        promotion.setCode(promotionDto.code());
        promotion.setState(state);
        
        Promotion updatedPromotion = promotionRepository.save(promotion);
        return mapToResponse(updatedPromotion);
    }

    @Override
    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Promotion with ID " + id + " not found"));
    }

    @Override
    public PromotionResponse getPromotionByIdResponse(Long id) {
        Promotion promotion = this.getPromotionById(id);
        return mapToResponse(promotion);
    }

    @Override
    public List<PromotionResponse> getPromotionsByState(Long stateId) {
        return promotionRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public PromotionResponse getPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Promotion with code " + code + " not found"));
        return mapToResponse(promotion);
    }

    @Override
    public void deletePromotion(Long id) {
        if (!promotionRepository.existsById(id)) {
            throw new EntityNotFoundException("Promotion with ID " + id + " not found");
        }
        promotionRepository.deleteById(id);
    }

    private PromotionResponse mapToResponse(Promotion promotion) {
        return new PromotionResponse(
                promotion.getId(),
                promotion.getName(),
                promotion.getDiscountValue(),
                promotion.getCode(),
                promotion.getState().getId(),
                promotion.getState().getState(),
                promotion.getCreatedAt(),
                promotion.getUpdatedAt()
        );
    }

    @Override
    public Promotion findById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Promotion with ID " + id + " not found"));
    }

    @Override
    public Promotion findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Promotion entity = promotionRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Promotion with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Promotion with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 