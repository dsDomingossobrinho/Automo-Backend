# Script de teste do Swagger - Automo Backend
# Baseado no afrikancoders-backend

Write-Host "🧪 Testando se o Swagger está funcionando..." -ForegroundColor Cyan

# Verificar se a aplicação está rodando
Write-Host "🔍 Verificando se a aplicação está rodando..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing -ErrorAction Stop
    Write-Host "✅ Aplicação está rodando em http://localhost:8081" -ForegroundColor Green
} catch {
    Write-Host "❌ Aplicação não está rodando em http://localhost:8081" -ForegroundColor Red
    exit 1
}

Write-Host "`n🔍 Testando endpoints do Swagger..." -ForegroundColor Yellow

# Testar Swagger UI
Write-Host "📱 Testando Swagger UI..." -ForegroundColor White
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8081/swagger-ui/index.html" -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Swagger UI está acessível" -ForegroundColor Green
    } else {
        Write-Host "❌ Swagger UI retornou status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Swagger UI não está acessível: $($_.Exception.Message)" -ForegroundColor Red
}

# Testar API Docs
Write-Host "📚 Testando API Docs..." -ForegroundColor White
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8081/api-docs" -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ API Docs está acessível" -ForegroundColor Green
    } else {
        Write-Host "❌ API Docs retornou status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ API Docs não está acessível: $($_.Exception.Message)" -ForegroundColor Red
}

# Testar Swagger Config
Write-Host "⚙️ Testando Swagger Config..." -ForegroundColor White
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8081/api-docs/swagger-config" -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ Swagger Config está acessível" -ForegroundColor Green
    } else {
        Write-Host "❌ Swagger Config retornou status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Swagger Config não está acessível: $($_.Exception.Message)" -ForegroundColor Red
}

# Testar V3 API Docs
Write-Host "🔌 Testando V3 API Docs..." -ForegroundColor White
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8081/v3/api-docs" -UseBasicParsing -ErrorAction Stop
    if ($response.StatusCode -eq 200) {
        Write-Host "✅ V3 API Docs está acessível" -ForegroundColor Green
    } else {
        Write-Host "❌ V3 API Docs retornou status: $($response.StatusCode)" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ V3 API Docs não está acessível: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n🌐 URLs para testar manualmente:" -ForegroundColor Cyan
Write-Host "   - Swagger UI: http://localhost:8081/swagger-ui/index.html" -ForegroundColor White
Write-Host "   - API Docs: http://localhost:8081/api-docs" -ForegroundColor White
Write-Host "   - Swagger Config: http://localhost:8081/api-docs/swagger-config" -ForegroundColor White
Write-Host "   - V3 API Docs: http://localhost:8081/v3/api-docs" -ForegroundColor White

Write-Host "`n💡 Se algum endpoint retornar 403, verifique o SecurityConfig.java" -ForegroundColor Yellow
Write-Host "💡 Se algum endpoint retornar 404, verifique a configuração do Swagger" -ForegroundColor Yellow

Write-Host "`n✅ Teste concluído!" -ForegroundColor Green 