# Script de build otimizado para Docker - Automo Backend (Windows)
# Baseado no afrikancoders-backend

Write-Host "🚀 Iniciando build do Docker para Automo Backend..." -ForegroundColor Green

# Verificar se o Docker está rodando
try {
    docker info | Out-Null
} catch {
    Write-Host "❌ Docker não está rodando. Inicie o Docker Desktop primeiro." -ForegroundColor Red
    exit 1
}

# Limpar containers e imagens antigas (opcional)
$cleanup = Read-Host "🧹 Deseja limpar containers e imagens antigas? (y/N)"
if ($cleanup -eq "y" -or $cleanup -eq "Y") {
    Write-Host "🧹 Limpando containers e imagens antigas..." -ForegroundColor Yellow
    docker system prune -f
    docker image prune -f
}

# Build da imagem
Write-Host "🔨 Fazendo build da imagem Docker..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml build --no-cache

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Build concluído com sucesso!" -ForegroundColor Green
    
    # Perguntar se quer iniciar os serviços
    $start = Read-Host "🚀 Deseja iniciar os serviços agora? (y/N)"
    if ($start -eq "y" -or $start -eq "Y") {
        Write-Host "🚀 Iniciando serviços..." -ForegroundColor Yellow
        docker-compose -f docker-compose-postgres.yml up -d
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ Serviços iniciados com sucesso!" -ForegroundColor Green
            Write-Host "📊 Status dos containers:" -ForegroundColor Cyan
            docker-compose -f docker-compose-postgres.yml ps
            
            Write-Host ""
            Write-Host "🌐 Acessos disponíveis:" -ForegroundColor Cyan
            Write-Host "   - Aplicação: http://localhost:8081" -ForegroundColor White
            Write-Host "   - PgAdmin: http://localhost:8082 (admin@automo.com / admin)" -ForegroundColor White
            Write-Host "   - PostgreSQL: localhost:5432" -ForegroundColor White
            
            Write-Host ""
            Write-Host "📋 Comandos úteis:" -ForegroundColor Cyan
            Write-Host "   - Ver logs: docker-compose -f docker-compose-postgres.yml logs -f" -ForegroundColor White
            Write-Host "   - Parar: docker-compose -f docker-compose-postgres.yml down" -ForegroundColor White
            Write-Host "   - Status: docker-compose -f docker-compose-postgres.yml ps" -ForegroundColor White
        } else {
            Write-Host "❌ Erro ao iniciar os serviços." -ForegroundColor Red
            exit 1
        }
    }
} else {
    Write-Host "❌ Erro no build da imagem Docker." -ForegroundColor Red
    exit 1
}

Write-Host "🎉 Processo concluído!" -ForegroundColor Green 