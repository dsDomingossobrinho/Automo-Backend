package com.automo.promotion.service;

import com.automo.promotion.dto.PromotionDto;
import com.automo.promotion.entity.Promotion;
import com.automo.promotion.response.PromotionResponse;

import java.util.List;

public interface PromotionService {

    PromotionResponse createPromotion(PromotionDto promotionDto);

    PromotionResponse updatePromotion(Long id, PromotionDto promotionDto);

    List<PromotionResponse> getAllPromotions();

    Promotion getPromotionById(Long id);

    PromotionResponse getPromotionByIdResponse(Long id);

    List<PromotionResponse> getPromotionsByState(Long stateId);

    PromotionResponse getPromotionByCode(String code);

    void deletePromotion(Long id);
    
    /**
     * Busca Promotion por ID - método obrigatório para comunicação entre services
     */
    Promotion findById(Long id);
    
    /**
     * Busca Promotion por ID e estado específico (state_id = 1 por padrão)
     */
    Promotion findByIdAndStateId(Long id, Long stateId);
} 