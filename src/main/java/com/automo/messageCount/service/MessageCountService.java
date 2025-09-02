package com.automo.messageCount.service;

import com.automo.messageCount.dto.MessageCountDto;
import com.automo.messageCount.entity.MessageCount;
import com.automo.messageCount.response.MessageCountResponse;

import java.util.List;

public interface MessageCountService {
    
    MessageCountResponse createMessageCount(MessageCountDto messageCountDto);
    
    MessageCountResponse updateMessageCount(Long id, MessageCountDto messageCountDto);
    
    List<MessageCountResponse> getAllMessageCounts();
    
    MessageCount getMessageCountById(Long id);
    
    MessageCountResponse getMessageCountByIdResponse(Long id);
    
    List<MessageCountResponse> getMessageCountsByLead(Long leadId);
    
    List<MessageCountResponse> getMessageCountsByState(Long stateId);
    
    void deleteMessageCount(Long id);
    
    /**
     * Busca MessageCount por ID - método obrigatório para comunicação entre services
     */
    MessageCount findById(Long id);
    
    /**
     * Busca MessageCount por ID e estado específico (state_id = 1 por padrão)
     */
    MessageCount findByIdAndStateId(Long id, Long stateId);
} 