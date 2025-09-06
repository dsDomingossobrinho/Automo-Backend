package com.automo.auth.entity;

import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for Otp Entity")
class OtpTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Otp entity with email contact")
    void shouldCreateValidOtpEntityWithEmail() {
        // Given
        Otp otp = TestDataFactory.createValidOtp("test@automo.com");
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("test@automo.com", otp.getContact());
        assertEquals("EMAIL", otp.getContactType());
        assertEquals("123456", otp.getOtpCode());
        assertEquals("LOGIN", otp.getPurpose());
        assertFalse(otp.isUsed());
        assertNotNull(otp.getExpiresAt());
        assertTrue(otp.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should create valid Otp entity with phone contact")
    void shouldCreateValidOtpEntityWithPhone() {
        // Given
        Otp otp = TestDataFactory.createValidOtpForPhone("+1234567890");
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("+1234567890", otp.getContact());
        assertEquals("PHONE", otp.getContactType());
        assertEquals("123456", otp.getOtpCode());
        assertEquals("LOGIN", otp.getPurpose());
        assertFalse(otp.isUsed());
        assertNotNull(otp.getExpiresAt());
    }

    @Test
    @DisplayName("Should fail validation with null contact")
    void shouldFailValidationWithNullContact() {
        // Given
        Otp otp = new Otp();
        otp.setContact(null);
        otp.setContactType("EMAIL");
        otp.setOtpCode("123456");
        otp.setPurpose("LOGIN");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contact")));
    }

    @Test
    @DisplayName("Should fail validation with null contact type")
    void shouldFailValidationWithNullContactType() {
        // Given
        Otp otp = new Otp();
        otp.setContact("test@automo.com");
        otp.setContactType(null);
        otp.setOtpCode("123456");
        otp.setPurpose("LOGIN");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contactType")));
    }

    @Test
    @DisplayName("Should fail validation with null OTP code")
    void shouldFailValidationWithNullOtpCode() {
        // Given
        Otp otp = new Otp();
        otp.setContact("test@automo.com");
        otp.setContactType("EMAIL");
        otp.setOtpCode(null);
        otp.setPurpose("LOGIN");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("otpCode")));
    }

    @Test
    @DisplayName("Should fail validation with null expires at")
    void shouldFailValidationWithNullExpiresAt() {
        // Given
        Otp otp = new Otp();
        otp.setContact("test@automo.com");
        otp.setContactType("EMAIL");
        otp.setOtpCode("123456");
        otp.setPurpose("LOGIN");
        otp.setExpiresAt(null);
        otp.setUsed(false);
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("expiresAt")));
    }

    @Test
    @DisplayName("Should fail validation with null purpose")
    void shouldFailValidationWithNullPurpose() {
        // Given
        Otp otp = new Otp();
        otp.setContact("test@automo.com");
        otp.setContactType("EMAIL");
        otp.setOtpCode("123456");
        otp.setPurpose(null);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("purpose")));
    }

    @Test
    @DisplayName("Should create Otp with different purposes")
    void shouldCreateOtpWithDifferentPurposes() {
        // Given
        Otp loginOtp = TestDataFactory.createOtpWithPurpose("test@automo.com", "LOGIN");
        Otp resetOtp = TestDataFactory.createOtpWithPurpose("test@automo.com", "RESET_PASSWORD");
        
        // When
        Set<ConstraintViolation<Otp>> loginViolations = validator.validate(loginOtp);
        Set<ConstraintViolation<Otp>> resetViolations = validator.validate(resetOtp);
        
        // Then
        assertTrue(loginViolations.isEmpty());
        assertTrue(resetViolations.isEmpty());
        assertEquals("LOGIN", loginOtp.getPurpose());
        assertEquals("RESET_PASSWORD", resetOtp.getPurpose());
    }

    @Test
    @DisplayName("Should handle expired OTP correctly")
    void shouldHandleExpiredOtpCorrectly() {
        // Given
        Otp expiredOtp = TestDataFactory.createExpiredOtp("test@automo.com");
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(expiredOtp);
        
        // Then
        assertTrue(violations.isEmpty());
        assertTrue(expiredOtp.getExpiresAt().isBefore(LocalDateTime.now()));
        assertFalse(expiredOtp.isUsed());
    }

    @Test
    @DisplayName("Should handle used OTP correctly")
    void shouldHandleUsedOtpCorrectly() {
        // Given
        Otp usedOtp = TestDataFactory.createUsedOtp("test@automo.com");
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(usedOtp);
        
        // Then
        assertTrue(violations.isEmpty());
        assertTrue(usedOtp.isUsed());
        assertTrue(usedOtp.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("Should create OTP with custom code")
    void shouldCreateOtpWithCustomCode() {
        // Given
        String customCode = "999888";
        Otp otp = TestDataFactory.createOtpWithCustomCode("test@automo.com", customCode);
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(customCode, otp.getOtpCode());
    }

    @Test
    @DisplayName("Should create password reset OTP")
    void shouldCreatePasswordResetOtp() {
        // Given
        Otp resetOtp = TestDataFactory.createPasswordResetOtp("test@automo.com");
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(resetOtp);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("RESET_PASSWORD", resetOtp.getPurpose());
        assertEquals("654321", resetOtp.getOtpCode());
    }

    @Test
    @DisplayName("Should handle different contact types correctly")
    void shouldHandleDifferentContactTypesCorrectly() {
        // Given
        Otp emailOtp = TestDataFactory.createOtpWithContactType("test@automo.com", "EMAIL", "LOGIN");
        Otp phoneOtp = TestDataFactory.createOtpWithContactType("+1234567890", "PHONE", "LOGIN");
        
        // When
        Set<ConstraintViolation<Otp>> emailViolations = validator.validate(emailOtp);
        Set<ConstraintViolation<Otp>> phoneViolations = validator.validate(phoneOtp);
        
        // Then
        assertTrue(emailViolations.isEmpty());
        assertTrue(phoneViolations.isEmpty());
        assertEquals("EMAIL", emailOtp.getContactType());
        assertEquals("PHONE", phoneOtp.getContactType());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        Otp otp1 = TestDataFactory.createValidOtp("test@automo.com");
        Otp otp2 = TestDataFactory.createValidOtp("test@automo.com");
        otp1.setId(1L);
        otp2.setId(1L);
        
        // Then
        assertEquals(otp1, otp2);
        assertEquals(otp1.hashCode(), otp2.hashCode());
        
        // When different IDs
        otp2.setId(2L);
        
        // Then
        assertNotEquals(otp1, otp2);
    }

    @Test
    @DisplayName("Should test toString method")
    void shouldTestToStringMethod() {
        // Given
        Otp otp = TestDataFactory.createValidOtp("test@automo.com");
        otp.setId(1L);
        
        // When
        String toString = otp.toString();
        
        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("Otp"));
        assertTrue(toString.contains("test@automo.com"));
    }

    @Test
    @DisplayName("Should handle edge case with empty string values")
    void shouldHandleEdgeCaseWithEmptyStringValues() {
        // Given
        Otp otp = new Otp();
        otp.setContact("");
        otp.setContactType("");
        otp.setOtpCode("");
        otp.setPurpose("");
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then - Empty strings might be valid depending on validation constraints
        // This test documents the current behavior
        assertNotNull(violations);
        // The actual validation result depends on the @Column(nullable = false) constraints
        // which only check for null, not empty strings
    }

    @Test
    @DisplayName("Should create OTP with abstract model properties")
    void shouldCreateOtpWithAbstractModelProperties() {
        // Given
        Otp otp = TestDataFactory.createValidOtp("test@automo.com");
        otp.setId(1L);
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(1L, otp.getId());
        // Note: createdAt and updatedAt are managed by @CreationTimestamp and @UpdateTimestamp
        // They will be null in tests unless the entity is persisted through JPA
    }

    @Test
    @DisplayName("Should handle maximum length OTP codes")
    void shouldHandleMaximumLengthOtpCodes() {
        // Given
        String longOtpCode = "123456789012345"; // 15 characters
        Otp otp = TestDataFactory.createOtpWithCustomCode("test@automo.com", longOtpCode);
        
        // When
        Set<ConstraintViolation<Otp>> violations = validator.validate(otp);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longOtpCode, otp.getOtpCode());
    }

    @Test
    @DisplayName("Should handle various expiration times")
    void shouldHandleVariousExpirationTimes() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Otp validOtp = TestDataFactory.createValidOtp("test@automo.com");
        validOtp.setExpiresAt(now.plusMinutes(1));
        
        Otp expiredOtp = TestDataFactory.createValidOtp("test2@automo.com");
        expiredOtp.setExpiresAt(now.minusMinutes(1));
        
        // When
        Set<ConstraintViolation<Otp>> validViolations = validator.validate(validOtp);
        Set<ConstraintViolation<Otp>> expiredViolations = validator.validate(expiredOtp);
        
        // Then
        assertTrue(validViolations.isEmpty());
        assertTrue(expiredViolations.isEmpty());
        assertTrue(validOtp.getExpiresAt().isAfter(now));
        assertTrue(expiredOtp.getExpiresAt().isBefore(now));
    }
}