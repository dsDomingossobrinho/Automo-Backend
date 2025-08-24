package com.automo.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record OtpVerificationRequest(
    @NotBlank(message = "Contact (email or phone) is required")
    String contact,
    
    @NotBlank(message = "OTP code is required")
    String otpCode
) {} 