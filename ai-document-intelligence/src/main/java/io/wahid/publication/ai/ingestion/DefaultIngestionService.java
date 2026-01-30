package io.wahid.publication.ai.ingestion;

import com.opencsv.bean.CsvToBeanBuilder;
import io.wahid.publication.ai.ApplicationContext;
import io.wahid.publication.ai.R2Client;
import io.wahid.publication.ai.dto.WeatherInfo;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.infra.qdrant.QdrantClient;
import io.wahid.publication.ai.service.CSVParser;
import io.wahid.publication.ai.service.IngestionService;
import io.wahid.publication.ai.service.WeatherAggregator;
import io.wahid.publication.ai.service.impl.StationWeatherAggregator;
import io.wahid.publication.ai.util.JobRegistry;
import io.wahid.publication.ai.util.JobStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
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

    public DefaultIngestionService(PipelineStage pipeline, EmbeddingClient embeddingClient, QdrantClient qdrantClient) {
        this.pipeline = pipeline;
        this.r2Client = new R2Client();
        this.embeddingClient = embeddingClient;
        this.qdrantClient = qdrantClient;
    }

    @Override
    public void ingest(String documentId, String type, InputStream input) throws Exception {
        LOGGER.log(Level.INFO, "Initiating ingest by {0}", pipeline.getClass().getName());
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", type);
        metadata.put("source", "upload");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
//                LOGGER.log(Level.FINE, "ingesting line-> {0}", line);
                pipeline.accept(new TextChunk(documentId, line, metadata));
            }
        }

        pipeline.flush();            // send last batch
        pipeline.awaitCompletion();  // 🔥 WAIT HERE
    }

    @Override
    public void ingestFromR2(String jobId, String type, String bucket, String objectKey) throws Exception {
        LOGGER.info("trying file download from r2");
        try (InputStream in = r2Client.download(bucket, objectKey)) {
            WeatherAggregator aggregator = new StationWeatherAggregator(embeddingClient, qdrantClient);

            new CSVParser(aggregator).parse(in);

            JobRegistry.update(jobId, JobStatus.AGGREGATED);
        }
//        ingest(bucket + jobId + objectKey, type, in);
    }
}

