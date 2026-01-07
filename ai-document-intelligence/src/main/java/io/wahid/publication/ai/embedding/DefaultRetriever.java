package io.wahid.publication.ai.embedding;

import io.wahid.publication.ai.rag.Retriever;
import io.wahid.publication.ai.vectorstore.VectorSearcher;

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
        List<Float> queryEmbedding = embeddingClient.embed(question);

        return vectorSearcher.search(queryEmbedding, topK);
    }
}
