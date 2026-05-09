package io.wahid.knowledge.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public interface IngestionService {
    void ingest(
            String documentId,
            String type,
            InputStream inputStream
    ) throws Exception;

    void ingestFromR2(
            String jobId,
            String type,
            String bucket,
            String objectKey
    ) throws Exception;

    default void ingest(String documentId,
                        String type,
                        Path file) throws Exception {
        ingest(documentId, type, Files.newInputStream(file));
    }
}
