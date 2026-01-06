package io.wahid.publication.ai.processing;

import io.wahid.publication.ai.ingestion.TextChunkConsumer;

public interface MetadataExtractor extends TextChunkConsumer {

    void setDownstream(TextChunkConsumer downstream);
}
