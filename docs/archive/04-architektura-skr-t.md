# Architektura — skrót

Architektura — skrót

Topologia:

- Frontend (CDN) -> API Gateway -> Ingress (Kubernetes/EKS) -> Mikroserwisy
- Persistencja: PostgreSQL (primary), Redis (cache)
- Observability: Prometheus, Grafana, Loki, Tempo, OpenTelemetry
- CI/CD: GitHub Actions -> Container Registry -> ArgoCD (promocje)
- Secrets: Vault (or SOPS/encrypted files)

Główne decyzje projektowe:

- Microservices w kontenerach, manifesty Helm dla K8s
- Infrastruktura zarządzana przez Terraform (moduły, state backend)
- Security-first: immutable logs, RBAC policy engine, audytowalny pipeline
