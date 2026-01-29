package io.wahid.publication.ai.embedding;

import io.wahid.publication.ai.ingestion.AbstractPipelineStage;
import io.wahid.publication.ai.ingestion.TextChunk;
import io.wahid.publication.ai.ingestion.TextChunkConsumer;
import io.wahid.publication.ai.processing.TokenEstimator;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * High-throughput, monotonic chunker for EMBEDDINGS.
 * - No overlap
 * - No duplicates
 * - Batches aggressively
 */
public final class EmbeddingChunker
        extends AbstractPipelineStage
        implements TextChunkConsumer {

    private static final Logger LOGGER = Logger.getLogger(EmbeddingChunker.class.getName());
    private final int maxTokens;
    private final int maxChars;

    private final StringBuilder buffer = new StringBuilder(8192);
    private int bufferedTokens = 0;

    private String documentId;
    private Map<String, Object> baseMetadata;

    public EmbeddingChunker(int maxTokens, int maxChars) {
        this.maxTokens = maxTokens;
        this.maxChars = maxChars;
    }

    @Override
    public void accept(TextChunk input) throws Exception {
        if (documentId == null) {
            documentId = input.documentId();
            baseMetadata = input.metadata();
        }

        // CSV rows or text blocks arrive here
        append(input.text());
    }

    private void append(String text) throws Exception {
        LOGGER.log(Level.INFO, "Embedding via EmbeddingChunker->{0}", text);
        int tokens = TokenEstimator.estimateTokens(text);
        int chars = text.length();

        // Hard safety split (rare but required)
        if (chars > maxChars) {
            for (int i = 0; i < text.length(); i += maxChars) {
                append(text.substring(i, Math.min(i + maxChars, text.length())));
            }
            return;
        }

        // Emit BEFORE overflow
        if (bufferedTokens + tokens > maxTokens
                || buffer.length() + chars > maxChars) {
            emit();
        }

        buffer.append(text).append('\n');
        bufferedTokens += tokens;
    }

    private void emit() throws Exception {
        if (buffer.isEmpty()) return;

        LOGGER.log(Level.INFO, "emitting buffer from embeddingchunker with -> {0}", downstream.getClass().getName());
        downstream.accept(new TextChunk(
                documentId,
                buffer.toString().trim(),
                enrichMetadata()
        ));

        buffer.setLength(0);
        bufferedTokens = 0;
    }

    private Map<String, Object> enrichMetadata() {
        Map<String, Object> meta = new HashMap<>(baseMetadata);
        meta.put("chunked", true);
        meta.put("timestamp", System.currentTimeMillis());
        return meta;
    }

    @Override
    public void flush() throws Exception {
        LOGGER.log(Level.INFO, "flushing buffer from embeddingchunker!");
        emit();
        buffer.setLength(0);
        bufferedTokens = 0;
        documentId = null;
        baseMetadata = null;
    }
}
