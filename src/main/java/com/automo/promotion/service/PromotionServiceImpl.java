package com.automo.promotion.service;

import com.automo.promotion.dto.PromotionDto;
import com.automo.promotion.entity.Promotion;
import com.automo.promotion.repository.PromotionRepository;
import com.automo.promotion.response.PromotionResponse;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;
    private final StateRepository stateRepository;

    @Override
    public PromotionResponse createPromotion(PromotionDto promotionDto) {
        State state = stateRepository.findById(promotionDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + promotionDto.stateId() + " not found"));

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
        
        State state = stateRepository.findById(promotionDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + promotionDto.stateId() + " not found"));

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
} 