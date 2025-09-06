package com.automo.user.repository;

import com.automo.accountType.entity.AccountType;
import com.automo.auth.entity.Auth;
import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import com.automo.user.entity.User;
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
@DisplayName("Tests for UserRepository")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private Auth testAuth;
    private AccountType accountType;
    private State activeState;
    private State eliminatedState;
    private User testUser;

    @BeforeEach
    void setUp() {
        testAuth = TestDataFactory.createValidAuth();
        testAuth = entityManager.persistAndFlush(testAuth);

        accountType = TestDataFactory.createIndividualAccountType();
        accountType = entityManager.persistAndFlush(accountType);

        activeState = TestDataFactory.createActiveState();
        activeState = entityManager.persistAndFlush(activeState);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = entityManager.persistAndFlush(eliminatedState);

        testUser = TestDataFactory.createValidUser(testAuth, accountType, activeState);
        testUser = entityManager.persistAndFlush(testUser);

        entityManager.clear();
    }

    @Test
    @DisplayName("Should find user by id successfully")
    void shouldFindUserByIdSuccessfully() {
        Optional<User> found = userRepository.findById(testUser.getId());

        assertTrue(found.isPresent());
        assertEquals(testUser.getName(), found.get().getName());
        assertEquals(testUser.getContact(), found.get().getContact());
        assertEquals(testUser.getAuth().getId(), found.get().getAuth().getId());
    }

    @Test
    @DisplayName("Should find users by state id")
    void shouldFindUsersByStateId() {
        // Create another user with the same state
        User anotherUser = TestDataFactory.createValidUser(testAuth, accountType, activeState);
        anotherUser.setName("Another User");
        anotherUser.setContact("913456789");
        entityManager.persistAndFlush(anotherUser);

        // Create user with different state
        User eliminatedUser = TestDataFactory.createValidUser(testAuth, accountType, eliminatedState);
        eliminatedUser.setName("Eliminated User");
        eliminatedUser.setContact("914567890");
        entityManager.persistAndFlush(eliminatedUser);

        entityManager.clear();

        List<User> activeUsers = userRepository.findByStateId(activeState.getId());
        List<User> eliminatedUsers = userRepository.findByStateId(eliminatedState.getId());

        assertEquals(2, activeUsers.size());
        assertEquals(1, eliminatedUsers.size());

        assertTrue(activeUsers.stream().anyMatch(u -> u.getName().equals("Test User")));
        assertTrue(activeUsers.stream().anyMatch(u -> u.getName().equals("Another User")));
        assertTrue(eliminatedUsers.stream().anyMatch(u -> u.getName().equals("Eliminated User")));
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        User newUser = TestDataFactory.createValidUser(testAuth, accountType, activeState);
        newUser.setName("New User");
        newUser.setContact("915678901");

        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser.getId());
        assertEquals("New User", savedUser.getName());
        assertEquals("915678901", savedUser.getContact());
        assertEquals(testAuth.getId(), savedUser.getAuth().getId());
        assertEquals(accountType.getId(), savedUser.getAccountType().getId());
        assertEquals(activeState.getId(), savedUser.getState().getId());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        testUser.setName("Updated User Name");
        testUser.setContact("919876543");

        User updatedUser = userRepository.save(testUser);

        assertEquals("Updated User Name", updatedUser.getName());
        assertEquals("919876543", updatedUser.getContact());
        assertEquals(testUser.getId(), updatedUser.getId());
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        Long userId = testUser.getId();

        userRepository.delete(testUser);
        entityManager.flush();

        Optional<User> deletedUser = userRepository.findById(userId);
        assertFalse(deletedUser.isPresent());
    }

    @Test
    @DisplayName("Should find all users")
    void shouldFindAllUsers() {
        // Create additional users
        User user2 = TestDataFactory.createValidUser(testAuth, accountType, activeState);
        user2.setName("User Two");
        user2.setContact("912222222");
        entityManager.persistAndFlush(user2);

        User user3 = TestDataFactory.createValidUser(testAuth, accountType, eliminatedState);
        user3.setName("User Three");
        user3.setContact("913333333");
        entityManager.persistAndFlush(user3);

        entityManager.clear();

        List<User> allUsers = userRepository.findAll();

        assertEquals(3, allUsers.size());
        assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("Test User")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("User Two")));
        assertTrue(allUsers.stream().anyMatch(u -> u.getName().equals("User Three")));
    }

    @Test
    @DisplayName("Should return empty list when no users exist for state")
    void shouldReturnEmptyListWhenNoUsersExistForState() {
        State newState = new State();
        newState.setState("PENDING");
        newState = entityManager.persistAndFlush(newState);

        List<User> users = userRepository.findByStateId(newState.getId());

        assertTrue(users.isEmpty());
    }

    @Test
    @DisplayName("Should handle cascade operations with auth entity")
    void shouldHandleCascadeOperationsWithAuthEntity() {
        // Verify that user is properly linked to auth
        Optional<User> foundUser = userRepository.findById(testUser.getId());
        assertTrue(foundUser.isPresent());
        
        Auth linkedAuth = foundUser.get().getAuth();
        assertNotNull(linkedAuth);
        assertEquals(testAuth.getEmail(), linkedAuth.getEmail());
    }

    @Test
    @DisplayName("Should handle different account types")
    void shouldHandleDifferentAccountTypes() {
        AccountType corporateType = TestDataFactory.createCorporateAccountType();
        corporateType = entityManager.persistAndFlush(corporateType);

        User corporateUser = TestDataFactory.createValidUser(testAuth, corporateType, activeState);
        corporateUser.setName("Corporate User");
        corporateUser.setContact("916789012");
        corporateUser = entityManager.persistAndFlush(corporateUser);

        entityManager.clear();

        Optional<User> foundUser = userRepository.findById(corporateUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("CORPORATE", foundUser.get().getAccountType().getAccountType());
    }

    @Test
    @DisplayName("Should persist and retrieve timestamps correctly")
    void shouldPersistAndRetrieveTimestampsCorrectly() {
        Optional<User> foundUser = userRepository.findById(testUser.getId());

        assertTrue(foundUser.isPresent());
        User user = foundUser.get();
        
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
        
        // Update the user to test updatedAt
        user.setName("Updated Name");
        User savedUser = userRepository.save(user);
        
        assertNotNull(savedUser.getUpdatedAt());
        assertTrue(savedUser.getUpdatedAt().isAfter(savedUser.getCreatedAt()) || 
                   savedUser.getUpdatedAt().isEqual(savedUser.getCreatedAt()));
    }
}