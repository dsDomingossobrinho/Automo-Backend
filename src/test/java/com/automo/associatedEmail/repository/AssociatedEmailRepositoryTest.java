package com.automo.associatedEmail.repository;

import com.automo.associatedEmail.entity.AssociatedEmail;
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
@DisplayName("Tests for AssociatedEmailRepository")
class AssociatedEmailRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AssociatedEmailRepository associatedEmailRepository;

    private Identifier testIdentifier;
    private IdentifierType identifierType;
    private State activeState;
    private State inactiveState;
    private AssociatedEmail testAssociatedEmail;

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

        // Create and persist AssociatedEmail
        testAssociatedEmail = TestDataFactory.createValidAssociatedEmail(testIdentifier, activeState);
        testAssociatedEmail = entityManager.persistAndFlush(testAssociatedEmail);
    }

    @Test
    @DisplayName("Should save and retrieve associated email")
    void shouldSaveAndRetrieveAssociatedEmail() {
        // Given
        AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, "new@automo.com", activeState);

        // When
        AssociatedEmail saved = associatedEmailRepository.save(email);
        Optional<AssociatedEmail> found = associatedEmailRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("new@automo.com", found.get().getEmail());
        assertEquals(testIdentifier.getId(), found.get().getIdentifier().getId());
        assertEquals(activeState.getId(), found.get().getState().getId());
        assertNotNull(found.get().getCreatedAt());
        assertNotNull(found.get().getUpdatedAt());
    }

    @Test
    @DisplayName("Should find emails by identifier id")
    void shouldFindEmailsByIdentifierId() {
        // Given
        AssociatedEmail email2 = TestDataFactory.createValidAssociatedEmail(testIdentifier, "second@automo.com", activeState);
        entityManager.persistAndFlush(email2);

        // When
        List<AssociatedEmail> emails = associatedEmailRepository.findByIdentifierId(testIdentifier.getId());

        // Then
        assertNotNull(emails);
        assertEquals(2, emails.size());
        
        for (AssociatedEmail email : emails) {
            assertEquals(testIdentifier.getId(), email.getIdentifier().getId());
        }
    }

    @Test
    @DisplayName("Should find emails by state id")
    void shouldFindEmailsByStateId() {
        // Given
        AssociatedEmail email2 = TestDataFactory.createValidAssociatedEmail(testIdentifier, "second@automo.com", activeState);
        entityManager.persistAndFlush(email2);
        
        AssociatedEmail inactiveEmail = TestDataFactory.createValidAssociatedEmail(testIdentifier, "inactive@automo.com", inactiveState);
        entityManager.persistAndFlush(inactiveEmail);

        // When
        List<AssociatedEmail> activeEmails = associatedEmailRepository.findByStateId(activeState.getId());
        List<AssociatedEmail> inactiveEmails = associatedEmailRepository.findByStateId(inactiveState.getId());

        // Then
        assertNotNull(activeEmails);
        assertNotNull(inactiveEmails);
        assertEquals(2, activeEmails.size());
        assertEquals(1, inactiveEmails.size());
        
        for (AssociatedEmail email : activeEmails) {
            assertEquals(activeState.getId(), email.getState().getId());
        }
        
        for (AssociatedEmail email : inactiveEmails) {
            assertEquals(inactiveState.getId(), email.getState().getId());
        }
    }

    @Test
    @DisplayName("Should find email by email address")
    void shouldFindEmailByEmailAddress() {
        // Given
        String emailAddress = "unique@automo.com";
        AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, emailAddress, activeState);
        entityManager.persistAndFlush(email);

        // When
        Optional<AssociatedEmail> found = associatedEmailRepository.findByEmail(emailAddress);

        // Then
        assertTrue(found.isPresent());
        assertEquals(emailAddress, found.get().getEmail());
        assertEquals(testIdentifier.getId(), found.get().getIdentifier().getId());
    }

    @Test
    @DisplayName("Should return empty when email address not found")
    void shouldReturnEmptyWhenEmailAddressNotFound() {
        // When
        Optional<AssociatedEmail> found = associatedEmailRepository.findByEmail("nonexistent@automo.com");

        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should handle case sensitivity in email search")
    void shouldHandleCaseSensitivityInEmailSearch() {
        // Given
        String emailAddress = "CaseSensitive@Automo.COM";
        AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, emailAddress, activeState);
        entityManager.persistAndFlush(email);

        // When
        Optional<AssociatedEmail> foundExact = associatedEmailRepository.findByEmail(emailAddress);
        Optional<AssociatedEmail> foundLowerCase = associatedEmailRepository.findByEmail(emailAddress.toLowerCase());

        // Then
        assertTrue(foundExact.isPresent());
        // Email search should be case-sensitive by default
        assertFalse(foundLowerCase.isPresent());
    }

    @Test
    @DisplayName("Should handle multiple emails for same identifier")
    void shouldHandleMultipleEmailsForSameIdentifier() {
        // Given
        String[] emailAddresses = {"email1@automo.com", "email2@automo.com", "email3@automo.com"};
        
        for (String emailAddr : emailAddresses) {
            AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, emailAddr, activeState);
            entityManager.persistAndFlush(email);
        }

        // When
        List<AssociatedEmail> emails = associatedEmailRepository.findByIdentifierId(testIdentifier.getId());

        // Then
        assertNotNull(emails);
        assertEquals(4, emails.size()); // 3 new + 1 from setUp
        
        // Verify all emails belong to the same identifier
        for (AssociatedEmail email : emails) {
            assertEquals(testIdentifier.getId(), email.getIdentifier().getId());
        }
    }

    @Test
    @DisplayName("Should preserve email information across persistence operations")
    void shouldPreserveEmailInformationAcrossPersistenceOperations() {
        // Given
        String originalEmail = "preserve@automo.com";
        AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, originalEmail, activeState);

        // When
        AssociatedEmail saved = entityManager.persistAndFlush(email);
        entityManager.clear(); // Clear persistence context
        Optional<AssociatedEmail> retrieved = associatedEmailRepository.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertEquals(originalEmail, retrieved.get().getEmail());
        assertEquals(testIdentifier.getId(), retrieved.get().getIdentifier().getId());
        assertEquals(activeState.getId(), retrieved.get().getState().getId());
    }

    @Test
    @DisplayName("Should handle email updates")
    void shouldHandleEmailUpdates() {
        // Given
        String originalEmail = "original@automo.com";
        String updatedEmail = "updated@automo.com";
        
        AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, originalEmail, activeState);
        AssociatedEmail saved = entityManager.persistAndFlush(email);

        // When
        saved.setEmail(updatedEmail);
        AssociatedEmail updated = associatedEmailRepository.save(saved);
        entityManager.flush();

        // Then
        Optional<AssociatedEmail> retrieved = associatedEmailRepository.findById(updated.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(updatedEmail, retrieved.get().getEmail());
        assertNotEquals(originalEmail, retrieved.get().getEmail());
    }

    @Test
    @DisplayName("Should handle state transitions")
    void shouldHandleStateTransitions() {
        // Given
        AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, "transition@automo.com", activeState);
        AssociatedEmail saved = entityManager.persistAndFlush(email);

        // When - Change state from active to inactive
        saved.setState(inactiveState);
        AssociatedEmail updated = associatedEmailRepository.save(saved);
        entityManager.flush();

        // Then
        Optional<AssociatedEmail> retrieved = associatedEmailRepository.findById(updated.getId());
        assertTrue(retrieved.isPresent());
        assertEquals(inactiveState.getId(), retrieved.get().getState().getId());
    }

    @Test
    @DisplayName("Should return empty list when no emails found for identifier")
    void shouldReturnEmptyListWhenNoEmailsFoundForIdentifier() {
        // When
        List<AssociatedEmail> emails = associatedEmailRepository.findByIdentifierId(999L);

        // Then
        assertNotNull(emails);
        assertTrue(emails.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no emails found for state")
    void shouldReturnEmptyListWhenNoEmailsFoundForState() {
        // When
        List<AssociatedEmail> emails = associatedEmailRepository.findByStateId(999L);

        // Then
        assertNotNull(emails);
        assertTrue(emails.isEmpty());
    }

    @Test
    @DisplayName("Should handle deletion of emails")
    void shouldHandleDeletionOfEmails() {
        // Given
        AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, "delete@automo.com", activeState);
        AssociatedEmail saved = entityManager.persistAndFlush(email);
        Long emailId = saved.getId();

        // When
        associatedEmailRepository.deleteById(emailId);
        entityManager.flush();

        // Then
        Optional<AssociatedEmail> deleted = associatedEmailRepository.findById(emailId);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should validate email field constraints")
    void shouldValidateEmailFieldConstraints() {
        // Given
        AssociatedEmail email = new AssociatedEmail();
        email.setIdentifier(testIdentifier);
        email.setEmail("valid@email.com");
        email.setState(activeState);

        // When & Then
        assertDoesNotThrow(() -> {
            AssociatedEmail saved = associatedEmailRepository.save(email);
            entityManager.flush();
            assertNotNull(saved.getId());
        });
    }

    @Test
    @DisplayName("Should handle special characters in email addresses")
    void shouldHandleSpecialCharactersInEmailAddresses() {
        // Given
        String[] specialEmails = {
            "user+tag@domain.com",
            "user.name@domain.com",
            "user_name@domain-name.com",
            "123@domain.com",
            "user@sub.domain.com"
        };
        
        for (String emailAddr : specialEmails) {
            // When
            AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, emailAddr, activeState);
            AssociatedEmail saved = entityManager.persistAndFlush(email);
            
            // Then
            assertNotNull(saved.getId());
            assertEquals(emailAddr, saved.getEmail());
            
            // Verify retrieval by email works
            Optional<AssociatedEmail> found = associatedEmailRepository.findByEmail(emailAddr);
            assertTrue(found.isPresent(), "Should find email: " + emailAddr);
            assertEquals(emailAddr, found.get().getEmail());
        }
    }

    @Test
    @DisplayName("Should handle international email domains")
    void shouldHandleInternationalEmailDomains() {
        // Given
        String[] internationalEmails = {
            "user@domain.pt",
            "user@domain.es",
            "user@domain.br",
            "user@domain.co.uk",
            "user@domain.com.br"
        };
        
        for (String emailAddr : internationalEmails) {
            // When
            AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, emailAddr, activeState);
            AssociatedEmail saved = entityManager.persistAndFlush(email);
            
            // Then
            assertNotNull(saved.getId());
            assertEquals(emailAddr, saved.getEmail());
        }
    }

    @Test
    @DisplayName("Should maintain referential integrity with identifier")
    void shouldMaintainReferentialIntegrityWithIdentifier() {
        // Given
        AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, "integrity@automo.com", activeState);
        AssociatedEmail saved = entityManager.persistAndFlush(email);

        // When
        Optional<AssociatedEmail> retrieved = associatedEmailRepository.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertNotNull(retrieved.get().getIdentifier());
        assertEquals(testIdentifier.getId(), retrieved.get().getIdentifier().getId());
        assertEquals(testIdentifier.getUserId(), retrieved.get().getIdentifier().getUserId());
    }

    @Test
    @DisplayName("Should maintain referential integrity with state")
    void shouldMaintainReferentialIntegrityWithState() {
        // Given
        AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, "state@automo.com", activeState);
        AssociatedEmail saved = entityManager.persistAndFlush(email);

        // When
        Optional<AssociatedEmail> retrieved = associatedEmailRepository.findById(saved.getId());

        // Then
        assertTrue(retrieved.isPresent());
        assertNotNull(retrieved.get().getState());
        assertEquals(activeState.getId(), retrieved.get().getState().getId());
        assertEquals(activeState.getState(), retrieved.get().getState().getState());
    }

    @Test
    @DisplayName("Should support bulk operations")
    void shouldSupportBulkOperations() {
        // Given
        String[] emailAddresses = {
            "bulk1@automo.com",
            "bulk2@automo.com", 
            "bulk3@automo.com"
        };
        
        // When
        for (String emailAddr : emailAddresses) {
            AssociatedEmail email = TestDataFactory.createValidAssociatedEmail(testIdentifier, emailAddr, activeState);
            associatedEmailRepository.save(email);
        }
        entityManager.flush();
        
        List<AssociatedEmail> allEmails = associatedEmailRepository.findByIdentifierId(testIdentifier.getId());

        // Then
        assertEquals(4, allEmails.size()); // 3 new + 1 from setUp
        
        // Delete all at once
        associatedEmailRepository.deleteAll(allEmails);
        entityManager.flush();
        
        List<AssociatedEmail> remainingEmails = associatedEmailRepository.findByIdentifierId(testIdentifier.getId());
        assertTrue(remainingEmails.isEmpty());
    }
}