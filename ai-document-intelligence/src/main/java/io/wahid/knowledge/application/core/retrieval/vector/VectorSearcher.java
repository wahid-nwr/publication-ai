package io.wahid.knowledge.application.core.retrieval.vector;

import io.wahid.knowledge.infrastructure.vectorstore.dto.DocumentPayload;

import java.util.List;

public interface VectorSearcher {

    List<SearchResult> search(
            String tenantId,
            String workspaceId,
            float[] queryVector,
            int topK
    );

    record SearchResult(
            String tenantId,
            String workspaceId,
            String documentId,
            String chunkId,
            String chunkText,
            DocumentPayload metadata,
            double score
    ) {
    }
}
