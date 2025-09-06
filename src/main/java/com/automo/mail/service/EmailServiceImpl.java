package com.automo.mail.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateService templateService;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendOtpEmail(String to, String otpCode, String purpose) {
        String subject = "Código de Verificação - Automo";
        String htmlContent = templateService.generateOtpEmailHtml(otpCode, purpose);
        
        sendHtmlEmail(to, subject, htmlContent);
        log.info("OTP HTML email sent to: {} for purpose: {}", to, purpose);
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String content) {
        String htmlContent = templateService.generateSimpleEmailHtml(subject, content);
        sendHtmlEmail(to, subject, htmlContent);
    }

    @Override
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML content
            
            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }
} 