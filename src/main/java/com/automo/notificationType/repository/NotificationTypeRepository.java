package com.automo.notificationType.repository;

import com.automo.notificationType.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    Optional<NotificationType> findByType(String type);
    
    boolean existsByType(String type);
} 