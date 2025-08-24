package com.automo.identifierType.dto;

import jakarta.validation.constraints.NotBlank;

public record IdentifierTypeDto(
    @NotBlank(message = "Type is required")
    String type,
    
    String description
) {} 