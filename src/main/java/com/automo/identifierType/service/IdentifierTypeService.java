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
    
    /**
     * Busca IdentifierType por tipo - método obrigatório para comunicação entre services
     */
    java.util.Optional<IdentifierType> findByType(String type);
    
    /**
     * Busca IdentifierType por ID - método obrigatório para comunicação entre services
     */
    IdentifierType findById(Long id);
    
    /**
     * Busca IdentifierType por ID e estado específico (state_id = 1 por padrão)
     */
    IdentifierType findByIdAndStateId(Long id, Long stateId);
    
    /**
     * Cria um IdentifierType padrão com tipo e descrição
     */
    IdentifierType createDefaultIdentifierType(String type, String description);
} 