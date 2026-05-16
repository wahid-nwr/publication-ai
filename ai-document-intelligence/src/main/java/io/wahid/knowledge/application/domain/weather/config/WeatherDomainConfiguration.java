package io.wahid.knowledge.application.domain.weather.config;

import io.wahid.knowledge.application.core.embedding.EmbeddingClient;
import io.wahid.knowledge.application.core.query.NumericIntentParser;
import io.wahid.knowledge.application.core.query.handler.semantic.SemanticSearchQueryHandler;
import io.wahid.knowledge.application.core.retrieval.NumericQueryEngine;
import io.wahid.knowledge.application.core.retrieval.strategy.RetrievalStrategy;
import io.wahid.knowledge.application.core.retrieval.strategy.VectorRetrievalStrategy;
import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;
import io.wahid.knowledge.application.domain.DomainContext;
import io.wahid.knowledge.application.domain.weather.insights.AnswerGenerator;
import io.wahid.knowledge.application.domain.weather.insights.OllamaAnswerGenerator;
import io.wahid.knowledge.application.domain.weather.query.handler.CompareQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.TopKQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.routing.WeatherQuestionRouter;
import io.wahid.knowledge.application.domain.weather.retrieval.WeatherQueryEngine;
import io.wahid.knowledge.infrastructure.graph.neo4j.Neo4jGraphClient;
import io.wahid.knowledge.infrastructure.llms.LLMClient;

public class WeatherDomainConfiguration {

    public DomainContext configure(
            VectorSearcher vectorSearcher,
            EmbeddingClient embeddingClient,
            LLMClient llmClient,
            Neo4jGraphClient neo4jGraphClient
    ) {

        /*
         * Query engine
         */
        NumericQueryEngine numericQueryEngine =
                new WeatherQueryEngine(
                        neo4jGraphClient,
                        llmClient
                );

        /*
         * Intent parser
         */
        NumericIntentParser numericIntentParser =
                new NumericIntentParser(llmClient);

        /*
         * Handlers
         */
        TopKQueryHandler topKHandler =
                new TopKQueryHandler(
                        numericQueryEngine,
                        llmClient
                );

        CompareQueryHandler compareHandler =
                new CompareQueryHandler(
                        llmClient
                );

        RetrievalStrategy retrievalStrategy =
                new VectorRetrievalStrategy(
                        embeddingClient,
                        vectorSearcher
                );

        SemanticSearchQueryHandler semanticHandler =
                new SemanticSearchQueryHandler(
                        retrievalStrategy,
                        llmClient
                );

        /*
         * Router
         */
        WeatherQuestionRouter questionRouter =
                new WeatherQuestionRouter(
                        numericIntentParser,
                        compareHandler,
                        semanticHandler
                );

        /*
         * Answer generator
         */
        AnswerGenerator answerGenerator =
                new OllamaAnswerGenerator(llmClient);

        /*
         * Domain context
         */
        DomainContext context =
                new DomainContext();

        context.setDomainName("weather");

        context.setQueryRouter(questionRouter);

        context.setInsightGenerator(answerGenerator);

        return context;
    }
}