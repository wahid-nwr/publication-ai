package io.wahid.knowledge.embedding;

import io.wahid.knowledge.rag.Retriever;
import io.wahid.knowledge.vectorstore.VectorSearcher;

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
            String question,
            int topK
    ) throws Exception {
        float[] queryEmbedding = embeddingClient.embed(question);

        return vectorSearcher.search(queryEmbedding, topK);
    }
}
