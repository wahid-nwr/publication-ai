package io.wahid.knowledge.infrastructure.health;


import io.wahid.knowledge.infrastructure.llms.LLMClient;
import io.wahid.knowledge.infrastructure.vectorstore.qdrant.QdrantClient;
import io.wahid.knowledge.infrastructure.dto.HealthStatus;

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
