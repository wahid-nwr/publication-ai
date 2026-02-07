package io.wahid.publication.ai.ingestion;

import io.wahid.publication.ai.dto.TextChunk;

public interface PipelineStage extends TextChunkConsumer {
    void accept(TextChunk chunk) throws Exception;
    void flush() throws Exception;
    default void awaitCompletion() throws Exception {
        // no-op by default
    }
}
