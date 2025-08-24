package com.automo.lead.service;

import com.automo.lead.dto.LeadDto;
import com.automo.lead.entity.Lead;
import com.automo.lead.repository.LeadRepository;
import com.automo.lead.response.LeadResponse;
import com.automo.leadType.entity.LeadType;
import com.automo.leadType.repository.LeadTypeRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadTypeRepository leadTypeRepository;
    private final StateRepository stateRepository;

    @Override
    public LeadResponse createLead(LeadDto leadDto) {
        LeadType leadType = leadTypeRepository.findById(leadDto.leadTypeId())
                .orElseThrow(() -> new EntityNotFoundException("LeadType with ID " + leadDto.leadTypeId() + " not found"));

        State state = stateRepository.findById(leadDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + leadDto.stateId() + " not found"));

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
        
        LeadType leadType = leadTypeRepository.findById(leadDto.leadTypeId())
                .orElseThrow(() -> new EntityNotFoundException("LeadType with ID " + leadDto.leadTypeId() + " not found"));

        State state = stateRepository.findById(leadDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + leadDto.stateId() + " not found"));

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
        return leadRepository.findAll().stream()
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
        return leadRepository.findByLeadTypeId(leadTypeId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<LeadResponse> getLeadsByIdentifier(Long identifierId) {
        return leadRepository.findByIdentifierId(identifierId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteLead(Long id) {
        if (!leadRepository.existsById(id)) {
            throw new EntityNotFoundException("Lead with ID " + id + " not found");
        }
        leadRepository.deleteById(id);
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
} 