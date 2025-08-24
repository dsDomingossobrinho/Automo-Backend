package com.automo.identifier.dto;

import jakarta.validation.constraints.NotNull;

public record IdentifierDto(
    @NotNull(message = "ID do usuário é obrigatório")
    Long userId,
    
    @NotNull(message = "ID do tipo de identificador é obrigatório")
    Long identifierTypeId,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 