package io.wahid.knowledge.application.domain.weather.config;

import io.wahid.knowledge.application.core.embedding.EmbeddingClient;
import io.wahid.knowledge.application.core.query.NumericIntentParser;
import io.wahid.knowledge.application.core.query.handler.QueryHandlerRegistry;
import io.wahid.knowledge.application.core.query.handler.semantic.SemanticSearchQueryHandler;
import io.wahid.knowledge.application.core.retrieval.NumericQueryEngine;
import io.wahid.knowledge.application.core.retrieval.strategy.RetrievalStrategy;
import io.wahid.knowledge.application.core.retrieval.strategy.VectorRetrievalStrategy;
import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;
import io.wahid.knowledge.application.domain.DomainContext;
import io.wahid.knowledge.application.domain.weather.insights.AnswerGenerator;
import io.wahid.knowledge.application.domain.weather.insights.OllamaAnswerGenerator;
import io.wahid.knowledge.application.domain.weather.query.handler.AggregationQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.BottomKQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.CompareQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.MaxQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.MinQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.TopKQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.TrendQueryHandler;
import io.wahid.knowledge.application.domain.weather.query.handler.ValueQueryHandler;
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
         * Retrieval strategy
         */
        RetrievalStrategy retrievalStrategy =
                new VectorRetrievalStrategy(
                        embeddingClient,
                        vectorSearcher
                );

        /*
         * Numeric engine
         */
        NumericQueryEngine numericQueryEngine = new WeatherQueryEngine(neo4jGraphClient);

        /*
         * Intent parser
         */
        NumericIntentParser numericIntentParser =
                new NumericIntentParser(llmClient);

        /*
         * Semantic handler
         */
        SemanticSearchQueryHandler semanticHandler =
                new SemanticSearchQueryHandler(
                        retrievalStrategy,
                        llmClient
                );

        /*
         * Handler registry
         */
        QueryHandlerRegistry registry = new QueryHandlerRegistry();


        registry.register(
                new MaxQueryHandler(
                        numericQueryEngine,
                        llmClient
                )
        );

        registry.register(
                new MinQueryHandler(
                        numericQueryEngine,
                        llmClient
                )
        );

        registry.register(
                new TopKQueryHandler(
                        numericQueryEngine,
                        llmClient
                )
        );

        registry.register(
                new BottomKQueryHandler(
                        numericQueryEngine,
                        llmClient
                )
        );

        registry.register(
                new CompareQueryHandler(
                        numericQueryEngine,
                        llmClient
                )
        );

        registry.register(
                new TrendQueryHandler(
                        numericQueryEngine,
                        llmClient
                )
        );

        registry.register(
                new ValueQueryHandler(
                        numericQueryEngine,
                        llmClient
                )
        );

        registry.register(
                semanticHandler
        );

        /*
         * Router
         */
        WeatherQuestionRouter questionRouter =
                new WeatherQuestionRouter(
                        numericIntentParser,
                        registry,
                        semanticHandler
                );

        /*
         * Answer generator
         */
        AnswerGenerator answerGenerator =
                new OllamaAnswerGenerator(
                        llmClient
                );

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