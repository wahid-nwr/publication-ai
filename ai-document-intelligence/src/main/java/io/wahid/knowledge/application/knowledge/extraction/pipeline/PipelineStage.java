package io.wahid.knowledge.application.knowledge.extraction.pipeline;

import io.wahid.knowledge.application.ingestion.chunking.TextChunkConsumer;
import io.wahid.knowledge.application.ingestion.chunking.dto.TextChunk;

public interface PipelineStage extends TextChunkConsumer {
    void accept(TextChunk chunk) throws Exception;
    void flush() throws Exception;
    default void awaitCompletion() throws Exception {
        // no-op by default
    }
}
