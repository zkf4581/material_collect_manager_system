#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
RUNTIME_DIR="${ROOT_DIR}/.runtime"

for service in backend h5 admin; do
  pid_file="${RUNTIME_DIR}/${service}.pid"
  if [[ -f "${pid_file}" ]]; then
    pid="$(cat "${pid_file}")"
    if kill -0 "${pid}" 2>/dev/null; then
      echo "${service}: RUNNING (${pid})"
    else
      echo "${service}: STOPPED (stale pid ${pid})"
    fi
  else
    echo "${service}: NOT STARTED"
  fi
done

if [[ -f "${RUNTIME_DIR}/run-mode" ]]; then
  echo "run-mode: $(cat "${RUNTIME_DIR}/run-mode")"
fi
for port_name in backend h5 admin; do
  port_file="${RUNTIME_DIR}/${port_name}.port"
  if [[ -f "${port_file}" ]]; then
    echo "${port_name}-port: $(cat "${port_file}")"
  fi
done

if [[ -f "${RUNTIME_DIR}/run-mode" ]] && [[ "$(cat "${RUNTIME_DIR}/run-mode")" == "full" ]]; then
  echo "docker compose:"
  docker compose -f "${ROOT_DIR}/ops/docker/docker-compose.local.yml" ps
fi
