package com.automo.auth.service;

public interface OtpService {
    
    String generateAndSendOtp(String contact, String purpose);
    
    boolean verifyOtp(String contact, String otpCode, String purpose);
    
    void cleanupExpiredOtps();
} 