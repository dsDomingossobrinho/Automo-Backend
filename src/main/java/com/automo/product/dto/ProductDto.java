package com.automo.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductDto(
    @NotBlank(message = "Nome é obrigatório")
    String name,
    
    String img,
    
    String description,
    
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    BigDecimal price,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 