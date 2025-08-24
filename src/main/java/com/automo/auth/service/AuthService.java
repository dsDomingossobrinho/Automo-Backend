package com.automo.auth.service;

import com.automo.auth.dto.AuthResponse;
import com.automo.auth.dto.RegisterRequest;
import com.automo.auth.dto.OtpRequest;
import com.automo.auth.dto.OtpVerificationRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse requestOtp(OtpRequest request);

    AuthResponse verifyOtpAndAuthenticate(OtpVerificationRequest request);

    AuthResponse authenticateBackOffice(OtpRequest request);

    AuthResponse verifyOtpAndAuthenticateBackOffice(OtpVerificationRequest request);

    AuthResponse authenticateUser(OtpRequest request);

    AuthResponse verifyOtpAndAuthenticateUser(OtpVerificationRequest request);
} 