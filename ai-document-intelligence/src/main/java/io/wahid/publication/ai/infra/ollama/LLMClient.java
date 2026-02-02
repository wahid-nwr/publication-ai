package io.wahid.publication.ai.infra.ollama;

public interface LLMClient {
    String generate(String prompt) throws Exception;
    boolean isUp();
    boolean hasModel(String modelName);
}
