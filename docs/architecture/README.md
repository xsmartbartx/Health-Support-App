# Architecture

Poniżej znajduje się pełny opis architektury systemu — komponenty, wzorce, zależności i rekomendacje projektowe.

1. High-level topology

- Frontend: statyczne zasoby serwowane z CDN (CloudFront lub alternatywa), z cache i krótkim TTL.
- Edge/API: API Gateway lub Load Balancer przyjmujący ruch i kierujący do warstwy aplikacji.
- Kubernetes: EKS (managed) jako główny runtime dla mikroserwisów. Zaletą EKS jest integracja z AWS IR oraz zarządzanie control plane; alternatywy: k3s/k0s dla tańszej produkcji lub kind/minikube dla deva.
- Ingress: kontroler ingress w klastrze (np. nginx-ingress, ingress-nginx, or Traefik) obsługujący TLS i routing.
- Mikroserwisy: dekompozycja domenowa; każdy serwis ma własne repo, image i Helm chart.
- Persistence: PostgreSQL (RDS Multi-AZ) jako primary DB, read replicas dla skalowania odczytów; Redis jako cache/session store.
- Observability: Prometheus (metrics), Grafana (dashboards), Loki/Fluentd (logs), Tempo/Jaeger (tracing) z instrumentacją OpenTelemetry.
- CI/CD: GitHub Actions do budowy i wstępnych kontroli (SAST, SCA), ArgoCD do GitOps i synchronizacji środowisk.

2. Infrastructure as Code (Terraform)

- Repo structure:
  - infra/modules/{vpc,eks,rds,iam}
  - infra/envs/{dev,staging,prod}
  - pipelines/

- Backend pattern: S3 bucket for tfstate + DynamoDB for locks. Use `terraform fmt` and `terraform validate` in CI.

- Example backend config (replace with org specifics):

```hcl
terraform {
  backend "s3" {
    bucket         = "project-terraform-state"
    key            = "envs/prod/terraform.tfstate"
    region         = "eu-central-1"
    dynamodb_table = "terraform-locks"
    encrypt        = true
  }
}
```

- Example module usage (VPC + EKS):

```hcl
module "vpc" {
  source = "git::ssh://git@example.com/infra/modules.git//vpc"
  name   = "project-vpc"
  cidr   = "10.10.0.0/16"
}

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
```

3. Storage & Backup

- S3 with versioning + lifecycle (GLACIER/DEEP_ARCHIVE) for long-term artifacts.
- Automatic RDS snapshots; perform manual snapshot before crucial changes.
- Use S3 WORM for evidence preservation where needed.

4. KMS & Keys

- Create CMK per environment; enable key rotation. Define access with least-privilege IAM policies.

5. Security notes

- Never use example ARNs, bucket names, or account IDs in production without substitution.
- Treat all example credentials as placeholders.

6. Notes on migrations & zero-downtime

- Use additive schema changes first; backfill via background jobs; switch traffic with feature flags.
- Tools: Flyway or Liquibase.

7. Operational considerations

- Define RPO/RTO per data class; schedule restore drills and document runbooks.
- Define cost-owners and monthly reporting; use AWS Budgets + anomaly detection.

8. Roadmap pointers

- Short-term: infra stabilization and docs consolidation.
- Medium: multi-region, observability improvements.
- Long-term: advanced analytics and ML features.

