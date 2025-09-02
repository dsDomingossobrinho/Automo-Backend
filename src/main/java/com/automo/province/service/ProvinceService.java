package com.automo.province.service;

import com.automo.province.dto.ProvinceDto;
import com.automo.province.entity.Province;
import com.automo.province.response.ProvinceResponse;

import java.util.List;

public interface ProvinceService {

    ProvinceResponse createProvince(ProvinceDto provinceDto);

    ProvinceResponse updateProvince(Long id, ProvinceDto provinceDto);

    List<ProvinceResponse> getAllProvinces();

    Province getProvinceById(Long id);

    ProvinceResponse getProvinceByIdResponse(Long id);

    List<ProvinceResponse> getProvincesByCountry(Long countryId);

    List<ProvinceResponse> getProvincesByState(Long stateId);

    void deleteProvince(Long id);
    
    /**
     * Busca Province por ID - método obrigatório para comunicação entre services
     */
    Province findById(Long id);
    
    /**
     * Busca Province por ID e estado específico (state_id = 1 por padrão)
     */
    Province findByIdAndStateId(Long id, Long stateId);
} 