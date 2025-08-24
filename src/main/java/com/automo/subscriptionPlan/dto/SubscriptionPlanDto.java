package com.automo.subscriptionPlan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record SubscriptionPlanDto(
    @NotBlank(message = "Nome é obrigatório")
    String name,
    
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    BigDecimal price,
    
    String description,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 