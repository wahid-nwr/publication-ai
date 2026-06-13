package io.wahid.knowledge.application.core.ingestion.processing.impl;

import io.wahid.knowledge.application.core.ingestion.chunking.dto.TextChunk;
import io.wahid.knowledge.application.domain.weather.ingestion.parser.WeatherCsvParser;
import io.wahid.knowledge.application.core.ingestion.processing.IngestionService;
import io.wahid.knowledge.infrastructure.storage.R2Client;
import io.wahid.knowledge.application.core.pipeline.PipelineStage;
import io.wahid.knowledge.application.core.embedding.EmbeddingClient;
import io.wahid.knowledge.infrastructure.graph.neo4j.Neo4jGraphClient;
import io.wahid.knowledge.infrastructure.vectorstore.qdrant.QdrantClient;
import io.wahid.knowledge.repository.StationSummaryRepository;
import io.wahid.knowledge.infrastructure.graph.neo4j.query.impl.Neo4jSyncService;
import io.wahid.knowledge.application.domain.weather.insights.weather.WeatherAggregator;
import io.wahid.knowledge.application.domain.weather.insights.impl.StationWeatherAggregator;
import io.wahid.knowledge.util.JobRegistry;
import io.wahid.knowledge.util.JobStatus;
import io.wahid.knowledge.util.JpaUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default ingestion service: feeds text through normalization, metadata extraction,
 * chunking, and embedding vector consumer.
 */
public class DefaultIngestionService implements IngestionService {
    private static final Logger LOGGER = Logger.getLogger(DefaultIngestionService.class.getName());
    private final PipelineStage pipeline;
    private final R2Client r2Client;

    private final EmbeddingClient embeddingClient;
    private final QdrantClient qdrantClient;
    private final Neo4jSyncService neo4jSyncService;

    public DefaultIngestionService(PipelineStage pipeline, EmbeddingClient embeddingClient, QdrantClient qdrantClient, Neo4jGraphClient neo4jGraphClient) {
        this.pipeline = pipeline;
        this.r2Client = new R2Client();
        this.embeddingClient = embeddingClient;
        this.qdrantClient = qdrantClient;
        this.neo4jSyncService = new Neo4jSyncService(new StationSummaryRepository(JpaUtil.getEntityManagerFactory()), neo4jGraphClient.getDriver());
    }

    @Override
    public void ingest(String tenantId, String workspaceId, String documentId, String type, InputStream input) throws Exception {
        LOGGER.log(Level.INFO, "Initiating ingest by {0}", pipeline.getClass().getName());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", type);
        metadata.put("source", "upload");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
//                LOGGER.log(Level.FINE, "ingesting line-> {0}", line);
                pipeline.accept(new TextChunk(tenantId, workspaceId, documentId, line, metadata));
            }
        }

        pipeline.flush();            // send last batch
        pipeline.awaitCompletion();  // 🔥 WAIT HERE
    }

    @Override
    public void ingestFromR2(String jobId, String type, String bucket, String objectKey) throws Exception {
        LOGGER.info("trying file download from r2");
        try (InputStream in = r2Client.download(bucket, objectKey)) {
            WeatherAggregator aggregator = new StationWeatherAggregator(embeddingClient, qdrantClient, neo4jSyncService);

            new WeatherCsvParser(aggregator).parse(in, objectKey);

            JobRegistry.update(jobId, JobStatus.AGGREGATED);
        }
    }
}

