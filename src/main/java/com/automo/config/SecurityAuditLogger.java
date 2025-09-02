package com.automo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@Component
public class SecurityAuditLogger {

    public void logSuccessfulLogin(String userIdentifier, String ipAddress, String userAgent) {
        log.info("SECURITY_AUDIT: Successful login - User: {}, IP: {}, UserAgent: {}, Time: {}", 
                userIdentifier, ipAddress, userAgent, LocalDateTime.now());
    }

    public void logFailedLogin(String userIdentifier, String ipAddress, String reason) {
        log.warn("SECURITY_AUDIT: Failed login - User: {}, IP: {}, Reason: {}, Time: {}", 
                userIdentifier, ipAddress, reason, LocalDateTime.now());
    }

    public void logPasswordChange(String userIdentifier, String ipAddress) {
        log.info("SECURITY_AUDIT: Password changed - User: {}, IP: {}, Time: {}", 
                userIdentifier, ipAddress, LocalDateTime.now());
    }

    public void logTokenRefresh(String userIdentifier, String ipAddress) {
        log.info("SECURITY_AUDIT: Token refreshed - User: {}, IP: {}, Time: {}", 
                userIdentifier, ipAddress, LocalDateTime.now());
    }

    public void logAccountLocked(String userIdentifier, String reason) {
        log.warn("SECURITY_AUDIT: Account locked - User: {}, Reason: {}, Time: {}", 
                userIdentifier, reason, LocalDateTime.now());
    }

    public void logPrivilegeEscalation(String userIdentifier, String fromRole, String toRole, String ipAddress) {
        log.warn("SECURITY_AUDIT: Privilege change - User: {}, From: {}, To: {}, IP: {}, Time: {}", 
                userIdentifier, fromRole, toRole, ipAddress, LocalDateTime.now());
    }

    public void logSuspiciousActivity(String userIdentifier, String activity, String ipAddress) {
        log.error("SECURITY_AUDIT: Suspicious activity - User: {}, Activity: {}, IP: {}, Time: {}", 
                userIdentifier, activity, ipAddress, LocalDateTime.now());
    }

    public void logDataAccess(String userIdentifier, String resourceType, String resourceId, String action, String ipAddress) {
        log.info("SECURITY_AUDIT: Data access - User: {}, Resource: {}:{}, Action: {}, IP: {}, Time: {}", 
                userIdentifier, resourceType, resourceId, action, ipAddress, LocalDateTime.now());
    }

    public String extractIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        
        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }
        
        return request.getRemoteAddr();
    }
}