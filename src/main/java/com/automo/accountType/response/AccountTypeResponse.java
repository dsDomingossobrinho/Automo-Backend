package com.automo.accountType.response;

import java.time.LocalDateTime;

public record AccountTypeResponse(
    Long id,
    String type,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 