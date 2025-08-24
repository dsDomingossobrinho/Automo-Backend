package com.automo.subscriptionPlan.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SubscriptionPlanResponse(
    Long id,
    String name,
    BigDecimal price,
    String description,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 