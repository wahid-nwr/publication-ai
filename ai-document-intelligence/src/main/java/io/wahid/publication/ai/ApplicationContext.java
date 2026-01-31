package io.wahid.publication.ai;

import io.wahid.publication.ai.config.AppConfig;
import io.wahid.publication.ai.embedding.*;
import io.wahid.publication.ai.infra.ollama.OllamaClient;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;
import io.wahid.publication.ai.ingestion.DefaultIngestionService;
import io.wahid.publication.ai.ingestion.PipelineStage;
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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.time.MonthDay;
import java.time.Year;
import java.util.UUID;

public class ApplicationContext {

    private final QdrantAdminClient admin;
    private final OllamaLLMClient llmClient;
    private final EmbeddingClient embeddingClient;
    private final R2Client r2Client;

    public ApplicationContext() {
        this.admin = new QdrantAdminClient(AppConfig.qdrantBaseUrl());
        this.llmClient = new OllamaLLMClient(
                AppConfig.ollamaBaseUrl(),
                AppConfig.llmModel()   // 🔥 GENERATION MODEL
        );
        if (AppConfig.openaiEnabled()) {
            this.embeddingClient = new OpenAIEmbeddingClient(AppConfig.openAIKey());
        } else {
            this.embeddingClient = new OllamaEmbeddingClient(
                    AppConfig.ollamaBaseUrl(),
                    AppConfig.embeddingModel()
            );
        }
        this.r2Client = new R2Client();
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

        return new DefaultIngestionService(textNormalizer, getEmbeddingClient(), getQdrantClient());
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

    private void s3Client() {
        S3Client s3 = S3Client.builder()
                .endpointOverride(URI.create("https://<ACCOUNT_ID>.r2.cloudflarestorage.com"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        System.getenv("R2_ACCESS_KEY"),
                                        System.getenv("R2_SECRET_KEY")
                                )
                        )
                )
                .region(Region.of("auto"))
                .build();

    }

    private String getUploadUrl(String userId, String originalFilename) {
        try (S3Presigner presigner = S3Presigner.builder()
                .endpointOverride(URI.create("https://<ACCOUNT_ID>.r2.cloudflarestorage.com"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                                System.getenv("R2_ACCESS_KEY"),
                                System.getenv("R2_SECRET_KEY")
                        )
                ))
                .region(Region.of("auto"))
                .build()) {

            String key =
                    userId + "/" +
                            Year.now() + "/" +
                            MonthDay.now().getMonthValue() + "/" +
                            UUID.randomUUID() + "-" + originalFilename;

            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket("documents")
                    .key(key)
                    .contentType("application/pdf")
                    .build();

            PresignedPutObjectRequest presigned =
                    presigner.presignPutObject(p -> p
                            .signatureDuration(Duration.ofMinutes(10))
                            .putObjectRequest(objectRequest)
                    );

            return presigned.url().toString();
        }
    }
}
