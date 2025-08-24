package com.automo.organizationType.dto;

import jakarta.validation.constraints.NotBlank;

public record OrganizationTypeDto(
    @NotBlank(message = "Type is required")
    String type,
    
    String description
) {} 