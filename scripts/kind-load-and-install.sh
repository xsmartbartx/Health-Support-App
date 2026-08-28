#!/usr/bin/env bash
set -euo pipefail
RELEASE=${1:-service-a-dev}
CHART=charts/service-a
IMAGE=${2:-ghcr.io/${GITHUB_REPOSITORY_OWNER:-your-org}/service-a:local}

# Build image locally
docker build -t "$IMAGE" services/service-a

# Create a kind cluster if not exists
kind get clusters | grep -q kind || kind create cluster

# Load image into kind
kind load docker-image "$IMAGE" --name kind

# Install/upgrade helm chart
helm upgrade --install "$RELEASE" "$CHART" --set image.repository=$(echo "$IMAGE" | sed 's/:.*$//') --set image.tag=$(echo "$IMAGE" | sed 's/^.*://')