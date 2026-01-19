package io.wahid.publication.ai.service;


import io.wahid.publication.ai.infra.ollama.OllamaClient;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;
import io.wahid.publication.ai.dto.HealthStatus;

public class HealthService {

    private final OllamaClient ollama;
    private final QdrantClient qdrant;

    public HealthService(OllamaClient ollama, QdrantClient qdrant) {
        this.ollama = ollama;
        this.qdrant = qdrant;
    }

    public HealthStatus check() {
        boolean ollamaUp = ollama.isUp();
        boolean qdrantUp = qdrant.isUp();

        return new HealthStatus(ollamaUp, qdrantUp);
    }
}
