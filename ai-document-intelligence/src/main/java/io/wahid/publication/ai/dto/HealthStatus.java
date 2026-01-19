package io.wahid.publication.ai.dto;

public record HealthStatus(
        boolean ollamaUp,
        boolean qdrantUp
) {
    public boolean isHealthy() {
        return ollamaUp && qdrantUp;
    }
}
