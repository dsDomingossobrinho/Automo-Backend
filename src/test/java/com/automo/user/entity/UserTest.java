package com.automo.user.entity;

import com.automo.accountType.entity.AccountType;
import com.automo.auth.entity.Auth;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for User Entity")
class UserTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid User entity")
    void shouldCreateValidUserEntity() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        State state = TestDataFactory.createActiveState();
        User user = TestDataFactory.createValidUser(auth, accountType, state);
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Test User", user.getName());
        assertEquals("912345678", user.getContact());
        assertEquals(auth, user.getAuth());
        assertEquals(accountType, user.getAccountType());
        assertEquals(state, user.getState());
    }

    @Test
    @DisplayName("Should fail validation with null name")
    void shouldFailValidationWithNullName() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        State state = TestDataFactory.createActiveState();
        
        User user = new User();
        user.setName(null);
        user.setContact("912345678");
        user.setAuth(auth);
        user.setAccountType(accountType);
        user.setState(state);
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with blank name")
    void shouldFailValidationWithBlankName() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        State state = TestDataFactory.createActiveState();
        
        User user = new User();
        user.setName("");
        user.setContact("912345678");
        user.setAuth(auth);
        user.setAccountType(accountType);
        user.setState(state);
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    @DisplayName("Should fail validation with null contact")
    void shouldFailValidationWithNullContact() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        State state = TestDataFactory.createActiveState();
        
        User user = new User();
        user.setName("Test User");
        user.setContact(null);
        user.setAuth(auth);
        user.setAccountType(accountType);
        user.setState(state);
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("contact")));
    }

    @Test
    @DisplayName("Should fail validation with null auth")
    void shouldFailValidationWithNullAuth() {
        // Given
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        State state = TestDataFactory.createActiveState();
        
        User user = new User();
        user.setName("Test User");
        user.setContact("912345678");
        user.setAuth(null);
        user.setAccountType(accountType);
        user.setState(state);
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("auth")));
    }

    @Test
    @DisplayName("Should fail validation with null account type")
    void shouldFailValidationWithNullAccountType() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        User user = new User();
        user.setName("Test User");
        user.setContact("912345678");
        user.setAuth(auth);
        user.setAccountType(null);
        user.setState(state);
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("accountType")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        
        User user = new User();
        user.setName("Test User");
        user.setContact("912345678");
        user.setAuth(auth);
        user.setAccountType(accountType);
        user.setState(null);
        
        // When
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should create User with different account types")
    void shouldCreateUserWithDifferentAccountTypes() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        State state = TestDataFactory.createActiveState();
        
        AccountType individual = TestDataFactory.createIndividualAccountType();
        AccountType corporate = TestDataFactory.createCorporateAccountType();
        
        User user1 = TestDataFactory.createValidUser(auth, individual, state);
        User user2 = TestDataFactory.createValidUser(auth, corporate, state);
        
        // When
        Set<ConstraintViolation<User>> violations1 = validator.validate(user1);
        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals("INDIVIDUAL", user1.getAccountType().getAccountType());
        assertEquals("CORPORATE", user2.getAccountType().getAccountType());
    }

    @Test
    @DisplayName("Should create User with unique contacts")
    void shouldCreateUserWithUniqueContacts() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        State state = TestDataFactory.createActiveState();
        
        String phone1 = TestDataFactory.createUniquePhone();
        String phone2 = TestDataFactory.createUniquePhone();
        
        User user1 = TestDataFactory.createValidUser(auth, accountType, state);
        User user2 = TestDataFactory.createValidUser(auth, accountType, state);
        user1.setContact(phone1);
        user2.setContact(phone2);
        
        // When
        Set<ConstraintViolation<User>> violations1 = validator.validate(user1);
        Set<ConstraintViolation<User>> violations2 = validator.validate(user2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertNotEquals(user1.getContact(), user2.getContact());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        Auth auth = TestDataFactory.createValidAuth();
        AccountType accountType = TestDataFactory.createIndividualAccountType();
        State state = TestDataFactory.createActiveState();
        
        User user1 = TestDataFactory.createValidUser(auth, accountType, state);
        User user2 = TestDataFactory.createValidUser(auth, accountType, state);
        user1.setId(1L);
        user2.setId(1L);
        
        // Then
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
        
        // When different IDs
        user2.setId(2L);
        
        // Then
        assertNotEquals(user1, user2);
    }
}