package io.wahid.publication.ai.processing;

import io.wahid.publication.ai.ingestion.TextChunkConsumer;

public interface Chunker extends TextChunkConsumer {

    void setDownstream(TextChunkConsumer downstream);

    void flush() throws Exception;
}
