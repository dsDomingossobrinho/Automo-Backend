package com.automo.deal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DealDto(
    @NotNull(message = "ID do identificador é obrigatório")
    Long identifierId,
    
    Long leadId,
    
    Long promotionId,
    
    @NotNull(message = "Total é obrigatório")
    @Positive(message = "Total deve ser positivo")
    BigDecimal total,
    
    LocalDate deliveryDate,
    
    @NotNull(message = "Quantidade de mensagens é obrigatória")
    @Positive(message = "Quantidade de mensagens deve ser positiva")
    Integer messageCount,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 