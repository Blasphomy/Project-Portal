# AI-Powered Learning Platform

Complete full-stack learning platform with AI-powered assistance, gamification, and interactive code editing.

## 🚀 Quick Start with Docker

```bash
# 1. Create environment file
cp .env.example .env
# Edit .env and add your GEMINI_API_KEY

# 2. Start all services
docker-compose -f docker-compose.simple.yml up -d --build

# 3. Access the platform
# Frontend: http://localhost:3000
# API: http://localhost:8080
```

## ✨ Features

- 🔐 **Authentication** - JWT-based login/register
- 📊 **Dashboard** - Stats, progress, daily challenges  
- 🏆 **Leaderboard** - Compete globally
- 👤 **Profile** - Achievements & stats
- 📚 **Learning System** - Topics, quests, tasks
- 💻 **Code Editor** - Monaco editor with auto-save
- 🤖 **AI Assistant** - Real-time help via Gemini AI
- 🎯 **Gamification** - XP, levels, daily challenges

## 📋 Prerequisites

- Docker Desktop
- Google Gemini API key ([Get free key](https://makersuite.google.com/app/apikey))

## 🛠️ Development Setup

See [deployment_guide.md](./.gemini/antigravity/brain/5df0b18a-b766-4bd8-bef0-06c4dfd72b25/deployment_guide.md) for detailed instructions.

## 📚 Documentation

- [Deployment Guide](./.gemini/antigravity/brain/5df0b18a-b766-4bd8-bef0-06c4dfd72b25/deployment_guide.md)
- [Complete Walkthrough](./.gemini/antigravity/brain/5df0b18a-b766-4bd8-bef0-06c4dfd72b25/walkthrough.md)
- [Task Checklist](./.gemini/antigravity/brain/5df0b18a-b766-4bd8-bef0-06c4dfd72b25/task.md)

## 🏗️ Architecture

```
Frontend (React) → API Gateway (8080)
                       ↓
    ┌──────────────────┼──────────────────┐
    ↓                  ↓                  ↓
User Service    Learning Service    AI Service
  (8081)            (8082)            (8083)
    ↓                  ↓
PostgreSQL        PostgreSQL
```

## 🎓 Tech Stack

**Backend**: Spring Boot 3.2, Java 17, PostgreSQL, JWT  
**Frontend**: React 18, Monaco Editor, Axios  
**AI**: Google Gemini 2.5 Flash  
**Deployment**: Docker, Docker Compose

## 📝 License

MIT License - See LICENSE file

---

Built with ❤️ using React, Spring Boot, and Google Gemini AI
