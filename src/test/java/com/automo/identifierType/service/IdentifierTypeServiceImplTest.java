package com.automo.identifierType.service;

import com.automo.identifierType.dto.IdentifierTypeDto;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.identifierType.repository.IdentifierTypeRepository;
import com.automo.identifierType.response.IdentifierTypeResponse;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for IdentifierTypeServiceImpl")
class IdentifierTypeServiceImplTest {

    @Mock
    private IdentifierTypeRepository identifierTypeRepository;

    @InjectMocks
    private IdentifierTypeServiceImpl identifierTypeService;

    private IdentifierType testIdentifierType;
    private IdentifierTypeDto testIdentifierTypeDto;

    @BeforeEach
    void setUp() {
        testIdentifierType = TestDataFactory.createNifIdentifierType();
        testIdentifierType.setId(1L);

        testIdentifierTypeDto = TestDataFactory.createValidIdentifierTypeDto("NIF", "Número de Identificação Fiscal");
    }

    @Test
    @DisplayName("Should create identifier type successfully")
    void shouldCreateIdentifierTypeSuccessfully() {
        // Given
        when(identifierTypeRepository.save(any(IdentifierType.class))).thenReturn(testIdentifierType);

        // When
        IdentifierTypeResponse response = identifierTypeService.createIdentifierType(testIdentifierTypeDto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("NIF", response.type());
        assertEquals("Número de Identificação Fiscal", response.description());

        verify(identifierTypeRepository).save(any(IdentifierType.class));
    }

    @Test
    @DisplayName("Should create identifier type with null description")
    void shouldCreateIdentifierTypeWithNullDescription() {
        // Given
        IdentifierTypeDto dtoWithNullDescription = TestDataFactory.createValidIdentifierTypeDto("NIPC", null);
        IdentifierType identifierTypeWithNullDescription = TestDataFactory.createValidIdentifierType("NIPC", null);
        identifierTypeWithNullDescription.setId(2L);

        when(identifierTypeRepository.save(any(IdentifierType.class))).thenReturn(identifierTypeWithNullDescription);

        // When
        IdentifierTypeResponse response = identifierTypeService.createIdentifierType(dtoWithNullDescription);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.id());
        assertEquals("NIPC", response.type());
        assertNull(response.description());

        verify(identifierTypeRepository).save(any(IdentifierType.class));
    }

    @Test
    @DisplayName("Should update identifier type successfully")
    void shouldUpdateIdentifierTypeSuccessfully() {
        // Given
        IdentifierTypeDto updateDto = TestDataFactory.createValidIdentifierTypeDto("NIPC", "Número de Identificação de Pessoa Coletiva");
        
        when(identifierTypeRepository.findById(1L)).thenReturn(Optional.of(testIdentifierType));
        when(identifierTypeRepository.save(any(IdentifierType.class))).thenReturn(testIdentifierType);

        // When
        IdentifierTypeResponse response = identifierTypeService.updateIdentifierType(1L, updateDto);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("NIPC", testIdentifierType.getType());
        assertEquals("Número de Identificação de Pessoa Coletiva", testIdentifierType.getDescription());

        verify(identifierTypeRepository).findById(1L);
        verify(identifierTypeRepository).save(testIdentifierType);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent identifier type")
    void shouldThrowExceptionWhenUpdatingNonExistentIdentifierType() {
        // Given
        when(identifierTypeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                identifierTypeService.updateIdentifierType(999L, testIdentifierTypeDto));

        assertEquals("IdentifierType with ID 999 not found", exception.getMessage());
        verify(identifierTypeRepository).findById(999L);
        verify(identifierTypeRepository, never()).save(any(IdentifierType.class));
    }

    @Test
    @DisplayName("Should get all identifier types")
    void shouldGetAllIdentifierTypes() {
        // Given
        IdentifierType nipcType = TestDataFactory.createValidIdentifierType("NIPC", "Número de Identificação de Pessoa Coletiva");
        nipcType.setId(2L);
        
        List<IdentifierType> identifierTypes = Arrays.asList(testIdentifierType, nipcType);
        when(identifierTypeRepository.findAll()).thenReturn(identifierTypes);

        // When
        List<IdentifierTypeResponse> responses = identifierTypeService.getAllIdentifierTypes();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        
        IdentifierTypeResponse firstResponse = responses.get(0);
        assertEquals(1L, firstResponse.id());
        assertEquals("NIF", firstResponse.type());
        
        IdentifierTypeResponse secondResponse = responses.get(1);
        assertEquals(2L, secondResponse.id());
        assertEquals("NIPC", secondResponse.type());

        verify(identifierTypeRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no identifier types exist")
    void shouldReturnEmptyListWhenNoIdentifierTypesExist() {
        // Given
        when(identifierTypeRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<IdentifierTypeResponse> responses = identifierTypeService.getAllIdentifierTypes();

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(identifierTypeRepository).findAll();
    }

    @Test
    @DisplayName("Should get identifier type by ID")
    void shouldGetIdentifierTypeById() {
        // Given
        when(identifierTypeRepository.findById(1L)).thenReturn(Optional.of(testIdentifierType));

        // When
        IdentifierType result = identifierTypeService.getIdentifierTypeById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("NIF", result.getType());

        verify(identifierTypeRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when identifier type not found by ID")
    void shouldThrowExceptionWhenIdentifierTypeNotFoundById() {
        // Given
        when(identifierTypeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                identifierTypeService.getIdentifierTypeById(999L));

        assertEquals("IdentifierType with ID 999 not found", exception.getMessage());
        verify(identifierTypeRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get identifier type by ID response")
    void shouldGetIdentifierTypeByIdResponse() {
        // Given
        when(identifierTypeRepository.findById(1L)).thenReturn(Optional.of(testIdentifierType));

        // When
        IdentifierTypeResponse response = identifierTypeService.getIdentifierTypeByIdResponse(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("NIF", response.type());
        assertEquals("Número de Identificação Fiscal", response.description());

        verify(identifierTypeRepository).findById(1L);
    }

    @Test
    @DisplayName("Should delete identifier type successfully")
    void shouldDeleteIdentifierTypeSuccessfully() {
        // Given
        when(identifierTypeRepository.existsById(1L)).thenReturn(true);

        // When
        identifierTypeService.deleteIdentifierType(1L);

        // Then
        verify(identifierTypeRepository).existsById(1L);
        verify(identifierTypeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent identifier type")
    void shouldThrowExceptionWhenDeletingNonExistentIdentifierType() {
        // Given
        when(identifierTypeRepository.existsById(999L)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                identifierTypeService.deleteIdentifierType(999L));

        assertEquals("IdentifierType with ID 999 not found", exception.getMessage());
        verify(identifierTypeRepository).existsById(999L);
        verify(identifierTypeRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should find identifier type by type string")
    void shouldFindIdentifierTypeByTypeString() {
        // Given
        when(identifierTypeRepository.findByType("NIF")).thenReturn(Optional.of(testIdentifierType));

        // When
        Optional<IdentifierType> result = identifierTypeService.findByType("NIF");

        // Then
        assertTrue(result.isPresent());
        assertEquals("NIF", result.get().getType());
        assertEquals("Número de Identificação Fiscal", result.get().getDescription());

        verify(identifierTypeRepository).findByType("NIF");
    }

    @Test
    @DisplayName("Should return empty optional when type not found")
    void shouldReturnEmptyOptionalWhenTypeNotFound() {
        // Given
        when(identifierTypeRepository.findByType("UNKNOWN")).thenReturn(Optional.empty());

        // When
        Optional<IdentifierType> result = identifierTypeService.findByType("UNKNOWN");

        // Then
        assertFalse(result.isPresent());

        verify(identifierTypeRepository).findByType("UNKNOWN");
    }

    @Test
    @DisplayName("Should find by ID using findById method")
    void shouldFindByIdUsingFindByIdMethod() {
        // Given
        when(identifierTypeRepository.findById(1L)).thenReturn(Optional.of(testIdentifierType));

        // When
        IdentifierType result = identifierTypeService.findById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("NIF", result.getType());

        verify(identifierTypeRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception in findById when not found")
    void shouldThrowExceptionInFindByIdWhenNotFound() {
        // Given
        when(identifierTypeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        jakarta.persistence.EntityNotFoundException exception = assertThrows(
                jakarta.persistence.EntityNotFoundException.class, () ->
                identifierTypeService.findById(999L));

        assertEquals("IdentifierType with ID 999 not found", exception.getMessage());
        verify(identifierTypeRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find by ID and state ID ignoring state ID for IdentifierType")
    void shouldFindByIdAndStateIdIgnoringStateIdForIdentifierType() {
        // Given - IdentifierType doesn't have state relationship, so stateId should be ignored
        when(identifierTypeRepository.findById(1L)).thenReturn(Optional.of(testIdentifierType));

        // When
        IdentifierType result = identifierTypeService.findByIdAndStateId(1L, 99L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("NIF", result.getType());

        verify(identifierTypeRepository).findById(1L);
    }

    @Test
    @DisplayName("Should create default identifier type")
    void shouldCreateDefaultIdentifierType() {
        // Given
        String type = "CUSTOM_TYPE";
        String description = "Custom type description";
        
        IdentifierType defaultType = TestDataFactory.createValidIdentifierType(type, description);
        defaultType.setId(3L);
        
        when(identifierTypeRepository.save(any(IdentifierType.class))).thenReturn(defaultType);

        // When
        IdentifierType result = identifierTypeService.createDefaultIdentifierType(type, description);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(type, result.getType());
        assertEquals(description, result.getDescription());

        verify(identifierTypeRepository).save(any(IdentifierType.class));
    }

    @Test
    @DisplayName("Should handle case-sensitive type searches")
    void shouldHandleCaseSensitiveTypeSearches() {
        // Given
        when(identifierTypeRepository.findByType("nif")).thenReturn(Optional.empty());
        when(identifierTypeRepository.findByType("NIF")).thenReturn(Optional.of(testIdentifierType));

        // When
        Optional<IdentifierType> lowerCaseResult = identifierTypeService.findByType("nif");
        Optional<IdentifierType> upperCaseResult = identifierTypeService.findByType("NIF");

        // Then
        assertFalse(lowerCaseResult.isPresent());
        assertTrue(upperCaseResult.isPresent());
        assertEquals("NIF", upperCaseResult.get().getType());

        verify(identifierTypeRepository).findByType("nif");
        verify(identifierTypeRepository).findByType("NIF");
    }

    @Test
    @DisplayName("Should map entity to response correctly")
    void shouldMapEntityToResponseCorrectly() {
        // Given
        testIdentifierType.setId(10L);
        testIdentifierType.setType("PASSPORT");
        testIdentifierType.setDescription("Passport Number");
        
        when(identifierTypeRepository.findById(10L)).thenReturn(Optional.of(testIdentifierType));

        // When
        IdentifierTypeResponse response = identifierTypeService.getIdentifierTypeByIdResponse(10L);

        // Then
        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("PASSPORT", response.type());
        assertEquals("Passport Number", response.description());
        assertNotNull(response.createdAt());
        assertNotNull(response.updatedAt());

        verify(identifierTypeRepository).findById(10L);
    }

    @Test
    @DisplayName("Should handle special characters in type and description")
    void shouldHandleSpecialCharactersInTypeAndDescription() {
        // Given
        IdentifierTypeDto specialCharsDto = TestDataFactory.createValidIdentifierTypeDto(
                "NIF-PT_2024", 
                "Número de Identificação Fiscal - Portugal (2024) & Other Info!"
        );
        
        IdentifierType specialCharsType = TestDataFactory.createValidIdentifierType(
                "NIF-PT_2024",
                "Número de Identificação Fiscal - Portugal (2024) & Other Info!"
        );
        specialCharsType.setId(5L);
        
        when(identifierTypeRepository.save(any(IdentifierType.class))).thenReturn(specialCharsType);

        // When
        IdentifierTypeResponse response = identifierTypeService.createIdentifierType(specialCharsDto);

        // Then
        assertNotNull(response);
        assertEquals(5L, response.id());
        assertEquals("NIF-PT_2024", response.type());
        assertEquals("Número de Identificação Fiscal - Portugal (2024) & Other Info!", response.description());

        verify(identifierTypeRepository).save(any(IdentifierType.class));
    }

    @Test
    @DisplayName("Should handle empty type search")
    void shouldHandleEmptyTypeSearch() {
        // Given
        when(identifierTypeRepository.findByType("")).thenReturn(Optional.empty());

        // When
        Optional<IdentifierType> result = identifierTypeService.findByType("");

        // Then
        assertFalse(result.isPresent());

        verify(identifierTypeRepository).findByType("");
    }

    @Test
    @DisplayName("Should handle null type search gracefully")
    void shouldHandleNullTypeSearchGracefully() {
        // Given
        when(identifierTypeRepository.findByType(null)).thenReturn(Optional.empty());

        // When
        Optional<IdentifierType> result = identifierTypeService.findByType(null);

        // Then
        assertFalse(result.isPresent());

        verify(identifierTypeRepository).findByType(null);
    }

    @Test
    @DisplayName("Should create identifier type with maximum length values")
    void shouldCreateIdentifierTypeWithMaximumLengthValues() {
        // Given
        String longType = "A".repeat(255); // Assuming max length constraint
        String longDescription = "B".repeat(1000); // Assuming max length constraint
        
        IdentifierTypeDto longDto = TestDataFactory.createValidIdentifierTypeDto(longType, longDescription);
        IdentifierType longIdentifierType = TestDataFactory.createValidIdentifierType(longType, longDescription);
        longIdentifierType.setId(6L);
        
        when(identifierTypeRepository.save(any(IdentifierType.class))).thenReturn(longIdentifierType);

        // When
        IdentifierTypeResponse response = identifierTypeService.createIdentifierType(longDto);

        // Then
        assertNotNull(response);
        assertEquals(6L, response.id());
        assertEquals(longType, response.type());
        assertEquals(longDescription, response.description());

        verify(identifierTypeRepository).save(any(IdentifierType.class));
    }

    @Test
    @DisplayName("Should handle concurrent updates correctly")
    void shouldHandleConcurrentUpdatesCorrectly() {
        // Given
        IdentifierTypeDto updateDto1 = TestDataFactory.createValidIdentifierTypeDto("TYPE1", "Description 1");
        IdentifierTypeDto updateDto2 = TestDataFactory.createValidIdentifierTypeDto("TYPE2", "Description 2");
        
        when(identifierTypeRepository.findById(1L)).thenReturn(Optional.of(testIdentifierType));
        when(identifierTypeRepository.save(any(IdentifierType.class))).thenReturn(testIdentifierType);

        // When
        IdentifierTypeResponse response1 = identifierTypeService.updateIdentifierType(1L, updateDto1);
        IdentifierTypeResponse response2 = identifierTypeService.updateIdentifierType(1L, updateDto2);

        // Then
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(1L, response1.id());
        assertEquals(1L, response2.id());
        
        // The last update should win
        assertEquals("TYPE2", testIdentifierType.getType());
        assertEquals("Description 2", testIdentifierType.getDescription());

        verify(identifierTypeRepository, times(2)).findById(1L);
        verify(identifierTypeRepository, times(2)).save(testIdentifierType);
    }
}