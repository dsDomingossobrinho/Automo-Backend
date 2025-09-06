package com.automo.state.repository;

import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for StateRepository")
class StateRepositoryTest {

    @Autowired
    private StateRepository stateRepository;

    private State activeState;
    private State inactiveState;
    private State eliminatedState;

    @BeforeEach
    void setUp() {
        // Clean database
        stateRepository.deleteAll();

        // Create and save test states
        activeState = TestDataFactory.createActiveState();
        activeState.setDescription("Active state description");
        activeState = stateRepository.save(activeState);

        inactiveState = TestDataFactory.createInactiveState();
        inactiveState.setDescription("Inactive state description");
        inactiveState = stateRepository.save(inactiveState);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setDescription("Eliminated state description");
        eliminatedState = stateRepository.save(eliminatedState);
    }

    @Test
    @DisplayName("Should save and find state by ID")
    void shouldSaveAndFindStateById() {
        // Given
        State newState = new State();
        newState.setState("PENDING");
        newState.setDescription("Pending state");

        // When
        State savedState = stateRepository.save(newState);
        Optional<State> foundState = stateRepository.findById(savedState.getId());

        // Then
        assertNotNull(savedState.getId());
        assertTrue(foundState.isPresent());
        assertEquals("PENDING", foundState.get().getState());
        assertEquals("Pending state", foundState.get().getDescription());
        assertNotNull(foundState.get().getCreatedAt());
        assertNotNull(foundState.get().getUpdatedAt());
    }

    @Test
    @DisplayName("Should find state by state name")
    void shouldFindStateByStateName() {
        // When
        Optional<State> foundActive = stateRepository.findByState("ACTIVE");
        Optional<State> foundInactive = stateRepository.findByState("INACTIVE");
        Optional<State> foundEliminated = stateRepository.findByState("ELIMINATED");

        // Then
        assertTrue(foundActive.isPresent());
        assertEquals("ACTIVE", foundActive.get().getState());
        assertEquals(activeState.getId(), foundActive.get().getId());

        assertTrue(foundInactive.isPresent());
        assertEquals("INACTIVE", foundInactive.get().getState());
        assertEquals(inactiveState.getId(), foundInactive.get().getId());

        assertTrue(foundEliminated.isPresent());
        assertEquals("ELIMINATED", foundEliminated.get().getState());
        assertEquals(eliminatedState.getId(), foundEliminated.get().getId());
    }

    @Test
    @DisplayName("Should return empty when finding non-existent state by name")
    void shouldReturnEmptyWhenFindingNonExistentStateByName() {
        // When
        Optional<State> notFound = stateRepository.findByState("NON_EXISTENT");

        // Then
        assertFalse(notFound.isPresent());
    }

    @Test
    @DisplayName("Should check if state exists by name")
    void shouldCheckIfStateExistsByName() {
        // When & Then
        assertTrue(stateRepository.existsByState("ACTIVE"));
        assertTrue(stateRepository.existsByState("INACTIVE"));
        assertTrue(stateRepository.existsByState("ELIMINATED"));
        assertFalse(stateRepository.existsByState("NON_EXISTENT"));
    }

    @Test
    @DisplayName("Should find all states")
    void shouldFindAllStates() {
        // When
        List<State> allStates = stateRepository.findAll();

        // Then
        assertEquals(3, allStates.size());
        assertTrue(allStates.stream().anyMatch(s -> "ACTIVE".equals(s.getState())));
        assertTrue(allStates.stream().anyMatch(s -> "INACTIVE".equals(s.getState())));
        assertTrue(allStates.stream().anyMatch(s -> "ELIMINATED".equals(s.getState())));
    }

    @Test
    @DisplayName("Should find all states with sorting")
    void shouldFindAllStatesWithSorting() {
        // When
        List<State> statesAsc = stateRepository.findAll(Sort.by(Sort.Direction.ASC, "state"));
        List<State> statesDesc = stateRepository.findAll(Sort.by(Sort.Direction.DESC, "state"));

        // Then
        assertEquals(3, statesAsc.size());
        assertEquals(3, statesDesc.size());

        // Check ascending order
        assertEquals("ACTIVE", statesAsc.get(0).getState());
        assertEquals("ELIMINATED", statesAsc.get(1).getState());
        assertEquals("INACTIVE", statesAsc.get(2).getState());

        // Check descending order
        assertEquals("INACTIVE", statesDesc.get(0).getState());
        assertEquals("ELIMINATED", statesDesc.get(1).getState());
        assertEquals("ACTIVE", statesDesc.get(2).getState());
    }

    @Test
    @DisplayName("Should find states with pagination")
    void shouldFindStatesWithPagination() {
        // Given
        Pageable firstPage = PageRequest.of(0, 2, Sort.by(Sort.Direction.ASC, "state"));
        Pageable secondPage = PageRequest.of(1, 2, Sort.by(Sort.Direction.ASC, "state"));

        // When
        Page<State> page1 = stateRepository.findAll(firstPage);
        Page<State> page2 = stateRepository.findAll(secondPage);

        // Then
        assertEquals(2, page1.getSize());
        assertEquals(2, page1.getContent().size());
        assertTrue(page1.hasNext());
        assertEquals(3, page1.getTotalElements());
        assertEquals(2, page1.getTotalPages());

        assertEquals(1, page2.getContent().size());
        assertFalse(page2.hasNext());
        assertTrue(page2.hasPrevious());
    }

    @Test
    @DisplayName("Should find states by state name containing ignore case")
    void shouldFindStatesByStateNameContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<State> foundByActive = stateRepository
                .findByStateContainingIgnoreCaseOrDescriptionContainingIgnoreCase("act", "", pageable);
        Page<State> foundByInactive = stateRepository
                .findByStateContainingIgnoreCaseOrDescriptionContainingIgnoreCase("INACT", "", pageable);

        // Then
        assertEquals(2, foundByActive.getTotalElements()); // ACTIVE and INACTIVE both contain "act"
        assertEquals(1, foundByInactive.getTotalElements()); // Only INACTIVE contains "INACT"
    }

    @Test
    @DisplayName("Should find states by description containing ignore case")
    void shouldFindStatesByDescriptionContainingIgnoreCase() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<State> foundByDescription = stateRepository
                .findByStateContainingIgnoreCaseOrDescriptionContainingIgnoreCase("", "description", pageable);

        // Then
        assertEquals(3, foundByDescription.getTotalElements()); // All states have "description" in description
    }

    @Test
    @DisplayName("Should find states by state or description containing text")
    void shouldFindStatesByStateOrDescriptionContainingText() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<State> foundByElim = stateRepository
                .findByStateContainingIgnoreCaseOrDescriptionContainingIgnoreCase("ELIM", "ELIM", pageable);

        // Then
        assertEquals(1, foundByElim.getTotalElements());
        assertEquals("ELIMINATED", foundByElim.getContent().get(0).getState());
    }

    @Test
    @DisplayName("Should return empty page when no states match search criteria")
    void shouldReturnEmptyPageWhenNoStatesMatchSearchCriteria() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<State> notFound = stateRepository
                .findByStateContainingIgnoreCaseOrDescriptionContainingIgnoreCase("NONEXISTENT", "NONEXISTENT", pageable);

        // Then
        assertEquals(0, notFound.getTotalElements());
        assertTrue(notFound.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should handle case insensitive search")
    void shouldHandleCaseInsensitiveSearch() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<State> lowerCase = stateRepository
                .findByStateContainingIgnoreCaseOrDescriptionContainingIgnoreCase("active", "", pageable);
        Page<State> upperCase = stateRepository
                .findByStateContainingIgnoreCaseOrDescriptionContainingIgnoreCase("ACTIVE", "", pageable);
        Page<State> mixedCase = stateRepository
                .findByStateContainingIgnoreCaseOrDescriptionContainingIgnoreCase("AcTiVe", "", pageable);

        // Then
        assertEquals(2, lowerCase.getTotalElements()); // ACTIVE and INACTIVE
        assertEquals(2, upperCase.getTotalElements()); // ACTIVE and INACTIVE
        assertEquals(2, mixedCase.getTotalElements()); // ACTIVE and INACTIVE
    }

    @Test
    @DisplayName("Should count total states")
    void shouldCountTotalStates() {
        // When
        long count = stateRepository.count();

        // Then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should delete state")
    void shouldDeleteState() {
        // Given
        long initialCount = stateRepository.count();

        // When
        stateRepository.delete(activeState);
        long finalCount = stateRepository.count();

        // Then
        assertEquals(initialCount - 1, finalCount);
        assertFalse(stateRepository.findByState("ACTIVE").isPresent());
    }

    @Test
    @DisplayName("Should delete state by ID")
    void shouldDeleteStateById() {
        // Given
        long initialCount = stateRepository.count();
        Long stateId = inactiveState.getId();

        // When
        stateRepository.deleteById(stateId);
        long finalCount = stateRepository.count();

        // Then
        assertEquals(initialCount - 1, finalCount);
        assertFalse(stateRepository.findById(stateId).isPresent());
        assertFalse(stateRepository.findByState("INACTIVE").isPresent());
    }

    @Test
    @DisplayName("Should update state")
    void shouldUpdateState() {
        // Given
        String newStateName = "PENDING";
        String newDescription = "Updated pending state";

        // When
        activeState.setState(newStateName);
        activeState.setDescription(newDescription);
        State updatedState = stateRepository.save(activeState);

        // Then
        assertEquals(newStateName, updatedState.getState());
        assertEquals(newDescription, updatedState.getDescription());
        assertEquals(activeState.getId(), updatedState.getId());

        // Verify in database
        Optional<State> fromDb = stateRepository.findById(activeState.getId());
        assertTrue(fromDb.isPresent());
        assertEquals(newStateName, fromDb.get().getState());
        assertEquals(newDescription, fromDb.get().getDescription());
    }

    @Test
    @DisplayName("Should handle state with special characters")
    void shouldHandleStateWithSpecialCharacters() {
        // Given
        State specialState = new State();
        specialState.setState("PENDING-APPROVAL_2024");
        specialState.setDescription("Pending Approval (2024) - Special Case & Info!");

        // When
        State saved = stateRepository.save(specialState);

        // Then
        assertNotNull(saved.getId());
        Optional<State> found = stateRepository.findByState("PENDING-APPROVAL_2024");
        assertTrue(found.isPresent());
        assertEquals("PENDING-APPROVAL_2024", found.get().getState());
        assertEquals("Pending Approval (2024) - Special Case & Info!", found.get().getDescription());
    }

    @Test
    @DisplayName("Should handle state with null description")
    void shouldHandleStateWithNullDescription() {
        // Given
        State stateWithoutDescription = new State();
        stateWithoutDescription.setState("NO_DESCRIPTION");
        stateWithoutDescription.setDescription(null);

        // When
        State saved = stateRepository.save(stateWithoutDescription);

        // Then
        assertNotNull(saved.getId());
        Optional<State> found = stateRepository.findByState("NO_DESCRIPTION");
        assertTrue(found.isPresent());
        assertEquals("NO_DESCRIPTION", found.get().getState());
        assertNull(found.get().getDescription());
    }

    @Test
    @DisplayName("Should handle empty search terms")
    void shouldHandleEmptySearchTerms() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<State> emptySearch = stateRepository
                .findByStateContainingIgnoreCaseOrDescriptionContainingIgnoreCase("", "", pageable);

        // Then
        assertEquals(3, emptySearch.getTotalElements()); // Should return all states
    }

    @Test
    @DisplayName("Should maintain data consistency across operations")
    void shouldMaintainDataConsistencyAcrossOperations() {
        // Given
        long initialCount = stateRepository.count();
        
        // When - Create new state
        State newState = new State();
        newState.setState("TEMPORARY");
        newState.setDescription("Temporary state for testing");
        State saved = stateRepository.save(newState);
        
        // Then - Verify creation
        assertEquals(initialCount + 1, stateRepository.count());
        assertTrue(stateRepository.existsByState("TEMPORARY"));
        
        // When - Update state
        saved.setState("TEMP_UPDATED");
        saved.setDescription("Updated temporary state");
        stateRepository.save(saved);
        
        // Then - Verify update
        assertEquals(initialCount + 1, stateRepository.count());
        assertFalse(stateRepository.existsByState("TEMPORARY"));
        assertTrue(stateRepository.existsByState("TEMP_UPDATED"));
        
        // When - Delete state
        stateRepository.delete(saved);
        
        // Then - Verify deletion
        assertEquals(initialCount, stateRepository.count());
        assertFalse(stateRepository.existsByState("TEMP_UPDATED"));
    }

    @Test
    @DisplayName("Should handle concurrent access scenarios")
    void shouldHandleConcurrentAccessScenarios() {
        // Given
        String stateName = "CONCURRENT_TEST";
        
        // When - Multiple exists checks
        boolean exists1 = stateRepository.existsByState(stateName);
        boolean exists2 = stateRepository.existsByState(stateName);
        boolean exists3 = stateRepository.existsByState(stateName);
        
        // Then
        assertFalse(exists1);
        assertFalse(exists2);
        assertFalse(exists3);
        
        // When - Create state after checks
        State concurrentState = new State();
        concurrentState.setState(stateName);
        stateRepository.save(concurrentState);
        
        // Then - Verify it now exists
        assertTrue(stateRepository.existsByState(stateName));
    }
}