package io.wahid.knowledge.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class KnowledgeServiceExecutor {
    private KnowledgeServiceExecutor() {}

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    public static Future<?> submit(Runnable task) {
        return EXECUTOR.submit(task);
    }
}
