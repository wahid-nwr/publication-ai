package io.wahid.publication.ai.service.impl;

import io.wahid.publication.ai.rag.AnswerGenerator;
import io.wahid.publication.ai.rag.Retriever;
import io.wahid.publication.ai.service.QueryService;
import io.wahid.publication.ai.vectorstore.VectorSearcher;

import java.util.List;
import java.util.stream.Collectors;

public class DefaultQueryService implements QueryService {

    private final Retriever retriever;
    private final AnswerGenerator answerGenerator;

    public DefaultQueryService(
            Retriever retriever,
            AnswerGenerator answerGenerator
    ) {
        this.retriever = retriever;
        this.answerGenerator = answerGenerator;
    }

    @Override
    public QueryResult query(String question, int topK) throws Exception {

        List<VectorSearcher.SearchResult> retrieved =
                retriever.retrieve(question, topK);

        String answer =
                answerGenerator.generateAnswer(question, retrieved);

        List<String> sources = retrieved.stream()
                .map(VectorSearcher.SearchResult::documentId)
                .distinct()
                .collect(Collectors.toList());

        return new QueryResult(answer, sources);
    }
}

