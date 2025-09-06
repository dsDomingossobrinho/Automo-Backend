package com.automo.country.entity;

import com.automo.state.entity.State;
import com.automo.test.config.BaseTestConfig;
import com.automo.test.utils.TestDataFactory;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for Country Entity")
class CountryTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Country entity")
    void shouldCreateValidCountryEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        country.setState(state);
        
        // When
        Set<ConstraintViolation<Country>> violations = validator.validate(country);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Portugal", country.getCountry());
        assertEquals(9, country.getNumberDigits());
        assertEquals("+351", country.getIndicative());
        assertEquals(state, country.getState());
    }

    @Test
    @DisplayName("Should fail validation with null country name")
    void shouldFailValidationWithNullCountryName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Country country = new Country();
        country.setCountry(null);
        country.setNumberDigits(9);
        country.setIndicative("+351");
        country.setState(state);
        
        // When
        Set<ConstraintViolation<Country>> violations = validator.validate(country);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("country")));
    }

    @Test
    @DisplayName("Should fail validation with blank country name")
    void shouldFailValidationWithBlankCountryName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Country country = new Country();
        country.setCountry("");
        country.setNumberDigits(9);
        country.setIndicative("+351");
        country.setState(state);
        
        // When
        Set<ConstraintViolation<Country>> violations = validator.validate(country);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("country")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Country country = new Country();
        country.setCountry("Portugal");
        country.setNumberDigits(9);
        country.setIndicative("+351");
        country.setState(null);
        
        // When
        Set<ConstraintViolation<Country>> violations = validator.validate(country);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should fail validation with zero or negative number digits")
    void shouldFailValidationWithInvalidNumberDigits() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Country country1 = new Country();
        country1.setCountry("Portugal");
        country1.setNumberDigits(0);
        country1.setIndicative("+351");
        country1.setState(state);
        
        Country country2 = new Country();
        country2.setCountry("Portugal");
        country2.setNumberDigits(-5);
        country2.setIndicative("+351");
        country2.setState(state);
        
        // When
        Set<ConstraintViolation<Country>> violations1 = validator.validate(country1);
        Set<ConstraintViolation<Country>> violations2 = validator.validate(country2);
        
        // Then
        assertFalse(violations1.isEmpty());
        assertFalse(violations2.isEmpty());
        assertTrue(violations1.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numberDigits")));
        assertTrue(violations2.stream().anyMatch(v -> v.getPropertyPath().toString().equals("numberDigits")));
    }

    @Test
    @DisplayName("Should create Country with different countries and number of digits")
    void shouldCreateCountryWithDifferentData() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Country portugal = TestDataFactory.createPortugalCountry();
        portugal.setState(state);
        
        Country spain = TestDataFactory.createSpainCountry();
        spain.setState(state);
        
        Country brazil = TestDataFactory.createBrazilCountry();
        brazil.setState(state);
        
        // When
        Set<ConstraintViolation<Country>> violations1 = validator.validate(portugal);
        Set<ConstraintViolation<Country>> violations2 = validator.validate(spain);
        Set<ConstraintViolation<Country>> violations3 = validator.validate(brazil);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertTrue(violations3.isEmpty());
        
        assertEquals("Portugal", portugal.getCountry());
        assertEquals(9, portugal.getNumberDigits());
        assertEquals("+351", portugal.getIndicative());
        
        assertEquals("Spain", spain.getCountry());
        assertEquals(9, spain.getNumberDigits());
        assertEquals("+34", spain.getIndicative());
        
        assertEquals("Brazil", brazil.getCountry());
        assertEquals(11, brazil.getNumberDigits());
        assertEquals("+55", brazil.getIndicative());
    }

    @Test
    @DisplayName("Should allow null indicative (optional field)")
    void shouldAllowNullIndicative() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Country country = new Country();
        country.setCountry("Test Country");
        country.setNumberDigits(9);
        country.setIndicative(null);
        country.setState(state);
        
        // When
        Set<ConstraintViolation<Country>> violations = validator.validate(country);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNull(country.getIndicative());
    }

    @Test
    @DisplayName("Should allow null number digits (optional field)")
    void shouldAllowNullNumberDigits() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Country country = new Country();
        country.setCountry("Test Country");
        country.setNumberDigits(null);
        country.setIndicative("+999");
        country.setState(state);
        
        // When
        Set<ConstraintViolation<Country>> violations = validator.validate(country);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNull(country.getNumberDigits());
    }

    @Test
    @DisplayName("Should create Country with different states")
    void shouldCreateCountryWithDifferentStates() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        
        Country country1 = TestDataFactory.createPortugalCountry();
        country1.setState(activeState);
        
        Country country2 = TestDataFactory.createPortugalCountry();
        country2.setState(inactiveState);
        
        // When
        Set<ConstraintViolation<Country>> violations1 = validator.validate(country1);
        Set<ConstraintViolation<Country>> violations2 = validator.validate(country2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals(activeState, country1.getState());
        assertEquals(inactiveState, country2.getState());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Country country1 = TestDataFactory.createPortugalCountry();
        country1.setState(state);
        country1.setId(1L);
        
        Country country2 = TestDataFactory.createPortugalCountry();
        country2.setState(state);
        country2.setId(1L);
        
        // Then
        assertEquals(country1, country2);
        assertEquals(country1.hashCode(), country2.hashCode());
        
        // When different IDs
        country2.setId(2L);
        
        // Then
        assertNotEquals(country1, country2);
    }

    @Test
    @DisplayName("Should test toString method")
    void shouldTestToStringMethod() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        country.setState(state);
        
        // When
        String toString = country.toString();
        
        // Then
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }
}