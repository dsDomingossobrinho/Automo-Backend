package com.automo.agent.service;

import com.automo.agent.dto.AgentDto;
import com.automo.agent.entity.Agent;
import com.automo.agent.repository.AgentRepository;
import com.automo.agent.response.AgentResponse;
import com.automo.agentAreas.repository.AgentAreasRepository;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final StateRepository stateRepository;
    private final AgentAreasRepository agentAreasRepository;

    @Override
    public AgentResponse createAgent(AgentDto agentDto) {
        State state = stateRepository.findById(agentDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + agentDto.stateId() + " not found"));

        Agent agent = new Agent();
        agent.setName(agentDto.name());
        agent.setDescription(agentDto.description());
        agent.setLocation(agentDto.location());
        agent.setRestrictions(agentDto.restrictions());
        agent.setActivityFlow(agentDto.activityFlow());
        agent.setState(state);
        
        Agent savedAgent = agentRepository.save(agent);
        return mapToResponse(savedAgent);
    }

    @Override
    public AgentResponse updateAgent(Long id, AgentDto agentDto) {
        Agent agent = this.getAgentById(id);
        
        State state = stateRepository.findById(agentDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + agentDto.stateId() + " not found"));

        agent.setName(agentDto.name());
        agent.setDescription(agentDto.description());
        agent.setLocation(agentDto.location());
        agent.setRestrictions(agentDto.restrictions());
        agent.setActivityFlow(agentDto.activityFlow());
        agent.setState(state);
        
        Agent updatedAgent = agentRepository.save(agent);
        return mapToResponse(updatedAgent);
    }

    @Override
    public List<AgentResponse> getAllAgents() {
        return agentRepository.findAllWithState().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Agent getAgentById(Long id) {
        return agentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agent with ID " + id + " not found"));
    }

    @Override
    public AgentResponse getAgentByIdResponse(Long id) {
        Agent agent = agentRepository.findByIdWithState(id)
                .orElseThrow(() -> new EntityNotFoundException("Agent with ID " + id + " not found"));
        return mapToResponse(agent);
    }

    @Override
    public List<AgentResponse> getAgentsByState(Long stateId) {
        return agentRepository.findByStateIdWithState(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AgentResponse> getAgentsByArea(Long areaId) {
        return agentAreasRepository.findByAreaId(areaId).stream()
                .map(agentArea -> mapToResponse(agentArea.getAgent()))
                .collect(Collectors.toList());
    }

    @Override
    public List<AgentResponse> searchAgentsByName(String name) {
        return agentRepository.findByNameContainingIgnoreCaseWithState(name).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAgent(Long id) {
        if (!agentRepository.existsById(id)) {
            throw new EntityNotFoundException("Agent with ID " + id + " not found");
        }
        agentRepository.deleteById(id);
    }

    private AgentResponse mapToResponse(Agent agent) {
        return new AgentResponse(
                agent.getId(),
                agent.getName(),
                agent.getDescription(),
                agent.getLocation(),
                agent.getRestrictions(),
                agent.getActivityFlow(),
                agent.getState().getId(),
                agent.getState().getState(),
                agent.getCreatedAt(),
                agent.getUpdatedAt()
        );
    }
} 