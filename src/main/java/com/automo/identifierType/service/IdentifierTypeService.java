package com.automo.identifierType.service;

import com.automo.identifierType.dto.IdentifierTypeDto;
import com.automo.identifierType.entity.IdentifierType;
import com.automo.identifierType.response.IdentifierTypeResponse;

import java.util.List;

public interface IdentifierTypeService {

    IdentifierTypeResponse createIdentifierType(IdentifierTypeDto identifierTypeDto);

    IdentifierTypeResponse updateIdentifierType(Long id, IdentifierTypeDto identifierTypeDto);

    List<IdentifierTypeResponse> getAllIdentifierTypes();

    IdentifierType getIdentifierTypeById(Long id);

    IdentifierTypeResponse getIdentifierTypeByIdResponse(Long id);

    void deleteIdentifierType(Long id);
} 