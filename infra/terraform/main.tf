terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
  required_version = ">= 1.3"
}

provider "aws" {
  region = var.aws_region
}

# ECR repository for service-a
resource "aws_ecr_repository" "service_a" {
  name                 = var.ecr_repo_name
  image_tag_mutability = "MUTABLE"
  tags = {
    Environment = var.environment
    Project     = "service-a"
  }
}

# RDS Postgres (example for dev only!)
resource "aws_db_subnet_group" "default" {
  name       = "${var.environment}-db-subnet-group"
  subnet_ids = var.db_subnet_ids
}

resource "aws_db_instance" "healthdb" {
  identifier         = "${var.environment}-healthdb"
  engine             = "postgres"
  engine_version     = var.rds_engine_version
  instance_class     = var.rds_instance_class
  allocated_storage  = var.rds_allocated_storage
  name               = var.db_name
  username           = var.db_username
  password           = var.db_password
  skip_final_snapshot = true
  publicly_accessible = false
  db_subnet_group_name = aws_db_subnet_group.default.name

  tags = {
    Environment = var.environment
    Project     = "service-a"
  }
}