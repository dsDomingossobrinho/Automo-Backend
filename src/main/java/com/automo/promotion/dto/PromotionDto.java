package com.automo.promotion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PromotionDto(
    @NotBlank(message = "Nome é obrigatório")
    String name,
    
    @NotNull(message = "Valor do desconto é obrigatório")
    @Positive(message = "Valor do desconto deve ser positivo")
    BigDecimal discountValue,
    
    @NotBlank(message = "Código é obrigatório")
    String code,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 