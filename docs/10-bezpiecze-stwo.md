# Bezpieczeństwo

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
