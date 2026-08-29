#!/usr/bin/env bash
set -euo pipefail

# Usage:
# ./scripts/kind-deploy-local.sh \
#    --image service-a:local \
#    --cluster kind \
#    --release service-a-dev \
#    --namespace default

# Defaults
IMAGE="${IMAGE:-service-a:local}"
CLUSTER="${CLUSTER:-kind}"
RELEASE="${RELEASE:-service-a-dev}"
NAMESPACE="${NAMESPACE:-default}"
CHART_PATH="${CHART_PATH:-charts/service-a}"
SERVICE_DIR="${SERVICE_DIR:-services/service-a}"
KIND_CREATE_TIMEOUT="${KIND_CREATE_TIMEOUT:-120}" # seconds

# parse args
while [[ $# -gt 0 ]]; do
  case "$1" in
    --image) IMAGE="$2"; shift 2;;
    --cluster) CLUSTER="$2"; shift 2;;
    --release) RELEASE="$2"; shift 2;;
    --namespace) NAMESPACE="$2"; shift 2;;
    --chart) CHART_PATH="$2"; shift 2;;
    --service-dir) SERVICE_DIR="$2"; shift 2;;
    --help|-h) echo "Usage: $0 [--image tag] [--cluster name] [--release name] [--namespace ns]"; exit 0;;
    *) echo "Unknown arg: $1"; exit 2;;
  esac
done

echo "Parameters:"
echo "  IMAGE=$IMAGE"
echo "  CLUSTER=$CLUSTER"
echo "  RELEASE=$RELEASE"
echo "  NAMESPACE=$NAMESPACE"
echo "  CHART_PATH=$CHART_PATH"
echo "  SERVICE_DIR=$SERVICE_DIR"
echo

# Preflight checks
command -v docker >/dev/null 2>&1 || { echo "docker is required but not found. Install Docker Desktop."; exit 1; }
command -v kind >/dev/null 2>&1 || { echo "kind is required but not found. Install via 'brew install kind' or https://kind.sigs.k8s.io/"; exit 1; }
command -v kubectl >/dev/null 2>&1 || { echo "kubectl is required but not found. Install via 'brew install kubectl' or https://kubernetes.io/docs/tasks/tools/"; exit 1; }
command -v helm >/dev/null 2>&1 || { echo "helm is required but not found. Install via 'brew install helm' or https://helm.sh/"; exit 1; }

# Ensure repository paths exist
if [[ ! -d "$SERVICE_DIR" ]]; then
  echo "Service directory not found: $SERVICE_DIR"
  echo "Run this script from the repository root or pass --service-dir"
  exit 1
fi
if [[ ! -d "$CHART_PATH" ]]; then
  echo "Helm chart path not found: $CHART_PATH"
  echo "Run this script from the repository root or pass --chart"
  exit 1
fi

# Build image
echo "Building Docker image $IMAGE from $SERVICE_DIR..."
docker build -t "$IMAGE" "$SERVICE_DIR"

# Ensure cluster exists; create if needed
if kind get clusters | grep -q "^${CLUSTER}$"; then
  echo "Kind cluster '${CLUSTER}' already exists."
else
  echo "Creating kind cluster '${CLUSTER}'..."
  kind create cluster --name "${CLUSTER}"
  echo "Waiting ${KIND_CREATE_TIMEOUT}s for cluster to be ready..."
  sleep 5
  # basic wait for nodes to be ready
  end=$((SECONDS + KIND_CREATE_TIMEOUT))
  while [ $SECONDS -lt $end ]; do
    if kubectl --context "kind-${CLUSTER}" get nodes >/dev/null 2>&1; then
      ready=$(kubectl --context "kind-${CLUSTER}" get nodes --no-headers 2>/dev/null | awk '{print $2}' | grep -c "Ready" || true)
      if [[ "$ready" -ge 1 ]]; then
        echo "Cluster nodes are Ready."
        break
      fi
    fi
    echo "Waiting for nodes to be Ready..."
    sleep 2
  done
fi

# Load image into kind
echo "Loading image into kind cluster '${CLUSTER}'..."
kind load docker-image "$IMAGE" --name "$CLUSTER"

# Create namespace if missing
if kubectl --context "kind-${CLUSTER}" get namespace "$NAMESPACE" >/dev/null 2>&1; then
  echo "Namespace '$NAMESPACE' already exists."
else
  echo "Creating namespace '$NAMESPACE'..."
  kubectl --context "kind-${CLUSTER}" create namespace "$NAMESPACE"
fi

# Helm upgrade/install
echo "Deploying Helm chart '$CHART_PATH' as release '$RELEASE' into namespace '$NAMESPACE'..."
helm upgrade --install "$RELEASE" "$CHART_PATH" \
  --namespace "$NAMESPACE" \
  --set image.repository="$(echo "$IMAGE" | sed 's/:.*$//')" \
  --set image.tag="$(echo "$IMAGE" | sed 's/^.*://')"

# Wait for deployment
DEPLOYMENT_NAME=$(kubectl --context "kind-${CLUSTER}" -n "$NAMESPACE" get deploy -l "app.kubernetes.io/instance=${RELEASE}" -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || true)
if [[ -z "$DEPLOYMENT_NAME" ]]; then
  # fallback to common name
  DEPLOYMENT_NAME="${RELEASE}"
fi

echo "Waiting for deployment '$DEPLOYMENT_NAME' rollout to complete in namespace '$NAMESPACE'..."
kubectl --context "kind-${CLUSTER}" -n "$NAMESPACE" rollout status deployment/"$DEPLOYMENT_NAME" --timeout=180s

echo "Deployment finished. Pods:"
kubectl --context "kind-${CLUSTER}" -n "$NAMESPACE" get pods -o wide

echo
echo "To test locally:"
echo "  kubectl --context kind-${CLUSTER} -n $NAMESPACE port-forward svc/$RELEASE 8080:8080"
echo "Then open http://localhost:8080/health and /actuator/prometheus if enabled."