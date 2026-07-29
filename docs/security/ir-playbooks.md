# Incident Response Playbooks

Example playbook: Suspicious Privileged Login

1. Triage: gather alert id, source IP, user, timestamp.
2. Containment: disable account, isolate host.
3. Forensics: snapshot host, collect logs, preserve evidence in S3 WORM.
4. Eradication & recovery: remove persistence, rotate keys, restore services.
5. Postmortem: timeline, root cause, action items.
