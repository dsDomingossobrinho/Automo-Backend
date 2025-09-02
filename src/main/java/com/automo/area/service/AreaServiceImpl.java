package com.automo.area.service;

import com.automo.area.dto.AreaDto;
import com.automo.area.entity.Area;
import com.automo.area.repository.AreaRepository;
import com.automo.area.response.AreaResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;
    private final StateService stateService;

    @Override
    public AreaResponse createArea(AreaDto areaDto) {
        State state = stateService.findById(areaDto.stateId());

        Area area = new Area();
        area.setArea(areaDto.area());
        area.setDescription(areaDto.description());
        area.setState(state);
        
        Area savedArea = areaRepository.save(area);
        return mapToResponse(savedArea);
    }

    @Override
    public AreaResponse updateArea(Long id, AreaDto areaDto) {
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Area with ID " + id + " not found"));

        State state = stateService.findById(areaDto.stateId());

        area.setArea(areaDto.area());
        area.setDescription(areaDto.description());
        area.setState(state);
        
        Area updatedArea = areaRepository.save(area);
        return mapToResponse(updatedArea);
    }

    @Override
    public List<AreaResponse> getAllAreas() {
        State eliminatedState = stateService.getEliminatedState();
        return areaRepository.findAll().stream()
                .filter(area -> area.getState() != null && !area.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Area getAreaById(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Area with ID " + id + " not found"));
    }

    @Override
    public AreaResponse getAreaByIdResponse(Long id) {
        Area area = this.getAreaById(id);
        return mapToResponse(area);
    }

    @Override
    public List<AreaResponse> getAreasByState(Long stateId) {
        return areaRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteArea(Long id) {
        Area area = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        area.setState(eliminatedState);
        
        areaRepository.save(area);
    }

    private AreaResponse mapToResponse(Area area) {
        return new AreaResponse(
                area.getId(),
                area.getArea(),
                area.getDescription(),
                area.getState().getId(),
                area.getState().getState(),
                area.getCreatedAt(),
                area.getUpdatedAt()
        );
    }

    @Override
    public Area findById(Long id) {
        return areaRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Area with ID " + id + " not found"));
    }

    @Override
    public Area findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Area entity = areaRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Area with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Area with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 