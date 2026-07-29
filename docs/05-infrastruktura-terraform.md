# Infrastruktura (Terraform)


Infrastruktura (Terraform)

Najważniejsze praktyki:

- Struktura: modules/ shared modules, envs/<dev|staging|prod>/
- Backend: zewnętrzny state (S3 + DynamoDB lock) dla AWS
- Local dev: terraform init -backend=false; terraform plan -out=tfplan; terraform apply tfplan
- Weryfikacja: terraform fmt, terraform validate, tfsec (SAST infra)

Przykład inicjalizacji (dev):

cd infra/envs/dev
terraform init -backend=false
terraform fmt -recursive
terraform validate
terraform plan -out=tfplan
# review tfplan, then
terraform apply tfplan

Sekrety:
- Nie trzymać sekretów w plaintext w repo
- Używać SOPS/age lub Vault
