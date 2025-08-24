package com.automo.identifierType.response;

import java.time.LocalDateTime;

public record IdentifierTypeResponse(
    Long id,
    String type,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 