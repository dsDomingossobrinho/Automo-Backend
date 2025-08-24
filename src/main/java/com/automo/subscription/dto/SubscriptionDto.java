package com.automo.subscription.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SubscriptionDto(
    @NotNull(message = "ID do usuário é obrigatório")
    Long userId,
    
    @NotNull(message = "ID do plano é obrigatório")
    Long planId,
    
    Long promotionId,
    
    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser positivo")
    BigDecimal price,
    
    @NotNull(message = "Data de início é obrigatória")
    LocalDate startDate,
    
    @NotNull(message = "Data de fim é obrigatória")
    LocalDate endDate,
    
    @NotNull(message = "Quantidade de mensagens é obrigatória")
    @Positive(message = "Quantidade de mensagens deve ser positiva")
    Integer messageCount,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 