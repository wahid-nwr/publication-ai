package io.wahid.publication.ai.vectorstore;

import io.wahid.publication.ai.dto.DocumentPayload;

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
