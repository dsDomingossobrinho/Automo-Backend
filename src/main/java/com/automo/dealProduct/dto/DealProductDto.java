package com.automo.dealProduct.dto;

import jakarta.validation.constraints.NotNull;

public record DealProductDto(
    @NotNull(message = "ID do negócio é obrigatório")
    Long dealId,
    
    @NotNull(message = "ID do produto é obrigatório")
    Long productId,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 