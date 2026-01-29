package io.wahid.publication.ai.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JobRegistry {
    private static final Map<String, JobStatus> jobs = new ConcurrentHashMap<>();

    private JobRegistry() {
    }

    public static void create(String id) {
        jobs.put(id, JobStatus.PENDING);
    }

    public static void update(String id, JobStatus status) {
        jobs.put(id, status);
    }

    public static JobStatus get(String id) {
        return jobs.get(id);
    }
}
