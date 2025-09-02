package com.automo.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserDto(
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    String email,
    
    @NotBlank(message = "Nome é obrigatório")
    String name,
    
    String img,
    
    String contacto,
    
    // Campos para criação do Auth (username será gerado automaticamente baseado no name)
    @NotBlank(message = "Password é obrigatório")
    String password,
    
    @NotNull(message = "ID do tipo de conta é obrigatório")
    Long accountTypeId,
    
    @NotNull(message = "ID do país é obrigatório")
    Long countryId,
    
    @NotNull(message = "ID do tipo de organização é obrigatório")
    Long organizationTypeId,
    
    Long provinceId,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 