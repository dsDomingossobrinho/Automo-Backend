package com.automo.agentAreas.service;

import com.automo.agentAreas.dto.AgentAreasDto;
import com.automo.agentAreas.entity.AgentAreas;
import com.automo.agentAreas.repository.AgentAreasRepository;
import com.automo.agentAreas.response.AgentAreasResponse;
import com.automo.agent.service.AgentService;
import com.automo.area.service.AreaService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
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
    private final AgentService agentService;
    private final AreaService areaService;
    private final StateService stateService;

    @Override
    public AgentAreasResponse createAgentAreas(AgentAreasDto agentAreasDto) {
        var agent = agentService.findById(agentAreasDto.agentId());

        var area = areaService.findById(agentAreasDto.areaId());

        var state = stateService.findById(agentAreasDto.stateId());

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
        
        var agent = agentService.findById(agentAreasDto.agentId());

        var area = areaService.findById(agentAreasDto.areaId());

        var state = stateService.findById(agentAreasDto.stateId());

        agentAreas.setAgent(agent);
        agentAreas.setArea(area);
        agentAreas.setState(state);
        
        var updatedAgentAreas = agentAreasRepository.save(agentAreas);
        return mapToResponse(updatedAgentAreas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentAreasResponse> getAllAgentAreas() {
        State eliminatedState = stateService.getEliminatedState();
        return agentAreasRepository.findAllWithAgentAreaAndState().stream()
                .filter(agentArea -> agentArea.getState() != null && !agentArea.getState().getId().equals(eliminatedState.getId()))
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
        var agentAreas = agentAreasRepository.findByIdWithAgentAreaAndState(id)
                .orElseThrow(() -> new EntityNotFoundException("AgentAreas with ID " + id + " not found"));
        return mapToResponse(agentAreas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentAreasResponse> getAgentAreasByAgent(Long agentId) {
        return agentAreasRepository.findByAgentIdWithAgentAreaAndState(agentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentAreasResponse> getAgentAreasByArea(Long areaId) {
        return agentAreasRepository.findByAreaIdWithAgentAreaAndState(areaId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentAreasResponse> getAgentAreasByState(Long stateId) {
        return agentAreasRepository.findByStateIdWithAgentAreaAndState(stateId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAgentAreas(Long id) {
        AgentAreas agentAreas = this.getAgentAreasById(id);
        
        // Set state to ELIMINATED for soft delete
        var eliminatedState = stateService.getEliminatedState();
        agentAreas.setState(eliminatedState);
        
        agentAreasRepository.save(agentAreas);
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
    
    @Override
    @Transactional(readOnly = true)
    public AgentAreas findById(Long id) {
        return agentAreasRepository.findByIdWithAgentAreaAndState(id)
                .orElseThrow(() -> new EntityNotFoundException("AgentAreas with ID " + id + " not found"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public AgentAreas findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        AgentAreas entity = agentAreasRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("AgentAreas with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("AgentAreas with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 