#!/bin/bash

echo "Running Docker Compose down to stop and remove containers, networks, and volumes..."
docker compose down -v

echo "Starting application with docker compose..."
docker compose up -d --build