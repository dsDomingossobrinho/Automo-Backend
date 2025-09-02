package com.automo.agentAreas.service;

import com.automo.agentAreas.dto.AgentAreasDto;
import com.automo.agentAreas.entity.AgentAreas;
import com.automo.agentAreas.response.AgentAreasResponse;

import java.util.List;

public interface AgentAreasService {
    
    AgentAreasResponse createAgentAreas(AgentAreasDto agentAreasDto);
    
    AgentAreasResponse updateAgentAreas(Long id, AgentAreasDto agentAreasDto);
    
    List<AgentAreasResponse> getAllAgentAreas();
    
    AgentAreas getAgentAreasById(Long id);
    
    AgentAreasResponse getAgentAreasByIdResponse(Long id);
    
    List<AgentAreasResponse> getAgentAreasByAgent(Long agentId);
    
    List<AgentAreasResponse> getAgentAreasByArea(Long areaId);
    
    List<AgentAreasResponse> getAgentAreasByState(Long stateId);
    
    void deleteAgentAreas(Long id);
    
    /**
     * Busca AgentAreas por ID - método obrigatório para comunicação entre services
     */
    AgentAreas findById(Long id);
    
    /**
     * Busca AgentAreas por ID e estado específico (state_id = 1 por padrão)
     */
    AgentAreas findByIdAndStateId(Long id, Long stateId);
} 