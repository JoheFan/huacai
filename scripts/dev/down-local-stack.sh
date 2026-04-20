#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/docker-compose.local.yml"
MYSQL_ONLY_COMPOSE_FILE="$ROOT_DIR/docker-compose.mysql.yml"
LEGACY_PROXY_CONTAINER="huacai-backend-proxy-live"

extra_args=()
if [[ "${1:-}" == "--volumes" ]]; then
  extra_args+=(--volumes)
fi

if (( ${#extra_args[@]} > 0 )); then
  docker compose -f "$COMPOSE_FILE" down --remove-orphans "${extra_args[@]}"
else
  docker compose -f "$COMPOSE_FILE" down --remove-orphans
fi
docker compose -f "$MYSQL_ONLY_COMPOSE_FILE" down --remove-orphans >/dev/null 2>&1 || true
docker rm -f "$LEGACY_PROXY_CONTAINER" >/dev/null 2>&1 || true
