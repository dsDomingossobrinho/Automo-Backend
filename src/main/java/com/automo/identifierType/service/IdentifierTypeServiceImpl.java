package com.automo.identifierType.service;

import com.automo.identifierType.dto.IdentifierTypeDto;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.identifierType.repository.IdentifierTypeRepository;
import com.automo.identifierType.response.IdentifierTypeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IdentifierTypeServiceImpl implements IdentifierTypeService {

    private final IdentifierTypeRepository identifierTypeRepository;

    @Override
    public IdentifierTypeResponse createIdentifierType(IdentifierTypeDto identifierTypeDto) {
        IdentifierType identifierType = new IdentifierType();
        identifierType.setType(identifierTypeDto.type());
        identifierType.setDescription(identifierTypeDto.description());
        
        IdentifierType savedIdentifierType = identifierTypeRepository.save(identifierType);
        return mapToResponse(savedIdentifierType);
    }

    @Override
    public IdentifierTypeResponse updateIdentifierType(Long id, IdentifierTypeDto identifierTypeDto) {
        IdentifierType identifierType = this.getIdentifierTypeById(id);
        
        identifierType.setType(identifierTypeDto.type());
        identifierType.setDescription(identifierTypeDto.description());
        
        IdentifierType updatedIdentifierType = identifierTypeRepository.save(identifierType);
        return mapToResponse(updatedIdentifierType);
    }

    @Override
    public List<IdentifierTypeResponse> getAllIdentifierTypes() {
        return identifierTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public IdentifierType getIdentifierTypeById(Long id) {
        return identifierTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("IdentifierType with ID " + id + " not found"));
    }

    @Override
    public IdentifierTypeResponse getIdentifierTypeByIdResponse(Long id) {
        IdentifierType identifierType = this.getIdentifierTypeById(id);
        return mapToResponse(identifierType);
    }

    @Override
    public void deleteIdentifierType(Long id) {
        if (!identifierTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("IdentifierType with ID " + id + " not found");
        }
        identifierTypeRepository.deleteById(id);
    }

    private IdentifierTypeResponse mapToResponse(IdentifierType identifierType) {
        return new IdentifierTypeResponse(
                identifierType.getId(),
                identifierType.getType(),
                identifierType.getDescription(),
                identifierType.getCreatedAt(),
                identifierType.getUpdatedAt()
        );
    }

    @Override
    public java.util.Optional<IdentifierType> findByType(String type) {
        return identifierTypeRepository.findByType(type);
    }

    @Override
    public IdentifierType findById(Long id) {
        return identifierTypeRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("IdentifierType with ID " + id + " not found"));
    }

    @Override
    public IdentifierType findByIdAndStateId(Long id, Long stateId) {
        IdentifierType entity = identifierTypeRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("IdentifierType with ID " + id + " not found"));
        
        // For entities without state relationship, return the entity regardless of stateId
        return entity;
    }
    
    @Override
    public IdentifierType createDefaultIdentifierType(String type, String description) {
        IdentifierType defaultType = new IdentifierType();
        defaultType.setType(type);
        defaultType.setDescription(description);
        return identifierTypeRepository.save(defaultType);
    }
} 