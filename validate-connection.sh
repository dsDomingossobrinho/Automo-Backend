#!/bin/bash

# Script de validação da conexão ao banco de dados - Automo Backend
# Baseado no afrikancoders-backend

echo "🔍 Validando configuração da conexão ao banco de dados..."

# Verificar se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Inicie o Docker Desktop primeiro."
    exit 1
fi

# Verificar se os containers estão rodando
echo "📊 Verificando status dos containers..."
docker-compose -f docker-compose-postgres.yml ps

echo ""
echo "🔍 Verificando logs do PostgreSQL..."
docker-compose -f docker-compose-postgres.yml logs postgres | tail -20

echo ""
echo "🔍 Verificando logs da aplicação..."
docker-compose -f docker-compose-postgres.yml logs app | tail -20

echo ""
echo "🌐 Testando conexão ao banco..."
echo "   - Host: postgres (container) / localhost (host)"
echo "   - Porta: 5432"
echo "   - Database: automo_db"
echo "   - Usuário: automo"
echo "   - Senha: automo123"

echo ""
echo "📋 Configurações do Docker Compose:"
echo "   - SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/automo_db"
echo "   - SPRING_DATASOURCE_USERNAME: automo"
echo "   - SPRING_DATASOURCE_PASSWORD: automo123"
echo "   - SPRING_PROFILES_ACTIVE: local"

echo ""
echo "📋 Configurações do application-local.properties:"
echo "   - spring.datasource.url: \${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/automo_db}"
echo "   - spring.datasource.username: \${SPRING_DATASOURCE_USERNAME:automo}"
echo "   - spring.datasource.password: \${SPRING_DATASOURCE_PASSWORD:automo123}"

echo ""
echo "💡 Para testar a conexão manualmente:"
echo "   1. Acesse PgAdmin: http://localhost:8082 (admin@automo.com / admin)"
echo "   2. Conecte ao servidor PostgreSQL: postgres:5432"
echo "   3. Verifique se o banco 'automo_db' existe"
echo "   4. Verifique se o usuário 'automo' tem acesso"

echo ""
echo "🚀 Para reiniciar os serviços:"
echo "   docker-compose -f docker-compose-postgres.yml down"
echo "   docker-compose -f docker-compose-postgres.yml up --build -d"

echo ""
echo "✅ Validação concluída!" 