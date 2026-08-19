
# Terraform stub for infra

# This is a placeholder Terraform module. Configure provider and resources as needed.
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0"
    }
  }
}

# provider "aws" {
#   region = var.region
# }

# Add real modules and resources here following docs/05-infrastruktura-terraform.md