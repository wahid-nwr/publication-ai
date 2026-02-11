terraform {
  required_version = ">= 1.6.0"

  required_providers {
    google = {
      source  = "hashicorp/google"
      version = "~> 5.0"
    }
  }
}

# -------------------------------
# Provider
# -------------------------------
provider "google" {
  project = var.project_id
  region  = var.region
}

# -------------------------------
# Artifact Registry (Container)
# -------------------------------
data "google_artifact_registry_repository" "rag_app_repo" {
  location       = var.region
  repository_id  = "app-repo"
}

# -------------------------------
# Service Account
# -------------------------------
data "google_service_account" "run_rag_sa" {
  account_id   = "cloudrun-rag-sa"
}

resource "google_project_iam_member" "run_sa_storage_reader" {
  project = var.project_id
  role    = "roles/storage.objectViewer"
  member  = "serviceAccount:${data.google_service_account.run_rag_sa.email}"
}

data "google_secret_manager_secret" "firebase_sa" {
  secret_id = "firebase-service-account"
}

resource "google_secret_manager_secret_iam_member" "firebase_access" {
  secret_id = data.google_secret_manager_secret.firebase_sa.secret_id
  role      = "roles/secretmanager.secretAccessor"
  member    = "serviceAccount:${data.google_service_account.run_rag_sa.email}"
}

resource "google_project_iam_member" "firebase_auth_admin" {
  project = var.project_id
  role    = "roles/firebaseauth.admin"
  member  = "serviceAccount:${data.google_service_account.run_rag_sa.email}"
}

resource "google_project_iam_member" "firestore_user" {
  project = var.project_id
  role    = "roles/datastore.user"
  member  = "serviceAccount:${data.google_service_account.run_rag_sa.email}"
}