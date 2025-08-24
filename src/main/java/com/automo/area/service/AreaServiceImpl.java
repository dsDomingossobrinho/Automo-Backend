package com.automo.area.service;

import com.automo.area.dto.AreaDto;
import com.automo.area.entity.Area;
import com.automo.area.repository.AreaRepository;
import com.automo.area.response.AreaResponse;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AreaServiceImpl implements AreaService {

    private final AreaRepository areaRepository;
    private final StateRepository stateRepository;

    @Override
    public AreaResponse createArea(AreaDto areaDto) {
        State state = stateRepository.findById(areaDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + areaDto.stateId() + " not found"));

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

        State state = stateRepository.findById(areaDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + areaDto.stateId() + " not found"));

        area.setArea(areaDto.area());
        area.setDescription(areaDto.description());
        area.setState(state);
        
        Area updatedArea = areaRepository.save(area);
        return mapToResponse(updatedArea);
    }

    @Override
    public List<AreaResponse> getAllAreas() {
        return areaRepository.findAllResponse();
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
        if (!areaRepository.existsById(id)) {
            throw new EntityNotFoundException("Area with ID " + id + " not found");
        }
        areaRepository.deleteById(id);
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
} 