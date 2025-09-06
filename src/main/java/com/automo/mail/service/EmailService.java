package com.automo.mail.service;

public interface EmailService {
    
    void sendOtpEmail(String to, String otpCode, String purpose);
    
    void sendSimpleEmail(String to, String subject, String content);
    
    void sendHtmlEmail(String to, String subject, String htmlContent);
} 