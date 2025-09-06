package com.automo.country.repository;

import com.automo.country.entity.Country;
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
@DisplayName("Tests for CountryRepository")
class CountryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    private State activeState;
    private State inactiveState;
    private Country testCountry;

    @BeforeEach
    void setUp() {
        // Create and persist states
        activeState = TestDataFactory.createActiveState();
        inactiveState = TestDataFactory.createInactiveState();
        
        activeState = stateRepository.save(activeState);
        inactiveState = stateRepository.save(inactiveState);
        
        // Create test country
        testCountry = TestDataFactory.createPortugalCountry();
        testCountry.setState(activeState);
        
        entityManager.persistAndFlush(testCountry);
        entityManager.clear();
    }

    @Test
    @DisplayName("Should save country successfully")
    void shouldSaveCountrySuccessfully() {
        // Given
        Country newCountry = TestDataFactory.createSpainCountry();
        newCountry.setState(activeState);
        
        // When
        Country savedCountry = countryRepository.save(newCountry);
        
        // Then
        assertNotNull(savedCountry);
        assertNotNull(savedCountry.getId());
        assertEquals("Spain", savedCountry.getCountry());
        assertEquals(9, savedCountry.getNumberDigits());
        assertEquals("+34", savedCountry.getIndicative());
        assertEquals(activeState.getId(), savedCountry.getState().getId());
        assertNotNull(savedCountry.getCreatedAt());
        assertNotNull(savedCountry.getUpdatedAt());
    }

    @Test
    @DisplayName("Should find country by ID")
    void shouldFindCountryById() {
        // When
        Optional<Country> found = countryRepository.findById(testCountry.getId());
        
        // Then
        assertTrue(found.isPresent());
        Country country = found.get();
        assertEquals("Portugal", country.getCountry());
        assertEquals(9, country.getNumberDigits());
        assertEquals("+351", country.getIndicative());
        assertEquals(activeState.getId(), country.getState().getId());
    }

    @Test
    @DisplayName("Should return empty when finding non-existent country")
    void shouldReturnEmptyWhenFindingNonExistentCountry() {
        // When
        Optional<Country> found = countryRepository.findById(999L);
        
        // Then
        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("Should find all countries")
    void shouldFindAllCountries() {
        // Given
        Country spain = TestDataFactory.createSpainCountry();
        spain.setState(activeState);
        entityManager.persistAndFlush(spain);
        
        Country brazil = TestDataFactory.createBrazilCountry();
        brazil.setState(inactiveState);
        entityManager.persistAndFlush(brazil);
        
        // When
        List<Country> countries = countryRepository.findAll();
        
        // Then
        assertEquals(3, countries.size());
        assertTrue(countries.stream().anyMatch(c -> c.getCountry().equals("Portugal")));
        assertTrue(countries.stream().anyMatch(c -> c.getCountry().equals("Spain")));
        assertTrue(countries.stream().anyMatch(c -> c.getCountry().equals("Brazil")));
    }

    @Test
    @DisplayName("Should find countries by state ID")
    void shouldFindCountriesByStateId() {
        // Given
        Country spain = TestDataFactory.createSpainCountry();
        spain.setState(activeState);
        entityManager.persistAndFlush(spain);
        
        Country brazil = TestDataFactory.createBrazilCountry();
        brazil.setState(inactiveState);
        entityManager.persistAndFlush(brazil);
        
        // When
        List<Country> activeCountries = countryRepository.findByStateId(activeState.getId());
        List<Country> inactiveCountries = countryRepository.findByStateId(inactiveState.getId());
        
        // Then
        assertEquals(2, activeCountries.size());
        assertEquals(1, inactiveCountries.size());
        
        assertTrue(activeCountries.stream().allMatch(c -> c.getState().getId().equals(activeState.getId())));
        assertTrue(inactiveCountries.stream().allMatch(c -> c.getState().getId().equals(inactiveState.getId())));
    }

    @Test
    @DisplayName("Should return empty list when no countries exist for state")
    void shouldReturnEmptyListWhenNoCountriesExistForState() {
        // Given
        State eliminatedState = TestDataFactory.createEliminatedState();
        eliminatedState = stateRepository.save(eliminatedState);
        
        // When
        List<Country> countries = countryRepository.findByStateId(eliminatedState.getId());
        
        // Then
        assertTrue(countries.isEmpty());
    }

    @Test
    @DisplayName("Should update country successfully")
    void shouldUpdateCountrySuccessfully() {
        // Given
        Country country = countryRepository.findById(testCountry.getId()).orElseThrow();
        country.setCountry("Updated Portugal");
        country.setNumberDigits(10);
        country.setIndicative("+999");
        
        // When
        Country updatedCountry = countryRepository.save(country);
        
        // Then
        assertEquals("Updated Portugal", updatedCountry.getCountry());
        assertEquals(10, updatedCountry.getNumberDigits());
        assertEquals("+999", updatedCountry.getIndicative());
        assertNotNull(updatedCountry.getUpdatedAt());
    }

    @Test
    @DisplayName("Should delete country successfully")
    void shouldDeleteCountrySuccessfully() {
        // Given
        Long countryId = testCountry.getId();
        
        // When
        countryRepository.deleteById(countryId);
        
        // Then
        Optional<Country> deleted = countryRepository.findById(countryId);
        assertFalse(deleted.isPresent());
    }

    @Test
    @DisplayName("Should maintain referential integrity with state")
    void shouldMaintainReferentialIntegrityWithState() {
        // Given
        Country country = countryRepository.findById(testCountry.getId()).orElseThrow();
        
        // When
        State state = country.getState();
        
        // Then
        assertNotNull(state);
        assertEquals(activeState.getId(), state.getId());
        assertEquals("ACTIVE", state.getState());
    }

    @Test
    @DisplayName("Should handle lazy loading of state")
    void shouldHandleLazyLoadingOfState() {
        // Given
        entityManager.clear(); // Clear persistence context
        
        // When
        Country country = countryRepository.findById(testCountry.getId()).orElseThrow();
        
        // Then - Accessing state should trigger lazy loading
        State state = country.getState();
        assertNotNull(state);
        assertEquals("ACTIVE", state.getState());
    }

    @Test
    @DisplayName("Should persist timestamps correctly")
    void shouldPersistTimestampsCorrectly() {
        // Given
        Country newCountry = TestDataFactory.createBrazilCountry();
        newCountry.setState(activeState);
        
        // When
        Country savedCountry = countryRepository.save(newCountry);
        
        // Then
        assertNotNull(savedCountry.getCreatedAt());
        assertNotNull(savedCountry.getUpdatedAt());
        assertEquals(savedCountry.getCreatedAt(), savedCountry.getUpdatedAt());
        
        // When updating
        savedCountry.setCountry("Updated Brazil");
        Country updatedCountry = countryRepository.save(savedCountry);
        
        // Then
        assertEquals(savedCountry.getCreatedAt(), updatedCountry.getCreatedAt());
        assertTrue(updatedCountry.getUpdatedAt().isAfter(updatedCountry.getCreatedAt()));
    }

    @Test
    @DisplayName("Should find countries with specific number of digits")
    void shouldFindCountriesWithSpecificNumberOfDigits() {
        // Given
        Country spain = TestDataFactory.createSpainCountry();
        spain.setState(activeState);
        entityManager.persistAndFlush(spain);
        
        Country brazil = TestDataFactory.createBrazilCountry();
        brazil.setState(activeState);
        entityManager.persistAndFlush(brazil);
        
        // When
        List<Country> allCountries = countryRepository.findAll();
        long countriesWith9Digits = allCountries.stream()
                .filter(c -> c.getNumberDigits() != null && c.getNumberDigits() == 9)
                .count();
        long countriesWith11Digits = allCountries.stream()
                .filter(c -> c.getNumberDigits() != null && c.getNumberDigits() == 11)
                .count();
        
        // Then
        assertEquals(2, countriesWith9Digits); // Portugal and Spain
        assertEquals(1, countriesWith11Digits); // Brazil
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void shouldHandleNullOptionalFields() {
        // Given
        Country country = new Country();
        country.setCountry("Test Country");
        country.setNumberDigits(null); // Optional field
        country.setIndicative(null);   // Optional field
        country.setState(activeState);
        
        // When
        Country savedCountry = countryRepository.save(country);
        
        // Then
        assertNotNull(savedCountry.getId());
        assertEquals("Test Country", savedCountry.getCountry());
        assertNull(savedCountry.getNumberDigits());
        assertNull(savedCountry.getIndicative());
        assertNotNull(savedCountry.getState());
    }

    @Test
    @DisplayName("Should enforce unique constraints if any exist")
    void shouldEnforceUniqueConstraintsIfAnyExist() {
        // Given - Create two countries with same name (if unique constraint exists)
        Country country1 = new Country();
        country1.setCountry("Duplicate Country");
        country1.setNumberDigits(9);
        country1.setState(activeState);
        
        Country country2 = new Country();
        country2.setCountry("Duplicate Country");
        country2.setNumberDigits(10);
        country2.setState(activeState);
        
        // When
        countryRepository.save(country1);
        Country savedCountry2 = countryRepository.save(country2);
        
        // Then - Both should save successfully (no unique constraint on country name)
        assertNotNull(savedCountry2.getId());
    }
}