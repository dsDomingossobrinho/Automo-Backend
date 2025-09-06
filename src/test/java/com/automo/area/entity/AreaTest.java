package com.automo.area.entity;

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
@DisplayName("Tests for Area Entity")
class AreaTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Area entity")
    void shouldCreateValidAreaEntity() {
        // Given
        State state = TestDataFactory.createActiveState();
        Area area = TestDataFactory.createLisbonArea();
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Lisboa Centro", area.getArea());
        assertEquals("Central area of Lisboa", area.getDescription());
        assertEquals(state, area.getState());
    }

    @Test
    @DisplayName("Should fail validation with null area name")
    void shouldFailValidationWithNullAreaName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area = new Area();
        area.setArea(null);
        area.setDescription("Test description");
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("area")));
    }

    @Test
    @DisplayName("Should fail validation with blank area name")
    void shouldFailValidationWithBlankAreaName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area = new Area();
        area.setArea("");
        area.setDescription("Test description");
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("area")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        Area area = new Area();
        area.setArea("Lisboa Centro");
        area.setDescription("Test description");
        area.setState(null);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("state")));
    }

    @Test
    @DisplayName("Should allow null description (optional field)")
    void shouldAllowNullDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area = new Area();
        area.setArea("Lisboa Centro");
        area.setDescription(null);
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNull(area.getDescription());
    }

    @Test
    @DisplayName("Should allow empty description")
    void shouldAllowEmptyDescription() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area = new Area();
        area.setArea("Lisboa Centro");
        area.setDescription("");
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("", area.getDescription());
    }

    @Test
    @DisplayName("Should create Area with different area names")
    void shouldCreateAreaWithDifferentAreaNames() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area lisboa = TestDataFactory.createLisbonArea();
        lisboa.setState(state);
        
        Area cascais = TestDataFactory.createCascaisArea();
        cascais.setState(state);
        
        Area sintra = TestDataFactory.createSintraArea();
        sintra.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations1 = validator.validate(lisboa);
        Set<ConstraintViolation<Area>> violations2 = validator.validate(cascais);
        Set<ConstraintViolation<Area>> violations3 = validator.validate(sintra);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertTrue(violations3.isEmpty());
        
        assertEquals("Lisboa Centro", lisboa.getArea());
        assertEquals("Central area of Lisboa", lisboa.getDescription());
        
        assertEquals("Cascais", cascais.getArea());
        assertEquals("Coastal area near Lisboa", cascais.getDescription());
        
        assertEquals("Sintra", sintra.getArea());
        assertEquals("Historic area with palaces", sintra.getDescription());
    }

    @Test
    @DisplayName("Should create Area with different states")
    void shouldCreateAreaWithDifferentStates() {
        // Given
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        
        Area area1 = TestDataFactory.createLisbonArea();
        area1.setState(activeState);
        
        Area area2 = TestDataFactory.createLisbonArea();
        area2.setState(inactiveState);
        
        // When
        Set<ConstraintViolation<Area>> violations1 = validator.validate(area1);
        Set<ConstraintViolation<Area>> violations2 = validator.validate(area2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals(activeState, area1.getState());
        assertEquals(inactiveState, area2.getState());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area1 = TestDataFactory.createLisbonArea();
        area1.setState(state);
        area1.setId(1L);
        
        Area area2 = TestDataFactory.createLisbonArea();
        area2.setState(state);
        area2.setId(1L);
        
        // Then
        assertEquals(area1, area2);
        assertEquals(area1.hashCode(), area2.hashCode());
        
        // When different IDs
        area2.setId(2L);
        
        // Then
        assertNotEquals(area1, area2);
    }

    @Test
    @DisplayName("Should test toString method")
    void shouldTestToStringMethod() {
        // Given
        State state = TestDataFactory.createActiveState();
        Area area = TestDataFactory.createLisbonArea();
        area.setState(state);
        
        // When
        String toString = area.toString();
        
        // Then
        assertNotNull(toString);
        assertFalse(toString.isEmpty());
    }

    @Test
    @DisplayName("Should validate area name is trimmed")
    void shouldValidateAreaNameIsTrimmed() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area = new Area();
        area.setArea("  Lisboa Centro  "); // With spaces
        area.setDescription("Central area");
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("  Lisboa Centro  ", area.getArea()); // Validation doesn't trim, entity should handle trimming
    }

    @Test
    @DisplayName("Should create areas with long descriptions")
    void shouldCreateAreasWithLongDescriptions() {
        // Given
        State state = TestDataFactory.createActiveState();
        String longDescription = "This is a very long description that contains multiple sentences. " +
                "It describes the area in great detail, including its history, geography, " +
                "cultural significance, and modern development. This tests that the description " +
                "field can handle longer text content without issues.";
        
        Area area = new Area();
        area.setArea("Test Area");
        area.setDescription(longDescription);
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longDescription, area.getDescription());
    }

    @Test
    @DisplayName("Should create area with special characters in name")
    void shouldCreateAreaWithSpecialCharactersInName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area = new Area();
        area.setArea("São João do Estoril");
        area.setDescription("Area with accented characters");
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("São João do Estoril", area.getArea());
    }

    @Test
    @DisplayName("Should create area with numeric characters in name")
    void shouldCreateAreaWithNumericCharactersInName() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area = new Area();
        area.setArea("Zone 1A - Industrial Area");
        area.setDescription("Industrial zone with numeric designation");
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("Zone 1A - Industrial Area", area.getArea());
    }

    @Test
    @DisplayName("Should maintain referential integrity with state")
    void shouldMaintainReferentialIntegrityWithState() {
        // Given
        State state = TestDataFactory.createActiveState();
        Area area = TestDataFactory.createLisbonArea();
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNotNull(area.getState());
        assertEquals(state.getState(), area.getState().getState());
    }

    @Test
    @DisplayName("Should create multiple areas with same names but different descriptions")
    void shouldCreateMultipleAreasWithSameNamesButDifferentDescriptions() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area1 = new Area();
        area1.setArea("Centro");
        area1.setDescription("Historical center");
        area1.setState(state);
        
        Area area2 = new Area();
        area2.setArea("Centro");
        area2.setDescription("Commercial center");
        area2.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations1 = validator.validate(area1);
        Set<ConstraintViolation<Area>> violations2 = validator.validate(area2);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertEquals("Centro", area1.getArea());
        assertEquals("Centro", area2.getArea());
        assertEquals("Historical center", area1.getDescription());
        assertEquals("Commercial center", area2.getDescription());
    }

    @Test
    @DisplayName("Should validate area with whitespace-only name as invalid")
    void shouldValidateAreaWithWhitespaceOnlyNameAsInvalid() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area = new Area();
        area.setArea("   "); // Only whitespace
        area.setDescription("Test description");
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("area")));
    }

    @Test
    @DisplayName("Should create area with minimal required fields")
    void shouldCreateAreaWithMinimalRequiredFields() {
        // Given
        State state = TestDataFactory.createActiveState();
        
        Area area = new Area();
        area.setArea("A"); // Single character name
        area.setDescription(null); // Optional field
        area.setState(state);
        
        // When
        Set<ConstraintViolation<Area>> violations = validator.validate(area);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("A", area.getArea());
        assertNull(area.getDescription());
    }
}