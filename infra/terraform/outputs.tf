output "ecr_repo_url" {
  value = aws_ecr_repository.service_a.repository_url
}

output "rds_endpoint" {
  value       = aws_db_instance.healthdb.address
  description = "RDS endpoint (may be empty if db_subnet_ids not provided)"
}
