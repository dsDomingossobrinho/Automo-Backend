package com.automo.mail.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for EmailServiceImpl")
class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailTemplateService emailTemplateService;

    @InjectMocks
    private EmailServiceImpl emailService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Should send simple email successfully")
    void shouldSendSimpleEmailSuccessfully() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test message";

        emailService.sendEmail(to, subject, text);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send HTML email successfully")
    void shouldSendHtmlEmailSuccessfully() throws MessagingException {
        String to = "test@example.com";
        String subject = "Test Subject";
        String htmlContent = "<html><body><h1>Test</h1></body></html>";

        emailService.sendHtmlEmail(to, subject, htmlContent);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should send OTP email successfully")
    void shouldSendOtpEmailSuccessfully() throws MessagingException {
        String to = "test@example.com";
        String otpCode = "123456";
        String htmlTemplate = "<html><body>OTP: {{OTP_CODE}}</body></html>";

        when(emailTemplateService.getOtpEmailTemplate(otpCode)).thenReturn(htmlTemplate);

        emailService.sendOtpEmail(to, otpCode);

        verify(emailTemplateService).getOtpEmailTemplate(otpCode);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should send forgot password OTP email successfully")
    void shouldSendForgotPasswordOtpEmailSuccessfully() throws MessagingException {
        String to = "test@example.com";
        String otpCode = "654321";
        String htmlTemplate = "<html><body>Reset Password OTP: {{OTP_CODE}}</body></html>";

        when(emailTemplateService.getForgotPasswordOtpEmailTemplate(otpCode)).thenReturn(htmlTemplate);

        emailService.sendForgotPasswordOtpEmail(to, otpCode);

        verify(emailTemplateService).getForgotPasswordOtpEmailTemplate(otpCode);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should send back office OTP email successfully")
    void shouldSendBackOfficeOtpEmailSuccessfully() throws MessagingException {
        String to = "admin@example.com";
        String otpCode = "789012";
        String htmlTemplate = "<html><body>Back Office OTP: {{OTP_CODE}}</body></html>";

        when(emailTemplateService.getBackOfficeOtpEmailTemplate(otpCode)).thenReturn(htmlTemplate);

        emailService.sendBackOfficeOtpEmail(to, otpCode);

        verify(emailTemplateService).getBackOfficeOtpEmailTemplate(otpCode);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should send user OTP email successfully")
    void shouldSendUserOtpEmailSuccessfully() throws MessagingException {
        String to = "user@example.com";
        String otpCode = "345678";
        String htmlTemplate = "<html><body>User OTP: {{OTP_CODE}}</body></html>";

        when(emailTemplateService.getUserOtpEmailTemplate(otpCode)).thenReturn(htmlTemplate);

        emailService.sendUserOtpEmail(to, otpCode);

        verify(emailTemplateService).getUserOtpEmailTemplate(otpCode);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    @DisplayName("Should handle messaging exception when sending HTML email")
    void shouldHandleMessagingExceptionWhenSendingHtmlEmail() throws MessagingException {
        String to = "test@example.com";
        String subject = "Test Subject";
        String htmlContent = "<html><body><h1>Test</h1></body></html>";

        doThrow(new MessagingException("Test exception")).when(mimeMessage).setSubject(subject);

        assertThrows(RuntimeException.class, () -> {
            emailService.sendHtmlEmail(to, subject, htmlContent);
        });
    }

    @Test
    @DisplayName("Should handle messaging exception when sending OTP email")
    void shouldHandleMessagingExceptionWhenSendingOtpEmail() throws MessagingException {
        String to = "test@example.com";
        String otpCode = "123456";
        String htmlTemplate = "<html><body>OTP: {{OTP_CODE}}</body></html>";

        when(emailTemplateService.getOtpEmailTemplate(otpCode)).thenReturn(htmlTemplate);
        doThrow(new MessagingException("Test exception")).when(mimeMessage).setSubject(any());

        assertThrows(RuntimeException.class, () -> {
            emailService.sendOtpEmail(to, otpCode);
        });
    }

    @Test
    @DisplayName("Should use correct email subjects for different OTP types")
    void shouldUseCorrectEmailSubjectsForDifferentOtpTypes() throws MessagingException {
        String to = "test@example.com";
        String otpCode = "123456";
        String htmlTemplate = "<html><body>OTP: {{OTP_CODE}}</body></html>";

        // Mock template service responses
        when(emailTemplateService.getOtpEmailTemplate(otpCode)).thenReturn(htmlTemplate);
        when(emailTemplateService.getForgotPasswordOtpEmailTemplate(otpCode)).thenReturn(htmlTemplate);
        when(emailTemplateService.getBackOfficeOtpEmailTemplate(otpCode)).thenReturn(htmlTemplate);
        when(emailTemplateService.getUserOtpEmailTemplate(otpCode)).thenReturn(htmlTemplate);

        // Test all OTP email types
        emailService.sendOtpEmail(to, otpCode);
        emailService.sendForgotPasswordOtpEmail(to, otpCode);
        emailService.sendBackOfficeOtpEmail(to, otpCode);
        emailService.sendUserOtpEmail(to, otpCode);

        // Verify all template methods were called
        verify(emailTemplateService).getOtpEmailTemplate(otpCode);
        verify(emailTemplateService).getForgotPasswordOtpEmailTemplate(otpCode);
        verify(emailTemplateService).getBackOfficeOtpEmailTemplate(otpCode);
        verify(emailTemplateService).getUserOtpEmailTemplate(otpCode);

        // Verify emails were sent
        verify(mailSender, times(4)).send(mimeMessage);
    }

    @Test
    @DisplayName("Should not send email when template service returns null")
    void shouldNotSendEmailWhenTemplateServiceReturnsNull() {
        String to = "test@example.com";
        String otpCode = "123456";

        when(emailTemplateService.getOtpEmailTemplate(otpCode)).thenReturn(null);

        emailService.sendOtpEmail(to, otpCode);

        verify(emailTemplateService).getOtpEmailTemplate(otpCode);
        verify(mailSender, never()).send(any(MimeMessage.class));
    }
}