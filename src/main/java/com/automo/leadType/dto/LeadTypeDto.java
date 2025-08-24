package com.automo.leadType.dto;

import jakarta.validation.constraints.NotBlank;

public record LeadTypeDto(
    @NotBlank(message = "Type is required")
    String type,
    
    String description
) {} 