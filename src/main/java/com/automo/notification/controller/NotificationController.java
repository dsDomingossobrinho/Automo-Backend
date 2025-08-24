package com.automo.notification.controller;

import com.automo.notification.dto.NotificationDto;
import com.automo.notification.response.NotificationResponse;
import com.automo.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification management APIs")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(description = "List all notifications", summary = "Get all notifications")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @Operation(description = "Get notification by ID", summary = "Get a specific notification by ID")
    @ApiResponse(responseCode = "200", description = "Notification retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationByIdResponse(id));
    }

    @Operation(description = "Create new notification", summary = "Create a new notification")
    @ApiResponse(responseCode = "201", description = "Notification created successfully")
    @PostMapping
    public ResponseEntity<NotificationResponse> createNotification(@Valid @RequestBody NotificationDto notificationDto) {
        NotificationResponse response = notificationService.createNotification(notificationDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update notification", summary = "Update an existing notification")
    @ApiResponse(responseCode = "200", description = "Notification updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<NotificationResponse> updateNotification(@PathVariable Long id, @Valid @RequestBody NotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.updateNotification(id, notificationDto));
    }

    @Operation(description = "Delete notification", summary = "Delete a notification")
    @ApiResponse(responseCode = "204", description = "Notification deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "Get notifications by state", summary = "Get notifications filtered by state ID")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    @GetMapping("/state/{stateId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByState(@PathVariable Long stateId) {
        return ResponseEntity.ok(notificationService.getNotificationsByState(stateId));
    }

    @Operation(description = "Get notifications by sender", summary = "Get notifications filtered by sender ID")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsBySender(@PathVariable Long senderId) {
        return ResponseEntity.ok(notificationService.getNotificationsBySender(senderId));
    }

    @Operation(description = "Get notifications by receiver", summary = "Get notifications filtered by receiver ID")
    @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByReceiver(@PathVariable Long receiverId) {
        return ResponseEntity.ok(notificationService.getNotificationsByReceiver(receiverId));
    }
} 