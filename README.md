# Health Support App

**Aplikacja wspierająca opiekę zdrowotną** — enterprise-grade repo z kodem serwisów, infrastrukturą (Terraform, Kubernetes, CI/CD), dokumentacją techniczną i procedurami operacyjnymi.

---

## 🚀 Quick Start (5 min)

```bash
git clone https://github.com/xsmartbartx/Health-Support-App.git
cd Health-Support-App
docker-compose -f infra/dev-docker-compose.yml up -d
./gradlew build && ./gradlew bootRun
```

Aplikacja będzie dostępna na `http://localhost:8080`

---

## 📚 Dokumentacja

Pełna dokumentacja znajduje się w folderze `docs/`. Aby zacząć:

### Szybki dostęp:
- **[Kompletna dokumentacja (Markdown)](./docs/CONSOLIDATED_DOCUMENTATION_FULL.md)** — wszystko w jednym pliku
- **[Podzielona dokumentacja (sekcje)](./docs/CONSOLIDATED_SPLIT_INDEX.md)** — oddzielne pliki per temat
- **[HTML wersja](./docs/CONSOLIDATED_DOCUMENTATION.html)** — do przeglądania w przeglądarce

### Spis treści dokumentacji

1. **[Quickstart](./docs/02-quickstart-5-minut.md)** — konfiguracja i pierwsze uruchomienie
2. **[Cel projektu](./docs/03-cel-i-motywacja.md)** — motywacja i scope
3. **[Architektura](./docs/04-architektura-skrt.md)** — topologia, komponenty, decyzje projektowe
4. **[Infrastruktura (Terraform)](./docs/05-infrastruktura-terraform.md)** — IaC, best practices, moduły
5. **[Kubernetes & Helm](./docs/06-kubernetes-helm.md)** — manifesty, deployment, konfiguracja
6. **[CI/CD (GitHub Actions)](./docs/07-ci-cd.md)** — workflow, testowanie lokalne z `act`, promotion
7. **[Baza danych & migracje](./docs/08-baza-danych-i-migracje.md)** — Flyway/Liquibase, backup/restore
8. **[Obserwowalność (Observability)](./docs/09-obserwowalno.md)** — Prometheus, Grafana, Loki, Tempo, OpenTelemetry
9. **[Bezpieczeństwo](./docs/10-bezpieczestwo.md)** — SIEM/SOAR, IR playbooks, AI integration guardrails
10. **[Operacje & runbooki](./docs/11-operacje-i-runbooki.md)** — restore drills, key rotation, compliance
11. **[AI Security Integration](./docs/security/ai-integration.md)** — zasady bezpiecznej integracji AI
12. **[Copilot Chat (AI principles)](./docs/copilot_chats/NWP3z7ip8LENiqYZ6NeW3.md)** — RBAC, logging, risk scoring, incident correlation
13. **[OSS Alternatives](./docs/alternatives.md)** — mapowanie paid → free/open-source tools

---

## 🛠️ Setup dla różnych OS

### macOS / Linux

```bash
# zainstaluj requirements
brew install git docker docker-compose openjdk

# clone, build, run
git clone https://github.com/xsmartbartx/Health-Support-App.git
cd Health-Support-App
docker-compose -f infra/dev-docker-compose.yml up -d
./gradlew clean build test
./gradlew bootRun
```

### Windows (PowerShell)

```powershell
# zainstaluj requirements (np. Chocolatey)
choco install git docker-desktop openjdk

# clone, build, run
git clone https://github.com/xsmartbartx/Health-Support-App.git
cd Health-Support-App
docker-compose -f infra/dev-docker-compose.yml up -d
.\gradlew clean build test
.\gradlew bootRun
```

---

## 📋 Checklisty

### Dla nowych developerów

- [ ] Przeczytaj [Quickstart](./docs/02-quickstart-5-minut.md)
- [ ] Uruchom lokalnie aplikację
- [ ] Uruchom testy: `./gradlew test`
- [ ] Zapoznaj się z [Architekturą](./docs/04-architektura-skrt.md)
- [ ] Przejrzyj [Contributing.md](./CONTRIBUTING.md)

### Przed wysłaniem PR

- [ ] Testy przechodzą lokalnie: `./gradlew test`
- [ ] Lintery i SAST przechodzą: `./gradlew check`
- [ ] Brak sekretów w zmianach: `git-secrets` lub `truffleHog`
- [ ] Wypełniony PR template (link poniżej)
- [ ] Dokumentacja zaktualizowana (jeśli dotyczy)

### Dla zespołu ops/security

- [ ] Przeczytaj [Bezpieczeństwo & IR Playbooks](./docs/security/siem-soar.md)
- [ ] Sprawdź [Operacje & runbooki](./docs/11-operacje-i-runbooki.md)
- [ ] Poznaj [AI Security Integration](./docs/security/ai-integration.md)
- [ ] Zapoznaj się z [SECURITY.md](./SECURITY.md)

---

## 🤝 Contributing

Zanim wysłesz PR, przeczytaj:
- [CONTRIBUTING.md](./CONTRIBUTING.md) — wytyczne dla kontrybutorów
- [CODE_OF_CONDUCT.md](./CODE_OF_CONDUCT.md) — kodeks postępowania
- [SECURITY.md](./SECURITY.md) — raportowanie podatności

Template PR: [.github/PULL_REQUEST_TEMPLATE](./github/PULL_REQUEST_TEMPLATE)

---

## 📄 Licencja

Projekt udostępniany na licencji [MIT](./LICENSE). 

---

## 📞 Kontakt

Maintainer: maintainer-email@example.com

---

## 🔗 Szybkie linki

- Pełna dokumentacja: [docs/CONSOLIDATED_DOCUMENTATION_FULL.md](./docs/CONSOLIDATED_DOCUMENTATION_FULL.md)
- Dokumentacja HTML: [docs/CONSOLIDATED_DOCUMENTATION.html](./docs/CONSOLIDATED_DOCUMENTATION.html)
- GitHub: https://github.com/xsmartbartx/Health-Support-App
- Issues: https://github.com/xsmartbartx/Health-Support-App/issues
- Pull Requests: https://github.com/xsmartbartx/Health-Support-App/pulls

