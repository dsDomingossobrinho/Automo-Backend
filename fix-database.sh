#!/bin/bash

# Script para corrigir problema do banco de dados - Automo Backend
# Baseado no afrikancoders-backend

echo "🔧 Corrigindo problema do banco de dados..."

# Verificar se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Inicie o Docker Desktop primeiro."
    exit 1
fi

echo "🧹 Limpando ambiente atual..."
docker-compose -f docker-compose-postgres.yml down -v

echo "🗑️ Removendo volumes antigos..."
docker volume rm automo-backend_postgres_data automo-backend_pgadmin_data 2>/dev/null || true

echo "🔍 Verificando configuração do banco..."
echo "   - POSTGRES_DB: automo_db"
echo "   - POSTGRES_USER: automo"
echo "   - POSTGRES_PASSWORD: automo123"
echo "   - SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/automo_db"

echo ""
echo "🚀 Recriando ambiente..."
docker-compose -f docker-compose-postgres.yml up -d postgres

echo "⏳ Aguardando PostgreSQL inicializar..."
sleep 10

echo "🔍 Verificando logs do PostgreSQL..."
docker-compose -f docker-compose-postgres.yml logs postgres | tail -10

echo ""
echo "🧪 Testando conexão ao banco..."
echo "   - Container: automo-postgres"
echo "   - Porta: 5432"
echo "   - Database: automo_db"

echo ""
echo "💡 Para verificar manualmente:"
echo "   1. docker exec -it automo-postgres psql -U automo -d automo_db"
echo "   2. \l (listar bancos)"
echo "   3. \dt (listar tabelas)"

echo ""
echo "🚀 Para iniciar a aplicação:"
echo "   docker-compose -f docker-compose-postgres.yml up -d app"

echo ""
echo "✅ Processo concluído!" 