package com.automo.area.service;

import com.automo.area.dto.AreaDto;
import com.automo.area.entity.Area;
import com.automo.area.repository.AreaRepository;
import com.automo.area.response.AreaResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.config.BaseTestConfig;
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
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for AreaServiceImpl")
class AreaServiceImplTest {

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private StateService stateService;

    @InjectMocks
    private AreaServiceImpl areaService;

    private Area testArea;
    private State activeState;
    private State eliminatedState;
    private AreaDto areaDto;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        testArea = TestDataFactory.createLisbonArea();
        testArea.setId(1L);
        testArea.setState(activeState);
        
        areaDto = TestDataFactory.createValidAreaDto(1L);
    }

    @Test
    @DisplayName("Should create area successfully")
    void shouldCreateAreaSuccessfully() {
        // Given
        when(stateService.findById(1L)).thenReturn(activeState);
        when(areaRepository.save(any(Area.class))).thenReturn(testArea);
        
        // When
        AreaResponse result = areaService.createArea(areaDto);
        
        // Then
        assertNotNull(result);
        assertEquals("Lisboa Centro", result.area());
        assertEquals("Central area of Lisboa", result.description());
        assertEquals(1L, result.stateId());
        assertEquals("ACTIVE", result.state());
        
        verify(stateService).findById(1L);
        verify(areaRepository).save(any(Area.class));
    }

    @Test
    @DisplayName("Should update area successfully")
    void shouldUpdateAreaSuccessfully() {
        // Given
        AreaDto updateDto = TestDataFactory.createValidAreaDto("Cascais", "Updated description", 1L);
        
        Area updatedArea = TestDataFactory.createCascaisArea();
        updatedArea.setId(1L);
        updatedArea.setState(activeState);
        updatedArea.setDescription("Updated description");
        
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(areaRepository.save(any(Area.class))).thenReturn(updatedArea);
        
        // When
        AreaResponse result = areaService.updateArea(1L, updateDto);
        
        // Then
        assertNotNull(result);
        assertEquals("Cascais", result.area());
        assertEquals("Updated description", result.description());
        
        verify(areaRepository).findById(1L);
        verify(stateService).findById(1L);
        verify(areaRepository).save(any(Area.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent area")
    void shouldThrowExceptionWhenUpdatingNonExistentArea() {
        // Given
        when(areaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            areaService.updateArea(999L, areaDto);
        });
        
        verify(areaRepository).findById(999L);
        verify(areaRepository, never()).save(any(Area.class));
    }

    @Test
    @DisplayName("Should get all areas excluding eliminated")
    void shouldGetAllAreasExcludingEliminated() {
        // Given
        Area area1 = TestDataFactory.createLisbonArea();
        area1.setState(activeState);
        
        Area area2 = TestDataFactory.createCascaisArea();
        area2.setState(activeState);
        
        Area eliminatedArea = TestDataFactory.createSintraArea();
        eliminatedArea.setState(eliminatedState);
        
        List<Area> areas = Arrays.asList(area1, area2, eliminatedArea);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(areaRepository.findAll()).thenReturn(areas);
        
        // When
        List<AreaResponse> result = areaService.getAllAreas();
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.area().equals("Lisboa Centro")));
        assertTrue(result.stream().anyMatch(a -> a.area().equals("Cascais")));
        assertFalse(result.stream().anyMatch(a -> a.area().equals("Sintra")));
        
        verify(stateService).getEliminatedState();
        verify(areaRepository).findAll();
    }

    @Test
    @DisplayName("Should get area by ID successfully")
    void shouldGetAreaByIdSuccessfully() {
        // Given
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        
        // When
        Area result = areaService.getAreaById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Lisboa Centro", result.getArea());
        assertEquals(1L, result.getId());
        
        verify(areaRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent area by ID")
    void shouldThrowExceptionWhenGettingNonExistentAreaById() {
        // Given
        when(areaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            areaService.getAreaById(999L);
        });
        
        verify(areaRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get area by ID as response successfully")
    void shouldGetAreaByIdResponseSuccessfully() {
        // Given
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        
        // When
        AreaResponse result = areaService.getAreaByIdResponse(1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Lisboa Centro", result.area());
        assertEquals(1L, result.id());
        
        verify(areaRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get areas by state ID")
    void shouldGetAreasByStateId() {
        // Given
        List<Area> areas = Arrays.asList(testArea);
        when(areaRepository.findByStateId(1L)).thenReturn(areas);
        
        // When
        List<AreaResponse> result = areaService.getAreasByState(1L);
        
        // Then
        assertEquals(1, result.size());
        assertEquals("Lisboa Centro", result.get(0).area());
        
        verify(areaRepository).findByStateId(1L);
    }

    @Test
    @DisplayName("Should delete area with soft delete")
    void shouldDeleteAreaWithSoftDelete() {
        // Given
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(areaRepository.save(any(Area.class))).thenReturn(testArea);
        
        // When
        areaService.deleteArea(1L);
        
        // Then
        verify(areaRepository).findById(1L);
        verify(stateService).getEliminatedState();
        verify(areaRepository).save(any(Area.class));
        
        // Verify that the area state was set to eliminated
        assertEquals(eliminatedState, testArea.getState());
    }

    @Test
    @DisplayName("Should find by ID successfully")
    void shouldFindByIdSuccessfully() {
        // Given
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        
        // When
        Area result = areaService.findById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testArea, result);
        
        verify(areaRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when finding by non-existent ID")
    void shouldThrowExceptionWhenFindingByNonExistentId() {
        // Given
        when(areaRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            areaService.findById(999L);
        });
        
        verify(areaRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find by ID and state ID successfully")
    void shouldFindByIdAndStateIdSuccessfully() {
        // Given
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        
        // When
        Area result = areaService.findByIdAndStateId(1L, 1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testArea, result);
        
        verify(areaRepository).findById(1L);
    }

    @Test
    @DisplayName("Should use default state when state ID is null")
    void shouldUseDefaultStateWhenStateIdIsNull() {
        // Given
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        
        // When
        Area result = areaService.findByIdAndStateId(1L, null);
        
        // Then
        assertNotNull(result);
        assertEquals(testArea, result);
        
        verify(areaRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when state doesn't match")
    void shouldThrowExceptionWhenStateDoesntMatch() {
        // Given
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            areaService.findByIdAndStateId(1L, 4L); // Different state ID
        });
        
        verify(areaRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle empty repository when getting all areas")
    void shouldHandleEmptyRepositoryWhenGettingAllAreas() {
        // Given
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(areaRepository.findAll()).thenReturn(Arrays.asList());
        
        // When
        List<AreaResponse> result = areaService.getAllAreas();
        
        // Then
        assertTrue(result.isEmpty());
        
        verify(areaRepository).findAll();
    }

    @Test
    @DisplayName("Should handle area with null state when getting all areas")
    void shouldHandleAreaWithNullStateWhenGettingAllAreas() {
        // Given
        Area areaWithNullState = TestDataFactory.createLisbonArea();
        areaWithNullState.setState(null);
        
        List<Area> areas = Arrays.asList(testArea, areaWithNullState);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(areaRepository.findAll()).thenReturn(areas);
        
        // When
        List<AreaResponse> result = areaService.getAllAreas();
        
        // Then
        assertEquals(1, result.size());
        assertEquals("Lisboa Centro", result.get(0).area());
        
        verify(areaRepository).findAll();
    }

    @Test
    @DisplayName("Should throw exception when state service fails")
    void shouldThrowExceptionWhenStateServiceFails() {
        // Given
        when(stateService.findById(999L)).thenThrow(new EntityNotFoundException("State not found"));
        
        AreaDto invalidDto = TestDataFactory.createValidAreaDto(999L);
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            areaService.createArea(invalidDto);
        });
        
        verify(stateService).findById(999L);
        verify(areaRepository, never()).save(any(Area.class));
    }

    @Test
    @DisplayName("Should create area with null description")
    void shouldCreateAreaWithNullDescription() {
        // Given
        AreaDto dtoWithNullDescription = TestDataFactory.createValidAreaDto("Test Area", null, 1L);
        
        Area areaWithNullDescription = new Area();
        areaWithNullDescription.setId(1L);
        areaWithNullDescription.setArea("Test Area");
        areaWithNullDescription.setDescription(null);
        areaWithNullDescription.setState(activeState);
        
        when(stateService.findById(1L)).thenReturn(activeState);
        when(areaRepository.save(any(Area.class))).thenReturn(areaWithNullDescription);
        
        // When
        AreaResponse result = areaService.createArea(dtoWithNullDescription);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Area", result.area());
        assertNull(result.description());
        
        verify(stateService).findById(1L);
        verify(areaRepository).save(any(Area.class));
    }

    @Test
    @DisplayName("Should create area with empty description")
    void shouldCreateAreaWithEmptyDescription() {
        // Given
        AreaDto dtoWithEmptyDescription = TestDataFactory.createValidAreaDto("Test Area", "", 1L);
        
        Area areaWithEmptyDescription = new Area();
        areaWithEmptyDescription.setId(1L);
        areaWithEmptyDescription.setArea("Test Area");
        areaWithEmptyDescription.setDescription("");
        areaWithEmptyDescription.setState(activeState);
        
        when(stateService.findById(1L)).thenReturn(activeState);
        when(areaRepository.save(any(Area.class))).thenReturn(areaWithEmptyDescription);
        
        // When
        AreaResponse result = areaService.createArea(dtoWithEmptyDescription);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Area", result.area());
        assertEquals("", result.description());
        
        verify(stateService).findById(1L);
        verify(areaRepository).save(any(Area.class));
    }

    @Test
    @DisplayName("Should update area description from null to value")
    void shouldUpdateAreaDescriptionFromNullToValue() {
        // Given
        Area areaWithNullDescription = TestDataFactory.createLisbonArea();
        areaWithNullDescription.setId(1L);
        areaWithNullDescription.setDescription(null);
        areaWithNullDescription.setState(activeState);
        
        AreaDto updateDto = TestDataFactory.createValidAreaDto("Lisboa Centro", "New description", 1L);
        
        Area updatedArea = TestDataFactory.createLisbonArea();
        updatedArea.setId(1L);
        updatedArea.setDescription("New description");
        updatedArea.setState(activeState);
        
        when(areaRepository.findById(1L)).thenReturn(Optional.of(areaWithNullDescription));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(areaRepository.save(any(Area.class))).thenReturn(updatedArea);
        
        // When
        AreaResponse result = areaService.updateArea(1L, updateDto);
        
        // Then
        assertNotNull(result);
        assertEquals("Lisboa Centro", result.area());
        assertEquals("New description", result.description());
        
        verify(areaRepository).findById(1L);
        verify(stateService).findById(1L);
        verify(areaRepository).save(any(Area.class));
    }

    @Test
    @DisplayName("Should filter areas correctly by different states")
    void shouldFilterAreasCorrectlyByDifferentStates() {
        // Given
        Area activeArea = TestDataFactory.createLisbonArea();
        activeArea.setState(activeState);
        
        State inactiveState = TestDataFactory.createInactiveState();
        inactiveState.setId(2L);
        
        Area inactiveArea = TestDataFactory.createCascaisArea();
        inactiveArea.setState(inactiveState);
        
        Area eliminatedArea = TestDataFactory.createSintraArea();
        eliminatedArea.setState(eliminatedState);
        
        List<Area> areas = Arrays.asList(activeArea, inactiveArea, eliminatedArea);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(areaRepository.findAll()).thenReturn(areas);
        
        // When
        List<AreaResponse> result = areaService.getAllAreas();
        
        // Then
        assertEquals(2, result.size()); // Should exclude only eliminated
        assertTrue(result.stream().anyMatch(a -> a.area().equals("Lisboa Centro")));
        assertTrue(result.stream().anyMatch(a -> a.area().equals("Cascais")));
        assertFalse(result.stream().anyMatch(a -> a.area().equals("Sintra")));
        
        verify(areaRepository).findAll();
    }
}