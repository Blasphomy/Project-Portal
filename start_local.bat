@echo off
echo Starting Learning Platform Locally...

echo Loading environment variables from .env...
for /f "usebackq tokens=*" %%a in (".env") do (
    echo %%a | findstr /b /v "#" >nul && set %%a
)

echo 1. Stopping any Docker containers...
docker-compose down

echo 2. Starting Postgres Database (Docker)...
docker-compose up -d postgres

echo Waiting for database to start...
timeout /t 5

echo 3. Building ALL modules...
call .\mvnw clean install -DskipTests -am

echo 4. Starting Learning Service (owns schema)...
start "Learning Service" java -jar backend\learning-service\target\learning-service-2.0.0-SNAPSHOT.jar --spring.profiles.active=local

echo Waiting for schema migration (15s)...
timeout /t 15

echo 5. Starting User Service & AI Service...
start "User Service" java -jar backend\user-service\target\user-service-2.0.0-SNAPSHOT.jar --spring.profiles.active=local
start "AI Service" java -jar backend\ai-service\target\ai-service-2.0.0-SNAPSHOT.jar --spring.profiles.active=local

echo 6. Starting API Gateway...
start "API Gateway" java -jar backend\api-gateway\target\api-gateway-2.0.0-SNAPSHOT.jar --spring.profiles.active=local

echo ===================================================
echo System starting up!
echo Monitor the opened command windows for logs.
echo Frontend: http://localhost:3000 (Run separately)
echo API Gateway: http://localhost:8080
echo ===================================================
pause
