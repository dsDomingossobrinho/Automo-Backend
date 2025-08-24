package com.automo.deal.service;

import com.automo.deal.dto.DealDto;
import com.automo.deal.entity.Deal;
import com.automo.deal.response.DealResponse;

import java.util.List;

public interface DealService {

    DealResponse createDeal(DealDto dealDto);

    DealResponse updateDeal(Long id, DealDto dealDto);

    List<DealResponse> getAllDeals();

    Deal getDealById(Long id);

    DealResponse getDealByIdResponse(Long id);

    List<DealResponse> getDealsByState(Long stateId);

    List<DealResponse> getDealsByIdentifier(Long identifierId);

    List<DealResponse> getDealsByLead(Long leadId);

    List<DealResponse> getDealsByPromotion(Long promotionId);

    void deleteDeal(Long id);
} 