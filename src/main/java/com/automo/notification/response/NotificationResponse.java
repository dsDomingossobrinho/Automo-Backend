package com.automo.notification.response;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    Long senderId,
    String senderName,
    Long receiverId,
    String receiverName,
    String urlRedirect,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 