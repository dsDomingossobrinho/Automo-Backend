# Script para corrigir problema do banco de dados - Automo Backend
# Baseado no afrikancoders-backend

Write-Host "🔧 Corrigindo problema do banco de dados..." -ForegroundColor Cyan

# Verificar se o Docker está rodando
try {
    docker info | Out-Null
    Write-Host "✅ Docker está rodando" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não está rodando. Inicie o Docker Desktop primeiro." -ForegroundColor Red
    exit 1
}

Write-Host "🧹 Limpando ambiente atual..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml down -v

Write-Host "🗑️ Removendo volumes antigos..." -ForegroundColor Yellow
docker volume rm automo-backend_postgres_data automo-backend_pgadmin_data 2>$null

Write-Host "🔍 Verificando configuração do banco..." -ForegroundColor Cyan
Write-Host "   - POSTGRES_DB: automo_db" -ForegroundColor White
Write-Host "   - POSTGRES_USER: automo" -ForegroundColor White
Write-Host "   - POSTGRES_PASSWORD: automo123" -ForegroundColor White
Write-Host "   - SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/automo_db" -ForegroundColor White

Write-Host ""
Write-Host "🚀 Recriando ambiente..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml up -d postgres

Write-Host "⏳ Aguardando PostgreSQL inicializar..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "🔍 Verificando logs do PostgreSQL..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml logs postgres | Select-Object -Last 10

Write-Host ""
Write-Host "🧪 Testando conexão ao banco..." -ForegroundColor Cyan
Write-Host "   - Container: automo-postgres" -ForegroundColor White
Write-Host "   - Porta: 5432" -ForegroundColor White
Write-Host "   - Database: automo_db" -ForegroundColor White

Write-Host ""
Write-Host "💡 Para verificar manualmente:" -ForegroundColor Yellow
Write-Host "   1. docker exec -it automo-postgres psql -U automo -d automo_db" -ForegroundColor White
Write-Host "   2. \l (listar bancos)" -ForegroundColor White
Write-Host "   3. \dt (listar tabelas)" -ForegroundColor White

Write-Host ""
Write-Host "🚀 Para iniciar a aplicação:" -ForegroundColor Yellow
Write-Host "   docker-compose -f docker-compose-postgres.yml up -d app" -ForegroundColor White

Write-Host ""
Write-Host "✅ Processo concluído!" -ForegroundColor Green 