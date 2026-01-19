package io.wahid.publication.ai.infra.ollama;

public interface OllamaClient {
    boolean isUp();
    boolean hasModel(String modelName);
}
