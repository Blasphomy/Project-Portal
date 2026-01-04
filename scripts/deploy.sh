#!/bin/bash

# Production Deployment Script
# Deploy the Learning Platform to production using Docker Compose

set -e  # Exit on error

echo "================================"
echo "Learning Platform Deployment"
echo "================================"
echo ""

# Check prerequisites
echo "Checking prerequisites..."

if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed"
    exit 1
fi

if [ ! -f ".env" ]; then
    echo "❌ .env file not found"
    echo "   Run: cp .env.example .env"
    echo "   Then edit .env with your production values"
    exit 1
fi

# Check critical environment variables
source .env

if [ -z "$GEMINI_API_KEY" ] || [[ "$GEMINI_API_KEY" == *"your_gemini_api_key"* ]]; then
    echo "❌ GEMINI_API_KEY is not set correctly in .env"
    exit 1
fi

if [[ "$JWT_SECRET" == *"your-256-bit-secret"* ]]; then
    echo "❌ JWT_SECRET is using default value"
    echo "   Generate a strong secret with: openssl rand -base64 64"
    exit 1
fi

if [[ "$POSTGRES_PASSWORD" == "postgres" ]]; then
    echo "⚠️  WARNING: POSTGRES_PASSWORD is set to default 'postgres'"
    echo "   For production, please change this to a strong password"
    read -p "Continue anyway? (y/n) " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

echo "✓ Prerequisites check passed"
echo ""

# Pull latest code
echo "Pulling latest code..."
git pull origin main || echo "Warning: Git pull failed or not a git repository"

# Build and start services
echo "Building and starting services..."
docker-compose up -d --build

# Wait for services
echo "Waiting for services to be ready..."
sleep 30

# Check service health
echo "Checking service health..."
# Note: Internal ports are 8080. We check external mapped ports.
services=(
    "api-gateway:8080"
    "learning-service:8081"
    "ai-service:8082"
    "user-service:8083"
)

for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    if curl -s -f "http://localhost:$port/actuator/health" > /dev/null; then
        echo "✓ $name is healthy"
    else
        echo "⚠️  $name health check failed (http://localhost:$port/actuator/health)"
    fi
done

# Check frontend
if curl -s -f "http://localhost:3000" > /dev/null; then
    echo "✓ Frontend is healthy"
else
    echo "❌ Frontend is not responding"
fi

echo ""
echo "================================"
echo "Deployment Complete!"
echo "================================"
