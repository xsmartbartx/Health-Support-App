# Bezpieczeństwo

Bezpieczeństwo projektu obejmuje RBAC, logging, incident response i AI integration guardrails.

## SIEM/SOAR Architecture

Centralized security operations with:
- Log aggregation (ELK or Loki)
- Real-time alerting (rule-based)
- Incident response automation (but AI-safe)
- Evidence preservation (chain of custody)

## AI Integration Guardrails

**Core principle**: AI is descriptive, not prescriptive in security operations.

See docs/security/ai-integration.md for detailed guidelines.

### What AI Can Do
- Describe logs, patterns, correlations
- Add context to security events
- Suggest remediation steps
- Flag policy violations
- Generate reports

### What AI Cannot Do
- Modify RBAC policies
- Delete or alter logs
- Change risk scores
- Decide incident correlation
- Auto-remediate critical issues without approval

## Secrets Management

- Use HashiCorp Vault or SOPS with age encryption
- Never commit secrets to repo
- Use git-secrets or truffleHog scanning in CI
- Rotate secrets regularly (automatic or manual)

## SAST/SCA/Secret Scanning

- Semgrep: SAST for code vulnerabilities
- Trivy: Container image scanning
- Grype: Dependency vulnerability scanner
- truffleHog: Secret detection
- All integrated in CI pipeline

## Incident Response (IR)

See docs/operations/ops-compliance-backup.md for full IR playbooks.

Quick phases:
1. **Triage**: Identify incident, initial assessment
2. **Containment**: Isolate affected systems
3. **Eradication**: Remove threat
4. **Recovery**: Restore services
5. **Post-Mortem**: Document lessons learned

## Compliance

- RBAC auditing (every access decision logged)
- Log integrity verification (checksums, signatures)
- Policy enforcement (no override without approval)
- Compliance reporting (automated via dashboards)

## Security Checklist

- [ ] All secrets managed in Vault/SOPS
- [ ] Git secrets scanning enabled in CI
- [ ] SAST/SCA scanning passing
- [ ] Audit logs immutable and complete
- [ ] AI security boundaries validated
- [ ] Incident response procedures tested
- [ ] Security policies up-to-date
