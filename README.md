# Learning Platform - Modern AI-Powered Tutorial Platform

> **Status:** 🏗️ **Under Active Development** - Core Infrastructure Optimized & Cleaned

A production-ready, AI-powered programming tutorial platform built with Spring Boot WebFlux, Google Gemini AI, and modern microservices architecture.

## 🎯 Project Vision

Transform traditional programming tutorials into an **interactive, AI-enhanced learning experience** with:
- Real-time AI-generated study materials
- Intelligent code hints and reviews
- Gamification (XP, badges, progress tracking)
- Modular architecture ready for scale

---

## 🏗️ Architecture

### Streamlined Microservices Structure
```
Project-Portal/
├── backend/
│   ├── common/              # Shared utilities & exceptions
│   ├── learning-service/    # Core domain & SCHEMA OWNER (Liquibase)
│   ├── ai-service/          # AI integration (Gemini)
│   ├── user-service/        # Auth & user management
│   └── api-gateway/         # Entry point & routing
├── frontend/                # React/Next.js app
├── infrastructure/
│   ├── docker/              # Optimized multi-stage Dockerfiles
│   │   ├── Dockerfile.learning-service
│   │   ├── Dockerfile.user-service
│   │   ├── Dockerfile.ai-service
│   │   └── Dockerfile.api-gateway
│   └── monitoring/          # Optional Prometheus/Grafana configs
├── scripts/                 # Deploy, backup, restore, health-check
└── docker-compose.yml       # Production-ready orchestration
```

### Technology Stack

**Backend:**
- **Spring Boot 3.2+ WebFlux** (Reactive)
- **R2DBC PostgreSQL** (Reactive database)
- **Liquibase** (Database migrations - managed by `learning-service`)
- **Spring Cloud Gateway** (Routing)
- **Java 21** (LTS)

**AI & Data:**
- **Google Gemini 1.5 Flash** (AI Model)
- **PostgreSQL 16** (Single shared database for simplicity & consistency)
- **pgvector** (Vector search support)

**Infrastructure:**
- **Docker & Docker Compose**
- **Maven** (Multi-module build)

---

## 🚀 Quick Start

### Prerequisites
- Docker Desktop
- Java 21 Use `mvnw` (wrapper provided)

### 1. Setup Environment
```bash
# Copy template
cp .env.example .env

# Edit .env with your actual values (REQUIRED)
# - GEMINI_API_KEY
# - JWT_SECRET
# - POSTGRES_PASSWORD
nano .env
```

### 2. Start Everything (Docker)
```bash
# Build and start all services
docker-compose up -d --build

# Check status
docker-compose ps
```

### 3. Access Services
- **Frontend:** http://localhost:3000
- **API Gateway:** http://localhost:8080
- **Learning Service:** http://localhost:8081
- **AI Service:** http://localhost:8082
- **User Service:** http://localhost:8083

---

## 💻 Local Development

Run services individually with the `local` profile (connects to `localhost` database):

### 1. Start Database
```bash
docker-compose up -d postgres
```

### 2. Run Services (Terminal 1-4)
```bash
# Learning Service (Starts first - owns schema)
cd backend/learning-service
../../mvnw spring-boot:run -Dspring-boot.run.profiles=local

# ... User Service, AI Service, API Gateway (in new terminals)
../../mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 🛠️ Maintenance Scripts

Located in `scripts/`:

- **Deploy:** `./scripts/deploy.sh` - automated production deployment
- **Backup:** `./scripts/backup.sh` - backs up `learning_db` to `.sql.gz`
- **Restore:** `./scripts/restore.sh <file>` - restores database from backup
- **Health:** `./scripts/health-check.sh` - checks all service endpoints

---

## 📊 Monitoring (Optional)

The full observability stack (Prometheus, Grafana, PgAdmin) is **optional** to save resources.

```bash
# Start with monitoring stack
docker-compose -f docker-compose.yml -f docker-compose.monitoring.yml up -d
```

| Service | URL | Default Creds |
|---------|-----|---------------|
| **Grafana** | http://localhost:3001 | `admin` / `admin123` |
| **Prometheus** | http://localhost:9090 | - |
| **PgAdmin** | http://localhost:5050 | `admin@learning-platform.com` / `admin` |

---

## 📁 Key Design Changes (Jan 2026 Cleanup)

1.  **Single Shared Database:** Moved from multiple partial DBs to one `learning_db` for simplicity. `learning-service` owns the schema via Liquibase.
2.  **Clean Configuration:** All `application.properties` stripped of unused bloat. Profile-based config (`application-local.properties`, `application-docker.properties`) added.
3.  **Correct Docker Ports:** Backend services run on internal 8080. Mapped to 8081-8083 externally.
4.  **Optimized Builds:** Dockerfiles now fully support multi-module Maven structure with dependency caching.

---

## 🤝 Contributing

This is a portfolio project showcasing modern, reactive, AI-integrated architecture.

**Current Focus:**
- [ ] Completing Frontend integration
- [ ] Enhancing User Service auth flows
- [ ] Expanding AI lesson generation capabilities

---

**License:** MIT
