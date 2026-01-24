package io.wahid.publication.ai.config;

public final class AppConfig {

    private AppConfig() {}

    public static String issuer() {
        return env("ISSUER", "https://securetoken.google.com/alert-cursor-476219-s1");
    }

    public static String ollamaBaseUrl() {
        return env("OLLAMA_BASE_URL", "http://ollama:11434");
    }

    public static String qdrantBaseUrl() {
        return env("QDRANT_BASE_URL", "http://qdrant:6333");
    }

    public static String embeddingModel() {
        return env("EMBEDDING_MODEL", "nomic-embed-text");
    }

    public static String llmModel() {
        return env("LLM_MODEL", "llama3");
    }

    public static String collectionName() {
        return env("QDRANT_COLLECTION", "documents");
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

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
