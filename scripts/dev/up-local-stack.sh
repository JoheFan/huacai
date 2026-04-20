#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
COMPOSE_FILE="$ROOT_DIR/docker-compose.local.yml"
MYSQL_ONLY_COMPOSE_FILE="$ROOT_DIR/docker-compose.mysql.yml"
MYSQL_CONTAINER="huacai-mysql"
BACKEND_CONTAINER="huacai-backend-test"
LEGACY_PROXY_CONTAINER="huacai-backend-proxy-live"
TARGET_VOLUME="1025_mysql-data"
BACKEND_HEALTH_URL="http://127.0.0.1:18081/api/v1/health"
MIGRATION_SCRIPT="$ROOT_DIR/scripts/dev/apply-local-migrations.sh"

log() {
  printf '[huacai-local] %s\n' "$*"
}

fail() {
  printf '[huacai-local] ERROR: %s\n' "$*" >&2
  exit 1
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || fail "缺少命令: $1"
}

detect_mysql_volume() {
  docker inspect "$MYSQL_CONTAINER" \
    --format '{{range .Mounts}}{{if eq .Destination "/var/lib/mysql"}}{{.Name}}{{end}}{{end}}' \
    2>/dev/null || true
}

ensure_volume() {
  docker volume inspect "$TARGET_VOLUME" >/dev/null 2>&1 || docker volume create "$TARGET_VOLUME" >/dev/null
}

sync_mysql_volume() {
  local source_volume="$1"

  ensure_volume
  log "同步 MySQL 数据卷: ${source_volume} -> ${TARGET_VOLUME}"
  docker run --rm \
    -v "${source_volume}:/from" \
    -v "${TARGET_VOLUME}:/to" \
    alpine:3.20 \
    sh -lc 'find /to -mindepth 1 -maxdepth 1 -exec rm -rf {} +; cp -a /from/. /to/'
}

remove_legacy_containers() {
  docker rm -f "$LEGACY_PROXY_CONTAINER" >/dev/null 2>&1 || true
  docker rm -f "$BACKEND_CONTAINER" >/dev/null 2>&1 || true
  docker rm -f "$MYSQL_CONTAINER" >/dev/null 2>&1 || true
}

remove_mysql_only_stack() {
  docker compose -f "$MYSQL_ONLY_COMPOSE_FILE" down --remove-orphans >/dev/null 2>&1 || true
}

wait_for_container_health() {
  local container_name="$1"
  local timeout_seconds="$2"
  local started_at
  local now
  local status

  started_at="$(date +%s)"
  while true; do
    status="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "$container_name" 2>/dev/null || true)"
    case "$status" in
      healthy)
        return 0
        ;;
      exited|dead)
        docker logs --tail 80 "$container_name" || true
        fail "容器 ${container_name} 已退出"
        ;;
    esac

    now="$(date +%s)"
    if (( now - started_at >= timeout_seconds )); then
      docker logs --tail 80 "$container_name" || true
      fail "等待容器 ${container_name} 健康超时"
    fi
    sleep 2
  done
}

wait_for_backend_http() {
  local timeout_seconds="$1"
  local started_at
  local now

  started_at="$(date +%s)"
  while true; do
    if curl -fsS --max-time 5 "$BACKEND_HEALTH_URL" >/dev/null 2>&1; then
      return 0
    fi

    now="$(date +%s)"
    if (( now - started_at >= timeout_seconds )); then
      docker logs --tail 120 "$BACKEND_CONTAINER" || true
      fail "等待后端健康检查超时: ${BACKEND_HEALTH_URL}"
    fi
    sleep 2
  done
}

main() {
  local source_volume

  require_cmd docker
  require_cmd curl

  docker compose -f "$COMPOSE_FILE" config >/dev/null

  source_volume="${HUACAI_MYSQL_SOURCE_VOLUME:-$(detect_mysql_volume)}"
  if [[ -n "$source_volume" && "$source_volume" != "$TARGET_VOLUME" ]]; then
    log "检测到旧 MySQL 匿名卷 ${source_volume}，将迁移到 ${TARGET_VOLUME}"
  fi

  remove_legacy_containers
  remove_mysql_only_stack

  if [[ -n "$source_volume" && "$source_volume" != "$TARGET_VOLUME" ]]; then
    sync_mysql_volume "$source_volume"
  else
    ensure_volume
  fi

  log "启动本地联调链路"
  docker compose -f "$COMPOSE_FILE" up -d --remove-orphans mysql

  log "等待 MySQL healthy"
  wait_for_container_health "$MYSQL_CONTAINER" 180

  log "应用本地 migration"
  "$MIGRATION_SCRIPT"

  log "启动后端"
  docker compose -f "$COMPOSE_FILE" up -d backend-test

  log "等待后端 healthy"
  wait_for_container_health "$BACKEND_CONTAINER" 360

  log "校验后端 HTTP 健康检查"
  wait_for_backend_http 60

  log "本地联调链路已就绪"
  log "健康检查: ${BACKEND_HEALTH_URL}"
}

main "$@"
