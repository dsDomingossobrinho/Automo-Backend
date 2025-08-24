package com.automo.promotion.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromotionResponse(
    Long id,
    String name,
    BigDecimal discountValue,
    String code,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 