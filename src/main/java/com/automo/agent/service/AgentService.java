package com.automo.agent.service;

import com.automo.agent.dto.AgentDto;
import com.automo.agent.entity.Agent;
import com.automo.agent.response.AgentResponse;

import java.util.List;

public interface AgentService {

    AgentResponse createAgent(AgentDto agentDto);

    AgentResponse updateAgent(Long id, AgentDto agentDto);

    List<AgentResponse> getAllAgents();

    Agent getAgentById(Long id);

    AgentResponse getAgentByIdResponse(Long id);

    List<AgentResponse> getAgentsByState(Long stateId);

    List<AgentResponse> getAgentsByArea(Long areaId);

    List<AgentResponse> searchAgentsByName(String name);

    void deleteAgent(Long id);
    
    /**
     * Busca Agent por ID - método obrigatório para comunicação entre services
     */
    Agent findById(Long id);
    
    /**
     * Busca Agent por ID e estado específico (state_id = 1 por padrão)
     */
    Agent findByIdAndStateId(Long id, Long stateId);
} 