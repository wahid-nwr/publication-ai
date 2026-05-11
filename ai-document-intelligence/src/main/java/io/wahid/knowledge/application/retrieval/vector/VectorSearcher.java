package io.wahid.knowledge.application.retrieval.vector;

import io.wahid.knowledge.infrastructure.vectorstore.dto.DocumentPayload;

import java.util.List;

public interface VectorSearcher {

    List<SearchResult> search(
            float[] queryVector,
            int topK
    );

    record SearchResult(
            String documentId,
            String chunkText,
            DocumentPayload metadata,
            double score
    ) {
    }
}
