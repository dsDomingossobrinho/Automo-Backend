package com.automo.notificationType.entity;

import com.automo.test.config.BaseTestConfig;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@BaseTestConfig
@DisplayName("Tests for NotificationType Entity")
class NotificationTypeTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid NotificationType entity")
    void shouldCreateValidNotificationTypeEntity() {
        // Given
        NotificationType notificationType = new NotificationType();
        notificationType.setType("EMAIL");
        notificationType.setDescription("Email notification type");
        
        // When
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("EMAIL", notificationType.getType());
        assertEquals("Email notification type", notificationType.getDescription());
    }

    @Test
    @DisplayName("Should fail validation with null type")
    void shouldFailValidationWithNullType() {
        // Given
        NotificationType notificationType = new NotificationType();
        notificationType.setType(null);
        notificationType.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("type") &&
            v.getMessage().equals("Type is required")));
    }

    @Test
    @DisplayName("Should fail validation with blank type")
    void shouldFailValidationWithBlankType() {
        // Given
        NotificationType notificationType = new NotificationType();
        notificationType.setType("");
        notificationType.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("type") &&
            v.getMessage().equals("Type is required")));
    }

    @Test
    @DisplayName("Should fail validation with whitespace-only type")
    void shouldFailValidationWithWhitespaceOnlyType() {
        // Given
        NotificationType notificationType = new NotificationType();
        notificationType.setType("   ");
        notificationType.setDescription("Test description");
        
        // When
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("type") &&
            v.getMessage().equals("Type is required")));
    }

    @Test
    @DisplayName("Should create NotificationType without description")
    void shouldCreateNotificationTypeWithoutDescription() {
        // Given
        NotificationType notificationType = new NotificationType();
        notificationType.setType("SMS");
        notificationType.setDescription(null);
        
        // When
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("SMS", notificationType.getType());
        assertNull(notificationType.getDescription());
    }

    @Test
    @DisplayName("Should create NotificationType with empty description")
    void shouldCreateNotificationTypeWithEmptyDescription() {
        // Given
        NotificationType notificationType = new NotificationType();
        notificationType.setType("PUSH");
        notificationType.setDescription("");
        
        // When
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("PUSH", notificationType.getType());
        assertEquals("", notificationType.getDescription());
    }

    @Test
    @DisplayName("Should create NotificationType with different types")
    void shouldCreateNotificationTypeWithDifferentTypes() {
        // Given
        NotificationType emailType = new NotificationType();
        emailType.setType("EMAIL");
        emailType.setDescription("Email notifications");
        
        NotificationType smsType = new NotificationType();
        smsType.setType("SMS");
        smsType.setDescription("SMS notifications");
        
        NotificationType pushType = new NotificationType();
        pushType.setType("PUSH");
        pushType.setDescription("Push notifications");
        
        // When
        Set<ConstraintViolation<NotificationType>> emailViolations = validator.validate(emailType);
        Set<ConstraintViolation<NotificationType>> smsViolations = validator.validate(smsType);
        Set<ConstraintViolation<NotificationType>> pushViolations = validator.validate(pushType);
        
        // Then
        assertTrue(emailViolations.isEmpty());
        assertTrue(smsViolations.isEmpty());
        assertTrue(pushViolations.isEmpty());
        
        assertEquals("EMAIL", emailType.getType());
        assertEquals("SMS", smsType.getType());
        assertEquals("PUSH", pushType.getType());
    }

    @Test
    @DisplayName("Should create NotificationType with long description")
    void shouldCreateNotificationTypeWithLongDescription() {
        // Given
        String longDescription = "This is a very long description for the notification type that contains " +
                                "multiple sentences and provides detailed information about what this " +
                                "notification type is used for in the system. It can handle large amounts " +
                                "of text without any issues and should be stored properly in the database.";
        
        NotificationType notificationType = new NotificationType();
        notificationType.setType("DETAILED");
        notificationType.setDescription(longDescription);
        
        // When
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("DETAILED", notificationType.getType());
        assertEquals(longDescription, notificationType.getDescription());
    }

    @Test
    @DisplayName("Should create NotificationType with special characters in type")
    void shouldCreateNotificationTypeWithSpecialCharactersInType() {
        // Given
        NotificationType notificationType = new NotificationType();
        notificationType.setType("CUSTOM_TYPE_123");
        notificationType.setDescription("Custom notification type with numbers and underscore");
        
        // When
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("CUSTOM_TYPE_123", notificationType.getType());
    }

    @Test
    @DisplayName("Should create NotificationType with special characters in description")
    void shouldCreateNotificationTypeWithSpecialCharactersInDescription() {
        // Given
        String specialDescription = "Description with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        
        NotificationType notificationType = new NotificationType();
        notificationType.setType("SPECIAL");
        notificationType.setDescription(specialDescription);
        
        // When
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("SPECIAL", notificationType.getType());
        assertEquals(specialDescription, notificationType.getDescription());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        NotificationType notificationType1 = new NotificationType();
        notificationType1.setType("EMAIL");
        notificationType1.setDescription("Email notifications");
        notificationType1.setId(1L);
        
        NotificationType notificationType2 = new NotificationType();
        notificationType2.setType("EMAIL");
        notificationType2.setDescription("Email notifications");
        notificationType2.setId(1L);
        
        // Then
        assertEquals(notificationType1, notificationType2);
        assertEquals(notificationType1.hashCode(), notificationType2.hashCode());
        
        // When different IDs
        notificationType2.setId(2L);
        
        // Then
        assertNotEquals(notificationType1, notificationType2);
    }

    @Test
    @DisplayName("Should create NotificationType using all-args constructor")
    void shouldCreateNotificationTypeUsingAllArgsConstructor() {
        // Given & When
        NotificationType notificationType = new NotificationType("WEBHOOK", "Webhook notifications");
        
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("WEBHOOK", notificationType.getType());
        assertEquals("Webhook notifications", notificationType.getDescription());
    }

    @Test
    @DisplayName("Should create NotificationType using no-args constructor")
    void shouldCreateNotificationTypeUsingNoArgsConstructor() {
        // Given & When
        NotificationType notificationType = new NotificationType();
        notificationType.setType("DEFAULT");
        notificationType.setDescription("Default notification type");
        
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("DEFAULT", notificationType.getType());
        assertEquals("Default notification type", notificationType.getDescription());
    }

    @Test
    @DisplayName("Should handle unicode characters in type and description")
    void shouldHandleUnicodeCharactersInTypeAndDescription() {
        // Given
        NotificationType notificationType = new NotificationType();
        notificationType.setType("UNICODE_测试");
        notificationType.setDescription("Description with unicode: 测试, éñ, 🔔");
        
        // When
        Set<ConstraintViolation<NotificationType>> violations = validator.validate(notificationType);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("UNICODE_测试", notificationType.getType());
        assertEquals("Description with unicode: 测试, éñ, 🔔", notificationType.getDescription());
    }

    @Test
    @DisplayName("Should maintain immutability for validation constraints")
    void shouldMaintainImmutabilityForValidationConstraints() {
        // Given
        NotificationType notificationType = new NotificationType();
        notificationType.setType("IMMUTABLE");
        notificationType.setDescription("Initial description");
        
        // When - First validation
        Set<ConstraintViolation<NotificationType>> violations1 = validator.validate(notificationType);
        
        // Then
        assertTrue(violations1.isEmpty());
        
        // When - Change type to invalid and validate again
        notificationType.setType("");
        Set<ConstraintViolation<NotificationType>> violations2 = validator.validate(notificationType);
        
        // Then
        assertFalse(violations2.isEmpty());
        assertTrue(violations2.stream().anyMatch(v -> v.getPropertyPath().toString().equals("type")));
        
        // When - Fix type and validate again
        notificationType.setType("FIXED");
        Set<ConstraintViolation<NotificationType>> violations3 = validator.validate(notificationType);
        
        // Then
        assertTrue(violations3.isEmpty());
    }
}