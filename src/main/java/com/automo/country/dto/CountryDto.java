package com.automo.country.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CountryDto(
    @NotBlank(message = "Country is required")
    String country,
    
    @Positive(message = "Number of digits must be positive")
    Integer numberDigits,
    
    String indicative,
    
    @NotNull(message = "State ID is required")
    Long stateId
) {} 