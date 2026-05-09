package io.wahid.knowledge.service;


import io.wahid.knowledge.infra.ollama.LLMClient;
import io.wahid.knowledge.infra.qdrant.QdrantClient;
import io.wahid.knowledge.dto.HealthStatus;

public class HealthService {

    private final LLMClient ollama;
    private final QdrantClient qdrant;

    public HealthService(LLMClient ollama, QdrantClient qdrant) {
        this.ollama = ollama;
        this.qdrant = qdrant;
    }

    public HealthStatus check() {
        boolean ollamaUp = ollama.isUp();
        boolean qdrantUp = qdrant.isUp();

        return new HealthStatus(ollamaUp, qdrantUp);
    }
}
