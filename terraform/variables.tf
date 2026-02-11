variable "project_id" {
  description = "GCP project ID"
  type        = string
}

variable "region" {
  description = "Deploy region"
  type        = string
  default     = "us-central1"
}

variable "vpc_connector" {
  description = "Cloud Run VPC connector name"
  type        = string
  default = "run-connector"
}

variable "app_container_image" {
  description = "App container image url for Cloud Run"
  type        = string
}

variable "log_level" {
  description = "GCP project ID"
  type        = string
}

variable "is_from_volume" {
  description = "GCP project ID"
  type        = string
}

variable "jdbc_url" {
  description = "JDBC url"
  type        = string
}

variable "db_host" {
  description = "db host"
  type        = string
}

variable "db_port" {
  description = "db port"
  type        = string
}

variable "openai_enabled" {
  description = "open ai enabled"
  type        = bool
}

variable "r2_access_url" {
  description = "r2 access url"
  type        = string
}

variable "qdrant_base_url" {
  description = "qdrant base url"
  type        = string
}

variable "embedding_model" {
  description = "embedding model"
  type        = string
}

variable "llm_model" {
  description = "llm model"
  type        = string
}

variable "qdrant_collection" {
  description = "qdrant collection"
  type        = string
}

variable "qdrant_distant" {
  description = "qdrant distant"
  type        = string
}

variable "chunk_size" {
  description = "chunk size"
  type        = number
}

variable "chunk_overlap" {
  description = "chunk overlap"
  type        = number
}

variable "max_token" {
  description = "max token"
  type        = number
}

variable "max_char" {
  description = "max char"
  type        = number
}