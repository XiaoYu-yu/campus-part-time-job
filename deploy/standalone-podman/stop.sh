#!/usr/bin/env bash
set -euo pipefail

for container in \
  campus-standalone-frontend \
  campus-standalone-backend \
  campus-standalone-mysql; do
  podman stop "$container" >/dev/null 2>&1 || true
done

echo "Stopped campus standalone containers. Data volumes were preserved."
