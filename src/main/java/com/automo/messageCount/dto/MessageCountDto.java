package com.automo.messageCount.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MessageCountDto(
    @NotNull(message = "ID do lead é obrigatório")
    Long leadId,
    
    @NotNull(message = "Quantidade de mensagens é obrigatória")
    @Positive(message = "Quantidade de mensagens deve ser positiva")
    Integer messageCount,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 