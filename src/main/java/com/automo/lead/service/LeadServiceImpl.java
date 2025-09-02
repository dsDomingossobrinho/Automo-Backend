package com.automo.lead.service;

import com.automo.lead.dto.LeadDto;
import com.automo.lead.entity.Lead;
import com.automo.lead.repository.LeadRepository;
import com.automo.lead.response.LeadResponse;
import com.automo.leadType.entity.LeadType;
import com.automo.leadType.service.LeadTypeService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadTypeService leadTypeService;
    private final StateService stateService;

    @Override
    public LeadResponse createLead(LeadDto leadDto) {
        LeadType leadType = leadTypeService.findById(leadDto.leadTypeId());

        State state = stateService.findById(leadDto.stateId());

        Lead lead = new Lead();
        lead.setName(leadDto.name());
        lead.setEmail(leadDto.email());
        lead.setContact(leadDto.contact());
        lead.setZone(leadDto.zone());
        lead.setLeadType(leadType);
        lead.setState(state);
        
        Lead savedLead = leadRepository.save(lead);
        return mapToResponse(savedLead);
    }

    @Override
    public LeadResponse updateLead(Long id, LeadDto leadDto) {
        Lead lead = this.getLeadById(id);
        
        LeadType leadType = leadTypeService.findById(leadDto.leadTypeId());

        State state = stateService.findById(leadDto.stateId());

        lead.setName(leadDto.name());
        lead.setEmail(leadDto.email());
        lead.setContact(leadDto.contact());
        lead.setZone(leadDto.zone());
        lead.setLeadType(leadType);
        lead.setState(state);
        
        Lead updatedLead = leadRepository.save(lead);
        return mapToResponse(updatedLead);
    }

    @Override
    public List<LeadResponse> getAllLeads() {
        State eliminatedState = stateService.getEliminatedState();
        return leadRepository.findAll().stream()
                .filter(lead -> lead.getState() != null && !lead.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Lead getLeadById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lead with ID " + id + " not found"));
    }

    @Override
    public LeadResponse getLeadByIdResponse(Long id) {
        Lead lead = this.getLeadById(id);
        return mapToResponse(lead);
    }

    @Override
    public List<LeadResponse> getLeadsByState(Long stateId) {
        return leadRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<LeadResponse> getLeadsByLeadType(Long leadTypeId) {
        State eliminatedState = stateService.getEliminatedState();
        return leadRepository.findByLeadTypeId(leadTypeId).stream()
                .filter(lead -> lead.getState() != null && !lead.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<LeadResponse> getLeadsByIdentifier(Long identifierId) {
        State eliminatedState = stateService.getEliminatedState();
        return leadRepository.findByIdentifierId(identifierId).stream()
                .filter(lead -> lead.getState() != null && !lead.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteLead(Long id) {
        Lead lead = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        lead.setState(eliminatedState);
        
        leadRepository.save(lead);
    }

    private LeadResponse mapToResponse(Lead lead) {
        return new LeadResponse(
                lead.getId(),
                lead.getIdentifier() != null ? lead.getIdentifier().getId() : null,
                lead.getName(),
                lead.getEmail(),
                lead.getContact(),
                lead.getZone(),
                lead.getLeadType().getId(),
                lead.getLeadType().getType(),
                lead.getState().getId(),
                lead.getState().getState(),
                lead.getCreatedAt(),
                lead.getUpdatedAt()
        );
    }

    @Override
    public Lead findById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Lead with ID " + id + " not found"));
    }

    @Override
    public Lead findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Lead entity = leadRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Lead with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Lead with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 