package com.automo.auth.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CompleteUserResponse(
    Long id,
    String name,
    String email,
    String contact,
    String username,
    Long accountTypeId,
    String accountTypeName,
    Long primaryRoleId,
    String primaryRoleName,
    List<RoleInfo> allRoles,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    boolean isAdmin,
    boolean isBackOffice,
    boolean isCorporate,
    boolean isAgent,
    boolean isManager
) {
    public record RoleInfo(
        Long id,
        String name,
        String description
    ) {}
}