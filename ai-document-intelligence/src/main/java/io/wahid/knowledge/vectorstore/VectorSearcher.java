package io.wahid.knowledge.vectorstore;

import io.wahid.knowledge.dto.DocumentPayload;

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
