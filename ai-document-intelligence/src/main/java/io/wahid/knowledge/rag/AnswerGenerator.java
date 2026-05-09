package io.wahid.knowledge.rag;

import io.wahid.knowledge.vectorstore.VectorSearcher;

import java.util.List;

public interface AnswerGenerator {

    String generateAnswer(
            String question,
            List<VectorSearcher.SearchResult> context
    ) throws Exception;
}
