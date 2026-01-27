package io.wahid.publication.ai.service;

import io.wahid.publication.ai.R2Client;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public interface IngestionService {
    R2Client r2Client = new R2Client();
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
    default void ingestFromR2(
            String jobId,
            String type,
            String bucket,
            String objectKey
    ) throws Exception {
        try (InputStream in = r2Client.download(bucket, objectKey)) {
            ingest(bucket + jobId + objectKey, type, in);
        }
    }
}
