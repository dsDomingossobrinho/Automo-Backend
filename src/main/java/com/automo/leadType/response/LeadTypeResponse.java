package com.automo.leadType.response;

import java.time.LocalDateTime;

public record LeadTypeResponse(
    Long id,
    String type,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 