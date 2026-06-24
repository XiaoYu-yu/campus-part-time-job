#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/../.." && pwd)"
ENV_FILE="${ENV_FILE:-$SCRIPT_DIR/.env}"

NETWORK="campus-standalone-net"
BACKEND_CONTAINER="campus-standalone-backend"
FRONTEND_CONTAINER="campus-standalone-frontend"
BACKEND_IMAGE="localhost/campus-standalone-backend:current"
FRONTEND_IMAGE="localhost/campus-standalone-frontend:current"
MAVEN_IMAGE="${MAVEN_IMAGE:-docker.m.daocloud.io/library/maven:3.9.9-eclipse-temurin-17}"
JRE_IMAGE="${JRE_IMAGE:-docker.m.daocloud.io/library/eclipse-temurin:17-jre}"
NODE_IMAGE="${NODE_IMAGE:-docker.m.daocloud.io/library/node:20-alpine}"
NGINX_IMAGE="${NGINX_IMAGE:-docker.m.daocloud.io/library/nginx:1.27-alpine}"
NPM_REGISTRY="${NPM_REGISTRY:-https://registry.npmmirror.com}"

if [[ -f "$ENV_FILE" ]]; then
  set -a
  # shellcheck disable=SC1090
  source "$ENV_FILE"
  set +a
fi

BACKEND_PORT="${BACKEND_PORT:-18081}"
FRONTEND_PORT="${FRONTEND_PORT:-18080}"

command -v podman >/dev/null 2>&1 || {
  echo "podman is required." >&2
  exit 1
}
command -v curl >/dev/null 2>&1 || {
  echo "curl is required." >&2
  exit 1
}

echo "Building isolated campus images for H2 smoke..."
podman build \
  --format docker \
  --build-arg "MAVEN_IMAGE=${MAVEN_IMAGE}" \
  --build-arg "JRE_IMAGE=${JRE_IMAGE}" \
  -f "$REPO_ROOT/deploy/internal-trial/backend.Dockerfile" \
  -t "$BACKEND_IMAGE" \
  "$REPO_ROOT"

podman build \
  --format docker \
  --build-arg "NODE_IMAGE=${NODE_IMAGE}" \
  --build-arg "NGINX_IMAGE=${NGINX_IMAGE}" \
  --build-arg "NPM_REGISTRY=${NPM_REGISTRY}" \
  --build-arg VITE_API_BASE_URL=/api \
  --build-arg "VITE_TENCENT_MAP_KEY=${VITE_TENCENT_MAP_KEY:-}" \
  -f "$REPO_ROOT/deploy/internal-trial/frontend.Dockerfile" \
  -t "$FRONTEND_IMAGE" \
  "$REPO_ROOT"

podman network exists "$NETWORK" || podman network create "$NETWORK" >/dev/null

for container in "$FRONTEND_CONTAINER" "$BACKEND_CONTAINER"; do
  podman rm -f "$container" >/dev/null 2>&1 || true
done

echo "Starting H2 smoke backend on 127.0.0.1:${BACKEND_PORT}..."
podman run -d \
  --name "$BACKEND_CONTAINER" \
  --network "$NETWORK" \
  --network-alias backend \
  --restart unless-stopped \
  --memory 768m \
  --log-opt max-size=20mb \
  -e SPRING_PROFILES_ACTIVE=test \
  -e SERVER_PORT=8080 \
  -p "127.0.0.1:${BACKEND_PORT}:8080" \
  "$BACKEND_IMAGE" >/dev/null

backend_ready=0
for _ in $(seq 1 60); do
  if curl -fsS "http://127.0.0.1:${BACKEND_PORT}/api/campus/public/health" >/dev/null 2>&1; then
    backend_ready=1
    break
  fi
  sleep 2
done
if [[ "$backend_ready" -ne 1 ]]; then
  podman logs --tail 100 "$BACKEND_CONTAINER"
  echo "H2 smoke backend did not become ready." >&2
  exit 1
fi

echo "Starting smoke frontend on 0.0.0.0:${FRONTEND_PORT}..."
podman run -d \
  --name "$FRONTEND_CONTAINER" \
  --network "$NETWORK" \
  --restart unless-stopped \
  --memory 128m \
  --log-opt max-size=20mb \
  -p "${FRONTEND_PORT}:80" \
  "$FRONTEND_IMAGE" >/dev/null

frontend_ready=0
for _ in $(seq 1 30); do
  if curl -fsS "http://127.0.0.1:${FRONTEND_PORT}/" >/dev/null 2>&1; then
    frontend_ready=1
    break
  fi
  sleep 1
done
if [[ "$frontend_ready" -ne 1 ]]; then
  podman logs --tail 100 "$FRONTEND_CONTAINER"
  echo "Smoke frontend did not become ready." >&2
  exit 1
fi

cat <<EOF
Standalone H2 smoke deployment is ready.
Frontend: http://0.0.0.0:${FRONTEND_PORT}
Backend host binding: 127.0.0.1:${BACKEND_PORT}

WARNING: this mode uses the backend test profile with in-memory H2 data.
It is suitable for LAN smoke verification only and is not a persistent
production deployment.
EOF
podman ps --filter "name=campus-standalone-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
