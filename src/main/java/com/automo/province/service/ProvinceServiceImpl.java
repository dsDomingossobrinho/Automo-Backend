package com.automo.province.service;

import com.automo.province.dto.ProvinceDto;
import com.automo.province.entity.Province;
import com.automo.province.repository.ProvinceRepository;
import com.automo.province.response.ProvinceResponse;
import com.automo.country.entity.Country;
import com.automo.country.service.CountryService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;
    private final CountryService countryService;
    private final StateService stateService;

    @Override
    public ProvinceResponse createProvince(ProvinceDto provinceDto) {
        Country country = countryService.findById(provinceDto.countryId());

        State state = stateService.findById(provinceDto.stateId());

        Province province = new Province();
        province.setProvince(provinceDto.province());
        province.setCountry(country);
        province.setState(state);
        
        Province savedProvince = provinceRepository.save(province);
        return mapToResponse(savedProvince);
    }

    @Override
    public ProvinceResponse updateProvince(Long id, ProvinceDto provinceDto) {
        Province province = provinceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Province with ID " + id + " not found"));

        Country country = countryService.findById(provinceDto.countryId());

        State state = stateService.findById(provinceDto.stateId());

        province.setProvince(provinceDto.province());
        province.setCountry(country);
        province.setState(state);
        
        Province updatedProvince = provinceRepository.save(province);
        return mapToResponse(updatedProvince);
    }

    @Override
    public List<ProvinceResponse> getAllProvinces() {
        return provinceRepository.findAllResponse();
    }

    @Override
    public Province getProvinceById(Long id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Province with ID " + id + " not found"));
    }

    @Override
    public ProvinceResponse getProvinceByIdResponse(Long id) {
        Province province = this.getProvinceById(id);
        return mapToResponse(province);
    }

    @Override
    public List<ProvinceResponse> getProvincesByCountry(Long countryId) {
        return provinceRepository.findResponseByCountryId(countryId);
    }

    @Override
    public List<ProvinceResponse> getProvincesByState(Long stateId) {
        return provinceRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteProvince(Long id) {
        if (!provinceRepository.existsById(id)) {
            throw new EntityNotFoundException("Province with ID " + id + " not found");
        }
        provinceRepository.deleteById(id);
    }

    private ProvinceResponse mapToResponse(Province province) {
        return new ProvinceResponse(
                province.getId(),
                province.getProvince(),
                province.getCountry().getId(),
                province.getCountry().getCountry(),
                province.getState().getId(),
                province.getState().getState(),
                province.getCreatedAt(),
                province.getUpdatedAt()
        );
    }

    @Override
    public Province findById(Long id) {
        return provinceRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Province with ID " + id + " not found"));
    }

    @Override
    public Province findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Province entity = provinceRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Province with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Province with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 