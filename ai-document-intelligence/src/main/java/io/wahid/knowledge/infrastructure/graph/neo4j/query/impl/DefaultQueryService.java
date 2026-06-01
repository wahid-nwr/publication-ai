package io.wahid.knowledge.infrastructure.graph.neo4j.query.impl;

import io.wahid.knowledge.application.core.retrieval.Retriever;
import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;
import io.wahid.knowledge.application.domain.DomainContext;
import io.wahid.knowledge.application.domain.DomainRegistry;
import io.wahid.knowledge.application.domain.DomainResolver;
import io.wahid.knowledge.domain.query.Query;
import io.wahid.knowledge.domain.query.result.QueryResult;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.QueryService;

import java.util.List;

public class DefaultQueryService implements QueryService {

    private Retriever retriever;
    private DomainResolver domainResolver;
    private DomainRegistry domainRegistry;
    // TODO REMOVE ANSWER, QUESTION
//    private AnswerGenerator answerGenerator;
//    private WeatherQuestionRouter questionRouter;

//    public DefaultQueryService(
//            WeatherQuestionRouter questionRouter,
//            Retriever retriever,
//            AnswerGenerator answerGenerator
//    ) {
//        this.retriever = retriever;
////        this.answerGenerator = answerGenerator;
////        this.questionRouter = questionRouter;
//    }

    public DefaultQueryService(
            DomainRegistry domainRegistry,
            DomainResolver domainResolver,
            Retriever retriever
    ) {
        this.retriever = retriever;
        this.domainRegistry = domainRegistry;
        this.domainResolver = domainResolver;
    }

    @Override
    public QueryResult query(String question, int topK) throws Exception {

        /*
         * Generic retrieval
         */
        List<VectorSearcher.SearchResult> retrieved = retriever.retrieve(question, topK);

        System.out.println("---- Retrieved Context ----");

        retrieved.stream().map(VectorSearcher.SearchResult::chunkText).forEach(System.out::println);

        System.out.println("---------------------------");

        /*
         * Resolve domain
         */
        Query query = new Query();
        query.setText(question);
        String domain = domainResolver.resolve(query);

        /*
         * Get domain context
         */
        DomainContext context = domainRegistry.get(domain);

        if (context == null) {
            throw new IllegalStateException(
                    "No domain registered for: " + domain
            );
        }

        /*
         * Route question to domain
         */
        return context.getQueryRouter().route(query);

        /*
         * Build sources
         */
        /*List<String> sources = retrieved.stream()
                .map(VectorSearcher.SearchResult::documentId)
                .distinct()
                .toList();*/

        /*return queryResult;*/
    }

    @Override
    public String route(String question) throws Exception {
        return query(question, 10).getAnswer();
    }
}

