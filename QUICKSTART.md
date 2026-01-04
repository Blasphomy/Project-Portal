# ⚡ Quick Start Guide

Get the AI-Powered Learning Platform running in 5 minutes!

## 📋 What You'll Get

- ✅ **Frontend**: Live at https://project-portal-9a6df.web.app
- ✅ **Backend**: Running locally on your machine
- ✅ **Database**: Free Supabase PostgreSQL

## 🚀 Setup Steps

### 1. Clone & Navigate

\`\`\`bash
git clone https://github.com/Blasphomy/Project-Portal.git
cd Project-Portal
git checkout third
\`\`\`

### 2. Get Your Supabase Database (FREE!)

1. Go to https://supabase.com
2. Sign in with GitHub
3. Click "New project"
4. Name it "project-portal-db"
5. Copy the connection string from Settings → Database → Connection string → URI

### 3. Create .env File

\`\`\`bash
# Copy the example
cp .env.example .env

# Edit .env and add your Supabase connection string
# Replace YOUR_PASSWORD with your actual Supabase password
\`\`\`

### 4. Start Docker Services

\`\`\`bash
docker-compose up -d
\`\`\`

Wait 30-60 seconds for services to start.

### 5. Open the App!

**Frontend (Live):**
https://project-portal-9a6df.web.app

**The frontend will automatically connect to your local backend at \`localhost:8080\`**

## ✅ Test It Works

1. Register a new account
2. Click "Custom Path"
3. Type: "learn to code"
4. Click "Generate My Skill Tree"
5. You should see a beautiful skill tree with 3 connected quests!

## 🔍 Verify Services are Running

\`\`\`bash
# Check all containers
docker ps

# Should see:
# - api-gateway (8080)
# - learning-service (8081)
# - user-service (8083)
# - ai-service (8082)
# - redis (6380)
\`\`\`

## 🐛 Something Not Working?

**Can't access the frontend?**
- Frontend is on Firebase - should always be accessible
- URL: https://project-portal-9a6df.web.app

**Services not starting?**
\`\`\`bash
# Check logs
docker-compose logs

# Rebuild and restart
docker-compose down
docker-compose up -d --build
\`\`\`

**Database connection errors?**
- Verify your Supabase connection string in .env
- Make sure you replaced YOUR_PASSWORD with actual password
- Check if Supabase project is running

**Still having issues?**
- Check the full README.md for detailed troubleshooting
- Look at docker logs: \`docker logs -f learning-service\`

## 🎉 You're All Set!

The platform is now running:
- Frontend: Firebase (public URL)
- Backend: Your local Docker
- Database: Supabase (cloud)

**Share with others:**
Just send them the GitHub link → they run \`docker-compose up\` → done!

---

Need more details? Check the [Full README](README.md)
