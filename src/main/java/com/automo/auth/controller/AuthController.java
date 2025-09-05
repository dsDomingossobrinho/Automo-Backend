package com.automo.auth.controller;

import com.automo.auth.dto.AuthResponse;
import com.automo.auth.dto.RegisterRequest;
import com.automo.auth.dto.OtpRequest;
import com.automo.auth.dto.OtpVerificationRequest;
import com.automo.auth.dto.LoginRequest;
import com.automo.auth.dto.LoginResponse;
import com.automo.auth.dto.ResendOtpRequest;
import com.automo.auth.dto.ForgotPasswordRequest;
import com.automo.auth.dto.ResetPasswordRequest;
import com.automo.auth.dto.CreateCompleteUserRequest;
import com.automo.auth.dto.CompleteUserResponse;
import com.automo.auth.dto.PagedUserRequest;
import com.automo.auth.dto.PagedUserResponse;
import com.automo.auth.dto.UserPermissionsRequest;
import com.automo.auth.dto.ChangeAccountTypeRequest;
import com.automo.auth.dto.ManageRoleRequest;
import com.automo.auth.dto.UserManagementResponse;
import com.automo.auth.dto.UserStatisticsResponse;
import com.automo.auth.dto.AgentStatisticsResponse;
import com.automo.auth.service.AuthService;
import com.automo.config.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @Operation(description = "Direct login without OTP", summary = "Authenticate user directly with credentials")
    @ApiResponse(responseCode = "200", description = "User authenticated successfully")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @Operation(description = "Request OTP for general authentication", summary = "Request OTP code for user authentication")
    @ApiResponse(responseCode = "200", description = "OTP sent successfully")
    @PostMapping("/login/request-otp")
    public ResponseEntity<AuthResponse> requestOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.requestOtp(request));
    }

    @Operation(description = "Verify OTP and authenticate user", summary = "Verify OTP code and complete authentication")
    @ApiResponse(responseCode = "200", description = "User authenticated successfully")
    @PostMapping("/login/verify-otp")
    public ResponseEntity<AuthResponse> verifyOtpAndAuthenticate(@Valid @RequestBody OtpVerificationRequest request) {
        return ResponseEntity.ok(authService.verifyOtpAndAuthenticate(request));
    }

    @Operation(description = "Resend OTP for general authentication", summary = "Resend OTP code for user authentication")
    @ApiResponse(responseCode = "200", description = "New OTP sent successfully")
    @PostMapping("/login/resend-otp")
    public ResponseEntity<AuthResponse> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        return ResponseEntity.ok(authService.resendOtp(request));
    }

    @Operation(description = "Request OTP for back office authentication", summary = "Request OTP code for back office user")
    @ApiResponse(responseCode = "200", description = "OTP sent successfully")
    @PostMapping("/login/backoffice/request-otp")
    public ResponseEntity<AuthResponse> requestBackOfficeOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.authenticateBackOffice(request));
    }

    @Operation(description = "Verify OTP and authenticate back office user", summary = "Verify OTP code and complete back office authentication")
    @ApiResponse(responseCode = "200", description = "Back office user authenticated successfully")
    @PostMapping("/login/backoffice/verify-otp")
    public ResponseEntity<AuthResponse> verifyBackOfficeOtpAndAuthenticate(@Valid @RequestBody OtpVerificationRequest request) {
        return ResponseEntity.ok(authService.verifyOtpAndAuthenticateBackOffice(request));
    }

    @Operation(description = "Resend OTP for back office authentication", summary = "Resend OTP code for back office user")
    @ApiResponse(responseCode = "200", description = "New OTP sent successfully")
    @PostMapping("/login/backoffice/resend-otp")
    public ResponseEntity<AuthResponse> resendBackOfficeOtp(@Valid @RequestBody ResendOtpRequest request) {
        return ResponseEntity.ok(authService.resendBackOfficeOtp(request));
    }

    @Operation(description = "Request OTP for regular user authentication", summary = "Request OTP code for regular user")
    @ApiResponse(responseCode = "200", description = "OTP sent successfully")
    @PostMapping("/login/user/request-otp")
    public ResponseEntity<AuthResponse> requestUserOtp(@Valid @RequestBody OtpRequest request) {
        return ResponseEntity.ok(authService.authenticateUser(request));
    }

    @Operation(description = "Verify OTP and authenticate regular user", summary = "Verify OTP code and complete regular user authentication")
    @ApiResponse(responseCode = "200", description = "Regular user authenticated successfully")
    @PostMapping("/login/user/verify-otp")
    public ResponseEntity<AuthResponse> verifyUserOtpAndAuthenticate(@Valid @RequestBody OtpVerificationRequest request) {
        return ResponseEntity.ok(authService.verifyOtpAndAuthenticateUser(request));
    }

    @Operation(description = "Resend OTP for regular user authentication", summary = "Resend OTP code for regular user")
    @ApiResponse(responseCode = "200", description = "New OTP sent successfully")
    @PostMapping("/login/user/resend-otp")
    public ResponseEntity<AuthResponse> resendUserOtp(@Valid @RequestBody ResendOtpRequest request) {
        return ResponseEntity.ok(authService.resendUserOtp(request));
    }

    @Operation(description = "User registration", summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(description = "Simple test endpoint without Swagger annotations", summary = "Simple test")
    @PostMapping("/simple-test")
    public ResponseEntity<String> simpleTest() {
        return ResponseEntity.ok("This is a simple test endpoint without Swagger annotations.");
    }

    @Operation(description = "Request OTP for password reset", summary = "Request password recovery code")
    @ApiResponse(responseCode = "200", description = "Password reset OTP sent successfully")
    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> requestPasswordReset(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.requestPasswordReset(request));
    }

    @Operation(description = "Verify OTP and reset password", summary = "Reset password with OTP verification")
    @ApiResponse(responseCode = "200", description = "Password reset successfully")
    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request));
    }

    @Operation(description = "List all admins in the system", summary = "Get all admin users",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Admins retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @GetMapping("/admins")
    public ResponseEntity<List<CompleteUserResponse>> getAllAdmins() {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem listar admins.");
        }
        return ResponseEntity.ok(authService.getAllAdmins());
    }

    @Operation(description = "List all users in the system", summary = "Get all users",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @GetMapping("/users")
    public ResponseEntity<List<CompleteUserResponse>> getAllUsers() {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem listar usuários.");
        }
        return ResponseEntity.ok(authService.getAllUsers());
    }

    @Operation(description = "List all admins with pagination and search", summary = "Get paginated admin users",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Paginated admins retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @GetMapping("/admins/paged")
    public ResponseEntity<PagedUserResponse<CompleteUserResponse>> getAllAdminsPaged(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem listar admins.");
        }
        
        PagedUserRequest request = new PagedUserRequest(page, size, search, sortBy, sortDirection);
        return ResponseEntity.ok(authService.getAllAdminsPaged(request));
    }

    @Operation(description = "List all users with pagination and search", summary = "Get paginated users",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Paginated users retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @GetMapping("/users/paged")
    public ResponseEntity<PagedUserResponse<CompleteUserResponse>> getAllUsersPaged(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem listar usuários.");
        }
        
        PagedUserRequest request = new PagedUserRequest(page, size, search, sortBy, sortDirection);
        return ResponseEntity.ok(authService.getAllUsersPaged(request));
    }

    @Operation(description = "Create a complete user with full configuration", 
               summary = "Create user with roles and account type",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "201", description = "User created successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @PostMapping("/users")
    public ResponseEntity<CompleteUserResponse> createCompleteUser(@Valid @RequestBody CreateCompleteUserRequest request) {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem criar usuários.");
        }
        CompleteUserResponse response = authService.createCompleteUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    // === ENDPOINTS DE GESTÃO DE USUÁRIOS AVANÇADA ===
    
    @Operation(description = "Get complete user details by ID", summary = "Get user details",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @GetMapping("/users/{userId}")
    public ResponseEntity<CompleteUserResponse> getUserDetails(@PathVariable Long userId) {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem ver detalhes de usuários.");
        }
        return ResponseEntity.ok(authService.getUserDetails(userId));
    }
    
    @Operation(description = "Assign permissions (roles) to a user - replaces all existing roles", 
               summary = "Assign user permissions",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Permissions assigned successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @PutMapping("/users/permissions")
    public ResponseEntity<UserManagementResponse> assignPermissions(@Valid @RequestBody UserPermissionsRequest request) {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem atribuir permissões.");
        }
        return ResponseEntity.ok(authService.assignPermissions(request));
    }
    
    @Operation(description = "Change user account type", summary = "Change account type",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Account type changed successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @PutMapping("/users/account-type")
    public ResponseEntity<UserManagementResponse> changeAccountType(@Valid @RequestBody ChangeAccountTypeRequest request) {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem alterar tipo de conta.");
        }
        return ResponseEntity.ok(authService.changeAccountType(request));
    }
    
    @Operation(description = "Add a role to a user", summary = "Add user role",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Role added successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @PostMapping("/users/roles")
    public ResponseEntity<UserManagementResponse> addRole(@Valid @RequestBody ManageRoleRequest request) {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem adicionar roles.");
        }
        return ResponseEntity.ok(authService.addRole(request));
    }
    
    @Operation(description = "Remove a role from a user", summary = "Remove user role",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Role removed successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @DeleteMapping("/users/roles")
    public ResponseEntity<UserManagementResponse> removeRole(@Valid @RequestBody ManageRoleRequest request) {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem remover roles.");
        }
        return ResponseEntity.ok(authService.removeRole(request));
    }
    
    @Operation(description = "Activate a user account", summary = "Activate user",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User activated successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @PutMapping("/users/{userId}/activate")
    public ResponseEntity<UserManagementResponse> activateUser(@PathVariable Long userId) {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem ativar usuários.");
        }
        return ResponseEntity.ok(authService.activateUser(userId));
    }
    
    @Operation(description = "Deactivate a user account", summary = "Deactivate user",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "User deactivated successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @PutMapping("/users/{userId}/deactivate")
    public ResponseEntity<UserManagementResponse> deactivateUser(@PathVariable Long userId) {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem desativar usuários.");
        }
        return ResponseEntity.ok(authService.deactivateUser(userId));
    }
    
    // === ENDPOINT DE ESTATÍSTICAS ===
    
    @Operation(description = "Get system statistics including user counts and message metrics", 
               summary = "Get system statistics",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin privileges required")
    @GetMapping("/statistics")
    public ResponseEntity<UserStatisticsResponse> getSystemStatistics() {
        if (!jwtUtils.isCurrentUserAdmin()) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem ver estatísticas.");
        }
        return ResponseEntity.ok(authService.getSystemStatistics());
    }
    
    @Operation(description = "Get agent-specific statistics including lead capture, deal closure, and message metrics", 
               summary = "Get agent statistics",
               security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponse(responseCode = "200", description = "Agent statistics retrieved successfully")
    @ApiResponse(responseCode = "403", description = "Access forbidden - Admin or Agent privileges required")
    @ApiResponse(responseCode = "404", description = "Agent not found")
    @GetMapping("/agent/{agentId}/statistics")
    public ResponseEntity<AgentStatisticsResponse> getAgentStatistics(@PathVariable Long agentId) {
        // Verifica se o usuário atual é admin ou se está acessando suas próprias estatísticas
        if (!jwtUtils.isCurrentUserAdmin() && !jwtUtils.getCurrentUserId().equals(agentId)) {
            throw new RuntimeException("Acesso negado. Apenas administradores podem ver estatísticas de outros agentes.");
        }
        return ResponseEntity.ok(authService.getAgentStatistics(agentId));
    }

    // Classe interna para resposta de informações do usuário
    public record UserInfoResponse(
        Long id,
        String email,
        String contact,
        String username,
        Long primaryRoleId,
        java.util.List<Long> allRoleIds,
        Long accountTypeId,
        boolean isBackOffice,
        boolean isCorporate,
        boolean isAdmin,
        boolean isAgent,
        boolean isManager
    ) {}
} 