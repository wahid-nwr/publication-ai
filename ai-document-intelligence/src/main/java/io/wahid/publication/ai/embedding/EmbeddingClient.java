package io.wahid.publication.ai.embedding;

import java.util.List;

public interface EmbeddingClient {

    List<Float> embed(String text) throws Exception;

}