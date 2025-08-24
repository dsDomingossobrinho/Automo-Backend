package com.automo.messageCount.response;

import java.time.LocalDateTime;

public record MessageCountResponse(
    Long id,
    Long leadId,
    String leadName,
    Integer messageCount,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 