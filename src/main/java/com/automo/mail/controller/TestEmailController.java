package com.automo.mail.controller;

import com.automo.mail.service.EmailService;
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

    @PostMapping("/email")
    public ResponseEntity<?> testEmail(@RequestBody TestEmailRequest request) {
        try {
            log.info("Testing email send to: {}", request.email);
            
            String subject = "Teste de Configuração de Email - Automo";
            String content = """
                Olá!
                
                Este é um email de teste para verificar a configuração SMTP.
                
                Se você recebeu esta mensagem, significa que o sistema de email está funcionando corretamente.
                
                Configurações utilizadas:
                - SMTP: Gmail (smtp.gmail.com:587)
                - STARTTLS: Habilitado
                - Autenticação: Habilitado
                
                Atenciosamente,
                Equipe Técnica Automo
                """;
            
            emailService.sendSimpleEmail(request.email, subject, content);
            
            log.info("Test email sent successfully to: {}", request.email);
            return ResponseEntity.ok().body(new TestEmailResponse("Email de teste enviado com sucesso para: " + request.email));
            
        } catch (Exception e) {
            log.error("Failed to send test email to: {}", request.email, e);
            return ResponseEntity.status(500).body(new TestEmailResponse("Erro ao enviar email: " + e.getMessage()));
        }
    }

    public record TestEmailRequest(@NotBlank @Email String email) {}
    
    public record TestEmailResponse(String message) {}
}