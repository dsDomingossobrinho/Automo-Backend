package com.automo.user.response;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String email,
    String name,
    String img,
    Long authId,
    String authUsername,
    Long countryId,
    String countryName,
    Long organizationTypeId,
    String organizationTypeName,
    Long provinceId,
    String provinceName,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 