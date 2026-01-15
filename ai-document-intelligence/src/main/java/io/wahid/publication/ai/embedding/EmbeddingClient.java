package io.wahid.publication.ai.embedding;

import java.util.List;

public interface EmbeddingClient {
    int dimension();
    List<Float> embed(String text) throws Exception;

}