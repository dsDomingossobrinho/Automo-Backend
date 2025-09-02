package com.automo.leadType.service;

import com.automo.leadType.dto.LeadTypeDto;
import com.automo.leadType.entity.LeadType;
import com.automo.leadType.repository.LeadTypeRepository;
import com.automo.leadType.response.LeadTypeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadTypeServiceImpl implements LeadTypeService {

    private final LeadTypeRepository leadTypeRepository;

    @Override
    public LeadTypeResponse createLeadType(LeadTypeDto leadTypeDto) {
        LeadType leadType = new LeadType();
        leadType.setType(leadTypeDto.type());
        leadType.setDescription(leadTypeDto.description());
        
        LeadType savedLeadType = leadTypeRepository.save(leadType);
        return mapToResponse(savedLeadType);
    }

    @Override
    public LeadTypeResponse updateLeadType(Long id, LeadTypeDto leadTypeDto) {
        LeadType leadType = this.getLeadTypeById(id);
        
        leadType.setType(leadTypeDto.type());
        leadType.setDescription(leadTypeDto.description());
        
        LeadType updatedLeadType = leadTypeRepository.save(leadType);
        return mapToResponse(updatedLeadType);
    }

    @Override
    public List<LeadTypeResponse> getAllLeadTypes() {
        return leadTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LeadType getLeadTypeById(Long id) {
        return leadTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LeadType with ID " + id + " not found"));
    }

    @Override
    public LeadTypeResponse getLeadTypeByIdResponse(Long id) {
        LeadType leadType = this.getLeadTypeById(id);
        return mapToResponse(leadType);
    }

    @Override
    public void deleteLeadType(Long id) {
        if (!leadTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("LeadType with ID " + id + " not found");
        }
        leadTypeRepository.deleteById(id);
    }

    private LeadTypeResponse mapToResponse(LeadType leadType) {
        return new LeadTypeResponse(
                leadType.getId(),
                leadType.getType(),
                leadType.getDescription(),
                leadType.getCreatedAt(),
                leadType.getUpdatedAt()
        );
    }

    @Override
    public LeadType findById(Long id) {
        return leadTypeRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("LeadType with ID " + id + " not found"));
    }

    @Override
    public LeadType findByIdAndStateId(Long id, Long stateId) {
        LeadType entity = leadTypeRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("LeadType with ID " + id + " not found"));
        
        // For entities without state relationship, return the entity regardless of stateId
        return entity;
    }
} 