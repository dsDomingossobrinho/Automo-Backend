package com.automo.notificationType.repository;

import com.automo.notificationType.entity.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Tests for NotificationTypeRepository")
class NotificationTypeRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationTypeRepository notificationTypeRepository;

    private NotificationType testNotificationType;

    @BeforeEach
    void setUp() {
        testNotificationType = new NotificationType();
        testNotificationType.setType("EMAIL");
        testNotificationType.setDescription("Email notification type");
    }

    @Test
    @DisplayName("Should save and find notification type successfully")
    void shouldSaveAndFindNotificationTypeSuccessfully() {
        // When
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        Optional<NotificationType> found = notificationTypeRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("EMAIL", found.get().getType());
        assertEquals("Email notification type", found.get().getDescription());
        assertNotNull(found.get().getCreatedAt());
        assertNotNull(found.get().getUpdatedAt());
    }

    @Test
    @DisplayName("Should find notification type by type successfully")
    void shouldFindNotificationTypeByTypeSuccessfully() {
        // Given
        notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        // When
        Optional<NotificationType> found = notificationTypeRepository.findByType("EMAIL");

        // Then
        assertTrue(found.isPresent());
        assertEquals("EMAIL", found.get().getType());
        assertEquals("Email notification type", found.get().getDescription());
    }

    @Test
    @DisplayName("Should return empty when notification type not found by type")
    void shouldReturnEmptyWhenNotificationTypeNotFoundByType() {
        // When
        Optional<NotificationType> found = notificationTypeRepository.findByType("NON_EXISTENT");

        // Then
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should check if notification type exists by type")
    void shouldCheckIfNotificationTypeExistsByType() {
        // Given
        notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        // When & Then
        assertTrue(notificationTypeRepository.existsByType("EMAIL"));
        assertFalse(notificationTypeRepository.existsByType("NON_EXISTENT"));
    }

    @Test
    @DisplayName("Should find all notification types successfully")
    void shouldFindAllNotificationTypesSuccessfully() {
        // Given
        NotificationType emailType = new NotificationType("EMAIL", "Email notifications");
        NotificationType smsType = new NotificationType("SMS", "SMS notifications");
        NotificationType pushType = new NotificationType("PUSH", "Push notifications");

        notificationTypeRepository.save(emailType);
        notificationTypeRepository.save(smsType);
        notificationTypeRepository.save(pushType);
        entityManager.flush();

        // When
        List<NotificationType> allTypes = notificationTypeRepository.findAll();

        // Then
        assertEquals(3, allTypes.size());
        assertTrue(allTypes.stream().anyMatch(type -> type.getType().equals("EMAIL")));
        assertTrue(allTypes.stream().anyMatch(type -> type.getType().equals("SMS")));
        assertTrue(allTypes.stream().anyMatch(type -> type.getType().equals("PUSH")));
    }

    @Test
    @DisplayName("Should update notification type successfully")
    void shouldUpdateNotificationTypeSuccessfully() {
        // Given
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        // When
        saved.setType("UPDATED_EMAIL");
        saved.setDescription("Updated email notification type");
        NotificationType updated = notificationTypeRepository.save(saved);
        entityManager.flush();

        Optional<NotificationType> found = notificationTypeRepository.findById(updated.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("UPDATED_EMAIL", found.get().getType());
        assertEquals("Updated email notification type", found.get().getDescription());
        assertNotNull(found.get().getUpdatedAt());
        assertTrue(found.get().getUpdatedAt().isAfter(found.get().getCreatedAt()) ||
                   found.get().getUpdatedAt().equals(found.get().getCreatedAt()));
    }

    @Test
    @DisplayName("Should delete notification type successfully")
    void shouldDeleteNotificationTypeSuccessfully() {
        // Given
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        Long notificationTypeId = saved.getId();
        assertTrue(notificationTypeRepository.existsById(notificationTypeId));

        // When
        notificationTypeRepository.deleteById(notificationTypeId);
        entityManager.flush();

        // Then
        assertFalse(notificationTypeRepository.existsById(notificationTypeId));
        Optional<NotificationType> found = notificationTypeRepository.findById(notificationTypeId);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should save notification type with null description")
    void shouldSaveNotificationTypeWithNullDescription() {
        // Given
        testNotificationType.setDescription(null);

        // When
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        Optional<NotificationType> found = notificationTypeRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("EMAIL", found.get().getType());
        assertNull(found.get().getDescription());
    }

    @Test
    @DisplayName("Should save notification type with empty description")
    void shouldSaveNotificationTypeWithEmptyDescription() {
        // Given
        testNotificationType.setDescription("");

        // When
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        Optional<NotificationType> found = notificationTypeRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("EMAIL", found.get().getType());
        assertEquals("", found.get().getDescription());
    }

    @Test
    @DisplayName("Should handle special characters in type")
    void shouldHandleSpecialCharactersInType() {
        // Given
        testNotificationType.setType("CUSTOM_TYPE_123");
        testNotificationType.setDescription("Custom type with underscore and numbers");

        // When
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        Optional<NotificationType> found = notificationTypeRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("CUSTOM_TYPE_123", found.get().getType());
        assertEquals("Custom type with underscore and numbers", found.get().getDescription());
    }

    @Test
    @DisplayName("Should handle unicode characters in type and description")
    void shouldHandleUnicodeCharactersInTypeAndDescription() {
        // Given
        testNotificationType.setType("UNICODE_测试");
        testNotificationType.setDescription("Description with unicode: 测试, éñ, 🔔");

        // When
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        Optional<NotificationType> found = notificationTypeRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("UNICODE_测试", found.get().getType());
        assertEquals("Description with unicode: 测试, éñ, 🔔", found.get().getDescription());
    }

    @Test
    @DisplayName("Should handle long description")
    void shouldHandleLongDescription() {
        // Given
        String longDescription = "This is a very long description for the notification type that contains " +
                                "multiple sentences and provides detailed information about what this " +
                                "notification type is used for in the system. It can handle large amounts " +
                                "of text without any issues and should be stored properly in the database. " +
                                "This description continues to be very long to test the system's ability " +
                                "to handle substantial amounts of text data.";
        
        testNotificationType.setDescription(longDescription);

        // When
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        Optional<NotificationType> found = notificationTypeRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("EMAIL", found.get().getType());
        assertEquals(longDescription, found.get().getDescription());
    }

    @Test
    @DisplayName("Should enforce not null constraint on type")
    void shouldEnforceNotNullConstraintOnType() {
        // Given
        testNotificationType.setType(null);

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> {
            notificationTypeRepository.save(testNotificationType);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Should handle case sensitivity in type")
    void shouldHandleCaseSensitivityInType() {
        // Given
        NotificationType emailLower = new NotificationType("email", "Email lowercase");
        NotificationType emailUpper = new NotificationType("EMAIL", "Email uppercase");

        // When
        NotificationType savedLower = notificationTypeRepository.save(emailLower);
        NotificationType savedUpper = notificationTypeRepository.save(emailUpper);
        entityManager.flush();

        // Then
        Optional<NotificationType> foundLower = notificationTypeRepository.findByType("email");
        Optional<NotificationType> foundUpper = notificationTypeRepository.findByType("EMAIL");

        assertTrue(foundLower.isPresent());
        assertTrue(foundUpper.isPresent());
        assertEquals("email", foundLower.get().getType());
        assertEquals("EMAIL", foundUpper.get().getType());
        assertNotEquals(savedLower.getId(), savedUpper.getId());
    }

    @Test
    @DisplayName("Should maintain referential integrity with audit fields")
    void shouldMaintainReferentialIntegrityWithAuditFields() {
        // Given & When
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        // Then
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertNotNull(saved.getUpdatedAt());
        assertTrue(saved.getId() > 0);
    }

    @Test
    @DisplayName("Should count notification types correctly")
    void shouldCountNotificationTypesCorrectly() {
        // Given
        NotificationType type1 = new NotificationType("TYPE1", "First type");
        NotificationType type2 = new NotificationType("TYPE2", "Second type");

        notificationTypeRepository.save(type1);
        notificationTypeRepository.save(type2);
        entityManager.flush();

        // When
        long count = notificationTypeRepository.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should check notification type existence correctly")
    void shouldCheckNotificationTypeExistenceCorrectly() {
        // Given
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        // When & Then
        assertTrue(notificationTypeRepository.existsById(saved.getId()));
        assertFalse(notificationTypeRepository.existsById(999L));
    }

    @Test
    @DisplayName("Should find by type with exact match")
    void shouldFindByTypeWithExactMatch() {
        // Given
        NotificationType emailType = new NotificationType("EMAIL", "Email notifications");
        NotificationType emailSimilar = new NotificationType("EMAIL_SIMILAR", "Similar email type");

        notificationTypeRepository.save(emailType);
        notificationTypeRepository.save(emailSimilar);
        entityManager.flush();

        // When
        Optional<NotificationType> foundEmail = notificationTypeRepository.findByType("EMAIL");
        Optional<NotificationType> foundSimilar = notificationTypeRepository.findByType("EMAIL_SIMILAR");

        // Then
        assertTrue(foundEmail.isPresent());
        assertTrue(foundSimilar.isPresent());
        assertEquals("EMAIL", foundEmail.get().getType());
        assertEquals("EMAIL_SIMILAR", foundSimilar.get().getType());
        assertNotEquals(foundEmail.get().getId(), foundSimilar.get().getId());
    }

    @Test
    @DisplayName("Should handle whitespace in type and description")
    void shouldHandleWhitespaceInTypeAndDescription() {
        // Given
        testNotificationType.setType("  WHITESPACE_TYPE  ");
        testNotificationType.setDescription("  Description with leading and trailing spaces  ");

        // When
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        Optional<NotificationType> found = notificationTypeRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        // Note: The repository should store exactly what was provided (including whitespace)
        assertEquals("  WHITESPACE_TYPE  ", found.get().getType());
        assertEquals("  Description with leading and trailing spaces  ", found.get().getDescription());
    }

    @Test
    @DisplayName("Should support batch operations")
    void shouldSupportBatchOperations() {
        // Given
        NotificationType type1 = new NotificationType("BATCH1", "First batch type");
        NotificationType type2 = new NotificationType("BATCH2", "Second batch type");
        NotificationType type3 = new NotificationType("BATCH3", "Third batch type");

        List<NotificationType> types = List.of(type1, type2, type3);

        // When
        List<NotificationType> savedTypes = notificationTypeRepository.saveAll(types);
        entityManager.flush();

        // Then
        assertEquals(3, savedTypes.size());
        assertEquals(3, notificationTypeRepository.count());

        assertTrue(notificationTypeRepository.existsByType("BATCH1"));
        assertTrue(notificationTypeRepository.existsByType("BATCH2"));
        assertTrue(notificationTypeRepository.existsByType("BATCH3"));
    }

    @Test
    @DisplayName("Should handle concurrent access scenarios")
    void shouldHandleConcurrentAccessScenarios() {
        // Given
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        // When - Simulate concurrent read operations
        Optional<NotificationType> found1 = notificationTypeRepository.findById(saved.getId());
        Optional<NotificationType> found2 = notificationTypeRepository.findByType(saved.getType());
        boolean exists = notificationTypeRepository.existsById(saved.getId());

        // Then
        assertTrue(found1.isPresent());
        assertTrue(found2.isPresent());
        assertTrue(exists);
        assertEquals(found1.get().getId(), found2.get().getId());
    }

    @Test
    @DisplayName("Should maintain data consistency after multiple updates")
    void shouldMaintainDataConsistencyAfterMultipleUpdates() {
        // Given
        NotificationType saved = notificationTypeRepository.save(testNotificationType);
        entityManager.flush();

        // When - Multiple updates
        saved.setDescription("First update");
        notificationTypeRepository.save(saved);
        entityManager.flush();

        saved.setDescription("Second update");
        notificationTypeRepository.save(saved);
        entityManager.flush();

        saved.setType("UPDATED_EMAIL");
        saved.setDescription("Final update");
        NotificationType finalSaved = notificationTypeRepository.save(saved);
        entityManager.flush();

        // Then
        Optional<NotificationType> found = notificationTypeRepository.findById(finalSaved.getId());
        assertTrue(found.isPresent());
        assertEquals("UPDATED_EMAIL", found.get().getType());
        assertEquals("Final update", found.get().getDescription());

        // Verify old type doesn't exist
        Optional<NotificationType> oldType = notificationTypeRepository.findByType("EMAIL");
        assertTrue(oldType.isEmpty());

        // Verify new type exists
        Optional<NotificationType> newType = notificationTypeRepository.findByType("UPDATED_EMAIL");
        assertTrue(newType.isPresent());
    }
}