package com.automo.organizationType.response;

import java.time.LocalDateTime;

public record OrganizationTypeResponse(
    Long id,
    String type,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 