# API Gateway - Quick Start Guide

## Overview
Spring Cloud Gateway routing all requests to backend services

## Services & Ports
- **Gateway**: :8080 (main entry point)
- **Learning Service**: :8081 (topics, quests, tasks)
- **AI Service**: :8082 (AI content generation)
- **User Service**: :8083 (authentication, users)

## Routes

### Learning Service (localhost:8081)
- `GET /api/topics` → All topics
- `GET /api/topics/{id}` → Topic by ID
- `GET /api/topics/{id}/quests` → Quests for topic
- `GET /api/quests/{id}/tasks` → Tasks for quest

### AI Service (localhost:8082)
- `POST /api/ai/generate-material` → Generate study material
- `POST /api/ai/hints` → Get code hints
- `POST /api/ai/review-code` → Review code

### User Service (localhost:8083)
- `POST /api/auth/register` → Register new user
- `POST /api/auth/login` → Login
- `GET /api/auth/me` → Current user

## Features
- ✅ Automatic routing
- ✅ Circuit breakers (Resilience4j)
- ✅ Request logging with correlation IDs
- ✅ CORS enabled for localhost:3000
- ✅ Redis rate limiting (ready)
- ✅ Health checks & metrics

## Usage

### Start Gateway
```bash
cd backend/api-gateway
../../mvnw spring-boot:run
```

### Test Routes
```bash
# All requests go through :8080

# Get topics (routes to learning-service)
curl http://localhost:8080/api/topics

# Login (routes to user-service)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'

# Check gateway health
curl http://localhost:8080/actuator/health
```

## Circuit Breaker
If a service is down, gateway will:
1. Try request normally
2. After 50% failure rate → Open circuit
3. Wait 30-60s before retry
4. Gradually allow test requests

## Next Steps
- Add JWT validation filter
- Implement rate limiting per user
- Add request/response transformation
