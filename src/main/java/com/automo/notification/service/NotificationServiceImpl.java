package com.automo.notification.service;

import com.automo.notification.dto.NotificationDto;
import com.automo.notification.entity.Notification;
import com.automo.notification.repository.NotificationRepository;
import com.automo.notification.response.NotificationResponse;
import com.automo.identifier.entity.Identifier;
import com.automo.identifier.repository.IdentifierRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import com.automo.user.entity.User;
import com.automo.user.repository.UserRepository;
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
    private final IdentifierRepository identifierRepository;
    private final StateRepository stateRepository;
    private final UserRepository userRepository;

    @Override
    public NotificationResponse createNotification(NotificationDto notificationDto) {
        Identifier sender = identifierRepository.findById(notificationDto.senderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender identifier with ID " + notificationDto.senderId() + " not found"));

        Identifier receiver = identifierRepository.findById(notificationDto.receiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver identifier with ID " + notificationDto.receiverId() + " not found"));

        State state = stateRepository.findById(notificationDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + notificationDto.stateId() + " not found"));

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
        
        Identifier sender = identifierRepository.findById(notificationDto.senderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender identifier with ID " + notificationDto.senderId() + " not found"));

        Identifier receiver = identifierRepository.findById(notificationDto.receiverId())
                .orElseThrow(() -> new EntityNotFoundException("Receiver identifier with ID " + notificationDto.receiverId() + " not found"));

        State state = stateRepository.findById(notificationDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + notificationDto.stateId() + " not found"));

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
        return notificationRepository.findAll().stream()
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
        if (!notificationRepository.existsById(id)) {
            throw new EntityNotFoundException("Notification with ID " + id + " not found");
        }
        notificationRepository.deleteById(id);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        // Buscar usuários para obter os nomes
        User senderUser = userRepository.findById(notification.getSender().getUserId())
                .orElse(null);
        User receiverUser = userRepository.findById(notification.getReceiver().getUserId())
                .orElse(null);
        
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
} 