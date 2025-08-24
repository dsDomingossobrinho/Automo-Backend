package com.automo.province.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProvinceDto(
    @NotBlank(message = "Province is required")
    String province,
    
    @NotNull(message = "Country ID is required")
    Long countryId,
    
    @NotNull(message = "State ID is required")
    Long stateId
) {} 