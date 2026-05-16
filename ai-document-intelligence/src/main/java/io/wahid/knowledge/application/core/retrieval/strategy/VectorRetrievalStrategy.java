package io.wahid.knowledge.application.core.retrieval.strategy;

import io.wahid.knowledge.application.core.embedding.EmbeddingClient;
import io.wahid.knowledge.application.core.retrieval.RetrievalContext;
import io.wahid.knowledge.application.core.retrieval.RetrievalResult;
import io.wahid.knowledge.domain.query.result.RetrievedDocument;
import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;
import io.wahid.knowledge.domain.query.Query;

import java.util.List;

public class VectorRetrievalStrategy implements RetrievalStrategy {

    private final EmbeddingClient embeddingClient;
    private final VectorSearcher vectorSearcher;

    public VectorRetrievalStrategy(
            EmbeddingClient embeddingClient,
            VectorSearcher vectorSearcher
    ) {
        this.embeddingClient = embeddingClient;
        this.vectorSearcher = vectorSearcher;
    }

    @Override
    public RetrievalResult retrieve(
            Query query,
            RetrievalContext context
    ) throws Exception {

        float[] embedding =
                embeddingClient.embed(query.getText());

        List<RetrievedDocument> documents =
                vectorSearcher.search(embedding, context.topK())
                        .stream()
                        .map(r -> new RetrievedDocument(
                                r.documentId(),
                                r.chunkId(),
                                r.chunkText(),
                                r.score()
                        ))
                        .toList();

        return new RetrievalResult(documents);
    }
}