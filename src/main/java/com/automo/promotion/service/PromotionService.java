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
} 