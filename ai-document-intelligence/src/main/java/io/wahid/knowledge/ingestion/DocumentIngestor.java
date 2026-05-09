package io.wahid.knowledge.ingestion;

import java.io.InputStream;

public interface DocumentIngestor {

    void ingest(
            String documentId,
            InputStream inputStream,
            TextChunkConsumer consumer
    ) throws Exception;
}
