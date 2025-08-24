#!/bin/bash

# Script de build otimizado para Docker - Automo Backend
# Baseado no afrikancoders-backend

echo "🚀 Iniciando build do Docker para Automo Backend..."

# Verificar se o Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Inicie o Docker Desktop primeiro."
    exit 1
fi

# Limpar containers e imagens antigas (opcional)
read -p "🧹 Deseja limpar containers e imagens antigas? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo "🧹 Limpando containers e imagens antigas..."
    docker system prune -f
    docker image prune -f
fi

# Build da imagem
echo "🔨 Fazendo build da imagem Docker..."
docker-compose -f docker-compose-postgres.yml build --no-cache

if [ $? -eq 0 ]; then
    echo "✅ Build concluído com sucesso!"
    
    # Perguntar se quer iniciar os serviços
    read -p "🚀 Deseja iniciar os serviços agora? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "🚀 Iniciando serviços..."
        docker-compose -f docker-compose-postgres.yml up -d
        
        if [ $? -eq 0 ]; then
            echo "✅ Serviços iniciados com sucesso!"
            echo "📊 Status dos containers:"
            docker-compose -f docker-compose-postgres.yml ps
            
            echo ""
            echo "🌐 Acessos disponíveis:"
            echo "   - Aplicação: http://localhost:8081"
            echo "   - PgAdmin: http://localhost:8082 (admin@automo.com / admin)"
            echo "   - PostgreSQL: localhost:5432"
            
            echo ""
            echo "📋 Comandos úteis:"
            echo "   - Ver logs: docker-compose -f docker-compose-postgres.yml logs -f"
            echo "   - Parar: docker-compose -f docker-compose-postgres.yml down"
            echo "   - Status: docker-compose -f docker-compose-postgres.yml ps"
        else
            echo "❌ Erro ao iniciar os serviços."
            exit 1
        fi
    fi
else
    echo "❌ Erro no build da imagem Docker."
    exit 1
fi

echo "🎉 Processo concluído!" 