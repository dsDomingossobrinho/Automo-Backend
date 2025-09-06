package com.automo.mail.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class EmailTemplateService {

    private static final String TEMPLATES_PATH = "templates/";
    private static final String OTP_TEMPLATE = "otp-email-template.html";
    private static final String SIMPLE_TEMPLATE = "simple-email-template.html";
    private static final String LOGO_PATH = "static/images/automo-logo.png";
    
    private String logoBase64 = null;
    
    public void clearLogoCache() {
        logoBase64 = null;
        log.info("Logo cache cleared - will reload on next use");
    }

    public String generateOtpEmailHtml(String otpCode, String purpose) {
        try {
            String template = loadTemplate(OTP_TEMPLATE);
            String logoData = getLogoBase64();
            return template
                    .replace("{{OTP_CODE}}", otpCode)
                    .replace("{{PURPOSE}}", purpose)
                    .replace("{{LOGO_BASE64}}", logoData);
        } catch (IOException e) {
            log.error("Error loading OTP email template", e);
            return generateFallbackOtpEmail(otpCode, purpose);
        }
    }

    public String generateSimpleEmailHtml(String subject, String content) {
        try {
            String template = loadTemplate(SIMPLE_TEMPLATE);
            String logoData = getLogoBase64();
            return template
                    .replace("{{SUBJECT}}", subject)
                    .replace("{{CONTENT}}", content)
                    .replace("{{LOGO_BASE64}}", logoData);
        } catch (IOException e) {
            log.error("Error loading simple email template", e);
            return generateFallbackSimpleEmail(subject, content);
        }
    }

    private String loadTemplate(String templateName) throws IOException {
        ClassPathResource resource = new ClassPathResource(TEMPLATES_PATH + templateName);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String getLogoBase64() {
        if (logoBase64 == null) {
            try {
                log.info("Attempting to load logo from path: {}", LOGO_PATH);
                ClassPathResource resource = new ClassPathResource(LOGO_PATH);
                
                if (!resource.exists()) {
                    log.error("Logo file does not exist at path: {}", LOGO_PATH);
                    logoBase64 = getDefaultLogoBase64();
                    return logoBase64;
                }
                
                byte[] logoBytes = resource.getInputStream().readAllBytes();
                log.info("Logo file loaded successfully, size: {} bytes", logoBytes.length);
                
                logoBase64 = "data:image/png;base64," + Base64.getEncoder().encodeToString(logoBytes);
                log.info("PNG Logo encoded to base64 successfully, total length: {}", logoBase64.length());
                
            } catch (IOException e) {
                log.error("Error loading PNG logo file from path: {}", LOGO_PATH, e);
                logoBase64 = getDefaultLogoBase64();
                log.info("Using default logo fallback");
            }
        }
        return logoBase64;
    }

    private String getDefaultLogoBase64() {
        // SVG simples como fallback
        String defaultSvg = """
            <svg width="200" height="100" xmlns="http://www.w3.org/2000/svg">
                <defs>
                    <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="0%">
                        <stop offset="0%" stop-color="#00BCD4"/>
                        <stop offset="100%" stop-color="#2196F3"/>
                    </linearGradient>
                </defs>
                <rect width="200" height="100" fill="url(#grad1)" rx="10"/>
                <text x="100" y="40" font-family="Arial, sans-serif" font-size="24" font-weight="bold" text-anchor="middle" fill="white">AUTO</text>
                <text x="100" y="70" font-family="Arial, sans-serif" font-size="24" font-weight="bold" text-anchor="middle" fill="white">MO</text>
            </svg>
            """;
        return "data:image/svg+xml;base64," + Base64.getEncoder().encodeToString(defaultSvg.getBytes(StandardCharsets.UTF_8));
    }

    private String generateFallbackOtpEmail(String otpCode, String purpose) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Código de Verificação - Automo</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px;">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h1 style="color: #2196f3;">Código de Verificação</h1>
                    </div>
                    <p>Olá!</p>
                    <p>Seu código de verificação para <strong>%s</strong> é:</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <span style="font-size: 32px; font-weight: bold; color: #2196f3; background: #f0f8ff; padding: 15px 30px; border-radius: 8px; letter-spacing: 5px;">%s</span>
                    </div>
                    <p style="color: #ff5722;"><strong>Este código expira em 5 minutos.</strong></p>
                    <p>Se você não solicitou este código, ignore este email.</p>
                    <div style="margin-top: 40px; text-align: center; color: #666;">
                        <p>Atenciosamente,</p>
                        <p><strong style="color: #2196f3;">Equipe Automo</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """, purpose, otpCode);
    }

    private String generateFallbackSimpleEmail(String subject, String content) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>%s - Automo</title>
            </head>
            <body style="font-family: Arial, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px;">
                    <div style="text-align: center; margin-bottom: 30px;">
                        <h1 style="color: #2196f3;">%s</h1>
                    </div>
                    <div style="line-height: 1.6; white-space: pre-line;">%s</div>
                    <div style="margin-top: 40px; text-align: center; color: #666;">
                        <p>Atenciosamente,</p>
                        <p><strong style="color: #2196f3;">Equipe Automo</strong></p>
                    </div>
                </div>
            </body>
            </html>
            """, subject, subject, content);
    }
}