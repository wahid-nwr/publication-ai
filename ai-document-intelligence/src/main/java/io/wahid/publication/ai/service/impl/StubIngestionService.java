package io.wahid.publication.ai.service.impl;

import io.wahid.publication.ai.service.IngestionService;

import java.io.InputStream;

public class StubIngestionService implements IngestionService {

    @Override
    public void ingest(String documentId, String type, InputStream inputStream) {
        // Next steps: route to CSV / PDF / MD processor
        System.out.println("Ingesting document: " + documentId + " type=" + type);
    }

    @Override
    public void ingestFromR2(String jobId, String type, String bucket, String objectKey) throws Exception {

    }
}
