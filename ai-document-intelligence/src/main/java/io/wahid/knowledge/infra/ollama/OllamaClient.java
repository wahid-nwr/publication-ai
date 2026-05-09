package io.wahid.knowledge.infra.ollama;

public interface OllamaClient extends LLMClient {
    boolean isUp();
    boolean hasModel(String modelName);
}
