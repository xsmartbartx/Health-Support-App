# CI/CD — GitHub Actions, ArgoCD i Helm

Skrócony opis flow i przykładowe workflowy.

## Zasady
- Separation: infra repo / services repo / charts repo
- Stages: build → test → SAST → image push → deploy staging → e2e → promote
- Use OIDC for AWS credentials in Actions

## Przykładowy workflow — CI
```yaml
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

## CD — promocja przez ArgoCD
ArgoCD powinien obserwować repo z chartami (GitOps). Promotion do prod powinna wymagać manualnego zatwierdzenia (RBAC) lub PR review.
