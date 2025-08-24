# Exemplos de Autorização de Endpoints - Automo Backend

Este documento fornece exemplos práticos e comentados de como implementar autorização de endpoints usando o sistema de `AuthRoles`.

## 🔐 **Sistema de Autorização Baseado em Roles**

### **1. Verificação Simples de Role Única**

```java
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management APIs")
@SecurityRequirement(name = SecurityConfig.SECURITY)
public class AdminController {

    private final AuthRolesRepository authRolesRepository;
    private final JwtUtils jwtUtils;

    @PostMapping("/system-settings")
    public ResponseEntity<String> updateSystemSettings(@RequestBody SystemSettingsDto dto) {
        // 🔒 VERIFICAÇÃO DE AUTORIZAÇÃO
        // Buscar o ID do usuário atual do token JWT
        Long currentUserId = jwtUtils.getCurrentUserId();
        
        // Verificar se o usuário tem a role ADMIN
        List<AuthRoles> userRoles = authRolesRepository.findByAuthId(currentUserId);
        boolean isAdmin = userRoles.stream()
                .anyMatch(authRole -> "ADMIN".equals(authRole.getRole().getRole()) 
                    && "ACTIVE".equals(authRole.getState().getState()));
        
        if (!isAdmin) {
            // ❌ ACESSO NEGADO - Usuário não é admin
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin role required.");
        }
        
        // ✅ ACESSO PERMITIDO - Usuário é admin
        // Executar lógica de negócio
        return ResponseEntity.ok("System settings updated successfully");
    }
}
```

### **2. Verificação de Múltiplas Roles (OR)**

```java
@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
public class ManagementController {

    private final AuthRolesRepository authRolesRepository;
    private final JwtUtils jwtUtils;

    @PostMapping("/team-management")
    public ResponseEntity<String> manageTeam(@RequestBody TeamManagementDto dto) {
        // 🔒 VERIFICAÇÃO DE AUTORIZAÇÃO - Múltiplas roles permitidas
        Long currentUserId = jwtUtils.getCurrentUserId();
        
        // Buscar roles do usuário
        List<AuthRoles> userRoles = authRolesRepository.findByAuthId(currentUserId);
        
        // Verificar se o usuário tem pelo menos UMA das roles necessárias
        // ADMIN OU MANAGER OU TEAM_LEAD
        boolean hasRequiredRole = userRoles.stream()
                .filter(authRole -> "ACTIVE".equals(authRole.getState().getState()))
                .anyMatch(authRole -> {
                    String roleName = authRole.getRole().getRole();
                    return "ADMIN".equals(roleName) || 
                           "MANAGER".equals(roleName) || 
                           "TEAM_LEAD".equals(roleName);
                });
        
        if (!hasRequiredRole) {
            // ❌ ACESSO NEGADO - Usuário não tem nenhuma das roles necessárias
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin, Manager, or Team Lead role required.");
        }
        
        // ✅ ACESSO PERMITIDO - Usuário tem pelo menos uma das roles necessárias
        return ResponseEntity.ok("Team management operation completed");
    }
}
```

### **3. Verificação de Múltiplas Roles (AND)**

```java
@RestController
@RequestMapping("/finance")
@RequiredArgsConstructor
public class FinanceController {

    private final AuthRolesRepository authRolesRepository;
    private final JwtUtils jwtUtils;

    @PostMapping("/approve-payment")
    public ResponseEntity<String> approvePayment(@RequestBody PaymentApprovalDto dto) {
        // 🔒 VERIFICAÇÃO DE AUTORIZAÇÃO - Todas as roles são necessárias
        Long currentUserId = jwtUtils.getCurrentUserId();
        
        // Buscar roles do usuário
        List<AuthRoles> userRoles = authRolesRepository.findByAuthId(currentUserId);
        
        // Verificar se o usuário tem TODAS as roles necessárias
        // FINANCE_MANAGER E PAYMENT_APPROVER
        Set<String> userRoleNames = userRoles.stream()
                .filter(authRole -> "ACTIVE".equals(authRole.getState().getState()))
                .map(authRole -> authRole.getRole().getRole())
                .collect(Collectors.toSet());
        
        boolean hasAllRequiredRoles = userRoleNames.contains("FINANCE_MANAGER") 
                && userRoleNames.contains("PAYMENT_APPROVER");
        
        if (!hasAllRequiredRoles) {
            // ❌ ACESSO NEGADO - Usuário não tem todas as roles necessárias
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Both Finance Manager and Payment Approver roles required.");
        }
        
        // ✅ ACESSO PERMITIDO - Usuário tem todas as roles necessárias
        return ResponseEntity.ok("Payment approved successfully");
    }
}
```

### **4. Verificação com Role Específica e Estado**

```java
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final AuthRolesRepository authRolesRepository;
    private final JwtUtils jwtUtils;

    @GetMapping("/sensitive-data")
    public ResponseEntity<String> getSensitiveReport() {
        // 🔒 VERIFICAÇÃO DE AUTORIZAÇÃO - Role específica com estado ativo
        Long currentUserId = jwtUtils.getCurrentUserId();
        
        // Buscar roles do usuário
        List<AuthRoles> userRoles = authRolesRepository.findByAuthId(currentUserId);
        
        // Verificar se o usuário tem a role específica e está ativa
        boolean hasSensitiveAccess = userRoles.stream()
                .anyMatch(authRole -> 
                    "SENSITIVE_DATA_ACCESS".equals(authRole.getRole().getRole()) 
                    && "ACTIVE".equals(authRole.getState().getState()));
        
        if (!hasSensitiveAccess) {
            // ❌ ACESSO NEGADO - Usuário não tem acesso a dados sensíveis
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Sensitive data access role required and must be active.");
        }
        
        // ✅ ACESSO PERMITIDO - Usuário tem acesso a dados sensíveis
        return ResponseEntity.ok("Sensitive report data retrieved");
    }
}
```

## 🛡️ **Anotações Customizadas para Autorização**

### **1. Anotação Customizada para Verificação de Role**

```java
// 📝 CRIAR ANOTAÇÃO PERSONALIZADA
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String[] value();        // Roles necessárias
    boolean allRequired() default false;  // true = todas as roles, false = pelo menos uma
}

// 📝 IMPLEMENTAR INTERCEPTOR/AOP
@Component
@Aspect
@RequiredArgsConstructor
public class RoleAuthorizationAspect {

    private final AuthRolesRepository authRolesRepository;
    private final JwtUtils jwtUtils;

    @Around("@annotation(requireRole)")
    public Object checkRoleAuthorization(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        // 🔒 VERIFICAÇÃO AUTOMÁTICA DE AUTORIZAÇÃO
        Long currentUserId = jwtUtils.getCurrentUserId();
        String[] requiredRoles = requireRole.value();
        boolean allRequired = requireRole.allRequired();
        
        // Buscar roles do usuário
        List<AuthRoles> userRoles = authRolesRepository.findByAuthId(currentUserId);
        Set<String> userRoleNames = userRoles.stream()
                .filter(authRole -> "ACTIVE".equals(authRole.getState().getState()))
                .map(authRole -> authRole.getRole().getRole())
                .collect(Collectors.toSet());
        
        boolean hasAccess;
        if (allRequired) {
            // Verificar se tem TODAS as roles
            hasAccess = Arrays.stream(requiredRoles)
                    .allMatch(userRoleNames::contains);
        } else {
            // Verificar se tem pelo menos UMA das roles
            hasAccess = Arrays.stream(requiredRoles)
                    .anyMatch(userRoleNames::contains);
        }
        
        if (!hasAccess) {
            // ❌ ACESSO NEGADO
            throw new AccessDeniedException("Insufficient permissions for this operation");
        }
        
        // ✅ ACESSO PERMITIDO - Continuar com a execução
        return joinPoint.proceed();
    }
}
```

### **2. Uso da Anotação Customizada**

```java
@RestController
@RequestMapping("/secure")
@RequiredArgsConstructor
public class SecureController {

    @PostMapping("/admin-only")
    @RequireRole(value = {"ADMIN"})
    public ResponseEntity<String> adminOnlyOperation() {
        // 🔒 AUTORIZAÇÃO AUTOMÁTICA - Apenas admins podem acessar
        // A verificação é feita automaticamente pelo AOP
        return ResponseEntity.ok("Admin operation completed");
    }

    @PostMapping("/manager-or-admin")
    @RequireRole(value = {"MANAGER", "ADMIN"})
    public ResponseEntity<String> managerOrAdminOperation() {
        // 🔒 AUTORIZAÇÃO AUTOMÁTICA - Managers OU Admins podem acessar
        return ResponseEntity.ok("Manager/Admin operation completed");
    }

    @PostMapping("/finance-team")
    @RequireRole(value = {"FINANCE_MANAGER", "ACCOUNTANT", "AUDITOR"}, allRequired = true)
    public ResponseEntity<String> financeTeamOperation() {
        // 🔒 AUTORIZAÇÃO AUTOMÁTICA - Todas as roles financeiras são necessárias
        return ResponseEntity.ok("Finance team operation completed");
    }
}
```

## 🔧 **Utilitários de Autorização**

### **1. Classe Utilitária para Verificações de Role**

```java
@Component
@RequiredArgsConstructor
public class RoleAuthorizationUtils {

    private final AuthRolesRepository authRolesRepository;
    private final JwtUtils jwtUtils;

    /**
     * 🔒 Verifica se o usuário atual tem uma role específica
     * @param requiredRole Nome da role necessária
     * @return true se o usuário tem a role, false caso contrário
     */
    public boolean hasRole(String requiredRole) {
        Long currentUserId = jwtUtils.getCurrentUserId();
        List<AuthRoles> userRoles = authRolesRepository.findByAuthId(currentUserId);
        
        return userRoles.stream()
                .anyMatch(authRole -> 
                    requiredRole.equals(authRole.getRole().getRole()) 
                    && "ACTIVE".equals(authRole.getState().getState()));
    }

    /**
     * 🔒 Verifica se o usuário atual tem pelo menos uma das roles especificadas
     * @param requiredRoles Array de roles necessárias
     * @return true se o usuário tem pelo menos uma role, false caso contrário
     */
    public boolean hasAnyRole(String... requiredRoles) {
        Long currentUserId = jwtUtils.getCurrentUserId();
        List<AuthRoles> userRoles = authRolesRepository.findByAuthId(currentUserId);
        
        Set<String> userRoleNames = userRoles.stream()
                .filter(authRole -> "ACTIVE".equals(authRole.getState().getState()))
                .map(authRole -> authRole.getRole().getRole())
                .collect(Collectors.toSet());
        
        return Arrays.stream(requiredRoles)
                .anyMatch(userRoleNames::contains);
    }

    /**
     * 🔒 Verifica se o usuário atual tem todas as roles especificadas
     * @param requiredRoles Array de roles necessárias
     * @return true se o usuário tem todas as roles, false caso contrário
     */
    public boolean hasAllRoles(String... requiredRoles) {
        Long currentUserId = jwtUtils.getCurrentUserId();
        List<AuthRoles> userRoles = authRolesRepository.findByAuthId(currentUserId);
        
        Set<String> userRoleNames = userRoles.stream()
                .filter(authRole -> "ACTIVE".equals(authRole.getState().getState()))
                .map(authRole -> authRole.getRole().getRole())
                .collect(Collectors.toSet());
        
        return Arrays.stream(requiredRoles)
                .allMatch(userRoleNames::contains);
    }

    /**
     * 🔒 Verifica se o usuário atual tem uma role específica com estado ativo
     * @param requiredRole Nome da role necessária
     * @param requiredState Estado necessário (ex: "ACTIVE", "PENDING")
     * @return true se o usuário tem a role com o estado especificado
     */
    public boolean hasRoleWithState(String requiredRole, String requiredState) {
        Long currentUserId = jwtUtils.getCurrentUserId();
        List<AuthRoles> userRoles = authRolesRepository.findByAuthId(currentUserId);
        
        return userRoles.stream()
                .anyMatch(authRole -> 
                    requiredRole.equals(authRole.getRole().getRole()) 
                    && requiredState.equals(authRole.getState().getState()));
    }

    /**
     * 🔒 Obtém todas as roles ativas do usuário atual
     * @return Lista de nomes das roles ativas
     */
    public List<String> getCurrentUserRoles() {
        Long currentUserId = jwtUtils.getCurrentUserId();
        List<AuthRoles> userRoles = authRolesRepository.findByAuthId(currentUserId);
        
        return userRoles.stream()
                .filter(authRole -> "ACTIVE".equals(authRole.getState().getState()))
                .map(authRole -> authRole.getRole().getRole())
                .collect(Collectors.toList());
    }

    /**
     * 🔒 Verifica se o usuário atual é admin
     * @return true se o usuário é admin, false caso contrário
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * 🔒 Verifica se o usuário atual é manager
     * @return true se o usuário é manager, false caso contrário
     */
    public boolean isManager() {
        return hasRole("MANAGER");
    }

    /**
     * 🔒 Verifica se o usuário atual tem permissões de back office
     * @return true se o usuário tem permissões de back office
     */
    public boolean hasBackOfficeAccess() {
        return hasAnyRole("ADMIN", "MANAGER", "BACK_OFFICE_USER");
    }
}
```

### **2. Uso dos Utilitários de Autorização**

```java
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {

    private final RoleAuthorizationUtils roleUtils;

    @GetMapping("/admin-data")
    public ResponseEntity<String> getAdminData() {
        // 🔒 VERIFICAÇÃO SIMPLES - Usar utilitário
        if (!roleUtils.isAdmin()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Admin role required.");
        }
        
        // ✅ ACESSO PERMITIDO
        return ResponseEntity.ok("Admin data retrieved");
    }

    @PostMapping("/manager-operation")
    public ResponseEntity<String> performManagerOperation() {
        // 🔒 VERIFICAÇÃO DE MÚLTIPLAS ROLES - Usar utilitário
        if (!roleUtils.hasAnyRole("MANAGER", "ADMIN", "TEAM_LEAD")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Manager, Admin, or Team Lead role required.");
        }
        
        // ✅ ACESSO PERMITIDO
        return ResponseEntity.ok("Manager operation completed");
    }

    @DeleteMapping("/sensitive-resource")
    public ResponseEntity<String> deleteSensitiveResource() {
        // 🔒 VERIFICAÇÃO DE TODAS AS ROLES - Usar utilitário
        if (!roleUtils.hasAllRoles("ADMIN", "SECURITY_OFFICER")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Both Admin and Security Officer roles required.");
        }
        
        // ✅ ACESSO PERMITIDO
        return ResponseEntity.ok("Sensitive resource deleted");
    }

    @GetMapping("/back-office")
    public ResponseEntity<String> getBackOfficeData() {
        // 🔒 VERIFICAÇÃO DE PERMISSÕES ESPECÍFICAS - Usar utilitário
        if (!roleUtils.hasBackOfficeAccess()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied. Back office access required.");
        }
        
        // ✅ ACESSO PERMITIDO
        return ResponseEntity.ok("Back office data retrieved");
    }
}
```

## 🚨 **Tratamento de Erros de Autorização**

### **1. Exceção Customizada para Acesso Negado**

```java
// 📝 EXCEÇÃO PERSONALIZADA PARA AUTORIZAÇÃO
public class InsufficientPermissionsException extends RuntimeException {
    
    private final String requiredRole;
    private final String userRole;
    
    public InsufficientPermissionsException(String requiredRole, String userRole) {
        super(String.format("Insufficient permissions. Required: %s, User has: %s", requiredRole, userRole));
        this.requiredRole = requiredRole;
        this.userRole = userRole;
    }
    
    public String getRequiredRole() { return requiredRole; }
    public String getUserRole() { return userRole; }
}

// 📝 HANDLER GLOBAL PARA EXCEÇÕES DE AUTORIZAÇÃO
@ControllerAdvice
public class AuthorizationExceptionHandler {

    @ExceptionHandler(InsufficientPermissionsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientPermissions(
            InsufficientPermissionsException ex) {
        
        ErrorResponse error = new ErrorResponse(
            "FORBIDDEN",
            "Insufficient permissions for this operation",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex) {
        
        ErrorResponse error = new ErrorResponse(
            "FORBIDDEN",
            "Access denied",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }
}
```

### **2. Uso da Exceção Customizada**

```java
@RestController
@RequestMapping("/secure")
@RequiredArgsConstructor
public class SecureController {

    private final RoleAuthorizationUtils roleUtils;

    @PostMapping("/critical-operation")
    public ResponseEntity<String> performCriticalOperation() {
        // 🔒 VERIFICAÇÃO COM EXCEÇÃO PERSONALIZADA
        if (!roleUtils.hasAllRoles("ADMIN", "SECURITY_OFFICER", "AUDITOR")) {
            throw new InsufficientPermissionsException(
                "ADMIN + SECURITY_OFFICER + AUDITOR", 
                String.join(", ", roleUtils.getCurrentUserRoles())
            );
        }
        
        // ✅ ACESSO PERMITIDO
        return ResponseEntity.ok("Critical operation completed");
    }
}
```

## 📊 **Exemplos de Respostas de Autorização**

### **1. Resposta de Sucesso**
```json
{
    "status": "SUCCESS",
    "message": "Operation completed successfully",
    "data": "Result data here",
    "timestamp": "2024-01-15T10:30:00"
}
```

### **2. Resposta de Acesso Negado**
```json
{
    "status": "FORBIDDEN",
    "message": "Insufficient permissions for this operation",
    "details": "Required: ADMIN + SECURITY_OFFICER, User has: USER, MANAGER",
    "timestamp": "2024-01-15T10:30:00"
}
```

### **3. Resposta de Erro de Autenticação**
```json
{
    "status": "UNAUTHORIZED",
    "message": "Authentication required",
    "details": "Valid JWT token is required",
    "timestamp": "2024-01-15T10:30:00"
}
```

## 🔍 **Boas Práticas de Implementação**

### **1. Sempre Verificar Estados**
```java
// ✅ CORRETO - Verificar estado da role
boolean hasActiveRole = userRoles.stream()
    .anyMatch(authRole -> 
        "ADMIN".equals(authRole.getRole().getRole()) 
        && "ACTIVE".equals(authRole.getState().getState()));

// ❌ INCORRETO - Não verificar estado
boolean hasRole = userRoles.stream()
    .anyMatch(authRole -> "ADMIN".equals(authRole.getRole().getRole()));
```

### **2. Usar Constantes para Nomes de Roles**
```java
// ✅ CORRETO - Usar constantes
public static final String ROLE_ADMIN = "ADMIN";
public static final String ROLE_MANAGER = "MANAGER";

if (roleUtils.hasRole(ROLE_ADMIN)) {
    // Lógica aqui
}

// ❌ INCORRETO - Strings hardcoded
if (roleUtils.hasRole("ADMIN")) {
    // Lógica aqui
}
```

### **3. Implementar Cache para Roles Frequentes**
```java
// ✅ CORRETO - Cache de roles para performance
@Cacheable("user-roles")
public List<String> getUserRoles(Long userId) {
    List<AuthRoles> authRoles = authRolesRepository.findByAuthId(userId);
    return authRoles.stream()
        .filter(authRole -> "ACTIVE".equals(authRole.getState().getState()))
        .map(authRole -> authRole.getRole().getRole())
        .collect(Collectors.toList());
}
```

### **4. Logs de Auditoria para Operações Sensíveis**
```java
@PostMapping("/sensitive-operation")
public ResponseEntity<String> performSensitiveOperation() {
    // 🔒 VERIFICAÇÃO DE AUTORIZAÇÃO
    if (!roleUtils.isAdmin()) {
        log.warn("Unauthorized access attempt to sensitive operation by user: {}", 
                jwtUtils.getCurrentUserEmail());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Access denied. Admin role required.");
    }
    
    // ✅ ACESSO PERMITIDO - Log de auditoria
    log.info("Sensitive operation performed by admin user: {}", 
            jwtUtils.getCurrentUserEmail());
    
    return ResponseEntity.ok("Sensitive operation completed");
}
```

## 🎯 **Resumo dos Padrões de Autorização**

| Padrão | Descrição | Exemplo |
|--------|-----------|---------|
| **Role Única** | Verifica se o usuário tem uma role específica | `hasRole("ADMIN")` |
| **Múltiplas Roles (OR)** | Verifica se o usuário tem pelo menos uma das roles | `hasAnyRole("MANAGER", "ADMIN")` |
| **Múltiplas Roles (AND)** | Verifica se o usuário tem todas as roles | `hasAllRoles("FINANCE", "APPROVER")` |
| **Role com Estado** | Verifica role e estado específico | `hasRoleWithState("ADMIN", "ACTIVE")` |
| **Anotação Customizada** | Autorização automática via AOP | `@RequireRole({"ADMIN"})` |
| **Utilitários** | Métodos reutilizáveis para verificação | `roleUtils.isAdmin()` |

---

**Sistema de autorização implementado com sucesso!** 🎉

Agora você tem um sistema robusto e flexível para controlar o acesso aos endpoints baseado em roles e estados. 