# Health-Support-App

Krótki opis
Aplikacja wspierająca opiekę zdrowotną — repozytorium zawiera opis architektury, przykładowe artefakty IaC (Terraform), przykładowe workflowy CI/CD (GitHub Actions + ArgoCD/Helm) oraz playbooki reagowania na incydenty.

Badges
- CI: ![CI](https://img.shields.io/badge/ci-not-configured-lightgrey)
- License: ![License](https://img.shields.io/badge/license-MIT-blue)

Spis treści
1. [Szybkie wprowadzenie](#szybkie-wprowadzenie)
2. [Szybki start](#szybki-start)
3. [Architektura (skrót)](#architektura-skrót)
4. [Dokumentacja szczegółowa](#dokumentacja-szczegółowa)
5. [Kontrybucja](#kontrybucja)
6. [Bezpieczeństwo](#bezpiecze%C5%84stwo)
7. [Licencja i kontakt](#licencja-i-kontakt)

## Szybkie wprowadzenie
To repozytorium pełni rolę "landing page" dla projektów Health-Support-App. Długie, szczegółowe sekcje architektury zostały przeniesione do katalogu `docs/` i rozbite na osobne pliki, dzięki czemu README jest czytelne i szybkie do przeglądu.

## Szybki start
Wymagania (przykładowe)
- Terraform >= 1.5
- kubectl >= 1.26
- Helm >= 3.8
- Java 17 (dla przykładów usług)
- Docker (lokalny build)

Przykładowy lokalny build (service)
```bash
git clone https://github.com/xsmartbartx/Health-Support-App.git
cd Health-Support-App/services/example-service
./gradlew build
docker build -t registry.example.com/example-service:local .
```

Inicjalizacja Terraform (env dev)
```bash
cd infra/envs/dev
terraform init
terraform plan -out=tfplan
terraform apply tfplan
```

## Architektura (skrót)
- Frontend (CDN) → API Gateway → EKS (Ingress) → mikroserwisy
- Persistencja: PostgreSQL, Redis; Observability: Prometheus, Grafana, OpenTelemetry
- CI/CD: GitHub Actions (build/SAST/SCA) → registry → ArgoCD (promotion)

Pełna dokumentacja jest w katalogu `docs/architecture/`.

## Dokumentacja szczegółowa
Zobacz:
- docs/architecture/README.md — pełna architektura (podzielona na sekcje)
- docs/infra/terraform.md — przykłady i konwencje Terraform
- docs/ci-cd/workflows.md — GitHub Actions i ArgoCD
- docs/security/ir-playbooks.md — playbooki IR i procedury

## Kontrybucja
Zanim wyślesz PR:
- Uruchom testy lokalnie.
- Sprawdź lintery i format (terraform fmt, helm lint, ./gradlew check).
- Użyj szablonu PR (`.github/PULL_REQUEST_TEMPLATE`).

Dodaj pliki: CONTRIBUTING.md, CODE_OF_CONDUCT.md, SECURITY.md (są przykłady w tym branchu).

## Bezpieczeństwo
- Nie umieszczaj sekretów w repo. Używaj Vault / AWS Secrets Manager.
- W CI korzystaj z OIDC zamiast długotrwałych kluczy.
- Jeśli znajdziesz podatność — zobacz SECURITY.md.

## Licencja i kontakt
- Licencja: MIT (plik LICENSE)
- Kontakt: maintainers@localhost (zastąp realnym adresem w konfiguracji)
