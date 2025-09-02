package com.automo.area.service;

import com.automo.area.dto.AreaDto;
import com.automo.area.entity.Area;
import com.automo.area.response.AreaResponse;

import java.util.List;

public interface AreaService {

    AreaResponse createArea(AreaDto areaDto);

    AreaResponse updateArea(Long id, AreaDto areaDto);

    List<AreaResponse> getAllAreas();

    Area getAreaById(Long id);

    AreaResponse getAreaByIdResponse(Long id);

    List<AreaResponse> getAreasByState(Long stateId);

    void deleteArea(Long id);
    
    /**
     * Busca Area por ID - método obrigatório para comunicação entre services
     */
    Area findById(Long id);
    
    /**
     * Busca Area por ID e estado específico (state_id = 1 por padrão)
     */
    Area findByIdAndStateId(Long id, Long stateId);
} 