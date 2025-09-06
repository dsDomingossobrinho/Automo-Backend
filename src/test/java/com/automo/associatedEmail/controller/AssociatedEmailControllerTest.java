package com.automo.associatedEmail.controller;

import com.automo.associatedEmail.dto.AssociatedEmailDto;
import com.automo.associatedEmail.response.AssociatedEmailResponse;
import com.automo.associatedEmail.service.AssociatedEmailService;
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

@WebMvcTest(AssociatedEmailController.class)
@ActiveProfiles("test")
@DisplayName("Tests for AssociatedEmailController")
class AssociatedEmailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AssociatedEmailService associatedEmailService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private AssociatedEmailDto emailDto;
    private AssociatedEmailResponse emailResponse;

    @BeforeEach
    void setUp() {
        emailDto = TestDataFactory.createValidAssociatedEmailDto(1L, "test@automo.com", 1L);
        emailResponse = new AssociatedEmailResponse(
            1L, 
            1L, 
            "Test User", 
            "test@automo.com", 
            1L, 
            "ACTIVE", 
            LocalDateTime.now(), 
            LocalDateTime.now()
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all associated emails successfully")
    void shouldGetAllAssociatedEmailsSuccessfully() throws Exception {
        // Given
        AssociatedEmailResponse email1 = new AssociatedEmailResponse(1L, 1L, "User1", "email1@automo.com", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        AssociatedEmailResponse email2 = new AssociatedEmailResponse(2L, 1L, "User2", "email2@automo.com", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        List<AssociatedEmailResponse> emails = Arrays.asList(email1, email2);

        when(associatedEmailService.getAllAssociatedEmails()).thenReturn(emails);

        // When & Then
        mockMvc.perform(get("/associated-emails")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("email1@automo.com"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("email2@automo.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get associated email by id successfully")
    void shouldGetAssociatedEmailByIdSuccessfully() throws Exception {
        // Given
        Long emailId = 1L;
        when(associatedEmailService.getAssociatedEmailByIdResponse(emailId)).thenReturn(emailResponse);

        // When & Then
        mockMvc.perform(get("/associated-emails/{id}", emailId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@automo.com"))
                .andExpect(jsonPath("$.userName").value("Test User"))
                .andExpect(jsonPath("$.stateId").value(1))
                .andExpect(jsonPath("$.stateName").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get associated email by email address successfully")
    void shouldGetAssociatedEmailByEmailAddressSuccessfully() throws Exception {
        // Given
        String emailAddress = "test@automo.com";
        when(associatedEmailService.getAssociatedEmailByEmail(emailAddress)).thenReturn(emailResponse);

        // When & Then
        mockMvc.perform(get("/associated-emails/email/{email}", emailAddress)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@automo.com"))
                .andExpect(jsonPath("$.userName").value("Test User"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create associated email successfully")
    void shouldCreateAssociatedEmailSuccessfully() throws Exception {
        // Given
        when(associatedEmailService.createAssociatedEmail(any(AssociatedEmailDto.class))).thenReturn(emailResponse);

        // When & Then
        mockMvc.perform(post("/associated-emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@automo.com"))
                .andExpect(jsonPath("$.identifierId").value(1))
                .andExpect(jsonPath("$.userName").value("Test User"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update associated email successfully")
    void shouldUpdateAssociatedEmailSuccessfully() throws Exception {
        // Given
        Long emailId = 1L;
        AssociatedEmailDto updateDto = TestDataFactory.createValidAssociatedEmailDto(1L, "updated@automo.com", 1L);
        AssociatedEmailResponse updatedResponse = new AssociatedEmailResponse(
            1L, 1L, "Test User", "updated@automo.com", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
        );

        when(associatedEmailService.updateAssociatedEmail(eq(emailId), any(AssociatedEmailDto.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/associated-emails/{id}", emailId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("updated@automo.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete associated email successfully")
    void shouldDeleteAssociatedEmailSuccessfully() throws Exception {
        // Given
        Long emailId = 1L;
        doNothing().when(associatedEmailService).deleteAssociatedEmail(emailId);

        // When & Then
        mockMvc.perform(delete("/associated-emails/{id}", emailId)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get associated emails by identifier successfully")
    void shouldGetAssociatedEmailsByIdentifierSuccessfully() throws Exception {
        // Given
        Long identifierId = 1L;
        List<AssociatedEmailResponse> emails = Arrays.asList(emailResponse);

        when(associatedEmailService.getAssociatedEmailsByIdentifier(identifierId)).thenReturn(emails);

        // When & Then
        mockMvc.perform(get("/associated-emails/identifier/{identifierId}", identifierId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].identifierId").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get associated emails by state successfully")
    void shouldGetAssociatedEmailsByStateSuccessfully() throws Exception {
        // Given
        Long stateId = 1L;
        List<AssociatedEmailResponse> emails = Arrays.asList(emailResponse);

        when(associatedEmailService.getAssociatedEmailsByState(stateId)).thenReturn(emails);

        // When & Then
        mockMvc.perform(get("/associated-emails/state/{stateId}", stateId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].stateId").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return bad request for invalid email data")
    void shouldReturnBadRequestForInvalidEmailData() throws Exception {
        // Given - Invalid DTO with null values
        AssociatedEmailDto invalidDto = TestDataFactory.createValidAssociatedEmailDto(null, null, null);

        // When & Then
        mockMvc.perform(post("/associated-emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return bad request for invalid email format")
    void shouldReturnBadRequestForInvalidEmailFormat() throws Exception {
        // Given - DTO with invalid email format
        AssociatedEmailDto invalidDto = TestDataFactory.createValidAssociatedEmailDto(1L, "invalid-email", 1L);

        // When & Then
        mockMvc.perform(post("/associated-emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return bad request for blank email")
    void shouldReturnBadRequestForBlankEmail() throws Exception {
        // Given - DTO with blank email
        AssociatedEmailDto invalidDto = TestDataFactory.createValidAssociatedEmailDto(1L, "", 1L);

        // When & Then
        mockMvc.perform(post("/associated-emails")
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
        Long emailId = 1L;
        AssociatedEmailDto invalidDto = TestDataFactory.createValidAssociatedEmailDto(null, null, null);

        // When & Then
        mockMvc.perform(put("/associated-emails/{id}", emailId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle empty list when no emails found")
    void shouldHandleEmptyListWhenNoEmailsFound() throws Exception {
        // Given
        when(associatedEmailService.getAllAssociatedEmails()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/associated-emails")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle empty list when no emails found by identifier")
    void shouldHandleEmptyListWhenNoEmailsFoundByIdentifier() throws Exception {
        // Given
        Long identifierId = 999L;
        when(associatedEmailService.getAssociatedEmailsByIdentifier(identifierId)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/associated-emails/identifier/{identifierId}", identifierId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle empty list when no emails found by state")
    void shouldHandleEmptyListWhenNoEmailsFoundByState() throws Exception {
        // Given
        Long stateId = 999L;
        when(associatedEmailService.getAssociatedEmailsByState(stateId)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/associated-emails/state/{stateId}", stateId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle various valid email formats in creation")
    void shouldHandleVariousValidEmailFormatsInCreation() throws Exception {
        // Given
        String[] emailFormats = {
            "user@domain.com",
            "test@automo.com",
            "user.name@domain.co.uk",
            "user+tag@domain.com",
            "user_name@domain-name.com"
        };

        for (String email : emailFormats) {
            AssociatedEmailDto dto = TestDataFactory.createValidAssociatedEmailDto(1L, email, 1L);
            AssociatedEmailResponse response = new AssociatedEmailResponse(
                1L, 1L, "Test User", email, 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
            );
            
            when(associatedEmailService.createAssociatedEmail(any(AssociatedEmailDto.class))).thenReturn(response);

            // When & Then
            mockMvc.perform(post("/associated-emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto))
                    .with(csrf()))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.email").value(email));
        }
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle multiple emails for same identifier")
    void shouldHandleMultipleEmailsForSameIdentifier() throws Exception {
        // Given
        Long identifierId = 1L;
        AssociatedEmailResponse email1 = new AssociatedEmailResponse(1L, identifierId, "User", "email1@automo.com", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        AssociatedEmailResponse email2 = new AssociatedEmailResponse(2L, identifierId, "User", "email2@automo.com", 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now());
        List<AssociatedEmailResponse> emails = Arrays.asList(email1, email2);

        when(associatedEmailService.getAssociatedEmailsByIdentifier(identifierId)).thenReturn(emails);

        // When & Then
        mockMvc.perform(get("/associated-emails/identifier/{identifierId}", identifierId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].identifierId").value(identifierId))
                .andExpect(jsonPath("$[1].identifierId").value(identifierId))
                .andExpect(jsonPath("$[0].email").value("email1@automo.com"))
                .andExpect(jsonPath("$[1].email").value("email2@automo.com"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should handle international email domains")
    void shouldHandleInternationalEmailDomains() throws Exception {
        // Given
        String[] internationalEmails = {
            "user@domain.pt",
            "user@domain.es", 
            "user@domain.br",
            "user@domain.co.uk"
        };

        for (String email : internationalEmails) {
            AssociatedEmailResponse response = new AssociatedEmailResponse(
                1L, 1L, "Test User", email, 1L, "ACTIVE", LocalDateTime.now(), LocalDateTime.now()
            );
            
            when(associatedEmailService.getAssociatedEmailByEmail(email)).thenReturn(response);

            // When & Then
            mockMvc.perform(get("/associated-emails/email/{email}", email)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(email));
        }
    }

    @Test
    @DisplayName("Should require authentication for all endpoints")
    void shouldRequireAuthenticationForAllEndpoints() throws Exception {
        // Test GET /associated-emails
        mockMvc.perform(get("/associated-emails"))
                .andExpect(status().isUnauthorized());

        // Test GET /associated-emails/{id}
        mockMvc.perform(get("/associated-emails/1"))
                .andExpect(status().isUnauthorized());

        // Test GET /associated-emails/email/{email}
        mockMvc.perform(get("/associated-emails/email/test@automo.com"))
                .andExpect(status().isUnauthorized());

        // Test POST /associated-emails
        mockMvc.perform(post("/associated-emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isUnauthorized());

        // Test PUT /associated-emails/{id}
        mockMvc.perform(put("/associated-emails/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isUnauthorized());

        // Test DELETE /associated-emails/{id}
        mockMvc.perform(delete("/associated-emails/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle JSON parsing errors gracefully")
    void shouldHandleJsonParsingErrorsGracefully() throws Exception {
        // Given - Invalid JSON
        String invalidJson = "{ invalid json }";

        // When & Then
        mockMvc.perform(post("/associated-emails")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should validate email format constraints")
    void shouldValidateEmailFormatConstraints() throws Exception {
        // Given - Invalid email formats
        String[] invalidEmails = {
            "invalid-email",
            "@domain.com",
            "user@",
            "user@domain",
            "user..name@domain.com"
        };

        for (String invalidEmail : invalidEmails) {
            AssociatedEmailDto invalidDto = TestDataFactory.createValidAssociatedEmailDto(1L, invalidEmail, 1L);

            // When & Then
            mockMvc.perform(post("/associated-emails")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidDto))
                    .with(csrf()))
                    .andExpect(status().isBadRequest());
        }
    }
}