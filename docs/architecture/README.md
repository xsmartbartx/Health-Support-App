# Architecture

Full architecture details plus practical developer guidance for building and running the system locally and in staging.

Developer-focused notes

1) Local run patterns

- For local development prefer running single service + local dependencies (Postgres, Redis). Use docker-compose config in infra/dev-docker-compose.yml (if present) or create one using images defined in services' Dockerfiles.
- Keep env file per-service: services/<service>/.env.example → copy to .env and update secrets.

2) Building images

- Build and run locally with Docker:

```bash
# from repo root
docker build -t registry.example.com/service:local -f services/serviceA/Dockerfile services/serviceA
docker run --env-file services/serviceA/.env -p 8080:8080 registry.example.com/service:local
```

3) Helm charts and local Kubernetes

- For integration tests, use kind or k3d to create a local cluster and deploy charts with Helm:

```bash
kind create cluster --name dev
helm upgrade --install serviceA charts/serviceA -n staging --create-namespace --set image.tag=local
```

4) Environment & secrets

- Do not store secrets in repo. Use .env files for local dev (excluded via .gitignore). For staging/prod use AWS Secrets Manager / Vault.

5) CI/CD notes

- CI builds images and pushes to registry; CD via ArgoCD reads Helm chart values from gitops repo. Use OIDC for short-lived AWS creds in Actions.

