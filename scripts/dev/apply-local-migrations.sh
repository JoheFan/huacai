#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
MIGRATION_DIR="$ROOT_DIR/sql/migration"
MYSQL_CONTAINER="${HUACAI_MYSQL_CONTAINER:-huacai-mysql}"
MYSQL_DATABASE="${HUACAI_MYSQL_DATABASE:-huacai_system}"
MYSQL_USER="${HUACAI_MYSQL_USER:-root}"
MYSQL_PASSWORD="${HUACAI_MYSQL_PASSWORD:-root}"
MIGRATION_TABLE="schema_migration_history"

log() {
  printf '[huacai-migration] %s\n' "$*"
}

fail() {
  printf '[huacai-migration] ERROR: %s\n' "$*" >&2
  exit 1
}

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || fail "缺少命令: $1"
}

mysql_exec() {
  docker exec "$MYSQL_CONTAINER" mysql \
    -h127.0.0.1 \
    -u"$MYSQL_USER" \
    -p"$MYSQL_PASSWORD" \
    "$MYSQL_DATABASE" \
    "$@"
}

ensure_migration_table() {
  mysql_exec -e "
    CREATE TABLE IF NOT EXISTS \`${MIGRATION_TABLE}\` (
      \`id\` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
      \`script_name\` VARCHAR(255) NOT NULL,
      \`applied_at\` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
      PRIMARY KEY (\`id\`),
      UNIQUE KEY \`uk_${MIGRATION_TABLE}_script_name\` (\`script_name\`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
  " >/dev/null
}

is_applied() {
  local script_name="$1"
  mysql_exec -N -s -e "
    SELECT COUNT(*)
    FROM \`${MIGRATION_TABLE}\`
    WHERE \`script_name\` = '${script_name}';
  "
}

mark_applied() {
  local script_name="$1"
  mysql_exec -e "
    INSERT INTO \`${MIGRATION_TABLE}\` (\`script_name\`)
    VALUES ('${script_name}');
  " >/dev/null
}

apply_script() {
  local script_path="$1"
  local script_name
  local applied_count

  script_name="$(basename "$script_path")"
  applied_count="$(is_applied "$script_name")"

  if [[ "$applied_count" != "0" ]]; then
    log "跳过已执行 migration: ${script_name}"
    return 0
  fi

  log "执行 migration: ${script_name}"
  docker exec -i "$MYSQL_CONTAINER" mysql \
    -h127.0.0.1 \
    -u"$MYSQL_USER" \
    -p"$MYSQL_PASSWORD" \
    "$MYSQL_DATABASE" < "$script_path"
  mark_applied "$script_name"
}

main() {
  require_cmd docker

  [[ -d "$MIGRATION_DIR" ]] || fail "migration 目录不存在: $MIGRATION_DIR"
  docker inspect "$MYSQL_CONTAINER" >/dev/null 2>&1 || fail "MySQL 容器不存在: $MYSQL_CONTAINER"

  ensure_migration_table

  while IFS= read -r script_path; do
    [[ -n "$script_path" ]] || continue
    apply_script "$script_path"
  done < <(find "$MIGRATION_DIR" -maxdepth 1 -type f -name '*.sql' | sort -V)

  log "所有 migration 已处理完成"
}

main "$@"
