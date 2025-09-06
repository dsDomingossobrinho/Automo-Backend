package com.automo.area.repository;

import com.automo.area.entity.Area;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for AreaRepository")
class AreaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private StateRepository stateRepository;

    private State activeState;
    private State inactiveState;
    private Area testArea;

    @BeforeEach
    void setUp() {
        // Create and persist states
        activeState = TestDataFactory.createActiveState();
        inactiveState = TestDataFactory.createInactiveState();
        
        activeState = stateRepository.save(activeState);
        inactiveState = stateRepository.save(inactiveState);
        
        // Create test area
        testArea = TestDataFactory.createLisbonArea();
        testArea.setState(activeState);
        
        entityManager.persistAndFlush(testArea);
        entityManager.clear();
    }

    @Test
    @DisplayName("Should save area successfully")
    void shouldSaveAreaSuccessfully() {
        // Given
        Area newArea = TestDataFactory.createCascaisArea();
        newArea.setState(activeState);
        
        // When
        Area savedArea = areaRepository.save(newArea);
        
        // Then
        assertNotNull(savedArea);
        assertNotNull(savedArea.getId());
        assertEquals("Cascais", savedArea.getArea());
        assertEquals("Coastal area near Lisboa", savedArea.getDescription());
        assertEquals(activeState.getId(), savedArea.getState().getId());
        assertNotNull(savedArea.getCreatedAt());
        assertNotNull(savedArea.getUpdatedAt());
    }

    @Test
    @DisplayName("Should find area by ID")
    void shouldFindAreaById() {
        // When
        Optional<Area> found = areaRepository.findById(testArea.getId());
        
        // Then
        assertTrue(found.isPresent());
        Area area = found.get();
        assertEquals("Lisboa Centro", area.getArea());
        assertEquals("Central area of Lisboa", area.getDescription());
        assertEquals(activeState.getId(), area.getState().getId());
    }

    @Test
    @DisplayName("Should return empty when finding non-existent area")
    void shouldReturnEmptyWhenFindingNonExistentArea() {
        // When
        Optional<Area> found = areaRepository.findById(999L);
        
        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find all areas")
    void shouldFindAllAreas() {
        // Given
        Area cascais = TestDataFactory.createCascaisArea();
        cascais.setState(activeState);
        entityManager.persistAndFlush(cascais);
        
        Area sintra = TestDataFactory.createSintraArea();
        sintra.setState(inactiveState);
        entityManager.persistAndFlush(sintra);
        
        // When
        List<Area> areas = areaRepository.findAll();
        
        // Then
        assertEquals(3, areas.size());
        assertTrue(areas.stream().anyMatch(a -> a.getArea().equals("Lisboa Centro")));
        assertTrue(areas.stream().anyMatch(a -> a.getArea().equals("Cascais")));
        assertTrue(areas.stream().anyMatch(a -> a.getArea().equals("Sintra")));
    }

    @Test
    @DisplayName("Should find areas by state ID")
    void shouldFindAreasByStateId() {
        // Given
        Area cascais = TestDataFactory.createCascaisArea();
        cascais.setState(activeState);
        entityManager.persistAndFlush(cascais);
        
        Area sintra = TestDataFactory.createSintraArea();
        sintra.setState(inactiveState);
        entityManager.persistAndFlush(sintra);
        
        // When
        List<Area> activeAreas = areaRepository.findByStateId(activeState.getId());
        List<Area> inactiveAreas = areaRepository.findByStateId(inactiveState.getId());
        
        // Then
        assertEquals(2, activeAreas.size());
        assertEquals(1, inactiveAreas.size());
        
        assertTrue(activeAreas.stream().allMatch(a -> a.getState().getId().equals(activeState.getId())));
        assertTrue(inactiveAreas.stream().allMatch(a -> a.getState().getId().equals(inactiveState.getId())));
        
        assertTrue(activeAreas.stream().anyMatch(a -> a.getArea().equals("Lisboa Centro")));
        assertTrue(activeAreas.stream().anyMatch(a -> a.getArea().equals("Cascais")));
        assertTrue(inactiveAreas.stream().anyMatch(a -> a.getArea().equals("Sintra")));
    }

    @Test
    @DisplayName("Should return empty list when no areas exist for state")
    void shouldReturnEmptyListWhenNoAreasExistForState() {
        // Given
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = stateRepository.save(eliminatedState);
        
        // When
        List<Area> areas = areaRepository.findByStateId(eliminatedState.getId());
        
        // Then
        assertTrue(areas.isEmpty());
    }

    @Test
    @DisplayName("Should update area successfully")
    void shouldUpdateAreaSuccessfully() {
        // Given
        Area area = areaRepository.findById(testArea.getId()).orElseThrow();
        area.setArea("Updated Lisboa Centro");
        area.setDescription("Updated description");
        
        // When
        Area updatedArea = areaRepository.save(area);
        
        // Then
        assertEquals("Updated Lisboa Centro", updatedArea.getArea());
        assertEquals("Updated description", updatedArea.getDescription());
        assertNotNull(updatedArea.getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete area successfully")
    void shouldDeleteAreaSuccessfully() {
        // Given
        Long areaId = testArea.getId();
        
        // When
        areaRepository.deleteById(areaId);
        
        // Then
        Optional<Area> deleted = areaRepository.findById(areaId);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should maintain referential integrity with state")
    void shouldMaintainReferentialIntegrityWithState() {
        // Given
        Area area = areaRepository.findById(testArea.getId()).orElseThrow();
        
        // When
        State state = area.getState();
        
        // Then
        assertNotNull(state);
        assertEquals(activeState.getId(), state.getId());
        assertEquals("ACTIVE", state.getState());
    }

    @Test
    @DisplayName("Should handle lazy loading of state")
    void shouldHandleLazyLoadingOfState() {
        // Given
        entityManager.clear(); // Clear persistence context
        
        // When
        Area area = areaRepository.findById(testArea.getId()).orElseThrow();
        
        // Then - Accessing state should trigger lazy loading
        State state = area.getState();
        assertNotNull(state);
        assertEquals("ACTIVE", state.getState());
    }

    @Test
    @DisplayName("Should persist timestamps correctly")
    void shouldPersistTimestampsCorrectly() {
        // Given
        Area newArea = TestDataFactory.createSintraArea();
        newArea.setState(activeState);
        
        // When
        Area savedArea = areaRepository.save(newArea);
        
        // Then
        assertNotNull(savedArea.getCreatedAt());
        assertNotNull(savedArea.getUpdatedAt());
        assertEquals(savedArea.getCreatedAt(), savedArea.getUpdatedAt());
        
        // When updating
        savedArea.setArea("Updated Sintra");
        Area updatedArea = areaRepository.save(savedArea);
        
        // Then
        assertEquals(savedArea.getCreatedAt(), updatedArea.getCreatedAt());
        assertTrue(updatedArea.getUpdatedAt().isAfter(updatedArea.getCreatedAt()));
    }

    @Test
    @DisplayName("Should handle null description")
    void shouldHandleNullDescription() {
        // Given
        Area area = new Area();
        area.setArea("Test Area");
        area.setDescription(null);
        area.setState(activeState);
        
        // When
        Area savedArea = areaRepository.save(area);
        
        // Then
        assertNotNull(savedArea.getId());
        assertEquals("Test Area", savedArea.getArea());
        assertNull(savedArea.getDescription());
        assertNotNull(savedArea.getState());
    }

    @Test
    @DisplayName("Should handle empty description")
    void shouldHandleEmptyDescription() {
        // Given
        Area area = new Area();
        area.setArea("Test Area");
        area.setDescription("");
        area.setState(activeState);
        
        // When
        Area savedArea = areaRepository.save(area);
        
        // Then
        assertNotNull(savedArea.getId());
        assertEquals("Test Area", savedArea.getArea());
        assertEquals("", savedArea.getDescription());
    }

    @Test
    @DisplayName("Should handle long descriptions")
    void shouldHandleLongDescriptions() {
        // Given
        String longDescription = "This is a very long description that contains multiple sentences. " +
                "It describes the area in great detail, including its history, geography, " +
                "cultural significance, and modern development. This tests that the description " +
                "field can handle longer text content without issues. It should be stored " +
                "and retrieved correctly from the database.";
        
        Area area = new Area();
        area.setArea("Historic District");
        area.setDescription(longDescription);
        area.setState(activeState);
        
        // When
        Area savedArea = areaRepository.save(area);
        
        // Then
        assertNotNull(savedArea.getId());
        assertEquals("Historic District", savedArea.getArea());
        assertEquals(longDescription, savedArea.getDescription());
    }

    @Test
    @DisplayName("Should handle special characters in area names")
    void shouldHandleSpecialCharactersInAreaNames() {
        // Given
        Area area = new Area();
        area.setArea("São João do Estoril");
        area.setDescription("Area with special characters");
        area.setState(activeState);
        
        // When
        Area savedArea = areaRepository.save(area);
        
        // Then
        assertNotNull(savedArea.getId());
        assertEquals("São João do Estoril", savedArea.getArea());
        assertEquals("Area with special characters", savedArea.getDescription());
    }

    @Test
    @DisplayName("Should handle numeric characters in area names")
    void shouldHandleNumericCharactersInAreaNames() {
        // Given
        Area area = new Area();
        area.setArea("Zone 1A - Industrial Area");
        area.setDescription("Industrial zone with numeric designation");
        area.setState(activeState);
        
        // When
        Area savedArea = areaRepository.save(area);
        
        // Then
        assertNotNull(savedArea.getId());
        assertEquals("Zone 1A - Industrial Area", savedArea.getArea());
        assertEquals("Industrial zone with numeric designation", savedArea.getDescription());
    }

    @Test
    @DisplayName("Should allow multiple areas with same name but different descriptions")
    void shouldAllowMultipleAreasWithSameNameButDifferentDescriptions() {
        // Given
        Area area1 = new Area();
        area1.setArea("Centro");
        area1.setDescription("Historical center");
        area1.setState(activeState);
        
        Area area2 = new Area();
        area2.setArea("Centro");
        area2.setDescription("Commercial center");
        area2.setState(activeState);
        
        // When
        Area savedArea1 = areaRepository.save(area1);
        Area savedArea2 = areaRepository.save(area2);
        
        // Then - Both should save successfully (no unique constraint on area name)
        assertNotNull(savedArea1.getId());
        assertNotNull(savedArea2.getId());
        assertEquals("Centro", savedArea1.getArea());
        assertEquals("Centro", savedArea2.getArea());
        assertEquals("Historical center", savedArea1.getDescription());
        assertEquals("Commercial center", savedArea2.getDescription());
    }

    @Test
    @DisplayName("Should handle state changes correctly")
    void shouldHandleStateChangesCorrectly() {
        // Given
        Area area = areaRepository.findById(testArea.getId()).orElseThrow();
        
        // When - Changing state
        area.setState(inactiveState);
        Area updatedArea = areaRepository.save(area);
        
        // Then
        assertEquals(inactiveState.getId(), updatedArea.getState().getId());
        assertEquals("INACTIVE", updatedArea.getState().getState());
    }

    @Test
    @DisplayName("Should filter areas by different states correctly")
    void shouldFilterAreasByDifferentStatesCorrectly() {
        // Given
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = stateRepository.save(eliminatedState);
        
        Area activeArea = TestDataFactory.createCascaisArea();
        activeArea.setState(activeState);
        entityManager.persistAndFlush(activeArea);
        
        Area inactiveArea = TestDataFactory.createSintraArea();
        inactiveArea.setState(inactiveState);
        entityManager.persistAndFlush(inactiveArea);
        
        Area eliminatedArea = new Area();
        eliminatedArea.setArea("Eliminated Area");
        eliminatedArea.setDescription("This area is eliminated");
        eliminatedArea.setState(eliminatedState);
        entityManager.persistAndFlush(eliminatedArea);
        
        // When
        List<Area> activeAreas = areaRepository.findByStateId(activeState.getId());
        List<Area> inactiveAreas = areaRepository.findByStateId(inactiveState.getId());
        List<Area> eliminatedAreas = areaRepository.findByStateId(eliminatedState.getId());
        
        // Then
        assertEquals(2, activeAreas.size()); // Lisboa Centro + Cascais
        assertEquals(1, inactiveAreas.size()); // Sintra
        assertEquals(1, eliminatedAreas.size()); // Eliminated Area
        
        assertTrue(activeAreas.stream().anyMatch(a -> a.getArea().equals("Lisboa Centro")));
        assertTrue(activeAreas.stream().anyMatch(a -> a.getArea().equals("Cascais")));
        assertTrue(inactiveAreas.stream().anyMatch(a -> a.getArea().equals("Sintra")));
        assertTrue(eliminatedAreas.stream().anyMatch(a -> a.getArea().equals("Eliminated Area")));
    }

    @Test
    @DisplayName("Should handle minimal area data")
    void shouldHandleMinimalAreaData() {
        // Given
        Area area = new Area();
        area.setArea("A"); // Single character
        area.setDescription(null); // Null description
        area.setState(activeState);
        
        // When
        Area savedArea = areaRepository.save(area);
        
        // Then
        assertNotNull(savedArea.getId());
        assertEquals("A", savedArea.getArea());
        assertNull(savedArea.getDescription());
        assertEquals(activeState.getId(), savedArea.getState().getId());
    }

    @Test
    @DisplayName("Should maintain data integrity across transactions")
    void shouldMaintainDataIntegrityAcrossTransactions() {
        // Given
        Long areaId = testArea.getId();
        
        // When - First transaction: update area name
        Area area1 = areaRepository.findById(areaId).orElseThrow();
        area1.setArea("Updated Name");
        areaRepository.save(area1);
        
        entityManager.flush();
        entityManager.clear();
        
        // When - Second transaction: update description
        Area area2 = areaRepository.findById(areaId).orElseThrow();
        area2.setDescription("Updated Description");
        areaRepository.save(area2);
        
        entityManager.flush();
        entityManager.clear();
        
        // Then - Final verification
        Area finalArea = areaRepository.findById(areaId).orElseThrow();
        assertEquals("Updated Name", finalArea.getArea());
        assertEquals("Updated Description", finalArea.getDescription());
    }
}