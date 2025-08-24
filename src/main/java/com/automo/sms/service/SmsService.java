package com.automo.sms.service;

public interface SmsService {
    
    void sendOtpSms(String phoneNumber, String otpCode, String purpose);
    
    void sendSimpleSms(String phoneNumber, String message);
} 