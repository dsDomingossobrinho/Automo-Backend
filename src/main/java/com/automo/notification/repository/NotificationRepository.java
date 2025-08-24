package com.automo.notification.repository;

import com.automo.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByStateId(Long stateId);
    List<Notification> findBySenderId(Long senderId);
    List<Notification> findByReceiverId(Long receiverId);
} 