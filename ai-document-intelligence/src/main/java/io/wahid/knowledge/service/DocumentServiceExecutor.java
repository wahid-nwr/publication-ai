package io.wahid.knowledge.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DocumentServiceExecutor {
    private DocumentServiceExecutor() {}

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static Future<?> submit(Runnable task) {
        return EXECUTOR.submit(task);
    }
}
