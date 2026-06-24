#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd -- "$SCRIPT_DIR/../.." && pwd)"
ENV_FILE="${ENV_FILE:-$SCRIPT_DIR/.env}"

NETWORK="campus-standalone-net"
MYSQL_VOLUME="campus-standalone-mysql-data"
UPLOADS_VOLUME="campus-standalone-uploads-data"
MYSQL_CONTAINER="campus-standalone-mysql"
BACKEND_CONTAINER="campus-standalone-backend"
FRONTEND_CONTAINER="campus-standalone-frontend"
BACKEND_IMAGE="localhost/campus-standalone-backend:current"
FRONTEND_IMAGE="localhost/campus-standalone-frontend:current"
MAVEN_IMAGE="${MAVEN_IMAGE:-docker.m.daocloud.io/library/maven:3.9.9-eclipse-temurin-17}"
JRE_IMAGE="${JRE_IMAGE:-docker.m.daocloud.io/library/eclipse-temurin:17-jre}"
NODE_IMAGE="${NODE_IMAGE:-docker.m.daocloud.io/library/node:20-bookworm-slim}"
NGINX_IMAGE="${NGINX_IMAGE:-docker.m.daocloud.io/library/nginx:1.27-alpine}"
MYSQL_IMAGE="${MYSQL_IMAGE:-docker.m.daocloud.io/library/mysql:8.0}"
NPM_REGISTRY="${NPM_REGISTRY:-https://registry.npmmirror.com}"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Missing $ENV_FILE. Run init-env.sh first." >&2
  exit 1
fi

command -v podman >/dev/null 2>&1 || {
  echo "podman is required." >&2
  exit 1
}
command -v curl >/dev/null 2>&1 || {
  echo "curl is required." >&2
  exit 1
}

set -a
# shellcheck disable=SC1090
source "$ENV_FILE"
set +a

required_vars=(
  DB_NAME DB_USERNAME DB_PASSWORD MYSQL_ROOT_PASSWORD JWT_SECRET
  APP_CORS_ALLOWED_ORIGINS MYSQL_PORT BACKEND_PORT FRONTEND_PORT
)
for name in "${required_vars[@]}"; do
  if [[ -z "${!name:-}" || "${!name}" == replace-with-* ]]; then
    echo "Required deployment value is missing or still a placeholder: $name" >&2
    exit 1
  fi
done

if (( ${#JWT_SECRET} < 32 )); then
  echo "JWT_SECRET must contain at least 32 characters." >&2
  exit 1
fi

# Export derived variables so Podman can inherit them with `-e NAME` without
# placing secret values directly in the process command line.
export MYSQL_DATABASE="$DB_NAME"
export MYSQL_USER="$DB_USERNAME"
export MYSQL_PASSWORD="$DB_PASSWORD"
export MYSQL_PWD="$MYSQL_ROOT_PASSWORD"
export SPRING_PROFILES_ACTIVE=prod
export SERVER_PORT=8080
export DB_HOST="$MYSQL_CONTAINER"
export DB_PORT=3306
export DB_USE_SSL="${DB_USE_SSL:-false}"
export DB_ALLOW_PUBLIC_KEY_RETRIEVAL="${DB_ALLOW_PUBLIC_KEY_RETRIEVAL:-true}"
export DB_MIGRATION_ENABLED="${DB_MIGRATION_ENABLED:-true}"
export DB_FLYWAY_BASELINE_ON_MIGRATE="${DB_FLYWAY_BASELINE_ON_MIGRATE:-false}"
export DB_FLYWAY_BASELINE_VERSION="${DB_FLYWAY_BASELINE_VERSION:-14}"
export JWT_EXPIRATION="${JWT_EXPIRATION:-86400000}"
export APP_UPLOAD_STORAGE_PATH=/app/uploads/private/images/
APP_SEED_INTERNAL_TRIAL="${APP_SEED_INTERNAL_TRIAL:-1}"

echo "Building isolated campus images..."
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
podman volume exists "$MYSQL_VOLUME" || podman volume create "$MYSQL_VOLUME" >/dev/null
podman volume exists "$UPLOADS_VOLUME" || podman volume create "$UPLOADS_VOLUME" >/dev/null

for container in "$FRONTEND_CONTAINER" "$BACKEND_CONTAINER" "$MYSQL_CONTAINER"; do
  podman rm -f "$container" >/dev/null 2>&1 || true
done

echo "Starting isolated MySQL on 127.0.0.1:${MYSQL_PORT}..."
podman run -d \
  --name "$MYSQL_CONTAINER" \
  --network "$NETWORK" \
  --restart unless-stopped \
  --memory 768m \
  --log-opt max-size=20mb \
  -e MYSQL_DATABASE \
  -e MYSQL_USER \
  -e MYSQL_PASSWORD \
  -e MYSQL_ROOT_PASSWORD \
  -p "127.0.0.1:${MYSQL_PORT}:3306" \
  -v "${MYSQL_VOLUME}:/var/lib/mysql" \
  "$MYSQL_IMAGE" \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci >/dev/null

mysql_ready=0
for _ in $(seq 1 60); do
  if podman exec \
    -e MYSQL_PWD \
    "$MYSQL_CONTAINER" \
    mysqladmin ping -uroot --silent >/dev/null 2>&1; then
    mysql_ready=1
    break
  fi
  sleep 2
done
if [[ "$mysql_ready" -ne 1 ]]; then
  podman logs --tail 100 "$MYSQL_CONTAINER"
  echo "MySQL did not become ready." >&2
  exit 1
fi

echo "Starting isolated backend on 127.0.0.1:${BACKEND_PORT}..."
podman run -d \
  --name "$BACKEND_CONTAINER" \
  --network "$NETWORK" \
  --network-alias backend \
  --restart unless-stopped \
  --memory 768m \
  --log-opt max-size=20mb \
  -e SPRING_PROFILES_ACTIVE \
  -e SERVER_PORT \
  -e DB_HOST \
  -e DB_PORT \
  -e DB_NAME \
  -e DB_USERNAME \
  -e DB_PASSWORD \
  -e DB_USE_SSL \
  -e DB_ALLOW_PUBLIC_KEY_RETRIEVAL \
  -e DB_MIGRATION_ENABLED \
  -e DB_FLYWAY_BASELINE_ON_MIGRATE \
  -e DB_FLYWAY_BASELINE_VERSION \
  -e JWT_SECRET \
  -e JWT_EXPIRATION \
  -e APP_CORS_ALLOWED_ORIGINS \
  -e APP_UPLOAD_STORAGE_PATH \
  -p "127.0.0.1:${BACKEND_PORT}:8080" \
  -v "${UPLOADS_VOLUME}:/app/uploads" \
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
  echo "Backend did not become ready." >&2
  exit 1
fi

if [[ "$APP_SEED_INTERNAL_TRIAL" == "1" || "$APP_SEED_INTERNAL_TRIAL" == "true" ]]; then
  seed_file="$REPO_ROOT/backend/db/seed/internal-trial-data.sql"
  if [[ ! -f "$seed_file" ]]; then
    echo "Internal trial seed file is missing: $seed_file" >&2
    exit 1
  fi

  echo "Applying idempotent internal-trial seed data..."
  podman exec \
    -e MYSQL_PWD \
    -i "$MYSQL_CONTAINER" \
    mysql -uroot "$DB_NAME" <"$seed_file"
fi

echo "Starting isolated frontend on 0.0.0.0:${FRONTEND_PORT}..."
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
  echo "Frontend did not become ready." >&2
  exit 1
fi

echo "Standalone deployment is ready."
echo "Frontend: http://0.0.0.0:${FRONTEND_PORT}"
echo "Backend host binding: 127.0.0.1:${BACKEND_PORT}"
echo "MySQL host binding: 127.0.0.1:${MYSQL_PORT}"
podman ps --filter "name=campus-standalone-" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
