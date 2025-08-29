package com.automo.config.security;

import com.automo.auth.entity.Auth;
import com.automo.auth.repository.AuthRepository;
import com.automo.authRoles.entity.AuthRoles;
import com.automo.authRoles.repository.AuthRolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthRepository authRepository;
    private final AuthRolesRepository authRolesRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscar usuário por email ou contato
        Auth auth = authRepository.findByEmailOrContact(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + username));

        // Buscar roles através de AuthRoles (Role será carregado automaticamente com FetchType.EAGER)
        List<AuthRoles> authRoles = authRolesRepository.findByAuthId(auth.getId());
        
        return new User(
                auth.getEmail(),
                auth.getPassword(),
                authRoles.stream()
                        .map(authRole -> new SimpleGrantedAuthority("ROLE_" + authRole.getRole().getRole()))
                        .collect(Collectors.toList())
        );
    }
} 