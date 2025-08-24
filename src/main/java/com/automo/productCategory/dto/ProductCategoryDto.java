package com.automo.productCategory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductCategoryDto(
    @NotBlank(message = "Category is required")
    String category,
    
    String description,
    
    @NotNull(message = "State ID is required")
    Long stateId
) {} 