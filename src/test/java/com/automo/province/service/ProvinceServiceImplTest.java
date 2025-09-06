package com.automo.province.service;

import com.automo.country.entity.Country;
import com.automo.country.service.CountryService;
import com.automo.province.dto.ProvinceDto;
import com.automo.province.entity.Province;
import com.automo.province.repository.ProvinceRepository;
import com.automo.province.response.ProvinceResponse;
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
@DisplayName("Tests for ProvinceServiceImpl")
class ProvinceServiceImplTest {

    @Mock
    private ProvinceRepository provinceRepository;

    @Mock
    private CountryService countryService;

    @Mock
    private StateService stateService;

    @InjectMocks
    private ProvinceServiceImpl provinceService;

    private Province testProvince;
    private Country testCountry;
    private State activeState;
    private State eliminatedState;
    private ProvinceDto provinceDto;

    @BeforeEach
    void setUp() {
        activeState = TestDataFactory.createActiveState();
        activeState.setId(1L);
        
        eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState.setId(4L);
        
        testCountry = TestDataFactory.createPortugalCountry();
        testCountry.setId(1L);
        testCountry.setState(activeState);
        
        testProvince = TestDataFactory.createLisbonProvince(testCountry);
        testProvince.setId(1L);
        testProvince.setState(activeState);
        
        provinceDto = TestDataFactory.createValidProvinceDto(1L, 1L);
    }

    @Test
    @DisplayName("Should create province successfully")
    void shouldCreateProvinceSuccessfully() {
        // Given
        when(countryService.findById(1L)).thenReturn(testCountry);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(provinceRepository.save(any(Province.class))).thenReturn(testProvince);
        
        // When
        ProvinceResponse result = provinceService.createProvince(provinceDto);
        
        // Then
        assertNotNull(result);
        assertEquals("Lisboa", result.province());
        assertEquals(1L, result.countryId());
        assertEquals("Portugal", result.countryName());
        assertEquals(1L, result.stateId());
        assertEquals("ACTIVE", result.state());
        
        verify(countryService).findById(1L);
        verify(stateService).findById(1L);
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    @DisplayName("Should update province successfully")
    void shouldUpdateProvinceSuccessfully() {
        // Given
        ProvinceDto updateDto = TestDataFactory.createValidProvinceDto("Porto", 1L, 1L);
        
        Province updatedProvince = TestDataFactory.createPortoProvince(testCountry);
        updatedProvince.setId(1L);
        updatedProvince.setState(activeState);
        
        when(provinceRepository.findById(1L)).thenReturn(Optional.of(testProvince));
        when(countryService.findById(1L)).thenReturn(testCountry);
        when(stateService.findById(1L)).thenReturn(activeState);
        when(provinceRepository.save(any(Province.class))).thenReturn(updatedProvince);
        
        // When
        ProvinceResponse result = provinceService.updateProvince(1L, updateDto);
        
        // Then
        assertNotNull(result);
        assertEquals("Porto", result.province());
        
        verify(provinceRepository).findById(1L);
        verify(countryService).findById(1L);
        verify(stateService).findById(1L);
        verify(provinceRepository).save(any(Province.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent province")
    void shouldThrowExceptionWhenUpdatingNonExistentProvince() {
        // Given
        when(provinceRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            provinceService.updateProvince(999L, provinceDto);
        });
        
        verify(provinceRepository).findById(999L);
        verify(provinceRepository, never()).save(any(Province.class));
    }

    @Test
    @DisplayName("Should get all provinces excluding eliminated")
    void shouldGetAllProvincesExcludingEliminated() {
        // Given
        Province province1 = TestDataFactory.createLisbonProvince(testCountry);
        province1.setState(activeState);
        
        Province province2 = TestDataFactory.createPortoProvince(testCountry);
        province2.setState(activeState);
        
        Province eliminatedProvince = TestDataFactory.createMadridProvince(testCountry);
        eliminatedProvince.setState(eliminatedState);
        
        List<Province> provinces = Arrays.asList(province1, province2, eliminatedProvince);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(provinceRepository.findAll()).thenReturn(provinces);
        
        // When
        List<ProvinceResponse> result = provinceService.getAllProvinces();
        
        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(p -> p.province().equals("Lisboa")));
        assertTrue(result.stream().anyMatch(p -> p.province().equals("Porto")));
        assertFalse(result.stream().anyMatch(p -> p.province().equals("Madrid")));
        
        verify(stateService).getEliminatedState();
        verify(provinceRepository).findAll();
    }

    @Test
    @DisplayName("Should get province by ID successfully")
    void shouldGetProvinceByIdSuccessfully() {
        // Given
        when(provinceRepository.findById(1L)).thenReturn(Optional.of(testProvince));
        
        // When
        Province result = provinceService.getProvinceById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Lisboa", result.getProvince());
        assertEquals(1L, result.getId());
        
        verify(provinceRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting non-existent province by ID")
    void shouldThrowExceptionWhenGettingNonExistentProvinceById() {
        // Given
        when(provinceRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            provinceService.getProvinceById(999L);
        });
        
        verify(provinceRepository).findById(999L);
    }

    @Test
    @DisplayName("Should get province by ID as response successfully")
    void shouldGetProvinceByIdResponseSuccessfully() {
        // Given
        when(provinceRepository.findById(1L)).thenReturn(Optional.of(testProvince));
        
        // When
        ProvinceResponse result = provinceService.getProvinceByIdResponse(1L);
        
        // Then
        assertNotNull(result);
        assertEquals("Lisboa", result.province());
        assertEquals(1L, result.id());
        
        verify(provinceRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get provinces by state ID")
    void shouldGetProvincesByStateId() {
        // Given
        List<Province> provinces = Arrays.asList(testProvince);
        when(provinceRepository.findByStateId(1L)).thenReturn(provinces);
        
        // When
        List<ProvinceResponse> result = provinceService.getProvincesByState(1L);
        
        // Then
        assertEquals(1, result.size());
        assertEquals("Lisboa", result.get(0).province());
        
        verify(provinceRepository).findByStateId(1L);
    }

    @Test
    @DisplayName("Should get provinces by country ID")
    void shouldGetProvincesByCountryId() {
        // Given
        List<Province> provinces = Arrays.asList(testProvince);
        when(provinceRepository.findByCountryId(1L)).thenReturn(provinces);
        
        // When
        List<ProvinceResponse> result = provinceService.getProvincesByCountry(1L);
        
        // Then
        assertEquals(1, result.size());
        assertEquals("Lisboa", result.get(0).province());
        
        verify(provinceRepository).findByCountryId(1L);
    }

    @Test
    @DisplayName("Should delete province with soft delete")
    void shouldDeleteProvinceWithSoftDelete() {
        // Given
        when(provinceRepository.findById(1L)).thenReturn(Optional.of(testProvince));
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(provinceRepository.save(any(Province.class))).thenReturn(testProvince);
        
        // When
        provinceService.deleteProvince(1L);
        
        // Then
        verify(provinceRepository).findById(1L);
        verify(stateService).getEliminatedState();
        verify(provinceRepository).save(any(Province.class));
        
        // Verify that the province state was set to eliminated
        assertEquals(eliminatedState, testProvince.getState());
    }

    @Test
    @DisplayName("Should find by ID successfully")
    void shouldFindByIdSuccessfully() {
        // Given
        when(provinceRepository.findById(1L)).thenReturn(Optional.of(testProvince));
        
        // When
        Province result = provinceService.findById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testProvince, result);
        
        verify(provinceRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when finding by non-existent ID")
    void shouldThrowExceptionWhenFindingByNonExistentId() {
        // Given
        when(provinceRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            provinceService.findById(999L);
        });
        
        verify(provinceRepository).findById(999L);
    }

    @Test
    @DisplayName("Should find by ID and state ID successfully")
    void shouldFindByIdAndStateIdSuccessfully() {
        // Given
        when(provinceRepository.findById(1L)).thenReturn(Optional.of(testProvince));
        
        // When
        Province result = provinceService.findByIdAndStateId(1L, 1L);
        
        // Then
        assertNotNull(result);
        assertEquals(testProvince, result);
        
        verify(provinceRepository).findById(1L);
    }

    @Test
    @DisplayName("Should use default state when state ID is null")
    void shouldUseDefaultStateWhenStateIdIsNull() {
        // Given
        when(provinceRepository.findById(1L)).thenReturn(Optional.of(testProvince));
        
        // When
        Province result = provinceService.findByIdAndStateId(1L, null);
        
        // Then
        assertNotNull(result);
        assertEquals(testProvince, result);
        
        verify(provinceRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when state doesn't match")
    void shouldThrowExceptionWhenStateDoesntMatch() {
        // Given
        when(provinceRepository.findById(1L)).thenReturn(Optional.of(testProvince));
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            provinceService.findByIdAndStateId(1L, 4L); // Different state ID
        });
        
        verify(provinceRepository).findById(1L);
    }

    @Test
    @DisplayName("Should handle empty repository when getting all provinces")
    void shouldHandleEmptyRepositoryWhenGettingAllProvinces() {
        // Given
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(provinceRepository.findAll()).thenReturn(Arrays.asList());
        
        // When
        List<ProvinceResponse> result = provinceService.getAllProvinces();
        
        // Then
        assertTrue(result.isEmpty());
        
        verify(provinceRepository).findAll();
    }

    @Test
    @DisplayName("Should handle province with null state when getting all provinces")
    void shouldHandleProvinceWithNullStateWhenGettingAllProvinces() {
        // Given
        Province provinceWithNullState = TestDataFactory.createLisbonProvince(testCountry);
        provinceWithNullState.setState(null);
        
        List<Province> provinces = Arrays.asList(testProvince, provinceWithNullState);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(provinceRepository.findAll()).thenReturn(provinces);
        
        // When
        List<ProvinceResponse> result = provinceService.getAllProvinces();
        
        // Then
        assertEquals(1, result.size());
        assertEquals("Lisboa", result.get(0).province());
        
        verify(provinceRepository).findAll();
    }

    @Test
    @DisplayName("Should handle province with null country when getting all provinces")
    void shouldHandleProvinceWithNullCountryWhenGettingAllProvinces() {
        // Given
        Province provinceWithNullCountry = new Province();
        provinceWithNullCountry.setProvince("Test Province");
        provinceWithNullCountry.setState(activeState);
        provinceWithNullCountry.setCountry(null);
        
        List<Province> provinces = Arrays.asList(testProvince, provinceWithNullCountry);
        
        when(stateService.getEliminatedState()).thenReturn(eliminatedState);
        when(provinceRepository.findAll()).thenReturn(provinces);
        
        // When
        List<ProvinceResponse> result = provinceService.getAllProvinces();
        
        // Then
        assertEquals(1, result.size()); // Only the valid province should be returned
        assertEquals("Lisboa", result.get(0).province());
        
        verify(provinceRepository).findAll();
    }

    @Test
    @DisplayName("Should throw exception when country service fails")
    void shouldThrowExceptionWhenCountryServiceFails() {
        // Given
        when(countryService.findById(999L)).thenThrow(new EntityNotFoundException("Country not found"));
        
        ProvinceDto invalidDto = TestDataFactory.createValidProvinceDto(999L, 1L);
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            provinceService.createProvince(invalidDto);
        });
        
        verify(countryService).findById(999L);
        verify(provinceRepository, never()).save(any(Province.class));
    }

    @Test
    @DisplayName("Should throw exception when state service fails")
    void shouldThrowExceptionWhenStateServiceFails() {
        // Given
        when(countryService.findById(1L)).thenReturn(testCountry);
        when(stateService.findById(999L)).thenThrow(new EntityNotFoundException("State not found"));
        
        ProvinceDto invalidDto = TestDataFactory.createValidProvinceDto(1L, 999L);
        
        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            provinceService.createProvince(invalidDto);
        });
        
        verify(countryService).findById(1L);
        verify(stateService).findById(999L);
        verify(provinceRepository, never()).save(any(Province.class));
    }
}