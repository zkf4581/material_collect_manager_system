#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
RUNTIME_DIR="${ROOT_DIR}/.runtime"

for service in backend h5 admin; do
  pid_file="${RUNTIME_DIR}/${service}.pid"
  if [[ -f "${pid_file}" ]]; then
    pid="$(cat "${pid_file}")"
    if kill -0 "${pid}" 2>/dev/null; then
      kill "${pid}" || true
      echo "已停止 ${service} (${pid})"
    fi
    rm -f "${pid_file}"
  fi
done

if [[ -f "${RUNTIME_DIR}/run-mode" ]] && [[ "$(cat "${RUNTIME_DIR}/run-mode")" == "full" ]]; then
  echo "停止本地 MySQL 与 Redis"
  docker compose -f "${ROOT_DIR}/ops/docker/docker-compose.local.yml" down
fi

rm -f "${RUNTIME_DIR}/run-mode"
rm -f "${RUNTIME_DIR}/backend.port" "${RUNTIME_DIR}/h5.port" "${RUNTIME_DIR}/admin.port"
