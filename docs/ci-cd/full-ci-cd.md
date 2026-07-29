# CI/CD — przykłady i instrukcje dla developera

Uruchamianie kroków lokalnie

- Można uruchomić niektóre kroki CI lokalnie: budowę (./gradlew build), testy (./gradlew test), linting i skany SCA przy pomocy lokalnych narzędzi (trivy, grype).

Test workflow locally

- Build: `./gradlew build`
- Unit tests: `./gradlew test`
- SAST: run CodeQL locally (optional)
- SCA: trivy image scan

Push/pull request flow

- Lokalnie przetestuj, zrób commit i push do feature branch → otwórz PR → CI uruchomi workflowy i wygeneruje artefakty.

