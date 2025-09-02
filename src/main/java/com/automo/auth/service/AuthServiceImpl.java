package com.automo.auth.service;

import com.automo.auth.dto.AuthResponse;
import com.automo.auth.dto.RegisterRequest;
import com.automo.auth.dto.OtpRequest;
import com.automo.auth.dto.OtpVerificationRequest;
import com.automo.auth.dto.LoginRequest;
import com.automo.auth.dto.LoginResponse;
import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.auth.util.ContactValidator;
import com.automo.authRoles.service.AuthRolesService;
import com.automo.config.security.JwtService;
import com.automo.exception.UserAlreadyExistsException;
import com.automo.exception.UserNotFoundException;
import com.automo.exception.InvalidCredentialsException;
import com.automo.role.entity.Role;
import com.automo.role.service.RoleService;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final RoleService roleService;
    private final StateService stateService;
    private final AuthRolesService authRolesService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;

    public AuthServiceImpl(AuthRepository authRepository,
                           RoleService roleService,
                           StateService stateService,
                           @Lazy AuthRolesService authRolesService,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           OtpService otpService) {
        this.authRepository = authRepository;
        this.roleService = roleService;
        this.stateService = stateService;
        this.authRolesService = authRolesService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.otpService = otpService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (authRepository.existsByEmail(request.email())) {
            throw UserAlreadyExistsException.withEmail(request.email());
        }
        if (authRepository.existsByContact(request.contact())) {
            throw UserAlreadyExistsException.withContact(request.contact());
        }

        // Get default role and state
        Role role = roleService.getRoleByRole("USER");
        State state = stateService.getStateByState("ACTIVE");

        // Create new auth user
        Auth auth = new Auth();
        auth.setEmail(request.email());
        auth.setContact(request.contact());
        auth.setUsername(request.username());
        auth.setPassword(passwordEncoder.encode(request.password()));
        auth.setState(state);

        authRepository.save(auth);
        
        // Criar associação AuthRoles com a role padrão
        authRolesService.createAuthRolesWithEntities(auth, role, state);

        // Generate token
        String token = jwtService.generateTokenForAuth(auth);
        return new AuthResponse(token, "User registered successfully", false);
    }

    @Override
    public AuthResponse requestOtp(OtpRequest request) {
        // Buscar usuário por email ou contato
        Auth auth = authRepository.findByEmailOrContact(request.emailOrContact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.emailOrContact()));

        // Autenticar com o email encontrado
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(auth.getEmail(), request.password())
        );

        // Gerar e enviar OTP (detecta automaticamente se é email ou telefone)
        String contactToSend = ContactValidator.isEmail(request.emailOrContact()) ? auth.getEmail() : auth.getContact();
        otpService.generateAndSendOtp(contactToSend, "LOGIN");

        String message = ContactValidator.isEmail(request.emailOrContact()) ? 
            "OTP sent to your email. Please check and enter the code." :
            "OTP sent to your phone. Please check and enter the code.";
            
        return new AuthResponse(null, message, true);
    }

    @Override
    public AuthResponse verifyOtpAndAuthenticate(OtpVerificationRequest request) {
        // Verificar OTP
        if (!otpService.verifyOtp(request.contact(), request.otpCode(), "LOGIN")) {
            throw InvalidCredentialsException.expiredToken();
        }

        // Buscar usuário (por email ou contato)
        Auth auth = authRepository.findByEmailOrContact(request.contact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.contact()));

        // Gerar token
        String token = jwtService.generateTokenForAuth(auth);
        return new AuthResponse(token, "Authentication successful", false);
    }

    @Override
    public AuthResponse authenticateBackOffice(OtpRequest request) {
        // Buscar usuário por email ou contato
        Auth auth = authRepository.findByEmailOrContact(request.emailOrContact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.emailOrContact()));

        // Autenticar com o email encontrado
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(auth.getEmail(), request.password())
        );

        // Verificar se o usuário tem tipo_conta_id = 1 (INDIVIDUAL - Back Office)
        if (auth.getAccountType() == null || !auth.getAccountType().getId().equals(1L)) {
            throw new IllegalArgumentException("Access denied. Only back office users can access this endpoint.");
        }

        // Gerar e enviar OTP (detecta automaticamente se é email ou telefone)
        String contactToSend = ContactValidator.isEmail(request.emailOrContact()) ? auth.getEmail() : auth.getContact();
        otpService.generateAndSendOtp(contactToSend, "LOGIN_BACKOFFICE");

        String message = ContactValidator.isEmail(request.emailOrContact()) ? 
            "OTP sent to your email. Please check and enter the code." :
            "OTP sent to your phone. Please check and enter the code.";
            
        return new AuthResponse(null, message, true);
    }

    @Override
    public AuthResponse verifyOtpAndAuthenticateBackOffice(OtpVerificationRequest request) {
        // Verificar OTP
        if (!otpService.verifyOtp(request.contact(), request.otpCode(), "LOGIN_BACKOFFICE")) {
            throw InvalidCredentialsException.expiredToken();
        }

        // Buscar usuário (por email ou contato)
        Auth auth = authRepository.findByEmailOrContact(request.contact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.contact()));

        // Verificar se o usuário tem tipo_conta_id = 1 (INDIVIDUAL - Back Office)
        if (auth.getAccountType() == null || !auth.getAccountType().getId().equals(1L)) {
            throw new IllegalArgumentException("Access denied. Only back office users can access this endpoint.");
        }

        // Gerar token
        String token = jwtService.generateTokenForAuth(auth);
        return new AuthResponse(token, "Back office authentication successful", false);
    }

    @Override
    public AuthResponse authenticateUser(OtpRequest request) {
        // Buscar usuário por email ou contato
        Auth auth = authRepository.findByEmailOrContact(request.emailOrContact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.emailOrContact()));

        // Autenticar com o email encontrado
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(auth.getEmail(), request.password())
        );

        // Verificar se o usuário tem tipo_conta_id = 2 (CORPORATE - Usuários)
        if (auth.getAccountType() == null || !auth.getAccountType().getId().equals(2L)) {
            throw new IllegalArgumentException("Access denied. Only regular users can access this endpoint.");
        }

        // Gerar e enviar OTP (detecta automaticamente se é email ou telefone)
        String contactToSend = ContactValidator.isEmail(request.emailOrContact()) ? auth.getEmail() : auth.getContact();
        otpService.generateAndSendOtp(contactToSend, "LOGIN_USER");

        String message = ContactValidator.isEmail(request.emailOrContact()) ? 
            "OTP sent to your email. Please check and enter the code." :
            "OTP sent to your phone. Please check and enter the code.";
            
        return new AuthResponse(null, message, true);
    }

    @Override
    public AuthResponse verifyOtpAndAuthenticateUser(OtpVerificationRequest request) {
        // Verificar OTP
        if (!otpService.verifyOtp(request.contact(), request.otpCode(), "LOGIN_USER")) {
            throw InvalidCredentialsException.expiredToken();
        }

        // Buscar usuário (por email ou contato)
        Auth auth = authRepository.findByEmailOrContact(request.contact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.contact()));

        // Verificar se o usuário tem tipo_conta_id = 2 (CORPORATE - Usuários)
        if (auth.getAccountType() == null || !auth.getAccountType().getId().equals(2L)) {
            throw new IllegalArgumentException("Access denied. Only regular users can access this endpoint.");
        }

        // Gerar token
        String token = jwtService.generateTokenForAuth(auth);
        return new AuthResponse(token, "User authentication successful", false);
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        // Buscar usuário por email, username ou contato
        Auth auth = authRepository.findByEmailOrUsernameOrContact(request.emailOrContact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.emailOrContact()));

        // Verificar senha
        if (!passwordEncoder.matches(request.password(), auth.getPassword())) {
            throw InvalidCredentialsException.create();
        }

        // Gerar token
        String accessToken = jwtService.generateTokenForAuth(auth);
        
        return new LoginResponse(accessToken);
    }

    @Override
    public Optional<Auth> findByEmailOrUsernameOrContact(String emailOrContact) {
        return authRepository.findByEmailOrUsernameOrContact(emailOrContact);
    }
    
    @Override
    public Auth findById(Long id) {
        return authRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Auth with ID " + id + " not found"));
    }
    
    @Override
    public Auth findByIdAndStateId(Long id, Long stateId) {
        if (stateId == null) {
            stateId = 1L; // Estado padrão (ativo)
        }
        
        Auth entity = authRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Auth with ID " + id + " not found"));
        
        // For entities with state relationship, check if entity's state matches required state
        if (entity.getState() != null && !entity.getState().getId().equals(stateId)) {
            throw new jakarta.persistence.EntityNotFoundException("Auth with ID " + id + " and state ID " + stateId + " not found");
        }
        
        return entity;
    }
} 