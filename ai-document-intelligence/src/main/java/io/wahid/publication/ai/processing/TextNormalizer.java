package io.wahid.publication.ai.processing;

import io.wahid.publication.ai.ingestion.TextChunkConsumer;

public interface TextNormalizer extends TextChunkConsumer {

    void setDownstream(TextChunkConsumer downstream);
}
