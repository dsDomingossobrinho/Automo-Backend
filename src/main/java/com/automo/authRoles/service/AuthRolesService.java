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
} 