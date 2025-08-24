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
} 