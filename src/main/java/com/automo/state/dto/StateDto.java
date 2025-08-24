package com.automo.state.dto;

import jakarta.validation.constraints.NotBlank;

public record StateDto(
    @NotBlank(message = "State is required")
    String state,
    
    String description
) {} 