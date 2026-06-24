#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${ENV_FILE:-$SCRIPT_DIR/.env}"
PUBLIC_ORIGIN="${PUBLIC_ORIGIN:-http://127.0.0.1:18080}"

if [[ -e "$ENV_FILE" ]]; then
  echo "Environment file already exists: $ENV_FILE"
  exit 0
fi

command -v openssl >/dev/null 2>&1 || {
  echo "openssl is required to generate deployment secrets." >&2
  exit 1
}

umask 077
DB_PASSWORD="$(openssl rand -hex 24)"
MYSQL_ROOT_PASSWORD="$(openssl rand -hex 24)"
JWT_SECRET="$(openssl rand -hex 48)"

cat >"$ENV_FILE" <<EOF
DB_NAME=campus_standalone
DB_USERNAME=campus_app
DB_PASSWORD=$DB_PASSWORD
MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD
JWT_SECRET=$JWT_SECRET
JWT_EXPIRATION=86400000
APP_CORS_ALLOWED_ORIGINS=$PUBLIC_ORIGIN
VITE_TENCENT_MAP_KEY=
MYSQL_PORT=13306
BACKEND_PORT=18081
FRONTEND_PORT=18080
EOF

chmod 600 "$ENV_FILE"
echo "Generated private environment file: $ENV_FILE"
echo "Public origin: $PUBLIC_ORIGIN"
