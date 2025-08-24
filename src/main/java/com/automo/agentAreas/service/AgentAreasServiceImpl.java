package com.automo.agentAreas.service;

import com.automo.agentAreas.dto.AgentAreasDto;
import com.automo.agentAreas.entity.AgentAreas;
import com.automo.agentAreas.repository.AgentAreasRepository;
import com.automo.agentAreas.response.AgentAreasResponse;
import com.automo.agent.repository.AgentRepository;
import com.automo.area.repository.AreaRepository;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AgentAreasServiceImpl implements AgentAreasService {

    private final AgentAreasRepository agentAreasRepository;
    private final AgentRepository agentRepository;
    private final AreaRepository areaRepository;
    private final StateRepository stateRepository;

    @Override
    public AgentAreasResponse createAgentAreas(AgentAreasDto agentAreasDto) {
        var agent = agentRepository.findById(agentAreasDto.agentId())
                .orElseThrow(() -> new EntityNotFoundException("Agent with ID " + agentAreasDto.agentId() + " not found"));

        var area = areaRepository.findById(agentAreasDto.areaId())
                .orElseThrow(() -> new EntityNotFoundException("Area with ID " + agentAreasDto.areaId() + " not found"));

        var state = stateRepository.findById(agentAreasDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + agentAreasDto.stateId() + " not found"));

        // Verificar se a associação já existe
        if (agentAreasRepository.existsByAgentIdAndAreaId(agentAreasDto.agentId(), agentAreasDto.areaId())) {
            throw new IllegalArgumentException("Agent is already associated with this area");
        }

        var agentAreas = new AgentAreas();
        agentAreas.setAgent(agent);
        agentAreas.setArea(area);
        agentAreas.setState(state);
        
        var savedAgentAreas = agentAreasRepository.save(agentAreas);
        return mapToResponse(savedAgentAreas);
    }

    @Override
    public AgentAreasResponse updateAgentAreas(Long id, AgentAreasDto agentAreasDto) {
        var agentAreas = this.getAgentAreasById(id);
        
        var agent = agentRepository.findById(agentAreasDto.agentId())
                .orElseThrow(() -> new EntityNotFoundException("Agent with ID " + agentAreasDto.agentId() + " not found"));

        var area = areaRepository.findById(agentAreasDto.areaId())
                .orElseThrow(() -> new EntityNotFoundException("Area with ID " + agentAreasDto.areaId() + " not found"));

        var state = stateRepository.findById(agentAreasDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + agentAreasDto.stateId() + " not found"));

        agentAreas.setAgent(agent);
        agentAreas.setArea(area);
        agentAreas.setState(state);
        
        var updatedAgentAreas = agentAreasRepository.save(agentAreas);
        return mapToResponse(updatedAgentAreas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentAreasResponse> getAllAgentAreas() {
        return agentAreasRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AgentAreas getAgentAreasById(Long id) {
        return agentAreasRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AgentAreas with ID " + id + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public AgentAreasResponse getAgentAreasByIdResponse(Long id) {
        var agentAreas = this.getAgentAreasById(id);
        return mapToResponse(agentAreas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentAreasResponse> getAgentAreasByAgent(Long agentId) {
        return agentAreasRepository.findByAgentId(agentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentAreasResponse> getAgentAreasByArea(Long areaId) {
        return agentAreasRepository.findByAreaId(areaId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentAreasResponse> getAgentAreasByState(Long stateId) {
        return agentAreasRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAgentAreas(Long id) {
        if (!agentAreasRepository.existsById(id)) {
            throw new EntityNotFoundException("AgentAreas with ID " + id + " not found");
        }
        agentAreasRepository.deleteById(id);
    }

    private AgentAreasResponse mapToResponse(AgentAreas agentAreas) {
        return new AgentAreasResponse(
                agentAreas.getId(),
                agentAreas.getAgent().getId(),
                agentAreas.getAgent().getName(),
                agentAreas.getArea().getId(),
                agentAreas.getArea().getArea(),
                agentAreas.getState().getId(),
                agentAreas.getState().getState(),
                agentAreas.getCreatedAt(),
                agentAreas.getUpdatedAt()
        );
    }
} 