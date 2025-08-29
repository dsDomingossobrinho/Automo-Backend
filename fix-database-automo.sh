#!/bin/bash

# Script para corrigir problema específico do banco "automo" - Automo Backend
# Baseado no afrikancoders-backend

echo "🔧 Corrigindo problema do banco 'automo'..."

# Verificar se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Inicie o Docker Desktop primeiro."
    exit 1
fi

echo "🛑 Parando todos os containers..."
docker-compose -f docker-compose-postgres.yml down

echo "🧹 Removendo volumes antigos..."
docker volume prune -f

echo "🗑️  Removendo containers órfãos..."
docker container prune -f

echo "🔍 Verificando se há conflito com docker-compose.yml principal..."
if [ -f "docker-compose.yml" ]; then
    echo "⚠️  ATENÇÃO: Existe docker-compose.yml principal que pode estar interferindo!"
    echo "   Renomeando para evitar conflitos..."
    mv docker-compose.yml docker-compose.yml.backup
fi

echo "🚀 Iniciando apenas o docker-compose-postgres.yml..."
docker-compose -f docker-compose-postgres.yml up --build -d

echo "⏳ Aguardando PostgreSQL inicializar..."
sleep 10

echo "🧪 Testando conexão ao banco correto..."
if docker exec -it automo-postgres psql -U automo -d automo_db -c "\l" 2>/dev/null; then
    echo "✅ Conexão ao banco 'automo_db' funcionou!"
else
    echo "❌ Conexão ao banco 'automo_db' falhou!"
    echo "🔍 Verificando logs..."
    docker-compose -f docker-compose-postgres.yml logs postgres | tail -20
fi

echo ""
echo "🔍 Verificando bancos existentes..."
docker exec -it automo-postgres psql -U automo -c "\l" 2>/dev/null || echo "❌ Não foi possível listar bancos"

echo ""
echo "💡 Configurações corrigidas:"
echo "   ✅ application.properties: usa SPRING_DATASOURCE_URL"
echo "   ✅ application-dev.properties: usa SPRING_DATASOURCE_URL"
echo "   ✅ application-local.properties: usa SPRING_DATASOURCE_URL"
echo "   ✅ docker-compose-postgres.yml: cria automo_db"

echo ""
echo "✅ Correção concluída!"
echo "   Use: docker-compose -f docker-compose-postgres.yml up --build"

