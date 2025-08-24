package com.automo.notificationType.dto;

import jakarta.validation.constraints.NotBlank;

public record NotificationTypeDto(
    @NotBlank(message = "Type is required")
    String type,
    
    String description
) {} 