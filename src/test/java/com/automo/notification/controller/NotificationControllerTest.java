package com.automo.notification.controller;

import com.automo.config.security.JwtUtils;
import com.automo.notification.dto.NotificationDto;
import com.automo.notification.response.NotificationResponse;
import com.automo.notification.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
@ActiveProfiles("test")
@DisplayName("Tests for NotificationController")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationDto notificationDto;
    private NotificationResponse notificationResponse;

    @BeforeEach
    void setUp() {
        notificationDto = new NotificationDto(1L, 2L, "https://example.com/test", 1L);
        
        notificationResponse = new NotificationResponse(
            1L,
            1L,
            "Sender User",
            2L,
            "Receiver User",
            "https://example.com/test",
            1L,
            "ACTIVE",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all notifications successfully")
    void shouldGetAllNotificationsSuccessfully() throws Exception {
        // Given
        NotificationResponse notification1 = new NotificationResponse(
            1L, 1L, "Sender1", 2L, "Receiver1", "https://test1.com", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );
        NotificationResponse notification2 = new NotificationResponse(
            2L, 1L, "Sender1", 3L, "Receiver2", "https://test2.com", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );
        List<NotificationResponse> notifications = Arrays.asList(notification1, notification2);

        when(notificationService.getAllNotifications()).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].senderName").value("Sender1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].receiverName").value("Receiver2"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get notification by id successfully")
    void shouldGetNotificationByIdSuccessfully() throws Exception {
        // Given
        when(notificationService.getNotificationByIdResponse(1L)).thenReturn(notificationResponse);

        // When & Then
        mockMvc.perform(get("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.senderId").value(1))
                .andExpect(jsonPath("$.senderName").value("Sender User"))
                .andExpect(jsonPath("$.receiverId").value(2))
                .andExpect(jsonPath("$.receiverName").value("Receiver User"))
                .andExpect(jsonPath("$.urlRedirect").value("https://example.com/test"))
                .andExpect(jsonPath("$.stateId").value(1))
                .andExpect(jsonPath("$.stateName").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 404 when notification not found")
    void shouldReturn404WhenNotificationNotFound() throws Exception {
        // Given
        when(notificationService.getNotificationByIdResponse(999L))
                .thenThrow(new EntityNotFoundException("Notification with ID 999 not found"));

        // When & Then
        mockMvc.perform(get("/notifications/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create notification successfully")
    void shouldCreateNotificationSuccessfully() throws Exception {
        // Given
        when(notificationService.createNotification(any(NotificationDto.class))).thenReturn(notificationResponse);

        // When & Then
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.senderId").value(1))
                .andExpect(jsonPath("$.receiverId").value(2))
                .andExpect(jsonPath("$.urlRedirect").value("https://example.com/test"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update notification successfully")
    void shouldUpdateNotificationSuccessfully() throws Exception {
        // Given
        NotificationResponse updatedResponse = new NotificationResponse(
            1L, 1L, "Sender User", 2L, "Receiver User", "https://example.com/updated", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );
        when(notificationService.updateNotification(eq(1L), any(NotificationDto.class))).thenReturn(updatedResponse);

        NotificationDto updateDto = new NotificationDto(1L, 2L, "https://example.com/updated", 1L);

        // When & Then
        mockMvc.perform(put("/notifications/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.urlRedirect").value("https://example.com/updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete notification successfully")
    void shouldDeleteNotificationSuccessfully() throws Exception {
        // Given
        doNothing().when(notificationService).deleteNotification(1L);

        // When & Then
        mockMvc.perform(delete("/notifications/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(notificationService).deleteNotification(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get notifications by state successfully")
    void shouldGetNotificationsByStateSuccessfully() throws Exception {
        // Given
        List<NotificationResponse> notifications = Arrays.asList(notificationResponse);
        when(notificationService.getNotificationsByState(1L)).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/notifications/state/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].stateId").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get notifications by sender successfully")
    void shouldGetNotificationsBySenderSuccessfully() throws Exception {
        // Given
        List<NotificationResponse> notifications = Arrays.asList(notificationResponse);
        when(notificationService.getNotificationsBySender(1L)).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/notifications/sender/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].senderId").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get notifications by receiver successfully")
    void shouldGetNotificationsByReceiverSuccessfully() throws Exception {
        // Given
        List<NotificationResponse> notifications = Arrays.asList(notificationResponse);
        when(notificationService.getNotificationsByReceiver(2L)).thenReturn(notifications);

        // When & Then
        mockMvc.perform(get("/notifications/receiver/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].receiverId").value(2));
    }

    @Test
    @DisplayName("Should return 401 for unauthorized access")
    void shouldReturn401ForUnauthorizedAccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden create operation")
    void shouldReturn403ForForbiddenCreateOperation() throws Exception {
        // When & Then
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden update operation")
    void shouldReturn403ForForbiddenUpdateOperation() throws Exception {
        // When & Then
        mockMvc.perform(put("/notifications/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden delete operation")
    void shouldReturn403ForForbiddenDeleteOperation() throws Exception {
        // When & Then
        mockMvc.perform(delete("/notifications/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid notification data")
    void shouldReturn400ForInvalidNotificationData() throws Exception {
        // Given
        NotificationDto invalidNotification = new NotificationDto(null, null, null, null);

        // When & Then
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNotification))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for missing sender ID")
    void shouldReturn400ForMissingSenderId() throws Exception {
        // Given
        NotificationDto invalidNotification = new NotificationDto(null, 2L, "https://test.com", 1L);

        // When & Then
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNotification))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for missing receiver ID")
    void shouldReturn400ForMissingReceiverId() throws Exception {
        // Given
        NotificationDto invalidNotification = new NotificationDto(1L, null, "https://test.com", 1L);

        // When & Then
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNotification))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for missing state ID")
    void shouldReturn400ForMissingStateId() throws Exception {
        // Given
        NotificationDto invalidNotification = new NotificationDto(1L, 2L, "https://test.com", null);

        // When & Then
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNotification))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create notification with null URL redirect")
    void shouldCreateNotificationWithNullUrlRedirect() throws Exception {
        // Given
        NotificationDto validNotification = new NotificationDto(1L, 2L, null, 1L);
        NotificationResponse responseWithNullUrl = new NotificationResponse(
            1L, 1L, "Sender User", 2L, "Receiver User", null, 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );

        when(notificationService.createNotification(any(NotificationDto.class))).thenReturn(responseWithNullUrl);

        // When & Then
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validNotification))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.urlRedirect").isEmpty());
    }

    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("Should allow agent to view notifications")
    void shouldAllowAgentToViewNotifications() throws Exception {
        // Given
        when(notificationService.getAllNotifications()).thenReturn(Arrays.asList(notificationResponse));

        // When & Then
        mockMvc.perform(get("/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("Should forbid agent to create notifications")
    void shouldForbidAgentToCreateNotifications() throws Exception {
        // When & Then
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service exception during creation")
    void shouldHandleServiceExceptionDuringCreation() throws Exception {
        // Given
        when(notificationService.createNotification(any(NotificationDto.class)))
                .thenThrow(new EntityNotFoundException("Identifier not found"));

        // When & Then
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationDto))
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service exception during update")
    void shouldHandleServiceExceptionDuringUpdate() throws Exception {
        // Given
        when(notificationService.updateNotification(eq(999L), any(NotificationDto.class)))
                .thenThrow(new EntityNotFoundException("Notification not found"));

        // When & Then
        mockMvc.perform(put("/notifications/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationDto))
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service exception during deletion")
    void shouldHandleServiceExceptionDuringDeletion() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("Notification not found"))
                .when(notificationService).deleteNotification(999L);

        // When & Then
        mockMvc.perform(delete("/notifications/999")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return empty list when no notifications found by state")
    void shouldReturnEmptyListWhenNoNotificationsFoundByState() throws Exception {
        // Given
        when(notificationService.getNotificationsByState(999L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/notifications/state/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return empty list when no notifications found by sender")
    void shouldReturnEmptyListWhenNoNotificationsFoundBySender() throws Exception {
        // Given
        when(notificationService.getNotificationsBySender(999L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/notifications/sender/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return empty list when no notifications found by receiver")
    void shouldReturnEmptyListWhenNoNotificationsFoundByReceiver() throws Exception {
        // Given
        when(notificationService.getNotificationsByReceiver(999L)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/notifications/receiver/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }
}