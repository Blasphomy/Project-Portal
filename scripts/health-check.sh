#!/bin/bash

# Health Check Script
# Checks all services and their dependencies

DB_CONTAINER="learning-platform-db"
DB_USER="postgres"

check_http() {
    local name=$1
    local url=$2
    if curl -s -f "$url" > /dev/null; then
        echo "✓ $name is healthy"
        return 0
    else
        echo "❌ $name is DOWN ($url)"
        return 1
    fi
}

echo "=== Health Check ==="

# Check Database
if docker exec "$DB_CONTAINER" pg_isready -U "$DB_USER" > /dev/null 2>&1; then
    echo "✓ Database is healthy"
else
    echo "❌ Database is DOWN"
fi

# Check Services (External Ports)
check_http "Frontend" "http://localhost:3000"
check_http "API Gateway" "http://localhost:8080/actuator/health"
check_http "Learning Service" "http://localhost:8081/actuator/health"
check_http "AI Service" "http://localhost:8082/actuator/health"
check_http "User Service" "http://localhost:8083/actuator/health"

echo "=== Check Complete ==="
