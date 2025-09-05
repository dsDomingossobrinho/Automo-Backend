package com.automo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(
    @NotBlank(message = "Email ou contacto é obrigatório")
    @Size(max = 255, message = "Email ou contacto deve ter no máximo 255 caracteres")
    String emailOrContact
) {
}