package com.automo.country.response;

import java.time.LocalDateTime;

public record CountryResponse(
    Long id,
    String country,
    Integer numberDigits,
    String indicative,
    Long stateId,
    String stateName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 