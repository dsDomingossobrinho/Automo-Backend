package com.automo.notification.entity;

import com.automo.identifier.entity.Identifier;
import com.automo.identifierType.entity.IdentifierType;
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
@DisplayName("Tests for Notification Entity")
class NotificationTest {

    @Autowired
    private Validator validator;

    @Test
    @DisplayName("Should create valid Notification entity")
    void shouldCreateValidNotificationEntity() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier sender = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        sender.setId(1L);
        Identifier receiver = TestDataFactory.createValidIdentifier(2L, identifierType, state);
        receiver.setId(2L);
        
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setUrlRedirect("https://example.com/redirect");
        notification.setState(state);
        
        // When
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(sender, notification.getSender());
        assertEquals(receiver, notification.getReceiver());
        assertEquals("https://example.com/redirect", notification.getUrlRedirect());
        assertEquals(state, notification.getState());
    }

    @Test
    @DisplayName("Should fail validation with null sender")
    void shouldFailValidationWithNullSender() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier receiver = TestDataFactory.createValidIdentifier(2L, identifierType, state);
        receiver.setId(2L);
        
        Notification notification = new Notification();
        notification.setSender(null);
        notification.setReceiver(receiver);
        notification.setUrlRedirect("https://example.com/redirect");
        notification.setState(state);
        
        // When
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("sender") &&
            v.getMessage().equals("Sender is required")));
    }

    @Test
    @DisplayName("Should fail validation with null receiver")
    void shouldFailValidationWithNullReceiver() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier sender = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        sender.setId(1L);
        
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(null);
        notification.setUrlRedirect("https://example.com/redirect");
        notification.setState(state);
        
        // When
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("receiver") &&
            v.getMessage().equals("Receiver is required")));
    }

    @Test
    @DisplayName("Should fail validation with null state")
    void shouldFailValidationWithNullState() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier sender = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        sender.setId(1L);
        Identifier receiver = TestDataFactory.createValidIdentifier(2L, identifierType, state);
        receiver.setId(2L);
        
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setUrlRedirect("https://example.com/redirect");
        notification.setState(null);
        
        // When
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> 
            v.getPropertyPath().toString().equals("state") &&
            v.getMessage().equals("State is required")));
    }

    @Test
    @DisplayName("Should create notification without urlRedirect")
    void shouldCreateNotificationWithoutUrlRedirect() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier sender = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        sender.setId(1L);
        Identifier receiver = TestDataFactory.createValidIdentifier(2L, identifierType, state);
        receiver.setId(2L);
        
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setUrlRedirect(null);
        notification.setState(state);
        
        // When
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Then
        assertTrue(violations.isEmpty());
        assertNull(notification.getUrlRedirect());
    }

    @Test
    @DisplayName("Should create notification with different states")
    void shouldCreateNotificationWithDifferentStates() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State activeState = TestDataFactory.createActiveState();
        State inactiveState = TestDataFactory.createInactiveState();
        
        Identifier sender = TestDataFactory.createValidIdentifier(1L, identifierType, activeState);
        sender.setId(1L);
        Identifier receiver = TestDataFactory.createValidIdentifier(2L, identifierType, activeState);
        receiver.setId(2L);
        
        Notification activeNotification = new Notification();
        activeNotification.setSender(sender);
        activeNotification.setReceiver(receiver);
        activeNotification.setState(activeState);
        
        Notification inactiveNotification = new Notification();
        inactiveNotification.setSender(sender);
        inactiveNotification.setReceiver(receiver);
        inactiveNotification.setState(inactiveState);
        
        // When
        Set<ConstraintViolation<Notification>> activeViolations = validator.validate(activeNotification);
        Set<ConstraintViolation<Notification>> inactiveViolations = validator.validate(inactiveNotification);
        
        // Then
        assertTrue(activeViolations.isEmpty());
        assertTrue(inactiveViolations.isEmpty());
        assertEquals("ACTIVE", activeNotification.getState().getState());
        assertEquals("INACTIVE", inactiveNotification.getState().getState());
    }

    @Test
    @DisplayName("Should test equals and hashCode methods")
    void shouldTestEqualsAndHashCodeMethods() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier sender = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        sender.setId(1L);
        Identifier receiver = TestDataFactory.createValidIdentifier(2L, identifierType, state);
        receiver.setId(2L);
        
        Notification notification1 = new Notification();
        notification1.setSender(sender);
        notification1.setReceiver(receiver);
        notification1.setState(state);
        notification1.setId(1L);
        
        Notification notification2 = new Notification();
        notification2.setSender(sender);
        notification2.setReceiver(receiver);
        notification2.setState(state);
        notification2.setId(1L);
        
        // Then
        assertEquals(notification1, notification2);
        assertEquals(notification1.hashCode(), notification2.hashCode());
        
        // When different IDs
        notification2.setId(2L);
        
        // Then
        assertNotEquals(notification1, notification2);
    }

    @Test
    @DisplayName("Should handle notification with same sender and receiver")
    void shouldHandleNotificationWithSameSenderAndReceiver() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier identifier = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        identifier.setId(1L);
        
        Notification notification = new Notification();
        notification.setSender(identifier);
        notification.setReceiver(identifier);
        notification.setState(state);
        notification.setUrlRedirect("https://example.com/self-notification");
        
        // When
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(notification.getSender(), notification.getReceiver());
    }

    @Test
    @DisplayName("Should create notification with long URL redirect")
    void shouldCreateNotificationWithLongUrlRedirect() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier sender = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        sender.setId(1L);
        Identifier receiver = TestDataFactory.createValidIdentifier(2L, identifierType, state);
        receiver.setId(2L);
        
        String longUrl = "https://example.com/very/long/path/with/many/segments/and/parameters?param1=value1&param2=value2&param3=value3";
        
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setUrlRedirect(longUrl);
        notification.setState(state);
        
        // When
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals(longUrl, notification.getUrlRedirect());
    }

    @Test
    @DisplayName("Should create notification with empty URL redirect")
    void shouldCreateNotificationWithEmptyUrlRedirect() {
        // Given
        IdentifierType identifierType = TestDataFactory.createNifIdentifierType();
        State state = TestDataFactory.createActiveState();
        
        Identifier sender = TestDataFactory.createValidIdentifier(1L, identifierType, state);
        sender.setId(1L);
        Identifier receiver = TestDataFactory.createValidIdentifier(2L, identifierType, state);
        receiver.setId(2L);
        
        Notification notification = new Notification();
        notification.setSender(sender);
        notification.setReceiver(receiver);
        notification.setUrlRedirect("");
        notification.setState(state);
        
        // When
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        
        // Then
        assertTrue(violations.isEmpty());
        assertEquals("", notification.getUrlRedirect());
    }
}