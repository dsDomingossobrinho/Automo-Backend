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
} 