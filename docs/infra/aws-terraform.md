# AWS Terraform — pełne wytyczne i przykłady

Sekcja zawiera szczegóły konfiguracji Terraform dla środowisk AWS: zasady, struktura repo, backend, kluczowe moduły oraz najlepsze praktyki.

9.1 Zasady i konwencje
- Oddzielne konta AWS dla DEV / QA / STAGING / PROD.
- Wspólne moduły Terraform w `infra/modules`.
- Stan Terraform: backend S3 + DynamoDB dla blokad.
- Formatowanie: `terraform fmt` i `terraform validate` w CI.
- Policy-as-Code: OPA (rego) lub Sentinel w pipeline.

9.2 Struktura repo (przykład)
infra/ ├─ modules/ │ ├─ vpc/ │ ├─ eks/ │ ├─ rds/ │ └─ iam/ ├─ envs/ │ ├─ dev/ │ ├─ staging/ │ └─ prod/ └─ pipelines/

9.3 Terraform backend (S3 + DynamoDB) — przykład
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

9.4 Przykładowe moduły — VPC i EKS
```hcl
module "vpc" {
  source = "git::ssh://git@example.com/infra/modules.git//vpc"
  name   = "project-vpc"
  cidr   = "10.10.0.0/16"
  public_subnets  = ["10.10.1.0/24","10.10.2.0/24"]
  private_subnets = ["10.10.10.0/24","10.10.11.0/24"]
}

module "eks" {
  source = "git::ssh://git@example.com/infra/modules.git//eks"
  cluster_name = "project-eks"
  vpc_id       = module.vpc.vpc_id
  subnets      = module.vpc.private_subnets
  node_groups = {
    app = { desired_capacity = 3, instance_type = "t3.medium" }
  }
  oidc_provider = true
}
```

9.5 Storage i backup

S3 z wersjonowaniem i lifecycle (GLACIER/DEEP_ARCHIVE) dla długoterminowych artefaktów.
RDS snapshots automatyczne + manualne przed kluczowymi zmianami.
S3 WORM (Write Once Read Many) dla dowodów incydentów.

9.6 KMS i klucze

CMK per environment, key rotation enabled.
Dostęp zdefiniowany przez polityki IAM (least privilege).
Uwagi bezpieczeństwa: wszystkie wartości ARNs, nazw bucketów i kont są przykładami — nigdy nie używaj przykładowych identyfikatorów w produkcji bez ich podmiany.
