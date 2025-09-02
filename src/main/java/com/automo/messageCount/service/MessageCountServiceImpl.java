package com.automo.messageCount.service;

import com.automo.lead.entity.Lead;
import com.automo.lead.service.LeadService;
import com.automo.messageCount.dto.MessageCountDto;
import com.automo.messageCount.entity.MessageCount;
import com.automo.messageCount.repository.MessageCountRepository;
import com.automo.messageCount.response.MessageCountResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageCountServiceImpl implements MessageCountService {

    private final MessageCountRepository messageCountRepository;
    private final LeadService leadService;
    private final StateService stateService;

    @Override
    public MessageCountResponse createMessageCount(MessageCountDto messageCountDto) {
        Lead lead = leadService.findById(messageCountDto.leadId());

        State state = stateService.findById(messageCountDto.stateId());

        MessageCount messageCount = new MessageCount();
        messageCount.setLead(lead);
        messageCount.setMessageCount(messageCountDto.messageCount());
        messageCount.setState(state);

        MessageCount savedMessageCount = messageCountRepository.save(messageCount);
        return mapToResponse(savedMessageCount);
    }

    @Override
    public MessageCountResponse updateMessageCount(Long id, MessageCountDto messageCountDto) {
        MessageCount messageCount = this.getMessageCountById(id);

        Lead lead = leadService.findById(messageCountDto.leadId());

        State state = stateService.findById(messageCountDto.stateId());

        messageCount.setLead(lead);
        messageCount.setMessageCount(messageCountDto.messageCount());
        messageCount.setState(state);

        MessageCount updatedMessageCount = messageCountRepository.save(messageCount);
        return mapToResponse(updatedMessageCount);
    }

    @Override
    public List<MessageCountResponse> getAllMessageCounts() {
        return messageCountRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public MessageCount getMessageCountById(Long id) {
        return messageCountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("MessageCount with ID " + id + " not found"));
    }

    @Override
    public MessageCountResponse getMessageCountByIdResponse(Long id) {
        MessageCount messageCount = this.getMessageCountById(id);
        return mapToResponse(messageCount);
    }

    @Override
    public List<MessageCountResponse> getMessageCountsByLead(Long leadId) {
        return messageCountRepository.findByLeadId(leadId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageCountResponse> getMessageCountsByState(Long stateId) {
        return messageCountRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMessageCount(Long id) {
        if (!messageCountRepository.existsById(id)) {
            throw new EntityNotFoundException("MessageCount with ID " + id + " not found");
        }
        messageCountRepository.deleteById(id);
    }

    private MessageCountResponse mapToResponse(MessageCount messageCount) {
        return new MessageCountResponse(
                messageCount.getId(),
                messageCount.getLead() != null ? messageCount.getLead().getId() : null,
                messageCount.getLead() != null ? messageCount.getLead().getName() : null,
                messageCount.getMessageCount(),
                messageCount.getState().getId(),
                messageCount.getState().getState(),
                messageCount.getCreatedAt(),
                messageCount.getUpdatedAt()
        );
    }

    @Override
    public MessageCount findById(Long id) {
        return messageCountRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("MessageCount with ID " + id + " not found"));
    }

    @Override
    public MessageCount findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        MessageCount entity = messageCountRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("MessageCount with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("MessageCount with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 