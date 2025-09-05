package com.automo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateCompleteUserRequest(
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    String name,
    
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ter formato válido")
    @Size(max = 255, message = "Email deve ter no máximo 255 caracteres")
    String email,
    
    @NotBlank(message = "Contato é obrigatório")
    @Size(max = 20, message = "Contato deve ter no máximo 20 caracteres")
    String contact,
    
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    String username,
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    String password,
    
    @NotNull(message = "ID do tipo de conta é obrigatório")
    Long accountTypeId,
    
    @NotNull(message = "Lista de roles é obrigatória")
    @Size(min = 1, message = "Usuário deve ter pelo menos uma role")
    List<Long> roleIds,
    
    Long stateId // Opcional, padrão será ACTIVE (1L)
) {
}