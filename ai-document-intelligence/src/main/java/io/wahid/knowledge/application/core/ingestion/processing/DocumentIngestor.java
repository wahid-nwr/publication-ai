package io.wahid.knowledge.application.core.ingestion.processing;

import io.wahid.knowledge.application.core.ingestion.chunking.TextChunkConsumer;

import java.io.InputStream;

public interface DocumentIngestor {

    void ingest(
            String documentId,
            InputStream inputStream,
            TextChunkConsumer consumer
    ) throws Exception;
}
