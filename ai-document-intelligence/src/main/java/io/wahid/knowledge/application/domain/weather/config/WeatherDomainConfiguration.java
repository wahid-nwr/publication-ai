package io.wahid.knowledge.application.domain.weather.config;

import io.wahid.knowledge.application.core.embedding.EmbeddingClient;
import io.wahid.knowledge.application.core.query.NumericIntentParser;
import io.wahid.knowledge.application.core.retrieval.NumericQueryEngine;
import io.wahid.knowledge.application.core.retrieval.Retriever;
import io.wahid.knowledge.application.core.retrieval.impl.DefaultRetriever;
import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;
import io.wahid.knowledge.application.domain.DomainContext;
import io.wahid.knowledge.application.domain.weather.insights.AnswerGenerator;
import io.wahid.knowledge.application.domain.weather.insights.OllamaAnswerGenerator;
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
         * Weather-specific query engine
         */
        NumericQueryEngine numericQueryEngine =
                new WeatherQueryEngine(
                        neo4jGraphClient,
                        llmClient
                );

        /*
         * Weather-specific intent parsing
         */
        NumericIntentParser numericIntentParser =
                new NumericIntentParser(llmClient);

        /*
         * Weather-specific router
         */
        WeatherQuestionRouter questionRouter =
                new WeatherQuestionRouter(
                        vectorSearcher,
                        embeddingClient,
                        llmClient,
                        numericQueryEngine,
                        numericIntentParser
                );

        /*
         * Generic retriever
         */
        Retriever retriever =
                new DefaultRetriever(
                        embeddingClient,
                        vectorSearcher
                );

        /*
         * Weather answer generation
         */
        AnswerGenerator answerGenerator =
                new OllamaAnswerGenerator(llmClient);

        /*
         * Build domain context
         */
        DomainContext context = new DomainContext();

        context.setDomainName("weather");
        context.setQueryRouter(questionRouter);
        context.setInsightGenerator(answerGenerator);

        /*
         * Optional for later chunks
         */
        // context.setRetriever(retriever);
        // context.setDocumentParser(...);
        // context.setDomainMapper(...);

        return context;
    }
}