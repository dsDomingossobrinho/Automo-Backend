package com.automo.associatedEmail.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssociatedEmailDto(
    @NotNull(message = "ID do identificador é obrigatório")
    Long identifierId,
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 