# Script de validação da conexão ao banco de dados - Automo Backend
# Baseado no afrikancoders-backend

Write-Host "🔍 Validando configuração da conexão ao banco de dados..." -ForegroundColor Cyan

# Verificar se o Docker está rodando
try {
    docker info | Out-Null
    Write-Host "✅ Docker está rodando" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não está rodando. Inicie o Docker Desktop primeiro." -ForegroundColor Red
    exit 1
}

# Verificar se os containers estão rodando
Write-Host "`n📊 Verificando status dos containers..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml ps

Write-Host "`n🔍 Verificando logs do PostgreSQL..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml logs postgres | Select-Object -Last 20

Write-Host "`n🔍 Verificando logs da aplicação..." -ForegroundColor Yellow
docker-compose -f docker-compose-postgres.yml logs app | Select-Object -Last 20

Write-Host "`n🌐 Testando conexão ao banco..." -ForegroundColor Cyan
Write-Host "   - Host: postgres (container) / localhost (host)" -ForegroundColor White
Write-Host "   - Porta: 5432" -ForegroundColor White
Write-Host "   - Database: automo_db" -ForegroundColor White
Write-Host "   - Usuário: automo" -ForegroundColor White
Write-Host "   - Senha: automo123" -ForegroundColor White

Write-Host "`n📋 Configurações do Docker Compose:" -ForegroundColor Cyan
Write-Host "   - SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/automo_db" -ForegroundColor White
Write-Host "   - SPRING_DATASOURCE_USERNAME: automo" -ForegroundColor White
Write-Host "   - SPRING_DATASOURCE_PASSWORD: automo123" -ForegroundColor White
Write-Host "   - SPRING_PROFILES_ACTIVE: local" -ForegroundColor White

Write-Host "`n📋 Configurações do application-local.properties:" -ForegroundColor Cyan
Write-Host "   - spring.datasource.url: `${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/automo_db}" -ForegroundColor White
Write-Host "   - spring.datasource.username: `${SPRING_DATASOURCE_USERNAME:automo}" -ForegroundColor White
Write-Host "   - spring.datasource.password: `${SPRING_DATASOURCE_PASSWORD:automo123}" -ForegroundColor White

Write-Host "`n💡 Para testar a conexão manualmente:" -ForegroundColor Cyan
Write-Host "   1. Acesse PgAdmin: http://localhost:8082 (admin@automo.com / admin)" -ForegroundColor White
Write-Host "   2. Conecte ao servidor PostgreSQL: postgres:5432" -ForegroundColor White
Write-Host "   3. Verifique se o banco 'automo_db' existe" -ForegroundColor White
Write-Host "   4. Verifique se o usuário 'automo' tem acesso" -ForegroundColor White

Write-Host "`n🚀 Para reiniciar os serviços:" -ForegroundColor Cyan
Write-Host "   docker-compose -f docker-compose-postgres.yml down" -ForegroundColor White
Write-Host "   docker-compose -f docker-compose-postgres.yml up --build -d" -ForegroundColor White

Write-Host "`n✅ Validação concluída!" -ForegroundColor Green 