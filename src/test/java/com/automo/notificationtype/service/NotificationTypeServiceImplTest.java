package com.automo.notificationType.service;

import com.automo.notificationType.dto.NotificationTypeDto;
import com.automo.notificationType.entity.NotificationType;
import com.automo.notificationType.repository.NotificationTypeRepository;
import com.automo.notificationType.response.NotificationTypeResponse;
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
@DisplayName("Tests for NotificationTypeServiceImpl")
class NotificationTypeServiceImplTest {

    @Mock
    private NotificationTypeRepository notificationTypeRepository;

    @InjectMocks
    private NotificationTypeServiceImpl notificationTypeService;

    private NotificationType testNotificationType;

    @BeforeEach
    void setUp() {
        testNotificationType = new NotificationType();
        testNotificationType.setId(1L);
        testNotificationType.setType("EMAIL");
        testNotificationType.setDescription("Email notification type");
        testNotificationType.setCreatedAt(LocalDateTime.now());
        testNotificationType.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create notification type successfully")
    void shouldCreateNotificationTypeSuccessfully() {
        // Given
        NotificationTypeDto notificationTypeDto = new NotificationTypeDto("SMS", "SMS notification type");

        when(notificationTypeRepository.save(any(NotificationType.class))).thenReturn(testNotificationType);

        // When
        NotificationTypeResponse result = notificationTypeService.createNotificationType(notificationTypeDto);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("EMAIL", result.type());
        assertEquals("Email notification type", result.description());
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());

        verify(notificationTypeRepository).save(any(NotificationType.class));
    }

    @Test
    @DisplayName("Should update notification type successfully")
    void shouldUpdateNotificationTypeSuccessfully() {
        // Given
        Long notificationTypeId = 1L;
        NotificationTypeDto notificationTypeDto = new NotificationTypeDto("PUSH", "Push notification type");

        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.of(testNotificationType));
        when(notificationTypeRepository.save(any(NotificationType.class))).thenReturn(testNotificationType);

        // When
        NotificationTypeResponse result = notificationTypeService.updateNotificationType(notificationTypeId, notificationTypeDto);

        // Then
        assertNotNull(result);
        verify(notificationTypeRepository).findById(notificationTypeId);
        verify(notificationTypeRepository).save(any(NotificationType.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing notification type")
    void shouldThrowExceptionWhenUpdatingNonExistingNotificationType() {
        // Given
        Long notificationTypeId = 999L;
        NotificationTypeDto notificationTypeDto = new NotificationTypeDto("PUSH", "Push notification type");

        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> notificationTypeService.updateNotificationType(notificationTypeId, notificationTypeDto));

        verify(notificationTypeRepository).findById(notificationTypeId);
        verify(notificationTypeRepository, never()).save(any(NotificationType.class));
    }

    @Test
    @DisplayName("Should get all notification types successfully")
    void shouldGetAllNotificationTypesSuccessfully() {
        // Given
        NotificationType notificationType1 = new NotificationType("EMAIL", "Email notifications");
        notificationType1.setId(1L);
        notificationType1.setCreatedAt(LocalDateTime.now());
        notificationType1.setUpdatedAt(LocalDateTime.now());

        NotificationType notificationType2 = new NotificationType("SMS", "SMS notifications");
        notificationType2.setId(2L);
        notificationType2.setCreatedAt(LocalDateTime.now());
        notificationType2.setUpdatedAt(LocalDateTime.now());

        when(notificationTypeRepository.findAll()).thenReturn(Arrays.asList(notificationType1, notificationType2));

        // When
        List<NotificationTypeResponse> result = notificationTypeService.getAllNotificationTypes();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        NotificationTypeResponse firstResponse = result.get(0);
        assertEquals(1L, firstResponse.id());
        assertEquals("EMAIL", firstResponse.type());
        assertEquals("Email notifications", firstResponse.description());

        NotificationTypeResponse secondResponse = result.get(1);
        assertEquals(2L, secondResponse.id());
        assertEquals("SMS", secondResponse.type());
        assertEquals("SMS notifications", secondResponse.description());

        verify(notificationTypeRepository).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no notification types exist")
    void shouldReturnEmptyListWhenNoNotificationTypesExist() {
        // Given
        when(notificationTypeRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<NotificationTypeResponse> result = notificationTypeService.getAllNotificationTypes();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(notificationTypeRepository).findAll();
    }

    @Test
    @DisplayName("Should get notification type by id successfully")
    void shouldGetNotificationTypeByIdSuccessfully() {
        // Given
        Long notificationTypeId = 1L;
        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.of(testNotificationType));

        // When
        NotificationType result = notificationTypeService.getNotificationTypeById(notificationTypeId);

        // Then
        assertNotNull(result);
        assertEquals(testNotificationType.getId(), result.getId());
        assertEquals(testNotificationType.getType(), result.getType());
        assertEquals(testNotificationType.getDescription(), result.getDescription());

        verify(notificationTypeRepository).findById(notificationTypeId);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existing notification type")
    void shouldThrowExceptionWhenGettingNonExistingNotificationType() {
        // Given
        Long notificationTypeId = 999L;
        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> notificationTypeService.getNotificationTypeById(notificationTypeId));

        verify(notificationTypeRepository).findById(notificationTypeId);
    }

    @Test
    @DisplayName("Should get notification type by id response successfully")
    void shouldGetNotificationTypeByIdResponseSuccessfully() {
        // Given
        Long notificationTypeId = 1L;
        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.of(testNotificationType));

        // When
        NotificationTypeResponse result = notificationTypeService.getNotificationTypeByIdResponse(notificationTypeId);

        // Then
        assertNotNull(result);
        assertEquals(testNotificationType.getId(), result.id());
        assertEquals(testNotificationType.getType(), result.type());
        assertEquals(testNotificationType.getDescription(), result.description());

        verify(notificationTypeRepository).findById(notificationTypeId);
    }

    @Test
    @DisplayName("Should physically delete notification type successfully")
    void shouldPhysicallyDeleteNotificationTypeSuccessfully() {
        // Given
        Long notificationTypeId = 1L;

        when(notificationTypeRepository.existsById(notificationTypeId)).thenReturn(true);
        doNothing().when(notificationTypeRepository).deleteById(notificationTypeId);

        // When
        notificationTypeService.deleteNotificationType(notificationTypeId);

        // Then
        verify(notificationTypeRepository).existsById(notificationTypeId);
        verify(notificationTypeRepository).deleteById(notificationTypeId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existing notification type")
    void shouldThrowExceptionWhenDeletingNonExistingNotificationType() {
        // Given
        Long notificationTypeId = 999L;

        when(notificationTypeRepository.existsById(notificationTypeId)).thenReturn(false);

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> notificationTypeService.deleteNotificationType(notificationTypeId));

        verify(notificationTypeRepository).existsById(notificationTypeId);
        verify(notificationTypeRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should implement findById method correctly")
    void shouldImplementFindByIdMethodCorrectly() {
        // Given
        Long notificationTypeId = 1L;
        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.of(testNotificationType));

        // When
        NotificationType result = notificationTypeService.findById(notificationTypeId);

        // Then
        assertNotNull(result);
        assertEquals(testNotificationType, result);
        verify(notificationTypeRepository).findById(notificationTypeId);
    }

    @Test
    @DisplayName("Should throw exception in findById when notification type not found")
    void shouldThrowExceptionInFindByIdWhenNotificationTypeNotFound() {
        // Given
        Long notificationTypeId = 999L;
        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class,
            () -> notificationTypeService.findById(notificationTypeId));

        verify(notificationTypeRepository).findById(notificationTypeId);
    }

    @Test
    @DisplayName("Should implement findByIdAndStateId method correctly for entity without state")
    void shouldImplementFindByIdAndStateIdMethodCorrectlyForEntityWithoutState() {
        // Given
        Long notificationTypeId = 1L;
        Long stateId = 1L; // This should be ignored since NotificationType doesn't have state

        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.of(testNotificationType));

        // When
        NotificationType result = notificationTypeService.findByIdAndStateId(notificationTypeId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testNotificationType, result);
        verify(notificationTypeRepository).findById(notificationTypeId);
        // State ID should be ignored for entities without state relationship
    }

    @Test
    @DisplayName("Should handle null stateId in findByIdAndStateId method")
    void shouldHandleNullStateIdInFindByIdAndStateIdMethod() {
        // Given
        Long notificationTypeId = 1L;
        Long stateId = null;

        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.of(testNotificationType));

        // When
        NotificationType result = notificationTypeService.findByIdAndStateId(notificationTypeId, stateId);

        // Then
        assertNotNull(result);
        assertEquals(testNotificationType, result);
        verify(notificationTypeRepository).findById(notificationTypeId);
    }

    @Test
    @DisplayName("Should create notification type with null description")
    void shouldCreateNotificationTypeWithNullDescription() {
        // Given
        NotificationTypeDto notificationTypeDto = new NotificationTypeDto("WEBHOOK", null);

        NotificationType savedNotificationType = new NotificationType("WEBHOOK", null);
        savedNotificationType.setId(2L);
        savedNotificationType.setCreatedAt(LocalDateTime.now());
        savedNotificationType.setUpdatedAt(LocalDateTime.now());

        when(notificationTypeRepository.save(any(NotificationType.class))).thenReturn(savedNotificationType);

        // When
        NotificationTypeResponse result = notificationTypeService.createNotificationType(notificationTypeDto);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("WEBHOOK", result.type());
        assertNull(result.description());

        verify(notificationTypeRepository).save(any(NotificationType.class));
    }

    @Test
    @DisplayName("Should create notification type with empty description")
    void shouldCreateNotificationTypeWithEmptyDescription() {
        // Given
        NotificationTypeDto notificationTypeDto = new NotificationTypeDto("CUSTOM", "");

        NotificationType savedNotificationType = new NotificationType("CUSTOM", "");
        savedNotificationType.setId(3L);
        savedNotificationType.setCreatedAt(LocalDateTime.now());
        savedNotificationType.setUpdatedAt(LocalDateTime.now());

        when(notificationTypeRepository.save(any(NotificationType.class))).thenReturn(savedNotificationType);

        // When
        NotificationTypeResponse result = notificationTypeService.createNotificationType(notificationTypeDto);

        // Then
        assertNotNull(result);
        assertEquals(3L, result.id());
        assertEquals("CUSTOM", result.type());
        assertEquals("", result.description());

        verify(notificationTypeRepository).save(any(NotificationType.class));
    }

    @Test
    @DisplayName("Should update notification type fields correctly")
    void shouldUpdateNotificationTypeFieldsCorrectly() {
        // Given
        Long notificationTypeId = 1L;
        NotificationTypeDto updateDto = new NotificationTypeDto("UPDATED_TYPE", "Updated description");

        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.of(testNotificationType));

        // Capture the saved entity to verify field updates
        when(notificationTypeRepository.save(any(NotificationType.class))).thenAnswer(invocation -> {
            NotificationType saved = invocation.getArgument(0);
            assertEquals("UPDATED_TYPE", saved.getType());
            assertEquals("Updated description", saved.getDescription());
            return saved;
        });

        // When
        NotificationTypeResponse result = notificationTypeService.updateNotificationType(notificationTypeId, updateDto);

        // Then
        assertNotNull(result);
        verify(notificationTypeRepository).findById(notificationTypeId);
        verify(notificationTypeRepository).save(testNotificationType);
    }

    @Test
    @DisplayName("Should handle repository exception during creation")
    void shouldHandleRepositoryExceptionDuringCreation() {
        // Given
        NotificationTypeDto notificationTypeDto = new NotificationTypeDto("ERROR_TYPE", "Error description");

        when(notificationTypeRepository.save(any(NotificationType.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
            () -> notificationTypeService.createNotificationType(notificationTypeDto));

        verify(notificationTypeRepository).save(any(NotificationType.class));
    }

    @Test
    @DisplayName("Should handle repository exception during update")
    void shouldHandleRepositoryExceptionDuringUpdate() {
        // Given
        Long notificationTypeId = 1L;
        NotificationTypeDto notificationTypeDto = new NotificationTypeDto("ERROR_TYPE", "Error description");

        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.of(testNotificationType));
        when(notificationTypeRepository.save(any(NotificationType.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThrows(RuntimeException.class,
            () -> notificationTypeService.updateNotificationType(notificationTypeId, notificationTypeDto));

        verify(notificationTypeRepository).findById(notificationTypeId);
        verify(notificationTypeRepository).save(any(NotificationType.class));
    }

    @Test
    @DisplayName("Should handle repository exception during deletion")
    void shouldHandleRepositoryExceptionDuringDeletion() {
        // Given
        Long notificationTypeId = 1L;

        when(notificationTypeRepository.existsById(notificationTypeId)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(notificationTypeRepository).deleteById(notificationTypeId);

        // When & Then
        assertThrows(RuntimeException.class,
            () -> notificationTypeService.deleteNotificationType(notificationTypeId));

        verify(notificationTypeRepository).existsById(notificationTypeId);
        verify(notificationTypeRepository).deleteById(notificationTypeId);
    }

    @Test
    @DisplayName("Should verify mapping to response is correct")
    void shouldVerifyMappingToResponseIsCorrect() {
        // Given
        Long notificationTypeId = 1L;
        testNotificationType.setType("MAPPING_TEST");
        testNotificationType.setDescription("Test mapping description");
        
        LocalDateTime createdAt = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2023, 1, 2, 15, 30);
        testNotificationType.setCreatedAt(createdAt);
        testNotificationType.setUpdatedAt(updatedAt);

        when(notificationTypeRepository.findById(notificationTypeId)).thenReturn(Optional.of(testNotificationType));

        // When
        NotificationTypeResponse result = notificationTypeService.getNotificationTypeByIdResponse(notificationTypeId);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("MAPPING_TEST", result.type());
        assertEquals("Test mapping description", result.description());
        assertEquals(createdAt, result.createdAt());
        assertEquals(updatedAt, result.updatedAt());

        verify(notificationTypeRepository).findById(notificationTypeId);
    }
}