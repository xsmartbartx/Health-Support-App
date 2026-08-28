Terraform stubs for dev infra (ECR + RDS).

Warning: these are example resources and will create real AWS resources that may incur costs.
Review carefully before applying.

Quick local use:
1. Configure AWS credentials (AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY).
2. Run:
   terraform init
   terraform plan -var 'db_password=YOUR_PASSWORD' -var 'db_subnet_ids=["subnet-..."]'
   terraform apply -var 'db_password=YOUR_PASSWORD' -var 'db_subnet_ids=["subnet-..."]'