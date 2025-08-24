package com.automo.notification.service;

import com.automo.notification.dto.NotificationDto;
import com.automo.notification.entity.Notification;
import com.automo.notification.response.NotificationResponse;

import java.util.List;

public interface NotificationService {

    NotificationResponse createNotification(NotificationDto notificationDto);

    NotificationResponse updateNotification(Long id, NotificationDto notificationDto);

    List<NotificationResponse> getAllNotifications();

    Notification getNotificationById(Long id);

    NotificationResponse getNotificationByIdResponse(Long id);

    List<NotificationResponse> getNotificationsByState(Long stateId);

    List<NotificationResponse> getNotificationsBySender(Long senderId);

    List<NotificationResponse> getNotificationsByReceiver(Long receiverId);

    void deleteNotification(Long id);
} 