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
} 