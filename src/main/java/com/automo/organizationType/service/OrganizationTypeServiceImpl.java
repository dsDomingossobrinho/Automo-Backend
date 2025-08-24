package com.automo.organizationType.service;

import com.automo.organizationType.dto.OrganizationTypeDto;
import com.automo.organizationType.entity.OrganizationType;
import com.automo.organizationType.repository.OrganizationTypeRepository;
import com.automo.organizationType.response.OrganizationTypeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationTypeServiceImpl implements OrganizationTypeService {

    private final OrganizationTypeRepository organizationTypeRepository;

    @Override
    public OrganizationTypeResponse createOrganizationType(OrganizationTypeDto organizationTypeDto) {
        OrganizationType organizationType = new OrganizationType();
        organizationType.setType(organizationTypeDto.type());
        organizationType.setDescription(organizationTypeDto.description());
        
        OrganizationType savedOrganizationType = organizationTypeRepository.save(organizationType);
        return mapToResponse(savedOrganizationType);
    }

    @Override
    public OrganizationTypeResponse updateOrganizationType(Long id, OrganizationTypeDto organizationTypeDto) {
        OrganizationType organizationType = this.getOrganizationTypeById(id);
        
        organizationType.setType(organizationTypeDto.type());
        organizationType.setDescription(organizationTypeDto.description());
        
        OrganizationType updatedOrganizationType = organizationTypeRepository.save(organizationType);
        return mapToResponse(updatedOrganizationType);
    }

    @Override
    public List<OrganizationTypeResponse> getAllOrganizationTypes() {
        return organizationTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrganizationType getOrganizationTypeById(Long id) {
        return organizationTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrganizationType with ID " + id + " not found"));
    }

    @Override
    public OrganizationTypeResponse getOrganizationTypeByIdResponse(Long id) {
        OrganizationType organizationType = this.getOrganizationTypeById(id);
        return mapToResponse(organizationType);
    }

    @Override
    public void deleteOrganizationType(Long id) {
        if (!organizationTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("OrganizationType with ID " + id + " not found");
        }
        organizationTypeRepository.deleteById(id);
    }

    private OrganizationTypeResponse mapToResponse(OrganizationType organizationType) {
        return new OrganizationTypeResponse(
                organizationType.getId(),
                organizationType.getType(),
                organizationType.getDescription(),
                organizationType.getCreatedAt(),
                organizationType.getUpdatedAt()
        );
    }
} 