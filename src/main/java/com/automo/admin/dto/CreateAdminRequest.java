package com.automo.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateAdminRequest(
    // Dados de autenticação
    @Email(message = "Formato de email inválido")
    @NotBlank(message = "Email é obrigatório")
    String email,

    @NotBlank(message = "Nome é obrigatório")
    String name,
    
    String img,
    
    @NotBlank(message = "Senha é obrigatória")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{8,}$", 
             message = "Senha deve ter pelo menos 8 caracteres, incluindo maiúscula, minúscula e número")
    String password,
    
    @NotNull(message = "Tipo de conta é obrigatório")
    Long accountTypeId,
    
    @NotNull(message = "Estado é obrigatório")
    Long stateId,
    
    @NotNull(message = "Lista de roles é obrigatória")
    @Size(min = 1, message = "Pelo menos uma role deve ser fornecida")
    List<Long> roles
    
    
) {}
