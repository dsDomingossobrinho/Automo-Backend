package com.automo.auth.dto;

public record LoginResponse(
    String accessToken,
    String refreshToken
) {}

