#!/usr/bin/env bash
set -euo pipefail

# Build & run using dev docker-compose
docker compose -f infra/dev-docker-compose.yml up --build --force-recreate
