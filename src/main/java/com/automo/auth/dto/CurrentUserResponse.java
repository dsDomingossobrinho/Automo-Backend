package com.automo.auth.dto;

import java.util.List;

public record CurrentUserResponse(
    Long id,
    String email,
    String username,
    String contact,
    String name,
    String img,
    Long accountTypeId,
    String accountType,
    List<String> roles,
    Long stateId,
    String state
) {}

