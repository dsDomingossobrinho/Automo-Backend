# Perfis de Propriedades - Automo Backend

Este documento explica os diferentes perfis de propriedades disponíveis no projeto Automo Backend.

## 🎯 **Perfis Disponíveis**

### **1. `application.properties` (Padrão)**
- **Uso**: Configurações base do projeto
- **Ativo**: Sempre carregado
- **Propósito**: Configurações comuns a todos os ambientes

### **2. `application-local.properties` (Local)**
- **Uso**: Desenvolvimento local com Docker
- **Ativo**: `spring.profiles.active=local`
- **Propósito**: Ambiente idêntico ao afrikancoders-backend
- **Docker**: Usado pelo `docker-compose-postgres.yml`

### **3. `application-dev.properties` (Desenvolvimento)**
- **Uso**: Desenvolvimento com hot-reload
- **Ativo**: `spring.profiles.active=dev`
- **Propósito**: Desenvolvimento com ferramentas avançadas
- **Docker**: Usado pelo `docker-compose.dev.yml`

### **4. `application-prod.properties` (Produção)**
- **Uso**: Ambiente de produção
- **Ativo**: `spring.profiles.active=prod`
- **Propósito**: Configurações seguras para produção
- **Docker**: Usado pelo `docker-compose.prod.yml`

## 🚀 **Como Usar**

### **Perfil Local (Recomendado para Docker)**
```bash
# Com Docker Compose PostgreSQL
docker-compose -f docker-compose-postgres.yml up -d

# Ou definir variável de ambiente
export SPRING_PROFILES_ACTIVE=local
java -jar automo-backend.jar
```

### **Perfil de Desenvolvimento**
```bash
# Com Docker Compose Development
docker-compose -f docker-compose.dev.yml up -d

# Ou definir variável de ambiente
export SPRING_PROFILES_ACTIVE=dev
java -jar automo-backend.jar
```

### **Perfil de Produção**
```bash
# Com Docker Compose Production
docker-compose -f docker-compose.prod.yml up -d

# Ou definir variável de ambiente
export SPRING_PROFILES_ACTIVE=prod
java -jar automo-backend.jar
```

## 📊 **Comparação dos Perfis**

| Configuração | Local | Dev | Prod |
|--------------|-------|-----|------|
| **DDL Auto** | `update` | `create-drop` | `validate` |
| **Show SQL** | `true` | `true` | `false` |
| **Logging** | `DEBUG` | `DEBUG` | `INFO` |
| **Actuator** | `*` | `*` | `health,info,metrics` |
| **Swagger** | ✅ | ✅ | ❌ |
| **Hot Reload** | ❌ | ✅ | ❌ |

## 🌍 **Variáveis de Ambiente**

### **Configurações do Banco**
```bash
# Local/Dev
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/automo_db
SPRING_DATASOURCE_USERNAME=automo
SPRING_DATASOURCE_PASSWORD=automo123

# Produção
DATABASE_URL=jdbc:postgresql://your-prod-host:5432/automo_db
DATABASE_USERNAME=your-prod-user
DATABASE_PASSWORD=your-prod-password
```

### **Configurações de JWT**
```bash
# Local/Dev
APPLICATION_SECURITY_JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970

# Produção
JWT_SECRET_KEY=your-very-secure-production-key
```

### **Configurações de Email**
```bash
# Local/Dev
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_USERNAME=no-reply@automo.com
SPRING_MAIL_PASSWORD=your-app-password

# Produção
SMTP_HOST=smtp.gmail.com
SMTP_USERNAME=no-reply@automo.com
SMTP_PASSWORD=your-production-password
```

## 🔧 **Configurações Específicas**

### **Perfil Local**
- **Baseado no**: afrikancoders-backend
- **DDL**: `update` (mantém dados)
- **Logging**: DEBUG completo
- **Swagger**: Habilitado
- **Actuator**: Todos os endpoints

### **Perfil Dev**
- **DDL**: `create-drop` (recria tabelas)
- **Hot Reload**: Habilitado
- **Debug**: Porta 5005
- **Logging**: SQL detalhado
- **Swagger**: Habilitado

### **Perfil Prod**
- **DDL**: `validate` (não altera schema)
- **Logging**: Apenas INFO/WARN
- **Swagger**: Desabilitado
- **Actuator**: Endpoints limitados
- **Segurança**: Máxima

## 📁 **Estrutura de Arquivos**

```
Automo-Backend/src/main/resources/
├── application.properties              # Configurações base
├── application-local.properties       # Perfil local (Docker)
├── application-dev.properties         # Perfil desenvolvimento
├── application-prod.properties        # Perfil produção
└── application-local.properties.example # Exemplo para local
```

## 🎯 **Recomendações de Uso**

### **Desenvolvedores Locais**
```bash
# Usar perfil local para Docker
docker-compose -f docker-compose-postgres.yml up -d
```

### **Desenvolvedores com Hot Reload**
```bash
# Usar perfil dev para desenvolvimento
docker-compose -f docker-compose.dev.yml up -d
```

### **Produção**
```bash
# Usar perfil prod com variáveis de ambiente
docker-compose -f docker-compose.prod.yml --env-file .env up -d
```

## 🚨 **Importante**

### **Segurança**
- **Local/Dev**: Configurações abertas para desenvolvimento
- **Produção**: Sempre usar variáveis de ambiente para senhas
- **JWT**: Chaves diferentes para cada ambiente

### **Banco de Dados**
- **Local**: `update` - mantém dados entre reinicializações
- **Dev**: `create-drop` - recria tabelas a cada inicialização
- **Prod**: `validate` - valida schema sem alterações

### **Logs**
- **Local/Dev**: DEBUG completo para troubleshooting
- **Produção**: Apenas INFO/WARN para performance

## 📚 **Recursos Adicionais**

- **Spring Boot Profiles**: https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles
- **Spring Boot Properties**: https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html
- **Docker Environment**: https://docs.docker.com/compose/environment-variables/

---

**Perfis de propriedades configurados com sucesso!** 🎉

Agora você tem configurações específicas para cada ambiente de desenvolvimento e produção. 