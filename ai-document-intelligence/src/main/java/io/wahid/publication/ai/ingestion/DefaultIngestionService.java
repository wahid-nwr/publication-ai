package io.wahid.publication.ai.ingestion;

import io.wahid.publication.ai.service.IngestionService;

import java.io.InputStream;

public class DefaultIngestionService implements IngestionService {

    private final IngestorRegistry registry = new IngestorRegistry();
    private final TextChunkConsumer downstreamConsumer;

    public DefaultIngestionService(TextChunkConsumer downstreamConsumer) {
        this.downstreamConsumer = downstreamConsumer;
    }

    @Override
    public void ingest(
            String documentId,
            String type,
            InputStream inputStream
    ) throws Exception {

        DocumentIngestor ingestor = registry.get(type);
        ingestor.ingest(documentId, inputStream, downstreamConsumer);
    }
}

