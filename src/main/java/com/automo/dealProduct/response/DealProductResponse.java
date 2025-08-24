package com.automo.dealProduct.response;

import java.time.LocalDateTime;

public record DealProductResponse(
    Long id,
    Long dealId,
    String dealTotal,
    Long productId,
    String productName,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 