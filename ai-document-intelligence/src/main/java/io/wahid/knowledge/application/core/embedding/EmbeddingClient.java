package io.wahid.knowledge.application.core.embedding;

import java.util.List;

public interface EmbeddingClient {
    int dimension();
    float[] embed(String text) throws Exception;
    float[] embed(String text, boolean isAnswer) throws Exception;
    List<float[]> embedBatch(List<String> texts);
    List<float[]> embedBatch(List<String> texts, boolean isAnswer);
}