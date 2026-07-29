# Dokumentacja (index)

Ten katalog zawiera szczegółową dokumentację architektury, infrastruktury i procedur operacyjnych projektu.

Dla programisty - szybkie helpy:

- Lokalny dev: opcje uruchomienia usług zależnych (docker-compose), przykładowe polecenia build/test i zmienne środowiskowe.
- Infra: jak przygotować i przetestować Terraform lokalnie (terraform init -backend=false; fmt; validate), gdzie są moduły, i jak konfigurować backend S3/Dynamo.
- CI/CD: jak działa pipeline (build → SAST → SCA → push → ArgoCD), oraz jak uruchamiać workflowy lokalnie dla testów.
- DB: instrukcje migracji (Flyway/Liquibase) i jak robić restore drills lokalnie.
- Observability: jak uruchomić Prometheus/Grafana/Tempo lokalnie do developmentu.

Szybkie linki

- Architecture — architecture/README.md
- Infra / Terraform — infra/aws-terraform.md, infra/terraform.md
- CI/CD — ci-cd/workflows.md, ci-cd/full-ci-cd.md
- Database & Migrations — db/migrations.md
- Observability — observability/README.md
- Security & IR — security/siem-soar.md, security/ir-playbooks.md
- Operations / Backup — operations/ops-compliance-backup.md
- Attachments & Diagrams — attachments.md
- Alternatives (płatne → OSS) — alternatives.md

Developer checklist

- Skonfiguruj lokalne środowisko: Docker, Java/Gradle, Node (jeśli potrzebne), narzędzia terraform.
- Uruchom lokalne zależności (docker-compose) przed uruchomieniem serwisów.
- Uruchom testy jednostkowe i integracyjne lokalnie przed PR.
- Przeprowadź skan sekretów (truffleHog/git-secrets) lokalnie.


## Copilot chats

- Copilot chats: docs/copilot_chats/NWP3z7ip8LENiqYZ6NeW3.md


## AI Security Integration

See docs/security/ai-integration.md for principles and guardrails for safe AI integration in security operations.


## Consolidated documentation split

The consolidated documentation has been split into per-section files for easier navigation:

- [Consolidated Documentation Index](./CONSOLIDATED_SPLIT_INDEX.md)
