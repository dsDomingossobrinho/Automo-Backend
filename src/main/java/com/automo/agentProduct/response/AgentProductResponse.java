package com.automo.agentProduct.response;

import java.time.LocalDateTime;

public record AgentProductResponse(
    Long id,
    Long agentId,
    String agentName,
    Long productId,
    String productName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 