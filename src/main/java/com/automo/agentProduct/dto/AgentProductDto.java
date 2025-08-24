package com.automo.agentProduct.dto;

import jakarta.validation.constraints.NotNull;

public record AgentProductDto(
    @NotNull(message = "ID do agente é obrigatório")
    Long agentId,
    
    @NotNull(message = "ID do produto é obrigatório")
    Long productId
) {} 