package com.automo.country.service;

import com.automo.country.dto.CountryDto;
import com.automo.country.entity.Country;
import com.automo.country.repository.CountryRepository;
import com.automo.country.response.CountryResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final StateService stateService;

    @Override
    public CountryResponse createCountry(CountryDto countryDto) {
        State state = stateService.findById(countryDto.stateId());

        Country country = new Country();
        country.setCountry(countryDto.country());
        country.setNumberDigits(countryDto.numberDigits());
        country.setIndicative(countryDto.indicative());
        country.setState(state);
        
        Country savedCountry = countryRepository.save(country);
        return mapToResponse(savedCountry);
    }

    @Override
    public CountryResponse updateCountry(Long id, CountryDto countryDto) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Country with ID " + id + " not found"));

        State state = stateService.findById(countryDto.stateId());

        country.setCountry(countryDto.country());
        country.setNumberDigits(countryDto.numberDigits());
        country.setIndicative(countryDto.indicative());
        country.setState(state);
        
        Country updatedCountry = countryRepository.save(country);
        return mapToResponse(updatedCountry);
    }

    @Override
    public List<CountryResponse> getAllCountries() {
        State eliminatedState = stateService.getEliminatedState();
        return countryRepository.findAll().stream()
                .filter(country -> country.getState() != null && !country.getState().getId().equals(eliminatedState.getId()))
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Country getCountryById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Country with ID " + id + " not found"));
    }

    @Override
    public CountryResponse getCountryByIdResponse(Long id) {
        Country country = this.getCountryById(id);
        return mapToResponse(country);
    }

    @Override
    public List<CountryResponse> getCountriesByState(Long stateId) {
        return countryRepository.findByStateId(stateId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteCountry(Long id) {
        Country country = this.findById(id);
        
        // Set state to ELIMINATED for soft delete
        State eliminatedState = stateService.getEliminatedState();
        country.setState(eliminatedState);
        
        countryRepository.save(country);
    }

    private CountryResponse mapToResponse(Country country) {
        return new CountryResponse(
                country.getId(),
                country.getCountry(),
                country.getNumberDigits(),
                country.getIndicative(),
                country.getState().getId(),
                country.getState().getState(),
                country.getCreatedAt(),
                country.getUpdatedAt()
        );
    }

    @Override
    public Country findById(Long id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Country with ID " + id + " not found"));
    }

    @Override
    public Country findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Country entity = countryRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Country with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Country with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 