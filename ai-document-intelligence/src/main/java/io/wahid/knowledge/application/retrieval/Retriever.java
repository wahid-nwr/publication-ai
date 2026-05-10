package io.wahid.knowledge.application.retrieval;

import io.wahid.knowledge.application.retrieval.vector.VectorSearcher;

import java.util.List;

public interface Retriever {

    List<VectorSearcher.SearchResult> retrieve(
            String question,
            int topK
    ) throws Exception;
}
