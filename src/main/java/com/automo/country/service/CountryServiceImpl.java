package com.automo.country.service;

import com.automo.country.dto.CountryDto;
import com.automo.country.entity.Country;
import com.automo.country.repository.CountryRepository;
import com.automo.country.response.CountryResponse;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;
    private final StateRepository stateRepository;

    @Override
    public CountryResponse createCountry(CountryDto countryDto) {
        State state = stateRepository.findById(countryDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + countryDto.stateId() + " not found"));

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

        State state = stateRepository.findById(countryDto.stateId())
                .orElseThrow(() -> new EntityNotFoundException("State with ID " + countryDto.stateId() + " not found"));

        country.setCountry(countryDto.country());
        country.setNumberDigits(countryDto.numberDigits());
        country.setIndicative(countryDto.indicative());
        country.setState(state);
        
        Country updatedCountry = countryRepository.save(country);
        return mapToResponse(updatedCountry);
    }

    @Override
    public List<CountryResponse> getAllCountries() {
        return countryRepository.findAllResponse();
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
        if (!countryRepository.existsById(id)) {
            throw new EntityNotFoundException("Country with ID " + id + " not found");
        }
        countryRepository.deleteById(id);
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
} 