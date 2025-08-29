package com.automo.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Email ou contato é obrigatório")
    String emailOrContact,
    
    @NotBlank(message = "Senha é obrigatória")
    String password
) {}
