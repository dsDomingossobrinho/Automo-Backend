package com.automo.associatedContact.service;

import com.automo.associatedContact.dto.AssociatedContactDto;
import com.automo.associatedContact.entity.AssociatedContact;
import com.automo.associatedContact.repository.AssociatedContactRepository;
import com.automo.associatedContact.response.AssociatedContactResponse;
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
@DisplayName("Tests for AssociatedContactServiceImpl")
class AssociatedContactServiceImplTest {

    @Mock
    private AssociatedContactRepository associatedContactRepository;

    @Mock
    private IdentifierService identifierService;

    @Mock
    private StateService stateService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AssociatedContactServiceImpl associatedContactService;

    private AssociatedContact testAssociatedContact;
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
        
        testAssociatedContact = TestDataFactory.createValidAssociatedContact(testIdentifier, activeState);
        testAssociatedContact.setId(1L);
    }

    @Test
    @DisplayName("Should create associated contact successfully")
    void shouldCreateAssociatedContactSuccessfully() {
        // Given
        AssociatedContactDto contactDto = TestDataFactory.createValidAssociatedContactDto(1L, "912345678", 1L);
        
        when(identifierService.findById(1L)).thenReturn(testIdentifier);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(associatedContactRepository.save(any(AssociatedContact.class))).thenReturn(testAssociatedContact);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        AssociatedContactResponse result = associatedContactService.createAssociatedContact(contactDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.identifierId());
        assertEquals("Test User", result.userName());
        assertEquals("912345678", result.contact());
        assertEquals(1L, result.stateId());
        assertEquals("ACTIVE", result.stateName());
        
        verify(identifierService).findById(1L);
        verify(stateService).findById(1L);
        verify(associatedContactRepository).save(any(AssociatedContact.class));
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should update associated contact successfully")
    void shouldUpdateAssociatedContactSuccessfully() {
        // Given
        Long contactId = 1L;
        AssociatedContactDto contactDto = TestDataFactory.createValidAssociatedContactDto(1L, "913456789", 1L);
        
        AssociatedContact updatedContact = TestDataFactory.createValidAssociatedContact(testIdentifier, "913456789", activeState);
        updatedContact.setId(1L);
        
        when(associatedContactRepository.findById(contactId)).thenReturn(Optional.of(testAssociatedContact));
        when(identifierService.findById(1L)).thenReturn(testIdentifier);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(associatedContactRepository.save(any(AssociatedContact.class))).thenReturn(updatedContact);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        AssociatedContactResponse result = associatedContactService.updateAssociatedContact(contactId, contactDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("913456789", result.contact());
        
        verify(associatedContactRepository).findById(contactId);
        verify(identifierService).findById(1L);
        verify(stateService).findById(1L);
        verify(associatedContactRepository).save(any(AssociatedContact.class));
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent contact")
    void shouldThrowExceptionWhenUpdatingNonExistentContact() {
        // Given
        Long contactId = 999L;
        AssociatedContactDto contactDto = TestDataFactory.createValidAssociatedContactDto(1L, 1L);
        
        when(associatedContactRepository.findById(contactId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            associatedContactService.updateAssociatedContact(contactId, contactDto));
        
        verify(associatedContactRepository).findById(contactId);
        verify(identifierService, never()).findById(anyLong());
        verify(stateService, never()).findById(anyLong());
        verify(associatedContactRepository, never()).save(any(AssociatedContact.class));
    }

    @Test
    @DisplayName("Should get all associated contacts excluding eliminated")
    void shouldGetAllAssociatedContactsExcludingEliminated() {
        // Given
        AssociatedContact contact1 = TestDataFactory.createValidAssociatedContact(testIdentifier, activeState);
        contact1.setId(1L);
        
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(testIdentifier, activeState);
        contact2.setId(2L);
        
        AssociatedContact eliminatedContact = TestDataFactory.createValidAssociatedContact(testIdentifier, eliminatedState);
        eliminatedContact.setId(3L);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(associatedContactRepository.findAllWithIdentifierAndState())
            .thenReturn(Arrays.asList(contact1, contact2, eliminatedContact));
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        List<AssociatedContactResponse> result = associatedContactService.getAllAssociatedContacts();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size()); // Should exclude eliminated contact
        
        verify(stateService).getEliminatedState();
        verify(associatedContactRepository).findAllWithIdentifierAndState();
    }

    @Test
    @DisplayName("Should get associated contact by id")
    void shouldGetAssociatedContactById() {
        // Given
        Long contactId = 1L;
        
        when(associatedContactRepository.findById(contactId)).thenReturn(Optional.of(testAssociatedContact));

        // When
        AssociatedContact result = associatedContactService.getAssociatedContactById(contactId);

        // Then
        assertNotNull(result);
        assertEquals(contactId, result.getId());
        assertEquals("912345678", result.getContact());
        
        verify(associatedContactRepository).findById(contactId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent contact by id")
    void shouldThrowExceptionWhenGettingNonExistentContactById() {
        // Given
        Long contactId = 999L;
        
        when(associatedContactRepository.findById(contactId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            associatedContactService.getAssociatedContactById(contactId));
        
        verify(associatedContactRepository).findById(contactId);
    }

    @Test
    @DisplayName("Should get associated contact by id response")
    void shouldGetAssociatedContactByIdResponse() {
        // Given
        Long contactId = 1L;
        
        when(associatedContactRepository.findByIdWithIdentifierAndState(contactId))
            .thenReturn(Optional.of(testAssociatedContact));
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        AssociatedContactResponse result = associatedContactService.getAssociatedContactByIdResponse(contactId);

        // Then
        assertNotNull(result);
        assertEquals(contactId, result.id());
        assertEquals("912345678", result.contact());
        assertEquals("Test User", result.userName());
        
        verify(associatedContactRepository).findByIdWithIdentifierAndState(contactId);
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should get associated contacts by identifier")
    void shouldGetAssociatedContactsByIdentifier() {
        // Given
        Long identifierId = 1L;
        List<AssociatedContact> contacts = Arrays.asList(testAssociatedContact);
        
        when(associatedContactRepository.findByIdentifierIdWithIdentifierAndState(identifierId))
            .thenReturn(contacts);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        List<AssociatedContactResponse> result = associatedContactService.getAssociatedContactsByIdentifier(identifierId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(identifierId, result.get(0).identifierId());
        
        verify(associatedContactRepository).findByIdentifierIdWithIdentifierAndState(identifierId);
    }

    @Test
    @DisplayName("Should get associated contacts by state")
    void shouldGetAssociatedContactsByState() {
        // Given
        Long stateId = 1L;
        List<AssociatedContact> contacts = Arrays.asList(testAssociatedContact);
        
        when(associatedContactRepository.findByStateIdWithIdentifierAndState(stateId))
            .thenReturn(contacts);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        List<AssociatedContactResponse> result = associatedContactService.getAssociatedContactsByState(stateId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(stateId, result.get(0).stateId());
        
        verify(associatedContactRepository).findByStateIdWithIdentifierAndState(stateId);
    }

    @Test
    @DisplayName("Should delete associated contact (soft delete)")
    void shouldDeleteAssociatedContactSoftDelete() {
        // Given
        Long contactId = 1L;
        
        when(associatedContactRepository.findById(contactId)).thenReturn(Optional.of(testAssociatedContact));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);

        // When
        associatedContactService.deleteAssociatedContact(contactId);

        // Then
        verify(associatedContactRepository).findById(contactId);
        verify(stateService).getEliminatedState();
        verify(associatedContactRepository).save(testAssociatedContact);
        assertEquals(eliminatedState, testAssociatedContact.getState());
    }

    @Test
    @DisplayName("Should find by id for inter-service communication")
    void shouldFindByIdForInterServiceCommunication() {
        // Given
        Long contactId = 1L;
        
        when(associatedContactRepository.findById(contactId)).thenReturn(Optional.of(testAssociatedContact));

        // When
        AssociatedContact result = associatedContactService.findById(contactId);

        // Then
        assertNotNull(result);
        assertEquals(contactId, result.getId());
        
        verify(associatedContactRepository).findById(contactId);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when finding by non-existent id")
    void shouldThrowEntityNotFoundExceptionWhenFindingByNonExistentId() {
        // Given
        Long contactId = 999L;
        
        when(associatedContactRepository.findById(contactId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> 
            associatedContactService.findById(contactId));
        
        verify(associatedContactRepository).findById(contactId);
    }

    @Test
    @DisplayName("Should find by id and state id")
    void shouldFindByIdAndStateId() {
        // Given
        Long contactId = 1L;
        Long stateId = 1L;
        
        when(associatedContactRepository.findById(contactId)).thenReturn(Optional.of(testAssociatedContact));

        // When
        AssociatedContact result = associatedContactService.findByIdAndStateId(contactId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(contactId, result.getId());
        assertEquals(stateId, result.getState().getId());
        
        verify(associatedContactRepository).findById(contactId);
    }

    @Test
    @DisplayName("Should use default state id when null")
    void shouldUseDefaultStateIdWhenNull() {
        // Given
        Long contactId = 1L;
        Long stateId = null; // Should default to 1L
        
        when(associatedContactRepository.findById(contactId)).thenReturn(Optional.of(testAssociatedContact));

        // When
        AssociatedContact result = associatedContactService.findByIdAndStateId(contactId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(contactId, result.getId());
        assertEquals(1L, result.getState().getId()); // Should use default state id
        
        verify(associatedContactRepository).findById(contactId);
    }

    @Test
    @DisplayName("Should throw exception when state id does not match")
    void shouldThrowExceptionWhenStateIdDoesNotMatch() {
        // Given
        Long contactId = 1L;
        Long stateId = 2L; // Different from testAssociatedContact's state id (1L)
        
        when(associatedContactRepository.findById(contactId)).thenReturn(Optional.of(testAssociatedContact));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> 
            associatedContactService.findByIdAndStateId(contactId, stateId));
        
        verify(associatedContactRepository).findById(contactId);
    }

    @Test
    @DisplayName("Should handle user not found gracefully in mapping")
    void shouldHandleUserNotFoundGracefullyInMapping() {
        // Given
        AssociatedContactDto contactDto = TestDataFactory.createValidAssociatedContactDto(1L, 1L);
        
        when(identifierService.findById(1L)).thenReturn(testIdentifier);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(associatedContactRepository.save(any(AssociatedContact.class))).thenReturn(testAssociatedContact);
        when(userService.findById(1L)).thenThrow(new EntityNotFoundException("User not found"));

        // When
        AssociatedContactResponse result = associatedContactService.createAssociatedContact(contactDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertNull(result.userName()); // Should be null when user not found
        
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should create multiple associated contacts for same identifier")
    void shouldCreateMultipleAssociatedContactsForSameIdentifier() {
        // Given
        AssociatedContactDto contactDto1 = TestDataFactory.createValidAssociatedContactDto(1L, "912345678", 1L);
        AssociatedContactDto contactDto2 = TestDataFactory.createValidAssociatedContactDto(1L, "913456789", 1L);
        
        AssociatedContact contact1 = TestDataFactory.createValidAssociatedContact(testIdentifier, "912345678", activeState);
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(testIdentifier, "913456789", activeState);
        contact1.setId(1L);
        contact2.setId(2L);
        
        when(identifierService.findById(1L)).thenReturn(testIdentifier);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(associatedContactRepository.save(any(AssociatedContact.class)))
            .thenReturn(contact1)
            .thenReturn(contact2);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        AssociatedContactResponse result1 = associatedContactService.createAssociatedContact(contactDto1);
        AssociatedContactResponse result2 = associatedContactService.createAssociatedContact(contactDto2);

        // Then
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals("912345678", result1.contact());
        assertEquals("913456789", result2.contact());
        assertEquals(result1.identifierId(), result2.identifierId()); // Same identifier
        
        verify(associatedContactRepository, times(2)).save(any(AssociatedContact.class));
    }
}