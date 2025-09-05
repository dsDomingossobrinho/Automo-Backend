package com.automo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
    @NotBlank(message = "Email ou contacto é obrigatório")
    @Size(max = 255, message = "Email ou contacto deve ter no máximo 255 caracteres")
    String emailOrContact,
    
    @NotBlank(message = "Código OTP é obrigatório")
    @Size(max = 6, message = "Código OTP deve ter no máximo 6 caracteres")
    String otpCode,
    
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 6, max = 100, message = "Nova senha deve ter entre 6 e 100 caracteres")
    String newPassword
) {
}