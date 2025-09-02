package com.automo.notification.service;

import com.automo.notification.dto.NotificationDto;
import com.automo.notification.entity.Notification;
import com.automo.notification.repository.NotificationRepository;
import com.automo.notification.response.NotificationResponse;
import com.automo.identifier.entity.Identifier;
import com.automo.identifier.service.IdentifierService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.user.entity.User;
import com.automo.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final IdentifierService identifierService;
    private final StateService stateService;
    private final UserService userService;

    @Override
    public NotificationResponse createNotification(NotificationDto notificationDto) {
        Identifier sender = identifierService.findById(notificationDto.senderId());

        Identifier receiver = identifierService.findById(notificationDto.receiverId());

        State state = stateService.findById(notificationDto.stateId());

        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setUrlRedirect(notificationDto.urlRedirect());
        notification.setState(state);
        
        Notification savedNotification = notificationRepository.save(notification);
        return mapToResponse(savedNotification);
    }

    @Override
    public NotificationResponse updateNotification(Long id, NotificationDto notificationDto) {
        Notification notification = this.getNotificationById(id);
        
        Identifier sender = identifierService.findById(notificationDto.senderId());

        Identifier receiver = identifierService.findById(notificationDto.receiverId());

        State state = stateService.findById(notificationDto.stateId());

        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setUrlRedirect(notificationDto.urlRedirect());
        notification.setState(state);
        
        Notification updatedNotification = notificationRepository.save(notification);
        return mapToResponse(updatedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getAllNotifications() {
        State eliminatedState = stateService.getEliminatedState();
        return notificationRepository.findAll().stream()
                .filter(notification -> notification.getState() != null && !notification.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notification with ID " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationByIdResponse(Long id) {
        Notification notification = this.getNotificationById(id);
        return mapToResponse(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByState(Long stateId) {
        return notificationRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsBySender(Long senderId) {
        return notificationRepository.findBySenderId(senderId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsByReceiver(Long receiverId) {
        return notificationRepository.findByReceiverId(receiverId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNotification(Long id) {
        Notification notification = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        notification.setState(eliminatedState);
        
        notificationRepository.save(notification);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        // Buscar usuários para obter os nomes
        User senderUser = null;
        User receiverUser = null;
        try {
            senderUser = userService.findById(notification.getSender().getUserId());
        } catch (Exception e) {
            // User not found, keep null
        }
        try {
            receiverUser = userService.findById(notification.getReceiver().getUserId());
        } catch (Exception e) {
            // User not found, keep null
        }
        
        return new NotificationResponse(
                notification.getId(),
                notification.getSender().getId(),
                senderUser != null ? senderUser.getName() : null, // Usando o nome do usuário do identifier
                notification.getReceiver().getId(),
                receiverUser != null ? receiverUser.getName() : null, // Usando o nome do usuário do identifier
                notification.getUrlRedirect(),
                notification.getState().getId(),
                notification.getState().getState(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }

    @Override
    public Notification findById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Notification with ID " + id + " not found"));
    }

    @Override
    public Notification findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Notification entity = notificationRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Notification with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Notification with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 