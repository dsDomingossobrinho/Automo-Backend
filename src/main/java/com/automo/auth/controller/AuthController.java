package com.automo.auth.controller;

import com.automo.auth.dto.AuthResponse;
import com.automo.auth.dto.RegisterRequest;
import com.automo.auth.dto.OtpRequest;
import com.automo.auth.dto.OtpVerificationRequest;
import com.automo.auth.service.AuthService;
import com.automo.config.security.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

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

    @Operation(description = "User registration", summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @Operation(description = "Get current user info from token", summary = "Get current user information from JWT token")
    @ApiResponse(responseCode = "200", description = "User information retrieved successfully")
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getCurrentUserInfo() {
        UserInfoResponse userInfo = new UserInfoResponse(
            jwtUtils.getCurrentUserId(),
            jwtUtils.getCurrentUserEmail(),
            jwtUtils.getCurrentUserContact(),
            jwtUtils.getCurrentUsername(),
            jwtUtils.getCurrentUserRoleId(),
            jwtUtils.getCurrentUserRoleIds(),
            jwtUtils.getCurrentUserAccountTypeId(),
            jwtUtils.isCurrentUserBackOffice(),
            jwtUtils.isCurrentUserCorporate(),
            jwtUtils.isCurrentUserAdmin(),
            jwtUtils.isCurrentUserAgent(),
            jwtUtils.isCurrentUserManager()
        );
        return ResponseEntity.ok(userInfo);
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