package com.automo.authRoles.repository;

import com.automo.authRoles.entity.AuthRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthRolesRepository extends JpaRepository<AuthRoles, Long> {
    
    List<AuthRoles> findByAuthId(Long authId);
    
    List<AuthRoles> findByRoleId(Long roleId);
    
    List<AuthRoles> findByStateId(Long stateId);
    
    boolean existsByAuthIdAndRoleId(Long authId, Long roleId);
} 