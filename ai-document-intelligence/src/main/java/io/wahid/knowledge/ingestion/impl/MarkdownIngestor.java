package io.wahid.knowledge.ingestion.impl;

import io.wahid.knowledge.ingestion.DocumentIngestor;
import io.wahid.knowledge.dto.TextChunk;
import io.wahid.knowledge.ingestion.TextChunkConsumer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MarkdownIngestor implements DocumentIngestor {

    @Override
    public void ingest(
            String documentId,
            InputStream inputStream,
            TextChunkConsumer consumer
    ) throws Exception {

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) continue;

                Map<String, Object> metadata = new HashMap<>();
                metadata.put("line", lineNumber);
                metadata.put("type", "markdown");

                consumer.accept(new TextChunk(
                        documentId,
                        line,
                        metadata
                ));
            }
        }
    }
}
