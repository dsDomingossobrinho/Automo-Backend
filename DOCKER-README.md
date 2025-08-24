# Docker Setup - Automo Backend

Este projeto utiliza Docker Compose para gerenciar os serviços de desenvolvimento e produção, baseado na estrutura do afrikancoders-backend.

## 🚀 **Configurações Disponíveis**

### **1. Docker Compose Principal (`docker-compose.yml`)**
- **Uso**: Ambiente de produção padrão
- **Portas**: 
  - PostgreSQL: 5432
  - PgAdmin: 8082
  - Aplicação: 8081

### **2. Docker Compose PostgreSQL (`docker-compose-postgres.yml`)**
- **Uso**: Ambiente completo com PostgreSQL (baseado no afrikancoders-backend)
- **Portas**: 
  - PostgreSQL: 5432
  - PgAdmin: 8082
  - Aplicação: 8081
- **Perfil**: `local` (desenvolvimento local)
- **Properties**: Usa `application-local.properties`
- **Java**: Java 21 (LTS - Long Term Support)



### **3. Docker Compose Development (`docker-compose.dev.yml`)**
- **Uso**: Ambiente de desenvolvimento com hot-reload
- **Portas**: 
  - PostgreSQL: 5433 (evita conflitos)
  - PgAdmin: 8083 (evita conflitos)
  - Aplicação: 8081
  - Debug: 5005

### **4. Docker Compose Production (`docker-compose.prod.yml`)**
- **Uso**: Ambiente de produção com variáveis de ambiente
- **Portas**: Configuráveis via variáveis de ambiente

## 🔧 **Como Usar**

### **Desenvolvimento (Recomendado para desenvolvedores)**
```bash
# Iniciar ambiente de desenvolvimento
docker-compose -f docker-compose.dev.yml up -d

# Ver logs
docker-compose -f docker-compose.dev.yml logs -f

# Parar ambiente
docker-compose -f docker-compose.dev.yml down
```

### **PostgreSQL Completo (Baseado no Afrikancoders)**
```bash
# Iniciar ambiente completo com PostgreSQL
docker-compose -f docker-compose-postgres.yml up -d

# Ver logs
docker-compose -f docker-compose-postgres.yml logs -f

# Parar ambiente
docker-compose -f docker-compose-postgres.yml down
```

### **Produção Padrão**
```bash
# Iniciar ambiente de produção
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar ambiente
docker-compose down
```

### **Produção com Variáveis de Ambiente**
```bash
# Copiar arquivo de exemplo
cp env.example .env

# Editar variáveis no .env
nano .env

# Iniciar com variáveis personalizadas
docker-compose -f docker-compose.prod.yml --env-file .env up -d
```

## 📊 **Serviços Disponíveis**

### **PostgreSQL**
- **Versão**: 15
- **Database**: `automo_db`
- **Usuário**: `automo`
- **Senha**: `automo123` (configurável em produção)
- **Health Check**: Verificação automática de disponibilidade

### **PgAdmin**
- **Interface**: Web para gerenciar PostgreSQL
- **Email**: `admin@automo.com` / `dev@automo.com`
- **Senha**: `admin` (configurável em produção)

### **Aplicação Spring Boot**
- **Porta**: 8080 (interna) / 8081 (externa)
- **Hot Reload**: Disponível em desenvolvimento
- **Debug**: Porta 5005 em desenvolvimento

## 🌍 **Variáveis de Ambiente**

### **Configurações do Banco**
```bash
POSTGRES_PASSWORD=your-secure-password
```

### **Configurações de Email**
```bash
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USERNAME=no-reply@automo.com
SMTP_PASSWORD=your-app-password
```

### **Configurações de JWT**
```bash
JWT_SECRET_KEY=your-very-long-and-secure-key
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000
```

### **Configurações de SMS**
```bash
SMS_ENABLED=false
SMS_API_KEY=your-sms-provider-api-key
SMS_SENDER=AUTOMO
```

## 🔄 **Comandos Úteis**

### **Gerenciamento de Volumes**
```bash
# Listar volumes
docker volume ls

# Remover volumes (cuidado: perde dados)
docker volume rm automo-backend_postgres_data
docker volume rm automo-backend_pgadmin_data
```

### **Logs e Debug**
```bash
# Logs da aplicação
docker-compose logs -f app

# Logs do PostgreSQL
docker-compose logs -f postgres

# Logs do PgAdmin
docker-compose logs -f pgadmin
```

### **Reiniciar Serviços**
```bash
# Reiniciar apenas a aplicação
docker-compose restart app

# Reiniciar todos os serviços
docker-compose restart
```

## 🚨 **Portas e Conflitos**

### **Portas Padrão**
- **5432**: PostgreSQL (produção)
- **5433**: PostgreSQL (desenvolvimento)
- **8081**: Aplicação Spring Boot
- **8082**: PgAdmin (produção)
- **8083**: PgAdmin (desenvolvimento)
- **5005**: Debug remoto (desenvolvimento)

### **Evitando Conflitos**
- **Desenvolvimento**: Usa portas 5433 e 8083
- **Produção**: Usa portas padrão 5432 e 8082
- **Aplicação**: Sempre na porta 8081 para consistência

## 📁 **Estrutura de Arquivos**

```
Automo-Backend/
├── docker-compose.yml              # Produção padrão
├── docker-compose-postgres.yml     # PostgreSQL completo (baseado no afrikancoders)
├── docker-compose.dev.yml          # Desenvolvimento
├── docker-compose.prod.yml         # Produção com variáveis
├── env.example                     # Exemplo de variáveis
├── Dockerfile                      # Imagem de produção
├── Dockerfile.dev                  # Imagem de desenvolvimento
└── DOCKER-README.md                # Este arquivo
```

## 🎯 **Diferenças do Afrikancoders-Backend**

### **Adaptações para Automo**
- **Nomes**: Containers e volumes específicos para Automo
- **Portas**: Configuração para evitar conflitos
- **Configurações**: Adaptadas para as necessidades do projeto
- **Estrutura**: Mantida a mesma organização e padrões

### **Funcionalidades Mantidas**
- **Health Checks**: Verificação automática de serviços
- **Volumes**: Persistência de dados
- **Networking**: Comunicação entre serviços
- **Environment**: Configuração flexível

## 🔍 **Troubleshooting**

### **Problemas Comuns**

#### **Erro de Compilação Java**
```bash
# Erro: "release version 24 not supported"
# Solução: O projeto foi configurado para Java 21 para maior compatibilidade
# Verificar versão no pom.xml: <java.version>21</java.version>
```

#### **Porta já em uso**
```bash
# Verificar portas em uso
netstat -tulpn | grep :5432
netstat -tulpn | grep :8081

# Parar serviços conflitantes
docker-compose down
```

#### **Volumes corrompidos**
```bash
# Remover e recriar volumes
docker-compose down -v
docker-compose up -d
```

#### **Aplicação não inicia**
```bash
# Verificar logs
docker-compose logs app

# Verificar dependências
docker-compose ps
```

## 📚 **Recursos Adicionais**

- **Documentação Docker**: https://docs.docker.com/
- **Docker Compose**: https://docs.docker.com/compose/
- **PostgreSQL**: https://www.postgresql.org/docs/
- **PgAdmin**: https://www.pgadmin.org/docs/

---

**Docker setup baseado no afrikancoders-backend implementado com sucesso!** 🎉

Agora você tem um ambiente Docker completo e flexível para desenvolvimento e produção. 