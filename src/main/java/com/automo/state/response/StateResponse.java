package com.automo.state.response;

import java.time.LocalDateTime;

public record StateResponse(
    Long id,
    String state,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 