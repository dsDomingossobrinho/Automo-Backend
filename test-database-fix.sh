#!/bin/bash

# Script para testar se a correção do healthcheck resolveu o problema - Automo Backend
# Baseado no afrikancoders-backend

echo "🧪 Testando se a correção do healthcheck resolveu o problema..."

# Verificar se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Inicie o Docker Desktop primeiro."
    exit 1
fi

echo "🛑 Parando containers existentes..."
docker-compose -f docker-compose-postgres.yml down

echo "🧹 Limpando volumes antigos..."
docker volume prune -f

echo "🚀 Iniciando com a correção do healthcheck..."
docker-compose -f docker-compose-postgres.yml up --build -d

echo "⏳ Aguardando PostgreSQL inicializar..."
sleep 15

echo "🔍 Verificando logs do PostgreSQL..."
echo "   Procurando por erros 'FATAL: database \"automo\" does not exist'..."
docker-compose -f docker-compose-postgres.yml logs postgres | grep -E "(FATAL|ERROR|automo)" | tail -10

echo ""
echo "🔍 Verificando se o healthcheck está funcionando..."
docker-compose -f docker-compose-postgres.yml ps

echo ""
echo "🧪 Testando conexão direta ao banco correto..."
if docker exec -it automo-postgres psql -U automo -d automo_db -c "\l" 2>/dev/null; then
    echo "✅ Conexão ao banco 'automo_db' funcionou!"
else
    echo "❌ Conexão ao banco 'automo_db' falhou!"
fi

echo ""
echo "🔍 Verificando bancos existentes..."
docker exec -it automo-postgres psql -U automo -c "\l" 2>/dev/null || echo "❌ Não foi possível listar bancos"

echo ""
echo "💡 Correção aplicada:"
echo "   ✅ healthcheck agora usa: pg_isready -U automo -d automo_db"
echo "   ✅ Isso deve evitar tentativas de conexão ao banco 'automo' inexistente"

echo ""
echo "✅ Teste concluído!"
echo "   Se não houver mais erros 'FATAL: database \"automo\" does not exist',"
echo "   a correção funcionou!"
