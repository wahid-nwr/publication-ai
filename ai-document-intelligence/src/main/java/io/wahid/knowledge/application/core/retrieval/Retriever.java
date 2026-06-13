package io.wahid.knowledge.application.core.retrieval;

import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;

import java.util.List;

public interface Retriever {

    List<VectorSearcher.SearchResult> retrieve(
            String tenantId,
            String workspaceId,
            String question,
            int topK
    ) throws Exception;
}
