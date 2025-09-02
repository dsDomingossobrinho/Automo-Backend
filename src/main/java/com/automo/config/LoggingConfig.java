package com.automo.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

@Configuration
public class LoggingConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new CommonsRequestLoggingFilter();
        
        loggingFilter.setIncludeClientInfo(true);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(false); // Disable for security (sensitive data)
        loggingFilter.setIncludeHeaders(false); // Disable for security (Authorization header)
        loggingFilter.setMaxPayloadLength(0);
        
        loggingFilter.setBeforeMessagePrefix("REQUEST: ");
        loggingFilter.setAfterMessagePrefix("RESPONSE: ");
        
        return loggingFilter;
    }
    
    public static void setMDC(String userId, String operation) {
        MDC.put("userId", userId);
        MDC.put("operation", operation);
        MDC.put("correlationId", UUID.randomUUID().toString());
    }
    
    public static void setMDC(HttpServletRequest request, String operation) {
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }
        
        MDC.put("correlationId", correlationId);
        MDC.put("operation", operation);
        MDC.put("remoteAddr", request.getRemoteAddr());
        MDC.put("userAgent", request.getHeader("User-Agent"));
    }
    
    public static void clearMDC() {
        MDC.clear();
    }
}