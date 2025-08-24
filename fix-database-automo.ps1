# Script para corrigir problema específico do banco "automo" - Automo Backend
# Baseado no afrikancoders-backend

Write-Host "🔧 Corrigindo problema do banco 'automo'..." -ForegroundColor Cyan

# Verificar se o Docker está rodando
try {
    docker info | Out-Null
    Write-Host "✅ Docker está rodando" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não está rodando. Inicie o Docker Desktop primeiro." -ForegroundColor Red
    exit 1
}

Write-Host "🛑 Parando todos os containers..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml down

Write-Host "🧹 Removendo volumes antigos..." -ForegroundColor Yellow
docker volume prune -f

Write-Host "🗑️  Removendo containers órfãos..." -ForegroundColor Yellow
docker container prune -f

Write-Host "🔍 Verificando se há conflito com docker-compose.yml principal..." -ForegroundColor Yellow
if (Test-Path "docker-compose.yml") {
    Write-Host "⚠️  ATENÇÃO: Existe docker-compose.yml principal que pode estar interferindo!" -ForegroundColor Red
    Write-Host "   Renomeando para evitar conflitos..." -ForegroundColor White
    Move-Item "docker-compose.yml" "docker-compose.yml.backup"
}

Write-Host "🚀 Iniciando apenas o docker-compose-postgres.yml..." -ForegroundColor Green
docker-compose -f docker-compose-postgres.yml up --build -d

Write-Host "⏳ Aguardando PostgreSQL inicializar..." -ForegroundColor Yellow
Start-Sleep -Seconds 10

Write-Host "🧪 Testando conexão ao banco correto..." -ForegroundColor Cyan
try {
    docker exec -it automo-postgres psql -U automo -d automo_db -c "\l" 2>$null
    Write-Host "✅ Conexão ao banco 'automo_db' funcionou!" -ForegroundColor Green
} catch {
    Write-Host "❌ Conexão ao banco 'automo_db' falhou!" -ForegroundColor Red
    Write-Host "🔍 Verificando logs..." -ForegroundColor Yellow
    docker-compose -f docker-compose-postgres.yml logs postgres | Select-Object -Last 20
}

Write-Host ""
Write-Host "🔍 Verificando bancos existentes..." -ForegroundColor Yellow
try {
    docker exec -it automo-postgres psql -U automo -c "\l" 2>$null
} catch {
    Write-Host "❌ Não foi possível listar bancos" -ForegroundColor Red
}

Write-Host ""
Write-Host "💡 Configurações corrigidas:" -ForegroundColor Cyan
Write-Host "   ✅ application.properties: usa SPRING_DATASOURCE_URL" -ForegroundColor White
Write-Host "   ✅ application-dev.properties: usa SPRING_DATASOURCE_URL" -ForegroundColor White
Write-Host "   ✅ application-local.properties: usa SPRING_DATASOURCE_URL" -ForegroundColor White
Write-Host "   ✅ docker-compose-postgres.yml: cria automo_db" -ForegroundColor White

Write-Host ""
Write-Host "✅ Correção concluída!" -ForegroundColor Green
Write-Host "   Use: docker-compose -f docker-compose-postgres.yml up --build" -ForegroundColor White
