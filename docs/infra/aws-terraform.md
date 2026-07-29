# AWS Terraform — praktyczny przewodnik dla developera

Sekcja zawiera przykłady i krok po kroku instrukcje jak testować Terraform lokalnie oraz jak zarządzać state.

Local development

- Nie inicjalizuj z backendem w dev: `terraform init -backend=false` — pozwala na szybką walidację bez zapisu stanu w S3.

Formatowanie i walidacja

```bash
# format check
terraform fmt -check
# validate
terraform validate
```

Przykładowy workflow lokalny

1. cd infra/envs/dev
2. terraform init -backend=false
3. terraform plan -out=tfplan
4. terraform apply tfplan

Modules

- Trzymaj moduły w infra/modules i testuj je osobno. Użyj Terratest (Go) lub test frameworku do weryfikacji modułów.

Security notes

- Nigdy nie commituj credentials ani keys do repo. Use placeholders and document how to provision real values in org-specific docs.

