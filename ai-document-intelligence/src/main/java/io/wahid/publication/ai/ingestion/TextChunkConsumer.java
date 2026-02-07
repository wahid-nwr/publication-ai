package io.wahid.publication.ai.ingestion;

import io.wahid.publication.ai.dto.TextChunk;

public interface TextChunkConsumer {

    void accept(TextChunk chunk) throws Exception;

}
