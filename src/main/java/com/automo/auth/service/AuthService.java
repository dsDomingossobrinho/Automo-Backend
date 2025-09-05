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

import java.util.List;
import java.util.Optional;

public interface AuthService {

    /**
     * Registra um novo usuário
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Solicita OTP para autenticação
     */
    AuthResponse requestOtp(OtpRequest request);

    /**
     * Verifica OTP e autentica usuário
     */
    AuthResponse verifyOtpAndAuthenticate(OtpVerificationRequest request);

    /**
     * Reenvia OTP para autenticação geral
     */
    AuthResponse resendOtp(ResendOtpRequest request);

    /**
     * Autentica back office
     */
    AuthResponse authenticateBackOffice(OtpRequest request);

    /**
     * Verifica OTP e autentica back office
     */
    AuthResponse verifyOtpAndAuthenticateBackOffice(OtpVerificationRequest request);

    /**
     * Reenvia OTP para autenticação back office
     */
    AuthResponse resendBackOfficeOtp(ResendOtpRequest request);

    /**
     * Autentica usuário
     */
    AuthResponse authenticateUser(OtpRequest request);

    /**
     * Verifica OTP e autentica usuário
     */
    AuthResponse verifyOtpAndAuthenticateUser(OtpVerificationRequest request);

    /**
     * Reenvia OTP para autenticação de usuário
     */
    AuthResponse resendUserOtp(ResendOtpRequest request);

    /**
     * Autentica usuário com login direto (sem OTP)
     */
    LoginResponse authenticate(LoginRequest request);

    /**
     * Busca usuário por email, username ou contato
     */
    Optional<Auth> findByEmailOrUsernameOrContact(String emailOrContact);
    
    /**
     * Busca Auth por ID - método obrigatório para comunicação entre services
     */
    Auth findById(Long id);
    
    /**
     * Busca Auth por ID e estado específico (state_id = 1 por padrão)
     */
    Auth findByIdAndStateId(Long id, Long stateId);
    
    /**
     * Gera um username único e válido baseado no nome fornecido
     */
    String generateUniqueUsername(String name);
    
    /**
     * Salva ou atualiza uma entidade Auth - método obrigatório para comunicação entre services
     */
    Auth save(Auth auth);
    
    /**
     * Solicita OTP para recuperação de senha
     */
    AuthResponse requestPasswordReset(ForgotPasswordRequest request);
    
    /**
     * Verifica OTP e altera a senha
     */
    AuthResponse resetPassword(ResetPasswordRequest request);
    
    /**
     * Lista todos os admins do sistema (BackOffice)
     */
    List<CompleteUserResponse> getAllAdmins();
    
    /**
     * Lista todos os usuários do sistema
     */
    List<CompleteUserResponse> getAllUsers();
    
    /**
     * Lista todos os admins do sistema com paginação e pesquisa
     */
    PagedUserResponse<CompleteUserResponse> getAllAdminsPaged(PagedUserRequest request);
    
    /**
     * Lista todos os usuários do sistema com paginação e pesquisa
     */
    PagedUserResponse<CompleteUserResponse> getAllUsersPaged(PagedUserRequest request);
    
    /**
     * Cria um usuário completo com roles e configurações
     */
    CompleteUserResponse createCompleteUser(CreateCompleteUserRequest request);
    
    // === GESTÃO DE USUÁRIOS AVANÇADA ===
    
    /**
     * Atribui permissões (roles) a um usuário - substitui todas as roles existentes
     */
    UserManagementResponse assignPermissions(UserPermissionsRequest request);
    
    /**
     * Altera o tipo de conta de um usuário
     */
    UserManagementResponse changeAccountType(ChangeAccountTypeRequest request);
    
    /**
     * Adiciona uma role a um usuário
     */
    UserManagementResponse addRole(ManageRoleRequest request);
    
    /**
     * Remove uma role de um usuário
     */
    UserManagementResponse removeRole(ManageRoleRequest request);
    
    /**
     * Ativa um usuário (muda state para ACTIVE)
     */
    UserManagementResponse activateUser(Long userId);
    
    /**
     * Desativa um usuário (muda state para INACTIVE)
     */
    UserManagementResponse deactivateUser(Long userId);
    
    /**
     * Obtém detalhes completos de um usuário por ID
     */
    CompleteUserResponse getUserDetails(Long userId);
    
    // === ESTATÍSTICAS ===
    
    /**
     * Obtém estatísticas completas do sistema
     */
    UserStatisticsResponse getSystemStatistics();
    
    /**
     * Obtém estatísticas específicas de um agente
     */
    AgentStatisticsResponse getAgentStatistics(Long agentId);
} 