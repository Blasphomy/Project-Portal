@echo off
echo Starting all services...

start "User Service" cmd /k "cd backend\user-service && ..\..\mvnw spring-boot:run"
start "Learning Service" cmd /k "cd backend\learning-service && ..\..\mvnw spring-boot:run"
start "AI Service" cmd /k "cd backend\ai-service && ..\..\mvnw spring-boot:run"
start "API Gateway" cmd /k "cd backend\api-gateway && ..\..\mvnw spring-boot:run"

echo Starting Frontend with Host Check Disabled (for Cloudflare)...
set DANGEROUSLY_DISABLE_HOST_CHECK=true
start "Frontend" cmd /k "cd frontend && npm start"

echo All services launched! Wait for them to startup (approx 30s).
