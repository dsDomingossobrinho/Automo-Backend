# Configuração de Segurança - Automo Backend

## Visão Geral

Este documento descreve a configuração de segurança implementada no sistema Automo Backend, que utiliza Spring Security com JWT para proteger as APIs.

## 🔐 Estratégia de Segurança

### **Princípio de Segurança**
- **Padrão**: Todas as rotas são **BLOQUEADAS** por padrão
- **Exceções**: Apenas rotas essenciais são **ABERTAS** para funcionamento básico
- **Autenticação**: JWT (JSON Web Token) obrigatório para acesso às APIs protegidas

## 🚪 Rotas Públicas (Sem Autenticação)

### **1. CORS Preflight Requests**
```
OPTIONS /** → PERMITIDO
```
- Necessário para requisições cross-origin
- Permite que o navegador verifique se a requisição é permitida

### **2. Autenticação e Registro**
```
POST /auth/register → PERMITIDO                    # Registro de usuários
POST /auth/login → PERMITIDO                       # Login direto com email/contact + senha
POST /auth/login/request-otp → PERMITIDO          # Solicitar código OTP
POST /auth/login/verify-otp → PERMITIDO           # Verificar código OTP
POST /auth/login/backoffice/request-otp → PERMITIDO # OTP para backoffice
POST /auth/login/backoffice/verify-otp → PERMITIDO  # Verificar OTP backoffice
POST /auth/login/user/request-otp → PERMITIDO     # OTP para usuários
POST /auth/login/user/verify-otp → PERMITIDO      # Verificar OTP usuários
```

### **3. Documentação Swagger/OpenAPI**
```
GET /swagger-ui/** → PERMITIDO                    # Interface Swagger UI
GET /v3/api-docs/** → PERMITIDO                   # Especificação OpenAPI
GET /swagger-ui.html → PERMITIDO                  # Página principal Swagger
GET /swagger-resources/** → PERMITIDO             # Recursos Swagger
GET /webjars/** → PERMITIDO                       # Dependências JavaScript/CSS
```

### **4. Monitoramento e Health Checks**
```
GET /actuator/health/** → PERMITIDO               # Status de saúde da aplicação
GET /actuator/info → PERMITIDO                    # Informações da aplicação
```

## 🔒 Rotas Protegidas (Com Autenticação)

### **Todas as outras rotas requerem token JWT válido**
```
GET /admin/** → AUTENTICADO                       # APIs de administradores
POST /admin/** → AUTENTICADO                      # Criação/atualização de admins
PUT /admin/** → AUTENTICADO                       # Atualização de admins
DELETE /admin/** → AUTENTICADO                    # Remoção de admins

GET /user/** → AUTENTICADO                        # APIs de usuários
POST /user/** → AUTENTICADO                       # Criação/atualização de usuários
PUT /user/** → AUTENTICADO                        # Atualização de usuários
DELETE /user/** → AUTENTICADO                     # Remoção de usuários

GET /auth/current-user → AUTENTICADO              # Informações do usuário atual
POST /auth/create → AUTENTICADO                   # Criação de entidades Auth

# E todas as outras APIs do sistema...
```

## 🛡️ Mecanismo de Proteção

### **1. Filtro JWT**
- **Localização**: `JwtAuthenticationFilter`
- **Função**: Intercepta todas as requisições HTTP
- **Processo**:
  1. Extrai token JWT do header `Authorization`
  2. Valida o token
  3. Cria contexto de autenticação
  4. Permite ou bloqueia o acesso

### **2. Validação de Token**
- **Verificação**: Assinatura, expiração, formato
- **Extração**: Email do usuário do token
- **Contexto**: Armazenado no `SecurityContextHolder`

### **3. Gerenciamento de Sessão**
- **Tipo**: `STATELESS` (sem sessão)
- **Razão**: APIs REST devem ser stateless
- **Benefício**: Escalabilidade e performance

## 🔑 Como Obter Acesso

### **1. Registro (Primeira vez)**
```http
POST /auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "contact": "+1234567890",
  "username": "username",
  "password": "password123"
}
```

### **2. Login Direto**
```http
POST /auth/login
Content-Type: application/json

{
  "emailOrContact": "user@example.com",
  "password": "password123"
}
```

### **3. Usar Token JWT**
```http
GET /admin/all
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 📋 Configuração Técnica

### **Arquivo**: `SecurityConfig.java`
```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())                    // Desabilitar CSRF
            .cors(cors -> cors.disable())                    // Desabilitar CORS
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Rotas públicas
                .requestMatchers("/auth/**").permitAll()     // Autenticação
                .requestMatchers("/swagger-ui/**").permitAll() // Swagger
                .requestMatchers("/actuator/health/**").permitAll() // Health
                
                // Todas as outras rotas protegidas
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

## 🚨 Cenários de Bloqueio

### **1. Token Ausente**
```http
GET /admin/all
# Resposta: 401 Unauthorized
# Erro: "Access Denied"
```

### **2. Token Inválido**
```http
GET /admin/all
Authorization: Bearer invalid-token
# Resposta: 401 Unauthorized
# Erro: "Invalid JWT token"
```

### **3. Token Expirado**
```http
GET /admin/all
Authorization: Bearer expired-token
# Resposta: 401 Unauthorized
# Erro: "JWT token has expired"
```

### **4. Rota Não Autorizada**
```http
GET /admin/all
Authorization: Bearer valid-token
# Resposta: 403 Forbidden (se usuário não tiver permissão)
```

## 🔍 Testando a Segurança

### **1. Swagger UI**
- Acesse: `http://localhost:8080/swagger-ui.html`
- Todas as rotas protegidas mostrarão o ícone de cadeado 🔒
- Clique no cadeado para inserir o token JWT

### **2. Teste com Postman/Insomnia**
```http
# 1. Faça login para obter token
POST /auth/login
{
  "emailOrContact": "admin@automo.com",
  "password": "admin123"
}

# 2. Use o token retornado
GET /admin/all
Authorization: Bearer {token_aqui}
```

### **3. Teste de Rotas Bloqueadas**
```bash
# Tentar acessar rota protegida sem token
curl -X GET http://localhost:8080/admin/all
# Deve retornar 401 Unauthorized

# Tentar acessar rota protegida com token inválido
curl -X GET http://localhost:8080/admin/all \
  -H "Authorization: Bearer invalid-token"
# Deve retornar 401 Unauthorized
```

## 📊 Resumo de Segurança

| Categoria | Status | Autenticação | Descrição |
|-----------|--------|---------------|-----------|
| **CORS** | ✅ Público | Não | Preflight requests |
| **Auth** | ✅ Público | Não | Login, registro, OTP |
| **Swagger** | ✅ Público | Não | Documentação da API |
| **Health** | ✅ Público | Não | Monitoramento |
| **Todas as outras** | 🔒 Protegido | **SIM** | Requer JWT válido |

## 🚀 Próximos Passos

### **1. Implementar Controle de Acesso por Role**
- Adicionar `@PreAuthorize` nos controllers
- Verificar roles do usuário antes de permitir acesso

### **2. Configurar CORS Adequadamente**
- Definir origens permitidas
- Configurar métodos HTTP permitidos
- Configurar headers permitidos

### **3. Implementar Rate Limiting**
- Limitar tentativas de login
- Proteger contra ataques de força bruta

### **4. Logs de Segurança**
- Registrar tentativas de acesso
- Monitorar atividades suspeitas
- Alertas de segurança

## 📞 Suporte

Para dúvidas sobre segurança ou configuração:
- **Email**: security@automo.com
- **Documentação**: Este arquivo
- **Swagger**: `http://localhost:8080/swagger-ui.html`

---

**⚠️ IMPORTANTE**: Esta configuração garante que apenas usuários autenticados possam acessar as APIs do sistema. Mantenha os tokens JWT seguros e não os compartilhe.
