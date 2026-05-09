package io.wahid.knowledge.rag;

import io.wahid.knowledge.vectorstore.VectorSearcher;

import java.util.List;

public interface Retriever {

    List<VectorSearcher.SearchResult> retrieve(
            String question,
            int topK
    ) throws Exception;
}
