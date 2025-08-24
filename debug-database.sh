#!/bin/bash

# Script para debugar problema específico do banco "automo" - Automo Backend
# Baseado no afrikancoders-backend

echo "🔍 Debugando problema do banco 'automo'..."

# Verificar se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Inicie o Docker Desktop primeiro."
    exit 1
fi

echo "📊 Status dos containers..."
docker-compose -f docker-compose-postgres.yml ps

echo ""
echo "🔍 Verificando logs do PostgreSQL..."
docker-compose -f docker-compose-postgres.yml logs postgres | grep -E "(FATAL|ERROR|automo)" | tail -20

echo ""
echo "🔍 Verificando logs da aplicação..."
docker-compose -f docker-compose-postgres.yml logs app | grep -E "(FATAL|ERROR|automo)" | tail -20

echo ""
echo "🧪 Testando conexão direta ao PostgreSQL..."
echo "   Tentando conectar como usuário 'automo' ao banco 'automo_db'..."

# Tentar conectar ao banco correto
if docker exec -it automo-postgres psql -U automo -d automo_db -c "\l" 2>/dev/null; then
    echo "✅ Conexão ao banco 'automo_db' funcionou!"
else
    echo "❌ Conexão ao banco 'automo_db' falhou!"
fi

echo ""
echo "🔍 Verificando bancos existentes..."
docker exec -it automo-postgres psql -U automo -c "\l" 2>/dev/null || echo "❌ Não foi possível listar bancos"

echo ""
echo "🔍 Verificando se há algum processo tentando conectar ao banco 'automo'..."
docker exec -it automo-postgres psql -U automo -c "SELECT * FROM pg_stat_activity WHERE datname = 'automo';" 2>/dev/null || echo "❌ Não foi possível verificar processos"

echo ""
echo "🔍 Verificando configurações do Docker Compose..."
echo "   POSTGRES_DB: automo_db"
echo "   POSTGRES_USER: automo"
echo "   SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/automo_db"

echo ""
echo "🔍 Verificando se há conflito com docker-compose.yml principal..."
if [ -f "docker-compose.yml" ]; then
    echo "⚠️  ATENÇÃO: Existe docker-compose.yml principal que pode estar interferindo!"
    echo "   Use apenas: docker-compose -f docker-compose-postgres.yml up"
fi

echo ""
echo "💡 Possíveis soluções:"
echo "   1. Parar TODOS os containers: docker stop \$(docker ps -q)"
echo "   2. Remover TODOS os volumes: docker volume prune -f"
echo "   3. Usar apenas docker-compose-postgres.yml"
echo "   4. Verificar se não há outro processo usando a porta 5432"

echo ""
echo "✅ Debug concluído!" 