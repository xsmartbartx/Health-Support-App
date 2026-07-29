# AWS Terraform — pełne wytyczne i przykłady

Sekcja zawiera szczegóły konfiguracji Terraform dla środowisk AWS: zasady, struktura repo, backend, kluczowe moduły oraz najlepsze praktyki.

9.1 Zasady i konwencje
- Oddzielne konta AWS dla DEV / QA / STAGING / PROD.
- Wspólne moduły Terraform w `infra/modules`.
- Stan Terraform: backend S3 + DynamoDB dla blokad.
- Formatowanie: `terraform fmt` i `terraform validate` w CI.
- Policy-as-Code: OPA (rego) lub Sentinel w pipeline.

9.2 Struktura repo (przykład)
