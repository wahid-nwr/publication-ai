#!/bin/bash

set -e

echo "Compiling backend..."
mvn clean package -DskipTests

echo "Starting backend with Docker Compose..."
docker compose up --build -d

echo "Backend started."

echo "Starting React frontend..."
cd ../publication-ai-frontend

docker rm -f ai-frontend

docker build -t publication-ai-frontend:latest .

docker run -d \
    -p 8081:8081 \
    --name ai-frontend \
    -e VITE_KEYCLOAK_URL="http://localhost:8088" \
    -e VITE_PUBLICATION_API_URL="http://localhost:8080" \
    -e VITE_APP_URL="http://localhost:8081/rag" \
    publication-ai-frontend:latest

echo "Started React frontend..."