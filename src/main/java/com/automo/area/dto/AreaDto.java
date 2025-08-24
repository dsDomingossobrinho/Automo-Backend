package com.automo.area.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AreaDto(
    @NotBlank(message = "Area is required")
    String area,
    
    String description,
    
    @NotNull(message = "State ID is required")
    Long stateId
) {} 