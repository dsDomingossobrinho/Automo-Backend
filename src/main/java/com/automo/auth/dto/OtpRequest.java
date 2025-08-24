package com.automo.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record OtpRequest(
    @NotBlank(message = "Email or contact is required")
    String emailOrContact,
    
    @NotBlank(message = "Password is required")
    String password
) {} 