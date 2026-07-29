# CI/CD Workflows

- GitHub Actions are configured in .github/workflows/. Use OIDC for AWS access.
- To debug workflows locally, use act (https://github.com/nektos/act) for simple reproduction.

Example: running linters locally

```bash
# markdown lint
markdownlint README.md
# helm lint
helm lint charts/*
```
