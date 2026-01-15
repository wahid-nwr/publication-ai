package io.wahid.publication.ai.ingestion;

import io.wahid.publication.ai.service.IngestionService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Default ingestion service: feeds text through normalization, metadata extraction,
 * chunking, and embedding vector consumer.
 */
public class DefaultIngestionService implements IngestionService {

    private final PipelineStage pipeline;

    public DefaultIngestionService(PipelineStage pipeline) {
        this.pipeline = pipeline;
    }

    @Override
    public void ingest(String documentId, String type, InputStream input)
            throws Exception {

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("type", type);
        metadata.put("source", "upload");

        try (BufferedReader reader =
                     new BufferedReader(
                             new InputStreamReader(input, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;

                pipeline.accept(
                        new TextChunk(
                                documentId,
                                line,
                                metadata
                        )
                );
            }
        }

        // 🔥 THIS IS CRITICAL
        pipeline.flush();
    }
}

