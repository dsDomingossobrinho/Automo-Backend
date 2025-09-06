package com.automo.user.controller;

import com.automo.config.security.JwtUtils;
import com.automo.user.dto.UserDto;
import com.automo.user.response.UserResponse;
import com.automo.user.service.UserService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
@DisplayName("Tests for UserController")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto userDto;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userDto = new UserDto("Test User", "912345678", 1L, 1L);
        userResponse = new UserResponse(1L, "Test User", "912345678", 1L, 1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() throws Exception {
        when(jwtUtils.getCurrentAuth()).thenReturn(null);
        when(userService.createUser(any(UserDto.class), any())).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.contact").value("912345678"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() throws Exception {
        UserResponse user1 = new UserResponse(1L, "User1", "911111111", 1L, 1L);
        UserResponse user2 = new UserResponse(2L, "User2", "922222222", 1L, 1L);
        List<UserResponse> users = Arrays.asList(user1, user2);

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("User1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("User2"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get user by id successfully")
    void shouldGetUserByIdSuccessfully() throws Exception {
        when(userService.getUserByIdResponse(1L)).thenReturn(userResponse);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.contact").value("912345678"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() throws Exception {
        UserResponse updatedUser = new UserResponse(1L, "Updated User", "933333333", 1L, 1L);
        when(userService.updateUser(eq(1L), any(UserDto.class))).thenReturn(updatedUser);

        UserDto updateDto = new UserDto("Updated User", "933333333", 1L, 1L);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated User"))
                .andExpect(jsonPath("$.contact").value("933333333"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should get users by state successfully")
    void shouldGetUsersByStateSuccessfully() throws Exception {
        List<UserResponse> users = Arrays.asList(userResponse);
        when(userService.getUsersByState(1L)).thenReturn(users);

        mockMvc.perform(get("/users/state/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Test User"));
    }

    @Test
    @DisplayName("Should return 401 for unauthorized access")
    void shouldReturn401ForUnauthorizedAccess() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden create operation")
    void shouldReturn403ForForbiddenCreateOperation() throws Exception {
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden update operation")
    void shouldReturn403ForForbiddenUpdateOperation() throws Exception {
        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto))
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Should return 403 for forbidden delete operation")
    void shouldReturn403ForForbiddenDeleteOperation() throws Exception {
        mockMvc.perform(delete("/users/1")
                .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should return 400 for invalid user data")
    void shouldReturn400ForInvalidUserData() throws Exception {
        UserDto invalidUser = new UserDto("", "", null, null);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}