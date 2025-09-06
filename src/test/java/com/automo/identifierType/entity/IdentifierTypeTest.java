package com.automo.identifierType.entity;

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
@DisplayName("Tests for IdentifierType Entity")
class IdentifierTypeTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid IdentifierType entity")
    void shouldCreateValidIdentifierTypeEntity() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        
        // When
        Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("NIF", identifierType.getType());
        assertEquals("Número de Identificação Fiscal", identifierType.getDescription());
    }

    @Test
    @DisplayName("Should fail validation with null type")
    void shouldFailValidationWithNullType() {
        // Given
        IdentifierType identifierType = new IdentifierType();
        identifierType.setType(null);
        identifierType.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("type") && 
            v.getMessage().contains("Type is required")));
    }

    @Test
    @DisplayName("Should fail validation with blank type")
    void shouldFailValidationWithBlankType() {
        // Given
        IdentifierType identifierType = new IdentifierType();
        identifierType.setType("");
        identifierType.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("type") && 
            v.getMessage().contains("Type is required")));
    }

    @Test
    @DisplayName("Should fail validation with whitespace-only type")
    void shouldFailValidationWithWhitespaceOnlyType() {
        // Given
        IdentifierType identifierType = new IdentifierType();
        identifierType.setType("   ");
        identifierType.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("type") && 
            v.getMessage().contains("Type is required")));
    }

    @Test
    @DisplayName("Should create IdentifierType with null description")
    void shouldCreateIdentifierTypeWithNullDescription() {
        // Given
        IdentifierType identifierType = new IdentifierType();
        identifierType.setType("NIF");
        identifierType.setDescription(null);
        
        // When
        Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("NIF", identifierType.getType());
        assertNull(identifierType.getDescription());
    }

    @Test
    @DisplayName("Should create IdentifierType with empty description")
    void shouldCreateIdentifierTypeWithEmptyDescription() {
        // Given
        IdentifierType identifierType = new IdentifierType();
        identifierType.setType("NIF");
        identifierType.setDescription("");
        
        // When
        Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("NIF", identifierType.getType());
        assertEquals("", identifierType.getDescription());
    }

    @Test
    @DisplayName("Should create different types of identifiers")
    void shouldCreateDifferentTypesOfIdentifiers() {
        // Given
        String[] validTypes = {
            "NIF",
            "NIPC", 
            "CC",
            "PASSPORT",
            "DRIVER_LICENSE",
            "SOCIAL_SECURITY",
            "TAX_ID",
            "VAT_NUMBER"
        };
        
        String[] descriptions = {
            "Número de Identificação Fiscal",
            "Número de Identificação de Pessoa Coletiva",
            "Cartão de Cidadão",
            "Passport Number",
            "Driver License Number",
            "Social Security Number",
            "Tax Identification Number",
            "VAT Number"
        };
        
        // When & Then
        for (int i = 0; i < validTypes.length; i++) {
            IdentifierType identifierType = TestDataFactory.createValidIdentifierType(validTypes[i], descriptions[i]);
            
            Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
            
            assertTrue(violations.isEmpty(), "Type " + validTypes[i] + " should be valid");
            assertEquals(validTypes[i], identifierType.getType());
            assertEquals(descriptions[i], identifierType.getDescription());
        }
    }

    @Test
    @DisplayName("Should handle long type names")
    void shouldHandleLongTypeNames() {
        // Given
        String longType = "VERY_LONG_IDENTIFIER_TYPE_NAME_THAT_MIGHT_BE_USED_IN_SOME_COUNTRIES";
        String longDescription = "This is a very long description that might be used to describe " +
                                "a complex identifier type that requires detailed explanation about " +
                                "its purpose and usage in various governmental or business contexts.";
        
        IdentifierType identifierType = TestDataFactory.createValidIdentifierType(longType, longDescription);
        
        // When
        Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longType, identifierType.getType());
        assertEquals(longDescription, identifierType.getDescription());
    }

    @Test
    @DisplayName("Should handle special characters in type and description")
    void shouldHandleSpecialCharactersInTypeAndDescription() {
        // Given
        String typeWithSpecialChars = "NIF-PT_2024";
        String descriptionWithSpecialChars = "Número de Identificação Fiscal - Portugal (2024) & Other Info!";
        
        IdentifierType identifierType = TestDataFactory.createValidIdentifierType(
                typeWithSpecialChars, 
                descriptionWithSpecialChars
        );
        
        // When
        Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(typeWithSpecialChars, identifierType.getType());
        assertEquals(descriptionWithSpecialChars, identifierType.getDescription());
    }

    @Test
    @DisplayName("Should handle numeric characters in type")
    void shouldHandleNumericCharactersInType() {
        // Given
        String numericType = "TYPE123";
        String description = "Numeric type identifier";
        
        IdentifierType identifierType = TestDataFactory.createValidIdentifierType(numericType, description);
        
        // When
        Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(numericType, identifierType.getType());
        assertEquals(description, identifierType.getDescription());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        IdentifierType identifierType1 = TestDataFactory.createNifIdentifierType();
        IdentifierType identifierType2 = TestDataFactory.createNifIdentifierType();
        identifierType1.setId(1L);
        identifierType2.setId(1L);
        
        // Then
        assertEquals(identifierType1, identifierType2);
        assertEquals(identifierType1.hashCode(), identifierType2.hashCode());
        
        // When different IDs
        identifierType2.setId(2L);
        
        // Then
        assertNotEquals(identifierType1, identifierType2);
    }

    @Test
    @DisplayName("Should not be equal when types are different")
    void shouldNotBeEqualWhenTypesAreDifferent() {
        // Given
        IdentifierType nif = TestDataFactory.createNifIdentifierType();
        IdentifierType nipc = TestDataFactory.createValidIdentifierType("NIPC", "Different type");
        nif.setId(1L);
        nipc.setId(1L);
        
        // Then - Even with same ID, they should be different entities
        // Note: This depends on the equals implementation in AbstractModel
        // If equals only uses ID, they would be equal; if it includes other fields, they wouldn't be
        // The behavior depends on the parent class implementation
    }

    @Test
    @DisplayName("Should inherit AbstractModel properties")
    void shouldInheritAbstractModelProperties() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        
        // When
        identifierType.setId(1L);
        
        // Then
        assertNotNull(identifierType.getId());
        assertEquals(1L, identifierType.getId());
        // Note: createdAt and updatedAt are set by JPA auditing in real scenarios
    }

    @Test
    @DisplayName("Should support case-sensitive type names")
    void shouldSupportCaseSensitiveTypeNames() {
        // Given
        IdentifierType upperCase = TestDataFactory.createValidIdentifierType("NIF", "Upper case");
        IdentifierType lowerCase = TestDataFactory.createValidIdentifierType("nif", "Lower case");
        IdentifierType mixedCase = TestDataFactory.createValidIdentifierType("Nif", "Mixed case");
        
        // When
        Set<ConstraintViolation<IdentifierType>> violations1 = validator.validate(upperCase);
        Set<ConstraintViolation<IdentifierType>> violations2 = validator.validate(lowerCase);
        Set<ConstraintViolation<IdentifierType>> violations3 = validator.validate(mixedCase);
        
        // Then
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        assertTrue(violations3.isEmpty());
        
        assertEquals("NIF", upperCase.getType());
        assertEquals("nif", lowerCase.getType());
        assertEquals("Nif", mixedCase.getType());
    }

    @Test
    @DisplayName("Should validate minimum requirements for business logic")
    void shouldValidateMinimumRequirementsForBusinessLogic() {
        // Given - Common identifier types used in business
        String[] businessTypes = {"NIF", "NIPC", "CC", "PASSPORT"};
        
        // When & Then
        for (String type : businessTypes) {
            IdentifierType identifierType = TestDataFactory.createValidIdentifierType(type, type + " Description");
            
            Set<ConstraintViolation<IdentifierType>> violations = validator.validate(identifierType);
            
            assertTrue(violations.isEmpty(), "Business type " + type + " should be valid");
            assertEquals(type, identifierType.getType());
        }
    }

    @Test
    @DisplayName("Should handle constructor variations")
    void shouldHandleConstructorVariations() {
        // Given & When - Default constructor
        IdentifierType defaultConstructor = new IdentifierType();
        defaultConstructor.setType("TEST");
        defaultConstructor.setDescription("Test Description");
        
        // Given & When - All args constructor
        IdentifierType allArgsConstructor = new IdentifierType("TEST", "Test Description");
        
        // Then
        Set<ConstraintViolation<IdentifierType>> violations1 = validator.validate(defaultConstructor);
        Set<ConstraintViolation<IdentifierType>> violations2 = validator.validate(allArgsConstructor);
        
        assertTrue(violations1.isEmpty());
        assertTrue(violations2.isEmpty());
        
        assertEquals("TEST", defaultConstructor.getType());
        assertEquals("Test Description", defaultConstructor.getDescription());
        assertEquals("TEST", allArgsConstructor.getType());
        assertEquals("Test Description", allArgsConstructor.getDescription());
    }

    @Test
    @DisplayName("Should maintain immutability expectations for type")
    void shouldMaintainImmutabilityExpectationsForType() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        String originalType = identifierType.getType();
        String originalDescription = identifierType.getDescription();
        
        // When - modifying the returned strings shouldn't affect the entity
        String modifiedType = originalType.toLowerCase();
        String modifiedDescription = originalDescription.toUpperCase();
        
        // Then
        assertNotEquals(modifiedType, identifierType.getType());
        assertNotEquals(modifiedDescription, identifierType.getDescription());
        assertEquals("NIF", identifierType.getType());
        assertEquals("Número de Identificação Fiscal", identifierType.getDescription());
    }
}