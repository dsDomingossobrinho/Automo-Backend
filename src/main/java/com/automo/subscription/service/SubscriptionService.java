package com.automo.subscription.service;

import com.automo.subscription.dto.SubscriptionDto;
import com.automo.subscription.entity.Subscription;
import com.automo.subscription.response.SubscriptionResponse;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionService {

    SubscriptionResponse createSubscription(SubscriptionDto subscriptionDto);

    SubscriptionResponse updateSubscription(Long id, SubscriptionDto subscriptionDto);

    List<SubscriptionResponse> getAllSubscriptions();

    Subscription getSubscriptionById(Long id);

    SubscriptionResponse getSubscriptionByIdResponse(Long id);

    List<SubscriptionResponse> getSubscriptionsByState(Long stateId);

    List<SubscriptionResponse> getSubscriptionsByUser(Long userId);

    List<SubscriptionResponse> getSubscriptionsByPlan(Long planId);

    List<SubscriptionResponse> getSubscriptionsByPromotion(Long promotionId);

    List<SubscriptionResponse> getSubscriptionsByDateRange(LocalDate startDate, LocalDate endDate);

    List<SubscriptionResponse> getExpiredSubscriptions(LocalDate date);

    void deleteSubscription(Long id);
} 