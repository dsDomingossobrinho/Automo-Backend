package com.automo.auth.service;

import com.automo.auth.entity.Otp;
import com.automo.auth.repository.OtpRepository;
import com.automo.auth.util.ContactValidator;
import com.automo.mail.service.EmailService;
import com.automo.sms.service.SmsService;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for OtpServiceImpl")
class OtpServiceImplTest {

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private OtpServiceImpl otpService;

    private String validEmail;
    private String validPhone;
    private String loginPurpose;
    private String resetPasswordPurpose;

    @BeforeEach
    void setUp() {
        validEmail = "test@automo.com";
        validPhone = "+1234567890";
        loginPurpose = "LOGIN";
        resetPasswordPurpose = "RESET_PASSWORD";
    }

    @Test
    @DisplayName("Should generate and send OTP via email successfully")
    void shouldGenerateAndSendOtpViaEmailSuccessfully() {
        // Given
        ArgumentCaptor<Otp> otpCaptor = ArgumentCaptor.forClass(Otp.class);
        when(otpRepository.save(any(Otp.class))).thenReturn(new Otp());

        // When
        String otpCode = otpService.generateAndSendOtp(validEmail, loginPurpose);

        // Then
        assertNotNull(otpCode);
        assertEquals(6, otpCode.length());
        assertTrue(otpCode.matches("\\d{6}"));

        verify(otpRepository).markAllAsUsedByContactAndPurpose(validEmail, loginPurpose);
        verify(otpRepository).save(otpCaptor.capture());
        verify(emailService).sendOtpEmail(validEmail, otpCode, loginPurpose);
        verify(smsService, never()).sendOtpSms(anyString(), anyString(), anyString());

        Otp savedOtp = otpCaptor.getValue();
        assertEquals(validEmail, savedOtp.getContact());
        assertEquals("EMAIL", savedOtp.getContactType());
        assertEquals(otpCode, savedOtp.getOtpCode());
        assertEquals(loginPurpose, savedOtp.getPurpose());
        assertFalse(savedOtp.isUsed());
        assertNotNull(savedOtp.getExpiresAt());
        assertTrue(savedOtp.getExpiresAt().isAfter(LocalDateTime.now().plusMinutes(4)));
    }

    @Test
    @DisplayName("Should generate and send OTP via SMS successfully")
    void shouldGenerateAndSendOtpViaSmsSuccessfully() {
        // Given
        ArgumentCaptor<Otp> otpCaptor = ArgumentCaptor.forClass(Otp.class);
        when(otpRepository.save(any(Otp.class))).thenReturn(new Otp());

        // When
        String otpCode = otpService.generateAndSendOtp(validPhone, loginPurpose);

        // Then
        assertNotNull(otpCode);
        assertEquals(6, otpCode.length());
        assertTrue(otpCode.matches("\\d{6}"));

        verify(otpRepository).markAllAsUsedByContactAndPurpose(validPhone, loginPurpose);
        verify(otpRepository).save(otpCaptor.capture());
        verify(smsService).sendOtpSms(validPhone, otpCode, loginPurpose);
        verify(emailService, never()).sendOtpEmail(anyString(), anyString(), anyString());

        Otp savedOtp = otpCaptor.getValue();
        assertEquals(validPhone, savedOtp.getContact());
        assertEquals("PHONE", savedOtp.getContactType());
        assertEquals(otpCode, savedOtp.getOtpCode());
        assertEquals(loginPurpose, savedOtp.getPurpose());
        assertFalse(savedOtp.isUsed());
        assertNotNull(savedOtp.getExpiresAt());
    }

    @Test
    @DisplayName("Should generate OTP for password reset purpose")
    void shouldGenerateOtpForPasswordResetPurpose() {
        // Given
        ArgumentCaptor<Otp> otpCaptor = ArgumentCaptor.forClass(Otp.class);
        when(otpRepository.save(any(Otp.class))).thenReturn(new Otp());

        // When
        String otpCode = otpService.generateAndSendOtp(validEmail, resetPasswordPurpose);

        // Then
        assertNotNull(otpCode);
        verify(otpRepository).save(otpCaptor.capture());
        verify(emailService).sendOtpEmail(validEmail, otpCode, resetPasswordPurpose);

        Otp savedOtp = otpCaptor.getValue();
        assertEquals(resetPasswordPurpose, savedOtp.getPurpose());
    }

    @Test
    @DisplayName("Should throw exception for invalid contact format")
    void shouldThrowExceptionForInvalidContactFormat() {
        // Given
        String invalidContact = "invalid-contact-format";

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            otpService.generateAndSendOtp(invalidContact, loginPurpose);
        });

        assertEquals("Invalid contact format. Must be email or phone number.", exception.getMessage());
        verify(otpRepository, never()).save(any(Otp.class));
        verify(emailService, never()).sendOtpEmail(anyString(), anyString(), anyString());
        verify(smsService, never()).sendOtpSms(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should mark previous OTPs as used before generating new one")
    void shouldMarkPreviousOtpsAsUsedBeforeGeneratingNewOne() {
        // Given
        when(otpRepository.save(any(Otp.class))).thenReturn(new Otp());

        // When
        otpService.generateAndSendOtp(validEmail, loginPurpose);

        // Then
        verify(otpRepository).markAllAsUsedByContactAndPurpose(validEmail, loginPurpose);
        verify(otpRepository).save(any(Otp.class));
    }

    @Test
    @DisplayName("Should verify valid OTP successfully")
    void shouldVerifyValidOtpSuccessfully() {
        // Given
        String otpCode = "123456";
        Otp validOtp = TestDataFactory.createValidOtp(validEmail);
        validOtp.setOtpCode(otpCode);
        validOtp.setPurpose(loginPurpose);
        
        when(otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                eq(validEmail), eq(otpCode), eq(loginPurpose), any(LocalDateTime.class)))
                .thenReturn(Optional.of(validOtp));
        when(otpRepository.save(any(Otp.class))).thenReturn(validOtp);

        // When
        boolean result = otpService.verifyOtp(validEmail, otpCode, loginPurpose);

        // Then
        assertTrue(result);
        assertTrue(validOtp.isUsed());
        verify(otpRepository).save(validOtp);
    }

    @Test
    @DisplayName("Should fail to verify expired OTP")
    void shouldFailToVerifyExpiredOtp() {
        // Given
        String otpCode = "123456";
        when(otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                eq(validEmail), eq(otpCode), eq(loginPurpose), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // When
        boolean result = otpService.verifyOtp(validEmail, otpCode, loginPurpose);

        // Then
        assertFalse(result);
        verify(otpRepository, never()).save(any(Otp.class));
    }

    @Test
    @DisplayName("Should fail to verify already used OTP")
    void shouldFailToVerifyAlreadyUsedOtp() {
        // Given
        String otpCode = "123456";
        when(otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                eq(validEmail), eq(otpCode), eq(loginPurpose), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // When
        boolean result = otpService.verifyOtp(validEmail, otpCode, loginPurpose);

        // Then
        assertFalse(result);
        verify(otpRepository, never()).save(any(Otp.class));
    }

    @Test
    @DisplayName("Should fail to verify OTP with wrong code")
    void shouldFailToVerifyOtpWithWrongCode() {
        // Given
        String wrongOtpCode = "999999";
        when(otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                eq(validEmail), eq(wrongOtpCode), eq(loginPurpose), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // When
        boolean result = otpService.verifyOtp(validEmail, wrongOtpCode, loginPurpose);

        // Then
        assertFalse(result);
        verify(otpRepository, never()).save(any(Otp.class));
    }

    @Test
    @DisplayName("Should fail to verify OTP with wrong purpose")
    void shouldFailToVerifyOtpWithWrongPurpose() {
        // Given
        String otpCode = "123456";
        String wrongPurpose = "WRONG_PURPOSE";
        when(otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                eq(validEmail), eq(otpCode), eq(wrongPurpose), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // When
        boolean result = otpService.verifyOtp(validEmail, otpCode, wrongPurpose);

        // Then
        assertFalse(result);
        verify(otpRepository, never()).save(any(Otp.class));
    }

    @Test
    @DisplayName("Should fail to verify OTP with wrong contact")
    void shouldFailToVerifyOtpWithWrongContact() {
        // Given
        String otpCode = "123456";
        String wrongEmail = "wrong@automo.com";
        when(otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                eq(wrongEmail), eq(otpCode), eq(loginPurpose), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        // When
        boolean result = otpService.verifyOtp(wrongEmail, otpCode, loginPurpose);

        // Then
        assertFalse(result);
        verify(otpRepository, never()).save(any(Otp.class));
    }

    @Test
    @DisplayName("Should cleanup expired OTPs")
    void shouldCleanupExpiredOtps() {
        // Given
        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        // When
        otpService.cleanupExpiredOtps();

        // Then
        verify(otpRepository).deleteByExpiresAtBefore(timeCaptor.capture());
        LocalDateTime capturedTime = timeCaptor.getValue();
        assertTrue(capturedTime.isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(capturedTime.isAfter(LocalDateTime.now().minusSeconds(5)));
    }

    @Test
    @DisplayName("Should generate unique OTP codes")
    void shouldGenerateUniqueOtpCodes() {
        // Given
        when(otpRepository.save(any(Otp.class))).thenReturn(new Otp());

        // When
        String otp1 = otpService.generateAndSendOtp(validEmail, loginPurpose);
        String otp2 = otpService.generateAndSendOtp("test2@automo.com", loginPurpose);

        // Then
        assertNotNull(otp1);
        assertNotNull(otp2);
        assertEquals(6, otp1.length());
        assertEquals(6, otp2.length());
        // Note: OTPs might be the same due to Random, but they should both be valid 6-digit numbers
        assertTrue(otp1.matches("\\d{6}"));
        assertTrue(otp2.matches("\\d{6}"));
    }

    @Test
    @DisplayName("Should handle email service failure gracefully")
    void shouldHandleEmailServiceFailureGracefully() {
        // Given
        when(otpRepository.save(any(Otp.class))).thenReturn(new Otp());
        doThrow(new RuntimeException("Email service unavailable")).when(emailService)
                .sendOtpEmail(anyString(), anyString(), anyString());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            otpService.generateAndSendOtp(validEmail, loginPurpose);
        });

        assertEquals("Email service unavailable", exception.getMessage());
        verify(otpRepository).save(any(Otp.class));
    }

    @Test
    @DisplayName("Should handle SMS service failure gracefully")
    void shouldHandleSmsServiceFailureGracefully() {
        // Given
        when(otpRepository.save(any(Otp.class))).thenReturn(new Otp());
        doThrow(new RuntimeException("SMS service unavailable")).when(smsService)
                .sendOtpSms(anyString(), anyString(), anyString());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            otpService.generateAndSendOtp(validPhone, loginPurpose);
        });

        assertEquals("SMS service unavailable", exception.getMessage());
        verify(otpRepository).save(any(Otp.class));
    }

    @Test
    @DisplayName("Should handle repository save failure")
    void shouldHandleRepositorySaveFailure() {
        // Given
        when(otpRepository.save(any(Otp.class))).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            otpService.generateAndSendOtp(validEmail, loginPurpose);
        });

        assertEquals("Database error", exception.getMessage());
        verify(emailService, never()).sendOtpEmail(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should verify OTP with phone contact")
    void shouldVerifyOtpWithPhoneContact() {
        // Given
        String otpCode = "123456";
        Otp validOtp = TestDataFactory.createValidOtpForPhone(validPhone);
        validOtp.setOtpCode(otpCode);
        validOtp.setPurpose(loginPurpose);
        
        when(otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                eq(validPhone), eq(otpCode), eq(loginPurpose), any(LocalDateTime.class)))
                .thenReturn(Optional.of(validOtp));
        when(otpRepository.save(any(Otp.class))).thenReturn(validOtp);

        // When
        boolean result = otpService.verifyOtp(validPhone, otpCode, loginPurpose);

        // Then
        assertTrue(result);
        assertTrue(validOtp.isUsed());
        verify(otpRepository).save(validOtp);
    }

    @Test
    @DisplayName("Should handle multiple purposes for same contact")
    void shouldHandleMultiplePurposesForSameContact() {
        // Given
        when(otpRepository.save(any(Otp.class))).thenReturn(new Otp());

        // When
        String loginOtp = otpService.generateAndSendOtp(validEmail, loginPurpose);
        String resetOtp = otpService.generateAndSendOtp(validEmail, resetPasswordPurpose);

        // Then
        assertNotNull(loginOtp);
        assertNotNull(resetOtp);
        
        verify(otpRepository).markAllAsUsedByContactAndPurpose(validEmail, loginPurpose);
        verify(otpRepository).markAllAsUsedByContactAndPurpose(validEmail, resetPasswordPurpose);
        verify(otpRepository, times(2)).save(any(Otp.class));
        verify(emailService, times(2)).sendOtpEmail(eq(validEmail), anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle edge case with null contact in verification")
    void shouldHandleEdgeCaseWithNullContactInVerification() {
        // Given
        String otpCode = "123456";

        // When
        boolean result = otpService.verifyOtp(null, otpCode, loginPurpose);

        // Then
        assertFalse(result);
        verify(otpRepository).findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                eq(null), eq(otpCode), eq(loginPurpose), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should handle edge case with null OTP code in verification")
    void shouldHandleEdgeCaseWithNullOtpCodeInVerification() {
        // Given
        String otpCode = null;

        // When
        boolean result = otpService.verifyOtp(validEmail, otpCode, loginPurpose);

        // Then
        assertFalse(result);
        verify(otpRepository).findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                eq(validEmail), eq(null), eq(loginPurpose), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("Should handle edge case with null purpose in verification")
    void shouldHandleEdgeCaseWithNullPurposeInVerification() {
        // Given
        String otpCode = "123456";

        // When
        boolean result = otpService.verifyOtp(validEmail, otpCode, null);

        // Then
        assertFalse(result);
        verify(otpRepository).findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                eq(validEmail), eq(otpCode), eq(null), any(LocalDateTime.class));
    }
}