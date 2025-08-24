package com.automo.config.security;

import com.automo.auth.entity.Auth;
import com.automo.authRoles.entity.AuthRoles;
import com.automo.authRoles.repository.AuthRolesRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final AuthRolesRepository authRolesRepository;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey;
    
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    public String generateToken(Auth auth) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", auth.getId());
        claims.put("contact", auth.getContact());
        claims.put("email", auth.getEmail());
        
        // Buscar roles através de AuthRoles
        List<AuthRoles> authRoles = authRolesRepository.findByAuthId(auth.getId());
        List<Long> roleIds = authRoles.stream()
                .map(authRole -> authRole.getRole().getId())
                .collect(Collectors.toList());
        claims.put("role_ids", roleIds);
        
        // Para compatibilidade, manter o role_id principal (primeira role)
        if (!roleIds.isEmpty()) {
            claims.put("role_id", roleIds.get(0));
        }
        
        claims.put("account_type_id", auth.getAccountType().getId());
        claims.put("username", auth.getUsername());
        return generateToken(claims, auth, jwtExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims, Auth auth, long expiration) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(auth.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshExpiration);
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith((javax.crypto.SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Métodos para extrair dados específicos do token
    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("id", Long.class));
    }

    public String extractContact(String token) {
        return extractClaim(token, claims -> claims.get("contact", String.class));
    }

    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    public Long extractRoleId(String token) {
        return extractClaim(token, claims -> claims.get("role_id", Long.class));
    }

    @SuppressWarnings("unchecked")
    public List<Long> extractRoleIds(String token) {
        return extractClaim(token, claims -> (List<Long>) claims.get("role_ids"));
    }

    public Long extractAccountTypeId(String token) {
        return extractClaim(token, claims -> claims.get("account_type_id", Long.class));
    }

    public String extractUsernameFromToken(String token) {
        return extractClaim(token, claims -> claims.get("username", String.class));
    }
} 