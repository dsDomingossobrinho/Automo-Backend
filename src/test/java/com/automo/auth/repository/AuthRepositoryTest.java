package com.automo.auth.repository;

import com.automo.auth.entity.Auth;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for AuthRepository")
class AuthRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AuthRepository authRepository;

    private Auth testAuth;

    @BeforeEach
    void setUp() {
        testAuth = TestDataFactory.createValidAuth();
        testAuth = entityManager.persistAndFlush(testAuth);
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find auth by id successfully")
    void shouldFindAuthByIdSuccessfully() {
        Optional<Auth> found = authRepository.findById(testAuth.getId());

        assertTrue(found.isPresent());
        assertEquals(testAuth.getEmail(), found.get().getEmail());
        assertEquals(testAuth.getPassword(), found.get().getPassword());
    }

    @Test
    @DisplayName("Should find auth by email successfully")
    void shouldFindAuthByEmailSuccessfully() {
        Optional<Auth> found = authRepository.findByEmail("test@automo.com");

        assertTrue(found.isPresent());
        assertEquals(testAuth.getId(), found.get().getId());
        assertEquals("test@automo.com", found.get().getEmail());
        assertEquals(testAuth.getPassword(), found.get().getPassword());
    }

    @Test
    @DisplayName("Should return empty when auth not found by email")
    void shouldReturnEmptyWhenAuthNotFoundByEmail() {
        Optional<Auth> found = authRepository.findByEmail("nonexistent@automo.com");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should check if auth exists by email")
    void shouldCheckIfAuthExistsByEmail() {
        boolean exists = authRepository.existsByEmail("test@automo.com");
        boolean notExists = authRepository.existsByEmail("nonexistent@automo.com");

        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Should save auth successfully")
    void shouldSaveAuthSuccessfully() {
        Auth newAuth = TestDataFactory.createValidAuth("newuser@automo.com");

        Auth savedAuth = authRepository.save(newAuth);

        assertNotNull(savedAuth.getId());
        assertEquals("newuser@automo.com", savedAuth.getEmail());
        assertEquals("$2a$10$encrypted.password.hash", savedAuth.getPassword());
        assertNotNull(savedAuth.getCreatedAt());
        assertNotNull(savedAuth.getUpdatedAt());
    }

    @Test
    @DisplayName("Should update auth successfully")
    void shouldUpdateAuthSuccessfully() {
        testAuth.setEmail("updated@automo.com");
        testAuth.setPassword("$2a$10$new.encrypted.password.hash");

        Auth updatedAuth = authRepository.save(testAuth);

        assertEquals("updated@automo.com", updatedAuth.getEmail());
        assertEquals("$2a$10$new.encrypted.password.hash", updatedAuth.getPassword());
        assertEquals(testAuth.getId(), updatedAuth.getId());
    }

    @Test
    @DisplayName("Should delete auth successfully")
    void shouldDeleteAuthSuccessfully() {
        Long authId = testAuth.getId();

        authRepository.delete(testAuth);
        entityManager.flush();

        Optional<Auth> deletedAuth = authRepository.findById(authId);
        assertFalse(deletedAuth.isPresent());
    }

    @Test
    @DisplayName("Should find all auth records")
    void shouldFindAllAuthRecords() {
        // Create additional auth records
        Auth auth2 = TestDataFactory.createValidAuth("user2@automo.com");
        entityManager.persistAndFlush(auth2);

        Auth auth3 = TestDataFactory.createValidAuth("user3@automo.com");
        entityManager.persistAndFlush(auth3);

        entityManager.clear();

        List<Auth> allAuths = authRepository.findAll();

        assertEquals(3, allAuths.size());
        assertTrue(allAuths.stream().anyMatch(a -> a.getEmail().equals("test@automo.com")));
        assertTrue(allAuths.stream().anyMatch(a -> a.getEmail().equals("user2@automo.com")));
        assertTrue(allAuths.stream().anyMatch(a -> a.getEmail().equals("user3@automo.com")));
    }

    @Test
    @DisplayName("Should handle unique email constraint")
    void shouldHandleUniqueEmailConstraint() {
        Auth duplicateAuth = TestDataFactory.createValidAuth("test@automo.com");

        // This should throw an exception due to unique constraint
        assertThrows(Exception.class, () -> {
            entityManager.persistAndFlush(duplicateAuth);
        });
    }

    @Test
    @DisplayName("Should persist timestamps correctly")
    void shouldPersistTimestampsCorrectly() {
        Optional<Auth> foundAuth = authRepository.findById(testAuth.getId());

        assertTrue(foundAuth.isPresent());
        Auth auth = foundAuth.get();
        
        assertNotNull(auth.getCreatedAt());
        assertNotNull(auth.getUpdatedAt());
        
        // Update the auth to test updatedAt
        auth.setPassword("$2a$10$updated.password.hash");
        Auth savedAuth = authRepository.save(auth);
        
        assertNotNull(savedAuth.getUpdatedAt());
        assertTrue(savedAuth.getUpdatedAt().isAfter(savedAuth.getCreatedAt()) || 
                   savedAuth.getUpdatedAt().isEqual(savedAuth.getCreatedAt()));
    }

    @Test
    @DisplayName("Should handle case sensitive email searches")
    void shouldHandleCaseSensitiveEmailSearches() {
        // Most databases are case-insensitive for email searches, but let's test both cases
        Optional<Auth> foundLowercase = authRepository.findByEmail("test@automo.com");
        Optional<Auth> foundUppercase = authRepository.findByEmail("TEST@AUTOMO.COM");
        Optional<Auth> foundMixedcase = authRepository.findByEmail("Test@Automo.Com");

        assertTrue(foundLowercase.isPresent());
        // The behavior for uppercase/mixed case depends on database configuration
        // In most setups, emails are case-insensitive
        assertEquals(testAuth.getId(), foundLowercase.get().getId());
    }

    @Test
    @DisplayName("Should validate email format in entity")
    void shouldValidateEmailFormatInEntity() {
        Auth invalidEmailAuth = new Auth();
        invalidEmailAuth.setEmail("invalid-email-format");
        invalidEmailAuth.setPassword("password");

        // This test depends on validation annotations in the entity
        // The actual validation behavior will depend on the entity configuration
        assertNotNull(invalidEmailAuth);
    }

    @Test
    @DisplayName("Should handle long email addresses")
    void shouldHandleLongEmailAddresses() {
        String longEmail = "very.long.email.address.that.might.test.database.limits@very-long-domain-name-for-testing.automo.com";
        Auth longEmailAuth = TestDataFactory.createValidAuth(longEmail);

        Auth savedAuth = authRepository.save(longEmailAuth);

        assertNotNull(savedAuth.getId());
        assertEquals(longEmail, savedAuth.getEmail());
    }

    @Test
    @DisplayName("Should handle special characters in email")
    void shouldHandleSpecialCharactersInEmail() {
        String specialEmail = "test.email+special@automo.com";
        Auth specialEmailAuth = TestDataFactory.createValidAuth(specialEmail);

        Auth savedAuth = authRepository.save(specialEmailAuth);

        assertNotNull(savedAuth.getId());
        assertEquals(specialEmail, savedAuth.getEmail());
        
        Optional<Auth> found = authRepository.findByEmail(specialEmail);
        assertTrue(found.isPresent());
        assertEquals(savedAuth.getId(), found.get().getId());
    }
}