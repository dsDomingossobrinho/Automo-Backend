package com.automo.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminDto(
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email,
    
    @NotBlank(message = "Nome é obrigatório")
    String name,
    
    String img,
    
    @NotNull(message = "ID da autenticação é obrigatório")
    Long authId,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 