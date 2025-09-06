package com.automo.notification.service;

import com.automo.identifier.entity.Identifier;
import com.automo.identifier.service.IdentifierService;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.notification.dto.NotificationDto;
import com.automo.notification.entity.Notification;
import com.automo.notification.repository.NotificationRepository;
import com.automo.notification.response.NotificationResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.utils.TestDataFactory;
import com.automo.user.entity.User;
import com.automo.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for NotificationServiceImpl")
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private IdentifierService identifierService;

    @Mock
    private StateService stateService;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification testNotification;
    private Identifier testSender;
    private Identifier testReceiver;
    private State activeState;
    private State eliminatedState;
    private User senderUser;
    private User receiverUser;

    @BeforeEach
    void setUp() {
        // Setup states
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);

        // Setup identifier type
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        identifierType.setId(1L);

        // Setup identifiers
        testSender = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        testSender.setId(1L);
        testSender.setUserId(1L);

        testReceiver = TestDataFactory.createValidIdentifier(2L, identifierType, activeState);
        testReceiver.setId(2L);
        testReceiver.setUserId(2L);

        // Setup users
        senderUser = new User();
        senderUser.setId(1L);
        senderUser.setName("Sender User");

        receiverUser = new User();
        receiverUser.setId(2L);
        receiverUser.setName("Receiver User");

        // Setup test notification
        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setSender(testSender);
        testNotification.setReceiver(testReceiver);
        testNotification.setUrlRedirect("https://example.com/test");
        testNotification.setState(activeState);
        testNotification.setCreatedAt(LocalDateTime.now());
        testNotification.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create notification successfully")
    void shouldCreateNotificationSuccessfully() {
        // Given
        NotificationDto notificationDto = new NotificationDto(1L, 2L, "https://example.com/test", 1L);

        when(identifierService.findById(1L)).thenReturn(testSender);
        when(identifierService.findById(2L)).thenReturn(testReceiver);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(userService.findById(1L)).thenReturn(senderUser);
        when(userService.findById(2L)).thenReturn(receiverUser);

        // When
        NotificationResponse result = notificationService.createNotification(notificationDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.senderId());
        assertEquals("Sender User", result.senderName());
        assertEquals(2L, result.receiverId());
        assertEquals("Receiver User", result.receiverName());
        assertEquals("https://example.com/test", result.urlRedirect());
        assertEquals(1L, result.stateId());
        assertEquals("ACTIVE", result.stateName());

        verify(identifierService).findById(1L);
        verify(identifierService).findById(2L);
        verify(stateService).findById(1L);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should update notification successfully")
    void shouldUpdateNotificationSuccessfully() {
        // Given
        Long notificationId = 1L;
        NotificationDto notificationDto = new NotificationDto(1L, 2L, "https://example.com/updated", 1L);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));
        when(identifierService.findById(1L)).thenReturn(testSender);
        when(identifierService.findById(2L)).thenReturn(testReceiver);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);
        when(userService.findById(1L)).thenReturn(senderUser);
        when(userService.findById(2L)).thenReturn(receiverUser);

        // When
        NotificationResponse result = notificationService.updateNotification(notificationId, notificationDto);

        // Then
        assertNotNull(result);
        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing notification")
    void shouldThrowExceptionWhenUpdatingNonExistingNotification() {
        // Given
        Long notificationId = 999L;
        NotificationDto notificationDto = new NotificationDto(1L, 2L, "https://example.com/test", 1L);

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> notificationService.updateNotification(notificationId, notificationDto));

        verify(notificationRepository).findById(notificationId);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should get all notifications excluding eliminated")
    void shouldGetAllNotificationsExcludingEliminated() {
        // Given
        Notification notification1 = new Notification();
        notification1.setId(1L);
        notification1.setSender(testSender);
        notification1.setReceiver(testReceiver);
        notification1.setState(activeState);
        notification1.setCreatedAt(LocalDateTime.now());
        notification1.setUpdatedAt(LocalDateTime.now());

        Notification notification2 = new Notification();
        notification2.setId(2L);
        notification2.setSender(testSender);
        notification2.setReceiver(testReceiver);
        notification2.setState(activeState);
        notification2.setCreatedAt(LocalDateTime.now());
        notification2.setUpdatedAt(LocalDateTime.now());

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(notificationRepository.findAll()).thenReturn(Arrays.asList(notification1, notification2));
        when(userService.findById(1L)).thenReturn(senderUser);
        when(userService.findById(2L)).thenReturn(receiverUser);

        // When
        List<NotificationResponse> result = notificationService.getAllNotifications();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(stateService).getEliminatedState();
        verify(notificationRepository).findAll();
    }

    @Test
    @DisplayName("Should filter out eliminated notifications")
    void shouldFilterOutEliminatedNotifications() {
        // Given
        Notification activeNotification = new Notification();
        activeNotification.setId(1L);
        activeNotification.setSender(testSender);
        activeNotification.setReceiver(testReceiver);
        activeNotification.setState(activeState);
        activeNotification.setCreatedAt(LocalDateTime.now());
        activeNotification.setUpdatedAt(LocalDateTime.now());

        Notification eliminatedNotification = new Notification();
        eliminatedNotification.setId(2L);
        eliminatedNotification.setSender(testSender);
        eliminatedNotification.setReceiver(testReceiver);
        eliminatedNotification.setState(eliminatedState);
        eliminatedNotification.setCreatedAt(LocalDateTime.now());
        eliminatedNotification.setUpdatedAt(LocalDateTime.now());

        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(notificationRepository.findAll()).thenReturn(Arrays.asList(activeNotification, eliminatedNotification));
        when(userService.findById(1L)).thenReturn(senderUser);
        when(userService.findById(2L)).thenReturn(receiverUser);

        // When
        List<NotificationResponse> result = notificationService.getAllNotifications();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());

        verify(stateService).getEliminatedState();
        verify(notificationRepository).findAll();
    }

    @Test
    @DisplayName("Should get notification by id successfully")
    void shouldGetNotificationByIdSuccessfully() {
        // Given
        Long notificationId = 1L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));

        // When
        Notification result = notificationService.getNotificationById(notificationId);

        // Then
        assertNotNull(result);
        assertEquals(testNotification.getId(), result.getId());
        assertEquals(testNotification.getSender(), result.getSender());

        verify(notificationRepository).findById(notificationId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existing notification")
    void shouldThrowExceptionWhenGettingNonExistingNotification() {
        // Given
        Long notificationId = 999L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> notificationService.getNotificationById(notificationId));

        verify(notificationRepository).findById(notificationId);
    }

    @Test
    @DisplayName("Should get notification by id response successfully")
    void shouldGetNotificationByIdResponseSuccessfully() {
        // Given
        Long notificationId = 1L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));
        when(userService.findById(1L)).thenReturn(senderUser);
        when(userService.findById(2L)).thenReturn(receiverUser);

        // When
        NotificationResponse result = notificationService.getNotificationByIdResponse(notificationId);

        // Then
        assertNotNull(result);
        assertEquals(testNotification.getId(), result.id());
        assertEquals("Sender User", result.senderName());
        assertEquals("Receiver User", result.receiverName());

        verify(notificationRepository).findById(notificationId);
    }

    @Test
    @DisplayName("Should get notifications by state successfully")
    void shouldGetNotificationsByStateSuccessfully() {
        // Given
        Long stateId = 1L;
        List<Notification> notifications = Arrays.asList(testNotification);

        when(notificationRepository.findByStateId(stateId)).thenReturn(notifications);
        when(userService.findById(1L)).thenReturn(senderUser);
        when(userService.findById(2L)).thenReturn(receiverUser);

        // When
        List<NotificationResponse> result = notificationService.getNotificationsByState(stateId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNotification.getId(), result.get(0).id());

        verify(notificationRepository).findByStateId(stateId);
    }

    @Test
    @DisplayName("Should get notifications by sender successfully")
    void shouldGetNotificationsBySenderSuccessfully() {
        // Given
        Long senderId = 1L;
        List<Notification> notifications = Arrays.asList(testNotification);

        when(notificationRepository.findBySenderId(senderId)).thenReturn(notifications);
        when(userService.findById(1L)).thenReturn(senderUser);
        when(userService.findById(2L)).thenReturn(receiverUser);

        // When
        List<NotificationResponse> result = notificationService.getNotificationsBySender(senderId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNotification.getId(), result.get(0).id());

        verify(notificationRepository).findBySenderId(senderId);
    }

    @Test
    @DisplayName("Should get notifications by receiver successfully")
    void shouldGetNotificationsByReceiverSuccessfully() {
        // Given
        Long receiverId = 2L;
        List<Notification> notifications = Arrays.asList(testNotification);

        when(notificationRepository.findByReceiverId(receiverId)).thenReturn(notifications);
        when(userService.findById(1L)).thenReturn(senderUser);
        when(userService.findById(2L)).thenReturn(receiverUser);

        // When
        List<NotificationResponse> result = notificationService.getNotificationsByReceiver(receiverId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testNotification.getId(), result.get(0).id());

        verify(notificationRepository).findByReceiverId(receiverId);
    }

    @Test
    @DisplayName("Should soft delete notification successfully")
    void shouldSoftDeleteNotificationSuccessfully() {
        // Given
        Long notificationId = 1L;

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // When
        notificationService.deleteNotification(notificationId);

        // Then
        verify(notificationRepository).findById(notificationId);
        verify(stateService).getEliminatedState();
        verify(notificationRepository).save(testNotification);
        assertEquals(eliminatedState, testNotification.getState());
    }

    @Test
    @DisplayName("Should implement findById method correctly")
    void shouldImplementFindByIdMethodCorrectly() {
        // Given
        Long notificationId = 1L;
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));

        // When
        Notification result = notificationService.findById(notificationId);

        // Then
        assertNotNull(result);
        assertEquals(testNotification, result);
        verify(notificationRepository).findById(notificationId);
    }

    @Test
    @DisplayName("Should implement findByIdAndStateId method correctly")
    void shouldImplementFindByIdAndStateIdMethodCorrectly() {
        // Given
        Long notificationId = 1L;
        Long stateId = 1L;

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));

        // When
        Notification result = notificationService.findByIdAndStateId(notificationId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testNotification, result);
        verify(notificationRepository).findById(notificationId);
    }

    @Test
    @DisplayName("Should throw exception in findByIdAndStateId when states don't match")
    void shouldThrowExceptionInFindByIdAndStateIdWhenStatesDontMatch() {
        // Given
        Long notificationId = 1L;
        Long stateId = 2L; // Different from notification's state (1L)

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> notificationService.findByIdAndStateId(notificationId, stateId));

        verify(notificationRepository).findById(notificationId);
    }

    @Test
    @DisplayName("Should use default state in findByIdAndStateId when stateId is null")
    void shouldUseDefaultStateInFindByIdAndStateIdWhenStateIdIsNull() {
        // Given
        Long notificationId = 1L;
        Long stateId = null;

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));

        // When
        Notification result = notificationService.findByIdAndStateId(notificationId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testNotification, result);
        verify(notificationRepository).findById(notificationId);
    }

    @Test
    @DisplayName("Should handle mapping when user not found")
    void shouldHandleMappingWhenUserNotFound() {
        // Given
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));
        when(userService.findById(1L)).thenThrow(new EntityNotFoundException("User not found"));
        when(userService.findById(2L)).thenThrow(new EntityNotFoundException("User not found"));

        // When
        NotificationResponse result = notificationService.getNotificationByIdResponse(1L);

        // Then
        assertNotNull(result);
        assertNull(result.senderName());
        assertNull(result.receiverName());
        assertEquals(1L, result.senderId());
        assertEquals(2L, result.receiverId());

        verify(notificationRepository).findById(1L);
        verify(userService).findById(1L);
        verify(userService).findById(2L);
    }

    @Test
    @DisplayName("Should throw exception when sender not found during creation")
    void shouldThrowExceptionWhenSenderNotFoundDuringCreation() {
        // Given
        NotificationDto notificationDto = new NotificationDto(999L, 2L, "https://example.com/test", 1L);

        when(identifierService.findById(999L)).thenThrow(new EntityNotFoundException("Identifier not found"));

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> notificationService.createNotification(notificationDto));

        verify(identifierService).findById(999L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should throw exception when receiver not found during creation")
    void shouldThrowExceptionWhenReceiverNotFoundDuringCreation() {
        // Given
        NotificationDto notificationDto = new NotificationDto(1L, 999L, "https://example.com/test", 1L);

        when(identifierService.findById(1L)).thenReturn(testSender);
        when(identifierService.findById(999L)).thenThrow(new EntityNotFoundException("Identifier not found"));

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> notificationService.createNotification(notificationDto));

        verify(identifierService).findById(1L);
        verify(identifierService).findById(999L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should throw exception when state not found during creation")
    void shouldThrowExceptionWhenStateNotFoundDuringCreation() {
        // Given
        NotificationDto notificationDto = new NotificationDto(1L, 2L, "https://example.com/test", 999L);

        when(identifierService.findById(1L)).thenReturn(testSender);
        when(identifierService.findById(2L)).thenReturn(testReceiver);
        when(stateService.findById(999L)).thenThrow(new EntityNotFoundException("State not found"));

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> notificationService.createNotification(notificationDto));

        verify(identifierService).findById(1L);
        verify(identifierService).findById(2L);
        verify(stateService).findById(999L);
        verify(notificationRepository, never()).save(any(Notification.class));
    }
}