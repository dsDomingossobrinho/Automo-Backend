package com.automo.country.service;

import com.automo.country.dto.CountryDto;
import com.automo.country.entity.Country;
import com.automo.country.repository.CountryRepository;
import com.automo.country.response.CountryResponse;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Tests for CountryServiceImpl")
class CountryServiceImplTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private StateService stateService;

    @InjectMocks
    private CountryServiceImpl countryService;

    private Country testCountry;
    private State activeState;
    private State eliminatedState;
    private CountryDto countryDto;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        testCountry = TestDataFactory.createPortugalCountry();
        testCountry.setId(1L);
        testCountry.setState(activeState);
        
        countryDto = TestDataFactory.createValidCountryDto(1L);
    }

    @Test
    @DisplayName("Should create country successfully")
    void shouldCreateCountrySuccessfully() {
        // Given
        when(stateService.findById(1L)).thenReturn(activeState);
        when(countryRepository.save(any(Country.class))).thenReturn(testCountry);
        
        // When
        CountryResponse result = countryService.createCountry(countryDto);
        
        // Then
        assertNotNull(result);
        assertEquals("Portugal", result.country());
        assertEquals(9, result.numberDigits());
        assertEquals("+351", result.indicative());
        assertEquals(1L, result.stateId());
        assertEquals("ACTIVE", result.state());
        
        verify(stateService).findById(1L);
        verify(countryRepository).save(any(Country.class));
    }

    @Test
    @DisplayName("Should update country successfully")
    void shouldUpdateCountrySuccessfully() {
        // Given
        CountryDto updateDto = TestDataFactory.createValidCountryDto("Spain", 9, "+34", 1L);
        
        Country updatedCountry = TestDataFactory.createSpainCountry();
        updatedCountry.setId(1L);
        updatedCountry.setState(activeState);
        
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(stateService.findById(1L)).thenReturn(activeState);
        when(countryRepository.save(any(Country.class))).thenReturn(updatedCountry);
        
        // When
        CountryResponse result = countryService.updateCountry(1L, updateDto);
        
        // Then
        assertNotNull(result);
        assertEquals("Spain", result.country());
        assertEquals("+34", result.indicative());
        
        verify(countryRepository).findById(1L);
        verify(stateService).findById(1L);
        verify(countryRepository).save(any(Country.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent country")
    void shouldThrowExceptionWhenUpdatingNonExistentCountry() {
        // Given
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            countryService.updateCountry(999L, countryDto);
        });
        
        verify(countryRepository).findById(999L);
        verify(countryRepository, never()).save(any(Country.class));
    }

    @Test
    @DisplayName("Should get all countries excluding eliminated")
    void shouldGetAllCountriesExcludingEliminated() {
        // Given
        Country country1 = TestDataFactory.createPortugalCountry();
        country1.setState(activeState);
        
        Country country2 = TestDataFactory.createSpainCountry();
        country2.setState(activeState);
        
        Country eliminatedCountry = TestDataFactory.createBrazilCountry();
        eliminatedCountry.setState(eliminatedState);
        
        List<Country> countries = Arrays.asList(country1, country2, eliminatedCountry);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(countryRepository.findAll()).thenReturn(countries);
        
        // When
        List<CountryResponse> result = countryService.getAllCountries();
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(c -> c.country().equals("Portugal")));
        assertTrue(result.stream().anyMatch(c -> c.country().equals("Spain")));
        assertFalse(result.stream().anyMatch(c -> c.country().equals("Brazil")));
        
        verify(stateService).getEliminatedState();
        verify(countryRepository).findAll();
    }

    @Test
    @DisplayName("Should get country by ID successfully")
    void shouldGetCountryByIdSuccessfully() {
        // Given
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        
        // When
        Country result = countryService.getCountryById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Portugal", result.getCountry());
        assertEquals(1L, result.getId());
        
        verify(countryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent country by ID")
    void shouldThrowExceptionWhenGettingNonExistentCountryById() {
        // Given
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            countryService.getCountryById(999L);
        });
        
        verify(countryRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get country by ID as response successfully")
    void shouldGetCountryByIdResponseSuccessfully() {
        // Given
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        
        // When
        CountryResponse result = countryService.getCountryByIdResponse(1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Portugal", result.country());
        assertEquals(1L, result.id());
        
        verify(countryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get countries by state ID")
    void shouldGetCountriesByStateId() {
        // Given
        List<Country> countries = Arrays.asList(testCountry);
        when(countryRepository.findByStateId(1L)).thenReturn(countries);
        
        // When
        List<CountryResponse> result = countryService.getCountriesByState(1L);
        
        // Then
        assertEquals(1, result.size());
        assertEquals("Portugal", result.get(0).country());
        
        verify(countryRepository).findByStateId(1L);
    }

    @Test
    @DisplayName("Should delete country with soft delete")
    void shouldDeleteCountryWithSoftDelete() {
        // Given
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(countryRepository.save(any(Country.class))).thenReturn(testCountry);
        
        // When
        countryService.deleteCountry(1L);
        
        // Then
        verify(countryRepository).findById(1L);
        verify(stateService).getEliminatedState();
        verify(countryRepository).save(any(Country.class));
        
        // Verify that the country state was set to eliminated
        assertEquals(eliminatedState, testCountry.getState());
    }

    @Test
    @DisplayName("Should find by ID successfully")
    void shouldFindByIdSuccessfully() {
        // Given
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        
        // When
        Country result = countryService.findById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testCountry, result);
        
        verify(countryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when finding by non-existent ID")
    void shouldThrowExceptionWhenFindingByNonExistentId() {
        // Given
        when(countryRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            countryService.findById(999L);
        });
        
        verify(countryRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find by ID and state ID successfully")
    void shouldFindByIdAndStateIdSuccessfully() {
        // Given
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        
        // When
        Country result = countryService.findByIdAndStateId(1L, 1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testCountry, result);
        
        verify(countryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should use default state when state ID is null")
    void shouldUseDefaultStateWhenStateIdIsNull() {
        // Given
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        
        // When
        Country result = countryService.findByIdAndStateId(1L, null);
        
        // Then
        assertNotNull(result);
        assertEquals(testCountry, result);
        
        verify(countryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when state doesn't match")
    void shouldThrowExceptionWhenStateDoesntMatch() {
        // Given
        when(countryRepository.findById(1L)).thenReturn(Optional.of(testCountry));
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            countryService.findByIdAndStateId(1L, 4L); // Different state ID
        });
        
        verify(countryRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle empty repository when getting all countries")
    void shouldHandleEmptyRepositoryWhenGettingAllCountries() {
        // Given
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(countryRepository.findAll()).thenReturn(Arrays.asList());
        
        // When
        List<CountryResponse> result = countryService.getAllCountries();
        
        // Then
        assertTrue(result.isEmpty());
        
        verify(countryRepository).findAll();
    }

    @Test
    @DisplayName("Should handle country with null state when getting all countries")
    void shouldHandleCountryWithNullStateWhenGettingAllCountries() {
        // Given
        Country countryWithNullState = TestDataFactory.createPortugalCountry();
        countryWithNullState.setState(null);
        
        List<Country> countries = Arrays.asList(testCountry, countryWithNullState);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(countryRepository.findAll()).thenReturn(countries);
        
        // When
        List<CountryResponse> result = countryService.getAllCountries();
        
        // Then
        assertEquals(1, result.size());
        assertEquals("Portugal", result.get(0).country());
        
        verify(countryRepository).findAll();
    }
}