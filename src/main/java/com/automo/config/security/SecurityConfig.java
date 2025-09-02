package com.automo.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SecurityScheme(name = SecurityConfig.SECURITY, type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SecurityConfig {

    public static final String SECURITY = "bearerAuth";
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilitar CSRF para APIs REST
            .csrf(csrf -> csrf.disable())
            
            // Habilitar CORS com configuração customizada
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            
            // Configurar sessões como stateless (JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configurar autorização de rotas
            .authorizeHttpRequests(auth -> auth
                // ========================================
                // ROTAS PÚBLICAS (SEM AUTENTICAÇÃO)
                // ========================================
                
                // 1. CORS preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 2. Rotas de autenticação públicas
                .requestMatchers("/auth/login").permitAll()                      // Login direto
                .requestMatchers("/auth/login/request-otp").permitAll()          // Solicitar OTP
                .requestMatchers("/auth/login/verify-otp").permitAll()           // Verificar OTP
                .requestMatchers("/auth/login/backoffice/request-otp").permitAll() // OTP backoffice
                .requestMatchers("/auth/login/backoffice/verify-otp").permitAll()  // Verificar OTP backoffice
                .requestMatchers("/auth/login/user/request-otp").permitAll()      // OTP usuário
                .requestMatchers("/auth/login/user/verify-otp").permitAll()       // Verificar OTP usuário
                
                // 3. Documentação Swagger/OpenAPI (TODAS as rotas necessárias)
                .requestMatchers("/swagger-ui/**").permitAll()                   // Interface Swagger
                .requestMatchers("/v3/api-docs").permitAll()                     // Especificação OpenAPI (rota raiz)
                .requestMatchers("/v3/api-docs/**").permitAll()                  // Especificação OpenAPI (com path)
                .requestMatchers("/api-docs/**").permitAll()                     // Configuração Swagger
                .requestMatchers("/swagger-ui.html").permitAll()                 // Página principal Swagger
                .requestMatchers("/swagger-resources/**").permitAll()            // Recursos Swagger
                .requestMatchers("/webjars/**").permitAll()                      // Dependências JavaScript/CSS
                .requestMatchers("/swagger-config").permitAll()                  // Configuração específica
                .requestMatchers("/swagger-config/**").permitAll()               // Configuração específica com path
                .requestMatchers("/swagger-ui/oauth2-redirect.html").permitAll() // OAuth2 redirect
                .requestMatchers("/swagger-ui/swagger-initializer.js").permitAll() // Inicializador Swagger
                .requestMatchers("/error").permitAll()                           // Página de erro (necessária para Swagger)
                .requestMatchers("/favicon.ico").permitAll()                     // Favicon (necessário para Swagger)
                .requestMatchers("/swagger-ui.css").permitAll()                  // CSS principal do Swagger
                .requestMatchers("/swagger-ui-bundle.js").permitAll()            // Bundle JavaScript do Swagger
                .requestMatchers("/swagger-ui-standalone-preset.js").permitAll() // Preset standalone do Swagger
                .requestMatchers("/swagger-initializer.js").permitAll()          // Inicializador Swagger (rota raiz)
                .requestMatchers("/index.css").permitAll()                       // CSS adicional do Swagger
                .requestMatchers("/favicon-32x32.png").permitAll()               // Favicon PNG do Swagger
                .requestMatchers("/swagger-ui-bundle.js.map").permitAll()        // Source map do bundle
                .requestMatchers("/swagger-ui-standalone-preset.js.map").permitAll() // Source map do preset
                .requestMatchers("/swagger-ui/index.css").permitAll()            // CSS index do Swagger
                .requestMatchers("/swagger-ui/swagger-ui.css").permitAll()       // CSS principal do Swagger
                .requestMatchers("/swagger-ui/swagger-ui-bundle.js").permitAll() // Bundle JavaScript do Swagger
                .requestMatchers("/swagger-ui/swagger-ui-standalone-preset.js").permitAll() // Preset standalone do Swagger
                .requestMatchers("/swagger-ui/swagger-initializer.js").permitAll() // Inicializador Swagger
                .requestMatchers("/swagger-ui/favicon-32x32.png").permitAll()    // Favicon PNG do Swagger
                
                // 4. Health checks e métricas (monitoramento)
                .requestMatchers("/actuator/health/**").permitAll()              // Status de saúde
                .requestMatchers("/actuator/info").permitAll()                   // Informações da aplicação
                
                // ========================================
                // ROTAS PROTEGIDAS (COM AUTENTICAÇÃO)
                // ========================================
                
                // TODAS as outras rotas requerem token JWT válido
                .anyRequest().authenticated()
            )
            
            // Configurar serviço de usuários
            .userDetailsService(userDetailsService)
            
            // Adicionar filtro JWT antes da autenticação padrão
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
