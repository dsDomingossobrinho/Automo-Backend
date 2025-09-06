package com.automo.lead.service;

import com.automo.identifier.entity.Identifier;
import com.automo.identifier.service.IdentifierService;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.lead.dto.LeadDto;
import com.automo.lead.entity.Lead;
import com.automo.lead.repository.LeadRepository;
import com.automo.lead.response.LeadResponse;
import com.automo.leadType.entity.LeadType;
import com.automo.leadType.service.LeadTypeService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for LeadServiceImpl")
class LeadServiceImplTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private IdentifierService identifierService;

    @Mock
    private LeadTypeService leadTypeService;

    @Mock
    private StateService stateService;

    @InjectMocks
    private LeadServiceImpl leadService;

    private State activeState;
    private State eliminatedState;
    private IdentifierType identifierType;
    private Identifier identifier;
    private LeadType leadType;
    private Lead testLead;
    private LeadDto testLeadDto;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        identifierType = TestDataFactory.createNifIdentifierType();
        identifierType.setId(1L);
        
        identifier = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        identifier.setId(1L);
        
        leadType = TestDataFactory.createCallLeadType();
        leadType.setId(1L);
        
        testLead = new Lead();
        testLead.setId(1L);
        testLead.setIdentifier(identifier);
        testLead.setName("João Silva");
        testLead.setEmail("joao.silva@example.com");
        testLead.setContact("912345678");
        testLead.setZone("Lisboa");
        testLead.setLeadType(leadType);
        testLead.setState(activeState);
        testLead.setCreatedAt(LocalDateTime.now());
        testLead.setUpdatedAt(LocalDateTime.now());

        testLeadDto = new LeadDto(
            1L, // identifierId
            "João Silva",
            "joao.silva@example.com", 
            "912345678",
            "Lisboa",
            1L, // leadTypeId
            1L  // stateId
        );
    }

    @Test
    @DisplayName("Should create lead successfully")
    void shouldCreateLeadSuccessfully() {
        // Given
        when(identifierService.findById(1L)).thenReturn(identifier);
        when(leadTypeService.findById(1L)).thenReturn(leadType);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);

        // When
        LeadResponse result = leadService.createLead(testLeadDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.identifierId());
        assertEquals("João Silva", result.name());
        assertEquals("joao.silva@example.com", result.email());
        assertEquals("912345678", result.contact());
        assertEquals("Lisboa", result.zone());
        assertEquals(1L, result.leadTypeId());
        assertEquals("CALL", result.leadTypeName());
        assertEquals(1L, result.stateId());
        assertEquals("ACTIVE", result.stateName());

        verify(identifierService).findById(1L);
        verify(leadTypeService).findById(1L);
        verify(stateService).findById(1L);
        verify(leadRepository).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should throw exception when creating lead with invalid leadType")
    void shouldThrowExceptionWhenCreatingLeadWithInvalidLeadType() {
        // Given
        when(identifierService.findById(1L)).thenReturn(identifier);
        when(leadTypeService.findById(999L)).thenThrow(new EntityNotFoundException("LeadType not found"));

        LeadDto invalidDto = new LeadDto(
            1L, "Test User", "test@example.com", "912345678", "Lisboa", 999L, 1L
        );

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> leadService.createLead(invalidDto));
        
        verify(identifierService).findById(1L);
        verify(leadTypeService).findById(999L);
        verify(stateService, never()).findById(anyLong());
        verify(leadRepository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should throw exception when creating lead with invalid state")
    void shouldThrowExceptionWhenCreatingLeadWithInvalidState() {
        // Given
        when(identifierService.findById(1L)).thenReturn(identifier);
        when(leadTypeService.findById(1L)).thenReturn(leadType);
        when(stateService.findById(999L)).thenThrow(new EntityNotFoundException("State not found"));

        LeadDto invalidDto = new LeadDto(
            1L, "Test User", "test@example.com", "912345678", "Lisboa", 1L, 999L
        );

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> leadService.createLead(invalidDto));
        
        verify(identifierService).findById(1L);
        verify(leadTypeService).findById(1L);
        verify(stateService).findById(999L);
        verify(leadRepository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should update lead successfully")
    void shouldUpdateLeadSuccessfully() {
        // Given
        LeadDto updateDto = new LeadDto(
            1L, "João Updated", "joao.updated@example.com", "999888777", "Porto", 1L, 1L
        );
        
        Lead updatedLead = new Lead();
        updatedLead.setId(1L);
        updatedLead.setIdentifier(identifier);
        updatedLead.setName("João Updated");
        updatedLead.setEmail("joao.updated@example.com");
        updatedLead.setContact("999888777");
        updatedLead.setZone("Porto");
        updatedLead.setLeadType(leadType);
        updatedLead.setState(activeState);

        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));
        when(identifierService.findById(1L)).thenReturn(identifier);
        when(leadTypeService.findById(1L)).thenReturn(leadType);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(leadRepository.save(any(Lead.class))).thenReturn(updatedLead);

        // When
        LeadResponse result = leadService.updateLead(1L, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("João Updated", result.name());
        assertEquals("joao.updated@example.com", result.email());
        assertEquals("999888777", result.contact());
        assertEquals("Porto", result.zone());

        verify(leadRepository).findById(1L);
        verify(identifierService).findById(1L);
        verify(leadTypeService).findById(1L);
        verify(stateService).findById(1L);
        verify(leadRepository).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent lead")
    void shouldThrowExceptionWhenUpdatingNonExistentLead() {
        // Given
        when(leadRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> 
            leadService.updateLead(999L, testLeadDto));
        
        verify(leadRepository).findById(999L);
        verify(leadTypeService, never()).findById(anyLong());
        verify(stateService, never()).findById(anyLong());
        verify(leadRepository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should get all leads excluding eliminated")
    void shouldGetAllLeadsExcludingEliminated() {
        // Given
        Lead lead1 = new Lead();
        lead1.setId(1L);
        lead1.setState(activeState);
        lead1.setIdentifier(identifier);
        lead1.setLeadType(leadType);
        lead1.setName("Lead 1");
        lead1.setEmail("lead1@example.com");
        
        Lead lead2 = new Lead();
        lead2.setId(2L);
        lead2.setState(activeState);
        lead2.setIdentifier(identifier);
        lead2.setLeadType(leadType);
        lead2.setName("Lead 2");
        lead2.setEmail("lead2@example.com");
        
        Lead eliminatedLead = new Lead();
        eliminatedLead.setId(3L);
        eliminatedLead.setState(eliminatedState);
        eliminatedLead.setIdentifier(identifier);
        eliminatedLead.setLeadType(leadType);
        eliminatedLead.setName("Eliminated Lead");
        eliminatedLead.setEmail("eliminated@example.com");

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(leadRepository.findAll()).thenReturn(Arrays.asList(lead1, lead2, eliminatedLead));

        // When
        List<LeadResponse> result = leadService.getAllLeads();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.name().equals("Lead 1")));
        assertTrue(result.stream().anyMatch(r -> r.name().equals("Lead 2")));
        assertFalse(result.stream().anyMatch(r -> r.name().equals("Eliminated Lead")));

        verify(stateService).getEliminatedState();
        verify(leadRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no leads exist")
    void shouldReturnEmptyListWhenNoLeadsExist() {
        // Given
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(leadRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<LeadResponse> result = leadService.getAllLeads();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(stateService).getEliminatedState();
        verify(leadRepository).findAll();
    }

    @Test
    @DisplayName("Should get lead by ID successfully")
    void shouldGetLeadByIdSuccessfully() {
        // Given
        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));

        // When
        Lead result = leadService.getLeadById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testLead.getId(), result.getId());
        assertEquals(testLead.getName(), result.getName());
        assertEquals(testLead.getEmail(), result.getEmail());

        verify(leadRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when lead not found by ID")
    void shouldThrowExceptionWhenLeadNotFoundById() {
        // Given
        when(leadRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> leadService.getLeadById(999L));
        
        assertEquals("Lead with ID 999 not found", exception.getMessage());
        verify(leadRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get lead by ID response successfully")
    void shouldGetLeadByIdResponseSuccessfully() {
        // Given
        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));

        // When
        LeadResponse result = leadService.getLeadByIdResponse(1L);

        // Then
        assertNotNull(result);
        assertEquals(testLead.getId(), result.id());
        assertEquals(testLead.getName(), result.name());
        assertEquals(testLead.getEmail(), result.email());

        verify(leadRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get leads by state")
    void shouldGetLeadsByState() {
        // Given
        Lead lead1 = new Lead();
        lead1.setId(1L);
        lead1.setState(activeState);
        lead1.setIdentifier(identifier);
        lead1.setLeadType(leadType);
        lead1.setName("Lead 1");
        lead1.setEmail("lead1@example.com");

        when(leadRepository.findByStateId(1L)).thenReturn(Arrays.asList(lead1));

        // When
        List<LeadResponse> result = leadService.getLeadsByState(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Lead 1", result.get(0).name());

        verify(leadRepository).findByStateId(1L);
    }

    @Test
    @DisplayName("Should get leads by lead type excluding eliminated")
    void shouldGetLeadsByLeadTypeExcludingEliminated() {
        // Given
        Lead activeLead = new Lead();
        activeLead.setId(1L);
        activeLead.setState(activeState);
        activeLead.setIdentifier(identifier);
        activeLead.setLeadType(leadType);
        activeLead.setName("Active Lead");
        activeLead.setEmail("active@example.com");
        
        Lead eliminatedLead = new Lead();
        eliminatedLead.setId(2L);
        eliminatedLead.setState(eliminatedState);
        eliminatedLead.setIdentifier(identifier);
        eliminatedLead.setLeadType(leadType);
        eliminatedLead.setName("Eliminated Lead");
        eliminatedLead.setEmail("eliminated@example.com");

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(leadRepository.findByLeadTypeId(1L)).thenReturn(Arrays.asList(activeLead, eliminatedLead));

        // When
        List<LeadResponse> result = leadService.getLeadsByLeadType(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Active Lead", result.get(0).name());

        verify(stateService).getEliminatedState();
        verify(leadRepository).findByLeadTypeId(1L);
    }

    @Test
    @DisplayName("Should get leads by identifier excluding eliminated")
    void shouldGetLeadsByIdentifierExcludingEliminated() {
        // Given
        Lead activeLead = new Lead();
        activeLead.setId(1L);
        activeLead.setState(activeState);
        activeLead.setIdentifier(identifier);
        activeLead.setLeadType(leadType);
        activeLead.setName("Active Lead");
        activeLead.setEmail("active@example.com");
        
        Lead eliminatedLead = new Lead();
        eliminatedLead.setId(2L);
        eliminatedLead.setState(eliminatedState);
        eliminatedLead.setIdentifier(identifier);
        eliminatedLead.setLeadType(leadType);
        eliminatedLead.setName("Eliminated Lead");
        eliminatedLead.setEmail("eliminated@example.com");

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(leadRepository.findByIdentifierId(1L)).thenReturn(Arrays.asList(activeLead, eliminatedLead));

        // When
        List<LeadResponse> result = leadService.getLeadsByIdentifier(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Active Lead", result.get(0).name());

        verify(stateService).getEliminatedState();
        verify(leadRepository).findByIdentifierId(1L);
    }

    @Test
    @DisplayName("Should delete lead using soft delete")
    void shouldDeleteLeadUsingSoftDelete() {
        // Given
        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(leadRepository.save(any(Lead.class))).thenReturn(testLead);

        // When
        leadService.deleteLead(1L);

        // Then
        verify(leadRepository).findById(1L);
        verify(stateService).getEliminatedState();
        verify(leadRepository).save(argThat(lead -> 
            lead.getState().equals(eliminatedState)));
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent lead")
    void shouldThrowExceptionWhenDeletingNonExistentLead() {
        // Given
        when(leadRepository.findById(999L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(EntityNotFoundException.class, () -> leadService.deleteLead(999L));
        
        verify(leadRepository).findById(999L);
        verify(stateService, never()).getEliminatedState();
        verify(leadRepository, never()).save(any(Lead.class));
    }

    @Test
    @DisplayName("Should find by ID for inter-service communication")
    void shouldFindByIdForInterServiceCommunication() {
        // Given
        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));

        // When
        Lead result = leadService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testLead.getId(), result.getId());
        assertEquals(testLead.getName(), result.getName());

        verify(leadRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find by ID and state ID with valid state")
    void shouldFindByIdAndStateIdWithValidState() {
        // Given
        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));

        // When
        Lead result = leadService.findByIdAndStateId(1L, 1L);

        // Then
        assertNotNull(result);
        assertEquals(testLead.getId(), result.getId());
        assertEquals(testLead.getState().getId(), result.getState().getId());

        verify(leadRepository).findById(1L);
    }

    @Test
    @DisplayName("Should use default state when stateId is null")
    void shouldUseDefaultStateWhenStateIdIsNull() {
        // Given
        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));

        // When
        Lead result = leadService.findByIdAndStateId(1L, null);

        // Then
        assertNotNull(result);
        assertEquals(testLead.getId(), result.getId());

        verify(leadRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when finding by ID and state ID with invalid state")
    void shouldThrowExceptionWhenFindingByIdAndStateIdWithInvalidState() {
        // Given
        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));

        // When/Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> leadService.findByIdAndStateId(1L, 2L));
        
        assertEquals("Lead with ID 1 and state ID 2 not found", exception.getMessage());
        verify(leadRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle null identifier in mapping response")
    void shouldHandleNullIdentifierInMappingResponse() {
        // Given
        Lead leadWithoutIdentifier = new Lead();
        leadWithoutIdentifier.setId(1L);
        leadWithoutIdentifier.setIdentifier(null);
        leadWithoutIdentifier.setName("Test Lead");
        leadWithoutIdentifier.setEmail("test@example.com");
        leadWithoutIdentifier.setLeadType(leadType);
        leadWithoutIdentifier.setState(activeState);

        when(leadRepository.findById(1L)).thenReturn(Optional.of(leadWithoutIdentifier));

        // When
        LeadResponse result = leadService.getLeadByIdResponse(1L);

        // Then
        assertNotNull(result);
        assertNull(result.identifierId());
        assertEquals("Test Lead", result.name());

        verify(leadRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle leads with null state in filtering")
    void shouldHandleLeadsWithNullStateInFiltering() {
        // Given
        Lead leadWithNullState = new Lead();
        leadWithNullState.setId(1L);
        leadWithNullState.setState(null);
        leadWithNullState.setIdentifier(identifier);
        leadWithNullState.setLeadType(leadType);
        leadWithNullState.setName("Lead with null state");
        leadWithNullState.setEmail("nullstate@example.com");

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(leadRepository.findAll()).thenReturn(Arrays.asList(testLead, leadWithNullState));

        // When
        List<LeadResponse> result = leadService.getAllLeads();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // Only testLead should be returned
        assertEquals("João Silva", result.get(0).name());

        verify(stateService).getEliminatedState();
        verify(leadRepository).findAll();
    }
}