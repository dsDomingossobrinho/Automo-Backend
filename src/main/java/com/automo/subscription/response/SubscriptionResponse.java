package com.automo.subscription.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record SubscriptionResponse(
    Long id,
    Long userId,
    String userName,
    Long planId,
    String planName,
    Long promotionId,
    String promotionName,
    BigDecimal price,
    LocalDate startDate,
    LocalDate endDate,
    Integer messageCount,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 