package io.wahid.publication.ai.ingestion;

public interface PipelineStage extends TextChunkConsumer {
    void flush() throws Exception;
}
