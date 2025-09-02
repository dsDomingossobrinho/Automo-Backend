package com.automo.organizationType.service;

import com.automo.organizationType.dto.OrganizationTypeDto;
import com.automo.organizationType.entity.OrganizationType;
import com.automo.organizationType.response.OrganizationTypeResponse;

import java.util.List;

public interface OrganizationTypeService {

    OrganizationTypeResponse createOrganizationType(OrganizationTypeDto organizationTypeDto);

    OrganizationTypeResponse updateOrganizationType(Long id, OrganizationTypeDto organizationTypeDto);

    List<OrganizationTypeResponse> getAllOrganizationTypes();

    OrganizationType getOrganizationTypeById(Long id);

    OrganizationTypeResponse getOrganizationTypeByIdResponse(Long id);

    void deleteOrganizationType(Long id);
    
    /**
     * Busca OrganizationType por ID - método obrigatório para comunicação entre services
     */
    OrganizationType findById(Long id);
    
    /**
     * Busca OrganizationType por ID e estado específico (state_id = 1 por padrão)
     */
    OrganizationType findByIdAndStateId(Long id, Long stateId);
} 