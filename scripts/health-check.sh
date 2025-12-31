#!/bin/bash

# Health Check Script
# Monitors all services and sends alerts if any are down

# Configuration
ALERT_EMAIL="${ALERT_EMAIL:-admin@learning-platform.com}"
LOG_FILE="/var/log/learning-platform/health-check.log"

# Create log directory
mkdir -p "$(dirname "$LOG_FILE")"

# Timestamp
timestamp() {
    date "+%Y-%m-%d %H:%M:%S"
}

# Log function
log() {
    echo "[$(timestamp)] $1" | tee -a "$LOG_FILE"
}

# Check service health
check_service() {
    local name=$1
    local url=$2
    
    if curl -f -s -o /dev/null "$url"; then
        log "✓ $name is healthy"
        return 0
    else
        log "❌ $name is DOWN"
        return 1
    fi
}

# Send alert (placeholder - integrate with your alerting system)
send_alert() {
    local service=$1
    log "ALERT: $service is down, sending notification..."
    
    # Option 1: Email (requires mailutils)
    # echo "$service is down at $(timestamp)" | mail -s "Service Alert" "$ALERT_EMAIL"
    
    # Option 2: Slack webhook
    # curl -X POST -H 'Content-type: application/json' \
    #      --data "{\"text\":\"⚠️ $service is down!\"}" \
    #      "$SLACK_WEBHOOK_URL"
    
    # Option 3: PagerDuty API
    # Implement PagerDuty integration here
}

log "=== Health Check Starting ==="

# Check all services
all_healthy=true

check_service "User Service" "http://localhost:8081/actuator/health" || {
    send_alert "User Service"
    all_healthy=false
}

check_service "Learning Service" "http://localhost:8082/actuator/health" || {
    send_alert "Learning Service"
    all_healthy=false
}

check_service "AI Service" "http://localhost:8083/actuator/health" || {
    send_alert "AI Service"
    all_healthy=false
}

check_service "API Gateway" "http://localhost:8080/actuator/health" || {
    send_alert "API Gateway"
    all_healthy=false
}

check_service "Frontend" "http://localhost:3000" || {
    send_alert "Frontend"
    all_healthy=false
}

# Check databases
if docker exec learning-platform-user-db pg_isready -U postgres > /dev/null 2>&1; then
    log "✓ User Database is healthy"
else
    log "❌ User Database is DOWN"
    send_alert "User Database"
    all_healthy=false
fi

if docker exec learning-platform-learning-db pg_isready -U postgres > /dev/null 2>&1; then
    log "✓ Learning Database is healthy"
else
    log "❌ Learning Database is DOWN"
    send_alert "Learning Database"
    all_healthy=false
fi

if $all_healthy; then
    log "=== All services healthy ==="
    exit 0
else
    log "=== Some services are down ===" 
    exit 1
fi
