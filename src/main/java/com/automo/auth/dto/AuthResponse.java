package com.automo.auth.dto;

public record AuthResponse(
    String token,
    String message,
    boolean requiresOtp
) {} 