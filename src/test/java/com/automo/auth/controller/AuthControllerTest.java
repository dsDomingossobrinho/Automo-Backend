package com.automo.auth.controller;

import com.automo.auth.dto.CompleteUserResponse;
import com.automo.auth.dto.CreateCompleteUserRequest;
import com.automo.auth.dto.ForgotPasswordRequest;
import com.automo.auth.dto.LoginRequest;
import com.automo.auth.dto.LoginResponse;
import com.automo.auth.dto.RegisterRequest;
import com.automo.auth.dto.ResendOtpRequest;
import com.automo.auth.dto.ResetPasswordRequest;
import com.automo.auth.dto.VerifyOtpRequest;
import com.automo.auth.service.AuthService;
import com.automo.config.security.JwtUtils;
import com.automo.test.config.BaseTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@DisplayName("Tests for AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequest loginRequest;
    private LoginResponse loginResponse;
    private RegisterRequest registerRequest;
    private VerifyOtpRequest verifyOtpRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private ResendOtpRequest resendOtpRequest;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("test@automo.com", "password123");
        loginResponse = new LoginResponse("jwt.token", "refresh.token", "USER");
        registerRequest = new RegisterRequest("test@automo.com", "password123", "Test User");
        verifyOtpRequest = new VerifyOtpRequest("test@automo.com", "123456");
        forgotPasswordRequest = new ForgotPasswordRequest("test@automo.com");
        resetPasswordRequest = new ResetPasswordRequest("test@automo.com", "123456", "newPassword123");
        resendOtpRequest = new ResendOtpRequest("test@automo.com");
    }

    @Test
    @DisplayName("Should login successfully")
    void shouldLoginSuccessfully() throws Exception {
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt.token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh.token"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    @DisplayName("Should return 400 for invalid login request")
    void shouldReturn400ForInvalidLoginRequest() throws Exception {
        LoginRequest invalidRequest = new LoginRequest("", "");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {
        when(authService.register(any(RegisterRequest.class))).thenReturn("User registered successfully");

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered successfully"));
    }

    @Test
    @DisplayName("Should request OTP successfully")
    void shouldRequestOtpSuccessfully() throws Exception {
        doNothing().when(authService).requestOtp(anyString());

        mockMvc.perform(post("/auth/login/request-otp")
                .param("email", "test@automo.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP sent successfully"));
    }

    @Test
    @DisplayName("Should verify OTP successfully")
    void shouldVerifyOtpSuccessfully() throws Exception {
        when(authService.verifyOtpAndLogin(any(VerifyOtpRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyOtpRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt.token"));
    }

    @Test
    @DisplayName("Should request back office OTP successfully")
    void shouldRequestBackOfficeOtpSuccessfully() throws Exception {
        doNothing().when(authService).requestBackOfficeOtp(anyString());

        mockMvc.perform(post("/auth/login/backoffice/request-otp")
                .param("email", "admin@automo.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Back Office OTP sent successfully"));
    }

    @Test
    @DisplayName("Should verify back office OTP successfully")
    void shouldVerifyBackOfficeOtpSuccessfully() throws Exception {
        when(authService.verifyBackOfficeOtpAndLogin(any(VerifyOtpRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login/backoffice/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyOtpRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt.token"));
    }

    @Test
    @DisplayName("Should request user OTP successfully")
    void shouldRequestUserOtpSuccessfully() throws Exception {
        doNothing().when(authService).requestUserOtp(anyString());

        mockMvc.perform(post("/auth/login/user/request-otp")
                .param("email", "user@automo.com")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User OTP sent successfully"));
    }

    @Test
    @DisplayName("Should verify user OTP successfully")
    void shouldVerifyUserOtpSuccessfully() throws Exception {
        when(authService.verifyUserOtpAndLogin(any(VerifyOtpRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/auth/login/user/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyOtpRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt.token"));
    }

    @Test
    @DisplayName("Should resend OTP successfully")
    void shouldResendOtpSuccessfully() throws Exception {
        doNothing().when(authService).resendOtp(any(ResendOtpRequest.class));

        mockMvc.perform(post("/auth/login/resend-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendOtpRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("OTP resent successfully"));
    }

    @Test
    @DisplayName("Should resend back office OTP successfully")
    void shouldResendBackOfficeOtpSuccessfully() throws Exception {
        doNothing().when(authService).resendBackOfficeOtp(any(ResendOtpRequest.class));

        mockMvc.perform(post("/auth/login/backoffice/resend-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendOtpRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Back Office OTP resent successfully"));
    }

    @Test
    @DisplayName("Should resend user OTP successfully")
    void shouldResendUserOtpSuccessfully() throws Exception {
        doNothing().when(authService).resendUserOtp(any(ResendOtpRequest.class));

        mockMvc.perform(post("/auth/login/user/resend-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resendOtpRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User OTP resent successfully"));
    }

    @Test
    @DisplayName("Should request forgot password OTP successfully")
    void shouldRequestForgotPasswordOtpSuccessfully() throws Exception {
        doNothing().when(authService).requestForgotPasswordOtp(any(ForgotPasswordRequest.class));

        mockMvc.perform(post("/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset OTP sent successfully"));
    }

    @Test
    @DisplayName("Should reset password successfully")
    void shouldResetPasswordSuccessfully() throws Exception {
        doNothing().when(authService).resetPassword(any(ResetPasswordRequest.class));

        mockMvc.perform(post("/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get complete users successfully")
    void shouldGetCompleteUsersSuccessfully() throws Exception {
        CompleteUserResponse user1 = new CompleteUserResponse(1L, "User1", "user1@test.com", "911111111", 1L, 1L);
        CompleteUserResponse user2 = new CompleteUserResponse(2L, "User2", "user2@test.com", "922222222", 1L, 1L);
        
        List<CompleteUserResponse> users = Arrays.asList(user1, user2);
        PageImpl<CompleteUserResponse> page = new PageImpl<>(users, PageRequest.of(0, 10), 2);

        when(authService.getCompleteUsers(any())).thenReturn(page);

        mockMvc.perform(get("/auth/admin/complete-users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("User1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create complete user successfully")
    void shouldCreateCompleteUserSuccessfully() throws Exception {
        CreateCompleteUserRequest request = new CreateCompleteUserRequest(
            "newuser@test.com", "password123", "New User", "933333333", 1L, 1L
        );
        
        CompleteUserResponse response = new CompleteUserResponse(
            3L, "New User", "newuser@test.com", "933333333", 1L, 1L
        );

        when(authService.createCompleteUser(any(CreateCompleteUserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/admin/complete-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.email").value("newuser@test.com"));
    }

    @Test
    @DisplayName("Should return 401 for unauthorized access to admin endpoints")
    void shouldReturn401ForUnauthorizedAccessToAdminEndpoints() throws Exception {
        mockMvc.perform(get("/auth/admin/complete-users"))
                .andExpect(status().isUnauthorized());
    }
}