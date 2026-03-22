#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

echo "[1/2] 启动本地 MySQL 与 Redis"
docker compose -f "${ROOT_DIR}/ops/docker/docker-compose.local.yml" up -d

echo "[2/2] 启动 Spring Boot 服务"
cd "${ROOT_DIR}/server"
./mvnw spring-boot:run
