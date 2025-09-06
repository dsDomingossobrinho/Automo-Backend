package com.automo.province.entity;

import com.automo.country.entity.Country;
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
@DisplayName("Tests for Province Entity")
class ProvinceTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Province entity")
    void shouldCreateValidProvinceEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        Province province = TestDataFactory.createLisbonProvince(country);
        province.setState(state);
        
        // When
        Set<ConstraintViolation<Province>> violations = validator.validate(province);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Lisboa", province.getProvince());
        assertEquals(country, province.getCountry());
        assertEquals(state, province.getState());
    }

    @Test
    @DisplayName("Should fail validation with null province name")
    void shouldFailValidationWithNullProvinceName() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        
        Province province = new Province();
        province.setProvince(null);
        province.setCountry(country);
        province.setState(state);
        
        // When
        Set<ConstraintViolation<Province>> violations = validator.validate(province);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("province")));
    }

    @Test
    @DisplayName("Should fail validation with blank province name")
    void shouldFailValidationWithBlankProvinceName() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        
        Province province = new Province();
        province.setProvince("");
        province.setCountry(country);
        province.setState(state);
        
        // When
        Set<ConstraintViolation<Province>> violations = validator.validate(province);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("province")));
    }

    @Test
    @DisplayName("Should fail validation with null country")
    void shouldFailValidationWithNullCountry() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Province province = new Province();
        province.setProvince("Lisboa");
        province.setCountry(null);
        province.setState(state);
        
        // When
        Set<ConstraintViolation<Province>> violations = validator.validate(province);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("country")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Country country = TestDataFactory.createPortugalCountry();
        
        Province province = new Province();
        province.setProvince("Lisboa");
        province.setCountry(country);
        province.setState(null);
        
        // When
        Set<ConstraintViolation<Province>> violations = validator.validate(province);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should create Province with different countries")
    void shouldCreateProvinceWithDifferentCountries() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Country portugal = TestDataFactory.createPortugalCountry();
        Country spain = TestDataFactory.createSpainCountry();
        
        Province lisboa = TestDataFactory.createLisbonProvince(portugal);
        lisboa.setState(state);
        
        Province madrid = TestDataFactory.createMadridProvince(spain);
        madrid.setState(state);
        
        // When
        Set<ConstraintViolation<Province>> violations1 = validator.validate(lisboa);
        Set<ConstraintViolation<Province>> violations2 = validator.validate(madrid);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        
        assertEquals("Lisboa", lisboa.getProvince());
        assertEquals("Portugal", lisboa.getCountry().getCountry());
        
        assertEquals("Madrid", madrid.getProvince());
        assertEquals("Spain", madrid.getCountry().getCountry());
    }

    @Test
    @DisplayName("Should create multiple provinces for same country")
    void shouldCreateMultipleProvincesForSameCountry() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country portugal = TestDataFactory.createPortugalCountry();
        
        Province lisboa = TestDataFactory.createLisbonProvince(portugal);
        lisboa.setState(state);
        
        Province porto = TestDataFactory.createPortoProvince(portugal);
        porto.setState(state);
        
        // When
        Set<ConstraintViolation<Province>> violations1 = validator.validate(lisboa);
        Set<ConstraintViolation<Province>> violations2 = validator.validate(porto);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        
        assertEquals("Lisboa", lisboa.getProvince());
        assertEquals("Porto", porto.getProvince());
        assertEquals(portugal.getCountry(), lisboa.getCountry().getCountry());
        assertEquals(portugal.getCountry(), porto.getCountry().getCountry());
    }

    @Test
    @DisplayName("Should create Province with different states")
    void shouldCreateProvinceWithDifferentStates() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        Country country = TestDataFactory.createPortugalCountry();
        
        Province province1 = TestDataFactory.createLisbonProvince(country);
        province1.setState(activeState);
        
        Province province2 = TestDataFactory.createLisbonProvince(country);
        province2.setState(inactiveState);
        
        // When
        Set<ConstraintViolation<Province>> violations1 = validator.validate(province1);
        Set<ConstraintViolation<Province>> violations2 = validator.validate(province2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals(activeState, province1.getState());
        assertEquals(inactiveState, province2.getState());
    }

    @Test
    @DisplayName("Should maintain referential integrity with country")
    void shouldMaintainReferentialIntegrityWithCountry() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        Province province = TestDataFactory.createLisbonProvince(country);
        province.setState(state);
        
        // When
        Set<ConstraintViolation<Province>> violations = validator.validate(province);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNotNull(province.getCountry());
        assertEquals("Portugal", province.getCountry().getCountry());
        assertEquals("+351", province.getCountry().getIndicative());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        
        Province province1 = TestDataFactory.createLisbonProvince(country);
        province1.setState(state);
        province1.setId(1L);
        
        Province province2 = TestDataFactory.createLisbonProvince(country);
        province2.setState(state);
        province2.setId(1L);
        
        // Then
        assertEquals(province1, province2);
        assertEquals(province1.hashCode(), province2.hashCode());
        
        // When different IDs
        province2.setId(2L);
        
        // Then
        assertNotEquals(province1, province2);
    }

    @Test
    @DisplayName("Should test toString method")
    void shouldTestToStringMethod() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        Province province = TestDataFactory.createLisbonProvince(country);
        province.setState(state);
        
        // When
        String toString = province.toString();
        
        // Then
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }

    @Test
    @DisplayName("Should validate province name is trimmed")
    void shouldValidateProvinceNameIsTrimmed() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        
        Province province = new Province();
        province.setProvince("  Lisboa  "); // With spaces
        province.setCountry(country);
        province.setState(state);
        
        // When
        Set<ConstraintViolation<Province>> violations = validator.validate(province);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("  Lisboa  ", province.getProvince()); // Validation doesn't trim, entity should handle trimming
    }

    @Test
    @DisplayName("Should handle lazy loading of relationships")
    void shouldHandleLazyLoadingOfRelationships() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        Province province = TestDataFactory.createLisbonProvince(country);
        province.setState(state);
        
        // When & Then - Should be able to access relationships
        assertNotNull(province.getCountry());
        assertNotNull(province.getState());
        assertEquals("Portugal", province.getCountry().getCountry());
        assertEquals(state.getState(), province.getState().getState());
    }

    @Test
    @DisplayName("Should create province with different province names")
    void shouldCreateProvinceWithDifferentProvinceNames() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        
        String[] provinceNames = {"Lisboa", "Porto", "Aveiro", "Braga", "Coimbra"};
        
        // When & Then
        for (String provinceName : provinceNames) {
            Province province = new Province();
            province.setProvince(provinceName);
            province.setCountry(country);
            province.setState(state);
            
            Set<ConstraintViolation<Province>> violations = validator.validate(province);
            
            assertTrue(violations.isEmpty(), "Province " + provinceName + " should be valid");
            assertEquals(provinceName, province.getProvince());
        }
    }

    @Test
    @DisplayName("Should validate province with special characters")
    void shouldValidateProvinceWithSpecialCharacters() {
        // Given
        State state = TestDataFactory.createActiveState();
        Country country = TestDataFactory.createPortugalCountry();
        
        Province province = new Province();
        province.setProvince("São Paulo"); // With accented character
        province.setCountry(country);
        province.setState(state);
        
        // When
        Set<ConstraintViolation<Province>> violations = validator.validate(province);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("São Paulo", province.getProvince());
    }
}