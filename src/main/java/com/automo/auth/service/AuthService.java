package com.automo.auth.service;

import com.automo.auth.dto.AuthResponse;
import com.automo.auth.dto.RegisterRequest;
import com.automo.auth.dto.OtpRequest;
import com.automo.auth.dto.OtpVerificationRequest;

public interface AuthService {

    /**
     * Registra um novo usuário
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Solicita OTP para autenticação
     */
    AuthResponse requestOtp(OtpRequest request);

    /**
     * Verifica OTP e autentica usuário
     */
    AuthResponse verifyOtpAndAuthenticate(OtpVerificationRequest request);

    /**
     * Autentica back office
     */
    AuthResponse authenticateBackOffice(OtpRequest request);

    /**
     * Verifica OTP e autentica back office
     */
    AuthResponse verifyOtpAndAuthenticateBackOffice(OtpVerificationRequest request);

    /**
     * Autentica usuário
     */
    AuthResponse authenticateUser(OtpRequest request);

    /**
     * Verifica OTP e autentica usuário
     */
    AuthResponse verifyOtpAndAuthenticateUser(OtpVerificationRequest request);
} 