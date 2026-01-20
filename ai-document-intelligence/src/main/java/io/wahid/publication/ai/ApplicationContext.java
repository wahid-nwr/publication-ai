package io.wahid.publication.ai;

import io.wahid.publication.ai.config.AppConfig;
import io.wahid.publication.ai.embedding.DefaultRetriever;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.embedding.OllamaEmbeddingClient;
import io.wahid.publication.ai.infra.ollama.OllamaClient;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;
import io.wahid.publication.ai.ingestion.DefaultIngestionService;
import io.wahid.publication.ai.ingestion.PipelineStage;
import io.wahid.publication.ai.processing.SlidingWindowChunker;
import io.wahid.publication.ai.processing.impl.DefaultMetadataExtractor;
import io.wahid.publication.ai.processing.impl.DefaultTextNormalizer;
import io.wahid.publication.ai.rag.AnswerGenerator;
import io.wahid.publication.ai.rag.OllamaAnswerGenerator;
import io.wahid.publication.ai.rag.OllamaLLMClient;
import io.wahid.publication.ai.rag.Retriever;
import io.wahid.publication.ai.service.IngestionService;
import io.wahid.publication.ai.service.QueryService;
import io.wahid.publication.ai.service.impl.DefaultQueryService;
import io.wahid.publication.ai.vectorstore.*;

import java.io.IOException;

public class ApplicationContext {

    private final QdrantAdminClient admin;
    private final OllamaLLMClient llmClient;
    private final OllamaEmbeddingClient embeddingClient;

    public ApplicationContext() {
        this.admin = new QdrantAdminClient(AppConfig.qdrantBaseUrl());
        this.llmClient = new OllamaLLMClient(
                AppConfig.ollamaBaseUrl(),
                AppConfig.llmModel()   // 🔥 GENERATION MODEL
        );
        this.embeddingClient = new OllamaEmbeddingClient(
                AppConfig.ollamaBaseUrl(),
                AppConfig.embeddingModel()
        );
    }

    public IngestionService ingestionService() throws IOException, InterruptedException {
        ensureQdrantCollection(embeddingClient);

        // Final consumer: embedding + vector storage
        PipelineStage embeddingStage = createEmbeddingPipeline();

        // Chunker
        SlidingWindowChunker chunker = new SlidingWindowChunker(AppConfig.chunkSize(), AppConfig.chunkOverlap());
        chunker.setDownstream(embeddingStage);

        // Metadata extractor
        DefaultMetadataExtractor metadataExtractor = new DefaultMetadataExtractor();
        metadataExtractor.setDownstream(chunker);

        // Text normalizer
        DefaultTextNormalizer textNormalizer = new DefaultTextNormalizer();
        textNormalizer.setDownstream(metadataExtractor);

        return new DefaultIngestionService(textNormalizer);
    }

    public QueryService queryService() {
        VectorSearcher vectorSearcher = new QdrantVectorSearcher(
                AppConfig.qdrantBaseUrl(),
                AppConfig.collectionName()
        );

        Retriever retriever = new DefaultRetriever(embeddingClient, vectorSearcher);

        AnswerGenerator answerGenerator = new OllamaAnswerGenerator(llmClient);

        return new DefaultQueryService(
                retriever,
                answerGenerator
        );
    }

    public QdrantClient getQdrantClient() {
        return this.admin;
    }

    public OllamaClient getOllamaClient() {
        return this.llmClient;
    }

    public EmbeddingClient getEmbeddingClient() {
        return embeddingClient;
    }

    private PipelineStage createEmbeddingPipeline() throws IOException, InterruptedException {
        VectorWriter vectorWriter = new QdrantVectorWriter(
                AppConfig.qdrantBaseUrl(),
                AppConfig.collectionName()
        );

        validateEmbeddingDimension(embeddingClient, vectorWriter);
        return new EmbeddingVectorConsumer(
                embeddingClient,
                vectorWriter
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
