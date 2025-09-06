package com.automo.province.repository;

import com.automo.country.entity.Country;
import com.automo.country.repository.CountryRepository;
import com.automo.province.entity.Province;
import com.automo.state.entity.State;
import com.automo.state.repository.StateRepository;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for ProvinceRepository")
class ProvinceRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    private State activeState;
    private State inactiveState;
    private Country testCountry;
    private Province testProvince;

    @BeforeEach
    void setUp() {
        // Create and persist states
        activeState = TestDataFactory.createActiveState();
        inactiveState = TestDataFactory.createInactiveState();
        
        activeState = stateRepository.save(activeState);
        inactiveState = stateRepository.save(inactiveState);
        
        // Create and persist country
        testCountry = TestDataFactory.createPortugalCountry();
        testCountry.setState(activeState);
        testCountry = countryRepository.save(testCountry);
        
        // Create test province
        testProvince = TestDataFactory.createLisbonProvince(testCountry);
        testProvince.setState(activeState);
        
        entityManager.persistAndFlush(testProvince);
        entityManager.clear();
    }

    @Test
    @DisplayName("Should save province successfully")
    void shouldSaveProvinceSuccessfully() {
        // Given
        Province newProvince = TestDataFactory.createPortoProvince(testCountry);
        newProvince.setState(activeState);
        
        // When
        Province savedProvince = provinceRepository.save(newProvince);
        
        // Then
        assertNotNull(savedProvince);
        assertNotNull(savedProvince.getId());
        assertEquals("Porto", savedProvince.getProvince());
        assertEquals(testCountry.getId(), savedProvince.getCountry().getId());
        assertEquals(activeState.getId(), savedProvince.getState().getId());
        assertNotNull(savedProvince.getCreatedAt());
        assertNotNull(savedProvince.getUpdatedAt());
    }

    @Test
    @DisplayName("Should find province by ID")
    void shouldFindProvinceById() {
        // When
        Optional<Province> found = provinceRepository.findById(testProvince.getId());
        
        // Then
        assertTrue(found.isPresent());
        Province province = found.get();
        assertEquals("Lisboa", province.getProvince());
        assertEquals(testCountry.getId(), province.getCountry().getId());
        assertEquals(activeState.getId(), province.getState().getId());
    }

    @Test
    @DisplayName("Should return empty when finding non-existent province")
    void shouldReturnEmptyWhenFindingNonExistentProvince() {
        // When
        Optional<Province> found = provinceRepository.findById(999L);
        
        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find all provinces")
    void shouldFindAllProvinces() {
        // Given
        Province porto = TestDataFactory.createPortoProvince(testCountry);
        porto.setState(activeState);
        entityManager.persistAndFlush(porto);
        
        Country spain = TestDataFactory.createSpainCountry();
        spain.setState(activeState);
        spain = countryRepository.save(spain);
        
        Province madrid = TestDataFactory.createMadridProvince(spain);
        madrid.setState(inactiveState);
        entityManager.persistAndFlush(madrid);
        
        // When
        List<Province> provinces = provinceRepository.findAll();
        
        // Then
        assertEquals(3, provinces.size());
        assertTrue(provinces.stream().anyMatch(p -> p.getProvince().equals("Lisboa")));
        assertTrue(provinces.stream().anyMatch(p -> p.getProvince().equals("Porto")));
        assertTrue(provinces.stream().anyMatch(p -> p.getProvince().equals("Madrid")));
    }

    @Test
    @DisplayName("Should find provinces by state ID")
    void shouldFindProvincesByStateId() {
        // Given
        Province porto = TestDataFactory.createPortoProvince(testCountry);
        porto.setState(activeState);
        entityManager.persistAndFlush(porto);
        
        Country spain = TestDataFactory.createSpainCountry();
        spain.setState(activeState);
        spain = countryRepository.save(spain);
        
        Province madrid = TestDataFactory.createMadridProvince(spain);
        madrid.setState(inactiveState);
        entityManager.persistAndFlush(madrid);
        
        // When
        List<Province> activeProvinces = provinceRepository.findByStateId(activeState.getId());
        List<Province> inactiveProvinces = provinceRepository.findByStateId(inactiveState.getId());
        
        // Then
        assertEquals(2, activeProvinces.size());
        assertEquals(1, inactiveProvinces.size());
        
        assertTrue(activeProvinces.stream().allMatch(p -> p.getState().getId().equals(activeState.getId())));
        assertTrue(inactiveProvinces.stream().allMatch(p -> p.getState().getId().equals(inactiveState.getId())));
    }

    @Test
    @DisplayName("Should find provinces by country ID")
    void shouldFindProvincesByCountryId() {
        // Given
        Province porto = TestDataFactory.createPortoProvince(testCountry);
        porto.setState(activeState);
        entityManager.persistAndFlush(porto);
        
        Country spain = TestDataFactory.createSpainCountry();
        spain.setState(activeState);
        spain = countryRepository.save(spain);
        
        Province madrid = TestDataFactory.createMadridProvince(spain);
        madrid.setState(activeState);
        entityManager.persistAndFlush(madrid);
        
        // When
        List<Province> portugalProvinces = provinceRepository.findByCountryId(testCountry.getId());
        List<Province> spainProvinces = provinceRepository.findByCountryId(spain.getId());
        
        // Then
        assertEquals(2, portugalProvinces.size());
        assertEquals(1, spainProvinces.size());
        
        assertTrue(portugalProvinces.stream().allMatch(p -> p.getCountry().getId().equals(testCountry.getId())));
        assertTrue(spainProvinces.stream().allMatch(p -> p.getCountry().getId().equals(spain.getId())));
        
        assertTrue(portugalProvinces.stream().anyMatch(p -> p.getProvince().equals("Lisboa")));
        assertTrue(portugalProvinces.stream().anyMatch(p -> p.getProvince().equals("Porto")));
        assertTrue(spainProvinces.stream().anyMatch(p -> p.getProvince().equals("Madrid")));
    }

    @Test
    @DisplayName("Should return empty list when no provinces exist for state")
    void shouldReturnEmptyListWhenNoProvincesExistForState() {
        // Given
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = stateRepository.save(eliminatedState);
        
        // When
        List<Province> provinces = provinceRepository.findByStateId(eliminatedState.getId());
        
        // Then
        assertTrue(provinces.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no provinces exist for country")
    void shouldReturnEmptyListWhenNoProvincesExistForCountry() {
        // Given
        Country brazil = TestDataFactory.createBrazilCountry();
        brazil.setState(activeState);
        brazil = countryRepository.save(brazil);
        
        // When
        List<Province> provinces = provinceRepository.findByCountryId(brazil.getId());
        
        // Then
        assertTrue(provinces.isEmpty());
    }

    @Test
    @DisplayName("Should update province successfully")
    void shouldUpdateProvinceSuccessfully() {
        // Given
        Province province = provinceRepository.findById(testProvince.getId()).orElseThrow();
        province.setProvince("Updated Lisboa");
        
        // When
        Province updatedProvince = provinceRepository.save(province);
        
        // Then
        assertEquals("Updated Lisboa", updatedProvince.getProvince());
        assertNotNull(updatedProvince.getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete province successfully")
    void shouldDeleteProvinceSuccessfully() {
        // Given
        Long provinceId = testProvince.getId();
        
        // When
        provinceRepository.deleteById(provinceId);
        
        // Then
        Optional<Province> deleted = provinceRepository.findById(provinceId);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should maintain referential integrity with country")
    void shouldMaintainReferentialIntegrityWithCountry() {
        // Given
        Province province = provinceRepository.findById(testProvince.getId()).orElseThrow();
        
        // When
        Country country = province.getCountry();
        
        // Then
        assertNotNull(country);
        assertEquals(testCountry.getId(), country.getId());
        assertEquals("Portugal", country.getCountry());
    }

    @Test
    @DisplayName("Should maintain referential integrity with state")
    void shouldMaintainReferentialIntegrityWithState() {
        // Given
        Province province = provinceRepository.findById(testProvince.getId()).orElseThrow();
        
        // When
        State state = province.getState();
        
        // Then
        assertNotNull(state);
        assertEquals(activeState.getId(), state.getId());
        assertEquals("ACTIVE", state.getState());
    }

    @Test
    @DisplayName("Should handle lazy loading of relationships")
    void shouldHandleLazyLoadingOfRelationships() {
        // Given
        entityManager.clear(); // Clear persistence context
        
        // When
        Province province = provinceRepository.findById(testProvince.getId()).orElseThrow();
        
        // Then - Accessing relationships should trigger lazy loading
        Country country = province.getCountry();
        assertNotNull(country);
        assertEquals("Portugal", country.getCountry());
        
        State state = province.getState();
        assertNotNull(state);
        assertEquals("ACTIVE", state.getState());
    }

    @Test
    @DisplayName("Should persist timestamps correctly")
    void shouldPersistTimestampsCorrectly() {
        // Given
        Province newProvince = TestDataFactory.createPortoProvince(testCountry);
        newProvince.setState(activeState);
        
        // When
        Province savedProvince = provinceRepository.save(newProvince);
        
        // Then
        assertNotNull(savedProvince.getCreatedAt());
        assertNotNull(savedProvince.getUpdatedAt());
        assertEquals(savedProvince.getCreatedAt(), savedProvince.getUpdatedAt());
        
        // When updating
        savedProvince.setProvince("Updated Porto");
        Province updatedProvince = provinceRepository.save(savedProvince);
        
        // Then
        assertEquals(savedProvince.getCreatedAt(), updatedProvince.getCreatedAt());
        assertTrue(updatedProvince.getUpdatedAt().isAfter(updatedProvince.getCreatedAt()));
    }

    @Test
    @DisplayName("Should find provinces with different countries")
    void shouldFindProvincesWithDifferentCountries() {
        // Given
        Country spain = TestDataFactory.createSpainCountry();
        spain.setState(activeState);
        spain = countryRepository.save(spain);
        
        Province madrid = TestDataFactory.createMadridProvince(spain);
        madrid.setState(activeState);
        entityManager.persistAndFlush(madrid);
        
        // When
        List<Province> allProvinces = provinceRepository.findAll();
        long portugalProvinces = allProvinces.stream()
                .filter(p -> p.getCountry().getCountry().equals("Portugal"))
                .count();
        long spainProvinces = allProvinces.stream()
                .filter(p -> p.getCountry().getCountry().equals("Spain"))
                .count();
        
        // Then
        assertEquals(1, portugalProvinces); // Lisboa
        assertEquals(1, spainProvinces); // Madrid
    }

    @Test
    @DisplayName("Should handle multiple provinces for same country")
    void shouldHandleMultipleProvincesForSameCountry() {
        // Given
        Province porto = TestDataFactory.createPortoProvince(testCountry);
        porto.setState(activeState);
        entityManager.persistAndFlush(porto);
        
        // When
        List<Province> portugalProvinces = provinceRepository.findByCountryId(testCountry.getId());
        
        // Then
        assertEquals(2, portugalProvinces.size());
        assertTrue(portugalProvinces.stream().anyMatch(p -> p.getProvince().equals("Lisboa")));
        assertTrue(portugalProvinces.stream().anyMatch(p -> p.getProvince().equals("Porto")));
        assertTrue(portugalProvinces.stream().allMatch(p -> p.getCountry().getCountry().equals("Portugal")));
    }

    @Test
    @DisplayName("Should handle cascade operations correctly")
    void shouldHandleCascadeOperationsCorrectly() {
        // Given
        Province province = provinceRepository.findById(testProvince.getId()).orElseThrow();
        
        // When - Changing country reference
        Country spain = TestDataFactory.createSpainCountry();
        spain.setState(activeState);
        spain = countryRepository.save(spain);
        
        province.setCountry(spain);
        Province updatedProvince = provinceRepository.save(province);
        
        // Then
        assertEquals(spain.getId(), updatedProvince.getCountry().getId());
        assertEquals("Spain", updatedProvince.getCountry().getCountry());
    }

    @Test
    @DisplayName("Should validate unique constraints if any exist")
    void shouldValidateUniqueConstraintsIfAnyExist() {
        // Given - Create two provinces with same name in same country (if unique constraint exists)
        Province province1 = new Province();
        province1.setProvince("Duplicate Province");
        province1.setCountry(testCountry);
        province1.setState(activeState);
        
        Province province2 = new Province();
        province2.setProvince("Duplicate Province");
        province2.setCountry(testCountry);
        province2.setState(activeState);
        
        // When
        provinceRepository.save(province1);
        Province savedProvince2 = provinceRepository.save(province2);
        
        // Then - Both should save successfully (no unique constraint on province name + country)
        assertNotNull(savedProvince2.getId());
    }

    @Test
    @DisplayName("Should handle province names with special characters")
    void shouldHandleProvinceNamesWithSpecialCharacters() {
        // Given
        Province province = new Province();
        province.setProvince("São Paulo");
        province.setCountry(testCountry);
        province.setState(activeState);
        
        // When
        Province savedProvince = provinceRepository.save(province);
        
        // Then
        assertNotNull(savedProvince.getId());
        assertEquals("São Paulo", savedProvince.getProvince());
    }
}