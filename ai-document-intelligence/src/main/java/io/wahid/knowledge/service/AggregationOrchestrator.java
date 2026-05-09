package io.wahid.knowledge.service;

import io.wahid.knowledge.util.JobRegistry;
import io.wahid.knowledge.util.JobStatus;

public class AggregationOrchestrator {

    private final IngestionService ingestionService;

    public AggregationOrchestrator(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    public void onUploadCompleted(
            String jobId,
            String type,
            String bucket,
            String objectKey
    ) {
        if (!JobRegistry.is(jobId, JobStatus.UPLOADED)) {
            return;
        }

        try {
            JobRegistry.update(jobId, JobStatus.AGGREGATING);

            ingestionService.ingestFromR2(
                    jobId,
                    type,
                    bucket,
                    objectKey
            );

            JobRegistry.update(jobId, JobStatus.AGGREGATED);

        } catch (Exception e) {
            JobRegistry.update(jobId, JobStatus.FAILED);
            throw new RuntimeException("Aggregation failed for job " + jobId, e);
        }
    }
}
