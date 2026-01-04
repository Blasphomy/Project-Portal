# Test Script for AI-Powered Learning Platform

Write-Host "🧪 Testing AI-Powered Learning Platform..." -ForegroundColor Cyan
Write-Host ""

# Check if services are running
Write-Host "📋 Checking Docker Services..." -ForegroundColor Yellow
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
Write-Host ""

# Test API Gateway
Write-Host "🌐 Testing API Gateway (http://localhost:8080)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5
    Write-Host "✅ API Gateway is UP!" -ForegroundColor Green
} catch {
    Write-Host "❌ API Gateway is DOWN" -ForegroundColor Red
}
Write-Host ""

# Test Learning Service
Write-Host "📚 Testing Learning Service (http://localhost:8081)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8081/actuator/health" -UseBasicParsing -TimeoutSec 5
    Write-Host "✅ Learning Service is UP!" -ForegroundColor Green
} catch {
    Write-Host "❌ Learning Service is DOWN" -ForegroundColor Red
}
Write-Host ""

# Test User Service
Write-Host "👤 Testing User Service (http://localhost:8083)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8083/actuator/health" -UseBasicParsing -TimeoutSec 5
    Write-Host "✅ User Service is UP!" -ForegroundColor Green
} catch {
    Write-Host "❌ User Service is DOWN" -ForegroundColor Red
}
Write-Host ""

# Test AI Service
Write-Host "🤖 Testing AI Service (http://localhost:8082)..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8082/actuator/health" -UseBasicParsing -TimeoutSec 5
    Write-Host "✅ AI Service is UP!" -ForegroundColor Green
} catch {
    Write-Host "❌ AI Service is DOWN" -ForegroundColor Red
}
Write-Host ""

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "🎉 Testing Complete!" -ForegroundColor Green
Write-Host ""
Write-Host "📱 Frontend URL: https://project-portal-9a6df.web.app" -ForegroundColor Yellow
Write-Host "🔗 API Gateway: http://localhost:8080" -ForegroundColor Yellow
Write-Host ""
Write-Host "💡 Next steps:" -ForegroundColor Cyan
Write-Host "  1. Open https://project-portal-9a6df.web.app" -ForegroundColor White
Write-Host "  2. Register a new account" -ForegroundColor White
Write-Host "  3. Generate a skill tree!" -ForegroundColor White
Write-Host ""
