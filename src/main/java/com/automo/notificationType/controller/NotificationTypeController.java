package com.automo.notificationType.controller;

import com.automo.notificationType.dto.NotificationTypeDto;
import com.automo.notificationType.response.NotificationTypeResponse;
import com.automo.notificationType.service.NotificationTypeService;
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
@RequestMapping("/notification-types")
@RequiredArgsConstructor
@Tag(name = "Notification Types", description = "Notification type management APIs")
@SecurityRequirement(name = "bearerAuth")
public class NotificationTypeController {

    private final NotificationTypeService notificationTypeService;

    @Operation(description = "List all notification types", summary = "Get all notification types")
    @ApiResponse(responseCode = "200", description = "Notification types retrieved successfully")
    @GetMapping
    public ResponseEntity<List<NotificationTypeResponse>> getAllNotificationTypes() {
        return ResponseEntity.ok(notificationTypeService.getAllNotificationTypes());
    }

    @Operation(description = "Get notification type by ID", summary = "Get a specific notification type by ID")
    @ApiResponse(responseCode = "200", description = "Notification type retrieved successfully")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationTypeResponse> getNotificationTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationTypeService.getNotificationTypeByIdResponse(id));
    }

    @Operation(description = "Create new notification type", summary = "Create a new notification type")
    @ApiResponse(responseCode = "201", description = "Notification type created successfully")
    @PostMapping
    public ResponseEntity<NotificationTypeResponse> createNotificationType(@Valid @RequestBody NotificationTypeDto notificationTypeDto) {
        NotificationTypeResponse response = notificationTypeService.createNotificationType(notificationTypeDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(description = "Update notification type", summary = "Update an existing notification type")
    @ApiResponse(responseCode = "200", description = "Notification type updated successfully")
    @PutMapping("/{id}")
    public ResponseEntity<NotificationTypeResponse> updateNotificationType(@PathVariable Long id, @Valid @RequestBody NotificationTypeDto notificationTypeDto) {
        return ResponseEntity.ok(notificationTypeService.updateNotificationType(id, notificationTypeDto));
    }

    @Operation(description = "Delete notification type", summary = "Delete a notification type")
    @ApiResponse(responseCode = "204", description = "Notification type deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificationType(@PathVariable Long id) {
        notificationTypeService.deleteNotificationType(id);
        return ResponseEntity.noContent().build();
    }
} 