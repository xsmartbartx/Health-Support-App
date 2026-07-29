# Health-Support-App — Kompletny przewodnik (skondensowany)

Krótki opis

Projekt: Health-Support-App — aplikacja wspierająca opiekę zdrowotną. Ten dokument to skondensowane, łatwe do czytania i przeszukiwania centrum dokumentacji: quickstart, architektura, infra, CI/CD, migracje DB, obserwowalność, bezpieczeństwo (w tym zasady integracji AI), procedury operacyjne oraz checklisty dla deweloperów i zespołu bezpieczeństwa.

Spis treści

- Quickstart
- Cel i motywacja
- Architektura — skrót
- Infrastruktura (Terraform, best practices)
- Kubernetes & Helm
- CI/CD (GitHub Actions + lokalne testy)
- Baza danych i migracje
- Obserwowalność (Prometheus/Grafana/Loki/Tempo)
- Bezpieczeństwo (SIEM/SOAR, IR, AI integration guardrails)
- Operacje i runbooki
- Checklisty dla deweloperów i zespołu bezpieczeństwa
- Podsumowanie i następne kroki

---

Quickstart (5 minut)

1. Klon repo:

   git clone https://github.com/xsmartbartx/Health-Support-App.git
   cd Health-Support-App
   git checkout docs/readme-refactor

2. Uruchom lokalnie zależności przez docker-compose (przykład):

   docker-compose -f docker/docker-compose.dev.yml up --build

3. Build i testy (przykład dla gradle/multi-module):

   ./gradlew clean build
   ./gradlew test

4. Migracje DB (lokalny Postgres):

   docker run --name hs-postgres -e POSTGRES_PASSWORD=pass -p 5432:5432 -d postgres:14
   ./gradlew flywayMigrate

5. Otwórz usługę: http://localhost:8080

(Uwaga: dostosuj porty i komendy do rzeczywistego stacka projektu.)

---

Cel i motywacja

Krótkie, przyjazne dla czytelnika centrum dokumentacji upraszcza onboardingu, przyspiesza debug oraz ułatwia wprowadzanie zmian. Dla długich, szczegółowych materiałów (playbooki, pełna architektura, przykłady Terraform, procedury IR) podlinkowano istniejące pliki w folderze docs/.

---

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

---

Infrastruktura (Terraform)

Najważniejsze praktyki:

- Struktura: modules/ shared modules, envs/<dev|staging|prod>/
- Backend: zewnętrzny state (S3 + DynamoDB lock) dla AWS
- Local dev: terraform init -backend=false; terraform plan -out=tfplan; terraform apply tfplan
- Weryfikacja: terraform fmt, terraform validate, tfsec (SAST infra)

Przykład inicjalizacji (dev):

cd infra/envs/dev
terraform init -backend=false
terraform fmt -recursive
terraform validate
terraform plan -out=tfplan
# review tfplan, then
terraform apply tfplan

Sekrety:
- Nie trzymać sekretów w plaintext w repo
- Używać SOPS/age lub Vault

---

Kubernetes & Helm

- Lokalny development: kind lub k3d
- Helm charts: umieść charts/ w repo lub korzystaj z zewnętrznych repo
- Lint: helm lint ./charts/<chart>
- Example: helm upgrade --install app ./charts/app -f values.dev.yaml

---

CI/CD

- GitHub Actions: pipeliney do build/test, SAST, SCA, publish container
- Local debug: instalacja act do lokalnego uruchamiania workflowów
- Promotion: ArgoCD automatycznie synchronizuje obrazy ze środowiskami po tagowaniu lub merge

Przykładowe kroki pipeline:
- checkout
- set up JDK/node/python
- cache deps
- build
- unit tests
- SAST (semgrep/tfsec/trivy)
- publish container
- open PR -> merge -> ArgoCD promuje

---

Baza danych i migracje

- Narzędzia: Flyway lub Liquibase
- Praktyka: migracje w repo (sql/ lub scripts/), wersjonowane wraz z kodem
- Lokalne migracje: uruchomić DB w docker i wykonać migracje przed testami
- Backup: regularne snapshoty (pg_dump/point-in-time) i test restore

---

Obserwowalność

- Metryki: Prometheus + Grafana (dashboards), zbieranie z aplikacji przez OTLP
- Logging: Loki / centralized logging (lokalne docker-compose dev stack)
- Tracing: OpenTelemetry -> Tempo/Jaeger

Szybkie uruchomienie lokalne (docker-compose):
- docker-compose -f infra/observability/docker-compose.yml up

---

Bezpieczeństwo

W skrócie (pełne szczegóły w docs/security/):

- RBAC: zarządzanie przez centralny policy engine (nie przez AI)
- Logs: niezmienialność (WORM, checksums), wszystkie AI rekomendacje logowane
- Risk scoring: rule-based engine, AI tylko sugeruje i opisuje
- Correlation: reguły, heurystyki i sygnatury; AI nie decyduje korelacji
- Evidence: chain of custody, checksums, niezmienność
- Secrets: Vault / SOPS
- SCA/SAST: Trivy, Semgrep, Grype w CI

AI Integration Guardrails (skondensowane — na podstawie Copilot chat)

- AI role: opis, interpretacja, rekomendacje, sugerowane remediacje
- AI nie może: modyfikować RBAC, usuwać/zmieniać logów, nadpisywać scoringu ryzyka, decydować korelacji incydentów, auto-remediować krytycznych działań bez ludzkiego zatwierdzenia
- Audyt: każda rekomendacja AI logowana z metadanymi (co, kiedy, dlaczego, dowody)
- Human-in-the-loop: zatwierdzenie wymagane dla wysokiego i krytycznego ryzyka

Szybka lista kontroli AI-security:
- RBAC: policy engine
- Logging: WORM/immutable storage
- Risk: rule-based scoring
- Correlation: deterministic rules
- Evidence: preserved and checksumed

---

Operacje i runbooki

Podstawowe runbooki (skrót):

- Restore DB: kroki do restore z backupu, weryfikacja integracji
- Key rotation: kroki i harmonogram rotacji kluczy
- Incident response: triage → contain → eradicate → recover → postmortem

Zobacz docs/operations/ops-compliance-backup.md dla pełnych instrukcji

---

Checklisty dla deweloperów

Przed PR:
- [ ] Uruchom testy lokalnie
- [ ] Uruchom lintery i SAST
- [ ] Upewnij się, że nie ma sekretów w zmianach
- [ ] Wypełnij PR template

Dla release:
- [ ] Zaktualizuj changelog
- [ ] Zaktualizuj tag i release notes
- [ ] Przeprowadź smoke tests na staging

---

Zawartość przeniesiona z Copilot chat (najważniejsze punkty)

- Metoda Study Mode: krok po kroku uczyć reguły bezpieczeństwa (kluczowa zasada, dlaczego, co AI może, co AI nie może, mini-pytanie, pauza)
- Kluczowe zasady: AI tylko opisuje, nie modyfikuje; logi i RBAC muszą być deterministyczne i niezmienialne
- Działania do wykonania: dodanie AI integration guidelines, testów bezpieczeństwa, audit trail dla rekomendacji AI

---

Pliki do aktualizacji (sugerowane)

- docs/security/ai-integration.md (pełne wytyczne) — już istnieje; sprawdź i uzupełnij
- docs/README.md — skrócone odnośniki i TOC (aktualne)
- docs/copilot_chats/NWP3z7ip8LENiqYZ6NeW3.md — pełen transcript i notatki (częściowo wstawione)

---

Następne kroki / Action items

1. Przejrzeć ten skondensowany dokument i wskazać brakujące fragmenty specyficzne dla repo (np. dokładne komendy build, porty, nazwy usług)
2. Uzupełnić przykłady Terraform/Helm z rzeczywistymi modułami repo
3. Dodać dokładne dashboardy Grafana i przykłady queries Prometheus
4. Dodać pełny transcript Copilot chat (jeśli chcesz, wstawię cały tekst)

---

Kontakt i autorska nota

Maintainer: maintainer-email@example.com — aktualizuj według potrzeb.

---

Plik skompilowany automatycznie na podstawie istniejących dokumentów w docs/ oraz notatek z copilot chat. Jeśli chcesz, rozbiję go ponownie na mniejsze pliki lub wygeneruję PDF/HTML dla wygodnego przeglądania.
