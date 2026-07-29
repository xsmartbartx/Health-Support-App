# Dokumentacja (index)

Ten katalog zawiera szczegółową dokumentację architektury, infrastruktury i procedur operacyjnych projektu.

Pełna architektura i materiały:

- Architecture
  - High-level architecture: Frontend (CDN) → API Gateway → EKS (Ingress) → mikroserwisy
  - Persistencja: PostgreSQL (RDS Multi-AZ), Redis (cache)
  - Observability: Prometheus, Grafana, Loki, Tempo, OpenTelemetry
  - CI/CD: GitHub Actions (build, SAST, SCA) → image registry → ArgoCD (gitops promotion)

- Infra / Terraform
  - Struktura repo: infra/modules (vpc, eks, rds, iam), infra/envs/{dev,staging,prod}
  - Backend: S3 + DynamoDB (locking), przykład konfiguracji w infra/aws-terraform.md
  - Przykładowe moduły VPC i EKS (seeded sources)

- CI/CD
  - Opis workflow: build → unit tests → SAST → SCA → image push → deploy staging → e2e → promote → deploy prod
  - Use OIDC for short-lived AWS creds in Actions; Gate: manual promotion to prod

- Database & Migrations
  - Narzędzia: Flyway lub Liquibase
  - Zasady: additive changes first, backfills as background jobs, test restore drills

- Observability
  - Collect metrics, logs, traces; SLO dashboards; instrument with OpenTelemetry SDK

- Security & IR
  - SIEM ingestion, detection engineering, SOAR playbooks, evidence preservation (S3 WORM)

- Operations
  - Backup strategy, RPO/RTO classification, cost ownership, runbooks, training

Pliki w tym katalogu:
- architecture/README.md — szczegółowa architektura i overview
- infra/aws-terraform.md — konfiguracje Terraform dla AWS (backend, moduły)
- ci-cd/full-ci-cd.md — przykładowe workflowy CI/CD
- db/migrations.md — migracje, zero-downtime
- observability/README.md — monitoring, logging, tracing
- security/siem-soar.md, security/ir-playbooks.md — wykrywanie i playbooki
- operations/ops-compliance-backup.md — operacje i backup
- alternatives.md — płatne → OSS rekomendacje

