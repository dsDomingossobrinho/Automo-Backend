package com.automo.country.service;

import com.automo.country.dto.CountryDto;
import com.automo.country.entity.Country;
import com.automo.country.response.CountryResponse;

import java.util.List;

public interface CountryService {

    CountryResponse createCountry(CountryDto countryDto);

    CountryResponse updateCountry(Long id, CountryDto countryDto);

    List<CountryResponse> getAllCountries();

    Country getCountryById(Long id);

    CountryResponse getCountryByIdResponse(Long id);

    List<CountryResponse> getCountriesByState(Long stateId);

    void deleteCountry(Long id);
} 