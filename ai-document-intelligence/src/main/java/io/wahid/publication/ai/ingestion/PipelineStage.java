package io.wahid.publication.ai.ingestion;

public interface PipelineStage extends TextChunkConsumer {
    void accept(TextChunk chunk) throws Exception;
    void flush() throws Exception;
    default void awaitCompletion() throws Exception {
        // no-op by default
    }
}
