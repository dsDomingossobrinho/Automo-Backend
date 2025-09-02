package com.automo.authRoles.service;

import com.automo.authRoles.dto.AuthRolesDto;
import com.automo.authRoles.response.AuthRolesResponse;

import java.util.List;

public interface AuthRolesService {
    
    AuthRolesResponse createAuthRoles(AuthRolesDto authRolesDto);
    
    AuthRolesResponse getAuthRolesById(Long id);
    
    List<AuthRolesResponse> getAllAuthRoles();
    
    AuthRolesResponse updateAuthRoles(Long id, AuthRolesDto authRolesDto);
    
    void deleteAuthRoles(Long id);
    
    List<AuthRolesResponse> getAuthRolesByAuthId(Long authId);
    
    List<AuthRolesResponse> getAuthRolesByRoleId(Long roleId);
    
    List<AuthRolesResponse> getAuthRolesByStateId(Long stateId);
    
    /**
     * Busca AuthRoles por ID - método obrigatório para comunicação entre services
     */
    com.automo.authRoles.entity.AuthRoles findById(Long id);
    
    /**
     * Busca AuthRoles por ID e estado específico (state_id = 1 por padrão)
     */
    com.automo.authRoles.entity.AuthRoles findByIdAndStateId(Long id, Long stateId);
    
    /**
     * Cria um AuthRoles com entidades já resolvidas - usado pelo AuthService
     */
    void createAuthRolesWithEntities(com.automo.auth.entity.Auth auth, com.automo.role.entity.Role role, com.automo.state.entity.State state);
} 