package com.automo.associatedContact.controller;

import com.automo.associatedContact.dto.AssociatedContactDto;
import com.automo.associatedContact.response.AssociatedContactResponse;
import com.automo.associatedContact.service.AssociatedContactService;
import com.automo.config.security.JwtUtils;
import com.automo.test.utils.TestDataFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AssociatedContactController.class)
@ActiveProfiles("test")
@DisplayName("Tests for AssociatedContactController")
class AssociatedContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssociatedContactService associatedContactService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private AssociatedContactDto contactDto;
    private AssociatedContactResponse contactResponse;

    @BeforeEach
    void setUp() {
        contactDto = TestDataFactory.createValidAssociatedContactDto(1L, "912345678", 1L);
        contactResponse = new AssociatedContactResponse(
            1L, 
            1L, 
            "Test User", 
            "912345678", 
            1L, 
            "ACTIVE", 
            LocalDateTime.now(), 
            LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all associated contacts successfully")
    void shouldGetAllAssociatedContactsSuccessfully() throws Exception {
        // Given
        AssociatedContactResponse contact1 = new AssociatedContactResponse(1L, 1L, "User1", "912345678", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        AssociatedContactResponse contact2 = new AssociatedContactResponse(2L, 1L, "User2", "913456789", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        List<AssociatedContactResponse> contacts = Arrays.asList(contact1, contact2);

        when(associatedContactService.getAllAssociatedContacts()).thenReturn(contacts);

        // When & Then
        mockMvc.perform(get("/associated-contacts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].contact").value("912345678"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].contact").value("913456789"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get associated contact by id successfully")
    void shouldGetAssociatedContactByIdSuccessfully() throws Exception {
        // Given
        Long contactId = 1L;
        when(associatedContactService.getAssociatedContactByIdResponse(contactId)).thenReturn(contactResponse);

        // When & Then
        mockMvc.perform(get("/associated-contacts/{id}", contactId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contact").value("912345678"))
                .andExpect(jsonPath("$.userName").value("Test User"))
                .andExpect(jsonPath("$.stateId").value(1))
                .andExpect(jsonPath("$.stateName").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create associated contact successfully")
    void shouldCreateAssociatedContactSuccessfully() throws Exception {
        // Given
        when(associatedContactService.createAssociatedContact(any(AssociatedContactDto.class))).thenReturn(contactResponse);

        // When & Then
        mockMvc.perform(post("/associated-contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contact").value("912345678"))
                .andExpect(jsonPath("$.identifierId").value(1))
                .andExpect(jsonPath("$.userName").value("Test User"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update associated contact successfully")
    void shouldUpdateAssociatedContactSuccessfully() throws Exception {
        // Given
        Long contactId = 1L;
        AssociatedContactDto updateDto = TestDataFactory.createValidAssociatedContactDto(1L, "913456789", 1L);
        AssociatedContactResponse updatedResponse = new AssociatedContactResponse(
            1L, 1L, "Test User", "913456789", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );

        when(associatedContactService.updateAssociatedContact(eq(contactId), any(AssociatedContactDto.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/associated-contacts/{id}", contactId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contact").value("913456789"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete associated contact successfully")
    void shouldDeleteAssociatedContactSuccessfully() throws Exception {
        // Given
        Long contactId = 1L;
        doNothing().when(associatedContactService).deleteAssociatedContact(contactId);

        // When & Then
        mockMvc.perform(delete("/associated-contacts/{id}", contactId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get associated contacts by identifier successfully")
    void shouldGetAssociatedContactsByIdentifierSuccessfully() throws Exception {
        // Given
        Long identifierId = 1L;
        List<AssociatedContactResponse> contacts = Arrays.asList(contactResponse);

        when(associatedContactService.getAssociatedContactsByIdentifier(identifierId)).thenReturn(contacts);

        // When & Then
        mockMvc.perform(get("/associated-contacts/identifier/{identifierId}", identifierId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].identifierId").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get associated contacts by state successfully")
    void shouldGetAssociatedContactsByStateSuccessfully() throws Exception {
        // Given
        Long stateId = 1L;
        List<AssociatedContactResponse> contacts = Arrays.asList(contactResponse);

        when(associatedContactService.getAssociatedContactsByState(stateId)).thenReturn(contacts);

        // When & Then
        mockMvc.perform(get("/associated-contacts/state/{stateId}", stateId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].stateId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return bad request for invalid contact data")
    void shouldReturnBadRequestForInvalidContactData() throws Exception {
        // Given - Invalid DTO with null values
        AssociatedContactDto invalidDto = TestDataFactory.createValidAssociatedContactDto(null, null, null);

        // When & Then
        mockMvc.perform(post("/associated-contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return bad request for blank contact")
    void shouldReturnBadRequestForBlankContact() throws Exception {
        // Given - DTO with blank contact
        AssociatedContactDto invalidDto = TestDataFactory.createValidAssociatedContactDto(1L, "", 1L);

        // When & Then
        mockMvc.perform(post("/associated-contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should validate required fields in update request")
    void shouldValidateRequiredFieldsInUpdateRequest() throws Exception {
        // Given
        Long contactId = 1L;
        AssociatedContactDto invalidDto = TestDataFactory.createValidAssociatedContactDto(null, null, null);

        // When & Then
        mockMvc.perform(put("/associated-contacts/{id}", contactId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle empty list when no contacts found")
    void shouldHandleEmptyListWhenNoContactsFound() throws Exception {
        // Given
        when(associatedContactService.getAllAssociatedContacts()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/associated-contacts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle empty list when no contacts found by identifier")
    void shouldHandleEmptyListWhenNoContactsFoundByIdentifier() throws Exception {
        // Given
        Long identifierId = 999L;
        when(associatedContactService.getAssociatedContactsByIdentifier(identifierId)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/associated-contacts/identifier/{identifierId}", identifierId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle empty list when no contacts found by state")
    void shouldHandleEmptyListWhenNoContactsFoundByState() throws Exception {
        // Given
        Long stateId = 999L;
        when(associatedContactService.getAssociatedContactsByState(stateId)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/associated-contacts/state/{stateId}", stateId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle various contact formats in creation")
    void shouldHandleVariousContactFormatsInCreation() throws Exception {
        // Given
        String[] contactFormats = {
            "912345678",           // Standard format
            "+351912345678",       // With country code
            "912-345-678",         // With dashes
            "912 345 678"          // With spaces
        };

        for (String contact : contactFormats) {
            AssociatedContactDto dto = TestDataFactory.createValidAssociatedContactDto(1L, contact, 1L);
            AssociatedContactResponse response = new AssociatedContactResponse(
                1L, 1L, "Test User", contact, 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
            );
            
            when(associatedContactService.createAssociatedContact(any(AssociatedContactDto.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/associated-contacts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
                    .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.contact").value(contact));
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle multiple contacts for same identifier")
    void shouldHandleMultipleContactsForSameIdentifier() throws Exception {
        // Given
        Long identifierId = 1L;
        AssociatedContactResponse contact1 = new AssociatedContactResponse(1L, identifierId, "User", "912345678", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        AssociatedContactResponse contact2 = new AssociatedContactResponse(2L, identifierId, "User", "913456789", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        List<AssociatedContactResponse> contacts = Arrays.asList(contact1, contact2);

        when(associatedContactService.getAssociatedContactsByIdentifier(identifierId)).thenReturn(contacts);

        // When & Then
        mockMvc.perform(get("/associated-contacts/identifier/{identifierId}", identifierId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].identifierId").value(identifierId))
                .andExpect(jsonPath("$[1].identifierId").value(identifierId))
                .andExpect(jsonPath("$[0].contact").value("912345678"))
                .andExpect(jsonPath("$[1].contact").value("913456789"));
    }

    @Test
    @DisplayName("Should require authentication for all endpoints")
    void shouldRequireAuthenticationForAllEndpoints() throws Exception {
        // Test GET /associated-contacts
        mockMvc.perform(get("/associated-contacts"))
                .andExpect(status().isUnauthorized());

        // Test GET /associated-contacts/{id}
        mockMvc.perform(get("/associated-contacts/1"))
                .andExpect(status().isUnauthorized());

        // Test POST /associated-contacts
        mockMvc.perform(post("/associated-contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactDto)))
                .andExpect(status().isUnauthorized());

        // Test PUT /associated-contacts/{id}
        mockMvc.perform(put("/associated-contacts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactDto)))
                .andExpect(status().isUnauthorized());

        // Test DELETE /associated-contacts/{id}
        mockMvc.perform(delete("/associated-contacts/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle JSON parsing errors gracefully")
    void shouldHandleJsonParsingErrorsGracefully() throws Exception {
        // Given - Invalid JSON
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/associated-contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}