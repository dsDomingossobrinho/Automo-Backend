# Sistema de AuthRoles - Automo Backend

Este documento explica o sistema de gerenciamento de roles de usuário através da entidade `AuthRoles`, que substitui a relação direta muitos-para-muitos entre `Auth` e `Role`.

## 🔄 **Mudança de Arquitetura**

### **Antes (Relação Direta)**
```java
// Auth.java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(name = "auth_roles", ...)
private Set<Role> roles;

// Role.java  
@ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
private Set<Auth> auths;
```

### **Depois (Entidade Intermediária)**
```java
// AuthRoles.java
@Entity
@Table(name = "auth_roles")
public class AuthRoles extends AbstractModel {
    @ManyToOne private Auth auth;
    @ManyToOne private Role role;
    @ManyToOne private State state;
}
```

## 🏗️ **Estrutura da Entidade AuthRoles**

### **Campos**
- **`id`**: Identificador único (herdado de AbstractModel)
- **`auth`**: Referência à entidade Auth
- **`role`**: Referência à entidade Role  
- **`state`**: Estado da associação (ACTIVE, INACTIVE, etc.)
- **`createdAt`**: Data de criação (herdado de AbstractModel)
- **`updatedAt`**: Data de atualização (herdado de AbstractModel)

### **Relacionamentos**
- **`@ManyToOne`** com `Auth`
- **`@ManyToOne`** com `Role`
- **`@ManyToOne`** com `State`

## 📋 **Endpoints Disponíveis**

### **CRUD Básico**
```
POST   /auth-roles                    # Criar associação
GET    /auth-roles/{id}              # Buscar por ID
GET    /auth-roles                    # Listar todas
PUT    /auth-roles/{id}              # Atualizar
DELETE /auth-roles/{id}              # Deletar
```

### **Consultas Específicas**
```
GET /auth-roles/auth/{authId}        # Roles de um usuário
GET /auth-roles/role/{roleId}        # Usuários com uma role
GET /auth-roles/state/{stateId}      # Associações por estado
```

## 🔧 **Uso no Sistema**

### **1. Criação de Usuário com Role**
```java
// Criar Auth
Auth auth = new Auth();
auth.setEmail("user@example.com");
auth.setUsername("user");
auth.setPassword(encodedPassword);
auth.setAccountType(accountType);
auth.setState(activeState);
authRepository.save(auth);

// Criar associação AuthRoles
AuthRoles authRoles = new AuthRoles();
authRoles.setAuth(auth);
authRoles.setRole(userRole);
authRoles.setState(activeState);
authRolesRepository.save(authRoles);
```

### **2. Busca de Roles de um Usuário**
```java
List<AuthRoles> userRoles = authRolesRepository.findByAuthId(authId);
List<String> roleNames = userRoles.stream()
    .map(authRole -> authRole.getRole().getRole())
    .collect(Collectors.toList());
```

### **3. Verificação de Permissões**
```java
List<AuthRoles> userRoles = authRolesRepository.findByAuthId(authId);
boolean hasAdminRole = userRoles.stream()
    .anyMatch(authRole -> "ADMIN".equals(authRole.getRole().getRole()));
```

## 🔒 **Segurança e Validação**

### **Validações Implementadas**
- ✅ **Unicidade**: Um usuário não pode ter a mesma role duas vezes
- ✅ **Existência**: Verifica se Auth, Role e State existem
- ✅ **Estado**: Cada associação tem seu próprio estado
- ✅ **Transações**: Operações são transacionais

### **Prevenção de Duplicatas**
```java
if (authRolesRepository.existsByAuthIdAndRoleId(authId, roleId)) {
    throw new RuntimeException("Auth already has this role assigned");
}
```

## 📊 **Vantagens da Nova Arquitetura**

### **1. Flexibilidade**
- ✅ **Múltiplos estados** para a mesma associação
- ✅ **Histórico** de mudanças de role
- ✅ **Auditoria** de quando roles foram atribuídas/removidas

### **2. Performance**
- ✅ **Lazy loading** por padrão
- ✅ **Consultas específicas** por Auth, Role ou State
- ✅ **Índices otimizados** na tabela intermediária

### **3. Manutenibilidade**
- ✅ **Separação de responsabilidades**
- ✅ **Código mais limpo** e organizado
- ✅ **Facilita testes** unitários

## 🔄 **Migração de Dados**

### **Script de Migração (SQL)**
```sql
-- Criar nova tabela
CREATE TABLE auth_roles (
    id BIGINT PRIMARY KEY,
    auth_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    state_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (auth_id) REFERENCES auth(id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (state_id) REFERENCES states(id)
);

-- Migrar dados existentes
INSERT INTO auth_roles (id, auth_id, role_id, state_id, created_at, updated_at)
SELECT 
    nextval('auth_roles_seq'),
    ar.auth_id,
    ar.role_id,
    a.state_id,
    a.created_at,
    a.updated_at
FROM auth_roles_old ar
JOIN auth a ON ar.auth_id = a.id;

-- Remover tabela antiga
DROP TABLE auth_roles_old;
```

## 🧪 **Testes**

### **Teste de Criação**
```java
@Test
public void testCreateAuthRoles() {
    AuthRolesDto dto = new AuthRolesDto(1L, 2L, 1L);
    AuthRolesResponse response = authRolesService.createAuthRoles(dto);
    
    assertNotNull(response);
    assertEquals(1L, response.authId());
    assertEquals(2L, response.roleId());
}
```

### **Teste de Validação**
```java
@Test
public void testDuplicateAuthRoles() {
    // Criar primeira associação
    authRolesService.createAuthRoles(new AuthRolesDto(1L, 2L, 1L));
    
    // Tentar criar duplicata
    assertThrows(RuntimeException.class, () -> {
        authRolesService.createAuthRoles(new AuthRolesDto(1L, 2L, 1L));
    });
}
```

## 📚 **Exemplos de Uso**

### **1. Usuário com Múltiplas Roles**
```java
// Criar usuário
Auth user = createUser("user@example.com", "user123");

// Atribuir múltiplas roles
createAuthRole(user.getId(), adminRole.getId(), activeState.getId());
createAuthRole(user.getId(), userRole.getId(), activeState.getId());
createAuthRole(user.getId(), managerRole.getId(), activeState.getId());
```

### **2. Consulta de Usuários por Role**
```java
// Buscar todos os usuários admin
List<AuthRolesResponse> adminUsers = authRolesService.getAuthRolesByRoleId(adminRoleId);

// Filtrar apenas usuários ativos
List<AuthRolesResponse> activeAdmins = adminUsers.stream()
    .filter(response -> "ACTIVE".equals(response.stateName()))
    .collect(Collectors.toList());
```

### **3. Gerenciamento de Estados**
```java
// Desativar role de usuário
AuthRoles authRole = authRolesRepository.findById(authRoleId).orElseThrow();
authRole.setState(inactiveState);
authRolesRepository.save(authRole);

// Reativar role
authRole.setState(activeState);
authRolesRepository.save(authRole);
```

## 🚨 **Considerações Importantes**

### **1. Performance**
- ⚠️ **Evite consultas N+1** ao buscar roles
- ✅ **Use JOINs** quando necessário
- ✅ **Considere cache** para roles frequentemente acessadas

### **2. Consistência**
- ✅ **Sempre verifique** se Auth, Role e State existem
- ✅ **Use transações** para operações complexas
- ✅ **Valide estados** antes de operações críticas

### **3. Segurança**
- ✅ **Verifique permissões** antes de modificar roles
- ✅ **Audite mudanças** de roles importantes
- ✅ **Valide inputs** para prevenir SQL injection

## 🔍 **Troubleshooting**

### **Problema: Role não encontrada**
```java
// Verificar se a role existe
Role role = roleRepository.findById(roleId)
    .orElseThrow(() -> new EntityNotFoundException("Role not found"));

// Verificar se a associação existe
List<AuthRoles> userRoles = authRolesRepository.findByAuthId(authId);
```

### **Problema: Usuário sem roles**
```java
// Verificar se o usuário tem roles
List<AuthRoles> userRoles = authRolesRepository.findByAuthId(authId);
if (userRoles.isEmpty()) {
    throw new RuntimeException("User has no roles assigned");
}
```

### **Problema: Estado inválido**
```java
// Verificar se o estado é válido
State state = stateRepository.findById(stateId)
    .orElseThrow(() -> new EntityNotFoundException("State not found"));

// Verificar se o estado é ativo
if (!"ACTIVE".equals(state.getState())) {
    throw new RuntimeException("Invalid state for role assignment");
}
```

## 📈 **Próximos Passos**

### **1. Implementações Futuras**
- 🔮 **Sistema de auditoria** para mudanças de role
- 🔮 **Workflow de aprovação** para roles sensíveis
- 🔮 **Expiração automática** de roles temporárias
- 🔮 **Notificações** quando roles são alteradas

### **2. Melhorias de Performance**
- 🔮 **Cache Redis** para roles frequentemente acessadas
- 🔮 **Índices compostos** para consultas complexas
- 🔮 **Paginação** para listas grandes de usuários

### **3. Funcionalidades Adicionais**
- 🔮 **Bulk operations** para múltiplas atribuições
- 🔮 **Templates de role** para grupos de usuários
- 🔮 **Relatórios** de distribuição de roles
- 🔮 **Import/Export** de configurações de role

---

**Sistema AuthRoles implementado com sucesso!** 🎉

A nova arquitetura oferece maior flexibilidade, melhor performance e facilita a manutenção do sistema de permissões. 