package io.wahid.knowledge.infrastructure.graph.neo4j.query.impl;

import io.wahid.knowledge.application.domain.weather.insights.AnswerGenerator;
import io.wahid.knowledge.application.core.retrieval.Retriever;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.QueryService;
import io.wahid.knowledge.application.core.query.routing.WeatherQuestionRouter;
import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;

import java.util.List;

public class DefaultQueryService implements QueryService {

    private final Retriever retriever;
    private final AnswerGenerator answerGenerator;
    private final WeatherQuestionRouter questionRouter;

    public DefaultQueryService(
            WeatherQuestionRouter questionRouter,
            Retriever retriever,
            AnswerGenerator answerGenerator
    ) {
        this.retriever = retriever;
        this.answerGenerator = answerGenerator;
        this.questionRouter = questionRouter;
    }

    @Override
    public QueryResult query(String question, int topK) throws Exception {

        List<VectorSearcher.SearchResult> retrieved = retriever.retrieve(question, topK);
        System.out.println("---- Retrieved Context ----");
        retrieved.stream().map(VectorSearcher.SearchResult::chunkText).forEach(System.out::println);
        System.out.println("---------------------------");

//        String answer = answerGenerator.generateAnswer(question, retrieved);
        String answer = route(question);

        List<String> sources = retrieved.stream()
                .map(VectorSearcher.SearchResult::documentId)
                .distinct()
                .toList();

        return new QueryResult(answer, sources);
    }

    @Override
    public String route(String question) throws Exception {
        return questionRouter.answer(question);
    }
}

