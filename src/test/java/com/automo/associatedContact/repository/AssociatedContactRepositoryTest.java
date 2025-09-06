package com.automo.associatedContact.repository;

import com.automo.associatedContact.entity.AssociatedContact;
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
@DisplayName("Tests for AssociatedContactRepository")
class AssociatedContactRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AssociatedContactRepository associatedContactRepository;

    private Identifier testIdentifier;
    private IdentifierType identifierType;
    private State activeState;
    private State inactiveState;
    private AssociatedContact testAssociatedContact;

    @BeforeEach
    void setUp() {
        // Create and persist IdentifierType
        identifierType = TestDataFactory.createNifIdentifierType();
        identifierType = entityManager.persistAndFlush(identifierType);

        // Create and persist States
        activeState = TestDataFactory.createActiveState();
        activeState = entityManager.persistAndFlush(activeState);

        inactiveState = TestDataFactory.createInactiveState();
        inactiveState = entityManager.persistAndFlush(inactiveState);

        // Create and persist Identifier
        testIdentifier = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        testIdentifier = entityManager.persistAndFlush(testIdentifier);

        // Create and persist AssociatedContact
        testAssociatedContact = TestDataFactory.createValidAssociatedContact(testIdentifier, activeState);
        testAssociatedContact = entityManager.persistAndFlush(testAssociatedContact);
    }

    @Test
    @DisplayName("Should save and retrieve associated contact")
    void shouldSaveAndRetrieveAssociatedContact() {
        // Given
        AssociatedContact contact = TestDataFactory.createValidAssociatedContact(testIdentifier, "913456789", activeState);

        // When
        AssociatedContact saved = associatedContactRepository.save(contact);
        Optional<AssociatedContact> found = associatedContactRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("913456789", found.get().getContact());
        assertEquals(testIdentifier.getId(), found.get().getIdentifier().getId());
        assertEquals(activeState.getId(), found.get().getState().getId());
        assertNotNull(found.get().getCreatedAt());
        assertNotNull(found.get().getUpdatedAt());
    }

    @Test
    @DisplayName("Should find all contacts with identifier and state eagerly loaded")
    void shouldFindAllContactsWithIdentifierAndStateEagerlyLoaded() {
        // Given - testAssociatedContact already created in setUp
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(testIdentifier, "914567890", activeState);
        entityManager.persistAndFlush(contact2);

        // When
        List<AssociatedContact> contacts = associatedContactRepository.findAllWithIdentifierAndState();

        // Then
        assertNotNull(contacts);
        assertTrue(contacts.size() >= 2);
        
        // Verify eager loading - no additional queries should be needed
        for (AssociatedContact contact : contacts) {
            assertNotNull(contact.getIdentifier());
            assertNotNull(contact.getState());
            assertNotNull(contact.getIdentifier().getIdentifierType());
        }
    }

    @Test
    @DisplayName("Should find contact by id with identifier and state eagerly loaded")
    void shouldFindContactByIdWithIdentifierAndStateEagerlyLoaded() {
        // When
        Optional<AssociatedContact> found = associatedContactRepository.findByIdWithIdentifierAndState(testAssociatedContact.getId());

        // Then
        assertTrue(found.isPresent());
        AssociatedContact contact = found.get();
        assertNotNull(contact.getIdentifier());
        assertNotNull(contact.getState());
        assertEquals(testIdentifier.getId(), contact.getIdentifier().getId());
        assertEquals(activeState.getId(), contact.getState().getId());
    }

    @Test
    @DisplayName("Should return empty when finding non-existent contact by id with eager loading")
    void shouldReturnEmptyWhenFindingNonExistentContactByIdWithEagerLoading() {
        // When
        Optional<AssociatedContact> found = associatedContactRepository.findByIdWithIdentifierAndState(999L);

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find contacts by identifier id with identifier and state eagerly loaded")
    void shouldFindContactsByIdentifierIdWithIdentifierAndStateEagerlyLoaded() {
        // Given
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(testIdentifier, "914567890", activeState);
        entityManager.persistAndFlush(contact2);

        // When
        List<AssociatedContact> contacts = associatedContactRepository.findByIdentifierIdWithIdentifierAndState(testIdentifier.getId());

        // Then
        assertNotNull(contacts);
        assertEquals(2, contacts.size());
        
        for (AssociatedContact contact : contacts) {
            assertEquals(testIdentifier.getId(), contact.getIdentifier().getId());
            assertNotNull(contact.getState());
        }
    }

    @Test
    @DisplayName("Should find contacts by state id with identifier and state eagerly loaded")
    void shouldFindContactsByStateIdWithIdentifierAndStateEagerlyLoaded() {
        // Given
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(testIdentifier, "914567890", activeState);
        entityManager.persistAndFlush(contact2);
        
        AssociatedContact inactiveContact = TestDataFactory.createValidAssociatedContact(testIdentifier, "915678901", inactiveState);
        entityManager.persistAndFlush(inactiveContact);

        // When
        List<AssociatedContact> activeContacts = associatedContactRepository.findByStateIdWithIdentifierAndState(activeState.getId());
        List<AssociatedContact> inactiveContacts = associatedContactRepository.findByStateIdWithIdentifierAndState(inactiveState.getId());

        // Then
        assertNotNull(activeContacts);
        assertNotNull(inactiveContacts);
        assertEquals(2, activeContacts.size());
        assertEquals(1, inactiveContacts.size());
        
        for (AssociatedContact contact : activeContacts) {
            assertEquals(activeState.getId(), contact.getState().getId());
            assertNotNull(contact.getIdentifier());
        }
        
        for (AssociatedContact contact : inactiveContacts) {
            assertEquals(inactiveState.getId(), contact.getState().getId());
            assertNotNull(contact.getIdentifier());
        }
    }

    @Test
    @DisplayName("Should find contacts by identifier id using backward compatibility method")
    void shouldFindContactsByIdentifierIdUsingBackwardCompatibilityMethod() {
        // Given
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(testIdentifier, "914567890", activeState);
        entityManager.persistAndFlush(contact2);

        // When
        List<AssociatedContact> contacts = associatedContactRepository.findByIdentifierId(testIdentifier.getId());

        // Then
        assertNotNull(contacts);
        assertEquals(2, contacts.size());
        
        for (AssociatedContact contact : contacts) {
            assertEquals(testIdentifier.getId(), contact.getIdentifier().getId());
        }
    }

    @Test
    @DisplayName("Should find contacts by state id using backward compatibility method")
    void shouldFindContactsByStateIdUsingBackwardCompatibilityMethod() {
        // Given
        AssociatedContact contact2 = TestDataFactory.createValidAssociatedContact(testIdentifier, "914567890", activeState);
        entityManager.persistAndFlush(contact2);

        // When
        List<AssociatedContact> contacts = associatedContactRepository.findByStateId(activeState.getId());

        // Then
        assertNotNull(contacts);
        assertEquals(2, contacts.size());
        
        for (AssociatedContact contact : contacts) {
            assertEquals(activeState.getId(), contact.getState().getId());
        }
    }

    @Test
    @DisplayName("Should handle multiple contacts for same identifier")
    void shouldHandleMultipleContactsForSameIdentifier() {
        // Given
        String[] phoneNumbers = {"913456789", "914567890", "915678901"};
        
        for (String phone : phoneNumbers) {
            AssociatedContact contact = TestDataFactory.createValidAssociatedContact(testIdentifier, phone, activeState);
            entityManager.persistAndFlush(contact);
        }

        // When
        List<AssociatedContact> contacts = associatedContactRepository.findByIdentifierId(testIdentifier.getId());

        // Then
        assertNotNull(contacts);
        assertEquals(4, contacts.size()); // 3 new + 1 from setUp
        
        // Verify all contacts belong to the same identifier
        for (AssociatedContact contact : contacts) {
            assertEquals(testIdentifier.getId(), contact.getIdentifier().getId());
        }
    }

    @Test
    @DisplayName("Should preserve contact information across persistence operations")
    void shouldPreserveContactInformationAcrossPersistenceOperations() {
        // Given
        String originalContact = "912123456";
        AssociatedContact contact = TestDataFactory.createValidAssociatedContact(testIdentifier, originalContact, activeState);

        // When
        AssociatedContact saved = entityManager.persistAndFlush(contact);
        entityManager.clear(); // Clear persistence context
        Optional<AssociatedContact> retrieved = associatedContactRepository.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(originalContact, retrieved.get().getContact());
        assertEquals(testIdentifier.getId(), retrieved.get().getIdentifier().getId());
        assertEquals(activeState.getId(), retrieved.get().getState().getId());
    }

    @Test
    @DisplayName("Should handle contact updates")
    void shouldHandleContactUpdates() {
        // Given
        String originalContact = "912123456";
        String updatedContact = "913456789";
        
        AssociatedContact contact = TestDataFactory.createValidAssociatedContact(testIdentifier, originalContact, activeState);
        AssociatedContact saved = entityManager.persistAndFlush(contact);

        // When
        saved.setContact(updatedContact);
        AssociatedContact updated = associatedContactRepository.save(saved);
        entityManager.flush();

        // Then
        Optional<AssociatedContact> retrieved = associatedContactRepository.findById(updated.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(updatedContact, retrieved.get().getContact());
        assertNotEquals(originalContact, retrieved.get().getContact());
    }

    @Test
    @DisplayName("Should handle state transitions")
    void shouldHandleStateTransitions() {
        // Given
        AssociatedContact contact = TestDataFactory.createValidAssociatedContact(testIdentifier, "912123456", activeState);
        AssociatedContact saved = entityManager.persistAndFlush(contact);

        // When - Change state from active to inactive
        saved.setState(inactiveState);
        AssociatedContact updated = associatedContactRepository.save(saved);
        entityManager.flush();

        // Then
        Optional<AssociatedContact> retrieved = associatedContactRepository.findById(updated.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(inactiveState.getId(), retrieved.get().getState().getId());
    }

    @Test
    @DisplayName("Should return empty list when no contacts found for identifier")
    void shouldReturnEmptyListWhenNoContactsFoundForIdentifier() {
        // When
        List<AssociatedContact> contacts = associatedContactRepository.findByIdentifierId(999L);

        // Then
        assertNotNull(contacts);
        assertTrue(contacts.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no contacts found for state")
    void shouldReturnEmptyListWhenNoContactsFoundForState() {
        // When
        List<AssociatedContact> contacts = associatedContactRepository.findByStateId(999L);

        // Then
        assertNotNull(contacts);
        assertTrue(contacts.isEmpty());
    }

    @Test
    @DisplayName("Should handle deletion of contacts")
    void shouldHandleDeletionOfContacts() {
        // Given
        AssociatedContact contact = TestDataFactory.createValidAssociatedContact(testIdentifier, "912123456", activeState);
        AssociatedContact saved = entityManager.persistAndFlush(contact);
        Long contactId = saved.getId();

        // When
        associatedContactRepository.deleteById(contactId);
        entityManager.flush();

        // Then
        Optional<AssociatedContact> deleted = associatedContactRepository.findById(contactId);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should validate contact field constraints")
    void shouldValidateContactFieldConstraints() {
        // Given
        AssociatedContact contact = new AssociatedContact();
        contact.setIdentifier(testIdentifier);
        contact.setContact("Valid contact");
        contact.setState(activeState);

        // When & Then
        assertDoesNotThrow(() -> {
            AssociatedContact saved = associatedContactRepository.save(contact);
            entityManager.flush();
            assertNotNull(saved.getId());
        });
    }
}