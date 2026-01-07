package io.wahid.publication.ai.rag;

import io.wahid.publication.ai.vectorstore.VectorSearcher;

import java.util.List;

public interface Retriever {

    List<VectorSearcher.SearchResult> retrieve(
            String question,
            int topK
    ) throws Exception;
}
