package com.automo.associatedContact.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssociatedContactDto(
    @NotNull(message = "ID do identificador é obrigatório")
    Long identifierId,
    
    @NotBlank(message = "Contato é obrigatório")
    String contact,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 