package com.automo.messageCount.repository;

import com.automo.messageCount.entity.MessageCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageCountRepository extends JpaRepository<MessageCount, Long> {
    
    List<MessageCount> findByLeadId(Long leadId);
    
    List<MessageCount> findByStateId(Long stateId);
    
    List<MessageCount> findByLeadIdAndStateId(Long leadId, Long stateId);
} 