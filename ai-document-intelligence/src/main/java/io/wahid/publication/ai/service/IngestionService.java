package io.wahid.publication.ai.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public interface IngestionService {

    void ingest(
            String documentId,
            String type,
            InputStream inputStream
    ) throws Exception;

    default void ingest(String documentId,
                        String type,
                        Path file) throws Exception {
        ingest(documentId, type, Files.newInputStream(file));
    }
}
