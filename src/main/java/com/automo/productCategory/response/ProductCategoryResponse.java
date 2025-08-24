package com.automo.productCategory.response;

import java.time.LocalDateTime;

public record ProductCategoryResponse(
    Long id,
    String category,
    String description,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 