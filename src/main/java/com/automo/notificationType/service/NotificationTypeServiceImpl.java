package com.automo.notificationType.service;

import com.automo.notificationType.dto.NotificationTypeDto;
import com.automo.notificationType.entity.NotificationType;
import com.automo.notificationType.repository.NotificationTypeRepository;
import com.automo.notificationType.response.NotificationTypeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationTypeServiceImpl implements NotificationTypeService {

    private final NotificationTypeRepository notificationTypeRepository;

    @Override
    public NotificationTypeResponse createNotificationType(NotificationTypeDto notificationTypeDto) {
        NotificationType notificationType = new NotificationType();
        notificationType.setType(notificationTypeDto.type());
        notificationType.setDescription(notificationTypeDto.description());
        
        NotificationType savedNotificationType = notificationTypeRepository.save(notificationType);
        return mapToResponse(savedNotificationType);
    }

    @Override
    public NotificationTypeResponse updateNotificationType(Long id, NotificationTypeDto notificationTypeDto) {
        NotificationType notificationType = this.getNotificationTypeById(id);
        
        notificationType.setType(notificationTypeDto.type());
        notificationType.setDescription(notificationTypeDto.description());
        
        NotificationType updatedNotificationType = notificationTypeRepository.save(notificationType);
        return mapToResponse(updatedNotificationType);
    }

    @Override
    public List<NotificationTypeResponse> getAllNotificationTypes() {
        return notificationTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationType getNotificationTypeById(Long id) {
        return notificationTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NotificationType with ID " + id + " not found"));
    }

    @Override
    public NotificationTypeResponse getNotificationTypeByIdResponse(Long id) {
        NotificationType notificationType = this.getNotificationTypeById(id);
        return mapToResponse(notificationType);
    }

    @Override
    public void deleteNotificationType(Long id) {
        if (!notificationTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("NotificationType with ID " + id + " not found");
        }
        notificationTypeRepository.deleteById(id);
    }

    private NotificationTypeResponse mapToResponse(NotificationType notificationType) {
        return new NotificationTypeResponse(
                notificationType.getId(),
                notificationType.getType(),
                notificationType.getDescription(),
                notificationType.getCreatedAt(),
                notificationType.getUpdatedAt()
        );
    }

    @Override
    public NotificationType findById(Long id) {
        return notificationTypeRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("NotificationType with ID " + id + " not found"));
    }

    @Override
    public NotificationType findByIdAndStateId(Long id, Long stateId) {
        NotificationType entity = notificationTypeRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("NotificationType with ID " + id + " not found"));
        
        // For entities without state relationship, return the entity regardless of stateId
        return entity;
    }
} 