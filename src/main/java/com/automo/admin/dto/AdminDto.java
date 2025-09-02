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
    
    // Campos para criação do Auth (username será gerado automaticamente baseado no name)
    @NotBlank(message = "Password é obrigatório")
    String password,
    
    String contact,
    
    @NotNull(message = "ID do tipo de conta é obrigatório")
    Long accountTypeId,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 