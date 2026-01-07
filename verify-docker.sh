#!/bin/bash
# Docker Environment Verification Script for Story 1.1
# Run this script after starting Docker Desktop to verify the environment

echo "=== Story 1.1: Docker Environment Verification ==="
echo ""

# Check if Docker is running
echo "1. Checking Docker availability..."
if ! command -v docker &> /dev/null; then
    echo "   ❌ Docker not found. Please install Docker Desktop."
    exit 1
fi
echo "   ✅ Docker is available: $(docker --version)"
echo ""

# Check if docker-compose is available
echo "2. Checking docker-compose availability..."
if ! command -v docker-compose &> /dev/null; then
    echo "   ❌ docker-compose not found."
    exit 1
fi
echo "   ✅ docker-compose is available: $(docker-compose --version)"
echo ""

# Validate docker-compose.yml syntax
echo "3. Validating docker-compose.yml syntax..."
if docker-compose config > /dev/null 2>&1; then
    echo "   ✅ docker-compose.yml is valid"
else
    echo "   ❌ docker-compose.yml has syntax errors"
    exit 1
fi
echo ""

# Start services
echo "4. Starting Docker services..."
docker-compose up -d
echo ""

# Wait for services to be healthy
echo "5. Waiting for services to be healthy..."
sleep 10
echo ""

# Check MySQL
echo "6. Checking MySQL service..."
if docker-compose exec -T mysql mysqladmin ping -h localhost > /dev/null 2>&1; then
    echo "   ✅ MySQL is healthy on port 3306"
else
    echo "   ❌ MySQL health check failed"
fi
echo ""

# Check Redis
echo "7. Checking Redis service..."
if docker-compose exec -T redis redis-cli ping > /dev/null 2>&1; then
    echo "   ✅ Redis is healthy on port 6379"
else
    echo "   ❌ Redis health check failed"
fi
echo ""

# Show running containers
echo "8. Running containers:"
docker-compose ps
echo ""

echo "=== Verification Complete ==="
echo "To stop services: docker-compose down"
echo "To stop and remove volumes: docker-compose down -v"
