package io.wahid.knowledge.application.domain.weather.insights.impl;

import io.wahid.knowledge.util.JobRegistry;
import io.wahid.knowledge.util.JobStatus;

import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UploadCheckTaskScheduler {

    private static final Logger LOGGER = Logger.getLogger(UploadCheckTaskScheduler.class.getName());

    private final AggregationOrchestrator aggregationOrchestrator;

    // Single shared scheduler
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // Track running tasks
    private final Map<String, ScheduledFuture<?>> runningTasks = new ConcurrentHashMap<>();

    public UploadCheckTaskScheduler(AggregationOrchestrator aggregationOrchestrator) {
        this.aggregationOrchestrator = aggregationOrchestrator;
    }

    public void startPeriodicTask(String jobId, String type, String bucket, String objectKey) {

        Runnable task = () -> {
            try {
                JobStatus job = JobRegistry.get(jobId);

                if (JobStatus.UPLOADED == job) {
                    stopTask(jobId);
                    aggregationOrchestrator.onUploadCompleted(
                            jobId, type, bucket, objectKey
                    );
                }

                LOGGER.log(Level.INFO,
                        "Checked upload status for job {0} at {1}",
                        new Object[]{jobId, System.currentTimeMillis()});

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error checking upload status for job " + jobId, e);
            }
        };

        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(task, 10, 10, TimeUnit.SECONDS);

        runningTasks.put(jobId, future);
    }

    private void stopTask(String jobId) {
        ScheduledFuture<?> future = runningTasks.remove(jobId);
        if (future != null) {
            future.cancel(false);
            LOGGER.info("Stopped scheduler for job " + jobId);
        }
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
