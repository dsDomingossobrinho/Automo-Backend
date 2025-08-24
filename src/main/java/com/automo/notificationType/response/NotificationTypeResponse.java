package com.automo.notificationType.response;

import java.time.LocalDateTime;

public record NotificationTypeResponse(
    Long id,
    String type,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 