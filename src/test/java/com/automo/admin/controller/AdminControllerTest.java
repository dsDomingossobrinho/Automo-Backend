package com.automo.admin.controller;

import com.automo.admin.dto.AdminDto;
import com.automo.admin.response.AdminResponse;
import com.automo.admin.service.AdminService;
import com.automo.config.security.JwtUtils;
import com.automo.model.dto.PaginatedResponse;
import com.automo.model.dto.PaginationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@ActiveProfiles("test")
@DisplayName("Tests for AdminController")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private AdminDto adminDto;
    private AdminResponse adminResponse;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        adminDto = new AdminDto(
            "admin@automo.com",
            "Admin User",
            "profile.jpg",
            "password123",
            "912345678",
            1L,
            1L
        );
        
        adminResponse = new AdminResponse(
            1L,
            "admin@automo.com",
            "Admin User", 
            "profile.jpg",
            1L,
            "admin_user",
            1L,
            "ACTIVE",
            now,
            now
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all admins successfully")
    void shouldGetAllAdminsSuccessfully() throws Exception {
        AdminResponse admin1 = new AdminResponse(1L, "admin1@automo.com", "Admin One", null, 1L, "admin1", 1L, "ACTIVE", now, now);
        AdminResponse admin2 = new AdminResponse(2L, "admin2@automo.com", "Admin Two", null, 2L, "admin2", 1L, "ACTIVE", now, now);
        List<AdminResponse> admins = Arrays.asList(admin1, admin2);

        when(adminService.getAllAdmins()).thenReturn(admins);

        mockMvc.perform(get("/admins"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].email").value("admin1@automo.com"))
                .andExpect(jsonPath("$[0].name").value("Admin One"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].email").value("admin2@automo.com"))
                .andExpect(jsonPath("$[1].name").value("Admin Two"));

        verify(adminService).getAllAdmins();
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny access to get all admins for non-admin users")
    void shouldDenyAccessToGetAllAdminsForNonAdminUsers() throws Exception {
        mockMvc.perform(get("/admins"))
                .andExpect(status().isForbidden());

        verify(adminService, never()).getAllAdmins();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get admin by id successfully")
    void shouldGetAdminByIdSuccessfully() throws Exception {
        when(adminService.getAdminByIdResponse(1L)).thenReturn(adminResponse);

        mockMvc.perform(get("/admins/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("admin@automo.com"))
                .andExpect(jsonPath("$.name").value("Admin User"))
                .andExpect(jsonPath("$.img").value("profile.jpg"))
                .andExpect(jsonPath("$.authId").value(1))
                .andExpect(jsonPath("$.username").value("admin_user"))
                .andExpect(jsonPath("$.stateId").value(1))
                .andExpect(jsonPath("$.state").value("ACTIVE"));

        verify(adminService).getAdminByIdResponse(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when admin not found by id")
    void shouldReturn404WhenAdminNotFoundById() throws Exception {
        when(adminService.getAdminByIdResponse(999L))
                .thenThrow(new EntityNotFoundException("Admin not found"));

        mockMvc.perform(get("/admins/999"))
                .andExpect(status().isNotFound());

        verify(adminService).getAdminByIdResponse(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create admin successfully")
    void shouldCreateAdminSuccessfully() throws Exception {
        when(adminService.createAdmin(any(AdminDto.class))).thenReturn(adminResponse);

        mockMvc.perform(post("/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("admin@automo.com"))
                .andExpect(jsonPath("$.name").value("Admin User"))
                .andExpect(jsonPath("$.img").value("profile.jpg"))
                .andExpect(jsonPath("$.authId").value(1))
                .andExpect(jsonPath("$.stateId").value(1))
                .andExpect(jsonPath("$.state").value("ACTIVE"));

        verify(adminService).createAdmin(any(AdminDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny access to create admin for non-admin users")
    void shouldDenyAccessToCreateAdminForNonAdminUsers() throws Exception {
        mockMvc.perform(post("/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDto))
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(adminService, never()).createAdmin(any(AdminDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid admin data on creation")
    void shouldReturn400ForInvalidAdminDataOnCreation() throws Exception {
        AdminDto invalidDto = new AdminDto(
            "", // Invalid email
            "", // Invalid name
            "profile.jpg",
            "", // Invalid password
            "912345678",
            null, // Invalid accountTypeId
            null  // Invalid stateId
        );

        mockMvc.perform(post("/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(adminService, never()).createAdmin(any(AdminDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update admin successfully")
    void shouldUpdateAdminSuccessfully() throws Exception {
        AdminResponse updatedResponse = new AdminResponse(
            1L, "updated@automo.com", "Updated Admin", "updated.jpg", 
            1L, "updated_user", 1L, "ACTIVE", now, now
        );
        
        AdminDto updateDto = new AdminDto(
            "updated@automo.com", "Updated Admin", "updated.jpg", 
            "newpassword", "913456789", 1L, 1L
        );

        when(adminService.updateAdmin(eq(1L), any(AdminDto.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/admins/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("updated@automo.com"))
                .andExpect(jsonPath("$.name").value("Updated Admin"))
                .andExpect(jsonPath("$.img").value("updated.jpg"));

        verify(adminService).updateAdmin(eq(1L), any(AdminDto.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny access to update admin for non-admin users")
    void shouldDenyAccessToUpdateAdminForNonAdminUsers() throws Exception {
        mockMvc.perform(put("/admins/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDto))
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(adminService, never()).updateAdmin(any(Long.class), any(AdminDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when updating non-existing admin")
    void shouldReturn404WhenUpdatingNonExistingAdmin() throws Exception {
        when(adminService.updateAdmin(eq(999L), any(AdminDto.class)))
                .thenThrow(new EntityNotFoundException("Admin not found"));

        mockMvc.perform(put("/admins/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDto))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(adminService).updateAdmin(eq(999L), any(AdminDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete admin successfully")
    void shouldDeleteAdminSuccessfully() throws Exception {
        doNothing().when(adminService).deleteAdmin(1L);

        mockMvc.perform(delete("/admins/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(adminService).deleteAdmin(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny access to delete admin for non-admin users")
    void shouldDenyAccessToDeleteAdminForNonAdminUsers() throws Exception {
        mockMvc.perform(delete("/admins/1")
                .with(csrf()))
                .andExpect(status().isForbidden());

        verify(adminService, never()).deleteAdmin(any(Long.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 404 when deleting non-existing admin")
    void shouldReturn404WhenDeletingNonExistingAdmin() throws Exception {
        doThrow(new EntityNotFoundException("Admin not found"))
                .when(adminService).deleteAdmin(999L);

        mockMvc.perform(delete("/admins/999")
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(adminService).deleteAdmin(999L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get paginated admins successfully")
    void shouldGetPaginatedAdminsSuccessfully() throws Exception {
        List<AdminResponse> admins = Arrays.asList(adminResponse);
        PaginatedResponse<AdminResponse> paginatedResponse = new PaginatedResponse<>(
            admins, 0, 10, 1L, 1, true, true
        );

        when(adminService.getEntitiesPaginated(any(PaginationRequest.class)))
                .thenReturn(paginatedResponse);

        mockMvc.perform(get("/admins/paginated")
                .param("search", "admin")
                .param("page", "0")
                .param("size", "10")
                .param("sortBy", "name")
                .param("sortDirection", "ASC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("admin@automo.com"))
                .andExpect(jsonPath("$.pageNumber").value(0))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));

        verify(adminService).getEntitiesPaginated(any(PaginationRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle empty paginated results")
    void shouldHandleEmptyPaginatedResults() throws Exception {
        PaginatedResponse<AdminResponse> emptyResponse = new PaginatedResponse<>(
            Collections.emptyList(), 0, 10, 0L, 0, true, true
        );

        when(adminService.getEntitiesPaginated(any(PaginationRequest.class)))
                .thenReturn(emptyResponse);

        mockMvc.perform(get("/admins/paginated")
                .param("search", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        verify(adminService).getEntitiesPaginated(any(PaginationRequest.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should deny access to paginated admins for non-admin users")
    void shouldDenyAccessToPaginatedAdminsForNonAdminUsers() throws Exception {
        mockMvc.perform(get("/admins/paginated"))
                .andExpect(status().isForbidden());

        verify(adminService, never()).getEntitiesPaginated(any(PaginationRequest.class));
    }

    @Test
    @DisplayName("Should require authentication for all endpoints")
    void shouldRequireAuthenticationForAllEndpoints() throws Exception {
        mockMvc.perform(get("/admins"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/admins/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDto))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/admins/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminDto))
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/admins/1")
                .with(csrf()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/admins/paginated"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle malformed JSON gracefully")
    void shouldHandleMalformedJsonGracefully() throws Exception {
        String malformedJson = "{ \"email\": \"admin@automo.com\", \"name\": }";

        mockMvc.perform(post("/admins")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson)
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(adminService, never()).createAdmin(any(AdminDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should validate pagination parameters")
    void shouldValidatePaginationParameters() throws Exception {
        PaginatedResponse<AdminResponse> response = new PaginatedResponse<>(
            Collections.emptyList(), 0, 10, 0L, 0, true, true
        );

        when(adminService.getEntitiesPaginated(any(PaginationRequest.class)))
                .thenReturn(response);

        // Test with default parameters
        mockMvc.perform(get("/admins/paginated"))
                .andExpect(status().isOk());

        // Test with custom parameters
        mockMvc.perform(get("/admins/paginated")
                .param("search", "test")
                .param("page", "1")
                .param("size", "20")
                .param("sortBy", "email")
                .param("sortDirection", "DESC"))
                .andExpect(status().isOk());

        verify(adminService, times(2)).getEntitiesPaginated(any(PaginationRequest.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should handle service exceptions gracefully")
    void shouldHandleServiceExceptionsGracefully() throws Exception {
        when(adminService.getAllAdmins())
                .thenThrow(new RuntimeException("Database connection error"));

        mockMvc.perform(get("/admins"))
                .andExpect(status().isInternalServerError());

        verify(adminService).getAllAdmins();
    }
}