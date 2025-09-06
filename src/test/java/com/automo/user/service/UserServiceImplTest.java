package com.automo.user.service;

import com.automo.accountType.entity.AccountType;
import com.automo.accountType.service.AccountTypeService;
import com.automo.auth.entity.Auth;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import com.automo.user.dto.UserDto;
import com.automo.user.entity.User;
import com.automo.user.repository.UserRepository;
import com.automo.user.response.UserResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StateService stateService;

    @Mock
    private AccountTypeService accountTypeService;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Auth testAuth;
    private State activeState;
    private State eliminatedState;
    private AccountType accountType;

    @BeforeEach
    void setUp() {
        testAuth = TestDataFactory.createValidAuth();
        testAuth.setId(1L);
        
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        accountType = TestDataFactory.createIndividualAccountType();
        accountType.setId(1L);
        
        testUser = TestDataFactory.createValidUser(testAuth, accountType, activeState);
        testUser.setId(1L);
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        UserDto userDto = new UserDto("John Doe", "912345678", 1L, 1L);
        
        when(stateService.findById(1L)).thenReturn(activeState);
        when(accountTypeService.findById(1L)).thenReturn(accountType);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse result = userService.createUser(userDto, testAuth);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.name());
        assertEquals("912345678", result.contact());
        assertEquals(1L, result.accountTypeId());
        assertEquals(1L, result.stateId());
        
        verify(stateService).findById(1L);
        verify(accountTypeService).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        Long userId = 1L;
        UserDto userDto = new UserDto("John Updated", "913456789", 1L, 1L);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(accountTypeService.findById(1L)).thenReturn(accountType);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserResponse result = userService.updateUser(userId, userDto);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing user")
    void shouldThrowExceptionWhenUpdatingNonExistingUser() {
        // Given
        Long userId = 999L;
        UserDto userDto = new UserDto("John Updated", "913456789", 1L, 1L);
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> userService.updateUser(userId, userDto));
        
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get all users excluding eliminated")
    void shouldGetAllUsersExcludingEliminated() {
        // Given
        User user1 = TestDataFactory.createValidUser(testAuth, accountType, activeState);
        user1.setId(1L);
        User user2 = TestDataFactory.createValidUser(testAuth, accountType, activeState);
        user2.setId(2L);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // When
        List<UserResponse> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        
        verify(stateService).getEliminatedState();
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Should get user by id successfully")
    void shouldGetUserByIdSuccessfully() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserById(userId);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existing user")
    void shouldThrowExceptionWhenGettingNonExistingUser() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> userService.getUserById(userId));
        
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should get user by id response successfully")
    void shouldGetUserByIdResponseSuccessfully() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        UserResponse result = userService.getUserByIdResponse(userId);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getName(), result.name());
        assertEquals(testUser.getContact(), result.contact());
        
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should get users by state successfully")
    void shouldGetUsersByStateSuccessfully() {
        // Given
        Long stateId = 1L;
        List<User> users = Arrays.asList(testUser);
        
        when(userRepository.findByStateId(stateId)).thenReturn(users);

        // When
        List<UserResponse> result = userService.getUsersByState(stateId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getName(), result.get(0).name());
        
        verify(userRepository).findByStateId(stateId);
    }

    @Test
    @DisplayName("Should soft delete user successfully")
    void shouldSoftDeleteUserSuccessfully() {
        // Given
        Long userId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deleteUser(userId);

        // Then
        verify(userRepository).findById(userId);
        verify(stateService).getEliminatedState();
        verify(userRepository).save(testUser);
        assertEquals(eliminatedState, testUser.getState());
    }

    @Test
    @DisplayName("Should implement findById method correctly")
    void shouldImplementFindByIdMethodCorrectly() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findById(userId);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should implement findByIdAndStateId method correctly")
    void shouldImplementFindByIdAndStateIdMethodCorrectly() {
        // Given
        Long userId = 1L;
        Long stateId = 1L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByIdAndStateId(userId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should throw exception in findByIdAndStateId when states don't match")
    void shouldThrowExceptionInFindByIdAndStateIdWhenStatesDontMatch() {
        // Given
        Long userId = 1L;
        Long stateId = 2L; // Different from user's state (1L)
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> userService.findByIdAndStateId(userId, stateId));
        
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should use default state in findByIdAndStateId when stateId is null")
    void shouldUseDefaultStateInFindByIdAndStateIdWhenStateIdIsNull() {
        // Given
        Long userId = 1L;
        Long stateId = null;
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.findByIdAndStateId(userId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);
        verify(userRepository).findById(userId);
    }
}