package com.automo.notification.dto;

import jakarta.validation.constraints.NotNull;

public record NotificationDto(
    @NotNull(message = "ID do remetente é obrigatório")
    Long senderId,
    
    @NotNull(message = "ID do destinatário é obrigatório")
    Long receiverId,
    
    String urlRedirect,
    
    @NotNull(message = "ID do estado é obrigatório")
    Long stateId
) {} 