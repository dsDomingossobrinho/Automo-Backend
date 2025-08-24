package com.automo.accountType.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountTypeDto(
    @NotBlank(message = "Type is required")
    String type,
    
    String description
) {} 