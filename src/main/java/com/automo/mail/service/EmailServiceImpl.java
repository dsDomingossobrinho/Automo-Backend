package com.automo.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String to, String otpCode, String purpose) {
        String subject = "Código de Verificação - Automo";
        String content = String.format("""
            Olá!
            
            Seu código de verificação para %s é: %s
            
            Este código expira em 5 minutos.
            
            Se você não solicitou este código, ignore este email.
            
            Atenciosamente,
            Equipe Automo
            """, purpose, otpCode);
        
        sendSimpleEmail(to, subject, content);
        log.info("OTP email sent to: {} for purpose: {}", to, purpose);
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
} 