package com.automo.lead.service;

import com.automo.lead.dto.LeadDto;
import com.automo.lead.entity.Lead;
import com.automo.lead.response.LeadResponse;

import java.util.List;

public interface LeadService {

    LeadResponse createLead(LeadDto leadDto);

    LeadResponse updateLead(Long id, LeadDto leadDto);

    List<LeadResponse> getAllLeads();

    Lead getLeadById(Long id);

    LeadResponse getLeadByIdResponse(Long id);

    List<LeadResponse> getLeadsByState(Long stateId);

    List<LeadResponse> getLeadsByLeadType(Long leadTypeId);

    List<LeadResponse> getLeadsByIdentifier(Long identifierId);

    void deleteLead(Long id);
    
    /**
     * Busca Lead por ID - método obrigatório para comunicação entre services
     */
    Lead findById(Long id);
    
    /**
     * Busca Lead por ID e estado específico (state_id = 1 por padrão)
     */
    Lead findByIdAndStateId(Long id, Long stateId);
} 