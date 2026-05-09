package io.wahid.knowledge.application.insights;

import io.wahid.knowledge.application.retrieval.vector.VectorSearcher;

import java.util.List;

public interface AnswerGenerator {

    String generateAnswer(
            String question,
            List<VectorSearcher.SearchResult> context
    ) throws Exception;
}
