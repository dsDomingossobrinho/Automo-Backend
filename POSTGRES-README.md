# Docker Compose PostgreSQL - Automo Backend

Este arquivo `docker-compose-postgres.yml` é baseado diretamente no `docker-compose-postgress.yml` do projeto afrikancoders-backend, adaptado para o projeto Automo.

## 🎯 **Objetivo**

Fornecer um ambiente Docker completo e idêntico ao afrikancoders-backend, incluindo:
- **PostgreSQL 15**: Banco de dados
- **PgAdmin 4**: Interface de administração
- **Aplicação Spring Boot**: Container da aplicação
- **Configurações**: Idênticas ao afrikancoders-backend

## 🚀 **Como Usar**

### **Iniciar Ambiente Completo**
```bash
# Iniciar todos os serviços
docker-compose -f docker-compose-postgres.yml up -d

# Ver status dos containers
docker-compose -f docker-compose-postgres.yml ps

# Ver logs em tempo real
docker-compose -f docker-compose-postgres.yml logs -f
```

### **Parar Ambiente**
```bash
# Parar todos os serviços
docker-compose -f docker-compose-postgres.yml down

# Parar e remover volumes (cuidado: perde dados)
docker-compose -f docker-compose-postgres.yml down -v
```

### **Reiniciar Serviços**
```bash
# Reiniciar apenas a aplicação
docker-compose -f docker-compose-postgres.yml restart app

# Reiniciar apenas o PostgreSQL
docker-compose -f docker-compose-postgres.yml restart postgres

# Reiniciar todos os serviços
docker-compose -f docker-compose-postgres.yml restart
```

## 📊 **Serviços Configurados**

### **1. PostgreSQL**
- **Imagem**: `postgres:15`
- **Container**: `automo-postgres`
- **Porta**: `5432:5432`
- **Database**: `automo_db`
- **Usuário**: `automo`
- **Senha**: `automo123`
- **Health Check**: Verificação automática a cada 10s

### **2. PgAdmin**
- **Imagem**: `dpage/pgadmin4`
- **Container**: `automo-pgadmin`
- **Porta**: `8082:80`
- **Email**: `admin@automo.com`
- **Senha**: `admin`
- **Restart**: `always`

### **3. Aplicação Spring Boot**
- **Build**: Contexto local com Dockerfile
- **Container**: `automo-app`
- **Porta**: `8081:8080`
- **Perfil**: `local`
- **DDL**: `update` (cria/atualiza tabelas automaticamente)

## 🌍 **Variáveis de Ambiente**

### **Banco de Dados**
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/automo_db
SPRING_DATASOURCE_USERNAME=automo
SPRING_DATASOURCE_PASSWORD=automo123
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
```

### **Email**
```bash
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=no-reply@automo.com
SPRING_MAIL_PASSWORD=your-email-password
SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
```

### **JWT**
```bash
APPLICATION_SECURITY_JWT_SECRET_KEY=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
APPLICATION_SECURITY_JWT_EXPIRATION=86400000
APPLICATION_SECURITY_JWT_REFRESH_TOKEN_EXPIRATION=604800000
```

### **SMS**
```bash
SMS_PROVIDER_ENABLED=false
SMS_PROVIDER_API_KEY=your-sms-api-key
SMS_PROVIDER_SENDER=AUTOMO
```

### **Admin**
```bash
DEFAULT_EMAIL_ADMIN=admin@automo.com
DEFAULT_PASSWORD_ADMIN=admin123
```

## 🔧 **Configurações Específicas**

### **Perfil Spring**
- **Ativo**: `local`
- **DDL**: `update` (desenvolvimento)
- **Dialect**: PostgreSQL

### **Health Checks**
- **PostgreSQL**: Verificação a cada 10s
- **Timeout**: 5s
- **Retries**: 5 tentativas

### **Volumes**
- **PostgreSQL**: `postgres_data:/var/lib/postgresql/data`
- **PgAdmin**: `pgadmin_data:/var/lib/pgadmin`

## 📱 **Acessos**

### **Aplicação**
- **URL**: http://localhost:8081
- **Swagger**: http://localhost:8081/swagger-ui/

### **PgAdmin**
- **URL**: http://localhost:8082
- **Email**: admin@automo.com
- **Senha**: admin

### **PostgreSQL**
- **Host**: localhost
- **Porta**: 5432
- **Database**: automo_db
- **Usuário**: automo
- **Senha**: automo123

## 🔄 **Comandos Úteis**

### **Logs Específicos**
```bash
# Logs da aplicação
docker-compose -f docker-compose-postgres.yml logs -f app

# Logs do PostgreSQL
docker-compose -f docker-compose-postgres.yml logs -f postgres

# Logs do PgAdmin
docker-compose -f docker-compose-postgres.yml logs -f pgadmin
```

### **Executar Comandos nos Containers**
```bash
# Acessar PostgreSQL
docker-compose -f docker-compose-postgres.yml exec postgres psql -U automo -d automo_db

# Acessar aplicação
docker-compose -f docker-compose-postgres.yml exec app bash

# Acessar PgAdmin
docker-compose -f docker-compose-postgres.yml exec pgadmin bash
```

### **Backup e Restore**
```bash
# Backup do banco
docker-compose -f docker-compose-postgres.yml exec postgres pg_dump -U automo automo_db > backup.sql

# Restore do banco
docker-compose -f docker-compose-postgres.yml exec -T postgres psql -U automo -d automo_db < backup.sql
```

## 🎯 **Comparação com Afrikancoders-Backend**

### **Idêntico**
- ✅ **Estrutura**: Mesma organização de serviços
- ✅ **Health Checks**: Mesma configuração
- ✅ **Volumes**: Mesma estratégia de persistência
- ✅ **Networking**: Mesma comunicação entre serviços
- ✅ **Environment**: Mesmas variáveis de ambiente

### **Adaptado para Automo**
- 🔄 **Nomes**: Containers específicos para Automo
- 🔄 **Database**: `automo_db` em vez de `afrikancoders`
- 🔄 **Usuários**: `automo` em vez de `afrikancoders`
- 🔄 **Portas**: Mantidas as mesmas para consistência
- 🔄 **Configurações**: Adaptadas para as necessidades do projeto

## 🚨 **Troubleshooting**

### **Problemas Comuns**

#### **Porta 5432 já em uso**
```bash
# Verificar se há outro PostgreSQL rodando
netstat -tulpn | grep :5432

# Parar serviços conflitantes
sudo systemctl stop postgresql
# ou
docker stop $(docker ps -q --filter "publish=5432")
```

#### **Container não inicia**
```bash
# Verificar logs
docker-compose -f docker-compose-postgres.yml logs postgres

# Verificar status
docker-compose -f docker-compose-postgres.yml ps

# Rebuild se necessário
docker-compose -f docker-compose-postgres.yml build --no-cache
```

#### **Aplicação não conecta ao banco**
```bash
# Verificar se PostgreSQL está rodando
docker-compose -f docker-compose-postgres.yml exec postgres pg_isready -U automo

# Verificar logs da aplicação
docker-compose -f docker-compose-postgres.yml logs app
```

## 📚 **Recursos Adicionais**

- **Docker Compose**: https://docs.docker.com/compose/
- **PostgreSQL**: https://www.postgresql.org/docs/
- **PgAdmin**: https://www.pgadmin.org/docs/
- **Spring Boot**: https://spring.io/projects/spring-boot

---

**Docker Compose PostgreSQL baseado no afrikancoders-backend implementado com sucesso!** 🎉

Este arquivo fornece um ambiente idêntico ao afrikancoders-backend, adaptado para o projeto Automo. 