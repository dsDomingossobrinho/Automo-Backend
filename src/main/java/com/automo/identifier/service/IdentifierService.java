package com.automo.identifier.service;

import com.automo.identifier.dto.IdentifierDto;
import com.automo.identifier.entity.Identifier;
import com.automo.identifier.response.IdentifierResponse;

import java.util.List;

public interface IdentifierService {

    IdentifierResponse createIdentifier(IdentifierDto identifierDto);

    IdentifierResponse updateIdentifier(Long id, IdentifierDto identifierDto);

    List<IdentifierResponse> getAllIdentifiers();

    Identifier getIdentifierById(Long id);

    IdentifierResponse getIdentifierByIdResponse(Long id);

    List<IdentifierResponse> getIdentifiersByState(Long stateId);

    List<IdentifierResponse> getIdentifiersByUser(Long userId);

    List<IdentifierResponse> getIdentifiersByType(Long identifierTypeId);

    void deleteIdentifier(Long id);

    // Novo método para criar identifier automaticamente
    void createIdentifierForEntity(Long userId, String entityType, Long stateId);
} 