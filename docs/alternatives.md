# Alternatywy: płatne → darmowe / OSS
<a id="docs-alternatives"></a>

Poniżej szybkie, praktyczne rekomendacje zamienników dla płatnych rozwiązań.

| Funkcja | Płatne wcześniej | Darmowy / Open source zamiennik (rekomendacja) |
|---|---|---|
| Cloud (start) | AWS (managed heavy) | AWS Free Tier / Hetzner Cloud / lokalne VM + ngrok (dev) |
| Kubernetes | EKS (managed) | k3s / k0s (production-ready), dev: kind / minikube |
| IaC | Terraform (ok) | Terraform OSS (zalecane) — alternatywa: Pulumi OSS |
| CI | GitHub Actions (częściowo płatne) | GitHub Actions / Drone CI OSS / Tekton / GitLab CE |
| Container registry | ECR (płatne) | GitHub Container Registry / GitLab Container Registry / Harbor |
| CD / GitOps | ArgoCD (ok) | ArgoCD OSS / Flux |
| Secrets | HashiCorp Vault (może być płatny) | Vault OSS / SOPS + KMS / Sealed Secrets / External Secrets Operator |
| Logs / SIEM | Splunk / commercial SIEM | Elastic Stack / OpenSearch / Wazuh / TheHive + Cortex |
| SOAR | Commercial SOAR | TheHive + Cortex / Shuffle |
| Observability | Datadog | Prometheus + Grafana + Loki + Tempo + OpenTelemetry |
| Tracing | Commercial APM | Jaeger / Tempo + OpenTelemetry |
| WAF / Bot protection | Commercial WAF | ModSecurity + OWASP CRS; cloud WAF free tiers |
| EDR | Commercial EDR | OSQuery, Wazuh, Falco (K8s runtime security) |
| Backups / Archive | Managed backups | Restic + S3-compatible storage (Backblaze, Wasabi, MinIO) |
| Artifact scanning / SCA | Snyk | Trivy, Grype, OWASP Dependency-Check |
| Policy as Code | Sentinel | OPA (Open Policy Agent) + Gatekeeper |

> Uwaga: wybór zależy od wymagań produkcyjnych, skali i poziomu wsparcia. Tabela ma charakter praktycznych rekomendacji dla projektów, które chcą ograniczyć koszty lub preferują OSS.
