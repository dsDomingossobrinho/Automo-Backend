package com.automo.auth.dto;

import java.util.List;

public record UserStatisticsResponse(
    long totalUsersRegistered,
    long totalActiveUsers,
    long totalInactiveUsers,
    long totalMessagesGlobal,
    List<UserMessageCount> messagesByUser
) {
    public record UserMessageCount(
        Long userId,
        String userName,
        String userEmail,
        long messageCount
    ) {}
}