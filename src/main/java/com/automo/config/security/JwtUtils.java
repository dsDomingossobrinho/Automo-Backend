package com.automo.config.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtUtils {

    private final JwtService jwtService;

    public JwtUtils(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Obtém o token atual do contexto de segurança
     */
    public String getCurrentToken() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            return (String) principal;
        }
        return null;
    }

    /**
     * Obtém o ID do usuário atual do token
     */
    public Long getCurrentUserId() {
        String token = getCurrentToken();
        return token != null ? jwtService.extractUserId(token) : null;
    }

    /**
     * Obtém o email do usuário atual do token
     */
    public String getCurrentUserEmail() {
        String token = getCurrentToken();
        return token != null ? jwtService.extractEmail(token) : null;
    }

    /**
     * Obtém o contato do usuário atual do token
     */
    public String getCurrentUserContact() {
        String token = getCurrentToken();
        return token != null ? jwtService.extractContact(token) : null;
    }

    /**
     * Obtém o ID da role principal do usuário atual do token
     */
    public Long getCurrentUserRoleId() {
        String token = getCurrentToken();
        return token != null ? jwtService.extractRoleId(token) : null;
    }

    /**
     * Obtém todos os IDs das roles do usuário atual do token
     */
    public List<Long> getCurrentUserRoleIds() {
        String token = getCurrentToken();
        return token != null ? jwtService.extractRoleIds(token) : null;
    }

    /**
     * Obtém o ID do tipo de conta do usuário atual do token
     */
    public Long getCurrentUserAccountTypeId() {
        String token = getCurrentToken();
        return token != null ? jwtService.extractAccountTypeId(token) : null;
    }

    /**
     * Obtém o username do usuário atual do token
     */
    public String getCurrentUsername() {
        String token = getCurrentToken();
        return token != null ? jwtService.extractUsernameFromToken(token) : null;
    }

    /**
     * Verifica se o usuário atual é do tipo Back Office (account_type_id = 1)
     */
    public boolean isCurrentUserBackOffice() {
        Long accountTypeId = getCurrentUserAccountTypeId();
        return accountTypeId != null && accountTypeId == 1L;
    }

    /**
     * Verifica se o usuário atual é do tipo Corporate (account_type_id = 2)
     */
    public boolean isCurrentUserCorporate() {
        Long accountTypeId = getCurrentUserAccountTypeId();
        return accountTypeId != null && accountTypeId == 2L;
    }

    /**
     * Verifica se o usuário atual tem uma role específica
     */
    public boolean hasCurrentUserRole(Long roleId) {
        List<Long> roleIds = getCurrentUserRoleIds();
        return roleIds != null && roleIds.contains(roleId);
    }

    /**
     * Verifica se o usuário atual tem pelo menos uma das roles especificadas
     */
    public boolean hasCurrentUserAnyRole(List<Long> roleIds) {
        List<Long> currentRoleIds = getCurrentUserRoleIds();
        if (currentRoleIds == null || roleIds == null) {
            return false;
        }
        return currentRoleIds.stream().anyMatch(roleIds::contains);
    }

    /**
     * Verifica se o usuário atual é administrador (role_id = 1)
     */
    public boolean isCurrentUserAdmin() {
        return hasCurrentUserRole(1L);
    }

    /**
     * Verifica se o usuário atual é usuário padrão (role_id = 2)
     */
    public boolean isCurrentUserRegular() {
        return hasCurrentUserRole(2L);
    }

    /**
     * Verifica se o usuário atual é agente (role_id = 3)
     */
    public boolean isCurrentUserAgent() {
        return hasCurrentUserRole(3L);
    }

    /**
     * Verifica se o usuário atual é gerente (role_id = 4)
     */
    public boolean isCurrentUserManager() {
        return hasCurrentUserRole(4L);
    }
} 