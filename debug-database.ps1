# Script para debugar problema específico do banco "automo" - Automo Backend
# Baseado no afrikancoders-backend

Write-Host "🔍 Debugando problema do banco 'automo'..." -ForegroundColor Cyan

# Verificar se o Docker está rodando
try {
    docker info | Out-Null
    Write-Host "✅ Docker está rodando" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não está rodando. Inicie o Docker Desktop primeiro." -ForegroundColor Red
    exit 1
}

Write-Host "📊 Status dos containers..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml ps

Write-Host ""
Write-Host "🔍 Verificando logs do PostgreSQL..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml logs postgres | Select-String -Pattern "(FATAL|ERROR|automo)" | Select-Object -Last 20

Write-Host ""
Write-Host "🔍 Verificando logs da aplicação..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml logs app | Select-String -Pattern "(FATAL|ERROR|automo)" | Select-Object -Last 20

Write-Host ""
Write-Host "🧪 Testando conexão direta ao PostgreSQL..." -ForegroundColor Cyan
Write-Host "   Tentando conectar como usuário 'automo' ao banco 'automo_db'..." -ForegroundColor White

# Tentar conectar ao banco correto
try {
    docker exec -it automo-postgres psql -U automo -d automo_db -c "\l" 2>$null
    Write-Host "✅ Conexão ao banco 'automo_db' funcionou!" -ForegroundColor Green
} catch {
    Write-Host "❌ Conexão ao banco 'automo_db' falhou!" -ForegroundColor Red
}

Write-Host ""
Write-Host "🔍 Verificando bancos existentes..." -ForegroundColor Yellow
try {
    docker exec -it automo-postgres psql -U automo -c "\l" 2>$null
} catch {
    Write-Host "❌ Não foi possível listar bancos" -ForegroundColor Red
}

Write-Host ""
Write-Host "🔍 Verificando se há algum processo tentando conectar ao banco 'automo'..." -ForegroundColor Yellow
try {
    docker exec -it automo-postgres psql -U automo -c "SELECT * FROM pg_stat_activity WHERE datname = 'automo';" 2>$null
} catch {
    Write-Host "❌ Não foi possível verificar processos" -ForegroundColor Red
}

Write-Host ""
Write-Host "🔍 Verificando configurações do Docker Compose..." -ForegroundColor Cyan
Write-Host "   POSTGRES_DB: automo_db" -ForegroundColor White
Write-Host "   POSTGRES_USER: automo" -ForegroundColor White
Write-Host "   SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/automo_db" -ForegroundColor White

Write-Host ""
Write-Host "🔍 Verificando se há conflito com docker-compose.yml principal..." -ForegroundColor Yellow
if (Test-Path "docker-compose.yml") {
    Write-Host "⚠️  ATENÇÃO: Existe docker-compose.yml principal que pode estar interferindo!" -ForegroundColor Red
    Write-Host "   Use apenas: docker-compose -f docker-compose-postgres.yml up" -ForegroundColor White
}

Write-Host ""
Write-Host "💡 Possíveis soluções:" -ForegroundColor Cyan
Write-Host "   1. Parar TODOS os containers: docker stop (docker ps -q)" -ForegroundColor White
Write-Host "   2. Remover TODOS os volumes: docker volume prune -f" -ForegroundColor White
Write-Host "   3. Usar apenas docker-compose-postgres.yml" -ForegroundColor White
Write-Host "   4. Verificar se não há outro processo usando a porta 5432" -ForegroundColor White

Write-Host ""
Write-Host "✅ Debug concluído!" -ForegroundColor Green 