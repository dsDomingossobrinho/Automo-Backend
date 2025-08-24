package com.automo.paymentType.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentTypeDto(
    @NotBlank(message = "Type is required")
    String type,
    
    String description
) {} 