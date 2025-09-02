package com.automo.subscriptionPlan.service;

import com.automo.subscriptionPlan.dto.SubscriptionPlanDto;
import com.automo.subscriptionPlan.entity.SubscriptionPlan;
import com.automo.subscriptionPlan.response.SubscriptionPlanResponse;

import java.util.List;

public interface SubscriptionPlanService {

    SubscriptionPlanResponse createSubscriptionPlan(SubscriptionPlanDto subscriptionPlanDto);

    SubscriptionPlanResponse updateSubscriptionPlan(Long id, SubscriptionPlanDto subscriptionPlanDto);

    List<SubscriptionPlanResponse> getAllSubscriptionPlans();

    SubscriptionPlan getSubscriptionPlanById(Long id);

    SubscriptionPlanResponse getSubscriptionPlanByIdResponse(Long id);

    List<SubscriptionPlanResponse> getSubscriptionPlansByState(Long stateId);

    void deleteSubscriptionPlan(Long id);
    
    /**
     * Busca SubscriptionPlan por ID - método obrigatório para comunicação entre services
     */
    SubscriptionPlan findById(Long id);
    
    /**
     * Busca SubscriptionPlan por ID e estado específico (state_id = 1 por padrão)
     */
    SubscriptionPlan findByIdAndStateId(Long id, Long stateId);
} 