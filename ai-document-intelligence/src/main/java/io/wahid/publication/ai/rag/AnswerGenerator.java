package io.wahid.publication.ai.rag;

import io.wahid.publication.ai.vectorstore.VectorSearcher;

import java.util.List;

public interface AnswerGenerator {

    String generateAnswer(
            String question,
            List<VectorSearcher.SearchResult> context
    ) throws Exception;
}
