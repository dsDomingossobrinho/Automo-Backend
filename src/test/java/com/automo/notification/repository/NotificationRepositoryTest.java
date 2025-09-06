package com.automo.notification.repository;

import com.automo.identifier.entity.Identifier;
import com.automo.identifier.repository.IdentifierRepository;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.identifierType.repository.IdentifierTypeRepository;
import com.automo.notification.entity.Notification;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
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
@DisplayName("Tests for NotificationRepository")
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private IdentifierRepository identifierRepository;

    @Autowired
    private IdentifierTypeRepository identifierTypeRepository;

    @Autowired
    private StateRepository stateRepository;

    private Notification testNotification;
    private Identifier testSender;
    private Identifier testReceiver;
    private State activeState;
    private State inactiveState;
    private IdentifierType identifierType;

    @BeforeEach
    void setUp() {
        // Setup states
        activeState = TestDataFactory.createActiveState();
        inactiveState = TestDataFactory.createInactiveState();
        
        activeState = stateRepository.save(activeState);
        inactiveState = stateRepository.save(inactiveState);

        // Setup identifier type
        identifierType = TestDataFactory.createNifIdentifierType();
        identifierType = identifierTypeRepository.save(identifierType);

        // Setup identifiers
        testSender = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        testReceiver = TestDataFactory.createValidIdentifier(2L, identifierType, activeState);
        
        testSender = identifierRepository.save(testSender);
        testReceiver = identifierRepository.save(testReceiver);

        // Setup test notification
        testNotification = new Notification();
        testNotification.setSender(testSender);
        testNotification.setReceiver(testReceiver);
        testNotification.setUrlRedirect("https://example.com/test");
        testNotification.setState(activeState);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should save and find notification successfully")
    void shouldSaveAndFindNotificationSuccessfully() {
        // When
        Notification saved = notificationRepository.save(testNotification);
        entityManager.flush();

        Optional<Notification> found = notificationRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(testSender.getId(), found.get().getSender().getId());
        assertEquals(testReceiver.getId(), found.get().getReceiver().getId());
        assertEquals("https://example.com/test", found.get().getUrlRedirect());
        assertEquals(activeState.getId(), found.get().getState().getId());
    }

    @Test
    @DisplayName("Should find notifications by state ID")
    void shouldFindNotificationsByStateId() {
        // Given
        Notification notification1 = new Notification();
        notification1.setSender(testSender);
        notification1.setReceiver(testReceiver);
        notification1.setState(activeState);
        notification1.setUrlRedirect("https://test1.com");

        Notification notification2 = new Notification();
        notification2.setSender(testSender);
        notification2.setReceiver(testReceiver);
        notification2.setState(activeState);
        notification2.setUrlRedirect("https://test2.com");

        Notification notification3 = new Notification();
        notification3.setSender(testSender);
        notification3.setReceiver(testReceiver);
        notification3.setState(inactiveState);
        notification3.setUrlRedirect("https://test3.com");

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);
        entityManager.flush();

        // When
        List<Notification> activeNotifications = notificationRepository.findByStateId(activeState.getId());
        List<Notification> inactiveNotifications = notificationRepository.findByStateId(inactiveState.getId());

        // Then
        assertEquals(2, activeNotifications.size());
        assertEquals(1, inactiveNotifications.size());

        assertTrue(activeNotifications.stream().allMatch(n -> n.getState().getId().equals(activeState.getId())));
        assertTrue(inactiveNotifications.stream().allMatch(n -> n.getState().getId().equals(inactiveState.getId())));
    }

    @Test
    @DisplayName("Should find notifications by sender ID")
    void shouldFindNotificationsBySenderId() {
        // Given
        Identifier anotherSender = TestDataFactory.createValidIdentifier(3L, identifierType, activeState);
        anotherSender = identifierRepository.save(anotherSender);

        Notification notification1 = new Notification();
        notification1.setSender(testSender);
        notification1.setReceiver(testReceiver);
        notification1.setState(activeState);
        notification1.setUrlRedirect("https://test1.com");

        Notification notification2 = new Notification();
        notification2.setSender(testSender);
        notification2.setReceiver(testReceiver);
        notification2.setState(activeState);
        notification2.setUrlRedirect("https://test2.com");

        Notification notification3 = new Notification();
        notification3.setSender(anotherSender);
        notification3.setReceiver(testReceiver);
        notification3.setState(activeState);
        notification3.setUrlRedirect("https://test3.com");

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);
        entityManager.flush();

        // When
        List<Notification> senderNotifications = notificationRepository.findBySenderId(testSender.getId());
        List<Notification> anotherSenderNotifications = notificationRepository.findBySenderId(anotherSender.getId());

        // Then
        assertEquals(2, senderNotifications.size());
        assertEquals(1, anotherSenderNotifications.size());

        assertTrue(senderNotifications.stream().allMatch(n -> n.getSender().getId().equals(testSender.getId())));
        assertTrue(anotherSenderNotifications.stream().allMatch(n -> n.getSender().getId().equals(anotherSender.getId())));
    }

    @Test
    @DisplayName("Should find notifications by receiver ID")
    void shouldFindNotificationsByReceiverId() {
        // Given
        Identifier anotherReceiver = TestDataFactory.createValidIdentifier(4L, identifierType, activeState);
        anotherReceiver = identifierRepository.save(anotherReceiver);

        Notification notification1 = new Notification();
        notification1.setSender(testSender);
        notification1.setReceiver(testReceiver);
        notification1.setState(activeState);
        notification1.setUrlRedirect("https://test1.com");

        Notification notification2 = new Notification();
        notification2.setSender(testSender);
        notification2.setReceiver(testReceiver);
        notification2.setState(activeState);
        notification2.setUrlRedirect("https://test2.com");

        Notification notification3 = new Notification();
        notification3.setSender(testSender);
        notification3.setReceiver(anotherReceiver);
        notification3.setState(activeState);
        notification3.setUrlRedirect("https://test3.com");

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        notificationRepository.save(notification3);
        entityManager.flush();

        // When
        List<Notification> receiverNotifications = notificationRepository.findByReceiverId(testReceiver.getId());
        List<Notification> anotherReceiverNotifications = notificationRepository.findByReceiverId(anotherReceiver.getId());

        // Then
        assertEquals(2, receiverNotifications.size());
        assertEquals(1, anotherReceiverNotifications.size());

        assertTrue(receiverNotifications.stream().allMatch(n -> n.getReceiver().getId().equals(testReceiver.getId())));
        assertTrue(anotherReceiverNotifications.stream().allMatch(n -> n.getReceiver().getId().equals(anotherReceiver.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no notifications found by state")
    void shouldReturnEmptyListWhenNoNotificationsFoundByState() {
        // Given
        Long nonExistentStateId = 999L;

        // When
        List<Notification> notifications = notificationRepository.findByStateId(nonExistentStateId);

        // Then
        assertTrue(notifications.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no notifications found by sender")
    void shouldReturnEmptyListWhenNoNotificationsFoundBySender() {
        // Given
        Long nonExistentSenderId = 999L;

        // When
        List<Notification> notifications = notificationRepository.findBySenderId(nonExistentSenderId);

        // Then
        assertTrue(notifications.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no notifications found by receiver")
    void shouldReturnEmptyListWhenNoNotificationsFoundByReceiver() {
        // Given
        Long nonExistentReceiverId = 999L;

        // When
        List<Notification> notifications = notificationRepository.findByReceiverId(nonExistentReceiverId);

        // Then
        assertTrue(notifications.isEmpty());
    }

    @Test
    @DisplayName("Should update notification successfully")
    void shouldUpdateNotificationSuccessfully() {
        // Given
        Notification saved = notificationRepository.save(testNotification);
        entityManager.flush();

        // When
        saved.setUrlRedirect("https://updated.com");
        saved.setState(inactiveState);
        Notification updated = notificationRepository.save(saved);
        entityManager.flush();

        Optional<Notification> found = notificationRepository.findById(updated.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("https://updated.com", found.get().getUrlRedirect());
        assertEquals(inactiveState.getId(), found.get().getState().getId());
        assertNotNull(found.get().getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete notification successfully")
    void shouldDeleteNotificationSuccessfully() {
        // Given
        Notification saved = notificationRepository.save(testNotification);
        entityManager.flush();

        Long notificationId = saved.getId();
        assertTrue(notificationRepository.existsById(notificationId));

        // When
        notificationRepository.deleteById(notificationId);
        entityManager.flush();

        // Then
        assertFalse(notificationRepository.existsById(notificationId));
        Optional<Notification> found = notificationRepository.findById(notificationId);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should find all notifications successfully")
    void shouldFindAllNotificationsSuccessfully() {
        // Given
        Notification notification1 = new Notification();
        notification1.setSender(testSender);
        notification1.setReceiver(testReceiver);
        notification1.setState(activeState);
        notification1.setUrlRedirect("https://test1.com");

        Notification notification2 = new Notification();
        notification2.setSender(testSender);
        notification2.setReceiver(testReceiver);
        notification2.setState(inactiveState);
        notification2.setUrlRedirect("https://test2.com");

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        entityManager.flush();

        // When
        List<Notification> allNotifications = notificationRepository.findAll();

        // Then
        assertEquals(2, allNotifications.size());
        assertTrue(allNotifications.stream().anyMatch(n -> n.getUrlRedirect().equals("https://test1.com")));
        assertTrue(allNotifications.stream().anyMatch(n -> n.getUrlRedirect().equals("https://test2.com")));
    }

    @Test
    @DisplayName("Should handle notification with null URL redirect")
    void shouldHandleNotificationWithNullUrlRedirect() {
        // Given
        testNotification.setUrlRedirect(null);

        // When
        Notification saved = notificationRepository.save(testNotification);
        entityManager.flush();

        Optional<Notification> found = notificationRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertNull(found.get().getUrlRedirect());
    }

    @Test
    @DisplayName("Should handle notification with empty URL redirect")
    void shouldHandleNotificationWithEmptyUrlRedirect() {
        // Given
        testNotification.setUrlRedirect("");

        // When
        Notification saved = notificationRepository.save(testNotification);
        entityManager.flush();

        Optional<Notification> found = notificationRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("", found.get().getUrlRedirect());
    }

    @Test
    @DisplayName("Should maintain referential integrity with identifiers")
    void shouldMaintainReferentialIntegrityWithIdentifiers() {
        // Given
        Notification saved = notificationRepository.save(testNotification);
        entityManager.flush();

        // When
        Optional<Notification> found = notificationRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertNotNull(found.get().getSender());
        assertNotNull(found.get().getReceiver());
        assertEquals(testSender.getId(), found.get().getSender().getId());
        assertEquals(testReceiver.getId(), found.get().getReceiver().getId());
    }

    @Test
    @DisplayName("Should maintain referential integrity with states")
    void shouldMaintainReferentialIntegrityWithStates() {
        // Given
        Notification saved = notificationRepository.save(testNotification);
        entityManager.flush();

        // When
        Optional<Notification> found = notificationRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertNotNull(found.get().getState());
        assertEquals(activeState.getId(), found.get().getState().getId());
        assertEquals("ACTIVE", found.get().getState().getState());
    }

    @Test
    @DisplayName("Should handle complex query scenarios")
    void shouldHandleComplexQueryScenarios() {
        // Given
        Identifier sender2 = TestDataFactory.createValidIdentifier(5L, identifierType, activeState);
        Identifier receiver2 = TestDataFactory.createValidIdentifier(6L, identifierType, activeState);
        sender2 = identifierRepository.save(sender2);
        receiver2 = identifierRepository.save(receiver2);

        // Create multiple notifications with different combinations
        Notification n1 = new Notification();
        n1.setSender(testSender);
        n1.setReceiver(testReceiver);
        n1.setState(activeState);
        n1.setUrlRedirect("https://test1.com");

        Notification n2 = new Notification();
        n2.setSender(testSender);
        n2.setReceiver(receiver2);
        n2.setState(activeState);
        n2.setUrlRedirect("https://test2.com");

        Notification n3 = new Notification();
        n3.setSender(sender2);
        n3.setReceiver(testReceiver);
        n3.setState(inactiveState);
        n3.setUrlRedirect("https://test3.com");

        notificationRepository.save(n1);
        notificationRepository.save(n2);
        notificationRepository.save(n3);
        entityManager.flush();

        // When & Then
        List<Notification> senderNotifications = notificationRepository.findBySenderId(testSender.getId());
        assertEquals(2, senderNotifications.size());

        List<Notification> receiverNotifications = notificationRepository.findByReceiverId(testReceiver.getId());
        assertEquals(2, receiverNotifications.size());

        List<Notification> activeNotifications = notificationRepository.findByStateId(activeState.getId());
        assertEquals(2, activeNotifications.size());

        List<Notification> inactiveNotifications = notificationRepository.findByStateId(inactiveState.getId());
        assertEquals(1, inactiveNotifications.size());
    }

    @Test
    @DisplayName("Should count notifications correctly")
    void shouldCountNotificationsCorrectly() {
        // Given
        Notification notification1 = new Notification();
        notification1.setSender(testSender);
        notification1.setReceiver(testReceiver);
        notification1.setState(activeState);

        Notification notification2 = new Notification();
        notification2.setSender(testSender);
        notification2.setReceiver(testReceiver);
        notification2.setState(activeState);

        notificationRepository.save(notification1);
        notificationRepository.save(notification2);
        entityManager.flush();

        // When
        long count = notificationRepository.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should check notification existence correctly")
    void shouldCheckNotificationExistenceCorrectly() {
        // Given
        Notification saved = notificationRepository.save(testNotification);
        entityManager.flush();

        // When & Then
        assertTrue(notificationRepository.existsById(saved.getId()));
        assertFalse(notificationRepository.existsById(999L));
    }
}