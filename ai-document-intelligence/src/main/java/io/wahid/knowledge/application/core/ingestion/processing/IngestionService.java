package io.wahid.knowledge.application.core.ingestion.processing;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public interface IngestionService {
    void ingest(
            String tenantId,
            String workspaceId,
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

    default void ingest(String tenantId, String workspaceId, String documentId,
                        String type,
                        Path file) throws Exception {
        ingest(tenantId, workspaceId, documentId, type, Files.newInputStream(file));
    }
}
