package io.wahid.publication.ai.service;

import java.io.InputStream;

public interface IngestionService {

    void ingest(
            String documentId,
            String type,
            InputStream inputStream
    ) throws Exception;
}
