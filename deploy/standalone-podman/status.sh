#!/usr/bin/env bash
set -euo pipefail

podman ps -a \
  --filter "name=campus-standalone-" \
  --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
