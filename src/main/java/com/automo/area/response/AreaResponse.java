package com.automo.area.response;

import java.time.LocalDateTime;

public record AreaResponse(
    Long id,
    String area,
    String description,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 