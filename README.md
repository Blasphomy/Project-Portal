# Learning Platform - Modern AI-Powered Tutorial Platform

> **Status:** 🏗️ **Under Active Development** - Core Infrastructure Complete, Modules In Progress

A production-ready, AI-powered programming tutorial platform built with Spring Boot WebFlux, Google Gemini AI, and modern observability stack.

## 🎯 Project Vision

Transform traditional programming tutorials into an **interactive, AI-enhanced learning experience** with:
- Real-time AI-generated study materials
- Intelligent code hints and reviews
- Gamification (XP, badges, progress tracking)
- Production-grade observability and resilience
- Modular monolith architecture ready for microservices extraction

---

## 📊 Current Implementation Status

### ✅ **Phase 1: Project Structure** (COMPLETE)
- [x] Multi-module Maven project
- [x] 5 backend modules: common, learning-service, ai-service, user-service, api-gateway
- [x] Parent POM with dependency management

### ✅ **Phase 2: Infrastructure** (COMPLETE)
- [x] Docker Compose with 10 services
  - PostgreSQL with pgvector extension
  - Redis for caching & sessions
  - Prometheus + Grafana (metrics & dashboards)
  - ELK Stack (Elasticsearch, Logstash, Kibana)
  - Zipkin (distributed tracing)
  - PgAdmin (database UI)
- [x] Liquibase database migrations (4 changesets)
  - Initial schema (topics, quests, tasks, badges)
  - Vector search support with pgvector
  - Auth tables (users, roles, JWT tokens)
  - Seed data
- [x] Dockerfiles for backend & frontend
- [x] Monitoring configurations (Prometheus, Grafana, Logstash)
- [x] Application properties (dev & docker profiles)

### ✅ **Phase 3: Backend Modules** (PARTIAL)

#### **Common Module** (COMPLETE)
- [x] Exception hierarchy (BaseException, ResourceNotFoundException, etc.)
- [x] GlobalExceptionHandler for WebFlux
- [x] ErrorResponse & ApiResponse DTOs
- [x] Utility classes (IdGenerator)
- [x] CORS configuration

#### **Learning Service** (COMPLETE)
- [x] Domain entities (Topic, Quest, Task, UserProgress)
- [x] R2DBC repositories
- [x] Services (TopicService, QuestService, TaskService, ProgressService)
- [x] REST controllers
- [x] Event publishing (TaskCompletedEvent)
- [x] Caching support
- [x] Main application class

#### **AI Service** (COMPLETE)
- [x] GeminiAiService with Resilience4j
  - Circuit breaker pattern
  - Rate limiting
  - Retry logic
- [x] WebClient configuration
- [x] Prompt builders for study materials, hints, code review
- [x] Fallback mechanisms

#### **User Service** (TODO)
- [ ] JWT token service
- [ ] OAuth2 configuration (Google, GitHub)
- [ ] User authentication & registration
- [ ] Role-based access control
- [ ] Password hashing (BCrypt)

#### **API Gateway** (TODO)
- [ ] Spring Cloud Gateway routes
- [ ] JWT validation filters
- [ ] Rate limiting per user
- [ ] CORS handling
- [ ] Request logging

### 🔄 **Phase 4: Frontend** (TODO)
- [ ] Next.js 14 setup with App Router
- [ ] Authentication pages
- [ ] Topic/Quest/Task UI
- [ ] Code editor (Monaco)
- [ ] Real-time updates (WebSocket/SSE)

### 🔄 **Phase 5: DevOps** (TODO)
- [ ] Kubernetes manifests
- [ ] Helm charts
- [ ] GitHub Actions CI/CD
- [ ] Testing (Unit, Integration, E2E)

---

## 🏗️ Architecture

### Modular Monolith Structure
```
Project-Portal/
├── backend/
│   ├── common/              # Shared utilities & exceptions
│   ├── learning-service/    # Core learning domain
│   ├── ai-service/          # AI integration with Resilience4j
│   ├── user-service/        # Auth & user management (TODO)
│   └── api-gateway/         # Spring Cloud Gateway (TODO)
├── frontend/                # Next.js app (TODO)
├── infrastructure/
│   ├── docker/              # Dockerfiles
│   ├── monitoring/          # Prometheus, Grafana, ELK
│   └── liquibase/           # Database migrations
└── docker-compose.yml       # Local dev environment
```

### Technology Stack

**Backend:**
- Spring Boot 3.2.1 WebFlux (Reactive)
- R2DBC PostgreSQL (Reactive database)
- Liquibase (Database migrations)
- Redis (Caching & sessions)
- Resilience4j (Circuit breaker, rate limiting, retry)
- Spring Cloud Gateway
- Spring Security + OAuth2 + JWT
- Lombok

**AI & Search:**
- Google Gemini 1.5 Flash
- Pinecone Vector Database
- pgvector (PostgreSQL extension)

**Observability:**
- Prometheus (Metrics)
- Grafana (Dashboards)
- ELK Stack (Logs)
- Zipkin (Distributed tracing)
- Spring Boot Actuator
- Micrometer

**Infrastructure:**
- Docker & Docker Compose
- Kubernetes (planned)
- PostgreSQL 16
- Redis 7

---

## 🚀 Quick Start

### Prerequisites
```bash
# Required
- Docker Desktop
- Java 21
- Maven 3.8+

# Optional
- Node.js 20+ (for frontend)
- kubectl (for Kubernetes)
```

### 1. Setup Environment Variables

Create `.env` file in project root (or use `environmrnt.env` and rename):
```env
# AI Services
AI_API_KEY=your_gemini_api_key
PINECONE_API_KEY=your_pinecone_key
PINECONE_ENVIRONMENT=your_pinecone_env

# OAuth2 (optional)
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GITHUB_CLIENT_ID=your_github_client_id
GITHUB_CLIENT_SECRET=your_github_client_secret

# JWT
JWT_SECRET=your_256_bit_secret
JWT_EXPIRATION=86400000

# Database (auto-configured by docker-compose)
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
POSTGRES_DB=learning_db
```

### 2. Start Infrastructure

```bash
# Start all services (PostgreSQL, Redis, Prometheus, Grafana, ELK, Zipkin)
docker-compose up -d

# Check status
docker-compose ps

# View logs
docker-compose logs -f postgres
```

### 3. Build Backend Modules

```bash
# Build all modules
./mvnw clean install

# Build specific module
cd backend/learning-service
../../mvnw clean package
```

### 4. Run Learning Service

```bash
cd backend/learning-service
../../mvnw spring-boot:run
```

**Endpoints:**
- Application: http://localhost:8080
- Actuator Health: http://localhost:8080/actuator/health
- Prometheus Metrics: http://localhost:8080/actuator/prometheus
- API Docs: http://localhost:8080/swagger-ui.html

### 5. Access Monitoring Tools

| Service | URL | Credentials |
|---------|-----|-------------|
| Grafana | http://localhost:3001 | admin / admin123 |
| Prometheus | http://localhost:9090 | - |
| Kibana | http://localhost:5601 | - |
| Zipkin | http://localhost:9411 | - |
| PgAdmin | http://localhost:8081 | admin@learning-platform.com / admin |

---

## 📁 File Inventory

**Total Files Created:** ~80 files

### Infrastructure (18 files)
- `docker-compose.yml` - Full stack orchestration
- `infrastructure/docker/` - Dockerfiles (2)
- `infrastructure/monitoring/` - Prometheus, Grafana configs (5)
- `infrastructure/monitoring/elk/` - Logstash configs (2)
- `infrastructure/liquibase/changelog/` - Database migrations (5)
- Application properties (2)

### Common Module (10 files)
- Exception classes (5)
- DTOs (2)
- Utilities (1)
- Configuration (2)

### Learning Service (13 files)
- Domain entities (4)
- Repositories (4)
- Services (4)
- Controllers (2)
- Events (1)
- Main application (1)

### AI Service (4 files)
- GeminiAiService with Resilience4j
- WebClientConfig
- Application main
- Properties

### Build Files (5 files)
- Parent POM
- Module POMs (4)

---

## 🧪 Testing the Setup

### 1. Test Database Connection
```bash
docker exec -it learning-platform-db psql -U postgres -d learning_db

# Check tables
\dt

# Verify seed data
SELECT * FROM topics;
SELECT * FROM quests;
```

### 2. Test Redis
```bash
docker exec -it learning-platform-redis redis-cli

# Test connection
PING
```

### 3. Test Learning Service API

```bash
# Get all topics
curl http://localhost:8080/api/topics

# Get specific topic
curl http://localhost:8080/api/topics/topic-java-101

# Get quests for topic
curl http://localhost:8080/api/topics/topic-java-101/quests

# Get tasks for quest
curl http://localhost:8080/api/quests/quest-1/tasks
```

### 4. Test Metrics
```bash
# Health check
curl http://localhost:8080/actuator/health

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

---

## 🔧 Development Workflow

### Building Modules
```bash
# Build everything
./mvnw clean install

# Build without tests
./mvnw clean install -DskipTests

# Build single module
cd backend/learning-service
../../mvnw clean package
```

### Database Migrations
```bash
# Migrations run automatically on startup via Liquibase

# Rollback last changeset
./mvnw liquibase:rollback -Dliquibase.rollbackCount=1
```

### Viewing Logs
```bash
# Application logs
tail -f logs/application.log

# Docker logs
docker-compose logs -f learning-platform-backend
```

---

## 📝 API Documentation

### Learning Service Endpoints

**Topics:**
- `GET /api/topics` - Get all topics
- `GET /api/topics/{id}` - Get topic by ID
- `GET /api/topics/difficulty/{difficulty}` - Filter by difficulty
- `GET /api/topics/category/{category}` - Filter by category
- `POST /api/topics` - Create topic
- `PUT /api/topics/{id}` - Update topic
- `DELETE /api/topics/{id}` - Delete topic

**Quests:**
- `GET /api/topics/{topicId}/quests` - Get quests for topic
- `GET /api/quests/{questId}` - Get quest details

**Tasks:**
- `GET /api/quests/{questId}/tasks` - Get tasks for quest
- `GET /api/tasks/{taskId}` - Get task details

---

## 🎯 Next Steps

### Immediate (Phase 3 Completion)
1. **User Service:**
   - Implement JWT token generation/validation
   - OAuth2 login (Google, GitHub)
   - User registration/login endpoints
   - Password hashing

2. **API Gateway:**
   - Configure routes to services
   - Add JWT validation filter
   - Implement rate limiting
   - Setup request logging

### Short-term (Phase 4)
3. **Frontend:**
   - Next.js 14 setup
   - Authentication pages
   - Topic/Quest browsing UI
   - Code editor integration
   - WebSocket for real-time updates

### Medium-term (Phase 5)
4. **Testing:**
   - Unit tests (JUnit 5 + Mockito)
   - Integration tests (Testcontainers)
   - E2E tests (Playwright)

5. **DevOps:**
   - Kubernetes manifests
   - Helm charts
   - GitHub Actions CI/CD
   - Production deployment guide

---

## 🐛 Troubleshooting

### Build Errors
```bash
# Clean and rebuild
./mvnw clean install -U

# Check Java version
java -version  # Should be 21

# Update dependencies
./mvnw dependency:resolve
```

### Database Connection Issues
```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Restart PostgreSQL
docker-compose restart postgres

# Check connection
docker exec -it learning-platform-db pg_isready
```

### Port Conflicts
```bash
# Check what's using port 8080
netstat -ano | findstr :8080

# Change port in application.properties
server.port=8081
```

---

## 🤝 Contributing

This is a portfolio/interview project showcasing:
- ✅ Modern Spring Boot architecture
- ✅ Reactive programming (WebFlux, R2DBC)
- ✅ Resilience patterns (Circuit breaker, retry, rate limiting)
- ✅ Observability (metrics, logs, tracing)
- ✅ Cloud-native design (Docker, K8s-ready)
- ✅ Event-driven architecture
- ✅ AI integration

---

## 📚 Key Design Decisions

1. **Modular Monolith:** Easier to develop/deploy than microservices, but structured for future extraction
2. **Reactive Stack:** WebFlux + R2DBC for non-blocking, scalable architecture
3. **Liquibase over Flyway:** Better rollback support and enterprise features
4. **Resilience4j:** Production-grade fault tolerance for AI service
5. **pgvector:** In-database vector search instead of separate vector DB for simplicity
6. **ELK + Prometheus:** Complete observability stack ready for production
7. **Event-Driven:** Spring Events for inter-module communication (easily replaceable with Kafka)

---

## 📄 License

This is a portfolio project. Use freely for learning and reference.

---

## 🙏 Acknowledgments

Built with:
- Spring Boot & Spring Cloud
- Google Gemini AI
- PostgreSQL + pgvector
- Resilience4j
- Prometheus & Grafana
- Docker

---

**Questions or Issues?** This is a work in progress. Current focus: Completing User Service and API Gateway.
