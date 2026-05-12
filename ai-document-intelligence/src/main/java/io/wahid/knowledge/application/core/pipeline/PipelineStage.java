package io.wahid.knowledge.application.core.pipeline;

import io.wahid.knowledge.application.core.ingestion.chunking.TextChunkConsumer;
import io.wahid.knowledge.application.core.ingestion.chunking.dto.TextChunk;

public interface PipelineStage extends TextChunkConsumer {
    void accept(TextChunk chunk) throws Exception;
    void flush() throws Exception;
    default void awaitCompletion() throws Exception {
        // no-op by default
    }
}
