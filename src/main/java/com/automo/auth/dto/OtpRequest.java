package com.automo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OtpRequest(
    @NotBlank(message = "Email ou contato é obrigatório")
    @Size(min = 3, max = 100, message = "Email ou contato deve ter entre 3 e 100 caracteres")
    String emailOrContact,
    
    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 100, message = "Senha deve ter entre 6 e 100 caracteres")
    String password
) {} 