package com.automo.auth.dto;

public record AgentStatisticsResponse(
    Long agentId,
    String agentName,
    String agentEmail,
    long totalLeadsCaptured,
    long totalActiveLeads,
    long totalDealsClosked,
    long totalMessagesSent,
    double averageMessagesPerLead,
    double averageMessagesPerDeal,
    double globalAverageMessagesPerLead,
    double globalAverageMessagesPerDeal
) {}