package com.automo.auth.dto;

import java.time.LocalDateTime;

public record AuthCreateResponse(
    Long id,
    String email,
    String username,
    String contact,
    Long accountTypeId,
    Long stateId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
