#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${1:-${SCRIPT_DIR}/.env}"
MANIFEST_FILE="${2:-}"

if [[ ! -f "${ENV_FILE}" ]]; then
  echo "ENV file not found: ${ENV_FILE}" >&2
  exit 1
fi

set -a
source "${ENV_FILE}"
set +a

: "${COMPOSE_PROJECT_NAME:=campus-trial}"
: "${DB_NAME:?DB_NAME is required in ${ENV_FILE}}"
: "${MYSQL_ROOT_PASSWORD:?MYSQL_ROOT_PASSWORD is required in ${ENV_FILE}}"

BACKUP_ROOT="${BACKUP_ROOT_DIR:-${SCRIPT_DIR}/backups}"
META_BACKUP_DIR="${BACKUP_ROOT}/meta"

if [[ -z "${MANIFEST_FILE}" ]]; then
  MANIFEST_FILE="$(find "${META_BACKUP_DIR}" -maxdepth 1 -type f -name "${COMPOSE_PROJECT_NAME}-backup-*.txt" | sort | tail -n 1)"
fi

if [[ -z "${MANIFEST_FILE}" || ! -f "${MANIFEST_FILE}" ]]; then
  echo "Backup manifest not found. Pass it explicitly or ensure backups/meta contains one." >&2
  exit 1
fi

DUMP_FILE="$(grep '^mysql_backup_file=' "${MANIFEST_FILE}" | sed 's/^mysql_backup_file=//')"
UPLOADS_FILE="$(grep '^uploads_backup_file=' "${MANIFEST_FILE}" | sed 's/^uploads_backup_file=//')"
ENV_BACKUP_FILE="$(grep '^env_backup_file=' "${MANIFEST_FILE}" | sed 's/^env_backup_file=//')"

for required_file in "${DUMP_FILE}" "${UPLOADS_FILE}" "${ENV_BACKUP_FILE}"; do
  if [[ ! -f "${required_file}" ]]; then
    echo "Required backup artifact missing: ${required_file}" >&2
    exit 1
  fi
done

TIMESTAMP="${RESTORE_DRILL_TIMESTAMP:-$(date +%Y%m%d-%H%M%S)}"
TMP_ROOT="${TMPDIR:-/tmp}/${COMPOSE_PROJECT_NAME}-restore-drill-${TIMESTAMP}"
RESTORE_DB="${RESTORE_DB_NAME:-restore_drill}"
CONTAINER_NAME="${COMPOSE_PROJECT_NAME}-restore-drill-${TIMESTAMP}"
UPLOADS_EXTRACT_DIR="${TMP_ROOT}/uploads"

mkdir -p "${UPLOADS_EXTRACT_DIR}"

cleanup() {
  docker rm -f "${CONTAINER_NAME}" >/dev/null 2>&1 || true
  rm -rf "${TMP_ROOT}"
}
trap cleanup EXIT

echo "[$(date '+%F %T')] Starting temporary MySQL container ${CONTAINER_NAME}"
docker run -d \
  --name "${CONTAINER_NAME}" \
  -e MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD}" \
  -e MYSQL_DATABASE="${RESTORE_DB}" \
  mysql:8.4 >/dev/null

echo "[$(date '+%F %T')] Waiting for temporary MySQL to become ready"
for _ in $(seq 1 60); do
  if docker exec "${CONTAINER_NAME}" mysqladmin ping -uroot -p"${MYSQL_ROOT_PASSWORD}" --silent >/dev/null 2>&1; then
    break
  fi
  sleep 2
done

if ! docker exec "${CONTAINER_NAME}" mysqladmin ping -uroot -p"${MYSQL_ROOT_PASSWORD}" --silent >/dev/null 2>&1; then
  echo "Temporary MySQL did not become ready in time." >&2
  exit 1
fi

echo "[$(date '+%F %T')] Restoring dump ${DUMP_FILE} into ${RESTORE_DB}"
gunzip -c "${DUMP_FILE}" | docker exec -i "${CONTAINER_NAME}" mysql -uroot -p"${MYSQL_ROOT_PASSWORD}" "${RESTORE_DB}"

echo "[$(date '+%F %T')] Verifying restored schema and sample data"
ORDER_COUNT="$(docker exec "${CONTAINER_NAME}" mysql -N -B -uroot -p"${MYSQL_ROOT_PASSWORD}" "${RESTORE_DB}" -e "select count(*) from campus_relay_order;")"
HAS_ORDER_7002="$(docker exec "${CONTAINER_NAME}" mysql -N -B -uroot -p"${MYSQL_ROOT_PASSWORD}" "${RESTORE_DB}" -e "select count(*) from campus_relay_order where id = 'CR202604070002';")"
HAS_ORDER_6001="$(docker exec "${CONTAINER_NAME}" mysql -N -B -uroot -p"${MYSQL_ROOT_PASSWORD}" "${RESTORE_DB}" -e "select count(*) from campus_relay_order where id = 'CR202604060001';")"

echo "[$(date '+%F %T')] Verifying uploads archive ${UPLOADS_FILE}"
tar xzf "${UPLOADS_FILE}" -C "${UPLOADS_EXTRACT_DIR}"
UPLOADS_FILE_COUNT="$(find "${UPLOADS_EXTRACT_DIR}" -type f | wc -l | tr -d ' ')"

echo "Restore drill completed."
echo "manifest=${MANIFEST_FILE}"
echo "dump=${DUMP_FILE}"
echo "uploads=${UPLOADS_FILE}"
echo "env_backup=${ENV_BACKUP_FILE}"
echo "restored_order_count=${ORDER_COUNT}"
echo "has_order_id_CR202604070002=${HAS_ORDER_7002}"
echo "has_order_id_CR202604060001=${HAS_ORDER_6001}"
echo "restored_upload_file_count=${UPLOADS_FILE_COUNT}"
