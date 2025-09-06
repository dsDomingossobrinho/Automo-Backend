package com.automo.mail.controller;

import com.automo.mail.service.EmailService;
import com.automo.mail.service.EmailTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Slf4j
public class TestEmailController {

    private final EmailService emailService;
    private final EmailTemplateService templateService;

    @PostMapping("/email")
    public ResponseEntity<?> testEmail(@RequestBody TestEmailRequest request) {
        try {
            log.info("Testing email send to: {}", request.email);
            
            String subject = "Teste de Configuração de Email - Automo";
            String content = """
                Olá!
                
                Este é um email de teste para verificar a configuração SMTP com template HTML.
                
                Se você recebeu esta mensagem, significa que o sistema de email está funcionando corretamente.
                
                Configurações utilizadas:
                - SMTP: Gmail (smtp.gmail.com:587)
                - STARTTLS: Habilitado
                - Autenticação: Habilitado
                - Template HTML: ✅ Ativo
                - Logo da empresa: ✅ Embutida
                
                Atenciosamente,
                Equipe Técnica Automo
                """;
            
            emailService.sendSimpleEmail(request.email, subject, content);
            
            log.info("Test email sent successfully to: {}", request.email);
            return ResponseEntity.ok().body(new TestEmailResponse("Email HTML de teste enviado com sucesso para: " + request.email));
            
        } catch (Exception e) {
            log.error("Failed to send test email to: {}", request.email, e);
            return ResponseEntity.status(500).body(new TestEmailResponse("Erro ao enviar email: " + e.getMessage()));
        }
    }

    @PostMapping("/otp")
    public ResponseEntity<?> testOtpEmail(@RequestBody TestEmailRequest request) {
        try {
            log.info("Testing OTP email send to: {}", request.email);
            
            String otpCode = "123456"; // Código de teste
            String purpose = "teste do template HTML";
            
            emailService.sendOtpEmail(request.email, otpCode, purpose);
            
            log.info("Test OTP email sent successfully to: {}", request.email);
            return ResponseEntity.ok().body(new TestEmailResponse("Email OTP HTML de teste enviado com sucesso para: " + request.email));
            
        } catch (Exception e) {
            log.error("Failed to send test OTP email to: {}", request.email, e);
            return ResponseEntity.status(500).body(new TestEmailResponse("Erro ao enviar OTP email: " + e.getMessage()));
        }
    }

    @PostMapping("/clear-logo-cache")
    public ResponseEntity<?> clearLogoCache() {
        try {
            templateService.clearLogoCache();
            log.info("Logo cache cleared successfully");
            return ResponseEntity.ok().body(new TestEmailResponse("Cache da logo limpo com sucesso. Próximos emails usarão a nova logo."));
        } catch (Exception e) {
            log.error("Failed to clear logo cache", e);
            return ResponseEntity.status(500).body(new TestEmailResponse("Erro ao limpar cache: " + e.getMessage()));
        }
    }

    @GetMapping("/test-logo")
    public ResponseEntity<?> testLogo() {
        try {
            // Primeiro limpa o cache
            templateService.clearLogoCache();
            
            // Gera um template de teste
            String htmlContent = templateService.generateSimpleEmailHtml("Teste Logo", "Este é um teste da logo.");
            
            // Verifica se a logo está presente no HTML
            boolean hasLogo = htmlContent.contains("data:image/png;base64,");
            
            log.info("Logo test - Has logo: {}", hasLogo);
            
            return ResponseEntity.ok().body(new TestLogoResponse(
                hasLogo,
                hasLogo ? "Logo carregada com sucesso!" : "Logo não encontrada no template",
                htmlContent.length()
            ));
        } catch (Exception e) {
            log.error("Failed to test logo", e);
            return ResponseEntity.status(500).body(new TestEmailResponse("Erro ao testar logo: " + e.getMessage()));
        }
    }

    public record TestLogoResponse(boolean hasLogo, String message, int htmlLength) {}

    public record TestEmailRequest(@NotBlank @Email String email) {}
    
    public record TestEmailResponse(String message) {}
}