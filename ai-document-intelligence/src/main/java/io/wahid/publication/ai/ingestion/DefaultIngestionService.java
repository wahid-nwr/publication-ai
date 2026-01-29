package io.wahid.publication.ai.ingestion;

import io.wahid.publication.ai.R2Client;
import io.wahid.publication.ai.service.IngestionService;

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

    public DefaultIngestionService(PipelineStage pipeline) {
        this.pipeline = pipeline;
        this.r2Client = new R2Client();
    }

    @Override
    public void ingest(String documentId, String type, InputStream input)
            throws Exception {
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
    public void ingestFromR2(
            String jobId,
            String type,
            String bucket,
            String objectKey
    ) throws Exception {
        LOGGER.info("trying file download from r2");
        try (InputStream in = r2Client.download(bucket, objectKey)) {
            LOGGER.info("downloaded file from r2");
            ingest(bucket + jobId + objectKey, type, in);
        }
    }
}

