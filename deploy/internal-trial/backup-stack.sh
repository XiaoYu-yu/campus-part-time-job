#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${SCRIPT_DIR}/docker-compose.yml"
ENV_FILE="${1:-${SCRIPT_DIR}/.env}"

if [[ ! -f "${ENV_FILE}" ]]; then
  echo "ENV file not found: ${ENV_FILE}" >&2
  exit 1
fi

set -a
source "${ENV_FILE}"
set +a

: "${COMPOSE_PROJECT_NAME:=campus-trial}"
: "${DB_NAME:?DB_NAME is required in ${ENV_FILE}}"
: "${DB_USERNAME:?DB_USERNAME is required in ${ENV_FILE}}"
: "${DB_PASSWORD:?DB_PASSWORD is required in ${ENV_FILE}}"

TIMESTAMP="${BACKUP_TIMESTAMP:-$(date +%Y%m%d-%H%M%S)}"
BACKUP_ROOT="${BACKUP_ROOT_DIR:-${SCRIPT_DIR}/backups}"
MYSQL_BACKUP_DIR="${BACKUP_ROOT}/mysql"
UPLOADS_BACKUP_DIR="${BACKUP_ROOT}/uploads"
META_BACKUP_DIR="${BACKUP_ROOT}/meta"

mkdir -p "${MYSQL_BACKUP_DIR}" "${UPLOADS_BACKUP_DIR}" "${META_BACKUP_DIR}"

MYSQL_BACKUP_FILE="${MYSQL_BACKUP_DIR}/${COMPOSE_PROJECT_NAME}-${DB_NAME}-${TIMESTAMP}.sql.gz"
UPLOADS_BACKUP_FILE="${UPLOADS_BACKUP_DIR}/${COMPOSE_PROJECT_NAME}-uploads-${TIMESTAMP}.tar.gz"
ENV_BACKUP_FILE="${META_BACKUP_DIR}/${COMPOSE_PROJECT_NAME}-env-${TIMESTAMP}.bak"
MANIFEST_FILE="${META_BACKUP_DIR}/${COMPOSE_PROJECT_NAME}-backup-${TIMESTAMP}.txt"
UPLOADS_VOLUME="${COMPOSE_PROJECT_NAME}_uploads-data"

echo "[$(date '+%F %T')] Backing up MySQL to ${MYSQL_BACKUP_FILE}"
docker compose --env-file "${ENV_FILE}" -f "${COMPOSE_FILE}" exec -T mysql \
  sh -lc "exec mysqldump --single-transaction --quick --routines --triggers --default-character-set=utf8mb4 -u'${DB_USERNAME}' -p'${DB_PASSWORD}' '${DB_NAME}'" \
  | gzip > "${MYSQL_BACKUP_FILE}"

echo "[$(date '+%F %T')] Backing up uploads volume ${UPLOADS_VOLUME} to ${UPLOADS_BACKUP_FILE}"
docker run --rm \
  -v "${UPLOADS_VOLUME}:/source:ro" \
  -v "${UPLOADS_BACKUP_DIR}:/backup" \
  busybox \
  sh -c "tar czf '/backup/$(basename "${UPLOADS_BACKUP_FILE}")' -C /source ."

echo "[$(date '+%F %T')] Copying env file to ${ENV_BACKUP_FILE}"
cp "${ENV_FILE}" "${ENV_BACKUP_FILE}"
chmod 600 "${ENV_BACKUP_FILE}" || true

{
  echo "timestamp=${TIMESTAMP}"
  echo "compose_project_name=${COMPOSE_PROJECT_NAME}"
  echo "env_file=${ENV_FILE}"
  echo "mysql_backup_file=${MYSQL_BACKUP_FILE}"
  echo "uploads_backup_file=${UPLOADS_BACKUP_FILE}"
  echo "env_backup_file=${ENV_BACKUP_FILE}"
  if command -v git >/dev/null 2>&1 && git -C "$(dirname "${SCRIPT_DIR}")" rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    echo "git_commit=$(git -C "$(dirname "${SCRIPT_DIR}")" rev-parse HEAD)"
  fi
} > "${MANIFEST_FILE}"

echo "Backup completed."
echo "MySQL:   ${MYSQL_BACKUP_FILE}"
echo "Uploads: ${UPLOADS_BACKUP_FILE}"
echo "Env:     ${ENV_BACKUP_FILE}"
echo "Manifest:${MANIFEST_FILE}"
