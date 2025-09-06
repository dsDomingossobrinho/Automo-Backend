package com.automo.identifier.service;

import com.automo.identifier.dto.IdentifierDto;
import com.automo.identifier.entity.Identifier;
import com.automo.identifier.repository.IdentifierRepository;
import com.automo.identifier.response.IdentifierResponse;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.identifierType.service.IdentifierTypeService;
import com.automo.user.entity.User;
import com.automo.user.service.UserService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.utils.TestDataFactory;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for IdentifierServiceImpl")
class IdentifierServiceImplTest {

    @Mock
    private IdentifierRepository identifierRepository;

    @Mock
    private UserService userService;

    @Mock
    private IdentifierTypeService identifierTypeService;

    @Mock
    private StateService stateService;

    @InjectMocks
    private IdentifierServiceImpl identifierService;

    private Identifier testIdentifier;
    private IdentifierType testIdentifierType;
    private User testUser;
    private State activeState;
    private State eliminatedState;
    private IdentifierDto testIdentifierDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        testIdentifierType = TestDataFactory.createNifIdentifierType();
        testIdentifierType.setId(1L);

        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);

        testUser = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth(),
                TestDataFactory.createIndividualAccountType(),
                activeState
        );
        testUser.setId(1L);

        testIdentifier = TestDataFactory.createValidIdentifier(1L, testIdentifierType, activeState);
        testIdentifier.setId(1L);

        testIdentifierDto = TestDataFactory.createValidIdentifierDto(1L, 1L, 1L);
    }

    @Test
    @DisplayName("Should create identifier successfully")
    void shouldCreateIdentifierSuccessfully() {
        // Given
        when(userService.findById(1L)).thenReturn(testUser);
        when(identifierTypeService.findById(1L)).thenReturn(testIdentifierType);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(identifierRepository.save(any(Identifier.class))).thenReturn(testIdentifier);

        // When
        IdentifierResponse response = identifierService.createIdentifier(testIdentifierDto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(1L, response.userId());
        assertEquals("Test User", response.userName());
        assertEquals(1L, response.identifierTypeId());
        assertEquals("NIF", response.identifierType());
        assertEquals(1L, response.stateId());
        assertEquals("ACTIVE", response.state());

        verify(userService).findById(1L);
        verify(identifierTypeService).findById(1L);
        verify(stateService).findById(1L);
        verify(identifierRepository).save(any(Identifier.class));
    }

    @Test
    @DisplayName("Should throw exception when user not found during creation")
    void shouldThrowExceptionWhenUserNotFoundDuringCreation() {
        // Given
        when(userService.findById(1L)).thenThrow(new EntityNotFoundException("User not found"));

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                identifierService.createIdentifier(testIdentifierDto));

        assertEquals("User not found", exception.getMessage());
        verify(userService).findById(1L);
        verify(identifierTypeService, never()).findById(anyLong());
        verify(stateService, never()).findById(anyLong());
        verify(identifierRepository, never()).save(any(Identifier.class));
    }

    @Test
    @DisplayName("Should throw exception when identifier type not found during creation")
    void shouldThrowExceptionWhenIdentifierTypeNotFoundDuringCreation() {
        // Given
        when(userService.findById(1L)).thenReturn(testUser);
        when(identifierTypeService.findById(1L)).thenThrow(new EntityNotFoundException("IdentifierType not found"));

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                identifierService.createIdentifier(testIdentifierDto));

        assertEquals("IdentifierType not found", exception.getMessage());
        verify(userService).findById(1L);
        verify(identifierTypeService).findById(1L);
        verify(stateService, never()).findById(anyLong());
        verify(identifierRepository, never()).save(any(Identifier.class));
    }

    @Test
    @DisplayName("Should update identifier successfully")
    void shouldUpdateIdentifierSuccessfully() {
        // Given
        IdentifierDto updateDto = TestDataFactory.createValidIdentifierDto(2L, 1L, 1L);
        User newUser = TestDataFactory.createValidUser(
                TestDataFactory.createValidAuth("user2@test.com"),
                TestDataFactory.createIndividualAccountType(),
                activeState
        );
        newUser.setId(2L);
        newUser.setName("Updated User");

        when(identifierRepository.findById(1L)).thenReturn(Optional.of(testIdentifier));
        when(userService.findById(2L)).thenReturn(newUser);
        when(identifierTypeService.findById(1L)).thenReturn(testIdentifierType);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(identifierRepository.save(any(Identifier.class))).thenReturn(testIdentifier);

        // When
        IdentifierResponse response = identifierService.updateIdentifier(1L, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals(2L, response.userId());
        assertEquals("Updated User", response.userName());

        verify(identifierRepository).findById(1L);
        verify(userService).findById(2L);
        verify(identifierTypeService).findById(1L);
        verify(stateService).findById(1L);
        verify(identifierRepository).save(any(Identifier.class));
    }

    @Test
    @DisplayName("Should throw exception when identifier not found during update")
    void shouldThrowExceptionWhenIdentifierNotFoundDuringUpdate() {
        // Given
        when(identifierRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                identifierService.updateIdentifier(1L, testIdentifierDto));

        assertEquals("Identifier with ID 1 not found", exception.getMessage());
        verify(identifierRepository).findById(1L);
        verify(userService, never()).findById(anyLong());
        verify(identifierTypeService, never()).findById(anyLong());
        verify(stateService, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Should get all identifiers excluding eliminated")
    void shouldGetAllIdentifiersExcludingEliminated() {
        // Given
        Identifier eliminatedIdentifier = TestDataFactory.createValidIdentifier(2L, testIdentifierType, eliminatedState);
        eliminatedIdentifier.setId(2L);

        List<Identifier> allIdentifiers = Arrays.asList(testIdentifier, eliminatedIdentifier);

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(identifierRepository.findAll()).thenReturn(allIdentifiers);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        List<IdentifierResponse> responses = identifierService.getAllIdentifiers();

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size()); // Only active identifier should be returned
        assertEquals(1L, responses.get(0).id());
        assertEquals("ACTIVE", responses.get(0).state());

        verify(stateService).getEliminatedState();
        verify(identifierRepository).findAll();
    }

    @Test
    @DisplayName("Should get identifier by ID")
    void shouldGetIdentifierById() {
        // Given
        when(identifierRepository.findById(1L)).thenReturn(Optional.of(testIdentifier));

        // When
        Identifier result = identifierService.getIdentifierById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getUserId());

        verify(identifierRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when identifier not found by ID")
    void shouldThrowExceptionWhenIdentifierNotFoundById() {
        // Given
        when(identifierRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                identifierService.getIdentifierById(999L));

        assertEquals("Identifier with ID 999 not found", exception.getMessage());
        verify(identifierRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get identifier by ID response")
    void shouldGetIdentifierByIdResponse() {
        // Given
        when(identifierRepository.findById(1L)).thenReturn(Optional.of(testIdentifier));
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        IdentifierResponse response = identifierService.getIdentifierByIdResponse(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Test User", response.userName());
        assertEquals("NIF", response.identifierType());

        verify(identifierRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get identifiers by state")
    void shouldGetIdentifiersByState() {
        // Given
        List<Identifier> identifiers = Arrays.asList(testIdentifier);
        when(identifierRepository.findByStateId(1L)).thenReturn(identifiers);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        List<IdentifierResponse> responses = identifierService.getIdentifiersByState(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).stateId());
        assertEquals("ACTIVE", responses.get(0).state());

        verify(identifierRepository).findByStateId(1L);
    }

    @Test
    @DisplayName("Should get identifiers by user")
    void shouldGetIdentifiersByUser() {
        // Given
        List<Identifier> identifiers = Arrays.asList(testIdentifier);
        when(identifierRepository.findByUserId(1L)).thenReturn(identifiers);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        List<IdentifierResponse> responses = identifierService.getIdentifiersByUser(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).userId());
        assertEquals("Test User", responses.get(0).userName());

        verify(identifierRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should get identifiers by type")
    void shouldGetIdentifiersByType() {
        // Given
        List<Identifier> identifiers = Arrays.asList(testIdentifier);
        when(identifierRepository.findByIdentifierTypeId(1L)).thenReturn(identifiers);
        when(userService.findById(1L)).thenReturn(testUser);

        // When
        List<IdentifierResponse> responses = identifierService.getIdentifiersByType(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).identifierTypeId());
        assertEquals("NIF", responses.get(0).identifierType());

        verify(identifierRepository).findByIdentifierTypeId(1L);
    }

    @Test
    @DisplayName("Should delete identifier using soft delete")
    void shouldDeleteIdentifierUsingSoftDelete() {
        // Given
        when(identifierRepository.findById(1L)).thenReturn(Optional.of(testIdentifier));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(identifierRepository.save(any(Identifier.class))).thenReturn(testIdentifier);

        // When
        identifierService.deleteIdentifier(1L);

        // Then
        verify(identifierRepository).findById(1L);
        verify(stateService).getEliminatedState();
        verify(identifierRepository).save(testIdentifier);
        // Verify that the state was set to eliminated
        assertEquals(eliminatedState, testIdentifier.getState());
    }

    @Test
    @DisplayName("Should create identifier for entity successfully")
    void shouldCreateIdentifierForEntitySuccessfully() {
        // Given
        String entityType = "USER";
        when(identifierTypeService.findByType(entityType)).thenReturn(Optional.of(testIdentifierType));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(identifierRepository.save(any(Identifier.class))).thenReturn(testIdentifier);

        // When
        identifierService.createIdentifierForEntity(1L, entityType, 1L);

        // Then
        verify(identifierTypeService).findByType(entityType);
        verify(stateService).findById(1L);
        verify(identifierRepository).save(any(Identifier.class));
    }

    @Test
    @DisplayName("Should create default identifier type when not found for entity")
    void shouldCreateDefaultIdentifierTypeWhenNotFoundForEntity() {
        // Given
        String entityType = "UNKNOWN_TYPE";
        when(identifierTypeService.findByType(entityType)).thenReturn(Optional.empty());
        when(identifierTypeService.createDefaultIdentifierType(entityType, entityType + " identifier"))
                .thenReturn(testIdentifierType);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(identifierRepository.save(any(Identifier.class))).thenReturn(testIdentifier);

        // When
        identifierService.createIdentifierForEntity(1L, entityType, 1L);

        // Then
        verify(identifierTypeService).findByType(entityType);
        verify(identifierTypeService).createDefaultIdentifierType(entityType, entityType + " identifier");
        verify(stateService).findById(1L);
        verify(identifierRepository).save(any(Identifier.class));
    }

    @Test
    @DisplayName("Should handle exception when creating identifier for entity")
    void shouldHandleExceptionWhenCreatingIdentifierForEntity() {
        // Given
        String entityType = "USER";
        when(identifierTypeService.findByType(entityType)).thenThrow(new RuntimeException("Database error"));

        // When - should not throw exception but handle it gracefully
        assertDoesNotThrow(() -> identifierService.createIdentifierForEntity(1L, entityType, 1L));

        // Then
        verify(identifierTypeService).findByType(entityType);
        verify(stateService, never()).findById(anyLong());
        verify(identifierRepository, never()).save(any(Identifier.class));
    }

    @Test
    @DisplayName("Should find by ID using findById method")
    void shouldFindByIdUsingFindByIdMethod() {
        // Given
        when(identifierRepository.findById(1L)).thenReturn(Optional.of(testIdentifier));

        // When
        Identifier result = identifierService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(identifierRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find by ID and state ID with matching state")
    void shouldFindByIdAndStateIdWithMatchingState() {
        // Given
        when(identifierRepository.findById(1L)).thenReturn(Optional.of(testIdentifier));

        // When
        Identifier result = identifierService.findByIdAndStateId(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getState().getId());
        verify(identifierRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find by ID and state ID with default state when null")
    void shouldFindByIdAndStateIdWithDefaultStateWhenNull() {
        // Given
        when(identifierRepository.findById(1L)).thenReturn(Optional.of(testIdentifier));

        // When
        Identifier result = identifierService.findByIdAndStateId(1L, null);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(identifierRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when state ID doesn't match in findByIdAndStateId")
    void shouldThrowExceptionWhenStateIdDoesntMatchInFindByIdAndStateId() {
        // Given
        when(identifierRepository.findById(1L)).thenReturn(Optional.of(testIdentifier));

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                identifierService.findByIdAndStateId(1L, 2L));

        assertEquals("Identifier with ID 1 and state ID 2 not found", exception.getMessage());
        verify(identifierRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle user not found gracefully in mapping")
    void shouldHandleUserNotFoundGracefullyInMapping() {
        // Given
        when(identifierRepository.findByUserId(1L)).thenReturn(Arrays.asList(testIdentifier));
        when(userService.findById(1L)).thenThrow(new EntityNotFoundException("User not found"));

        // When
        List<IdentifierResponse> responses = identifierService.getIdentifiersByUser(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).userId());
        assertNull(responses.get(0).userName()); // Should be null when user not found

        verify(identifierRepository).findByUserId(1L);
        verify(userService).findById(1L);
    }

    @Test
    @DisplayName("Should return empty list when no identifiers found by state")
    void shouldReturnEmptyListWhenNoIdentifiersFoundByState() {
        // Given
        when(identifierRepository.findByStateId(999L)).thenReturn(Arrays.asList());

        // When
        List<IdentifierResponse> responses = identifierService.getIdentifiersByState(999L);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(identifierRepository).findByStateId(999L);
    }

    @Test
    @DisplayName("Should return empty list when no identifiers found by user")
    void shouldReturnEmptyListWhenNoIdentifiersFoundByUser() {
        // Given
        when(identifierRepository.findByUserId(999L)).thenReturn(Arrays.asList());

        // When
        List<IdentifierResponse> responses = identifierService.getIdentifiersByUser(999L);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(identifierRepository).findByUserId(999L);
    }

    @Test
    @DisplayName("Should return empty list when no identifiers found by type")
    void shouldReturnEmptyListWhenNoIdentifiersFoundByType() {
        // Given
        when(identifierRepository.findByIdentifierTypeId(999L)).thenReturn(Arrays.asList());

        // When
        List<IdentifierResponse> responses = identifierService.getIdentifiersByType(999L);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(identifierRepository).findByIdentifierTypeId(999L);
    }
}