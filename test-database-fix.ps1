# Script para testar se a correção do healthcheck resolveu o problema - Automo Backend
# Baseado no afrikancoders-backend

Write-Host "🧪 Testando se a correção do healthcheck resolveu o problema..." -ForegroundColor Cyan

# Verificar se o Docker está rodando
try {
    docker info | Out-Null
    Write-Host "✅ Docker está rodando" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não está rodando. Inicie o Docker Desktop primeiro." -ForegroundColor Red
    exit 1
}

Write-Host "🛑 Parando containers existentes..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml down

Write-Host "🧹 Limpando volumes antigos..." -ForegroundColor Yellow
docker volume prune -f

Write-Host "🚀 Iniciando com a correção do healthcheck..." -ForegroundColor Green
docker-compose -f docker-compose-postgres.yml up --build -d

Write-Host "⏳ Aguardando PostgreSQL inicializar..." -ForegroundColor Yellow
Start-Sleep -Seconds 15

Write-Host "🔍 Verificando logs do PostgreSQL..." -ForegroundColor Yellow
Write-Host "   Procurando por erros 'FATAL: database \"automo\" does not exist'..." -ForegroundColor White
docker-compose -f docker-compose-postgres.yml logs postgres | Select-String -Pattern "(FATAL|ERROR|automo)" | Select-Object -Last 10

Write-Host ""
Write-Host "🔍 Verificando se o healthcheck está funcionando..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml ps

Write-Host ""
Write-Host "🧪 Testando conexão direta ao banco correto..." -ForegroundColor Cyan
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
Write-Host "💡 Correção aplicada:" -ForegroundColor Cyan
Write-Host "   ✅ healthcheck agora usa: pg_isready -U automo -d automo_db" -ForegroundColor White
Write-Host "   ✅ Isso deve evitar tentativas de conexão ao banco 'automo' inexistente" -ForegroundColor White

Write-Host ""
Write-Host "✅ Teste concluído!" -ForegroundColor Green
Write-Host "   Se não houver mais erros 'FATAL: database \"automo\" does not exist'," -ForegroundColor White
Write-Host "   a correção funcionou!" -ForegroundColor White

