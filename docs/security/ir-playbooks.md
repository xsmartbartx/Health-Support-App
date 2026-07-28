# Playbooki IR i procedury bezpieczeństwa

W tym pliku znajdziesz szablony playbooków reakcji na incydenty, procedury przechowywania dowodów i podstawowe kroki triage.

## Przykładowy playbook: "Suspicious Privileged Login"
Trigger: SIEM alert — repeated failed privileged login attempts

Severity: High

Kroki (skrót):
1. Triage: capture alert id, timestamps, source IPs, user account
2. Containment: tymczasowo zablokuj konto, zablokuj IP na WAF
3. Eradication: przeskanuj hosty, usuń artefakty
4. Recovery: przywróć usługi z clean snapshot
5. Postmortem: timeline, lessons learned, aktualizacja detekcji

## Evidence handling
- Store evidence in write-once storage (S3 WORM)
- Record checksums and chain-of-custody entries

Pełne playbooki umieść w `docs/security/playbooks/`.
