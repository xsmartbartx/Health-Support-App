# Wykrywanie i reagowanie na incydenty (SIEM / SOAR / Playbooki)

Security for developers

- Do not commit secrets. Use git-secrets pre-commit hook and run trufflehog before pushing.
- For local development use SOPS or .env files and ensure .gitignore excludes them.

Secret scanning

```bash
# run trufflehog if installed
trufflehog filesystem --no-update --exclude .git .
```

Playbooks

- See docs/security/ir-playbooks.md for step-by-step incident handling.
