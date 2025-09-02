package com.automo.leadType.service;

import com.automo.leadType.dto.LeadTypeDto;
import com.automo.leadType.entity.LeadType;
import com.automo.leadType.response.LeadTypeResponse;

import java.util.List;

public interface LeadTypeService {

    LeadTypeResponse createLeadType(LeadTypeDto leadTypeDto);

    LeadTypeResponse updateLeadType(Long id, LeadTypeDto leadTypeDto);

    List<LeadTypeResponse> getAllLeadTypes();

    LeadType getLeadTypeById(Long id);

    LeadTypeResponse getLeadTypeByIdResponse(Long id);

    void deleteLeadType(Long id);
    
    /**
     * Busca LeadType por ID - método obrigatório para comunicação entre services
     */
    LeadType findById(Long id);
    
    /**
     * Busca LeadType por ID e estado específico (state_id = 1 por padrão)
     */
    LeadType findByIdAndStateId(Long id, Long stateId);
} 