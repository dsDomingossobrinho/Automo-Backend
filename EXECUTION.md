# Instruções de Execução - Automo Backend

## Pré-requisitos

1. **Java 21** instalado e configurado
2. **Maven 3.6+** instalado
3. **Docker** instalado (opcional, para usar o banco containerizado)
4. **PostgreSQL 12+** instalado (se não usar Docker)

## Opção 1: Usando Docker (Recomendado para desenvolvimento)

### 1. Iniciar o banco de dados
```bash
# Na pasta raiz do projeto
docker-compose up -d postgres-dev
```

### 2. Verificar se o banco está rodando
```bash
docker ps
# Deve mostrar o container automo-postgres-dev rodando na porta 5433
```

### 3. Executar a aplicação
```bash
# Usando o perfil de desenvolvimento
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Opção 2: Usando PostgreSQL local

### 1. Criar o banco de dados
```sql
CREATE DATABASE automo_db_dev;
CREATE USER postgres WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE automo_db_dev TO postgres;
```

### 2. Configurar as credenciais
Editar o arquivo `src/main/resources/application-dev.properties` com suas credenciais locais.

### 3. Executar a aplicação
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Verificando a Execução

### 1. Verificar logs da aplicação
A aplicação deve mostrar logs indicando:
- Conexão com o banco estabelecida
- Tabelas criadas pelo Hibernate
- Dados iniciais inseridos
- Aplicação rodando na porta 8080

### 2. Testar endpoints
```bash
# Health check
curl http://localhost:8080/actuator/health

# Swagger UI
http://localhost:8080/swagger-ui/

# API Docs
http://localhost:8080/v3/api-docs
```

### 3. Testar autenticação
```bash
# Registrar usuário
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "contact": "+55 11 99999-9999",
    "username": "testuser",
    "password": "Senha123"
  }'

# Fazer login geral (com email)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "test@example.com",
    "password": "password123"
  }'

# Fazer login geral (com contato)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "+55 11 99999-9999",
    "password": "password123"
  }'

# Fazer login Back Office (tipo_conta_id = 1) - com email
curl -X POST http://localhost:8080/auth/login/backoffice \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "admin@automo.com",
    "password": "admin123"
  }'

# Fazer login Back Office (tipo_conta_id = 1) - com contato
curl -X POST http://localhost:8080/auth/login/backoffice \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "+55 11 99999-9999",
    "password": "admin123"
  }'

# Fazer login Usuário (tipo_conta_id = 2) - com email
curl -X POST http://localhost:8080/auth/login/user \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "user@automo.com",
    "password": "user123"
  }'

# Fazer login Usuário (tipo_conta_id = 2) - com contato
curl -X POST http://localhost:8080/auth/login/user \
  -H "Content-Type: application/json" \
  -d '{
    "emailOrContact": "+55 11 88888-8888",
    "password": "user123"
  }'
```

## Perfis Disponíveis

### Development (`dev`)
- Banco: `automo_db_dev`
- Porta: 5433 (Docker) ou 5432 (local)
- DDL: `create-drop` (recria tabelas a cada execução)
- Logs: DEBUG
- Swagger: Habilitado

### Production (`prod`)
- Banco: `automo_db`
- Porta: 5432
- DDL: `validate` (não altera estrutura)
- Logs: INFO
- Swagger: Desabilitado

### Default
- Banco: `automo_db`
- Porta: 5432
- DDL: `update`
- Logs: DEBUG

## Solução de Problemas

### Erro de conexão com banco
```bash
# Verificar se o PostgreSQL está rodando
docker ps | grep postgres

# Verificar logs do container
docker logs automo-postgres-dev

# Testar conexão
psql -h localhost -p 5433 -U postgres -d automo_db_dev
```

### Erro de porta em uso
```bash
# Verificar processos na porta 8080
netstat -tulpn | grep 8080

# Matar processo se necessário
kill -9 <PID>
```

### Erro de permissão
```bash
# Dar permissão de execução ao Maven wrapper
chmod +x mvnw
```

## Estrutura de Dados Inicial

O sistema cria automaticamente:
- Estados básicos (ACTIVE, INACTIVE, PENDING, etc.)
- Papéis de usuário (ADMIN, USER, AGENT, MANAGER)
- Tipos de conta, pagamento, organização
- Usuário administrador padrão (Back Office - tipo_conta_id = 1):
  - Email: admin@automo.com
  - Username: admin
  - Senha: admin123
- Usuário de exemplo (Usuário - tipo_conta_id = 2):
  - Email: user@automo.com
  - Username: user
  - Senha: user123

## Próximos Passos

1. **Implementar controllers** para todas as entidades
2. **Adicionar validações** de negócio
3. **Implementar testes** unitários e de integração
4. **Configurar CI/CD** com GitHub Actions
5. **Adicionar monitoramento** com Prometheus/Grafana
6. **Implementar cache** com Redis
7. **Adicionar documentação** da API com exemplos 