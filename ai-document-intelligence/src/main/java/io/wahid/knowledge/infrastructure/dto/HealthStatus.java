package io.wahid.knowledge.infrastructure.dto;

public record HealthStatus(
        boolean ollamaUp,
        boolean qdrantUp
) {
    public boolean isHealthy() {
        return ollamaUp && qdrantUp;
    }
}
