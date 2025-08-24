package com.automo.province.response;

import java.time.LocalDateTime;

public record ProvinceResponse(
    Long id,
    String province,
    Long countryId,
    String countryName,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 