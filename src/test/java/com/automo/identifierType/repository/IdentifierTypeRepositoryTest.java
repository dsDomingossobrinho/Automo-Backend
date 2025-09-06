package com.automo.identifierType.repository;

import com.automo.identifierType.entity.IdentifierType;
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
@DisplayName("Tests for IdentifierTypeRepository")
class IdentifierTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IdentifierTypeRepository identifierTypeRepository;

    private IdentifierType nifType;
    private IdentifierType nipcType;
    private IdentifierType passportType;

    @BeforeEach
    void setUp() {
        // Setup identifier types
        nifType = TestDataFactory.createNifIdentifierType();
        nifType = entityManager.persistAndFlush(nifType);

        nipcType = TestDataFactory.createValidIdentifierType("NIPC", "Número de Identificação de Pessoa Coletiva");
        nipcType = entityManager.persistAndFlush(nipcType);

        passportType = TestDataFactory.createValidIdentifierType("PASSPORT", "Passport Number");
        passportType = entityManager.persistAndFlush(passportType);

        entityManager.clear(); // Clear the persistence context to ensure fresh queries
    }

    @Test
    @DisplayName("Should save and find identifier type by ID")
    void shouldSaveAndFindIdentifierTypeById() {
        // Given
        IdentifierType newType = TestDataFactory.createValidIdentifierType("CC", "Cartão de Cidadão");
        
        // When
        IdentifierType saved = identifierTypeRepository.save(newType);
        Optional<IdentifierType> found = identifierTypeRepository.findById(saved.getId());
        
        // Then
        assertTrue(found.isPresent());
        assertEquals("CC", found.get().getType());
        assertEquals("Cartão de Cidadão", found.get().getDescription());
    }

    @Test
    @DisplayName("Should find all identifier types")
    void shouldFindAllIdentifierTypes() {
        // When
        List<IdentifierType> identifierTypes = identifierTypeRepository.findAll();
        
        // Then
        assertEquals(3, identifierTypes.size());
        
        // Verify all types are present
        assertTrue(identifierTypes.stream().anyMatch(type -> "NIF".equals(type.getType())));
        assertTrue(identifierTypes.stream().anyMatch(type -> "NIPC".equals(type.getType())));
        assertTrue(identifierTypes.stream().anyMatch(type -> "PASSPORT".equals(type.getType())));
    }

    @Test
    @DisplayName("Should find identifier type by type string")
    void shouldFindIdentifierTypeByTypeString() {
        // When
        Optional<IdentifierType> nifResult = identifierTypeRepository.findByType("NIF");
        Optional<IdentifierType> nipcResult = identifierTypeRepository.findByType("NIPC");
        Optional<IdentifierType> passportResult = identifierTypeRepository.findByType("PASSPORT");
        Optional<IdentifierType> nonExistentResult = identifierTypeRepository.findByType("UNKNOWN");
        
        // Then
        assertTrue(nifResult.isPresent());
        assertEquals("NIF", nifResult.get().getType());
        assertEquals("Número de Identificação Fiscal", nifResult.get().getDescription());
        
        assertTrue(nipcResult.isPresent());
        assertEquals("NIPC", nipcResult.get().getType());
        assertEquals("Número de Identificação de Pessoa Coletiva", nipcResult.get().getDescription());
        
        assertTrue(passportResult.isPresent());
        assertEquals("PASSPORT", passportResult.get().getType());
        assertEquals("Passport Number", passportResult.get().getDescription());
        
        assertFalse(nonExistentResult.isPresent());
    }

    @Test
    @DisplayName("Should check existence by type")
    void shouldCheckExistenceByType() {
        // When & Then
        assertTrue(identifierTypeRepository.existsByType("NIF"));
        assertTrue(identifierTypeRepository.existsByType("NIPC"));
        assertTrue(identifierTypeRepository.existsByType("PASSPORT"));
        assertFalse(identifierTypeRepository.existsByType("UNKNOWN"));
        assertFalse(identifierTypeRepository.existsByType(""));
        assertFalse(identifierTypeRepository.existsByType(null));
    }

    @Test
    @DisplayName("Should handle case-sensitive type searches")
    void shouldHandleCaseSensitiveTypeSearches() {
        // When
        Optional<IdentifierType> upperCase = identifierTypeRepository.findByType("NIF");
        Optional<IdentifierType> lowerCase = identifierTypeRepository.findByType("nif");
        Optional<IdentifierType> mixedCase = identifierTypeRepository.findByType("Nif");
        
        boolean upperCaseExists = identifierTypeRepository.existsByType("NIF");
        boolean lowerCaseExists = identifierTypeRepository.existsByType("nif");
        boolean mixedCaseExists = identifierTypeRepository.existsByType("Nif");
        
        // Then
        assertTrue(upperCase.isPresent());
        assertFalse(lowerCase.isPresent());
        assertFalse(mixedCase.isPresent());
        
        assertTrue(upperCaseExists);
        assertFalse(lowerCaseExists);
        assertFalse(mixedCaseExists);
    }

    @Test
    @DisplayName("Should update identifier type")
    void shouldUpdateIdentifierType() {
        // Given
        IdentifierType identifierType = identifierTypeRepository.findById(nifType.getId()).orElseThrow();
        assertEquals("NIF", identifierType.getType());
        assertEquals("Número de Identificação Fiscal", identifierType.getDescription());
        
        // When
        identifierType.setType("NIF_UPDATED");
        identifierType.setDescription("Updated Description");
        IdentifierType updated = identifierTypeRepository.save(identifierType);
        
        // Then
        assertEquals("NIF_UPDATED", updated.getType());
        assertEquals("Updated Description", updated.getDescription());
        
        // Verify persistence
        IdentifierType found = identifierTypeRepository.findById(nifType.getId()).orElseThrow();
        assertEquals("NIF_UPDATED", found.getType());
        assertEquals("Updated Description", found.getDescription());
    }

    @Test
    @DisplayName("Should delete identifier type by ID")
    void shouldDeleteIdentifierTypeById() {
        // Given
        Long identifierTypeId = nifType.getId();
        assertTrue(identifierTypeRepository.existsById(identifierTypeId));
        
        // When
        identifierTypeRepository.deleteById(identifierTypeId);
        
        // Then
        assertFalse(identifierTypeRepository.existsById(identifierTypeId));
        assertTrue(identifierTypeRepository.findById(identifierTypeId).isEmpty());
        
        // Verify other types are still present
        assertEquals(2, identifierTypeRepository.count());
        assertTrue(identifierTypeRepository.existsByType("NIPC"));
        assertTrue(identifierTypeRepository.existsByType("PASSPORT"));
    }

    @Test
    @DisplayName("Should delete identifier type entity")
    void shouldDeleteIdentifierTypeEntity() {
        // Given
        assertTrue(identifierTypeRepository.existsById(nipcType.getId()));
        
        // When
        identifierTypeRepository.delete(nipcType);
        
        // Then
        assertFalse(identifierTypeRepository.existsById(nipcType.getId()));
        assertEquals(2, identifierTypeRepository.count());
    }

    @Test
    @DisplayName("Should count identifier types correctly")
    void shouldCountIdentifierTypesCorrectly() {
        // When
        long count = identifierTypeRepository.count();
        
        // Then
        assertEquals(3, count);
        
        // Add one more and verify count increases
        IdentifierType newType = TestDataFactory.createValidIdentifierType("DRIVER_LICENSE", "Driver License Number");
        identifierTypeRepository.save(newType);
        
        assertEquals(4, identifierTypeRepository.count());
    }

    @Test
    @DisplayName("Should check existence by ID")
    void shouldCheckExistenceById() {
        // When & Then
        assertTrue(identifierTypeRepository.existsById(nifType.getId()));
        assertTrue(identifierTypeRepository.existsById(nipcType.getId()));
        assertTrue(identifierTypeRepository.existsById(passportType.getId()));
        assertFalse(identifierTypeRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should handle null and empty values in type search")
    void shouldHandleNullAndEmptyValuesInTypeSearch() {
        // When
        Optional<IdentifierType> nullResult = identifierTypeRepository.findByType(null);
        Optional<IdentifierType> emptyResult = identifierTypeRepository.findByType("");
        Optional<IdentifierType> whitespaceResult = identifierTypeRepository.findByType("   ");
        
        boolean nullExists = identifierTypeRepository.existsByType(null);
        boolean emptyExists = identifierTypeRepository.existsByType("");
        boolean whitespaceExists = identifierTypeRepository.existsByType("   ");
        
        // Then
        assertFalse(nullResult.isPresent());
        assertFalse(emptyResult.isPresent());
        assertFalse(whitespaceResult.isPresent());
        
        assertFalse(nullExists);
        assertFalse(emptyExists);
        assertFalse(whitespaceExists);
    }

    @Test
    @DisplayName("Should handle special characters in type names")
    void shouldHandleSpecialCharactersInTypeNames() {
        // Given
        IdentifierType specialType = TestDataFactory.createValidIdentifierType(
                "NIF-PT_2024", 
                "Número de Identificação Fiscal - Portugal (2024)"
        );
        
        // When
        IdentifierType saved = identifierTypeRepository.save(specialType);
        Optional<IdentifierType> found = identifierTypeRepository.findByType("NIF-PT_2024");
        boolean exists = identifierTypeRepository.existsByType("NIF-PT_2024");
        
        // Then
        assertNotNull(saved.getId());
        assertTrue(found.isPresent());
        assertTrue(exists);
        assertEquals("NIF-PT_2024", found.get().getType());
        assertEquals("Número de Identificação Fiscal - Portugal (2024)", found.get().getDescription());
    }

    @Test
    @DisplayName("Should handle long type names and descriptions")
    void shouldHandleLongTypeNamesAndDescriptions() {
        // Given
        String longType = "VERY_LONG_IDENTIFIER_TYPE_NAME_FOR_TESTING_PURPOSES";
        String longDescription = "This is a very long description that tests the database's ability " +
                                "to handle extended text fields for identifier types. It includes " +
                                "special characters like áéíóú and symbols like @#$%^&*()_+-={}[]|\\:;\"'<>?,./" +
                                "to ensure proper encoding and storage.";
        
        IdentifierType longType1 = TestDataFactory.createValidIdentifierType(longType, longDescription);
        
        // When
        IdentifierType saved = identifierTypeRepository.save(longType1);
        Optional<IdentifierType> found = identifierTypeRepository.findByType(longType);
        
        // Then
        assertNotNull(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(longType, found.get().getType());
        assertEquals(longDescription, found.get().getDescription());
    }

    @Test
    @DisplayName("Should maintain referential integrity")
    void shouldMaintainReferentialIntegrity() {
        // Given
        IdentifierType identifierType = identifierTypeRepository.findById(nifType.getId()).orElseThrow();
        
        // When - updating and re-fetching
        identifierType.setDescription("Updated NIF Description");
        identifierTypeRepository.save(identifierType);
        
        IdentifierType refetched = identifierTypeRepository.findById(nifType.getId()).orElseThrow();
        
        // Then
        assertEquals("NIF", refetched.getType());
        assertEquals("Updated NIF Description", refetched.getDescription());
        assertEquals(identifierType.getId(), refetched.getId());
    }

    @Test
    @DisplayName("Should handle batch operations efficiently")
    void shouldHandleBatchOperationsEfficiently() {
        // Given
        IdentifierType type1 = TestDataFactory.createValidIdentifierType("TYPE1", "Description 1");
        IdentifierType type2 = TestDataFactory.createValidIdentifierType("TYPE2", "Description 2");
        IdentifierType type3 = TestDataFactory.createValidIdentifierType("TYPE3", "Description 3");
        
        List<IdentifierType> typesToSave = List.of(type1, type2, type3);
        
        // When
        List<IdentifierType> savedTypes = identifierTypeRepository.saveAll(typesToSave);
        
        // Then
        assertEquals(3, savedTypes.size());
        assertEquals(6, identifierTypeRepository.count()); // 3 original + 3 new
        
        // Verify all were saved with proper values
        for (IdentifierType saved : savedTypes) {
            assertNotNull(saved.getId());
            assertTrue(saved.getType().startsWith("TYPE"));
            assertTrue(saved.getDescription().startsWith("Description"));
        }
        
        // Verify they can be found individually
        assertTrue(identifierTypeRepository.existsByType("TYPE1"));
        assertTrue(identifierTypeRepository.existsByType("TYPE2"));
        assertTrue(identifierTypeRepository.existsByType("TYPE3"));
    }

    @Test
    @DisplayName("Should handle concurrent access scenarios")
    void shouldHandleConcurrentAccessScenarios() {
        // Given
        IdentifierType originalType = identifierTypeRepository.findById(nifType.getId()).orElseThrow();
        
        // When - simulate concurrent modifications
        originalType.setDescription("Concurrent Update 1");
        IdentifierType updated1 = identifierTypeRepository.save(originalType);
        
        // Simulate another concurrent update
        IdentifierType sameType = identifierTypeRepository.findById(nifType.getId()).orElseThrow();
        sameType.setDescription("Concurrent Update 2");
        IdentifierType updated2 = identifierTypeRepository.save(sameType);
        
        // Then
        assertEquals("Concurrent Update 2", updated2.getDescription());
        
        // Verify final state
        IdentifierType finalType = identifierTypeRepository.findById(nifType.getId()).orElseThrow();
        assertEquals("Concurrent Update 2", finalType.getDescription());
    }

    @Test
    @DisplayName("Should handle delete operations with non-existent entities")
    void shouldHandleDeleteOperationsWithNonExistentEntities() {
        // Given
        Long nonExistentId = 99999L;
        
        // When & Then - should not throw exceptions
        assertDoesNotThrow(() -> {
            identifierTypeRepository.deleteById(nonExistentId);
        });
        
        assertDoesNotThrow(() -> {
            IdentifierType nonExistentType = new IdentifierType();
            nonExistentType.setId(nonExistentId);
            nonExistentType.setType("NON_EXISTENT");
            identifierTypeRepository.delete(nonExistentType);
        });
        
        // Verify original data is intact
        assertEquals(3, identifierTypeRepository.count());
    }

    @Test
    @DisplayName("Should support query methods with exact matching")
    void shouldSupportQueryMethodsWithExactMatching() {
        // Given
        IdentifierType similarType1 = TestDataFactory.createValidIdentifierType("TEST", "Test Description");
        IdentifierType similarType2 = TestDataFactory.createValidIdentifierType("TEST_SIMILAR", "Test Similar Description");
        
        identifierTypeRepository.save(similarType1);
        identifierTypeRepository.save(similarType2);
        
        // When
        Optional<IdentifierType> exactMatch = identifierTypeRepository.findByType("TEST");
        Optional<IdentifierType> similarMatch = identifierTypeRepository.findByType("TEST_SIMILAR");
        Optional<IdentifierType> partialMatch = identifierTypeRepository.findByType("TES");
        
        // Then
        assertTrue(exactMatch.isPresent());
        assertEquals("TEST", exactMatch.get().getType());
        
        assertTrue(similarMatch.isPresent());
        assertEquals("TEST_SIMILAR", similarMatch.get().getType());
        
        assertFalse(partialMatch.isPresent());
    }

    @Test
    @DisplayName("Should maintain data consistency after multiple operations")
    void shouldMaintainDataConsistencyAfterMultipleOperations() {
        // Given - initial count
        long initialCount = identifierTypeRepository.count();
        
        // When - perform multiple operations
        IdentifierType newType = TestDataFactory.createValidIdentifierType("TEMP", "Temporary");
        IdentifierType saved = identifierTypeRepository.save(newType);
        
        assertEquals(initialCount + 1, identifierTypeRepository.count());
        assertTrue(identifierTypeRepository.existsById(saved.getId()));
        
        // Update the saved type
        saved.setType("TEMP_UPDATED");
        identifierTypeRepository.save(saved);
        
        assertEquals(initialCount + 1, identifierTypeRepository.count());
        assertTrue(identifierTypeRepository.existsByType("TEMP_UPDATED"));
        assertFalse(identifierTypeRepository.existsByType("TEMP"));
        
        // Delete the type
        identifierTypeRepository.delete(saved);
        
        // Then
        assertEquals(initialCount, identifierTypeRepository.count());
        assertFalse(identifierTypeRepository.existsById(saved.getId()));
        assertFalse(identifierTypeRepository.existsByType("TEMP_UPDATED"));
    }
}