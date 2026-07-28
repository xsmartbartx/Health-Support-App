# Terraform — Zasady i przykłady

Ten plik zawiera skondensowane najlepsze praktyki dotyczace Terraform oraz przykładowe fragmenty konfiguracyjne. Pełne przykłady przenieś do `docs/infra/examples/`.

## Zasady i konwencje
- Oddzielne konta AWS: dev / staging / prod
- Moduły współdzielone w `infra/modules`
- Backend: S3 + DynamoDB (lock)
- Testuj moduły: terraform validate, terraform fmt

## Backend (przykład)
```hcl
terraform {
  backend "s3" {
    bucket         = "project-terraform-state" # PRZYKŁAD — zastąp własnym
    key            = "envs/prod/terraform.tfstate"
    region         = "eu-central-1"
    dynamodb_table = "terraform-locks"
    encrypt        = true
  }
}
```

## IAM role for GitHub Actions OIDC (skrócony przykład)
```hcl
resource "aws_iam_role" "github_actions" {
  name = "github-actions-role"
  assume_role_policy = data.aws_iam_policy_document.github_assume_role.json
}
```

Upewnij się, że wszystkie wartości ARN i identyfikatory są przykładowe i wymagają modyfikacji przed użyciem w prod.
