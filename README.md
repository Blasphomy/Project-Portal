# 🚀 AI-Powered Learning Platform

An intelligent learning platform that generates personalized skill trees using AI, tracks progress, and provides adaptive learning paths.

## 🌟 Features

- ✨ **AI-Generated Skill Trees**: Personalized learning paths created by Gemini AI
- 🎯 **Interactive Quest System**: Gamified learning with quest tracking
- 📊 **Progress Tracking**: Visual progress indicators and completion tracking
- 🎨 **Beautiful UI**: Modern, responsive design with cosmic theme
- 🔐 **User Authentication**: Secure JWT-based authentication
- 💾 **Persistent Storage**: PostgreSQL database with reactive R2DBC

## 🏗️ Architecture

**Microservices:**
- `api-gateway` - Routes requests to appropriate services (Port 8080)
- `learning-service` - Manages skill trees, quests, and progress (Port 8081)
- `user-service` - Handles authentication and user management (Port 8083)
- `ai-service` - Generates skill trees using Gemini AI (Port 8082)

**Tech Stack:**
- **Backend**: Spring Boot 3.x, WebFlux, R2DBC
- **Frontend**: React 18, React Flow, Firebase Hosting
- **Database**: PostgreSQL (Supabase)
- **Cache**: Redis
- **AI**: Google Gemini API

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Git
- (Optional) Gemini API Key from [Google AI Studio](https://makersuite.google.com/app/apikey)

### 1. Clone the Repository

\`\`\`bash
git clone https://github.com/Blasphomy/Project-Portal.git
cd Project-Portal
git checkout third
\`\`\`

### 2. Configure Environment Variables

Create a \`.env\` file in the project root:

\`\`\`env
# Database Configuration (Use Supabase or local PostgreSQL)
DATABASE_URL=postgresql://postgres:YOUR_PASSWORD@db.xxx.supabase.co:5432/postgres

# Optional: Gemini API Key (Leave empty to use hardcoded test data)
GEMINI_API_KEY=your_gemini_api_key_here

# JWT Secret (Auto-generated if not provided)
JWT_SECRET=your_jwt_secret_here
\`\`\`

### 3. Start the Services

\`\`\`bash
docker-compose up -d
\`\`\`

This will start:
- API Gateway: http://localhost:8080
- Learning Service: http://localhost:8081
- User Service: http://localhost:8083
- AI Service: http://localhost:8082
- Redis: localhost:6380

### 4. Access the Application

**Frontend (Live on Firebase):**
https://project-portal-9a6df.web.app

**Local Development:**
The frontend is already deployed to Firebase and configured to work with \`localhost:8080\` for local backend development.

## 📝 API Documentation

### Authentication Endpoints

\`\`\`
POST /api/auth/register - Register new user
POST /api/auth/login - Login user
\`\`\`

### Skill Tree Endpoints

\`\`\`
POST /api/ai/skill-tree/generate - Generate AI skill tree
GET /api/skill-trees/user/{userId} - Get user's skill tree
POST /api/skill-trees/{skillTreeId}/quests/{questId}/complete - Complete a quest
\`\`\`

## 🗄️ Database Setup

The application uses **Supabase** (free PostgreSQL hosting) instead of local PostgreSQL.

**To set up your own Supabase database:**

1. Go to https://supabase.com
2. Create a new project
3. Get your connection string from Settings → Database
4. Update the \`.env\` file with your connection string
5. The database schema will be created automatically via Liquibase migrations

## 🎮 Usage

1. **Register an Account**: Use the registration page to create an account
2. **Generate Skill Tree**: Navigate to "Custom Path" and enter your learning goal
3. **Explore Quests**: Click on quest nodes to see details and start quests
4. **Track Progress**: Mark quests as complete and unlock dependent quests

## 🔧 Development

### Running Individual Services

\`\`\`bash
# Build specific service
docker-compose build learning-service

# Restart specific service
docker-compose restart api-gateway

# View logs
docker logs -f learning-service
\`\`\`

### Frontend Development

The frontend is deployed to Firebase, but you can run it locally:

\`\`\`bash
cd frontend
npm install
npm start
\`\`\`

Frontend will be available at http://localhost:3000

### Database Migrations

Liquibase migrations run automatically on service startup. Migration files are in:
\`backend/learning-service/src/main/resources/db/changelog/\`

## 🐛 Troubleshooting

**Services not starting:**
- Check Docker logs: \`docker-compose logs\`
- Ensure ports 8080-8083, 6380 are available
- Verify .env file configuration

**Database connection errors:**
- Verify Supabase connection string in .env
- Check if database is accessible from your network
- Ensure Liquibase migrations completed successfully

**AI generation not working:**
- The app works with hardcoded test data by default
- To enable real AI: Add your Gemini API key to .env and rebuild ai-service

## 📦 Deployment

**Current Deployment:**
- Frontend: Firebase Hosting
- Database: Supabase PostgreSQL
- Backend: Local Docker (for now)

**For production deployment**, consider:
- Railway.app (with $5 free credit)
- Render.com (free tier)
- Fly.io (free tier)

## 🤝 Contributing

This is a portfolio project. Feel free to fork and extend!

## 📄 License

MIT License

## 👤 Author

**Joseph Jonas**
- GitHub: [@Blasphomy](https://github.com/Blasphomy)

---

⭐ **Star this repo if you find it helpful!**
