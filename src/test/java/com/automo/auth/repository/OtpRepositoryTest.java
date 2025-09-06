package com.automo.auth.repository;

import com.automo.auth.entity.Otp;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for OtpRepository")
class OtpRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OtpRepository otpRepository;

    private String testEmail;
    private String testPhone;
    private String loginPurpose;
    private String resetPasswordPurpose;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        testEmail = "test@automo.com";
        testPhone = "+1234567890";
        loginPurpose = "LOGIN";
        resetPasswordPurpose = "RESET_PASSWORD";
        now = LocalDateTime.now();
    }

    @Test
    @DisplayName("Should find valid OTP by contact, code, purpose and not used")
    void shouldFindValidOtpByContactCodePurposeAndNotUsed() {
        // Given
        String otpCode = "123456";
        Otp validOtp = TestDataFactory.createValidOtp(testEmail);
        validOtp.setOtpCode(otpCode);
        validOtp.setPurpose(loginPurpose);
        validOtp.setExpiresAt(now.plusMinutes(5));
        validOtp.setUsed(false);
        
        entityManager.persistAndFlush(validOtp);

        // When
        Optional<Otp> result = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testEmail, otpCode, loginPurpose, now);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testEmail, result.get().getContact());
        assertEquals(otpCode, result.get().getOtpCode());
        assertEquals(loginPurpose, result.get().getPurpose());
        assertFalse(result.get().isUsed());
        assertTrue(result.get().getExpiresAt().isAfter(now));
    }

    @Test
    @DisplayName("Should not find expired OTP")
    void shouldNotFindExpiredOtp() {
        // Given
        String otpCode = "123456";
        Otp expiredOtp = TestDataFactory.createValidOtp(testEmail);
        expiredOtp.setOtpCode(otpCode);
        expiredOtp.setPurpose(loginPurpose);
        expiredOtp.setExpiresAt(now.minusMinutes(5));
        expiredOtp.setUsed(false);
        
        entityManager.persistAndFlush(expiredOtp);

        // When
        Optional<Otp> result = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testEmail, otpCode, loginPurpose, now);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should not find used OTP")
    void shouldNotFindUsedOtp() {
        // Given
        String otpCode = "123456";
        Otp usedOtp = TestDataFactory.createValidOtp(testEmail);
        usedOtp.setOtpCode(otpCode);
        usedOtp.setPurpose(loginPurpose);
        usedOtp.setExpiresAt(now.plusMinutes(5));
        usedOtp.setUsed(true);
        
        entityManager.persistAndFlush(usedOtp);

        // When
        Optional<Otp> result = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testEmail, otpCode, loginPurpose, now);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should not find OTP with wrong contact")
    void shouldNotFindOtpWithWrongContact() {
        // Given
        String otpCode = "123456";
        Otp otp = TestDataFactory.createValidOtp(testEmail);
        otp.setOtpCode(otpCode);
        otp.setPurpose(loginPurpose);
        otp.setExpiresAt(now.plusMinutes(5));
        otp.setUsed(false);
        
        entityManager.persistAndFlush(otp);

        // When
        Optional<Otp> result = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                "wrong@automo.com", otpCode, loginPurpose, now);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should not find OTP with wrong code")
    void shouldNotFindOtpWithWrongCode() {
        // Given
        String otpCode = "123456";
        Otp otp = TestDataFactory.createValidOtp(testEmail);
        otp.setOtpCode(otpCode);
        otp.setPurpose(loginPurpose);
        otp.setExpiresAt(now.plusMinutes(5));
        otp.setUsed(false);
        
        entityManager.persistAndFlush(otp);

        // When
        Optional<Otp> result = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testEmail, "999999", loginPurpose, now);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should not find OTP with wrong purpose")
    void shouldNotFindOtpWithWrongPurpose() {
        // Given
        String otpCode = "123456";
        Otp otp = TestDataFactory.createValidOtp(testEmail);
        otp.setOtpCode(otpCode);
        otp.setPurpose(loginPurpose);
        otp.setExpiresAt(now.plusMinutes(5));
        otp.setUsed(false);
        
        entityManager.persistAndFlush(otp);

        // When
        Optional<Otp> result = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testEmail, otpCode, "WRONG_PURPOSE", now);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find phone OTP correctly")
    void shouldFindPhoneOtpCorrectly() {
        // Given
        String otpCode = "123456";
        Otp phoneOtp = TestDataFactory.createValidOtpForPhone(testPhone);
        phoneOtp.setOtpCode(otpCode);
        phoneOtp.setPurpose(loginPurpose);
        phoneOtp.setExpiresAt(now.plusMinutes(5));
        phoneOtp.setUsed(false);
        
        entityManager.persistAndFlush(phoneOtp);

        // When
        Optional<Otp> result = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testPhone, otpCode, loginPurpose, now);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testPhone, result.get().getContact());
        assertEquals("PHONE", result.get().getContactType());
    }

    @Test
    @DisplayName("Should mark all OTPs as used by contact and purpose")
    void shouldMarkAllOtpsAsUsedByContactAndPurpose() {
        // Given
        Otp otp1 = TestDataFactory.createValidOtp(testEmail);
        otp1.setPurpose(loginPurpose);
        otp1.setUsed(false);
        
        Otp otp2 = TestDataFactory.createValidOtp(testEmail);
        otp2.setPurpose(loginPurpose);
        otp2.setUsed(false);
        
        Otp otp3 = TestDataFactory.createValidOtp(testEmail);
        otp3.setPurpose(resetPasswordPurpose);
        otp3.setUsed(false);
        
        entityManager.persistAndFlush(otp1);
        entityManager.persistAndFlush(otp2);
        entityManager.persistAndFlush(otp3);

        // When
        otpRepository.markAllAsUsedByContactAndPurpose(testEmail, loginPurpose);
        entityManager.flush();
        entityManager.clear();

        // Then
        Otp refreshedOtp1 = entityManager.find(Otp.class, otp1.getId());
        Otp refreshedOtp2 = entityManager.find(Otp.class, otp2.getId());
        Otp refreshedOtp3 = entityManager.find(Otp.class, otp3.getId());
        
        assertTrue(refreshedOtp1.isUsed());
        assertTrue(refreshedOtp2.isUsed());
        assertFalse(refreshedOtp3.isUsed()); // Different purpose, should remain unused
    }

    @Test
    @DisplayName("Should delete expired OTPs")
    void shouldDeleteExpiredOtps() {
        // Given
        Otp validOtp = TestDataFactory.createValidOtp(testEmail);
        validOtp.setExpiresAt(now.plusMinutes(5));
        
        Otp expiredOtp1 = TestDataFactory.createValidOtp("test2@automo.com");
        expiredOtp1.setExpiresAt(now.minusMinutes(10));
        
        Otp expiredOtp2 = TestDataFactory.createValidOtp("test3@automo.com");
        expiredOtp2.setExpiresAt(now.minusMinutes(5));
        
        entityManager.persistAndFlush(validOtp);
        entityManager.persistAndFlush(expiredOtp1);
        entityManager.persistAndFlush(expiredOtp2);

        // When
        otpRepository.deleteByExpiresAtBefore(now);
        entityManager.flush();

        // Then
        List<Otp> remainingOtps = otpRepository.findAll();
        assertEquals(1, remainingOtps.size());
        assertEquals(validOtp.getId(), remainingOtps.get(0).getId());
    }

    @Test
    @DisplayName("Should handle multiple OTPs with same contact but different purposes")
    void shouldHandleMultipleOtpsWithSameContactButDifferentPurposes() {
        // Given
        String otpCode1 = "123456";
        String otpCode2 = "654321";
        
        Otp loginOtp = TestDataFactory.createValidOtp(testEmail);
        loginOtp.setOtpCode(otpCode1);
        loginOtp.setPurpose(loginPurpose);
        loginOtp.setExpiresAt(now.plusMinutes(5));
        loginOtp.setUsed(false);
        
        Otp resetOtp = TestDataFactory.createValidOtp(testEmail);
        resetOtp.setOtpCode(otpCode2);
        resetOtp.setPurpose(resetPasswordPurpose);
        resetOtp.setExpiresAt(now.plusMinutes(5));
        resetOtp.setUsed(false);
        
        entityManager.persistAndFlush(loginOtp);
        entityManager.persistAndFlush(resetOtp);

        // When
        Optional<Otp> loginResult = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testEmail, otpCode1, loginPurpose, now);
        Optional<Otp> resetResult = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testEmail, otpCode2, resetPasswordPurpose, now);

        // Then
        assertTrue(loginResult.isPresent());
        assertTrue(resetResult.isPresent());
        assertEquals(loginPurpose, loginResult.get().getPurpose());
        assertEquals(resetPasswordPurpose, resetResult.get().getPurpose());
    }

    @Test
    @DisplayName("Should handle OTP with exact expiration time")
    void shouldHandleOtpWithExactExpirationTime() {
        // Given
        String otpCode = "123456";
        LocalDateTime exactTime = now;
        
        Otp otp = TestDataFactory.createValidOtp(testEmail);
        otp.setOtpCode(otpCode);
        otp.setPurpose(loginPurpose);
        otp.setExpiresAt(exactTime);
        otp.setUsed(false);
        
        entityManager.persistAndFlush(otp);

        // When - Query with the exact same time (should not find it as we need expiresAt AFTER the given time)
        Optional<Otp> result = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testEmail, otpCode, loginPurpose, exactTime);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should save and retrieve OTP with all properties")
    void shouldSaveAndRetrieveOtpWithAllProperties() {
        // Given
        Otp otp = new Otp();
        otp.setContact(testEmail);
        otp.setContactType("EMAIL");
        otp.setOtpCode("123456");
        otp.setPurpose(loginPurpose);
        otp.setExpiresAt(now.plusMinutes(5));
        otp.setUsed(false);

        // When
        Otp savedOtp = otpRepository.saveAndFlush(otp);
        Optional<Otp> retrievedOtp = otpRepository.findById(savedOtp.getId());

        // Then
        assertTrue(retrievedOtp.isPresent());
        assertEquals(testEmail, retrievedOtp.get().getContact());
        assertEquals("EMAIL", retrievedOtp.get().getContactType());
        assertEquals("123456", retrievedOtp.get().getOtpCode());
        assertEquals(loginPurpose, retrievedOtp.get().getPurpose());
        assertFalse(retrievedOtp.get().isUsed());
        assertNotNull(retrievedOtp.get().getId());
        assertNotNull(retrievedOtp.get().getCreatedAt());
        assertNotNull(retrievedOtp.get().getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle bulk operations correctly")
    void shouldHandleBulkOperationsCorrectly() {
        // Given
        Otp otp1 = TestDataFactory.createValidOtp(testEmail);
        otp1.setPurpose(loginPurpose);
        
        Otp otp2 = TestDataFactory.createValidOtp(testEmail);
        otp2.setPurpose(loginPurpose);
        
        Otp otp3 = TestDataFactory.createValidOtp("different@automo.com");
        otp3.setPurpose(loginPurpose);
        
        List<Otp> otps = List.of(otp1, otp2, otp3);
        otpRepository.saveAllAndFlush(otps);

        // When
        List<Otp> allOtps = otpRepository.findAll();

        // Then
        assertEquals(3, allOtps.size());
        assertTrue(allOtps.stream().allMatch(otp -> otp.getId() != null));
    }

    @Test
    @DisplayName("Should handle edge cases with null values in query")
    void shouldHandleEdgeCasesWithNullValuesInQuery() {
        // Given
        String otpCode = "123456";
        Otp otp = TestDataFactory.createValidOtp(testEmail);
        otp.setOtpCode(otpCode);
        entityManager.persistAndFlush(otp);

        // When
        Optional<Otp> result1 = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                null, otpCode, loginPurpose, now);
        Optional<Otp> result2 = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testEmail, null, loginPurpose, now);
        Optional<Otp> result3 = otpRepository.findByContactAndOtpCodeAndPurposeAndUsedFalseAndExpiresAtAfter(
                testEmail, otpCode, null, now);

        // Then
        assertFalse(result1.isPresent());
        assertFalse(result2.isPresent());
        assertFalse(result3.isPresent());
    }

    @Test
    @DisplayName("Should handle deletion of non-existent expired OTPs")
    void shouldHandleDeletionOfNonExistentExpiredOtps() {
        // Given - No OTPs in database
        
        // When
        otpRepository.deleteByExpiresAtBefore(now);
        
        // Then - Should not throw exception
        List<Otp> allOtps = otpRepository.findAll();
        assertEquals(0, allOtps.size());
    }

    @Test
    @DisplayName("Should update OTP used status correctly")
    void shouldUpdateOtpUsedStatusCorrectly() {
        // Given
        Otp otp = TestDataFactory.createValidOtp(testEmail);
        otp.setUsed(false);
        Otp savedOtp = entityManager.persistAndFlush(otp);

        // When
        savedOtp.setUsed(true);
        otpRepository.saveAndFlush(savedOtp);
        entityManager.clear();

        // Then
        Otp updatedOtp = otpRepository.findById(savedOtp.getId()).orElseThrow();
        assertTrue(updatedOtp.isUsed());
    }
}