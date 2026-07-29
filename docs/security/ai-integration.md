# AI Security Integration Guidelines

This document provides comprehensive guidance on integrating AI safely into security operations, based on principles from chat NWP3z7ip8LENiqYZ6NeW3.

Core principle: **AI is descriptive, not prescriptive in critical security operations.**

## Quick Reference: What AI Can vs. Cannot Do

| Domain | AI CAN do | AI CANNOT do |
|--------|-----------|--------------|
| RBAC | Describe policies; suggest improvements | Grant/deny access; bypass RBAC; modify permissions |
| Logging | Describe logs; correlate entries; find patterns | Modify logs; delete entries; hide actions |
| Risk Scoring | Describe risk factors; suggest levels | Calculate final score; override policies; ignore metrics |
| Correlation | Describe correlations; suggest related events | Decide which incidents correlate; ignore signatures |
| Classification | Describe sensitivity; add context | Auto-classify without review; lower sensitivity |
| Evidence | Describe evidence; suggest preservation | Alter evidence; modify timestamps; break chain of custody |
| Compliance | Describe policies; flag violations; suggest actions | Override policies; auto-remediate critical issues; hide violations |

## Implementation Checklist

- [ ] RBAC: Policy engine guards all access decisions
- [ ] Logging: Logs are immutable and tamper-proof (S3 WORM or equivalent)
- [ ] Risk Scoring: Risk engine is rule-based, not AI-driven
- [ ] Correlation: Correlation rules are signature + heuristic-based
- [ ] Classification: Data classification changes require human review
- [ ] Evidence: Evidence preservation is enforced and audited
- [ ] Compliance: No auto-override of policies; all changes logged and approved

## Security Testing

For each AI component, create tests that verify:

1. **Boundary violations**: Attempt AI actions outside permitted scope
2. **Policy bypass**: Attempt to circumvent policy engine
3. **Data leakage**: Attempt to access data outside RBAC
4. **Audit evasion**: Attempt to hide AI actions from logs
5. **State manipulation**: Attempt to modify critical system state

All tests should fail (i.e., AI cannot violate guardrails).

## Further Reading

- See chat NWP3z7ip8LENiqYZ6NeW3 in docs/copilot_chats/ for detailed discussion
- docs/security/siem-soar.md for SIEM/SOAR architecture
- docs/operations/ops-compliance-backup.md for compliance requirements
