# Wykrywanie i reagowanie na incydenty (SIEM / SOAR / Playbooki)

17.1 SIEM architektura
- Ingest: Fluentd/Vector → S3 (raw) → SIEM ingestion (Elastic/Splunk).
- Enrichment: GeoIP, AD/IdP context, asset tagging.

17.2 Detection engineering
- Rule lifecycle: author → test in staging → tune → promote.
- Track FP rate and TTR metrics.

17.3 SOAR
- Automatyzuj niskiego ryzyka akcje (isolate host), wymagaj human approval dla high-impact.
- Loguj wszystkie automated actions with audit trail.

17.4 Evidence preservation
- Use S3 WORM, checksums, signed manifests, chain-of-custody logs.

17.5 Playbook example (Suspicious Privileged Login)
- Triage: capture alert id, source IP, user
- Containment: disable account, block IP, snapshot host
- Eradication: remove persistence, rotate keys
- Recovery: restore clean, validate
- Postmortem: timeline, lessons learned
