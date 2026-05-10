package io.wahid.knowledge.infrastructure.llms.ollama;

import io.wahid.knowledge.infrastructure.llms.LLMClient;

public interface OllamaClient extends LLMClient {
    boolean isUp();
    boolean hasModel(String modelName);
}
