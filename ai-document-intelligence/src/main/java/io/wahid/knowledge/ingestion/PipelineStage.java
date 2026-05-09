package io.wahid.knowledge.ingestion;

import io.wahid.knowledge.dto.TextChunk;

public interface PipelineStage extends TextChunkConsumer {
    void accept(TextChunk chunk) throws Exception;
    void flush() throws Exception;
    default void awaitCompletion() throws Exception {
        // no-op by default
    }
}
