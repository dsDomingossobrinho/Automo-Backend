package com.automo.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record OtpVerificationRequest(
    @NotBlank(message = "Contato (email ou telefone) é obrigatório")
    @Size(min = 3, max = 100, message = "Contato deve ter entre 3 e 100 caracteres")
    String contact,
    
    @NotBlank(message = "Código OTP é obrigatório")
    @Pattern(regexp = "^\\d{6}$", message = "Código OTP deve ter exatamente 6 dígitos")
    String otpCode
) {} 