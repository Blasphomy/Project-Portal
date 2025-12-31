#!/bin/bash

# Production Deployment Script
# This script deploys the Learning Platform to production

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
    echo "   Run: cp .env.production .env"
    echo "   Then edit .env with your production values"
    exit 1
fi

# Check critical environment variables
source .env

if [ -z "$GEMINI_API_KEY" ] || [ "$GEMINI_API_KEY" == "your_gemini_api_key_here" ]; then
    echo "❌ GEMINI_API_KEY not set in .env"
    exit 1
fi

if [ "$JWT_SECRET" == "CHANGE_THIS_TO_STRONG_64_CHAR_SECRET_GENERATED_WITH_OPENSSL" ]; then
    echo "❌ JWT_SECRET not changed from default"
    echo "   Generate with: openssl rand -base64 64"
    exit 1
fi

if [ "$POSTGRES_PASSWORD" == "CHANGE_THIS_TO_STRONG_PASSWORD" ]; then
    echo "❌ POSTGRES_PASSWORD not changed from default"
    exit 1
fi

echo "✓ Prerequisites check passed"
echo ""

# Backup existing data if databases exist
echo "Checking for existing data..."
if docker volume ls | grep -q "learning_data\|user_data"; then
    echo "⚠️  Existing data found"
    read -p "Create backup before deploying? (yes/no): " backup_confirm
    
    if [ "$backup_confirm" == "yes" ]; then
        echo "Creating backup..."
        bash scripts/backup.sh
    fi
fi

# Pull latest changes
echo "Pulling latest code..."
git pull origin main || echo "Warning: Git pull failed or not a git repository"

# Stop existing containers
echo "Stopping existing containers..."
docker-compose -f docker-compose.simple.yml down

# Build and start services
echo "Building and starting services..."
docker-compose -f docker-compose.simple.yml up -d --build

# Wait for services to be healthy
echo "Waiting for services to be ready..."
sleep 30

# Check service health
echo "Checking service health..."
services=("user-service:8081" "learning-service:8082" "ai-service:8083" "api-gateway:8080")

for service in "${services[@]}"; do
    IFS=':' read -r name port <<< "$service"
    if curl -f http://localhost:$port/actuator/health 2>/dev/null; then
        echo "✓ $name is healthy"
    else
        echo "⚠️  $name health check failed (this may be normal if service doesn't have actuator)"
    fi
done

# Check frontend
if curl -f http://localhost:3000 2>/dev/null; then
    echo "✓ frontend is healthy"
else
    echo "❌ frontend is not responding"
fi

echo ""
echo "================================"
echo "Deployment Complete!"
echo "================================"
echo ""
echo "Services:"
echo "  Frontend: http://localhost:3000"
echo "  API Gateway: http://localhost:8080"
echo ""
echo "Next steps:"
echo "  1. Test login/register"
echo "  2. Browse topics"
echo "  3. Monitor logs: docker-compose -f docker-compose.simple.yml logs -f"
echo "  4. Setup SSL/HTTPS for production domain"
echo ""
