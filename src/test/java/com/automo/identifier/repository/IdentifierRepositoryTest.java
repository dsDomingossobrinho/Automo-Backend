package com.automo.identifier.repository;

import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.state.entity.State;
import com.automo.test.utils.TestDataFactory;
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
@DisplayName("Tests for IdentifierRepository")
class IdentifierRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IdentifierRepository identifierRepository;

    private IdentifierType nifType;
    private IdentifierType nipcType;
    private State activeState;
    private State inactiveState;
    private State eliminatedState;
    private Identifier identifier1;
    private Identifier identifier2;
    private Identifier identifier3;

    @BeforeEach
    void setUp() {
        // Setup identifier types
        nifType = TestDataFactory.createNifIdentifierType();
        nifType = entityManager.persistAndFlush(nifType);

        nipcType = TestDataFactory.createValidIdentifierType("NIPC", "Número de Identificação de Pessoa Coletiva");
        nipcType = entityManager.persistAndFlush(nipcType);

        // Setup states
        activeState = TestDataFactory.createActiveState();
        activeState = entityManager.persistAndFlush(activeState);

        inactiveState = TestDataFactory.createInactiveState();
        inactiveState = entityManager.persistAndFlush(inactiveState);

        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = entityManager.persistAndFlush(eliminatedState);

        // Setup identifiers
        identifier1 = TestDataFactory.createValidIdentifier(1L, nifType, activeState);
        identifier1 = entityManager.persistAndFlush(identifier1);

        identifier2 = TestDataFactory.createValidIdentifier(2L, nipcType, activeState);
        identifier2 = entityManager.persistAndFlush(identifier2);

        identifier3 = TestDataFactory.createValidIdentifier(1L, nipcType, inactiveState);
        identifier3 = entityManager.persistAndFlush(identifier3);

        entityManager.clear(); // Clear the persistence context to ensure fresh queries
    }

    @Test
    @DisplayName("Should save and find identifier by ID")
    void shouldSaveAndFindIdentifierById() {
        // Given
        Identifier newIdentifier = TestDataFactory.createValidIdentifier(3L, nifType, activeState);
        
        // When
        Identifier saved = identifierRepository.save(newIdentifier);
        Optional<Identifier> found = identifierRepository.findById(saved.getId());
        
        // Then
        assertTrue(found.isPresent());
        assertEquals(3L, found.get().getUserId());
        assertEquals(nifType.getId(), found.get().getIdentifierType().getId());
        assertEquals(activeState.getId(), found.get().getState().getId());
    }

    @Test
    @DisplayName("Should find all identifiers")
    void shouldFindAllIdentifiers() {
        // When
        List<Identifier> identifiers = identifierRepository.findAll();
        
        // Then
        assertEquals(3, identifiers.size());
    }

    @Test
    @DisplayName("Should find identifiers by state ID")
    void shouldFindIdentifiersByStateId() {
        // When
        List<Identifier> activeIdentifiers = identifierRepository.findByStateId(activeState.getId());
        List<Identifier> inactiveIdentifiers = identifierRepository.findByStateId(inactiveState.getId());
        List<Identifier> eliminatedIdentifiers = identifierRepository.findByStateId(eliminatedState.getId());
        
        // Then
        assertEquals(2, activeIdentifiers.size()); // identifier1 and identifier2
        assertEquals(1, inactiveIdentifiers.size()); // identifier3
        assertEquals(0, eliminatedIdentifiers.size()); // no eliminated identifiers
        
        // Verify specific identifiers
        assertTrue(activeIdentifiers.stream().anyMatch(i -> i.getId().equals(identifier1.getId())));
        assertTrue(activeIdentifiers.stream().anyMatch(i -> i.getId().equals(identifier2.getId())));
        assertTrue(inactiveIdentifiers.stream().anyMatch(i -> i.getId().equals(identifier3.getId())));
    }

    @Test
    @DisplayName("Should find identifiers by user ID")
    void shouldFindIdentifiersByUserId() {
        // When
        List<Identifier> user1Identifiers = identifierRepository.findByUserId(1L);
        List<Identifier> user2Identifiers = identifierRepository.findByUserId(2L);
        List<Identifier> user999Identifiers = identifierRepository.findByUserId(999L);
        
        // Then
        assertEquals(2, user1Identifiers.size()); // identifier1 and identifier3
        assertEquals(1, user2Identifiers.size()); // identifier2
        assertEquals(0, user999Identifiers.size()); // no identifiers for non-existent user
        
        // Verify specific identifiers
        assertTrue(user1Identifiers.stream().anyMatch(i -> i.getId().equals(identifier1.getId())));
        assertTrue(user1Identifiers.stream().anyMatch(i -> i.getId().equals(identifier3.getId())));
        assertTrue(user2Identifiers.stream().anyMatch(i -> i.getId().equals(identifier2.getId())));
    }

    @Test
    @DisplayName("Should find identifiers by identifier type ID")
    void shouldFindIdentifiersByIdentifierTypeId() {
        // When
        List<Identifier> nifIdentifiers = identifierRepository.findByIdentifierTypeId(nifType.getId());
        List<Identifier> nipcIdentifiers = identifierRepository.findByIdentifierTypeId(nipcType.getId());
        
        // Then
        assertEquals(1, nifIdentifiers.size()); // identifier1
        assertEquals(2, nipcIdentifiers.size()); // identifier2 and identifier3
        
        // Verify specific identifiers
        assertTrue(nifIdentifiers.stream().anyMatch(i -> i.getId().equals(identifier1.getId())));
        assertTrue(nipcIdentifiers.stream().anyMatch(i -> i.getId().equals(identifier2.getId())));
        assertTrue(nipcIdentifiers.stream().anyMatch(i -> i.getId().equals(identifier3.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no identifiers found by state ID")
    void shouldReturnEmptyListWhenNoIdentifiersFoundByStateId() {
        // When
        List<Identifier> identifiers = identifierRepository.findByStateId(999L);
        
        // Then
        assertTrue(identifiers.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no identifiers found by user ID")
    void shouldReturnEmptyListWhenNoIdentifiersFoundByUserId() {
        // When
        List<Identifier> identifiers = identifierRepository.findByUserId(999L);
        
        // Then
        assertTrue(identifiers.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no identifiers found by identifier type ID")
    void shouldReturnEmptyListWhenNoIdentifiersFoundByIdentifierTypeId() {
        // When
        List<Identifier> identifiers = identifierRepository.findByIdentifierTypeId(999L);
        
        // Then
        assertTrue(identifiers.isEmpty());
    }

    @Test
    @DisplayName("Should update identifier state")
    void shouldUpdateIdentifierState() {
        // Given
        Identifier identifier = identifierRepository.findById(identifier1.getId()).orElseThrow();
        assertEquals(activeState.getId(), identifier.getState().getId());
        
        // When
        identifier.setState(inactiveState);
        Identifier updated = identifierRepository.save(identifier);
        
        // Then
        assertEquals(inactiveState.getId(), updated.getState().getId());
        
        // Verify persistence
        Identifier found = identifierRepository.findById(identifier1.getId()).orElseThrow();
        assertEquals(inactiveState.getId(), found.getState().getId());
    }

    @Test
    @DisplayName("Should update identifier user ID")
    void shouldUpdateIdentifierUserId() {
        // Given
        Identifier identifier = identifierRepository.findById(identifier1.getId()).orElseThrow();
        assertEquals(1L, identifier.getUserId());
        
        // When
        identifier.setUserId(99L);
        Identifier updated = identifierRepository.save(identifier);
        
        // Then
        assertEquals(99L, updated.getUserId());
        
        // Verify persistence
        Identifier found = identifierRepository.findById(identifier1.getId()).orElseThrow();
        assertEquals(99L, found.getUserId());
    }

    @Test
    @DisplayName("Should update identifier type")
    void shouldUpdateIdentifierType() {
        // Given
        Identifier identifier = identifierRepository.findById(identifier1.getId()).orElseThrow();
        assertEquals(nifType.getId(), identifier.getIdentifierType().getId());
        
        // When
        identifier.setIdentifierType(nipcType);
        Identifier updated = identifierRepository.save(identifier);
        
        // Then
        assertEquals(nipcType.getId(), updated.getIdentifierType().getId());
        
        // Verify persistence
        Identifier found = identifierRepository.findById(identifier1.getId()).orElseThrow();
        assertEquals(nipcType.getId(), found.getIdentifierType().getId());
    }

    @Test
    @DisplayName("Should delete identifier by ID")
    void shouldDeleteIdentifierById() {
        // Given
        Long identifierId = identifier1.getId();
        assertTrue(identifierRepository.existsById(identifierId));
        
        // When
        identifierRepository.deleteById(identifierId);
        
        // Then
        assertFalse(identifierRepository.existsById(identifierId));
        assertTrue(identifierRepository.findById(identifierId).isEmpty());
    }

    @Test
    @DisplayName("Should delete identifier entity")
    void shouldDeleteIdentifierEntity() {
        // Given
        assertTrue(identifierRepository.existsById(identifier1.getId()));
        
        // When
        identifierRepository.delete(identifier1);
        
        // Then
        assertFalse(identifierRepository.existsById(identifier1.getId()));
    }

    @Test
    @DisplayName("Should count identifiers correctly")
    void shouldCountIdentifiersCorrectly() {
        // When
        long count = identifierRepository.count();
        
        // Then
        assertEquals(3, count);
    }

    @Test
    @DisplayName("Should check existence by ID")
    void shouldCheckExistenceById() {
        // When & Then
        assertTrue(identifierRepository.existsById(identifier1.getId()));
        assertTrue(identifierRepository.existsById(identifier2.getId()));
        assertTrue(identifierRepository.existsById(identifier3.getId()));
        assertFalse(identifierRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should support complex queries combining filters")
    void shouldSupportComplexQueriesCombiningFilters() {
        // Given - Add an identifier with same user ID and identifier type as existing one
        Identifier identifier4 = TestDataFactory.createValidIdentifier(1L, nifType, eliminatedState);
        identifier4 = entityManager.persistAndFlush(identifier4);
        
        // When
        List<Identifier> user1Identifiers = identifierRepository.findByUserId(1L);
        List<Identifier> nifIdentifiers = identifierRepository.findByIdentifierTypeId(nifType.getId());
        List<Identifier> eliminatedIdentifiers = identifierRepository.findByStateId(eliminatedState.getId());
        
        // Then
        assertEquals(3, user1Identifiers.size()); // identifier1, identifier3, identifier4
        assertEquals(2, nifIdentifiers.size()); // identifier1, identifier4
        assertEquals(1, eliminatedIdentifiers.size()); // identifier4
        
        // Verify the new identifier appears in relevant queries
        assertTrue(user1Identifiers.stream().anyMatch(i -> i.getId().equals(identifier4.getId())));
        assertTrue(nifIdentifiers.stream().anyMatch(i -> i.getId().equals(identifier4.getId())));
        assertTrue(eliminatedIdentifiers.stream().anyMatch(i -> i.getId().equals(identifier4.getId())));
    }

    @Test
    @DisplayName("Should handle multiple identifiers for same user with different states")
    void shouldHandleMultipleIdentifiersForSameUserWithDifferentStates() {
        // Given - Add more identifiers for user 1
        Identifier activeIdentifier = TestDataFactory.createValidIdentifier(1L, nifType, activeState);
        activeIdentifier = entityManager.persistAndFlush(activeIdentifier);
        
        Identifier eliminatedIdentifier = TestDataFactory.createValidIdentifier(1L, nipcType, eliminatedState);
        eliminatedIdentifier = entityManager.persistAndFlush(eliminatedIdentifier);
        
        // When
        List<Identifier> user1Identifiers = identifierRepository.findByUserId(1L);
        List<Identifier> activeIdentifiers = identifierRepository.findByStateId(activeState.getId());
        List<Identifier> inactiveIdentifiers = identifierRepository.findByStateId(inactiveState.getId());
        List<Identifier> eliminatedIdentifiers = identifierRepository.findByStateId(eliminatedState.getId());
        
        // Then
        assertEquals(4, user1Identifiers.size()); // All identifiers for user 1
        assertEquals(3, activeIdentifiers.size()); // identifier1, identifier2, activeIdentifier
        assertEquals(1, inactiveIdentifiers.size()); // identifier3
        assertEquals(1, eliminatedIdentifiers.size()); // eliminatedIdentifier
    }

    @Test
    @DisplayName("Should maintain referential integrity with identifier types")
    void shouldMaintainReferentialIntegrityWithIdentifierTypes() {
        // Given
        Identifier identifier = identifierRepository.findById(identifier1.getId()).orElseThrow();
        
        // When - accessing the identifier type
        IdentifierType type = identifier.getIdentifierType();
        
        // Then
        assertNotNull(type);
        assertEquals(nifType.getId(), type.getId());
        assertEquals("NIF", type.getType());
        assertEquals("Número de Identificação Fiscal", type.getDescription());
    }

    @Test
    @DisplayName("Should maintain referential integrity with states")
    void shouldMaintainReferentialIntegrityWithStates() {
        // Given
        Identifier identifier = identifierRepository.findById(identifier1.getId()).orElseThrow();
        
        // When - accessing the state
        State state = identifier.getState();
        
        // Then
        assertNotNull(state);
        assertEquals(activeState.getId(), state.getId());
        assertEquals("ACTIVE", state.getState());
    }

    @Test
    @DisplayName("Should handle batch operations efficiently")
    void shouldHandleBatchOperationsEfficiently() {
        // Given
        Identifier newId1 = TestDataFactory.createValidIdentifier(10L, nifType, activeState);
        Identifier newId2 = TestDataFactory.createValidIdentifier(11L, nipcType, inactiveState);
        Identifier newId3 = TestDataFactory.createValidIdentifier(12L, nifType, eliminatedState);
        
        List<Identifier> identifiersToSave = List.of(newId1, newId2, newId3);
        
        // When
        List<Identifier> savedIdentifiers = identifierRepository.saveAll(identifiersToSave);
        
        // Then
        assertEquals(3, savedIdentifiers.size());
        assertEquals(6, identifierRepository.count()); // 3 original + 3 new
        
        // Verify all were saved with proper relationships
        for (Identifier saved : savedIdentifiers) {
            assertNotNull(saved.getId());
            assertNotNull(saved.getIdentifierType());
            assertNotNull(saved.getState());
        }
    }
}