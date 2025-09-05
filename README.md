# Automo Backend

Sistema backend para gestão de negócios automotivos, desenvolvido com Spring Boot e Spring Security JWT.

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.5.4**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**
- **Swagger/OpenAPI 3**
- **Spring Boot Actuator**

## 🏗️ Estrutura do Projeto

```
src/main/java/com/automo/
├── config/security/          # Configurações de segurança
├── model/                    # Modelos base
├── auth/                     # Autenticação e autorização
├── state/                    # Estados do sistema
├── area/                     # Áreas de atuação
├── productCategory/          # Categorias de produtos
├── country/                  # Países
├── province/                 # Províncias/Estados
├── role/                     # Papéis de usuário
├── accountType/              # Tipos de conta
├── paymentType/              # Tipos de pagamento
├── organizationType/         # Tipos de organização
├── identifierType/           # Tipos de identificação
├── notificationType/         # Tipos de notificação
├── leadType/                 # Tipos de lead
├── subscriptionPlan/         # Planos de assinatura
├── product/                  # Produtos
├── lead/                     # Leads
├── deal/                     # Negócios
├── promotion/                # Promoções
├── subscription/             # Assinaturas
├── agent/                    # Agentes
├── payment/                  # Pagamentos
├── dealProduct/              # Produtos de negócio
├── agentProduct/             # Produtos de agente
├── admin/                    # Administradores
├── user/                     # Usuários
├── identifier/               # Identificadores
├── notification/             # Notificações
├── associatedEmail/          # Emails associados
└── associatedContact/        # Contatos associados
```

## 🔐 Sistema de Autenticação JWT

### Estrutura do Token

O token JWT contém as seguintes informações do usuário:

```json
{
  "sub": "user@email.com",
  "id": 123,
  "contact": "+55 11 99999-9999",
  "email": "user@email.com",
  "role_id": 2,
  "role_ids": [2, 3],
  "account_type_id": 2,
  "username": "username",
  "iat": 1234567890,
  "exp": 1234567890
}
```

### 🔑 Sistema de Roles (Muitos para Muitos)

O sistema agora suporta **múltiplas roles por usuário** através de uma relação muitos para muitos:

#### **Tabela de Relacionamento:**
- **`auth_roles`**: Tabela intermediária que conecta `auth` e `roles`
- **`auth_id`**: Referência ao usuário
- **`role_id`**: Referência à role

#### **Roles Disponíveis:**
- **`role_id = 1`**: ADMIN - Administrador do sistema
- **`role_id = 2`**: USER - Usuário padrão do sistema
- **`role_id = 3`**: AGENT - Agente de vendas
- **`role_id = 4`**: MANAGER - Gerente de equipe

#### **Exemplos de Usuários:**
- **Admin**: Roles [ADMIN, USER] - Acesso total ao sistema
- **Usuário**: Role [USER] - Acesso básico
- **Agente**: Roles [AGENT, USER] - Acesso de agente + usuário
- **Gerente**: Roles [MANAGER, USER] - Acesso de gerente + usuário

### Tipos de Conta

- **`account_type_id = 1`**: Back Office (INDIVIDUAL)
- **`account_type_id = 2`**: Usuários Corporativos (CORPORATE)

### Endpoints de Autenticação

#### **Login Direto**
- **`POST /auth/login`**: Autenticação direta com email/username/contato e senha

#### **Login com OTP**
- **`POST /auth/login/request-otp`**: Solicitar código OTP para autenticação geral
- **`POST /auth/login/verify-otp`**: Verificar código OTP e autenticar
- **`POST /auth/login/resend-otp`**: Reenviar código OTP para autenticação geral

#### **Login Back Office com OTP**
- **`POST /auth/login/backoffice/request-otp`**: Solicitar código OTP para Back Office
- **`POST /auth/login/backoffice/verify-otp`**: Verificar OTP e autenticar Back Office
- **`POST /auth/login/backoffice/resend-otp`**: Reenviar código OTP para Back Office

#### **Login Usuário Corporativo com OTP**
- **`POST /auth/login/user/request-otp`**: Solicitar código OTP para usuários corporativos
- **`POST /auth/login/user/verify-otp`**: Verificar OTP e autenticar usuário corporativo
- **`POST /auth/login/user/resend-otp`**: Reenviar código OTP para usuários corporativos

#### **Recuperação de Senha**
- **`POST /auth/forgot-password`**: Solicitar código OTP para recuperação de senha
- **`POST /auth/reset-password`**: Verificar OTP e alterar senha

#### **Outros**
- **`POST /auth/register`**: Registro de usuários
- **`GET /auth/me`**: Informações do usuário atual (requer autenticação)

## 🔐 Sistema OTP (One-Time Password)

### Características do Sistema OTP

- **Detecção Automática**: O sistema detecta automaticamente se o contato fornecido é email ou telefone
- **Envio por Email**: Códigos OTP são enviados via Gmail SMTP para emails
- **Envio por SMS**: Configurado para envio por SMS (atualmente desabilitado)
- **Múltiplos Contextos**: Suporte para diferentes tipos de OTP (LOGIN, LOGIN_BACKOFFICE, LOGIN_USER, PASSWORD_RESET)
- **Expiração**: Códigos OTP possuem tempo de expiração configurável
- **Reenvio**: Funcionalidade de reenvio de código OTP

### Como Funciona

1. **Solicitação de OTP**: Usuário fornece email/contato e senha
2. **Validação**: Sistema valida credenciais e tipo de conta
3. **Geração**: Código OTP é gerado e enviado por email ou SMS
4. **Verificação**: Usuário insere código OTP para completar autenticação
5. **Token JWT**: Sistema retorna token JWT válido após verificação

### Exemplos de Uso

#### 1. Solicitar OTP para Login Geral
```bash
curl -X POST http://localhost:8080/auth/login/request-otp \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "user@example.com",
    "password": "senha123"
  }'
```

#### 2. Verificar OTP e Fazer Login
```bash
curl -X POST http://localhost:8080/auth/login/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "contact": "user@example.com",
    "otpCode": "123456"
  }'
```

#### 3. Reenviar OTP
```bash
curl -X POST http://localhost:8080/auth/login/resend-otp \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "user@example.com"
  }'
```

#### 4. Recuperação de Senha
```bash
# Solicitar código de recuperação
curl -X POST http://localhost:8080/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "user@example.com"
  }'

# Alterar senha com código OTP
curl -X POST http://localhost:8080/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "user@example.com",
    "otpCode": "123456",
    "newPassword": "novaSenha123"
  }'
```

## 🛠️ Como Usar o JWT Utils

### 1. Injetar JwtUtils no Controller

```java
@RestController
public class ExampleController {
    
    private final JwtUtils jwtUtils;
    
    public ExampleController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }
}
```

### 2. Acessar Dados do Usuário Atual

```java
@GetMapping("/user-info")
public ResponseEntity<UserInfo> getUserInfo() {
    Long userId = jwtUtils.getCurrentUserId();
    String email = jwtUtils.getCurrentUserEmail();
    String contact = jwtUtils.getCurrentUserContact();
    
    // Role principal (primeira role)
    Long primaryRoleId = jwtUtils.getCurrentUserRoleId();
    
    // Todas as roles do usuário
    List<Long> allRoleIds = jwtUtils.getCurrentUserRoleIds();
    
    Long accountTypeId = jwtUtils.getCurrentUserAccountTypeId();
    
    // ... usar os dados
}
```

### 3. Verificar Permissões por Role

```java
@PostMapping("/admin-only")
public ResponseEntity<String> adminOnlyEndpoint() {
    if (!jwtUtils.isCurrentUserAdmin()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    // Apenas administradores podem acessar
    return ResponseEntity.ok("Admin access granted");
}

@PostMapping("/agent-only")
public ResponseEntity<String> agentOnlyEndpoint() {
    if (!jwtUtils.isCurrentUserAgent()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    // Apenas agentes podem acessar
    return ResponseEntity.ok("Agent access granted");
}

@PostMapping("/manager-only")
public ResponseEntity<String> managerOnlyEndpoint() {
    if (!jwtUtils.isCurrentUserManager()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    // Apenas gerentes podem acessar
    return ResponseEntity.ok("Manager access granted");
}
```

### 4. Verificar Múltiplas Roles

```java
@PostMapping("/admin-or-manager")
public ResponseEntity<String> adminOrManagerEndpoint() {
    List<Long> allowedRoles = List.of(1L, 4L); // ADMIN ou MANAGER
    
    if (!jwtUtils.hasCurrentUserAnyRole(allowedRoles)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    // Apenas administradores ou gerentes podem acessar
    return ResponseEntity.ok("Admin or Manager access granted");
}

@PostMapping("/specific-role")
public ResponseEntity<String> specificRoleEndpoint() {
    if (!jwtUtils.hasCurrentUserRole(2L)) { // Role ID = 2 (USER)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    // Apenas usuários com role específica podem acessar
    return ResponseEntity.ok("Role access granted");
}
```

### 5. Verificar Tipos de Conta

```java
@PostMapping("/backoffice-only")
public ResponseEntity<String> backOfficeOnlyEndpoint() {
    if (!jwtUtils.isCurrentUserBackOffice()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    // Apenas usuários Back Office podem acessar
    return ResponseEntity.ok("Back Office access granted");
}

@PostMapping("/corporate-only")
public ResponseEntity<String> corporateOnlyEndpoint() {
    if (!jwtUtils.isCurrentUserCorporate()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    // Apenas usuários corporativos podem acessar
    return ResponseEntity.ok("Corporate access granted");
}
```

## 📊 Exemplo de Uso em Controller

```java
@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ExampleController {

    private final JwtUtils jwtUtils;
    private final ExampleService exampleService;

    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint() {
        // Acessar dados do usuário atual
        Long userId = jwtUtils.getCurrentUserId();
        String userEmail = jwtUtils.getCurrentUserEmail();
        List<Long> userRoles = jwtUtils.getCurrentUserRoleIds();
        
        // Verificar permissões
        if (jwtUtils.isCurrentUserAdmin()) {
            return ResponseEntity.ok("Admin: " + userEmail + " - Roles: " + userRoles);
        } else if (jwtUtils.isCurrentUserBackOffice()) {
            return ResponseEntity.ok("Back Office: " + userEmail + " - Roles: " + userRoles);
        } else if (jwtUtils.isCurrentUserAgent()) {
            return ResponseEntity.ok("Agent: " + userEmail + " - Roles: " + userRoles);
        } else {
            return ResponseEntity.ok("Regular User: " + userEmail + " - Roles: " + userRoles);
        }
    }

    @PostMapping("/admin-action")
    public ResponseEntity<String> adminAction() {
        if (!jwtUtils.isCurrentUserAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied. Admin role required.");
        }
        
        return ResponseEntity.ok("Admin action executed successfully");
    }

    @PostMapping("/agent-action")
    public ResponseEntity<String> agentAction() {
        if (!jwtUtils.isCurrentUserAgent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied. Agent role required.");
        }
        
        return ResponseEntity.ok("Agent action executed successfully");
    }

    @PostMapping("/multi-role-action")
    public ResponseEntity<String> multiRoleAction() {
        // Verificar se o usuário tem pelo menos uma das roles necessárias
        List<Long> requiredRoles = List.of(1L, 3L, 4L); // ADMIN, AGENT, ou MANAGER
        
        if (!jwtUtils.hasCurrentUserAnyRole(requiredRoles)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied. Admin, Agent, or Manager role required.");
        }
        
        return ResponseEntity.ok("Multi-role action executed successfully");
    }
}
```

## 🔧 Configuração

### Propriedades JWT

```properties
# JWT Configuration
application.security.jwt.secret-key=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
application.security.jwt.expiration=14400000
application.security.jwt.refresh-token.expiration=604800000
```

### Banco de Dados

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/automo_db
spring.datasource.username=automo
spring.datasource.password=automo123
spring.jpa.hibernate.ddl-auto=update
```

### Configuração de Email (OTP)

```properties
# Email Configuration for OTP
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=seu-email@gmail.com
spring.mail.password=sua-senha-app-gmail
spring.mail.properties.mail.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Admin Configuration
admin.email=admin@automo.com
admin.default.password=admin123
```

### Configuração de SMS (Opcional)

```properties
# SMS Configuration (currently disabled)
sms.enabled=false
sms.api.key=your-sms-api-key
```

## 🚀 Execução

### 1. Banco de Dados

```bash
docker-compose up -d
```

### 2. Aplicação

```bash
mvn spring-boot:run
```

### 3. Acessar

- **API**: http://localhost:8080
- **Swagger**: http://localhost:8080/swagger-ui/
- **Health Check**: http://localhost:8080/actuator/health

## 📝 Exemplo de Login

### 1. Back Office (Admin com múltiplas roles)

```bash
curl -X POST http://localhost:8080/auth/login/backoffice \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "admin@automo.com",
    "password": "admin123"
  }'
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userInfo": {
    "id": 1,
    "email": "admin@automo.com",
    "primaryRoleId": 1,
    "allRoleIds": [1, 2],
    "accountTypeId": 1,
    "isAdmin": true,
    "isBackOffice": true
  }
}
```

### 2. Usuário Corporativo (Role única)

```bash
curl -X POST http://localhost:8080/auth/login/user \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "user@automo.com",
    "password": "user123"
  }'
```

### 3. Agente (Múltiplas roles)

```bash
curl -X POST http://localhost:8080/auth/login/user \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "agent@automo.com",
    "password": "agent123"
  }'
```

### 4. Usar Token

```bash
curl -X GET http://localhost:8080/auth/me \
  -H "Authorization: Bearer SEU_TOKEN_JWT_AQUI"
```

## 🔒 Segurança

- **JWT Expiration**: 4 horas
- **Password Encoding**: BCrypt
- **CORS**: Desabilitado para desenvolvimento
- **CSRF**: Desabilitado (JWT stateless)
- **Endpoints Públicos**: Apenas autenticação e documentação
- **Múltiplas Roles**: Suporte completo com verificação de permissões

## 📚 Documentação da API

Acesse o Swagger UI em `/swagger-ui/` para documentação interativa da API.

## 🧪 Testes

```bash
mvn test
```

## 📦 Build

```bash
mvn clean package
```

## 🐳 Docker

```bash
docker build -t automo-backend .
docker run -p 8080:8080 automo-backend
```

---

**Desenvolvido com ❤️ para o sistema Automo**
