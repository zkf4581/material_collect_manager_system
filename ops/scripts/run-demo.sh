#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
RUNTIME_DIR="${ROOT_DIR}/.runtime"
RUN_MODE="${RUN_MODE:-demo}"
BACKEND_PORT="${BACKEND_PORT:-18080}"
H5_PORT="${H5_PORT:-5173}"
ADMIN_PORT="${ADMIN_PORT:-5174}"

mkdir -p "${RUNTIME_DIR}"
echo "${RUN_MODE}" > "${RUNTIME_DIR}/run-mode"
echo "${BACKEND_PORT}" > "${RUNTIME_DIR}/backend.port"
echo "${H5_PORT}" > "${RUNTIME_DIR}/h5.port"
echo "${ADMIN_PORT}" > "${RUNTIME_DIR}/admin.port"

if [[ "${RUN_MODE}" == "full" ]]; then
  echo "[1/5] 启动本地 MySQL 与 Redis"
  docker compose -f "${ROOT_DIR}/ops/docker/docker-compose.local.yml" up -d
else
  echo "[1/5] 使用 demo 模式启动，不依赖 Docker 数据库"
fi

echo "[2/5] 启动 Spring Boot 后端"
if [[ -f "${RUNTIME_DIR}/backend.pid" ]] && kill -0 "$(cat "${RUNTIME_DIR}/backend.pid")" 2>/dev/null; then
  echo "后端已在运行，跳过"
else
  (
    cd "${ROOT_DIR}/server"
    nohup env SPRING_PROFILES_ACTIVE="${RUN_MODE}" SERVER_PORT="${BACKEND_PORT}" ./mvnw spring-boot:run > "${RUNTIME_DIR}/backend.log" 2>&1 &
    echo $! > "${RUNTIME_DIR}/backend.pid"
  )
fi

echo "[3/5] 启动 H5 前端"
if [[ -f "${RUNTIME_DIR}/h5.pid" ]] && kill -0 "$(cat "${RUNTIME_DIR}/h5.pid")" 2>/dev/null; then
  echo "H5 已在运行，跳过"
else
  (
    cd "${ROOT_DIR}/client-h5"
    nohup env VITE_API_BASE_URL="http://127.0.0.1:${BACKEND_PORT}/api" pnpm dev --host 0.0.0.0 --port "${H5_PORT}" > "${RUNTIME_DIR}/h5.log" 2>&1 &
    echo $! > "${RUNTIME_DIR}/h5.pid"
  )
fi

echo "[4/5] 启动后台前端"
if [[ -f "${RUNTIME_DIR}/admin.pid" ]] && kill -0 "$(cat "${RUNTIME_DIR}/admin.pid")" 2>/dev/null; then
  echo "后台已在运行，跳过"
else
  (
    cd "${ROOT_DIR}/admin-web"
    nohup env VITE_API_BASE_URL="http://127.0.0.1:${BACKEND_PORT}/api" pnpm dev --host 0.0.0.0 --port "${ADMIN_PORT}" > "${RUNTIME_DIR}/admin.log" 2>&1 &
    echo $! > "${RUNTIME_DIR}/admin.pid"
  )
fi

echo "[5/5] 当前访问地址"
echo "H5: http://localhost:${H5_PORT}"
echo "后台: http://localhost:${ADMIN_PORT}"
echo "后端: http://localhost:${BACKEND_PORT}"
echo "健康检查: http://localhost:${BACKEND_PORT}/api/health"
