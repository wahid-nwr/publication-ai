package io.wahid.publication.ai.config;

import java.lang.reflect.GenericDeclaration;

public final class AppConfig {

    private AppConfig() {}

    public static String issuer() {
        return env("ISSUER", "http://localhost:8088/realms/rag-system");
    }

    public static String clientId() {
        return env("CLIENT_ID", "publication-hybrid-rag");
    }

    public static String audience() {
        return env("AUDIENCE", "account");
    }

    public static String jwksUri() {
        return env("JWKS_URI", "http://keycloak:8080/realms/rag-system/protocol/openid-connect/certs");
    }

    public static boolean openaiEnabled() {
        return envBoolean("OPENAI_ENABLED", false);
    }

    public static String openAIKey() {
        return env("OPENAI_API_KEY", "");
    }

    public static String r2AccessUrl() {
        return env("R2_ACCESS_URL", "https://047c814396bdfb908b85ffd11aaac5c1.r2.cloudflarestorage.com");
    }

    public static String r2AccessKey() {
        return env("R2_ACCESS_KEY", "");
    }

    public static String r2SecretKey() {
        return env("R2_SECRET_KEY", "");
    }

    public static String ollamaBaseUrl() {
        return env("OLLAMA_BASE_URL", "http://ollama:11434");
    }

    public static String qdrantBaseUrl() {
        return env("QDRANT_BASE_URL", "http://qdrant:6333");
    }

    public static String neo4jBaseUrl() {
        return env("NEO4J_BASE_URL", "bolt://neo4j:7687");
    }

    public static String neo4jUser() {
        return env("NEO4J_USER", "neo4j");
    }

    public static String neo4jPass() {
        return env("NEO4J_PASS", "mysecretpassword");
    }

    public static String embeddingModel() {
        return env("EMBEDDING_MODEL", "nomic-embed-text");
    }

    public static String llmModel() {
        return env("LLM_MODEL", "llama3");
    }

    public static String collectionName() {
        String collectionName = AppConfig.openaiEnabled()
                ? "documents_openai"
                : "documents_ollama";
        return env("QDRANT_COLLECTION", collectionName);
    }

    public static String distanceMetric() {
        return env("QDRANT_DISTANCE", "Cosine");
    }

    public static int chunkSize() {
        return envInt("CHUNK_SIZE", 500);
    }

    public static int chunkOverlap() {
        return envInt("CHUNK_OVERLAP", 50);
    }

    public static int maxToken() {
        return envInt("MAX_TOKEN", 1200);
    }

    public static int maxChar() {
        return envInt("MAX_CHAR", 400);
    }

    private static int envInt(String key, int defaultValue) {
        try {
            String v = System.getenv(key);
            return v == null ? defaultValue : Integer.parseInt(v);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static boolean envBoolean(String key, boolean defaultValue) {
        try {
            String v = System.getenv(key);
            return v == null ? defaultValue : Boolean.parseBoolean(v);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
