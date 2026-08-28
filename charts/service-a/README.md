# service-a Helm chart

Usage:

- Build the Docker image locally and tag it (or push to registry).
- Install into kind (see scripts/kind-load-and-install.sh).

Override the image repository & tag in values.yaml or with `--set image.repository=... --set image.tag=...`.