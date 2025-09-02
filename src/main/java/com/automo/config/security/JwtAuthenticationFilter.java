package com.automo.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        log.debug("JWT Filter - Request: {} {}", request.getMethod(), request.getRequestURI());
        log.debug("JWT Filter - Authorization Header: {}", authHeader);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("JWT Filter - No valid Authorization header found, continuing filter chain");
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7).trim();
        log.debug("JWT Filter - JWT Token extracted: {}...", jwt.substring(0, Math.min(jwt.length(), 20)));
        
        userEmail = jwtService.extractUsername(jwt);
        log.debug("JWT Filter - Username extracted from JWT: {}", userEmail);
        
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.debug("JWT Filter - Loading user details for: {}", userEmail);
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                log.debug("JWT Filter - User details loaded successfully for: {}", userEmail);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.debug("JWT Filter - JWT token is valid for user: {}", userEmail);
                    // Criar um token de autenticação que inclui o email como principal
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userEmail, // Armazenar o email como principal para fácil acesso
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("JWT Filter - Authentication set in SecurityContext for user: {}", userEmail);
                } else {
                    log.warn("JWT Filter - JWT token is INVALID for user: {}", userEmail);
                }
            } catch (Exception e) {
                log.error("JWT Filter - Error loading user details for: {}", userEmail, e);
            }
        } else {
            if (userEmail == null) {
                log.warn("JWT Filter - Could not extract username from JWT");
            } else {
                log.debug("JWT Filter - User already authenticated or username is null: {}", userEmail);
            }
        }
        
        filterChain.doFilter(request, response);
    }
} 