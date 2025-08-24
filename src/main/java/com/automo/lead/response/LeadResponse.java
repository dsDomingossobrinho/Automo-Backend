package com.automo.lead.response;

import java.time.LocalDateTime;

public record LeadResponse(
    Long id,
    Long identifierId,
    String name,
    String email,
    String contact,
    String zone,
    Long leadTypeId,
    String leadTypeName,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 