package com.automo.role.response;

import java.time.LocalDateTime;

public record RoleResponse(
    Long id,
    String role,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 