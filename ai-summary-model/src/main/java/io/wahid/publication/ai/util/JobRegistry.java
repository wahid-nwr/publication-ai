package io.wahid.publication.ai.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobRegistry {
    private static final Map<String, JobStatus> JOBS = new ConcurrentHashMap<>();

    private JobRegistry() {
    }

    public static void update(String jobId, JobStatus status) {
        JOBS.put(jobId, status);
    }

    public static JobStatus get(String jobId) {
        return JOBS.get(jobId);
    }

    public static boolean is(String jobId, JobStatus status) {
        return status == JOBS.get(jobId);
    }
}
