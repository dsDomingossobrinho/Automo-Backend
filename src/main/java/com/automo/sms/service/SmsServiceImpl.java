package com.automo.sms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsServiceImpl implements SmsService {

    @Value("${sms.provider.enabled:false}")
    private boolean smsEnabled;
    
    @Value("${sms.provider.api-key:}")
    private String apiKey;
    
    @Value("${sms.provider.sender:AUTOMO}")
    private String senderName;

    @Override
    public void sendOtpSms(String phoneNumber, String otpCode, String purpose) {
        String message = String.format(
            "Automo: Seu código de verificação para %s é: %s. Válido por 5 minutos. Não compartilhe este código.",
            purpose, otpCode
        );
        
        sendSimpleSms(phoneNumber, message);
        log.info("OTP SMS sent to: {} for purpose: {}", phoneNumber, purpose);
    }

    @Override
    public void sendSimpleSms(String phoneNumber, String message) {
        if (!smsEnabled) {
            log.warn("SMS service is disabled. Would send to {}: {}", phoneNumber, message);
            return;
        }
        
        try {
            // Aqui você integraria com um provedor de SMS real
            // Exemplos: Twilio, AWS SNS, Vonage, etc.
            sendSmsViaTwilio(phoneNumber, message);
            
            log.info("SMS sent successfully to: {}", phoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", phoneNumber, e);
            throw new RuntimeException("Failed to send SMS", e);
        }
    }
    
    private void sendSmsViaTwilio(String phoneNumber, String message) {
        // Implementação simulada - substitua pela integração real
        log.info("SIMULATED SMS SEND:");
        log.info("To: {}", phoneNumber);
        log.info("Message: {}", message);
        log.info("Sender: {}", senderName);
        
        // Para integração real com Twilio, você usaria algo como:
        /*
        Twilio.init(accountSid, authToken);
        Message.creator(
            new PhoneNumber(phoneNumber),
            new PhoneNumber(twilioPhoneNumber),
            message
        ).create();
        */
    }
} 