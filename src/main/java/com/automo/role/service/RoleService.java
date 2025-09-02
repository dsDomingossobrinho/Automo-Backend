package com.automo.role.service;

import com.automo.role.dto.RoleDto;
import com.automo.role.entity.Role;
import com.automo.role.response.RoleResponse;

import java.util.List;

public interface RoleService {

    /**
     * Cria um novo role
     */
    RoleResponse createRole(RoleDto roleDto);

    /**
     * Atualiza um role existente
     */
    RoleResponse updateRole(Long id, RoleDto roleDto);

    /**
     * Obtém todos os roles
     */
    List<RoleResponse> getAllRoles();

    /**
     * Obtém role por ID
     */
    Role getRoleById(Long id);

    /**
     * Obtém role por ID com resposta DTO
     */
    RoleResponse getRoleByIdResponse(Long id);

    /**
     * Obtém role por nome do role
     */
    Role getRoleByRole(String role);

    /**
     * Deleta um role
     */
    void deleteRole(Long id);
    
    /**
     * Busca Role por ID - método obrigatório para comunicação entre services
     */
    Role findById(Long id);
    
    /**
     * Busca Role por ID e estado específico (state_id = 1 por padrão)
     */
    Role findByIdAndStateId(Long id, Long stateId);
} 