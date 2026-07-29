# CI/CD — GitHub Actions, ArgoCD, Helm — pełne przykłady

Sekcja opisuje szczegółowo workflowy CI i CD, gate'y bezpieczeństwa oraz promocję przez GitOps.

10.1 Zasady
- Separation: repozytoria infra, services, charts.
- Stages: build → unit tests → SAST → SCA → image push → deploy staging → e2e → promote → deploy prod (manual gate).
- Immutability: obrazy oznaczane SHA.
- Use OIDC for short-lived AWS creds in Actions.

10.2 Przykładowy workflow — CI (rozszerzony)
```yaml
name: CI

on:
  push:
    branches: ["main","develop"]
  pull_request:
    branches: ["main"]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '17'
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
```

10.3 Release workflow (CD) — skrót

Job build-and-push: buduje i wypycha obraz (używając OIDC do uwierzytelnienia w ECR).
Job promote: aktualizuje repo gitops (Helm values) i tworzy PR do repo chartów. ArgoCD obserwuje repo z chartami i synchronizuje.

10.4 Gate'y bezpieczeństwa

Pipeline blokuje PRy jeśli SAST/DAST/SCA przekroczą threshold.
Policy as code (OPA) wstawiony do pipeline — np. zakaz publicznych S3.

10.5 Secrets

GitHub Actions OIDC zamiast long-lived secrets.
Vault lub AWS Secrets Manager jako źródło sekretów w runtime.
Pliki z przykładami i szablonami workflow znajdują się w docs/ci-cd/examples/ (rozszerzaj w miarę potrzeb).
