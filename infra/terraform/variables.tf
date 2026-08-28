variable "aws_region" {
  type    = string
  default = "eu-west-1"
}

variable "environment" {
  type    = string
  default = "dev"
}

variable "ecr_repo_name" {
  type    = string
  default = "service-a"
}

variable "db_subnet_ids" {
  type    = list(string)
  description = "List of subnet IDs for RDS subnet group"
  default = []
}

variable "db_name" {
  type    = string
  default = "healthdb"
}

variable "db_username" {
  type    = string
  default = "postgres"
}

variable "db_password" {
  type = string
  sensitive = true
}

variable "rds_engine_version" {
  type = string
  default = "15.4"
}

variable "rds_instance_class" {
  type = string
  default = "db.t3.micro"
}

variable "rds_allocated_storage" {
  type = number
  default = 20
}
