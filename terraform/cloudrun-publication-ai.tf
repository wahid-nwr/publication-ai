# -------------------------------
# Cloud Run Service
# -------------------------------
resource "google_cloud_run_v2_service" "publication-ai" {
  name     = "publication-ai"
  location = var.region

  template {
    revision = "publication-ai-${substr(md5(timestamp()), 0, 8)}"
    # FREE TIER SAFE — scale to zero
    scaling {
      min_instance_count = 0
      max_instance_count = 1
    }
    volumes {
      name = "firebase-secret"

      secret {
        secret = data.google_secret_manager_secret.firebase_sa.secret_id

        items {
          path    = "firebase"
          version = "latest"
        }
      }
    }
    containers {
      image = "us-central1-docker.pkg.dev/alert-cursor-476219-s1/publication-repo/publication-ai@sha256:d759c024a22fe8ce12125207a38bd28fee9fe14da5f71bdb16b19cbf27cc11be"
      volume_mounts {
        name       = "firebase-secret"
        mount_path = "/run/secrets"
      }
      # --------------------------------------
      # Environment variables for your app
      # --------------------------------------
      env {
        name  = "GOOGLE_APPLICATION_CREDENTIALS"
        value = "/run/secrets/firebase"
      }
      env {
        name = "OPENAI_API_KEY"
        value_source {
          secret_key_ref {
            secret  = "OPENAI_API_KEY" # existing secret name
            version = "latest"
          }
        }
      }
      env {
        name = "R2_ACCESS_KEY"
        value_source {
          secret_key_ref {
            secret  = "R2_ACCESS_KEY" # existing secret name
            version = "latest"
          }
        }
      }
      env {
        name = "R2_SECRET_KEY"
        value_source {
          secret_key_ref {
            secret  = "R2_SECRET_KEY" # existing secret name
            version = "latest"
          }
        }
      }
      env {
        name = "NEO4J_BASE_URL"
        value_source {
          secret_key_ref {
            secret  = "NEO4J_BASE_URL" # existing secret name
            version = "latest"
          }
        }
      }
      env {
        name = "NEO4J_PASS"
        value_source {
          secret_key_ref {
            secret  = "NEO4J_PASS" # existing secret name
            version = "latest"
          }
        }
      }
      env {
        name = "JWKS_URI"
        value_source {
          secret_key_ref {
            secret  = "JWKS_URI" # existing secret name
            version = "latest"
          }
        }
      }
      env {
        name = "ISSUER"
        value_source {
          secret_key_ref {
            secret  = "ISSUER" # existing secret name
            version = "latest"
          }
        }
      }
      env {
        name = "TRUSTSTORE_PASSWORD"
        value_source {
          secret_key_ref {
            secret  = "TRUSTSTORE_PASSWORD" # existing secret name
            version = "latest"
          }
        }
      }
      env {
        name = "WEB_PORT"
        value = "8080"
      }
      env {
        name  = "LOG_LEVEL"
        value = var.log_level
      }
      env {
        name  = "JPA_JDBC_URL"
        value = var.jdbc_url
      }
      env {
        name  = "DB_HOST"
        value = var.db_host
      }
      env {
        name  = "DB_PORT"
        value = var.db_port
      }
      env {
        name = "OPENAI_ENABLED"
        value = var.openai_enabled
      }
      env {
        name = "R2_ACCESS_URL"
        value = var.r2_access_url
      }
      env {
        name = "QDRANT_BASE_URL"
        value = var.qdrant_base_url
      }
      env {
        name = "EMBEDDING_MODEL"
        value = var.embedding_model
      }
      env {
        name = "LLM_MODEL"
        value = var.llm_model
      }
      env {
        name = "QDRANT_DISTANCE"
        value = var.qdrant_distant
      }
      env {
        name = "QDRANT_COLLECTION"
        value = var.qdrant_collection
      }
      env {
        name = "CHUNK_SIZE"
        value = var.chunk_size
      }
      env {
        name = "CHUNK_OVERLAP"
        value = var.chunk_overlap
      }
      env {
        name = "MAX_TOKEN"
        value = var.max_token
      }
      env {
        name = "MAX_CHAR"
        value = var.max_char
      }

      resources {
        limits = {
          cpu    = "1"
          memory = "512Mi"   # FREE TIER SAFE (you can bump to 1Gi if needed)
        }
      }
    }
    # ------------ VPC ACCESS (VALID) ------------
    vpc_access {
      connector = "projects/${var.project_id}/locations/${var.region}/connectors/${var.vpc_connector}"
      egress    = "PRIVATE_RANGES_ONLY"   # or PRIVATE_RANGES_ONLY
    }
    service_account = data.google_service_account.run_rag_sa.email
    timeout = "30s" # microservice should respond fast
    #     ingress = "INGRESS_ALL"
  }
}