package com.automo.admin.response;

import java.time.LocalDateTime;

public record AdminResponse(
    Long id,
    String email,
    String name,
    String img,
    Long authId,
    String username,
    Long stateId,
    String state,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 