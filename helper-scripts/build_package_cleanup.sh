#!/bin/bash
set -e  # Exit immediately on error

cd ..

echo "Building Docker image 'database'..."
docker build -t database ./db

echo "Starting container 'wallet_database' on port 5432..."
docker run --name wallet_database -dp 5432:5432 database

cd wallet_database

echo "Running gradle clean build..."
gradle clean build

echo "Stopping container 'wallet_database'..."
docker stop wallet_database

echo "Removing container 'wallet_database'..."
docker rm wallet_database

echo "Removing Docker image 'database'..."
docker image rm database

echo "All steps completed successfully!"