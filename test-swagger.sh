#!/bin/bash

# Script de teste do Swagger - Automo Backend
# Baseado no afrikancoders-backend

echo "🧪 Testando se o Swagger está funcionando..."

# Verificar se a aplicação está rodando
echo "🔍 Verificando se a aplicação está rodando..."
if curl -s http://localhost:8081/actuator/health > /dev/null; then
    echo "✅ Aplicação está rodando em http://localhost:8081"
else
    echo "❌ Aplicação não está rodando em http://localhost:8081"
    exit 1
fi

echo ""
echo "🔍 Testando endpoints do Swagger..."

# Testar Swagger UI
echo "📱 Testando Swagger UI..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/swagger-ui/index.html | grep -q "200"; then
    echo "✅ Swagger UI está acessível"
else
    echo "❌ Swagger UI não está acessível"
fi

# Testar API Docs
echo "📚 Testando API Docs..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/api-docs | grep -q "200"; then
    echo "✅ API Docs está acessível"
else
    echo "❌ API Docs não está acessível"
fi

# Testar Swagger Config
echo "⚙️ Testando Swagger Config..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/api-docs/swagger-config | grep -q "200"; then
    echo "✅ Swagger Config está acessível"
else
    echo "❌ Swagger Config não está acessível"
fi

# Testar V3 API Docs
echo "🔌 Testando V3 API Docs..."
if curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/v3/api-docs | grep -q "200"; then
    echo "✅ V3 API Docs está acessível"
else
    echo "❌ V3 API Docs não está acessível"
fi

echo ""
echo "🌐 URLs para testar manualmente:"
echo "   - Swagger UI: http://localhost:8081/swagger-ui/index.html"
echo "   - API Docs: http://localhost:8081/api-docs"
echo "   - Swagger Config: http://localhost:8081/api-docs/swagger-config"
echo "   - V3 API Docs: http://localhost:8081/v3/api-docs"

echo ""
echo "💡 Se algum endpoint retornar 403, verifique o SecurityConfig.java"
echo "💡 Se algum endpoint retornar 404, verifique a configuração do Swagger"

echo ""
echo "✅ Teste concluído!" 