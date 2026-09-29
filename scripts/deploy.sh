#!/usr/bin/env bash
# ===================================================================
# deploy.sh
# Deployment script for Ubuntu / AWS EC2 running Docker
# Usage:
#   ./deploy.sh <ENVIRONMENT> <IMAGE_TAG>
# Example:
#   ./deploy.sh dev registry.gitlab.com/user/student-api:a1b2c3d
#   ./deploy.sh prod registry.gitlab.com/user/student-api:a1b2c3d
# ===================================================================

set -euo pipefail

ENV="${1:-dev}"
IMAGE="${2:-student-api:latest}"
CONTAINER_NAME="student-api-${ENV}"

echo "========================================="
echo " Starting Deployment on Ubuntu / EC2     "
echo " Environment : ${ENV}                    "
echo " Image       : ${IMAGE}                  "
echo " Container   : ${CONTAINER_NAME}         "
echo "========================================="

# 1. Pull latest requested image tag
echo "[1/4] Pulling Docker image..."
docker pull "${IMAGE}"

# 2. Stop and remove existing container if running
echo "[2/4] Stopping existing container if present..."
if docker ps -a --format '{{.Names}}' | grep -Eq "^${CONTAINER_NAME}\$"; then
    docker stop "${CONTAINER_NAME}" || true
    docker rm "${CONTAINER_NAME}" || true
fi

# 3. Run new container with profile and environment variables
echo "[3/4] Launching new container..."
docker run -d \
  --name "${CONTAINER_NAME}" \
  --restart always \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE="${ENV}" \
  -e DB_HOST="${DB_HOST:-localhost}" \
  -e DB_PORT="${DB_PORT:-3306}" \
  -e DB_NAME="${DB_NAME:-student_${ENV}}" \
  -e DB_USERNAME="${DB_USERNAME:-root}" \
  -e DB_PASSWORD="${DB_PASSWORD:-}" \
  "${IMAGE}"

# 4. Verification
echo "[4/4] Verifying container status..."
sleep 5
if docker ps | grep -q "${CONTAINER_NAME}"; then
    echo "========================================="
    echo " SUCCESS: ${CONTAINER_NAME} is running! "
    echo " Accessible at: http://<SERVER_IP>:8080/api/students"
    echo "========================================="
else
    echo "ERROR: Container failed to start. Showing logs:"
    docker logs "${CONTAINER_NAME}"
    exit 1
fi
