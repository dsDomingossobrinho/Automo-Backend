package com.automo.notificationType.service;

import com.automo.notificationType.dto.NotificationTypeDto;
import com.automo.notificationType.entity.NotificationType;
import com.automo.notificationType.response.NotificationTypeResponse;

import java.util.List;

public interface NotificationTypeService {

    NotificationTypeResponse createNotificationType(NotificationTypeDto notificationTypeDto);

    NotificationTypeResponse updateNotificationType(Long id, NotificationTypeDto notificationTypeDto);

    List<NotificationTypeResponse> getAllNotificationTypes();

    NotificationType getNotificationTypeById(Long id);

    NotificationTypeResponse getNotificationTypeByIdResponse(Long id);

    void deleteNotificationType(Long id);
} 