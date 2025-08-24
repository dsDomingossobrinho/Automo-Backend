package com.automo.role.service;

import com.automo.role.dto.RoleDto;
import com.automo.role.entity.Role;
import com.automo.role.response.RoleResponse;

import java.util.List;

public interface RoleService {

    RoleResponse createRole(RoleDto roleDto);

    RoleResponse updateRole(Long id, RoleDto roleDto);

    List<RoleResponse> getAllRoles();

    Role getRoleById(Long id);

    RoleResponse getRoleByIdResponse(Long id);

    Role getRoleByRole(String role);

    void deleteRole(Long id);
} 