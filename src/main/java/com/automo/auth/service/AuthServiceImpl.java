package com.automo.auth.service;

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
import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.auth.util.ContactValidator;
import com.automo.config.security.JwtService;
import com.automo.exception.UserAlreadyExistsException;
import com.automo.exception.UserNotFoundException;
import com.automo.exception.InvalidCredentialsException;
import com.automo.state.entity.State;
import com.automo.state.service.StateService;
import com.automo.admin.repository.AdminRepository;
import com.automo.lead.repository.LeadRepository;
import com.automo.deal.repository.DealRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import com.automo.admin.entity.Admin;
import com.automo.role.service.RoleService;
import com.automo.accountType.service.AccountTypeService;
import com.automo.authRoles.service.AuthRolesService;
import com.automo.authRoles.entity.AuthRoles;
import com.automo.role.entity.Role;
import com.automo.accountType.entity.AccountType;
import com.automo.messageCount.repository.MessageCountRepository;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final StateService stateService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final AdminRepository adminRepository;
    private final RoleService roleService;
    private final AccountTypeService accountTypeService;
    private final AuthRolesService authRolesService;
    private final MessageCountRepository messageCountRepository;
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;

    public AuthServiceImpl(AuthRepository authRepository,
                           StateService stateService,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           OtpService otpService,
                           AdminRepository adminRepository,
                           RoleService roleService,
                           AccountTypeService accountTypeService,
                           @Lazy AuthRolesService authRolesService,
                           MessageCountRepository messageCountRepository,
                           LeadRepository leadRepository,
                           DealRepository dealRepository) {
        this.authRepository = authRepository;
        this.stateService = stateService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.otpService = otpService;
        this.adminRepository = adminRepository;
        this.roleService = roleService;
        this.accountTypeService = accountTypeService;
        this.authRolesService = authRolesService;
        this.messageCountRepository = messageCountRepository;
        this.leadRepository = leadRepository;
        this.dealRepository = dealRepository;
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

        State state = stateService.getStateByState("ACTIVE");

        // Create new auth user
        Auth auth = new Auth();
        auth.setEmail(request.email());
        auth.setContact(request.contact());
        auth.setUsername(request.username());
        auth.setPassword(passwordEncoder.encode(request.password()));
        auth.setState(state);

        authRepository.save(auth);

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
        return new AuthResponse(token, "OTP validado com sucesso! Login realizado.", false);
    }

    @Override
    public AuthResponse authenticateBackOffice(OtpRequest request) {
        // Buscar usuário por email ou contato com AccountType eagerly loaded
        Auth auth = authRepository.findByEmailOrContactWithAccountType(request.emailOrContact())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        // Verificar se Auth está ATIVO (state = 1)
        if (auth.getState() == null || !auth.getState().getId().equals(1L)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // Autenticar credenciais
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(auth.getEmail(), request.password())
            );
        } catch (Exception e) {
            log.warn("Authentication failed for back office login: {}", request.emailOrContact());
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // VALIDAÇÃO ESPECÍFICA: Verificar se account type é "admin" 
        if (auth.getAccountType() == null || 
            !auth.getAccountType().getType().equalsIgnoreCase("admin")) {
            log.warn("Access denied - not admin account type: {}", request.emailOrContact());
            throw new InvalidCredentialsException("Access denied. Only admin users can access back office");
        }

        // VALIDAÇÃO OBRIGATÓRIA: Verificar se existe admin ativo na tabela admins
        if (!isActiveAdminByAuthId(auth.getId())) {
            log.warn("Access denied - admin account not found or inactive: {}", request.emailOrContact());
            throw new InvalidCredentialsException("Access denied. Admin account not found or inactive");
        }

        // Gerar e enviar OTP para o email registrado
        otpService.generateAndSendOtp(auth.getEmail(), "LOGIN_BACKOFFICE");
        log.info("OTP sent for back office login: {}", auth.getEmail());
            
        return new AuthResponse(null, "OTP sent to your registered email. Please check and enter the code.", true);
    }

    @Override
    public AuthResponse verifyOtpAndAuthenticateBackOffice(OtpVerificationRequest request) {
        // Verificar OTP
        if (!otpService.verifyOtp(request.contact(), request.otpCode(), "LOGIN_BACKOFFICE")) {
            throw InvalidCredentialsException.expiredToken();
        }

        // Buscar usuário (por email) com AccountType eagerly loaded
        Auth auth = authRepository.findByEmailOrContactWithAccountType(request.contact())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        // Verificar se Auth está ATIVO (state = 1)
        if (auth.getState() == null || !auth.getState().getId().equals(1L)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // VALIDAÇÃO ESPECÍFICA: Verificar se account type é "admin"
        if (auth.getAccountType() == null || 
            !auth.getAccountType().getType().equalsIgnoreCase("admin")) {
            log.warn("Access denied - not admin account type during OTP verification: {}", request.contact());
            throw new InvalidCredentialsException("Access denied. Only admin users can access back office");
        }

        // VALIDAÇÃO OBRIGATÓRIA: Verificar se existe admin ativo na tabela admins
        if (!isActiveAdminByAuthId(auth.getId())) {
            log.warn("Access denied - admin account not found or inactive during OTP verification: {}", request.contact());
            throw new InvalidCredentialsException("Access denied. Admin account not found or inactive");
        }

        // Gerar token JWT
        String token = jwtService.generateTokenForAuth(auth);
        log.info("Back office authentication successful for: {}", auth.getEmail());
        
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
        return new AuthResponse(token, "OTP validado com sucesso! Login de usuário realizado.", false);
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
    
    @Override
    public String generateUniqueUsername(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        // Limpar e normalizar o nome
        String cleanName = name.toLowerCase()
                .replaceAll("[^a-zA-Z0-9\\s]", "") // Remove caracteres especiais
                .replaceAll("\\s+", ".") // Substitui espaços por pontos
                .trim();
        
        // Se o nome ficou vazio após limpeza, usar um padrão
        if (cleanName.isEmpty()) {
            cleanName = "user";
        }
        
        // Verificar se username já existe
        String baseUsername = cleanName;
        String finalUsername = baseUsername;
        int counter = 1;
        
        // Procurar até encontrar um username único
        while (authRepository.existsByUsername(finalUsername)) {
            finalUsername = baseUsername + "." + counter;
            counter++;
        }
        
        return finalUsername;
    }
    
    @Override
    public Auth save(Auth auth) {
        log.info("Salvando entidade Auth ID: {}", auth.getId());
        Auth savedAuth = authRepository.save(auth);
        log.debug("Auth salvo com sucesso. ID: {}, Email: {}", savedAuth.getId(), savedAuth.getEmail());
        return savedAuth;
    }
    
    @Override
    public AuthResponse resendOtp(ResendOtpRequest request) {
        // Buscar usuário por email ou contato
        Auth auth = authRepository.findByEmailOrContact(request.emailOrContact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.emailOrContact()));

        // Gerar e enviar novo OTP (detecta automaticamente se é email ou telefone)
        String contactToSend = ContactValidator.isEmail(request.emailOrContact()) ? auth.getEmail() : auth.getContact();
        otpService.generateAndSendOtp(contactToSend, "LOGIN");

        String message = ContactValidator.isEmail(request.emailOrContact()) ? 
            "Novo OTP enviado para seu email. Verifique sua caixa de entrada." :
            "Novo OTP enviado para seu telefone. Verifique as mensagens.";
            
        return new AuthResponse(null, message, true);
    }

    @Override
    public AuthResponse resendBackOfficeOtp(ResendOtpRequest request) {
        // Buscar usuário por email ou contato com AccountType eagerly loaded
        Auth auth = authRepository.findByEmailOrContactWithAccountType(request.emailOrContact())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));

        // Verificar se Auth está ATIVO (state = 1)
        if (auth.getState() == null || !auth.getState().getId().equals(1L)) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        // VALIDAÇÃO ESPECÍFICA: Verificar se account type é "admin" 
        if (auth.getAccountType() == null || 
            !auth.getAccountType().getType().equalsIgnoreCase("admin")) {
            log.warn("Access denied - not admin account type: {}", request.emailOrContact());
            throw new InvalidCredentialsException("Access denied. Only admin users can access back office");
        }

        // VALIDAÇÃO OBRIGATÓRIA: Verificar se existe admin ativo na tabela admins
        if (!isActiveAdminByAuthId(auth.getId())) {
            log.warn("Access denied - admin account not found or inactive: {}", request.emailOrContact());
            throw new InvalidCredentialsException("Access denied. Admin account not found or inactive");
        }

        // Gerar e enviar novo OTP para o email registrado
        otpService.generateAndSendOtp(auth.getEmail(), "LOGIN_BACKOFFICE");
        log.info("OTP resent for back office login: {}", auth.getEmail());
            
        return new AuthResponse(null, "Novo OTP enviado para seu email registrado. Verifique sua caixa de entrada.", true);
    }

    @Override
    public AuthResponse resendUserOtp(ResendOtpRequest request) {
        // Buscar usuário por email ou contato
        Auth auth = authRepository.findByEmailOrContact(request.emailOrContact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.emailOrContact()));

        // Verificar se o usuário tem tipo_conta_id = 2 (CORPORATE - Usuários)
        if (auth.getAccountType() == null || !auth.getAccountType().getId().equals(2L)) {
            throw new IllegalArgumentException("Access denied. Only regular users can access this endpoint.");
        }

        // Gerar e enviar novo OTP (detecta automaticamente se é email ou telefone)
        String contactToSend = ContactValidator.isEmail(request.emailOrContact()) ? auth.getEmail() : auth.getContact();
        otpService.generateAndSendOtp(contactToSend, "LOGIN_USER");

        String message = ContactValidator.isEmail(request.emailOrContact()) ? 
            "Novo OTP enviado para seu email. Verifique sua caixa de entrada." :
            "Novo OTP enviado para seu telefone. Verifique as mensagens.";
            
        return new AuthResponse(null, message, true);
    }

    @Override
    public AuthResponse requestPasswordReset(ForgotPasswordRequest request) {
        // Buscar usuário por email ou contato
        Auth auth = authRepository.findByEmailOrContact(request.emailOrContact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.emailOrContact()));

        // Verificar se Auth está ATIVO (state = 1)
        if (auth.getState() == null || !auth.getState().getId().equals(1L)) {
            throw new InvalidCredentialsException("Conta inativa. Entre em contato com o suporte.");
        }

        // Gerar e enviar OTP (detecta automaticamente se é email ou telefone)
        String contactToSend = ContactValidator.isEmail(request.emailOrContact()) ? auth.getEmail() : auth.getContact();
        otpService.generateAndSendOtp(contactToSend, "PASSWORD_RESET");

        String message = ContactValidator.isEmail(request.emailOrContact()) ? 
            "Código de recuperação enviado para seu email. Verifique sua caixa de entrada." :
            "Código de recuperação enviado para seu telefone. Verifique as mensagens.";
            
        return new AuthResponse(null, message, true);
    }

    @Override
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        // Verificar OTP
        if (!otpService.verifyOtp(request.emailOrContact(), request.otpCode(), "PASSWORD_RESET")) {
            throw InvalidCredentialsException.expiredToken();
        }

        // Buscar usuário por email ou contato
        Auth auth = authRepository.findByEmailOrContact(request.emailOrContact())
                .orElseThrow(() -> UserNotFoundException.byEmailOrContact(request.emailOrContact()));

        // Verificar se Auth está ATIVO (state = 1)
        if (auth.getState() == null || !auth.getState().getId().equals(1L)) {
            throw new InvalidCredentialsException("Conta inativa. Entre em contato com o suporte.");
        }

        // Atualizar senha
        auth.setPassword(passwordEncoder.encode(request.newPassword()));
        authRepository.save(auth);

        log.info("Password reset successful for: {}", auth.getEmail());
        
        return new AuthResponse(null, "Senha alterada com sucesso! Você já pode fazer login com a nova senha.", false);
    }

    @Override
    public List<CompleteUserResponse> getAllAdmins() {
        List<Auth> admins = authRepository.findByAccountType_Type("admin");
        return admins.stream()
                .map(this::mapToCompleteUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompleteUserResponse> getAllUsers() {
        List<Auth> users = authRepository.findAll();
        return users.stream()
                .map(this::mapToCompleteUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PagedUserResponse<CompleteUserResponse> getAllAdminsPaged(PagedUserRequest request) {
        log.info("Retrieving paginated admins - page: {}, size: {}, search: {}, sortBy: {}, sortDirection: {}", 
                request.page(), request.size(), request.search(), request.sortBy(), request.sortDirection());
        
        try {
            // Validate input parameters
            if (request.page() < 0) {
                throw new IllegalArgumentException("Page number must be greater than or equal to 0");
            }
            if (request.size() < 1) {
                throw new IllegalArgumentException("Page size must be greater than 0");
            }
            
            // Create sort direction
            Sort.Direction direction = "ASC".equalsIgnoreCase(request.sortDirection()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
            
            // Create sort object - validate sortBy field
            Sort sort;
            switch (request.sortBy().toLowerCase()) {
                case "id", "email", "username", "contact", "createdat", "updatedat" ->
                    sort = Sort.by(direction, request.sortBy());
                default -> {
                    log.warn("Invalid sortBy field: {}. Using default 'createdAt'", request.sortBy());
                    sort = Sort.by(direction, "createdAt");
                }
            }
            
            // Create pageable
            Pageable pageable = PageRequest.of(request.page(), request.size(), sort);
            
            // Execute repository query
            Page<Auth> adminPage = authRepository.findAllAdminsPaged(request.search(), pageable);
            
            // Convert entities to DTOs
            List<CompleteUserResponse> adminResponses = adminPage.getContent()
                    .stream()
                    .map(this::mapToCompleteUserResponse)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} admins out of {} total", adminResponses.size(), adminPage.getTotalElements());
            
            // Return paginated response
            return PagedUserResponse.of(
                    adminResponses,
                    request.page(),
                    request.size(),
                    adminPage.getTotalElements()
            );
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid parameters for getAllAdminsPaged: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving paginated admins", e);
            throw new RuntimeException("Failed to retrieve paginated admins: " + e.getMessage(), e);
        }
    }

    @Override
    public PagedUserResponse<CompleteUserResponse> getAllUsersPaged(PagedUserRequest request) {
        log.info("Retrieving paginated users - page: {}, size: {}, search: {}, sortBy: {}, sortDirection: {}", 
                request.page(), request.size(), request.search(), request.sortBy(), request.sortDirection());
        
        try {
            // Validate input parameters
            if (request.page() < 0) {
                throw new IllegalArgumentException("Page number must be greater than or equal to 0");
            }
            if (request.size() < 1) {
                throw new IllegalArgumentException("Page size must be greater than 0");
            }
            
            // Create sort direction
            Sort.Direction direction = "ASC".equalsIgnoreCase(request.sortDirection()) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
            
            // Create sort object - validate sortBy field
            Sort sort;
            switch (request.sortBy().toLowerCase()) {
                case "id", "email", "username", "contact", "createdat", "updatedat" ->
                    sort = Sort.by(direction, request.sortBy());
                default -> {
                    log.warn("Invalid sortBy field: {}. Using default 'createdAt'", request.sortBy());
                    sort = Sort.by(direction, "createdAt");
                }
            }
            
            // Create pageable
            Pageable pageable = PageRequest.of(request.page(), request.size(), sort);
            
            // Execute repository query
            Page<Auth> userPage = authRepository.findAllUsersPaged(request.search(), pageable);
            
            // Convert entities to DTOs
            List<CompleteUserResponse> userResponses = userPage.getContent()
                    .stream()
                    .map(this::mapToCompleteUserResponse)
                    .collect(Collectors.toList());
            
            log.info("Retrieved {} users out of {} total", userResponses.size(), userPage.getTotalElements());
            
            // Return paginated response
            return PagedUserResponse.of(
                    userResponses,
                    request.page(),
                    request.size(),
                    userPage.getTotalElements()
            );
            
        } catch (IllegalArgumentException e) {
            log.error("Invalid parameters for getAllUsersPaged: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving paginated users", e);
            throw new RuntimeException("Failed to retrieve paginated users: " + e.getMessage(), e);
        }
    }

    @Override
    public CompleteUserResponse createCompleteUser(CreateCompleteUserRequest request) {
        log.info("Creating complete user with email: {} and username: {}", request.email(), request.username());
        
        // Validações de entrada
        if (request.email() == null || request.email().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (request.username() == null || request.username().trim().isEmpty()) {
            throw new IllegalArgumentException("Username é obrigatório");
        }
        if (request.password() == null || request.password().trim().isEmpty()) {
            throw new IllegalArgumentException("Password é obrigatória");
        }
        if (request.roleIds() == null || request.roleIds().isEmpty()) {
            throw new IllegalArgumentException("Pelo menos uma role deve ser especificada");
        }
        
        // Validar formato do email
        if (!request.email().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Formato de email inválido");
        }
        
        // Validar se email/contato/username já existem
        if (authRepository.existsByEmail(request.email().trim())) {
            throw UserAlreadyExistsException.withEmail(request.email());
        }
        if (request.contact() != null && !request.contact().trim().isEmpty() && 
            authRepository.existsByContact(request.contact().trim())) {
            throw UserAlreadyExistsException.withContact(request.contact());
        }
        if (authRepository.existsByUsername(request.username().trim())) {
            throw new IllegalArgumentException("Username já existe: " + request.username());
        }

        // Buscar e validar dependências
        AccountType accountType;
        try {
            accountType = accountTypeService.findById(request.accountTypeId());
            if (accountType == null) {
                throw new IllegalArgumentException("Tipo de conta não encontrado: " + request.accountTypeId());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Tipo de conta inválido: " + request.accountTypeId());
        }
        
        State activeState;
        try {
            activeState = request.stateId() != null ? 
                stateService.findById(request.stateId()) : 
                stateService.findById(1L); // ACTIVE por padrão
            if (activeState == null) {
                throw new IllegalArgumentException("Estado não encontrado");
            }
        } catch (Exception e) {
            log.warn("Error finding state, using default ACTIVE state", e);
            activeState = stateService.findById(1L);
        }

        // Validar e buscar roles
        List<Role> roles = new ArrayList<>();
        for (Long roleId : request.roleIds()) {
            try {
                Role role = roleService.findById(roleId);
                if (role == null) {
                    throw new IllegalArgumentException("Role não encontrada: " + roleId);
                }
                roles.add(role);
            } catch (Exception e) {
                throw new IllegalArgumentException("Role inválida: " + roleId + " - " + e.getMessage());
            }
        }
        
        // Validar consistência: se accountType for BackOffice, deve ter role ADMIN
        if (accountType.getId().equals(1L)) { // BackOffice
            boolean hasAdminRole = request.roleIds().contains(1L);
            if (!hasAdminRole) {
                throw new IllegalArgumentException("Contas BackOffice devem ter role ADMIN (ID: 1)");
            }
        }

        // Criar Auth com validações adicionais
        Auth auth = new Auth();
        auth.setEmail(request.email().trim().toLowerCase()); // Normalize email
        auth.setContact(request.contact() != null ? request.contact().trim() : null);
        auth.setUsername(request.username().trim());
        
        // Validar força da senha
        if (request.password().length() < 8) {
            throw new IllegalArgumentException("Password deve ter pelo menos 8 caracteres");
        }
        
        auth.setPassword(passwordEncoder.encode(request.password()));
        auth.setAccountType(accountType);
        auth.setState(activeState);
        
        // Salvar Auth com tratamento de erro
        Auth savedAuth;
        try {
            savedAuth = authRepository.save(auth);
            log.info("Auth entity created successfully with ID: {}", savedAuth.getId());
        } catch (Exception e) {
            log.error("Error saving Auth entity for email: {}", request.email(), e);
            throw new RuntimeException("Erro ao criar usuário: " + e.getMessage());
        }
        
        // Criar relacionamentos Auth-Roles com tratamento de erro
        try {
            for (Role role : roles) {
                AuthRoles authRole = new AuthRoles();
                authRole.setAuth(savedAuth);
                authRole.setRole(role);
                authRolesService.save(authRole);
                log.debug("Role {} assigned to user {}", role.getRole(), savedAuth.getEmail());
            }
        } catch (Exception e) {
            log.error("Error creating role relationships for user: {}", savedAuth.getEmail(), e);
            // Rollback: delete the created Auth
            try {
                authRepository.delete(savedAuth);
                log.info("Rollback: Auth entity deleted due to role assignment failure");
            } catch (Exception rollbackError) {
                log.error("Critical: Failed to rollback Auth creation", rollbackError);
            }
            throw new RuntimeException("Erro ao atribuir roles ao usuário: " + e.getMessage());
        }

        log.info("Complete user created successfully: {} with {} roles: {}", 
                savedAuth.getEmail(), roles.size(), request.roleIds());
        
        return mapToCompleteUserResponse(savedAuth);
    }

    /**
     * Mapeia Auth para CompleteUserResponse
     */
    private CompleteUserResponse mapToCompleteUserResponse(Auth auth) {
        // Buscar roles do usuário
        List<AuthRoles> authRoles = authRolesService.findByAuthId(auth.getId());
        List<CompleteUserResponse.RoleInfo> roleInfos = authRoles.stream()
                .map(ar -> new CompleteUserResponse.RoleInfo(
                    ar.getRole().getId(),
                    ar.getRole().getRole(),
                    ar.getRole().getDescription()
                ))
                .collect(Collectors.toList());

        // Role primária (primeira da lista)
        Long primaryRoleId = !roleInfos.isEmpty() ? roleInfos.get(0).id() : null;
        String primaryRoleName = !roleInfos.isEmpty() ? roleInfos.get(0).name() : null;

        // Extrair IDs das roles
        List<Long> roleIds = roleInfos.stream()
                .map(CompleteUserResponse.RoleInfo::id)
                .collect(Collectors.toList());

        // Verificar tipos de usuário
        boolean isAdmin = roleIds.contains(1L);
        boolean isBackOffice = auth.getAccountType() != null && 
                               auth.getAccountType().getId().equals(1L);
        boolean isCorporate = auth.getAccountType() != null && 
                             auth.getAccountType().getId().equals(2L);
        boolean isAgent = roleIds.contains(3L);
        boolean isManager = roleIds.contains(4L);

        return new CompleteUserResponse(
            auth.getId(),
            null, // name não está disponível em Auth
            auth.getEmail(),
            auth.getContact(),
            auth.getUsername(),
            auth.getAccountType() != null ? auth.getAccountType().getId() : null,
            auth.getAccountType() != null ? auth.getAccountType().getType() : null,
            primaryRoleId,
            primaryRoleName,
            roleInfos,
            auth.getState() != null ? auth.getState().getId() : null,
            auth.getState() != null ? auth.getState().getState() : null,
            auth.getCreatedAt(),
            auth.getUpdatedAt(),
            isAdmin,
            isBackOffice,
            isCorporate,
            isAgent,
            isManager
        );
    }

    // === MÉTODOS DE GESTÃO DE USUÁRIOS AVANÇADA ===
    
    @Override
    public UserManagementResponse assignPermissions(UserPermissionsRequest request) {
        log.info("Assigning permissions to user ID: {} with roles: {}", request.userId(), request.roleIds());
        
        try {
            // Buscar usuário
            Auth auth = authRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + request.userId()));
            
            // Verificar se usuário está ativo
            if (auth.getState() == null || !auth.getState().getId().equals(1L)) {
                throw new RuntimeException("Não é possível atribuir permissões a usuário inativo");
            }
            
            // Validar roles
            List<Role> newRoles = new ArrayList<>();
            for (Long roleId : request.roleIds()) {
                Role role = roleService.findById(roleId);
                if (role == null) {
                    throw new RuntimeException("Role não encontrada: " + roleId);
                }
                newRoles.add(role);
            }
            
            // Remover todas as roles existentes
            List<AuthRoles> existingRoles = authRolesService.findByAuthId(auth.getId());
            for (AuthRoles existingRole : existingRoles) {
                authRolesService.delete(existingRole);
            }
            
            // Adicionar novas roles
            for (Role role : newRoles) {
                AuthRoles authRole = new AuthRoles();
                authRole.setAuth(auth);
                authRole.setRole(role);
                authRolesService.save(authRole);
            }
            
            log.info("Permissions assigned successfully to user: {}", auth.getEmail());
            CompleteUserResponse updatedUser = mapToCompleteUserResponse(auth);
            return UserManagementResponse.success("Permissões atribuídas com sucesso", updatedUser);
            
        } catch (Exception e) {
            log.error("Error assigning permissions to user ID: {}", request.userId(), e);
            return UserManagementResponse.error("Erro ao atribuir permissões: " + e.getMessage());
        }
    }
    
    @Override
    public UserManagementResponse changeAccountType(ChangeAccountTypeRequest request) {
        log.info("Changing account type for user ID: {} to account type ID: {}", 
                request.userId(), request.accountTypeId());
        
        try {
            // Buscar usuário
            Auth auth = authRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + request.userId()));
            
            // Buscar novo tipo de conta
            AccountType newAccountType = accountTypeService.findById(request.accountTypeId());
            if (newAccountType == null) {
                throw new RuntimeException("Tipo de conta não encontrado: " + request.accountTypeId());
            }
            
            // Validar consistência: se mudar para BackOffice, deve ter role ADMIN
            if (newAccountType.getId().equals(1L)) { // BackOffice
                List<AuthRoles> userRoles = authRolesService.findByAuthId(auth.getId());
                boolean hasAdminRole = userRoles.stream()
                        .anyMatch(ar -> ar.getRole().getId().equals(1L));
                if (!hasAdminRole) {
                    throw new RuntimeException("Para alterar para conta BackOffice, usuário deve ter role ADMIN");
                }
            }
            
            // Atualizar tipo de conta
            auth.setAccountType(newAccountType);
            authRepository.save(auth);
            
            log.info("Account type changed successfully for user: {}", auth.getEmail());
            CompleteUserResponse updatedUser = mapToCompleteUserResponse(auth);
            return UserManagementResponse.success("Tipo de conta alterado com sucesso", updatedUser);
            
        } catch (Exception e) {
            log.error("Error changing account type for user ID: {}", request.userId(), e);
            return UserManagementResponse.error("Erro ao alterar tipo de conta: " + e.getMessage());
        }
    }
    
    @Override
    public UserManagementResponse addRole(ManageRoleRequest request) {
        log.info("Adding role ID: {} to user ID: {}", request.roleId(), request.userId());
        
        try {
            // Buscar usuário
            Auth auth = authRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + request.userId()));
            
            // Verificar se usuário está ativo
            if (auth.getState() == null || !auth.getState().getId().equals(1L)) {
                throw new RuntimeException("Não é possível adicionar role a usuário inativo");
            }
            
            // Buscar role
            Role role = roleService.findById(request.roleId());
            if (role == null) {
                throw new RuntimeException("Role não encontrada: " + request.roleId());
            }
            
            // Verificar se usuário já tem essa role
            List<AuthRoles> existingRoles = authRolesService.findByAuthId(auth.getId());
            boolean hasRole = existingRoles.stream()
                    .anyMatch(ar -> ar.getRole().getId().equals(request.roleId()));
            
            if (hasRole) {
                return UserManagementResponse.error("Usuário já possui essa role");
            }
            
            // Adicionar nova role
            AuthRoles authRole = new AuthRoles();
            authRole.setAuth(auth);
            authRole.setRole(role);
            authRolesService.save(authRole);
            
            log.info("Role {} added successfully to user: {}", role.getRole(), auth.getEmail());
            CompleteUserResponse updatedUser = mapToCompleteUserResponse(auth);
            return UserManagementResponse.success("Role adicionada com sucesso", updatedUser);
            
        } catch (Exception e) {
            log.error("Error adding role to user ID: {}", request.userId(), e);
            return UserManagementResponse.error("Erro ao adicionar role: " + e.getMessage());
        }
    }
    
    @Override
    public UserManagementResponse removeRole(ManageRoleRequest request) {
        log.info("Removing role ID: {} from user ID: {}", request.roleId(), request.userId());
        
        try {
            // Buscar usuário
            Auth auth = authRepository.findById(request.userId())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + request.userId()));
            
            // Buscar AuthRole específica
            List<AuthRoles> authRoles = authRolesService.findByAuthId(auth.getId());
            AuthRoles authRoleToRemove = authRoles.stream()
                    .filter(ar -> ar.getRole().getId().equals(request.roleId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Usuário não possui essa role"));
            
            // Verificar se não é a última role do usuário
            if (authRoles.size() <= 1) {
                throw new RuntimeException("Não é possível remover a última role do usuário");
            }
            
            // Verificar consistência: se for BackOffice e tentar remover ADMIN
            if (auth.getAccountType() != null && auth.getAccountType().getId().equals(1L) && // BackOffice
                request.roleId().equals(1L)) { // ADMIN role
                throw new RuntimeException("Usuários BackOffice devem manter a role ADMIN");
            }
            
            // Remover role
            authRolesService.delete(authRoleToRemove);
            
            log.info("Role {} removed successfully from user: {}", 
                    authRoleToRemove.getRole().getRole(), auth.getEmail());
            CompleteUserResponse updatedUser = mapToCompleteUserResponse(auth);
            return UserManagementResponse.success("Role removida com sucesso", updatedUser);
            
        } catch (Exception e) {
            log.error("Error removing role from user ID: {}", request.userId(), e);
            return UserManagementResponse.error("Erro ao remover role: " + e.getMessage());
        }
    }
    
    @Override
    public UserManagementResponse activateUser(Long userId) {
        log.info("Activating user ID: {}", userId);
        
        try {
            // Buscar usuário
            Auth auth = authRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));
            
            // Buscar estado ACTIVE
            State activeState = stateService.findById(1L); // ACTIVE
            if (activeState == null) {
                throw new RuntimeException("Estado ACTIVE não encontrado no sistema");
            }
            
            // Atualizar estado
            auth.setState(activeState);
            authRepository.save(auth);
            
            log.info("User activated successfully: {}", auth.getEmail());
            CompleteUserResponse updatedUser = mapToCompleteUserResponse(auth);
            return UserManagementResponse.success("Usuário ativado com sucesso", updatedUser);
            
        } catch (Exception e) {
            log.error("Error activating user ID: {}", userId, e);
            return UserManagementResponse.error("Erro ao ativar usuário: " + e.getMessage());
        }
    }
    
    @Override
    public UserManagementResponse deactivateUser(Long userId) {
        log.info("Deactivating user ID: {}", userId);
        
        try {
            // Buscar usuário
            Auth auth = authRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));
            
            // Buscar estado INACTIVE
            State inactiveState = stateService.findById(2L); // INACTIVE
            if (inactiveState == null) {
                throw new RuntimeException("Estado INACTIVE não encontrado no sistema");
            }
            
            // Atualizar estado
            auth.setState(inactiveState);
            authRepository.save(auth);
            
            log.info("User deactivated successfully: {}", auth.getEmail());
            CompleteUserResponse updatedUser = mapToCompleteUserResponse(auth);
            return UserManagementResponse.success("Usuário desativado com sucesso", updatedUser);
            
        } catch (Exception e) {
            log.error("Error deactivating user ID: {}", userId, e);
            return UserManagementResponse.error("Erro ao desativar usuário: " + e.getMessage());
        }
    }
    
    @Override
    public CompleteUserResponse getUserDetails(Long userId) {
        log.info("Getting user details for ID: {}", userId);
        
        Auth auth = authRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + userId));
        
        return mapToCompleteUserResponse(auth);
    }
    
    @Override
    public UserStatisticsResponse getSystemStatistics() {
        log.info("Generating system statistics...");
        
        try {
            // Estatísticas de usuários
            long totalUsersRegistered = authRepository.countTotalUsersRegistered();
            long totalActiveUsers = authRepository.countTotalActiveUsers();
            long totalInactiveUsers = authRepository.countTotalInactiveUsers();
            
            // Estatísticas de mensagens
            long totalMessagesGlobal = messageCountRepository.countTotalMessagesGlobal();
            List<UserStatisticsResponse.UserMessageCount> messagesByUser = 
                    messageCountRepository.findMessageCountByUser();
            
            log.info("System statistics generated successfully - Users: {}, Active: {}, Messages: {}", 
                    totalUsersRegistered, totalActiveUsers, totalMessagesGlobal);
            
            return new UserStatisticsResponse(
                    totalUsersRegistered,
                    totalActiveUsers,
                    totalInactiveUsers,
                    totalMessagesGlobal,
                    messagesByUser
            );
            
        } catch (Exception e) {
            log.error("Error generating system statistics", e);
            throw new RuntimeException("Erro ao gerar estatísticas do sistema: " + e.getMessage());
        }
    }

    /**
     * Verifica se existe admin ativo por Auth ID
     * Implementado diretamente aqui para evitar dependência circular
     */
    private boolean isActiveAdminByAuthId(Long authId) {
        try {
            Optional<Admin> adminOptional = adminRepository.findByAuthId(authId);
            if (adminOptional.isEmpty()) {
                return false;
            }
            
            Admin admin = adminOptional.get();
            // Verificar se o admin está ativo (state_id = 1)
            return admin.getState() != null && admin.getState().getId().equals(1L);
        } catch (Exception e) {
            log.error("Error checking admin status for auth ID: {} - {}", authId, e.getMessage());
            return false;
        }
    }
    
    @Override
    public AgentStatisticsResponse getAgentStatistics(Long agentId) {
        log.info("Generating agent statistics for agentId: {}", agentId);
        
        try {
            // Busca informações do agente
            Auth agent = this.findById(agentId);
            
            // Estatísticas de leads captados
            long totalLeadsCaptured = leadRepository.countLeadsCapturedByAgent(agentId);
            long totalActiveLeads = leadRepository.countActiveLeadsCapturedByAgent(agentId);
            
            // Estatísticas de negócios fechados
            long totalDealsClosked = dealRepository.countDealsClosedByAgent(agentId);
            
            // Total de mensagens enviadas pelo agente
            long totalMessagesSent = messageCountRepository.countTotalMessagesByAgent(agentId);
            
            // Médias de mensagens por lead e por deal para este agente
            Double avgMessagesPerLead = messageCountRepository.calculateAverageMessagesPerLeadByAgent(agentId);
            Double avgMessagesPerDeal = dealRepository.calculateAverageMessagesPerDealByAgent(agentId);
            
            // Médias globais para comparação
            Double globalAvgMessagesPerLead = messageCountRepository.calculateGlobalAverageMessagesPerLead();
            Double globalAvgMessagesPerDeal = dealRepository.calculateGlobalAverageMessagesPerDeal();
            
            log.info("Agent statistics generated successfully for agentId: {} - Leads: {}, Deals: {}, Messages: {}", 
                    agentId, totalLeadsCaptured, totalDealsClosked, totalMessagesSent);
            
            return new AgentStatisticsResponse(
                    agentId,
                    agent.getUsername(),
                    agent.getEmail(),
                    totalLeadsCaptured,
                    totalActiveLeads,
                    totalDealsClosked,
                    totalMessagesSent,
                    avgMessagesPerLead != null ? avgMessagesPerLead : 0.0,
                    avgMessagesPerDeal != null ? avgMessagesPerDeal : 0.0,
                    globalAvgMessagesPerLead != null ? globalAvgMessagesPerLead : 0.0,
                    globalAvgMessagesPerDeal != null ? globalAvgMessagesPerDeal : 0.0
            );
            
        } catch (Exception e) {
            log.error("Error generating agent statistics for agentId: {}", agentId, e);
            throw new RuntimeException("Erro ao gerar estatísticas do agente: " + e.getMessage());
        }
    }
} 