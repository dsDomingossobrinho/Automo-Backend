package com.automo.notificationType.controller;

import com.automo.config.security.JwtUtils;
import com.automo.notificationType.dto.NotificationTypeDto;
import com.automo.notificationType.response.NotificationTypeResponse;
import com.automo.notificationType.service.NotificationTypeService;
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

@WebMvcTest(NotificationTypeController.class)
@ActiveProfiles("test")
@DisplayName("Tests for NotificationTypeController")
class NotificationTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationTypeService notificationTypeService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private NotificationTypeDto notificationTypeDto;
    private NotificationTypeResponse notificationTypeResponse;

    @BeforeEach
    void setUp() {
        notificationTypeDto = new NotificationTypeDto("EMAIL", "Email notification type");
        
        notificationTypeResponse = new NotificationTypeResponse(
            1L,
            "EMAIL",
            "Email notification type",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all notification types successfully")
    void shouldGetAllNotificationTypesSuccessfully() throws Exception {
        // Given
        NotificationTypeResponse type1 = new NotificationTypeResponse(
            1L, "EMAIL", "Email notifications", LocalDateTime.now(), LocalDateTime.now()
        );
        NotificationTypeResponse type2 = new NotificationTypeResponse(
            2L, "SMS", "SMS notifications", LocalDateTime.now(), LocalDateTime.now()
        );
        List<NotificationTypeResponse> notificationTypes = Arrays.asList(type1, type2);

        when(notificationTypeService.getAllNotificationTypes()).thenReturn(notificationTypes);

        // When & Then
        mockMvc.perform(get("/notification-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("EMAIL"))
                .andExpect(jsonPath("$[0].description").value("Email notifications"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].type").value("SMS"))
                .andExpect(jsonPath("$[1].description").value("SMS notifications"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get notification type by id successfully")
    void shouldGetNotificationTypeByIdSuccessfully() throws Exception {
        // Given
        when(notificationTypeService.getNotificationTypeByIdResponse(1L)).thenReturn(notificationTypeResponse);

        // When & Then
        mockMvc.perform(get("/notification-types/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("EMAIL"))
                .andExpect(jsonPath("$.description").value("Email notification type"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 404 when notification type not found")
    void shouldReturn404WhenNotificationTypeNotFound() throws Exception {
        // Given
        when(notificationTypeService.getNotificationTypeByIdResponse(999L))
                .thenThrow(new EntityNotFoundException("NotificationType with ID 999 not found"));

        // When & Then
        mockMvc.perform(get("/notification-types/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create notification type successfully")
    void shouldCreateNotificationTypeSuccessfully() throws Exception {
        // Given
        when(notificationTypeService.createNotificationType(any(NotificationTypeDto.class)))
                .thenReturn(notificationTypeResponse);

        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationTypeDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("EMAIL"))
                .andExpect(jsonPath("$.description").value("Email notification type"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update notification type successfully")
    void shouldUpdateNotificationTypeSuccessfully() throws Exception {
        // Given
        NotificationTypeResponse updatedResponse = new NotificationTypeResponse(
            1L, "PUSH", "Push notification type", LocalDateTime.now(), LocalDateTime.now()
        );
        when(notificationTypeService.updateNotificationType(eq(1L), any(NotificationTypeDto.class)))
                .thenReturn(updatedResponse);

        NotificationTypeDto updateDto = new NotificationTypeDto("PUSH", "Push notification type");

        // When & Then
        mockMvc.perform(put("/notification-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("PUSH"))
                .andExpect(jsonPath("$.description").value("Push notification type"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete notification type successfully")
    void shouldDeleteNotificationTypeSuccessfully() throws Exception {
        // Given
        doNothing().when(notificationTypeService).deleteNotificationType(1L);

        // When & Then
        mockMvc.perform(delete("/notification-types/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(notificationTypeService).deleteNotificationType(1L);
    }

    @Test
    @DisplayName("Should return 401 for unauthorized access")
    void shouldReturn401ForUnauthorizedAccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/notification-types"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden create operation")
    void shouldReturn403ForForbiddenCreateOperation() throws Exception {
        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationTypeDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden update operation")
    void shouldReturn403ForForbiddenUpdateOperation() throws Exception {
        // When & Then
        mockMvc.perform(put("/notification-types/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationTypeDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden delete operation")
    void shouldReturn403ForForbiddenDeleteOperation() throws Exception {
        // When & Then
        mockMvc.perform(delete("/notification-types/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid notification type data")
    void shouldReturn400ForInvalidNotificationTypeData() throws Exception {
        // Given
        NotificationTypeDto invalidNotificationType = new NotificationTypeDto(null, "Description");

        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNotificationType))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for blank type")
    void shouldReturn400ForBlankType() throws Exception {
        // Given
        NotificationTypeDto invalidNotificationType = new NotificationTypeDto("", "Description");

        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNotificationType))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create notification type with null description")
    void shouldCreateNotificationTypeWithNullDescription() throws Exception {
        // Given
        NotificationTypeDto validNotificationType = new NotificationTypeDto("WEBHOOK", null);
        NotificationTypeResponse responseWithNullDescription = new NotificationTypeResponse(
            1L, "WEBHOOK", null, LocalDateTime.now(), LocalDateTime.now()
        );

        when(notificationTypeService.createNotificationType(any(NotificationTypeDto.class)))
                .thenReturn(responseWithNullDescription);

        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validNotificationType))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("WEBHOOK"))
                .andExpect(jsonPath("$.description").isEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create notification type with empty description")
    void shouldCreateNotificationTypeWithEmptyDescription() throws Exception {
        // Given
        NotificationTypeDto validNotificationType = new NotificationTypeDto("CUSTOM", "");
        NotificationTypeResponse responseWithEmptyDescription = new NotificationTypeResponse(
            1L, "CUSTOM", "", LocalDateTime.now(), LocalDateTime.now()
        );

        when(notificationTypeService.createNotificationType(any(NotificationTypeDto.class)))
                .thenReturn(responseWithEmptyDescription);

        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validNotificationType))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("CUSTOM"))
                .andExpect(jsonPath("$.description").value(""));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("Should allow agent to view notification types")
    void shouldAllowAgentToViewNotificationTypes() throws Exception {
        // Given
        when(notificationTypeService.getAllNotificationTypes()).thenReturn(Arrays.asList(notificationTypeResponse));

        // When & Then
        mockMvc.perform(get("/notification-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = "AGENT")
    @DisplayName("Should forbid agent to create notification types")
    void shouldForbidAgentToCreateNotificationTypes() throws Exception {
        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationTypeDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service exception during creation")
    void shouldHandleServiceExceptionDuringCreation() throws Exception {
        // Given
        when(notificationTypeService.createNotificationType(any(NotificationTypeDto.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationTypeDto))
                .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service exception during update")
    void shouldHandleServiceExceptionDuringUpdate() throws Exception {
        // Given
        when(notificationTypeService.updateNotificationType(eq(999L), any(NotificationTypeDto.class)))
                .thenThrow(new EntityNotFoundException("NotificationType not found"));

        // When & Then
        mockMvc.perform(put("/notification-types/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notificationTypeDto))
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service exception during deletion")
    void shouldHandleServiceExceptionDuringDeletion() throws Exception {
        // Given
        doThrow(new EntityNotFoundException("NotificationType not found"))
                .when(notificationTypeService).deleteNotificationType(999L);

        // When & Then
        mockMvc.perform(delete("/notification-types/999")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return empty list when no notification types exist")
    void shouldReturnEmptyListWhenNoNotificationTypesExist() throws Exception {
        // Given
        when(notificationTypeService.getAllNotificationTypes()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/notification-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle whitespace-only type validation")
    void shouldHandleWhitespaceOnlyTypeValidation() throws Exception {
        // Given
        NotificationTypeDto invalidNotificationType = new NotificationTypeDto("   ", "Description");

        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidNotificationType))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should accept special characters in type")
    void shouldAcceptSpecialCharactersInType() throws Exception {
        // Given
        NotificationTypeDto specialCharType = new NotificationTypeDto("CUSTOM_TYPE_123", "Custom type with underscore and numbers");
        NotificationTypeResponse specialCharResponse = new NotificationTypeResponse(
            1L, "CUSTOM_TYPE_123", "Custom type with underscore and numbers", LocalDateTime.now(), LocalDateTime.now()
        );

        when(notificationTypeService.createNotificationType(any(NotificationTypeDto.class)))
                .thenReturn(specialCharResponse);

        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(specialCharType))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("CUSTOM_TYPE_123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should accept unicode characters in type and description")
    void shouldAcceptUnicodeCharactersInTypeAndDescription() throws Exception {
        // Given
        NotificationTypeDto unicodeType = new NotificationTypeDto("UNICODE_测试", "Description with unicode: 测试, éñ, 🔔");
        NotificationTypeResponse unicodeResponse = new NotificationTypeResponse(
            1L, "UNICODE_测试", "Description with unicode: 测试, éñ, 🔔", LocalDateTime.now(), LocalDateTime.now()
        );

        when(notificationTypeService.createNotificationType(any(NotificationTypeDto.class)))
                .thenReturn(unicodeResponse);

        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(unicodeType))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type").value("UNICODE_测试"))
                .andExpect(jsonPath("$.description").value("Description with unicode: 测试, éñ, 🔔"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle malformed JSON gracefully")
    void shouldHandleMalformedJsonGracefully() throws Exception {
        // Given
        String malformedJson = "{\"type\": \"EMAIL\", \"description\": }";

        // When & Then
        mockMvc.perform(post("/notification-types")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle missing content type")
    void shouldHandleMissingContentType() throws Exception {
        // When & Then
        mockMvc.perform(post("/notification-types")
                .content(objectMapper.writeValueAsString(notificationTypeDto))
                .with(csrf()))
                .andExpect(status().isUnsupportedMediaType());
    }
}