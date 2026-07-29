# Health-Support-App — Complete Consolidated Documentation


# Health-Support-App â€” Kompletny przewodnik (skondensowany)

KrÃ³tki opis

Projekt: Health-Support-App â€” aplikacja wspierajÄ…ca opiekÄ™ zdrowotnÄ…. Ten dokument to skondensowane, Å‚atwe do czytania i przeszukiwania centrum dokumentacji: quickstart, architektura, infra, CI/CD, migracje DB, obserwowalnoÅ›Ä‡, bezpieczeÅ„stwo (w tym zasady integracji AI), procedury operacyjne oraz checklisty dla deweloperÃ³w i zespoÅ‚u bezpieczeÅ„stwa.

Spis treÅ›ci

- Quickstart
- Cel i motywacja
- Architektura â€” skrÃ³t
- Infrastruktura (Terraform, best practices)
- Kubernetes & Helm
- CI/CD (GitHub Actions + lokalne testy)
- Baza danych i migracje
- ObserwowalnoÅ›Ä‡ (Prometheus/Grafana/Loki/Tempo)
- BezpieczeÅ„stwo (SIEM/SOAR, IR, AI integration guardrails)
- Operacje i runbooki
- Checklisty dla deweloperÃ³w i zespoÅ‚u bezpieczeÅ„stwa
- Podsumowanie i nastÄ™pne kroki



---


# Quickstart (5 minut)

Quickstart (5 minut)

1. Klon repo:

   git clone https://github.com/xsmartbartx/Health-Support-App.git
   cd Health-Support-App
   git checkout docs/readme-refactor

2. Uruchom lokalnie zaleÅ¼noÅ›ci przez docker-compose (przykÅ‚ad):

   docker-compose -f docker/docker-compose.dev.yml up --build

3. Build i testy (przykÅ‚ad dla gradle/multi-module):

   ./gradlew clean build
   ./gradlew test

4. Migracje DB (lokalny Postgres):

   docker run --name hs-postgres -e POSTGRES_PASSWORD=pass -p 5432:5432 -d postgres:14
   ./gradlew flywayMigrate

5. OtwÃ³rz usÅ‚ugÄ™: http://localhost:8080

(Uwaga: dostosuj porty i komendy do rzeczywistego stacka projektu.)



---


# Cel i motywacja

Cel i motywacja

KrÃ³tkie, przyjazne dla czytelnika centrum dokumentacji upraszcza onboardingu, przyspiesza debug oraz uÅ‚atwia wprowadzanie zmian. Dla dÅ‚ugich, szczegÃ³Å‚owych materiaÅ‚Ã³w (playbooki, peÅ‚na architektura, przykÅ‚ady Terraform, procedury IR) podlinkowano istniejÄ…ce pliki w folderze docs/.



---


# Infrastruktura (Terraform)

Infrastruktura (Terraform)

NajwaÅ¼niejsze praktyki:

- Struktura: modules/ shared modules, envs/<dev|staging|prod>/
- Backend: zewnÄ™trzny state (S3 + DynamoDB lock) dla AWS
- Local dev: terraform init -backend=false; terraform plan -out=tfplan; terraform apply tfplan
- Weryfikacja: terraform fmt, terraform validate, tfsec (SAST infra)

PrzykÅ‚ad inicjalizacji (dev):

cd infra/envs/dev
terraform init -backend=false
terraform fmt -recursive
terraform validate
terraform plan -out=tfplan
# review tfplan, then
terraform apply tfplan

Sekrety:
- Nie trzymaÄ‡ sekretÃ³w w plaintext w repo
- UÅ¼ywaÄ‡ SOPS/age lub Vault



---


# Kubernetes & Helm

Kubernetes & Helm

- Lokalny development: kind lub k3d
- Helm charts: umieÅ›Ä‡ charts/ w repo lub korzystaj z zewnÄ™trznych repo
- Lint: helm lint ./charts/<chart>
- Example: helm upgrade --install app ./charts/app -f values.dev.yaml



---


# CI/CD

CI/CD

- GitHub Actions: pipeliney do build/test, SAST, SCA, publish container
- Local debug: instalacja act do lokalnego uruchamiania workflowÃ³w
- Promotion: ArgoCD automatycznie synchronizuje obrazy ze Å›rodowiskami po tagowaniu lub merge

PrzykÅ‚adowe kroki pipeline:
- checkout
- set up JDK/node/python
- cache deps
- build
- unit tests
- SAST (semgrep/tfsec/trivy)
- publish container
- open PR -> merge -> ArgoCD promuje



---


# Baza danych i migracje

Baza danych i migracje

- NarzÄ™dzia: Flyway lub Liquibase
- Praktyka: migracje w repo (sql/ lub scripts/), wersjonowane wraz z kodem
- Lokalne migracje: uruchomiÄ‡ DB w docker i wykonaÄ‡ migracje przed testami
- Backup: regularne snapshoty (pg_dump/point-in-time) i test restore



---


# ObserwowalnoÅ›Ä‡

ObserwowalnoÅ›Ä‡

- Metryki: Prometheus + Grafana (dashboards), zbieranie z aplikacji przez OTLP
- Logging: Loki / centralized logging (lokalne docker-compose dev stack)
- Tracing: OpenTelemetry -> Tempo/Jaeger

Szybkie uruchomienie lokalne (docker-compose):
- docker-compose -f infra/observability/docker-compose.yml up



---


# Operacje i runbooki

Operacje i runbooki

Podstawowe runbooki (skrÃ³t):

- Restore DB: kroki do restore z backupu, weryfikacja integracji
- Key rotation: kroki i harmonogram rotacji kluczy
- Incident response: triage â†’ contain â†’ eradicate â†’ recover â†’ postmortem

Zobacz docs/operations/ops-compliance-backup.md dla peÅ‚nych instrukcji



---


# ZawartoÅ›Ä‡ przeniesiona z Copilot chat (najwaÅ¼niejsze punkty)

ZawartoÅ›Ä‡ przeniesiona z Copilot chat (najwaÅ¼niejsze punkty)

- Metoda Study Mode: krok po kroku uczyÄ‡ reguÅ‚y bezpieczeÅ„stwa (kluczowa zasada, dlaczego, co AI moÅ¼e, co AI nie moÅ¼e, mini-pytanie, pauza)
- Kluczowe zasady: AI tylko opisuje, nie modyfikuje; logi i RBAC muszÄ… byÄ‡ deterministyczne i niezmienialne
- DziaÅ‚ania do wykonania: dodanie AI integration guidelines, testÃ³w bezpieczeÅ„stwa, audit trail dla rekomendacji AI



---


# Pliki do aktualizacji (sugerowane)

Pliki do aktualizacji (sugerowane)

- docs/security/ai-integration.md (peÅ‚ne wytyczne) â€” juÅ¼ istnieje; sprawdÅº i uzupeÅ‚nij
- docs/README.md â€” skrÃ³cone odnoÅ›niki i TOC (aktualne)
- docs/copilot_chats/NWP3z7ip8LENiqYZ6NeW3.md â€” peÅ‚en transcript i notatki (czÄ™Å›ciowo wstawione)



---


# Kontakt i autorska nota

Kontakt i autorska nota

Maintainer: maintainer-email@example.com â€” aktualizuj wedÅ‚ug potrzeb.



---


# Plik skompilowany automatycznie na podstawie istniejÄ…cych dokumentÃ³w w docs/ oraz notatek z copilot chat. JeÅ›li chcesz, rozbijÄ™ go ponownie na mniejsze pliki lub wygenerujÄ™ PDF/HTML dla wygodnego przeglÄ…dania.

Plik skompilowany automatycznie na podstawie istniejÄ…cych dokumentÃ³w w docs/ oraz notatek z copilot chat. JeÅ›li chcesz, rozbijÄ™ go ponownie na mniejsze pliki lub wygenerujÄ™ PDF/HTML dla wygodnego przeglÄ…dania.



---


