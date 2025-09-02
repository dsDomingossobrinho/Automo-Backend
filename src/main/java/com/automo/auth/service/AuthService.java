package com.automo.auth.service;

import com.automo.auth.dto.AuthResponse;
import com.automo.auth.dto.RegisterRequest;
import com.automo.auth.dto.OtpRequest;
import com.automo.auth.dto.OtpVerificationRequest;
import com.automo.auth.dto.LoginRequest;
import com.automo.auth.dto.LoginResponse;
import com.automo.auth.entity.Auth;

import java.util.Optional;

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

    /**
     * Autentica usuário com login direto (sem OTP)
     */
    LoginResponse authenticate(LoginRequest request);

    /**
     * Busca usuário por email, username ou contato
     */
    Optional<Auth> findByEmailOrUsernameOrContact(String emailOrContact);
    
    /**
     * Busca Auth por ID - método obrigatório para comunicação entre services
     */
    Auth findById(Long id);
    
    /**
     * Busca Auth por ID e estado específico (state_id = 1 por padrão)
     */
    Auth findByIdAndStateId(Long id, Long stateId);
} 