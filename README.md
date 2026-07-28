# Health-Support-App
Medical App

 Architektura Systemu — [Nazwa Projektu]

---

## Spis treści
1. Wprowadzenie  
2. Zakres i cele  
3. Słownik pojęć i skróty  
4. Wymagania niefunkcjonalne  
5. Przegląd architektury  
6. Warstwy systemu  
7. Komponenty i moduły  
8. Integracje zewnętrzne  
9. Infrastruktura i środowiska (AWS Terraform)  
10. CI CD i pipeline (GitHub Actions + ArgoCD / Helm)  
11. Baza danych i migracje  
12. Konfiguracja i zarządzanie sekretami (Vault + KMS)  
13. RBAC i zarządzanie tożsamością (AWS IAM + OIDC)  
14. Monitoring i obserwowalność (OpenTelemetry, Prometheus, Grafana)  
15. Alertowanie i eskalacja  
16. Bezpieczeństwo aplikacji i infra (SAST/DAST, WAF, EDR)  
17. Wykrywanie i reagowanie na incydenty (SIEM, SOAR, Playbooki IR)  
18. Korelacja retencja i archiwizacja (S3 WORM, Glacier, checksums)  
19. Automatyczne blokady i polityki (deterministyczne, human-in-loop)  
20. Testy i walidacja (chaos, perf, security)  
21. Plan wdrożenia i rollback  
22. Zarządzanie kosztami i limity  
23. Compliance i audyt  
24. Backup i odzyskiwanie  
25. SLA i umowy operacyjne  
26. Szkolenia i operacje zespołu  
27. Roadmapa rozwoju  
28. Załączniki i artefakty

---

# 1 Wprowadzenie

**Cel dokumentu**  
Kompletny master‑dokument architektury Enterprise dla projektu **[Nazwa Projektu]** z gotowymi fragmentami Terraform, GitHub Actions, Kubernetes/Helm, SIEM i playbookami IR. Dokument ma być kopiowalny do `ARCHITECTURE.md` i uzupełniany przez właścicieli sekcji.

**Zakres**  
Pełna architektura techniczna i operacyjna, gotowe szablony IaC i pipeline, oraz szczegółowe playbooki reagowania na incydenty.

---

# 2 Zakres i cele

**Cele biznesowe**  
- Bezpieczne i skalowalne środowisko produkcyjne.  
- Szybkie, powtarzalne wdrożenia z kontrolą jakości i bezpieczeństwa.  
- Zgodność z RODO i wymaganiami sektorowymi.

**Cele techniczne**  
- AWS jako główny cloud; Terraform jako IaC; Kubernetes (EKS) jako runtime; GitHub Actions jako CI; ArgoCD jako CD.  
- SIEM (Elastic/Splunk) + SOAR integracja; playbooki IR gotowe do użycia.

---

# 3 Słownik pojęć i skróty

- **IaC** Infrastructure as Code  
- **EKS** Elastic Kubernetes Service  
- **KMS** Key Management Service  
- **S3** Simple Storage Service  
- **SIEM** Security Information and Event Management  
- **SOAR** Security Orchestration Automation and Response

---

# 4 Wymagania niefunkcjonalne

- **Dostępność**: 99.95% dla krytycznych usług.  
- **Wydajność**: P95 latencji API < 300 ms.  
- **Bezpieczeństwo**: TLS 1.2+, AES-256 at rest, MFA dla adminów.  
- **Retencja**: operacyjne logi 90 dni; security logs 1 rok; dowody incydentów 7 lat.  
- **SLA**: SLOs i error budgets zdefiniowane per serwis.

---

# 5 Przegląd architektury

**Wysoki poziom**  
- Frontend (CDN) → API Gateway (ALB / NLB) → Ingress EKS → Mikroserwisy → DB/Cache.  
- Observability: Fluentd/Vector → S3 + SIEM; Prometheus → Grafana; OpenTelemetry traces → Jaeger/Tempo.  
- CI/CD: GitHub Actions → container registry → ArgoCD → EKS.  
- Security: WAF, Network ACLs, Security Groups, EDR, Vault + KMS.

---

# 6 Warstwy systemu

- **Prezentacja**: CDN, SPA/SSR.  
- **Aplikacja**: mikroserwisy kontenerowe.  
- **Integracja**: Kafka / SQS / SNS.  
- **Dane**: PostgreSQL, Redis, ClickHouse/BigQuery.  
- **Bezpieczeństwo**: WAF, IDS/IPS, SIEM.

---

# 7 Komponenty i moduły

- **API Gateway**: rate limiting, auth, TLS termination.  
- **Auth Service**: OIDC, JWT, token refresh.  
- **User Service, Billing Service, Audit Service**: kontrakty API, idempotencja.  
- **Workers**: background jobs, DLQ.  
- **Observability**: metrics, logs, traces.  
- **Security**: Vault, KMS, WAF, EDR, SIEM.

---

# 8 Integracje zewnętrzne

- **Płatności**: tokenizacja, PCI scope minimization.  
- **Email/SMS**: provider z retry i idempotency.  
- **KYC**: minimal data retention, DPA.  
- **Monitoring**: external APM, Synthetics.

---

# 9 Infrastruktura i środowiska — AWS Terraform

## 9.1 Zasady i konwencje
- **Konta AWS**: oddzielne konta dla DEV / QA / STAGING / PROD.  
- **Moduły Terraform**: moduły współdzielone w `git:://infra-modules`.  
- **State**: backend S3 + DynamoDB lock.  
- **Policy as Code**: OPA / Sentinel w pipeline.

## 9.2 Struktura repo
infra/
├─ modules/
│   ├─ vpc/
│   ├─ eks/
│   ├─ rds/
│   └─ iam/
├─ envs/
│   ├─ dev/
│   ├─ staging/
│   └─ prod/
└─ pipelines/

Code

## 9.3 Kluczowe moduły i konfiguracje

### Terraform backend (S3 + DynamoDB)
```hcl
terraform {
  backend "s3" {
    bucket = "project-terraform-state"
    key    = "envs/prod/terraform.tfstate"
    region = "eu-central-1"
    dynamodb_table = "terraform-locks"
    encrypt = true
  }
}
VPC module snippet
hcl
module "vpc" {
  source = "git::ssh://git@example.com/infra/modules.git//vpc"
  name   = "project-vpc"
  cidr   = "10.10.0.0/16"
  public_subnets  = ["10.10.1.0/24","10.10.2.0/24"]
  private_subnets = ["10.10.10.0/24","10.10.11.0/24"]
}
EKS module snippet
hcl
module "eks" {
  source = "git::ssh://git@example.com/infra/modules.git//eks"
  cluster_name = "project-eks"
  vpc_id       = module.vpc.vpc_id
  subnets      = module.vpc.private_subnets
  node_groups = {
    app = { desired_capacity = 3, instance_type = "t3.medium" }
  }
  oidc_provider = true
}
9.4 Sieć i bezpieczeństwo
Public / Private subnets; NAT Gateway dla prywatnych.

Bastion host w oddzielnym security group z MFA i ograniczonym dostępem.

Flow logs do S3 + SIEM.

Security Groups: minimalne reguły, no wide open ports.

9.5 Storage i backup
S3 z wersjonowaniem i lifecycle (transition to Glacier).

RDS snapshots automatyczne + manualne przed deployem.

S3 WORM dla dowodów incydentów (write once).

9.6 KMS i klucze
KMS CMKs per environment; key rotation enabled; access via IAM policies.

10 CI CD i pipeline — GitHub Actions + ArgoCD + Helm
10.1 Zasady
Separation: repo infra, repo services, repo charts.

Pipeline stages: build → unit tests → SAST → container scan → push → deploy staging → e2e → promote → deploy prod (manual gate).

Immutability: images immutable, tagged by SHA.

10.2 GitHub Actions — przykładowy workflow
yaml
name: CI

on:
  push:
    branches: [ "main", "develop" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK
        uses: actions/setup-java@v4
        with: java-version: '17'
      - name: Build
        run: ./gradlew build
      - name: Unit tests
        run: ./gradlew test
      - name: SAST scan
        uses: github/codeql-action/analyze@v2
      - name: Build and push image
        uses: docker/build-push-action@v4
        with:
          push: true
          tags: registry.example.com/service:${{ github.sha }}
10.3 CD — ArgoCD + Helm
ArgoCD watches gitops repo with Helm charts.

Promotion: staging auto, prod manual approval via ArgoCD App of Apps + RBAC.

Canary: use Argo Rollouts or Flagger for canary/blue-green.

Helm values example
yaml
replicaCount: 3
image:
  repository: registry.example.com/service
  tag: "sha-{{ .Values.imageTag }}"
resources:
  limits:
    cpu: "500m"
    memory: "512Mi"
10.4 Gate’y bezpieczeństwa
SAST/DAST must pass; SCA vulnerability threshold blocks merge.

Policy as Code: OPA checks in pipeline (e.g., disallow public S3 buckets).

10.5 Secrets in pipeline
Use GitHub Actions OIDC to request short‑lived credentials for AWS; avoid long‑lived secrets.

Secrets stored in Vault or GitHub Secrets with strict access.

11 Baza danych i migracje
Primary DB: Amazon RDS PostgreSQL Multi‑AZ.

Read replicas for heavy reads.

Migrations: use tools (Flyway / Liquibase) with backward compatible migrations and feature toggles.

Zero downtime: expand schema with additive changes, backfill in background, switch flags.

12 Konfiguracja i zarządzanie sekretami — Vault + KMS
12.1 Vault deployment
HashiCorp Vault in HA mode (consul or integrated storage) or cloud managed secrets (AWS Secrets Manager) with Vault as abstraction.

Auth methods: AWS IAM, Kubernetes auth (service account), OIDC for humans.

12.2 Best practices
Short lived credentials for DB and cloud APIs.

Automatic rotation for DB credentials and API keys.

Audit logs forwarded to SIEM.

13 RBAC i zarządzanie tożsamością
Least privilege model.

IdP: Azure AD / Okta with SSO, OIDC.

Admin roles require MFA and periodic access review.

Kubernetes RBAC: map IAM roles to K8s roles via IRSA / OIDC.

14 Monitoring i obserwowalność
Metrics: Prometheus (node, kube, app).

Logs: structured JSON logs → Fluentd/Vector → S3 + SIEM.

Tracing: OpenTelemetry → Tempo/Jaeger.

Dashboards: Grafana for SLOs; Kibana for logs.

15 Alertowanie i eskalacja
Alert rules: defined in Prometheus Alertmanager.

Channels: PagerDuty for pages; Slack for ops; Email for reports.

Runbooks: linked in alerts; include triage steps and rollback criteria.

16 Bezpieczeństwo aplikacji i infrastruktury
SAST/DAST/SCA in CI.

WAF in front of ALB; custom rules for OWASP Top 10.

EDR on bastion and critical hosts.

Network microsegmentation and security groups per service.

17 Wykrywanie i reagowanie na incydenty — SIEM, SOAR, Playbooki IR
17.1 SIEM architektura
Log ingestion: Fluentd/Vector → S3 (raw) → SIEM ingestion (Elastic/Splunk).

Parsing: JSON schemas, ECS/CEF mapping.

Enrichment: GeoIP, user directory, asset tagging.

Retention: hot 90 days, warm 1 year, cold archive 7 years.

17.2 Detection engineering
Detections: signature rules, statistical baselines, ML anomaly detectors (descriptive only).

Rule lifecycle: author → test in staging → tune → promote.

False positive tracking: ticketing and metrics.

17.3 SOAR and automation
SOAR playbooks for low‑risk containment (e.g., isolate host) but require human approval for high‑impact actions.

Audit trail for every automated action.

17.4 Playbook IR — minimal template
Triage
Ingest alert: capture alert id, source, timestamp.

Initial enrichment: user, host, process, network context.

Severity: map to severity matrix.

Containment
Short term: isolate host (network ACL), revoke sessions, block IP at WAF.

Long term: disable compromised accounts, rotate keys.

Eradication
Root cause analysis: identify vector, remove persistence.

Remediation: patch, config change, credential rotation.

Recovery
Restore services: from clean images/snapshots.

Validate: run integrity checks, monitoring.

Postmortem
Timeline: build timeline of events.

Lessons learned: update detections, playbooks, runbooks.

17.5 Evidence preservation
Write once storage for evidence (S3 WORM).

Checksums and signed manifests.

Chain of custody log: who accessed evidence, when, why.

18 Korelacja retencja i archiwizacja
Deterministyczne korelacje: rules based on timestamps, session ids, user ids, asset ids.

Retencja: logs 90 days hot, 1 year warm, 7 years cold for legal evidence.

Archiwizacja: S3 Glacier Deep Archive for long term; manifest + checksum.

19 Automatyczne blokady i polityki
Blocking rules: deterministic thresholds and signatures.

Human-in-loop: high impact blocks require manual approval; all blocks logged.

AI role: recommend actions, summarize context; never autonomously delete or alter evidence.

20 Testy i walidacja
Unit / Integration / E2E in CI.

Security: SAST/DAST, dependency scanning, container image scanning.

Chaos engineering: controlled experiments in staging.

DR drills: quarterly restore tests.

21 Plan wdrożenia i rollback
Wdrożenie

Deploy infra via Terraform to PROD account (with approvals).

Deploy EKS cluster and core services.

Deploy monitoring and SIEM collectors.

Deploy application via ArgoCD canary.

Validate smoke tests and SLOs.

Rollback

Criteria: SLO breach, critical errors, security incidents.

Mechanism: ArgoCD rollback to previous manifest; restore DB snapshot if needed.

22 Zarządzanie kosztami i limity
Budżet: cost owners per service; monthly reports.

Alerts: AWS budgets + cost anomaly detection.

Optimization: rightsizing, reserved instances, spot for non-critical workloads.

23 Compliance i audyt
RODO: data mapping, DPIAs, DPA with vendors.

ISO/NIST: control mapping and evidence collection.

Audits: quarterly internal, annual external.

24 Backup i odzyskiwanie
RPO/RTO per data class.

Backups: automated RDS snapshots, S3 versioning.

Restore drills: quarterly, documented.

25 SLA i umowy operacyjne
Public SLA for customers; internal SLOs for teams.

Error budget monitoring and burn rate alerts.

26 Szkolenia i operacje zespołu
Onboarding: access checklist, environment walkthrough.

IR exercises: tabletop twice a year; full scale annually.

Knowledge base: runbooks, playbooks, runbook repository.

27 Roadmapa rozwoju
Short term: infra stabilization, CI/CD maturity, baseline monitoring.

Medium term: multi-region, advanced analytics, automated compliance reporting.

Long term: ML features, cost optimization, platform hardening.

28 Załączniki i artefakty
Diagramy: diagrams/architecture-highlevel.drawio

Repozytoria: git@example.com:project/infra, git@example.com:project/services

Playbooki: docs/playbooks/ir.md

Contact list: docs/contacts.md

Załącznik A — Szczegóły techniczne AWS Terraform (pełny przykład)
IAM role for GitHub Actions OIDC
hcl
resource "aws_iam_role" "github_actions" {
  name = "github-actions-role"
  assume_role_policy = data.aws_iam_policy_document.github_assume_role.json
}

data "aws_iam_policy_document" "github_assume_role" {
  statement {
    effect = "Allow"
    principals {
      type = "Federated"
      identifiers = [aws_iam_openid_connect_provider.github.arn]
    }
    actions = ["sts:AssumeRoleWithWebIdentity"]
    condition {
      test = "StringEquals"
      values = ["repo:project/service:ref:refs/heads/main"]
      variable = "token.actions.githubusercontent.com:sub"
    }
  }
}
S3 bucket for logs with lifecycle
hcl
resource "aws_s3_bucket" "logs" {
  bucket = "project-logs-prod"
  versioning { enabled = true }
  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "aws:kms"
        kms_master_key_id = aws_kms_key.logs_key.arn
      }
    }
  }
  lifecycle_rule {
    id      = "archive"
    enabled = true
    transition {
      days          = 90
      storage_class = "GLACIER"
    }
  }
}
Załącznik B — GitHub Actions full CD example with OIDC and ArgoCD promotion
yaml
name: Release

on:
  workflow_dispatch:
    inputs:
      promote_to:
        description: 'target environment'
        required: true
        default: 'staging'

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Build
        run: ./gradlew build
      - name: Login to ECR
        uses: aws-actions/configure-aws-credentials@v2
        with:
          role-to-assume: arn:aws:iam::123456789012:role/github-actions-role
          aws-region: eu-central-1
      - name: Build and push
        uses: docker/build-push-action@v4
        with:
          push: true
          tags: ${{ env.REGISTRY }}/service:${{ github.sha }}

  promote:
    needs: build-and-push
    runs-on: ubuntu-latest
    steps:
      - name: Create ArgoCD Application PR
        run: |
          # update values.yaml with new image tag and create PR to gitops repo
Załącznik C — SIEM Detection Rule Example (Elastic Query DSL style)
json
{
  "query": {
    "bool": {
      "must": [
        { "match": { "event.action": "authentication_failure" } },
        { "range": { "@timestamp": { "gte": "now-1h" } } }
      ],
      "filter": [
        { "term": { "user.name": "root" } }
      ]
    }
  }
}
Opis: wykrywa powtarzające się nieudane logowania do konta uprzywilejowanego w ciągu ostatniej godziny; wymaga testów w staging i tuning.

Playbook IR — gotowy szablon do wklejenia
Playbook: Suspicious Privileged Login
Trigger: SIEM alert — repeated failed privileged login attempts

Severity: High

Steps

Triage

Capture alert id, timestamps, source IPs, user account.

Enrich with asset owner and recent changes.

Containment

Temporarily disable the account or force password reset.

Block source IP at WAF and network ACL.

Snapshot affected host(s).

Eradication

Investigate persistence mechanisms; remove malicious artifacts.

Rotate credentials and revoke tokens.

Recovery

Restore from clean snapshot if host compromised.

Validate service health and monitoring.

Postmortem

Build timeline, root cause, update detection rules and runbooks.

Notify stakeholders and update compliance artifacts.

Evidence Handling

Store logs and snapshots in S3 WORM with checksum and signed manifest.

Record chain of custody entries for each access.
