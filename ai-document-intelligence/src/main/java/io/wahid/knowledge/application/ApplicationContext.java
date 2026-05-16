package io.wahid.knowledge.application;

import io.wahid.knowledge.application.core.embedding.EmbeddingClient;
import io.wahid.knowledge.application.core.ingestion.chunking.EmbeddingChunker;
import io.wahid.knowledge.application.core.ingestion.processing.IngestionService;
import io.wahid.knowledge.application.core.ingestion.processing.VectorWriter;
import io.wahid.knowledge.application.core.ingestion.processing.impl.DefaultIngestionService;
import io.wahid.knowledge.application.core.pipeline.PipelineStage;
import io.wahid.knowledge.application.core.pipeline.impl.DefaultMetadataExtractor;
import io.wahid.knowledge.application.core.pipeline.impl.DefaultTextNormalizer;
import io.wahid.knowledge.application.core.query.NumericIntentParser;
import io.wahid.knowledge.application.core.retrieval.NumericQueryEngine;
import io.wahid.knowledge.application.core.retrieval.Retriever;
import io.wahid.knowledge.application.core.retrieval.impl.DefaultRetriever;
import io.wahid.knowledge.application.core.retrieval.vector.VectorSearcher;
import io.wahid.knowledge.application.domain.DomainContext;
import io.wahid.knowledge.application.domain.DomainRegistry;
import io.wahid.knowledge.application.domain.DomainResolver;
import io.wahid.knowledge.application.domain.weather.config.WeatherDomainConfiguration;
import io.wahid.knowledge.application.domain.weather.insights.AnswerGenerator;
import io.wahid.knowledge.application.domain.weather.insights.OllamaAnswerGenerator;
import io.wahid.knowledge.application.domain.weather.query.routing.WeatherQuestionRouter;
import io.wahid.knowledge.application.domain.weather.retrieval.WeatherQueryEngine;
import io.wahid.knowledge.infrastructure.config.AppConfig;
import io.wahid.knowledge.infrastructure.embedding.ollama.OllamaEmbeddingClient;
import io.wahid.knowledge.infrastructure.embedding.openai.OpenAIEmbeddingClient;
import io.wahid.knowledge.infrastructure.graph.neo4j.Neo4jGraphClient;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.QueryService;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.impl.DefaultQueryService;
import io.wahid.knowledge.infrastructure.llms.LLMClient;
import io.wahid.knowledge.infrastructure.llms.openai.OpenAILLMClient;
import io.wahid.knowledge.infrastructure.storage.R2Client;
import io.wahid.knowledge.infrastructure.vectorstore.qdrant.QdrantAdminClient;
import io.wahid.knowledge.infrastructure.vectorstore.qdrant.QdrantClient;
import io.wahid.knowledge.infrastructure.vectorstore.qdrant.QdrantVectorSearcher;
import io.wahid.knowledge.infrastructure.vectorstore.qdrant.QdrantVectorWriter;
import io.wahid.knowledge.infrastructure.vectorstore.qdrant.impl.BatchEmbeddingVectorConsumer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {
    private static final Map<String, Integer> METRICS = new HashMap<>();
    private final QdrantAdminClient admin;
    private final LLMClient llmClient;
    private final EmbeddingClient embeddingClient;
    private final R2Client r2Client;
    private final Neo4jGraphClient neo4jGraphClient;
    private final VectorSearcher vectorSearcher;

    private final DomainRegistry domainRegistry = new DomainRegistry();
    private final DomainResolver domainResolver = new DomainResolver();

    public ApplicationContext() {
        this.admin = new QdrantAdminClient(AppConfig.qdrantBaseUrl());
        this.llmClient = new OpenAILLMClient(AppConfig.openAIKey(), "gpt-4.1-mini");

        this.neo4jGraphClient = new Neo4jGraphClient(AppConfig.neo4jBaseUrl(),
                AppConfig.neo4jUser(),
                AppConfig.neo4jPass());
        if (AppConfig.openaiEnabled()) {
            this.embeddingClient = new OpenAIEmbeddingClient(AppConfig.openAIKey());
        } else {
            this.embeddingClient = new OllamaEmbeddingClient(
                    AppConfig.ollamaBaseUrl(),
                    AppConfig.embeddingModel()
            );
        }
        this.r2Client = new R2Client();
        this.vectorSearcher = new QdrantVectorSearcher(AppConfig.qdrantBaseUrl(), AppConfig.collectionName());

        DomainContext weatherContext = new WeatherDomainConfiguration()
                .configure(vectorSearcher, embeddingClient, llmClient, neo4jGraphClient);

        domainRegistry.register("weather", weatherContext);
    }

    public static int getMetricValue(String metric) {
        return METRICS.getOrDefault(metric, 0);
    }

    public static void setMetricValue(String metric, int value) {
        METRICS.merge(metric, value, Integer::sum);
    }

    public IngestionService ingestionService() throws IOException, InterruptedException {
        ensureQdrantCollection(embeddingClient);

        // Final consumer: embedding + vector storage
        PipelineStage embeddingStage = createEmbeddingPipeline();

        // Chunker
        EmbeddingChunker chunker = new EmbeddingChunker(AppConfig.maxToken(), AppConfig.maxChar());
        chunker.setDownstream(embeddingStage);

        // Metadata extractor
        DefaultMetadataExtractor metadataExtractor = new DefaultMetadataExtractor();
        metadataExtractor.setDownstream(chunker);

        // Text normalizer
        DefaultTextNormalizer textNormalizer = new DefaultTextNormalizer();
        textNormalizer.setDownstream(metadataExtractor);

        return new DefaultIngestionService(textNormalizer, getEmbeddingClient(), getQdrantClient(), neo4jGraphClient);
    }

    public QueryService queryService() {
        Retriever retriever = new DefaultRetriever(embeddingClient, vectorSearcher);
        return new DefaultQueryService(
                domainRegistry,
                domainResolver,
                retriever
        );
    }

    public QdrantClient getQdrantClient() {
        return this.admin;
    }

    public LLMClient getLLMClient() {
        return this.llmClient;
    }

    public EmbeddingClient getEmbeddingClient() {
        return embeddingClient;
    }

    public R2Client getR2Client() {
        return this.r2Client;
    }

    private PipelineStage createEmbeddingPipeline() throws IOException, InterruptedException {
        VectorWriter vectorWriter = new QdrantVectorWriter(
                AppConfig.qdrantBaseUrl(),
                AppConfig.collectionName()
        );

        validateEmbeddingDimension(embeddingClient, vectorWriter);
        return new BatchEmbeddingVectorConsumer(
                embeddingClient,
                vectorWriter,
                128
        );
    }

    private void validateEmbeddingDimension(EmbeddingClient embeddingClient, VectorWriter vectorWriter) throws IOException, InterruptedException {
        int modelDim = embeddingClient.dimension();
        int collectionDim = vectorWriter.requiredVectorSize();

        if (modelDim != collectionDim) {
            throw new IllegalStateException(
                    "Embedding dimension mismatch: model=" + modelDim +
                            ", Qdrant collection=" + collectionDim
            );
        }
    }

    private void ensureQdrantCollection(EmbeddingClient embeddingClient) {
        admin.ensureCollection(
                AppConfig.collectionName(),
                embeddingClient.dimension(),           // embedding dimension
                AppConfig.distanceMetric()
        );
    }
}
