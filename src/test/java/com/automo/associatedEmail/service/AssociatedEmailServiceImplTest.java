package com.automo.associatedEmail.service;

import com.automo.associatedEmail.dto.AssociatedEmailDto;
import com.automo.associatedEmail.entity.AssociatedEmail;
import com.automo.associatedEmail.repository.AssociatedEmailRepository;
import com.automo.associatedEmail.response.AssociatedEmailResponse;
import com.automo.identifier.entity.Identifier;
import com.automo.identifier.service.IdentifierService;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.utils.TestDataFactory;
import com.automo.user.entity.User;
import com.automo.user.service.UserService;
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
@DisplayName("Tests for AssociatedEmailServiceImpl")
class AssociatedEmailServiceImplTest {

    @Mock
    private AssociatedEmailRepository associatedEmailRepository;

    @Mock
    private IdentifierService identifierService;

    @Mock
    private StateService stateService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AssociatedEmailServiceImpl associatedEmailService;

    private AssociatedEmail testAssociatedEmail;
    private Identifier testIdentifier;
    private State activeState;
    private State eliminatedState;
    private User testUser;
    private IdentifierType identifierType;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        identifierType = TestDataFactory.createNifIdentifierType();
        identifierType.setId(1L);
        
        testUser = TestDataFactory.createValidUser(
            TestDataFactory.createValidAuth(), 
            TestDataFactory.createIndividualAccountType(), 
            activeState
        );
        testUser.setId(1L);
        
        testIdentifier = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        testIdentifier.setId(1L);
        
        testAssociatedEmail = TestDataFactory.createValidAssociatedEmail(testIdentifier, activeState);
        testAssociatedEmail.setId(1L);
    }

    @Test
    @DisplayName("Should create associated email successfully")
    void shouldCreateAssociatedEmailSuccessfully() {
        // Given
        AssociatedEmailDto emailDto = TestDataFactory.createValidAssociatedEmailDto(1L, "test@automo.com", 1L);
        
        when(identifierService.findById(1L)).thenReturn(testIdentifier);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(associatedEmailRepository.save(any(AssociatedEmail.class))).thenReturn(testAssociatedEmail);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        AssociatedEmailResponse result = associatedEmailService.createAssociatedEmail(emailDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.identifierId());
        assertEquals("Test User", result.userName());
        assertEquals("test@automo.com", result.email());
        assertEquals(1L, result.stateId());
        assertEquals("ACTIVE", result.stateName());
        
        verify(identifierService).findById(1L);
        verify(stateService).findById(1L);
        verify(associatedEmailRepository).save(any(AssociatedEmail.class));
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should update associated email successfully")
    void shouldUpdateAssociatedEmailSuccessfully() {
        // Given
        Long emailId = 1L;
        AssociatedEmailDto emailDto = TestDataFactory.createValidAssociatedEmailDto(1L, "updated@automo.com", 1L);
        
        AssociatedEmail updatedEmail = TestDataFactory.createValidAssociatedEmail(testIdentifier, "updated@automo.com", activeState);
        updatedEmail.setId(1L);
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.of(testAssociatedEmail));
        when(identifierService.findById(1L)).thenReturn(testIdentifier);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(associatedEmailRepository.save(any(AssociatedEmail.class))).thenReturn(updatedEmail);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        AssociatedEmailResponse result = associatedEmailService.updateAssociatedEmail(emailId, emailDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("updated@automo.com", result.email());
        
        verify(associatedEmailRepository).findById(emailId);
        verify(identifierService).findById(1L);
        verify(stateService).findById(1L);
        verify(associatedEmailRepository).save(any(AssociatedEmail.class));
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent email")
    void shouldThrowExceptionWhenUpdatingNonExistentEmail() {
        // Given
        Long emailId = 999L;
        AssociatedEmailDto emailDto = TestDataFactory.createValidAssociatedEmailDto(1L, 1L);
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            associatedEmailService.updateAssociatedEmail(emailId, emailDto));
        
        verify(associatedEmailRepository).findById(emailId);
        verify(identifierService, never()).findById(anyLong());
        verify(stateService, never()).findById(anyLong());
        verify(associatedEmailRepository, never()).save(any(AssociatedEmail.class));
    }

    @Test
    @DisplayName("Should get all associated emails excluding eliminated")
    void shouldGetAllAssociatedEmailsExcludingEliminated() {
        // Given
        AssociatedEmail email1 = TestDataFactory.createValidAssociatedEmail(testIdentifier, activeState);
        email1.setId(1L);
        
        AssociatedEmail email2 = TestDataFactory.createValidAssociatedEmail(testIdentifier, activeState);
        email2.setId(2L);
        
        AssociatedEmail eliminatedEmail = TestDataFactory.createValidAssociatedEmail(testIdentifier, eliminatedState);
        eliminatedEmail.setId(3L);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(associatedEmailRepository.findAll())
            .thenReturn(Arrays.asList(email1, email2, eliminatedEmail));
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        List<AssociatedEmailResponse> result = associatedEmailService.getAllAssociatedEmails();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Should exclude eliminated email
        
        verify(stateService).getEliminatedState();
        verify(associatedEmailRepository).findAll();
    }

    @Test
    @DisplayName("Should get associated email by id")
    void shouldGetAssociatedEmailById() {
        // Given
        Long emailId = 1L;
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.of(testAssociatedEmail));

        // When
        AssociatedEmail result = associatedEmailService.getAssociatedEmailById(emailId);

        // Then
        assertNotNull(result);
        assertEquals(emailId, result.getId());
        assertEquals("test@automo.com", result.getEmail());
        
        verify(associatedEmailRepository).findById(emailId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent email by id")
    void shouldThrowExceptionWhenGettingNonExistentEmailById() {
        // Given
        Long emailId = 999L;
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            associatedEmailService.getAssociatedEmailById(emailId));
        
        verify(associatedEmailRepository).findById(emailId);
    }

    @Test
    @DisplayName("Should get associated email by id response")
    void shouldGetAssociatedEmailByIdResponse() {
        // Given
        Long emailId = 1L;
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.of(testAssociatedEmail));
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        AssociatedEmailResponse result = associatedEmailService.getAssociatedEmailByIdResponse(emailId);

        // Then
        assertNotNull(result);
        assertEquals(emailId, result.id());
        assertEquals("test@automo.com", result.email());
        assertEquals("Test User", result.userName());
        
        verify(associatedEmailRepository).findById(emailId);
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should get associated emails by identifier")
    void shouldGetAssociatedEmailsByIdentifier() {
        // Given
        Long identifierId = 1L;
        List<AssociatedEmail> emails = Arrays.asList(testAssociatedEmail);
        
        when(associatedEmailRepository.findByIdentifierId(identifierId)).thenReturn(emails);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        List<AssociatedEmailResponse> result = associatedEmailService.getAssociatedEmailsByIdentifier(identifierId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(identifierId, result.get(0).identifierId());
        
        verify(associatedEmailRepository).findByIdentifierId(identifierId);
    }

    @Test
    @DisplayName("Should get associated emails by state")
    void shouldGetAssociatedEmailsByState() {
        // Given
        Long stateId = 1L;
        List<AssociatedEmail> emails = Arrays.asList(testAssociatedEmail);
        
        when(associatedEmailRepository.findByStateId(stateId)).thenReturn(emails);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        List<AssociatedEmailResponse> result = associatedEmailService.getAssociatedEmailsByState(stateId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(stateId, result.get(0).stateId());
        
        verify(associatedEmailRepository).findByStateId(stateId);
    }

    @Test
    @DisplayName("Should get associated email by email address")
    void shouldGetAssociatedEmailByEmailAddress() {
        // Given
        String emailAddress = "test@automo.com";
        
        when(associatedEmailRepository.findByEmail(emailAddress)).thenReturn(Optional.of(testAssociatedEmail));
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        AssociatedEmailResponse result = associatedEmailService.getAssociatedEmailByEmail(emailAddress);

        // Then
        assertNotNull(result);
        assertEquals(emailAddress, result.email());
        assertEquals("Test User", result.userName());
        
        verify(associatedEmailRepository).findByEmail(emailAddress);
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when email address not found")
    void shouldThrowExceptionWhenEmailAddressNotFound() {
        // Given
        String emailAddress = "nonexistent@automo.com";
        
        when(associatedEmailRepository.findByEmail(emailAddress)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            associatedEmailService.getAssociatedEmailByEmail(emailAddress));
        
        verify(associatedEmailRepository).findByEmail(emailAddress);
        verify(userService, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should delete associated email (soft delete)")
    void shouldDeleteAssociatedEmailSoftDelete() {
        // Given
        Long emailId = 1L;
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.of(testAssociatedEmail));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);

        // When
        associatedEmailService.deleteAssociatedEmail(emailId);

        // Then
        verify(associatedEmailRepository).findById(emailId);
        verify(stateService).getEliminatedState();
        verify(associatedEmailRepository).save(testAssociatedEmail);
        assertEquals(eliminatedState, testAssociatedEmail.getState());
    }

    @Test
    @DisplayName("Should find by id for inter-service communication")
    void shouldFindByIdForInterServiceCommunication() {
        // Given
        Long emailId = 1L;
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.of(testAssociatedEmail));

        // When
        AssociatedEmail result = associatedEmailService.findById(emailId);

        // Then
        assertNotNull(result);
        assertEquals(emailId, result.getId());
        
        verify(associatedEmailRepository).findById(emailId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when finding by non-existent id")
    void shouldThrowEntityNotFoundExceptionWhenFindingByNonExistentId() {
        // Given
        Long emailId = 999L;
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> 
            associatedEmailService.findById(emailId));
        
        verify(associatedEmailRepository).findById(emailId);
    }

    @Test
    @DisplayName("Should find by id and state id")
    void shouldFindByIdAndStateId() {
        // Given
        Long emailId = 1L;
        Long stateId = 1L;
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.of(testAssociatedEmail));

        // When
        AssociatedEmail result = associatedEmailService.findByIdAndStateId(emailId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(emailId, result.getId());
        assertEquals(stateId, result.getState().getId());
        
        verify(associatedEmailRepository).findById(emailId);
    }

    @Test
    @DisplayName("Should use default state id when null")
    void shouldUseDefaultStateIdWhenNull() {
        // Given
        Long emailId = 1L;
        Long stateId = null; // Should default to 1L
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.of(testAssociatedEmail));

        // When
        AssociatedEmail result = associatedEmailService.findByIdAndStateId(emailId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(emailId, result.getId());
        assertEquals(1L, result.getState().getId()); // Should use default state id
        
        verify(associatedEmailRepository).findById(emailId);
    }

    @Test
    @DisplayName("Should throw exception when state id does not match")
    void shouldThrowExceptionWhenStateIdDoesNotMatch() {
        // Given
        Long emailId = 1L;
        Long stateId = 2L; // Different from testAssociatedEmail's state id (1L)
        
        when(associatedEmailRepository.findById(emailId)).thenReturn(Optional.of(testAssociatedEmail));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> 
            associatedEmailService.findByIdAndStateId(emailId, stateId));
        
        verify(associatedEmailRepository).findById(emailId);
    }

    @Test
    @DisplayName("Should handle user not found gracefully in mapping")
    void shouldHandleUserNotFoundGracefullyInMapping() {
        // Given
        AssociatedEmailDto emailDto = TestDataFactory.createValidAssociatedEmailDto(1L, 1L);
        
        when(identifierService.findById(1L)).thenReturn(testIdentifier);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(associatedEmailRepository.save(any(AssociatedEmail.class))).thenReturn(testAssociatedEmail);
        when(userService.findById(1L)).thenThrow(new EntityNotFoundException("User not found"));

        // When
        AssociatedEmailResponse result = associatedEmailService.createAssociatedEmail(emailDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNull(result.userName()); // Should be null when user not found
        
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should create multiple associated emails for same identifier")
    void shouldCreateMultipleAssociatedEmailsForSameIdentifier() {
        // Given
        AssociatedEmailDto emailDto1 = TestDataFactory.createValidAssociatedEmailDto(1L, "email1@automo.com", 1L);
        AssociatedEmailDto emailDto2 = TestDataFactory.createValidAssociatedEmailDto(1L, "email2@automo.com", 1L);
        
        AssociatedEmail email1 = TestDataFactory.createValidAssociatedEmail(testIdentifier, "email1@automo.com", activeState);
        AssociatedEmail email2 = TestDataFactory.createValidAssociatedEmail(testIdentifier, "email2@automo.com", activeState);
        email1.setId(1L);
        email2.setId(2L);
        
        when(identifierService.findById(1L)).thenReturn(testIdentifier);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(associatedEmailRepository.save(any(AssociatedEmail.class)))
            .thenReturn(email1)
            .thenReturn(email2);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        AssociatedEmailResponse result1 = associatedEmailService.createAssociatedEmail(emailDto1);
        AssociatedEmailResponse result2 = associatedEmailService.createAssociatedEmail(emailDto2);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("email1@automo.com", result1.email());
        assertEquals("email2@automo.com", result2.email());
        assertEquals(result1.identifierId(), result2.identifierId()); // Same identifier
        
        verify(associatedEmailRepository, times(2)).save(any(AssociatedEmail.class));
    }

    @Test
    @DisplayName("Should validate email format in business logic")
    void shouldValidateEmailFormatInBusinessLogic() {
        // Given
        AssociatedEmailDto emailDto = TestDataFactory.createValidAssociatedEmailDto(1L, "valid@email.com", 1L);
        AssociatedEmail validEmail = TestDataFactory.createValidAssociatedEmail(testIdentifier, "valid@email.com", activeState);
        validEmail.setId(1L);
        
        when(identifierService.findById(1L)).thenReturn(testIdentifier);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(associatedEmailRepository.save(any(AssociatedEmail.class))).thenReturn(validEmail);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        AssociatedEmailResponse result = associatedEmailService.createAssociatedEmail(emailDto);

        // Then
        assertNotNull(result);
        assertTrue(result.email().contains("@"));
        assertTrue(result.email().contains("."));
        
        verify(associatedEmailRepository).save(any(AssociatedEmail.class));
    }
}