package io.wahid.publication.ai.vectorstore;

import io.wahid.publication.ai.ApplicationContext;
import io.wahid.publication.ai.config.AppConfig;
import io.wahid.publication.ai.embedding.EmbeddingClient;
import io.wahid.publication.ai.ingestion.AbstractPipelineStage;
import io.wahid.publication.ai.dto.TextChunk;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BatchEmbeddingVectorConsumer
        extends AbstractPipelineStage
        implements Consumer<TextChunk> {

    private static final Logger LOGGER = Logger.getLogger(BatchEmbeddingVectorConsumer.class.getName());
    private static final int MAX_PARALLEL = 6;
    private final ExecutorService executor = Executors.newFixedThreadPool(MAX_PARALLEL);
    private final List<CompletableFuture<Void>> inFlight = new ArrayList<>();

    private final EmbeddingClient embeddingClient;
    private final VectorWriter vectorWriter;

    private final int batchSize; // configurable
    private final List<TextChunk> buffer = new ArrayList<>();

    public BatchEmbeddingVectorConsumer(EmbeddingClient embeddingClient, VectorWriter vectorWriter) {
        this(embeddingClient, vectorWriter, 16);
    }

    public BatchEmbeddingVectorConsumer(EmbeddingClient embeddingClient, VectorWriter vectorWriter, int batchSize) {
        this.embeddingClient = embeddingClient;
        this.vectorWriter = vectorWriter;
        this.batchSize = batchSize;
    }

    @Override
    public synchronized void accept(TextChunk chunk) {
        LOGGER.log(Level.INFO, "Consuming text chunk with BatchEmbeddingVectorConsumer -> {0}", chunk.text());
        buffer.add(chunk);
        if (buffer.size() >= batchSize) {
            flushBatch();
        }
    }

    @Override
    public void awaitCompletion() throws Exception {
        flush(); // safety

        for (CompletableFuture<?> f : inFlight) {
            f.join();    // 🔥 THIS is what you were missing
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.MINUTES);
    }

    private synchronized void flushBatch() {
        LOGGER.log(Level.INFO, "Flush batch buffer BatchEmbeddingVectorConsumer -> {0}", buffer.isEmpty());
        if (buffer.isEmpty()) return;

        List<TextChunk> toProcess = new ArrayList<>(buffer);
        buffer.clear();

        CompletableFuture<Void> future =
                CompletableFuture
                        .supplyAsync(() -> embeddingClient.embedBatch(
                                toProcess.stream().map(TextChunk::text).toList()
                        ), executor)
                        .thenAccept(embeddings -> {
                            for (int i = 0; i < toProcess.size(); i++) {
                                float[] vector = embeddings.get(i);
                                if (vector.length == 0) continue;

                                try {
                                    LOGGER.log(Level.INFO, "writing vectors -> {0}", toProcess.get(i));
                                    vectorWriter.write(toProcess.get(i), vector);
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        })
                        .exceptionally(ex -> {
                            LOGGER.severe("Embedding batch failed: " + ex.getMessage());
                            throw new CompletionException(ex);
                        });


        inFlight.add(future);
    }


    @Override
    public void flush() {
        LOGGER.log(Level.INFO, "Flush buffer BatchEmbeddingVectorConsumer -> {0}", buffer.isEmpty());
        if (!buffer.isEmpty()) flushBatch();
    }
}
