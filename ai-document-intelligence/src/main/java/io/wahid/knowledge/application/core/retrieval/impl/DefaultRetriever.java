package io.wahid.knowledge.application.core.retrieval.impl;

import io.wahid.knowledge.application.core.embedding.EmbeddingClient;
import io.wahid.knowledge.application.core.retrieval.Retriever;
import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;

import java.util.List;

public class DefaultRetriever implements Retriever {

    private final EmbeddingClient embeddingClient;
    private final VectorSearcher vectorSearcher;

    public DefaultRetriever(
            EmbeddingClient embeddingClient,
            VectorSearcher vectorSearcher
    ) {
        this.embeddingClient = embeddingClient;
        this.vectorSearcher = vectorSearcher;
    }

    @Override
    public List<VectorSearcher.SearchResult> retrieve(
            String tenantId,
            String workspaceId,
            String question,
            int topK
    ) throws Exception {
        float[] queryEmbedding = embeddingClient.embed(question);

        return vectorSearcher.search(tenantId, workspaceId, queryEmbedding, topK);
    }
}
