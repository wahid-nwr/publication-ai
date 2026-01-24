package io.wahid.publication.ai.embedding;

import java.util.List;

public interface EmbeddingClient {
    int dimension();
    float[] embed(String text) throws Exception;
    List<float[]> embedBatch(List<String> texts);
}